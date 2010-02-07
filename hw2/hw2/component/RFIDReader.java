// $Id: RFIDReader.java,v 1.4 2010/01/29 09:26:40 zahorjan Exp $

package component;

import component.RFIDTag;
import component.RFIDChannel;
import frame.*;

import java.util.*;

/** <font color=red><b>[CSE461]</b></font> Implementation of the RFID Reader, which controls
    the collision resolution procedure.
*/
public class RFIDReader {

    /** <font color=red><b>[CSE461]</b></font> As you find tag EPCs, add them to this HashSet.
     */
    private HashSet<BitMemory> currentInventory = new HashSet<BitMemory>();

    /** This RFIDChannel allows you to send frame to and receive frames from
        the tags.
    */
    private RFIDChannel channel;

    public RFIDReader(RFIDChannel chan) {
        channel = chan;
    }

    /** <font color=red><b>[CSE461]</b></font> Main loop of the simulation.
        This is where (most of) your code goes.  
        The goal is to inventory as many tags as you can before time expires.
        Basically, you sit in a forever loop sending frames to the tags, reading
        their response(s), and deciding what to do next.
        Control returns from this method when an exception is thrown (by the RFIDChannel
        object, not that it matters), indicating that the time limit has expired
        (the tags have moved out of the field of the reader).
        <p>
        The default implementation first selects all the tags.  It then sends a query
        frame, hoping that exactly one tag will select slot 0 and reply with an RN16 frame.
        If that doesn't happen, the reader sends another query.
        If it does happen, the reader tries to read the responding tag's EPC by engaging in
        the ACK/ECP frame exchange that causes the tag to provide its EPC (assuming
        no frames are corrupted).
        <p>
        
    */

    public void inventory() throws sim.SimDoneException {
        // The dummy implementation:
        //   - tries to select all the tags
        //   - simply repeats a query with a fixed window size until
        //     some one tag replies
        //  - inventories and deselects the tag that replied
        // 
        // This is as dumb as it gets.
        // The implementation is admirable, though -- it's a direct
        // encoding of a state machine.   Here are the possible states.
        // (The state diagram is given by the code...)

        final int START = 0;
        final int QUERYSELECTED = 1;
        final int RNWAIT = 2;
        final int EPCWAIT = 3;

        int state = START;

        // Uncomment the next line to see a rather verbose trace of
        // traffic in both directions
        //channel.setDebug(true);

        BitMemory mask = new BitMemory(0);

        RtoTFrame outFrame = null;
        TtoRFrame replyFrame = null;

        while(true) {

            switch ( state ) {
                case START:
                             outFrame =  new SelectFrame(1, 0, 0, 0, mask);
                             state = QUERYSELECTED;
                             break;

                case QUERYSELECTED:
                            outFrame = new QueryFrame(3, 0, 5);
                            state = RNWAIT;
                            break;

                case RNWAIT:
                            // we're hoping to get an RN16Frame back.  It might be detected as
                            // corrupted (if it carries a CRC).
                            if ( replyFrame == null ) {
                              state = QUERYSELECTED;
                              outFrame = null;
                            }
                            else if ( replyFrame.isCorrupted()) {
                              state = QUERYSELECTED;
                              outFrame = null;
                            }
                            else {
                              int rn = RN16Frame.getRN(replyFrame);
                              outFrame = new AckFrame(rn);
                              state = EPCWAIT;
                            }
                            break;

                case EPCWAIT:
                    // We're hoping for an EPCFrame back.
                    if ( replyFrame != null && !replyFrame.isCorrupted() ) {
                      currentInventory.add( EPCFrame.getEPC(replyFrame) );
                      state = QUERYSELECTED;
                    } else {
                      state = QUERYSELECTED;
                    }
                    outFrame = null;
                    break;
            }

            // channel.sendFrame() will cause the frame to be 
            // delivered to each tag.  The frame is randomly
            // corrupted according to the BER, independently
            // for each tag.
            // The returned value, a frame, is also corrupted,
            // both by the BER and by collisions.
            if ( outFrame != null ) replyFrame = channel.sendFrame( outFrame );
            else                    replyFrame = null;
        }
    }

    /** Retrieves the set of EPCs that the reader thinks 
        it has discovered so far.
    */
    public HashSet<BitMemory> getInventory() {
        return currentInventory;
    }
}
