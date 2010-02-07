/** Implementation of the various frame types passed between the reader
    and the tags.
    <p>
    The (abstract) base class, RFIDFrame, is subclassed into frames
    sent only from the reader to the tags (RtoTFrame) and frames
    sent only from the tags to the reader (TtoRFrame).
    <p>
    RtoTFrame classes:
    <ul>
    <li>ACKFrame
    <li>QueryAdjFrame
    <li>QueryFrame
    <li>QueryRepFrame
    <li>SelectFrame
    </ul>
    <p>
    TtoRFrame classes:
    <ul>
    <li>EPCFrame
    <li>RN16Frame
    </ul>
*/

package frame;
