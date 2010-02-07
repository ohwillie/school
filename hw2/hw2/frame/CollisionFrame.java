// $Id: CollisionFrame.java,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

package frame;

/** Artificial frame type delivered to reader when more than
    one tag replies.
    <p>
    Wire format: None.
    <p>
      This frame type is simply used to signal that collision has occurred.
      (In contrast, if a single tag replies, but its frame experiences bit 
      errors, that frame is delivered to the reader.)
*/

public class CollisionFrame extends TtoRFrame {

    public CollisionFrame(int size) {
        super(size, FrameType.COLLISION);
        hasCRC = true; // must be true for corruption signaling to work
        isCorrupted = true;
    }
}
