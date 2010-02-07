// $Id: SelectFrame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;
import component.BitMemory;

/** Reader to tags Select frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>command<td>4<td>1010 (decimal 10)</tr>
    <tr><td>Target<td>1<td><table>
                       <tr><td>0<td>Inventoried</tr>
                       <tr><td>1<td>SL</tr>
                       </table></tr>
    <tr><td>Action<td>3<td>See Table 6.19 in Class 1 Generation 2 UHF Air Interface
                           Protocol Standard Version 1.0.9: "Gen 2", linked from
                           <a href="http://www.epcglobalinc.org/standards/">this page</a>.</tr>
    <tr><td>Ptr<td>7<td>0000000 - 1111111 (unsigned int 0-128)
                        <br>Offset into EPC to begin matching against mask.</tr>
    <tr><td>Length<td>8<td>(unsigned) length of Mask field</tr>
    <tr><td>Mask<td>variable<td>Bit mask to match against substring of EPC</tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class SelectFrame extends RtoTFrame {

    private int maskLen;

    /* CSE461 - change this to true if you want CRC's on these frames */
    private static final boolean selectFrameHasCRC = true;

    private static final int  SELECT_TARGET_FIELD = RtoT_CMD_FIELD + RtoT_CMD_WIDTH;
    private static final int  SELECT_TARGET_WIDTH = 1;

    private static final int  SELECT_ACTION_FIELD = SELECT_TARGET_FIELD + SELECT_TARGET_WIDTH;
    private static final int  SELECT_ACTION_WIDTH = 3;

    private static final int  SELECT_PTR_FIELD = SELECT_ACTION_FIELD + SELECT_ACTION_WIDTH;
    private static final int  SELECT_PTR_WIDTH = 7;

    private static final int  SELECT_LENGTH_FIELD = SELECT_PTR_FIELD + SELECT_PTR_WIDTH;
    private static final int  SELECT_LENGTH_WIDTH = 8;

    private static final int  SELECT_MASK_FIELD = SELECT_LENGTH_FIELD + SELECT_LENGTH_WIDTH;
    // no fixed length -- have to look it up in the length field

    // size without MASK or CRC (but with CMD field)
    private static final int selectFrameSize = SELECT_MASK_FIELD + RtoT_CMD_WIDTH;

    public SelectFrame(int target, int action, int ptr, int len, BitMemory mask) {
        super(selectFrameSize + len, SELECT_CMD, FrameType.SELECTFRAME);
        hasCRC = selectFrameHasCRC;
        maskLen = len;
        
        data.write(target, SELECT_TARGET_FIELD, SELECT_TARGET_WIDTH);
        data.write(action, SELECT_ACTION_FIELD, SELECT_ACTION_WIDTH);
        data.write(ptr, SELECT_PTR_FIELD, SELECT_PTR_WIDTH);
        data.write(len, SELECT_LENGTH_FIELD, SELECT_LENGTH_WIDTH);
        data.write(mask, SELECT_MASK_FIELD);

    }

    /** Extract target field bits from f. */
    public static int getTarget(RFIDFrame f) {
        return f.data.read(SELECT_TARGET_FIELD, SELECT_TARGET_WIDTH);
    }

    /** Extract action field bits from f. */
    public static int getAction(RFIDFrame f) {
        return f.data.read(SELECT_ACTION_FIELD, SELECT_ACTION_WIDTH);
    }

    /** Extract ptr field bits from f. */
    public static int getPtr(RFIDFrame f) {
        return f.data.read(SELECT_PTR_FIELD, SELECT_PTR_WIDTH);
    }

    /** Extract len field bits from f. */
    public static int getLen(RFIDFrame f) {
        return f.data.read(SELECT_LENGTH_FIELD, SELECT_LENGTH_WIDTH);
    }

    /* Extract one bit of the mask field from f.
       The offset of the first (leftmost, highest order) mask bit is 0.
     */
    public static int getMaskBit(RFIDFrame f, int offset) {
        if ( offset<0 || offset >= getLen(f) ) return 0;
        return f.data.read(SELECT_MASK_FIELD+offset, 1);
    }
}
