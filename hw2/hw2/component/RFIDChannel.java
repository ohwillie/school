// $Id: RFIDChannel.java,v 1.3 2010/01/27 02:25:00 zahorjan Exp $

package component;

import frame.RFIDFrame;
import frame.RtoTFrame;
import frame.TtoRFrame;
import frame.CollisionFrame;
import component.RFIDTag;
import sim.SimDoneException;

/** An RFIDChannel represents the wireless medium between the reader
    and the tags.  It does a number of things.
    <p>
    First, it delivers a frame from the reader to each of the
    tags.  As it does that, it uses the BER to inject random
    bit errors, corrupting each bit independently.  If the 
    frame type contains a CRC field, it is assumed to detect all
    bit errors that occur.  If the frame does not contain
    a CRC field, the corrupted packet is delivered.
    <p>
    Second, correct delivery of a frame causes the tag to update
    its state, and to generate a reply if called for by the current
    state of the tag and the received frame.
    <p>
    Third, it collects all reply frames.  If there is more than
    one, the reply is unreadable (corrupted).  If there is only
    one, the BER is applied, as in the reader to tag case.  If
    there are no replies, the reply frame is null.
    <p>
    Finally, the RFIDChannel is in control of the simulator's
    clock.  It increments the clock, based on the time required
    to send the frame from the reader, the time to send the reply
    frame if there is one, and the required inter-frame gaps.
    Additionally, when the simuator clock exceeds the time allowed
    to inventory the tags, an exception is thrown, causing control
    to exit the RFIDReader.inventory() method.
*/

public class RFIDChannel {

  private RFIDTag[] tag;
  private int       stopTime;
  private double    BER;
  private double    bandwidth;

  private double    clock;

  // these are used to speed up generating bit errors
  private double    geometricCDF[];
  private int       bitsToNextError;

  // these are used to enable debug tracing and statistics dumping
  private boolean   debug;
  public  int[]     frameCnt = new int[RFIDFrame.FrameType.NUMFRAMETYPES.ordinal()];

  // inter-message gaps and timeouts.  We first express them in bits,
  // and then convert them to time based on the channel bandwidth.
  private static final int PREAMBLE_GAP = 8;  // ok, it's not exactly a gap, but it acts like one
  private static final int PRERESPONSE_GAP = 10;
  private static final int POSTRESPONSE_GAP = 3;
  private static final int READERTIMEOUT_GAP = 2*PRERESPONSE_GAP;
  private static final int NORESPONSENEEDED_GAP = 4;

  private double PREAMBLE_TIME;
  private double PRERESPONSE_TIME;
  private double POSTRESPONSE_TIME;
  private double READERTIMEOUT_TIME;
  private double NORESPONSENEEDED_TIME;

  /** Constructor. */
  public RFIDChannel( RFIDTag[] tagArray, double errorRate, double bw, int timeLimit ) {
    tag = tagArray;
    BER = errorRate;
    bandwidth = bw / 1000;  // bandwidth here is bits per msec.
    stopTime = timeLimit;
    clock = 0;
    debug = false;

    PREAMBLE_TIME = PREAMBLE_GAP / bandwidth;
    PRERESPONSE_TIME = PRERESPONSE_GAP / bandwidth;
    POSTRESPONSE_TIME = POSTRESPONSE_GAP / bandwidth;;
    READERTIMEOUT_TIME = READERTIMEOUT_GAP / bandwidth;;
    NORESPONSENEEDED_TIME = NORESPONSENEEDED_GAP / bandwidth;;

    if ( BER > 0.0 ) {
      int cdfSize = (int)(1.0/BER);
      if ( cdfSize > 100 ) cdfSize = 100;
      geometricCDF = new double[cdfSize];
      double term = BER;
      geometricCDF[0] = term;
      for ( int i=1; i<cdfSize; i++ ) {
        term *= (1.0-BER);
        geometricCDF[i] = geometricCDF[i-1] + term;
      }
      bitsToNextError = generateGeometricSample();
    }
  }

  /** Control whether the channel
      should print debugging info (currently a trace of packets sent in
      each direction).
  */
  public void setDebug(boolean b) {
    debug = b;
  }

  /** Delivers frame f to each
      tag, injecting bit errors as it does so.  (The bit errors are
      generated independently for each tag.)  That causes each tag
      to take whatever transition is required by the frame it received,
      and to possibly send a reply frame.
      <p>
      Returns:
      <ul>
      <li>If no tags reply, a timeout event occurs and null is returned.
      <li>If exactly one tag replies, that frame is returned.  Bit errors
      are injected.
      <li>If more than one tag replies, a CollisionFrame is returned.
      </ul>
  */

  public TtoRFrame sendFrame( RtoTFrame f ) throws SimDoneException {
    TtoRFrame reply = null;

    // update clock.  It's already at the beginning of the reader's transmission
    clock += PREAMBLE_TIME + f.getSize() / bandwidth;

    // update frame count statistics, plus debug printing
    frameCnt[f.getType().ordinal()]++;
    if ( debug ) {
      System.out.println( f.toString("") );
    }

    // this threshold is used only for frames with CRC's, but java demands that it be initialized
    double corruptionThreshold;
    corruptionThreshold =  f.hasCRC()? Math.pow(1.0-BER, f.getSize()) : 0;
    RtoTFrame erroredCRCFrame = null;

    int replyCnt = 0;
    int maxReplySize = 0;
        
    for ( int t=0; t < tag.length; t++ ) {
      RtoTFrame frameDelivered = null;

      // if the frame has a CRC, all bit errors are assumed detected.
      // (So, no need to generate errors on a per-bit basis.)
      if ( f.hasCRC() ) {
        // a frame has at least one bit error if it doesn't have 0 bit errors
        if ( Math.random() > corruptionThreshold ) {
          if ( erroredCRCFrame == null ) {
            erroredCRCFrame = (RtoTFrame)f.dup();
            erroredCRCFrame.markCorrupted();
          }
          frameDelivered = erroredCRCFrame;
        } else {
          frameDelivered = f;
        }
      }
      else {
        frameDelivered = (RtoTFrame)f.dup();
        corruptBits(frameDelivered);
      }

      // hand frame over to the next tag
      TtoRFrame thisReply = tag[t].deliver( frameDelivered );
      if ( thisReply != null ) {
        replyCnt++;
        reply = thisReply;
        if ( reply.getSize() > maxReplySize )  maxReplySize = reply.getSize();
      }
    }

    if ( replyCnt > 1 ) {
      reply = new CollisionFrame(maxReplySize);
      frameCnt[RFIDFrame.FrameType.COLLISIONSZ.ordinal()] += replyCnt;
    }

    // update clock to beginning of next reader frame
    if ( reply != null ) {
      frameCnt[reply.getType().ordinal()]++;
      clock += PRERESPONSE_TIME + reply.getSize() / bandwidth + POSTRESPONSE_TIME;
      if ( !reply.isCorrupted() ) {
        if ( reply.hasCRC() ) {
          if ( Math.random() > Math.pow(1.0-BER,reply.getSize()) ) reply.markCorrupted();
        } else {
          corruptBits(reply);
        }
      }

      // debug and statistics code
      if ( debug ) System.out.println( reply.toString("\t") );
    }
    else {
      // No reply.  If the reader was waiting for a response, it timed out.
      // Of the implemented RtoT frames, only Select doesn't expect a response
      if ( f.getCommand() == RtoTFrame.SELECT_CMD ) {
        clock += NORESPONSENEEDED_TIME;
      } else {
        clock += READERTIMEOUT_TIME;
        frameCnt[RFIDFrame.FrameType.TIMEOUTS.ordinal()]++;   // increment TIMEOUT counter
      }
    }

    if ( clock >= stopTime ) {
      if ( frameCnt[RFIDFrame.FrameType.COLLISION.ordinal()] > 0 )
        frameCnt[RFIDFrame.FrameType.COLLISIONSZ.ordinal()] /= 
          frameCnt[RFIDFrame.FrameType.COLLISION.ordinal()];
      throw new SimDoneException( clock );
    }

    return reply;
  }

  /** Utility function to generate a geometric sample (with 
      parameter BER).
  */
  private int generateGeometricSample() {
    int distance = 0;
    while (true ) {
      double sample = Math.random();
      for (int n=0; n<geometricCDF.length; n++ ) {
        if ( sample < geometricCDF[n] ) {
          distance += n;
          return distance;
        }
      }
      distance += geometricCDF.length;
    }
  }


  /** Utility function that uses BER to flip frame bits.
      Should never be called for packets that use CRCs (as CRCs are
      not actually stored by the simulator, so their bits can't be
      flipped.)
  */
  private void corruptBits(RFIDFrame f) {
    if ( BER <= 0.0 ) return;
    int size = f.getSize();
    int offset = 0;
    while ( offset + bitsToNextError  < size ) {
      offset += bitsToNextError;
      f.flipBit(offset);
      offset++;
      bitsToNextError = generateGeometricSample();
    }
    bitsToNextError -= size - offset;
  }

  /** Utility method that
      returns a printable String summarizing what the channel has
      seen to this point.  In the default implementation, this is
      a table of counts of the various frame types that have been
      sent.
  */
  public String dumpStats(String prefix) {
    String result = "";
    RFIDFrame.FrameType allTypes[] = RFIDFrame.FrameType.values();
    for (RFIDFrame.FrameType t : allTypes ) {
      if ( t == RFIDFrame.FrameType.NUMFRAMETYPES ) break;
      result += prefix + t + "\t" + frameCnt[t.ordinal()] + "\n";
    }
    return result;
  }
}
