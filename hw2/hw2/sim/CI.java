// $Id: CI.java,v 1.1 2010/01/26 07:31:59 zahorjan Exp $

package sim;

import java.lang.Math;

/** Simple confidence interval implementation.
 */

public class CI {
    int     numSamples = 0;
    double  meanTotal = 0.0;
    double  momentTotal = 0.0;

    private static String ciLevelStr = "95%";
    private static double ciLevelWidth = 1.96;

    public CI() {
    }

    public void addSample(double sample) {
        numSamples++;
        meanTotal += sample;
        momentTotal += sample*sample;
    }

    /** Returns current sample mean. */
    public double getMean() {
        if ( numSamples <= 0 ) {
            return 0.0;
        }
        return meanTotal / numSamples;
    }

    /** Returns current confidence interval width, at confidence
        level corresponding to hardcoded ciLevelWidth value. */
    public double getCI() {
        if ( numSamples <= 1 ) {
            return 0.0;
        }
        return ciLevelWidth * Math.sqrt( (momentTotal - meanTotal*meanTotal/numSamples) / (numSamples - 1) / numSamples );
    }

    /** Returns the confidence level being used as a string. */
    public String getCIString() {
        return ciLevelStr;
    }
  
}
