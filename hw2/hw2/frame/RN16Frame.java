// $Id: RN16Frame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;

/** Tag to reader RN16 frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>RN16<td>16<td>Tag-generated 16-bit random number</tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class RN16Frame extends TtoRFrame {

    /** Change this to true if you want CRC's on this type of frame */
    private static final boolean rn16FrameHasCRC = false;

    private static final int  RN16_RN_FIELD = 0;
    private static final int  RN16_RN_WIDTH = 16;

    private static final int rn16FrameSize = 16;   // size without CRC

    public RN16Frame(int val) {
        super(rn16FrameSize, FrameType.RN16FRAME);
        hasCRC = rn16FrameHasCRC;
        
        data.write(val, RN16_RN_FIELD, RN16_RN_WIDTH);
    }

    /** Extracts the RN field bits from f. */
    public static int getRN(RFIDFrame f) {
        return f.data.read(RN16_RN_FIELD, RN16_RN_WIDTH);
    }
}
