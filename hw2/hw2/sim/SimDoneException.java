// $Id: SimDoneException.java,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

package sim;

/** Inventory time window expired exception.
 */
public class SimDoneException extends Exception {

    static final long serialVersionUID = -2451599027396927470L;

    private double simTime;

    /** The parameter t is the simulator clock when the exception
        is thrown.
    */
    public SimDoneException(double t) {
        simTime = t;
    }

    public String toString() {
        return String.valueOf(simTime);
    }
}
