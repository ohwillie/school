// $Id: TtoRFrame.java,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Base class of all frames from the tags to the reader.
    <p>
    This class does pretty much nothing.  We have it so that Java
    can type check that the frames flowing in each direction
    in the simulator are of the correct sort.
*/

public abstract class TtoRFrame extends RFIDFrame {

    protected TtoRFrame(int size, FrameType type) {
        super(size, type);
    }

}

