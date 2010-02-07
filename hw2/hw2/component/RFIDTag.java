// $Id: RFIDTag.java,v 1.3 2010/01/27 02:25:00 zahorjan Exp $

package component;

import frame.*;
import component.BitMemory;

/** Implementation of a CSE461 tag, which is based on the Class 1 Gen 2 UHF
    spec.  Our tags have only a tiny amount of memory --
    their EPC, their selected bit (SL), and their inventoried bit (INV).
    They also have only one session (so session numbers are not contained
    in any of the simulated frame formats).
    <p>
    Each tag is an FSA, corresponding reasonably closely to a subset of
    the one in Figure 6.19 of the Gen 2 Air Standards
    in http://www.epcglobalinc.org/standards/.  Only the Ready, Arbitrate,
    Reply, and Acknowledged states are implemented.
*/
public class RFIDTag {

    // tag length
    public static int EPCLen = 64;
    private BitMemory EPC;

    private boolean SL = false;
    private boolean INV = false;

    private int Q;       // last Q value set by a query
    private int Qslot;   // 15-bit unsigned current value of slot number 
    private static final int QslotMask = 0x00007fff;
    private int RN16;    // random 16-bit value

    // FSA state names
    public static final int READY = 0;
    public static final int ARBITRATE = 1;
    public static final int REPLY = 2;
    public static final int ACKNOWLEDGED = 3;

    // current FSA state
    private int state = READY;

    /** Constructor just picks a random EPC
     */
    public RFIDTag( BitMemory EPCValue ) {
      EPC = new BitMemory(EPCLen);
      EPC.write( EPCValue, 0 );
    }

    /** Utility function that allows the simulator to magically peek
        into a tag and reliably determine its EPC.  Used to evaluate
        accuracy of the reader's inventory procedure.  Actual readers
        are not physically capable of this operation, of course, so
        you cannot use it in making decisions about what the reader
        should do next.
    */
    public BitMemory peekEPC() {
        return EPC;
    }

    /** Called for each tag when the reader puts a frame on the air.
        The actions taken here are determined by the RFID spec, and
        cannot be changed.
     */
    public TtoRFrame deliver(RtoTFrame f) {
        TtoRFrame replyFrame = null;

        int cmd;

        if ( !f.isCorrupted() ) cmd = f.getCommand();
        else                    cmd = RtoTFrame.CORRUPTED_CMD;
        
        switch ( cmd ) {
        case RtoTFrame.QUERYREP_CMD:
            Qslot--;
            Qslot &= QslotMask;  // have to wrap in 15 bits
            switch( state ) {
                case READY:          break;
                case ARBITRATE:  
                case REPLY:          if ( Qslot == 0 ) state = REPLY;
                                     else state = ARBITRATE;
                                     break;
                case ACKNOWLEDGED:   INV = !INV;
                                     state = READY;
                                     break;
            }
            break;

        case RtoTFrame.ACK_CMD:
            if ( state == REPLY || state == ACKNOWLEDGED ) {
                if ( AckFrame.getRN(f) == RN16 ) {
                    state = ACKNOWLEDGED;
                } else {
                    state = ARBITRATE;
                }
            }
            break;

        case RtoTFrame.QUERY_CMD:
            // all states invoke a new round
            if ( state == ACKNOWLEDGED) INV = !INV;
            int sel = QueryFrame.getSel(f);
            int target = QueryFrame.getTarget(f);
            // check to see if tag fails matching criteria
            if ( (sel == 2 && SL) || (sel==3 && !SL) ||
                 (target==0 && INV) || (target==1 && !INV) ) {
                state = READY;
            } else {
                Q = QueryFrame.getQ(f);
                newSlotState();
            }
            break;

        case RtoTFrame.QUERYADJ_CMD:
            int updn = QueryAdjFrame.getUpDn(f);
            boolean badUpDn = false;
            switch ( updn ) {
                case 6:   Q++;
                          break;
                case 0:   break;
                case 3:   Q--;
                          break;
                default:
                          badUpDn = true;
                          break;
            }
            if ( Q<0 || Q>15 ) Q=0;
            if ( !badUpDn) {
                switch ( state ) {
                case READY:         break;
                case ARBITRATE:     
                case REPLY:         newSlotState();
                                    break;
                case ACKNOWLEDGED:  INV = !INV;
                                    state = READY;
                                    break;
                }
            }
            break;

        case RtoTFrame.SELECT_CMD:
            boolean selected = true;
            int len = SelectFrame.getLen(f);
            int ptr = SelectFrame.getPtr(f);
            for ( int i = 0; i<len; i++ ) {
                if (SelectFrame.getMaskBit(f,i) !=  EPC.read(ptr+i, 1) ) {
                    selected = false;
                    break;
                }
            }

            if ( SelectFrame.getTarget(f) == 0 ) {
                // want to operate on inventoried
                if  (selected) {
                    switch(SelectFrame.getAction(f)) {
                    case 0: 
                    case 1:
                        INV = false;
                        break;
                    case 2: 
                    case 6:
                    case 7:
                        break;
                    case 3:
                        INV = !INV;
                        break;
                    case 4:
                    case 5:
                        INV = true;
                        break;
                    }
                }
                else { 
                    // not selected
                    switch(SelectFrame.getAction(f)) {
                    case 0:
                    case 2:
                        INV = true;
                        break;
                    case 1:
                    case 3:
                    case 5:
                        break;
                    case 4:
                    case 6:
                        INV = false;
                        break;
                    case 7:
                        INV = !INV;
                        break;
                    }
                }
            }
            else {
                // want to operate on SL
                if (selected) {
                    switch(SelectFrame.getAction(f)) {
                    case 0:
                    case 1:
                        SL = true;
                        break;
                    case 2:
                    case 6:
                    case 7:
                        break;
                    case 3:
                        SL = !SL;
                        break;
                    case 4:
                    case 5:
                        SL = false;
                        break;
                    }
                }
                else {
                    // not selected
                    switch(SelectFrame.getAction(f)) {
                    case 0:
                    case 2:
                        SL = false;
                        break;
                    case 1:
                    case 3:
                    case 5:
                        break;
                    case 4:
                    case 6:
                        SL = true;
                        break;
                    case 7:
                        SL = !SL;
                        break;
                    }
                }
            }

            state = READY;
            break;

        default:
            // unrecognized command (both frames marked corrupted and those that have
            // had their command field corrupted into something unrecognizable)
            switch( state ) {
                case REPLY:
                case ACKNOWLEDGED:
                                   state = ARBITRATE;
                                   break;
            }
            break;
        }

        // Now see if there's a message to send
        switch ( state ) {
            case REPLY:         updateRN16();
                                replyFrame = new RN16Frame( RN16 );
                                break;

            case ACKNOWLEDGED:  replyFrame = new EPCFrame( EPC );
                                break;
        }

        return replyFrame;
    }

    /** Utility routine to do some of the work required when a new round
        is initiated.
    */
    private void newSlotState() { 
        Qslot = (int)(Math.random()*Math.pow(2,Q)) & QslotMask;
        if ( Qslot > 0 ) state = ARBITRATE;
        else             state = REPLY;
    }

    /** Pick a new RN16.
     */
    private void updateRN16() {
        RN16 = (int)(0x00010000 * Math.random());
    }
}

