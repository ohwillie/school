// $Id: AckFrame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Reader to tag ACK frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>command<td>4<td>0100 (decimal 4)</tr>
    <tr><td>RN<td>16<td>Echoed 16-bit random number</tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class AckFrame extends RtoTFrame {

    /* Change this to true if you want CRC's on these frames */
    private static final boolean  ackFrameHasCRC = false;

    private static final int  ACK_RN_FIELD = RtoT_CMD_FIELD + RtoT_CMD_WIDTH;
    private static final int  ACK_RN_WIDTH = 16;

    private static final int  ackFrameSize = ACK_RN_WIDTH + RtoT_CMD_WIDTH;   // size without CRC (but with CMD field)

    public AckFrame(int rn) {
        super(ackFrameSize, ACK_CMD, FrameType.ACKFRAME);
        hasCRC = ackFrameHasCRC;
        data.write(rn, ACK_RN_FIELD, ACK_RN_WIDTH);
    }

    /** Retrieve the RN field bits from f. */
    public static int getRN(RFIDFrame f) {
        return f.data.read(ACK_RN_FIELD, ACK_RN_WIDTH);
    }
}
