// $Id: QueryRepFrame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Reader to tag QueryRep frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>command<td>4<td>0000 (decimal 0)</tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class QueryRepFrame extends RtoTFrame {

    /* CSE461 - change this to true if you want CRC's on these frames */
    private static final boolean queryRepFrameHasCRC = false;

    // size without CRC (but with CMD field)
    private static final int queryRepFrameSize = RtoT_CMD_WIDTH;

    public QueryRepFrame() {
        super(queryRepFrameSize, QUERYREP_CMD, FrameType.QUERYREPFRAME);
        hasCRC = queryRepFrameHasCRC;
    }
}
