// $Id: RFIDFrame.java,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

package frame;

import component.BitMemory;

/** Root class of all frame types.
 */
public abstract class RFIDFrame implements Cloneable {

    /** These definitions are used to support RFIDChannel's logging.
     */
    public enum FrameType {ACKFRAME, EPCFRAME, QUERYFRAME, QUERYADJFRAME,
                           QUERYREPFRAME, RN16FRAME, SELECTFRAME, COLLISION,
                           TIMEOUTS,      // okay, there are no TIMEOUTS frames;
                                          // see RFIDChannel.java for why this is here
                           COLLISIONSZ,   // avg. number of participants in a collision
                           NUMFRAMETYPES }
    private FrameType type;


    // From here on, everthing is legitimately for the implementation of frames
    // (with minor warts having to do with the type instance variable).

    protected boolean isCorrupted;
    protected boolean hasCRC;

    /** These are the bits of the frame.
        Note that we don't store them all -- we keep neither the preamble
        nor the CRC (even if one is being used).
    */
    protected BitMemory data;

    protected static final int CRC_SIZE = 16;  // unlike spec, all CRC's are assumed to be 16 bits

    /** The constructor can be called only by a derived class - don't
        try to new one of these, new one of the subclasses.
    */
    protected RFIDFrame(int bitsize, FrameType t) {
        type = t;
        data = new BitMemory(bitsize);
        isCorrupted = false;
    }

    /** Returns the size of the frame, in bits
        (not including preamble).  Size includes CRC, if
        the frame's class has been configured to carry one.
    */
    public int getSize() {
        // now bits are allocated for CRC, so frame size is...
        return data.getSize() + (hasCRC()?CRC_SIZE:0);
    }

    /** This is an oracle method that reliably returns
        the type of a frame, even a corrupted one.  It s 
        a utility to help the simulator with logging.
        <p>
        Your reader implementation cannot use this for making
        decisions.  (You could potentially use it for debugging,
        but it isn't something an actual reader could do.)
    */
    public FrameType getType() {
        return type;
    }

    /** Returns true if the frame represents a collision; false
        otherwise.  Works for both frames with and without CRCs.
        <p>
        It's okay to use this function in making decisions in
        you reader.
    */
    public boolean isCollision() {
        return getType() == FrameType.COLLISION;
    }

    /** Returns true only if (a) the frame's class has CRC
        enabled AND a bit error has occured in this frame, or
        (b) a collision has occurred.
    */
    public boolean isCorrupted() {
        return isCorrupted && hasCRC;
    }

    /** IF the frame's class carries a CRC, mark it as corrupted.
        Otherwise, do nothing.
    */
    public void markCorrupted() {
        isCorrupted = hasCRC;
    }

    /** Fliips a single bit of the frame.
        Bit flipping should be used only by frames without CRCs
     */
    public void flipBit(int b) {
        markCorrupted();   // self-defense against misuse
        data.write(
                   data.read(b,1)==1?0:1,
                   b,
                   1
                   );
    }

    /** Returns true if the frame's class has been configured
        with a CRC field, false otherwise.
    */
    public boolean hasCRC() {
        return hasCRC;
    }

    public String toString(String prefix) {
        return prefix + type + (isCollision()?"  [collision]":"") + (isCorrupted?"  [corrupted]":"");
    }

    /** Wrapper to clone frame, catching possible exceptions. 
     */
    public RFIDFrame dup() {
        RFIDFrame result = null;
        try {
            result = clone();
        } catch (Exception e) {
            System.out.println("Fatal error in RFIDFrame.dup() call to clone()");
            System.out.println(e);
            System.exit(1);
        }

        return result;
    }

    /** Overridden clone function, to create new data instance variable.
     */
    public RFIDFrame clone() throws java.lang.CloneNotSupportedException {
        RFIDFrame duplicate = (RFIDFrame) super.clone();

        duplicate.data = new BitMemory(this.data.getSize());
        duplicate.data.write(this.data, 0);

        return duplicate;
    }

}
