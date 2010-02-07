// $Id: RtoTFrame.java,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Base class for all frames from the reader to the tags.
    All such tags have a command field as their first four 
    bits.  (Frames from the tags to the reader do not have
    a command field.)
*/
public abstract class RtoTFrame extends RFIDFrame {

    protected static final int RtoT_CMD_FIELD = 0;   // bit offset of command field in frame
    protected static final int RtoT_CMD_WIDTH = 4;   // unlike spec, cmd field is constant across all frame types

    public static final int QUERYREP_CMD    = 0;   // bit value 0000
    public static final int ACK_CMD         = 4;   // bit value 0100
    public static final int QUERY_CMD       = 8;   // bit value 1000
    public static final int QUERYADJ_CMD    = 9;   // bit value 1001
    public static final int SELECT_CMD      = 10;  // bit value 1010
    public static final int CORRUPTED_CMD   = 16;  // exists only for convience of simulator implementation (not a real frame type/command)

    protected RtoTFrame(int size, int cmd, FrameType type) {
        super(size, type);
        data.write(cmd, RtoT_CMD_FIELD, RtoT_CMD_WIDTH);
    }

    /** Extracts the command field bits. */
    public int getCommand() {
        return data.read(RtoT_CMD_FIELD, RtoT_CMD_WIDTH);
    }

}

