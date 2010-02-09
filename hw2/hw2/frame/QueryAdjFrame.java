// $Id: QueryAdjFrame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Reader to tag QueryAdjust frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>command<td>4<td>1001 (decimal 9)</tr>
    <tr><td>UpDn<td>3<td><table cellspacing=2>
                         <tr><td>110<td>Q++</tr>
                         <tr><td>000<td>no change to Q</tr>
                         <tr><td>011<td>Q--</tr>
                         <tr><td>other<td>Frame is ignored</tr>
                         </table></tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class QueryAdjFrame extends RtoTFrame {

    /* Change this to true if you want CRC's on these frames */
    private static final boolean queryAdjFrameHasCRC = true;

    private static final int  QUERYADJ_UPDN_FIELD = RtoT_CMD_FIELD + RtoT_CMD_WIDTH;
    private static final int  QUERYADJ_UPDN_WIDTH = 3;

    // size without CRC (but with CMD field)
    private static final int queryAdjFrameSize = QUERYADJ_UPDN_FIELD + QUERYADJ_UPDN_WIDTH +
                                               RtoT_CMD_WIDTH;

    public QueryAdjFrame(int upDn) {
        super(queryAdjFrameSize, QUERYADJ_CMD, FrameType.QUERYADJFRAME);
        hasCRC = queryAdjFrameHasCRC;
        
        data.write(upDn, QUERYADJ_UPDN_FIELD, QUERYADJ_UPDN_WIDTH);
    }

    /** Retrieves the UpDn field bits from f. */
    public static int getUpDn(RFIDFrame f) {
        return f.data.read(QUERYADJ_UPDN_FIELD, QUERYADJ_UPDN_WIDTH);
    }
}
