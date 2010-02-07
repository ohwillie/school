// $Id: QueryFrame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Reader to tag Query frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>command<td>4<td>1000 (decimal 8)</tr>
    <tr><td>Sel<td>2<td><table cellspacing=2>
                        <tr><td>00<td>All</tr>
                        <tr><td>01<td>All</tr>
                        <tr><td>10<td>~SL</tr>
                        <tr><td>11<td>SL</tr>
                        </table></tr>
    <tr><td>Target<td>1<td><table cellspacing=2>
                           <tr><td>0<td>~Inventoried</tr>
                           <tr><td>1<td>Inventoried</tr>
                           </table></tr>
    <tr><td>Q<td>4<td>0000 - 1111 (unsigned int 0-15)</tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class QueryFrame extends RtoTFrame {

    /* CSE461 - Change this to true if you want CRC's for Query frames.
    */
    private static final boolean queryFrameHasCRC = false;

    private static final int  QUERY_SEL_FIELD = RtoT_CMD_FIELD + RtoT_CMD_WIDTH;
    private static final int  QUERY_SEL_WIDTH = 2;

    private static final int  QUERY_TARGET_FIELD = QUERY_SEL_FIELD + QUERY_SEL_WIDTH;
    private static final int  QUERY_TARGET_WIDTH = 1;

    private static final int  QUERY_Q_FIELD = QUERY_TARGET_FIELD + QUERY_TARGET_WIDTH;
    private static final int  QUERY_Q_WIDTH = 4;

    // size without CRC (but with CMD field)
    private static final int queryFrameSize = QUERY_Q_FIELD + QUERY_Q_WIDTH +
                                            RtoT_CMD_WIDTH;

    public QueryFrame(int sel, int target, int q) {
        super(queryFrameSize, QUERY_CMD, FrameType.QUERYFRAME);
        hasCRC = queryFrameHasCRC;
        
        data.write(sel, QUERY_SEL_FIELD, QUERY_SEL_WIDTH);
        data.write(target, QUERY_TARGET_FIELD, QUERY_TARGET_WIDTH);
        data.write(q, QUERY_Q_FIELD, QUERY_Q_WIDTH);
    }

    /** Extracts Sel field bits from f. */
    public static int getSel(RFIDFrame f) {
        return f.data.read(QUERY_SEL_FIELD, QUERY_SEL_WIDTH);
    }

    /** Extracts Target field bits from f. */
    public static int getTarget(RFIDFrame f) {
        return f.data.read(QUERY_TARGET_FIELD, QUERY_TARGET_WIDTH);
    }

    /** Extracts Q field bits from f. */
    public static int getQ(RFIDFrame f) {
        return f.data.read(QUERY_Q_FIELD, QUERY_Q_WIDTH);
    }
}
