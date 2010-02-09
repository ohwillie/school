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
    // Uncomment the next line to see a rather verbose trace of
    // traffic in both directions
    // channel.setDebug(true);

    RtoTFrame outFrame = null;
    TtoRFrame replyFrame = null;
    
    // NOTE modifying C to a different value could be one of our possible
    // iterative algorithm tweaks; keep this in mind.
    final double C = 0.3;
    double Qfp = 4.0;
    
    while (true) {
      int Q = (int) Math.round(Qfp);
      
      outFrame = new QueryFrame(0, 0, Q);
      replyFrame = channel.sendFrame(outFrame);
      
      if (replyFrame == null) {
        Qfp = Math.max(0, Qfp - C);
      } else if (replyFrame.isCollision()) {
        Qfp = Math.min(15, Qfp + C);
      } else {
        int retries = 0;
        while (replyFrame == null || (!replyFrame.isCollision() && replyFrame.isCorrupted())) {
          if (retries++ > 5 || (replyFrame == null && retries > 1)) {
            break;
          }
          outFrame = new QueryAdjFrame(1);
          replyFrame = channel.sendFrame(outFrame);
        }
        
        if (replyFrame != null && !replyFrame.isCorrupted()) {
          int rn16 = RN16Frame.getRN(replyFrame);
          outFrame = new AckFrame(rn16);
          replyFrame = channel.sendFrame(outFrame);
          if (replyFrame != null && !replyFrame.isCorrupted()) {
            BitMemory epc = EPCFrame.getEPC(replyFrame);
            currentInventory.add(epc);
            outFrame = new SelectFrame(0, 5, 0, RFIDTag.EPCLen, epc);
            channel.sendFrame(outFrame);
          }
        }
      }
    }
  }

  /** Retrieves the set of EPCs that the reader thinks 
        it has discovered so far.
   */
  public HashSet<BitMemory> getInventory() {
    return currentInventory;
  }
}
