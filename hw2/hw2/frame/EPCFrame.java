// $Id: EPCFrame.java,v 1.2 2010/01/26 08:21:42 zahorjan Exp $

package frame;

import frame.RFIDFrame;
import component.RFIDTag;
import component.BitMemory;

/** Tag to reader EPC frame.
    <blockquote>
    <table cellspacing=3 cellpadding=3 border=1>
    <tr><th>Field<th>Length<th>Value</tr>
    <tr><td>EPC<td>component.RFIDTag.EPCLen<td>The EPC reported by the tag</tr>
    <tr><td>CRC<td>16<br>(Optional)<td>Optional CRC covering entire frame.</tr>
    </table>
    </blockquote>
*/

public class EPCFrame extends TtoRFrame {

    /* Change to true to if you want CRC's on these frames */
    private static final boolean epcFrameHasCRC = true;

    private static final int  EPC_EPC_FIELD = 0;
    private static final int  EPC_EPC_WIDTH = RFIDTag.EPCLen;

    private static final int epcFrameSize = EPC_EPC_WIDTH;

    public EPCFrame(BitMemory EPC) {
        super(epcFrameSize, FrameType.EPCFRAME);
        hasCRC = epcFrameHasCRC;
        
        data.write(EPC, EPC_EPC_FIELD);
    }

    /** Extracts bit positions corresponding to the EPC field of
        an EPC frame from f.
    */
    public static BitMemory getEPC(RFIDFrame f) {
        return f.data;
    }
}
