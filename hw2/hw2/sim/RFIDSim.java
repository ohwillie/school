// $Id: RFIDSim.java,v 1.5 2010/01/27 18:15:46 zahorjan Exp $

package sim;

import component.*;
import frame.*;

import java.util.HashSet;
import java.util.Random;
import java.text.DecimalFormat;

/** An RFIDSim orchestrates the simulation.
 */

public class RFIDSim {

    RFIDTag[]   tag;
    RFIDReader  reader;
    RFIDChannel channel;

    private static int progressInterval = 200;

    public int tagsFound;
    public int falsePositives;
    public int falseNegatives;
    public int tagSetSize;
    
    /** Constructs the tags, the channel, and the reader objects.
     *  <p>
     *  The EPC value initialization here pretty much presumes 64-bit EPCs.
     */

    public RFIDSim(int nTags, String EPCDistribution, double BER, double bw, int simTime) {

      tag = new RFIDTag[nTags];

      // we have separate code to generate each kind of tag distribution, for simplicity.
      BitMemory EPCVal = new BitMemory(RFIDTag.EPCLen);

      if ( EPCDistribution.equals("uniform") ) {
        for ( int t=0; t<nTags; t++ ) {
          for (int i=0; i<=(RFIDTag.EPCLen-1)/32; i++ ) {
            int randomBits  = ((int)(Math.random() * Integer.MAX_VALUE)<<1) |
                              (Math.random()>0.5 ? 1 : 0 );
            EPCVal.write(randomBits, i*32, 32);
          }
          tag[t] = new RFIDTag( EPCVal );
        }
      }

      else if ( EPCDistribution.equals("consecutive") ) {
       int lowBits = 0;
        for (int i=(RFIDTag.EPCLen-1)/32; i>=0; i-- ) {
          lowBits  = ((int)(Math.random() * Integer.MAX_VALUE)<<1) |
                            (Math.random()>0.5 ? 1 : 0 );
          EPCVal.write(lowBits, i*32, 32);
        }
        for ( int t=0; t<nTags; t++ ) {
          tag[t] = new RFIDTag( EPCVal );
          lowBits++;
          EPCVal.write( lowBits, 0, 32 );
        }
      }

      else if ( EPCDistribution.equals("gaussian") ) {
        Random gaussGenerator = new Random();
        int center = 0;
        for (int i=(RFIDTag.EPCLen-1)/32; i>=0; i-- ) {
          center  = ((int)(Math.random() * Integer.MAX_VALUE)<<1) |
                            (Math.random()>0.5 ? 1 : 0 );
          EPCVal.write(center, i*32, 32);
        }
        for ( int t=0; t<nTags; t++ ) {
          int offset = (int)(gaussGenerator.nextGaussian() * Integer.MAX_VALUE/256);
          int lowBits = center + offset;
          EPCVal.write( lowBits, 0, 32 );
          tag[t] = new RFIDTag( EPCVal );
        }
      }

      else throw new IllegalArgumentException("Unrecognized tag EPC distribution: '" + EPCDistribution + "'");

      channel = new RFIDChannel(tag, BER, bw, simTime);
      reader = new RFIDReader(channel);
    }

    /** Causes reader to start its inventorying process.
        <p>
        Returns an array of counts of the number of frames of
        each type sent.  (The types are defined by the FrameType
        enum in RFIDFrame.java.)
     */
    public int[] inventory() {
        try {
            reader.inventory();
        } catch (SimDoneException e) {
            checkResults();
            //System.out.println("Simulation done: sim time = " + e);
            //System.out.println( channel.dumpStats("\t") );
        }
        return channel.frameCnt;
    }

    /** Computes number of tags found, number of false positives,
        and number of false negatives, stuffing the results in
        public instance variables.
    */
    private void checkResults() {
        HashSet<BitMemory> readerSet = reader.getInventory();
        tagSetSize = readerSet.size();

        tagsFound = 0;
        falsePositives = 0;
        falseNegatives = 0;

        // check reader results against actual results
        for (int t=0; t<tag.length; t++) {
            if ( readerSet.contains(tag[t].peekEPC()) ) {
                tagsFound++;
            }
        }
        falsePositives = readerSet.size() - tagsFound;
        falseNegatives = tag.length - tagsFound;
    }

    /** This main runs the show.
        <p>
        Command line args, in order:
        <ul>
        <li>number of iterations to run
        <li>number of tags per run
        <li>tag EPC distribution (one of "uniform", "consecutive", or "gaussian")
        <li>bit error rate (e.g., .0001)
        <li>bandwidth, in bps (e.g., 100000)
        <li>maximum inventorying time (in msec.)
        </ul>
        A sample invocation might look like this:
        <blockquote>
        java -cp classes sim/RFIDSim 3000 20 .01 100000 1000
        </blockquote>
    */

    public static void main(String args[]) {
        RFIDSim thisSim;

        CI      ciPrecision;
        CI      ciRecall;
        CI      ciFalsePositives;
        CI      ciFalseNegatives;
        CI      ciTagSetSize;
        CI[]    ciFrameCount;

        int numTrials = Integer.parseInt(args[0]);
        int numTags = Integer.parseInt(args[1]);
        String EPCDistribution = args[2];
        double BER = Double.parseDouble(args[3]);
        double bw = Double.parseDouble(args[4]);
        int simTime = Integer.parseInt(args[5]);

        ciPrecision = new CI();
        ciRecall = new CI();

        ciTagSetSize = new CI();
        ciFalsePositives = new CI();
        ciFalseNegatives = new CI();

        int NumFrameTypes = frame.RFIDFrame.FrameType.NUMFRAMETYPES.ordinal();
        ciFrameCount = new CI[NumFrameTypes];
        for (int type=0; type<NumFrameTypes; type++) {
            ciFrameCount[type] = new CI();
        }
        int trialFrameCount[] = null;

        for ( int trial=1; trial<=numTrials; trial++) {
            thisSim = new RFIDSim(numTags, EPCDistribution, BER, bw, simTime);
            try {
                trialFrameCount = thisSim.inventory();
            } catch (Exception e) {
                System.out.println("Caught unexpected exception during inventory() call:");
                System.out.println( e );
                e.printStackTrace( System.out );
                System.exit(1);
            }

            ciTagSetSize.addSample( thisSim.tagSetSize );
            ciFalsePositives.addSample( thisSim.falsePositives / (double)numTags );
            ciFalseNegatives.addSample( thisSim.falseNegatives / (double)numTags  );

            double denom = (double)(thisSim.tagsFound + thisSim.falsePositives);
            if ( denom == 0 ) {
              ciPrecision.addSample( 1.0 );
            } else {
              ciPrecision.addSample( thisSim.tagsFound / denom );
            }
            ciRecall.addSample( thisSim.tagsFound / (double)numTags );

            for (int type=0; type<NumFrameTypes; type++) {
                ciFrameCount[type].addSample( trialFrameCount[type] );
            }

            if ( trial % progressInterval == 0 ) {
                System.out.println( new Integer(trial) + " trials done" );
            }
        }

        DecimalFormat dFormat = new DecimalFormat("#,##0.0");
        DecimalFormat iFormat = new DecimalFormat("#,##0");
        DecimalFormat ciFormat = new DecimalFormat("#0.000");

        System.out.println("\n| " + iFormat.format(numTrials) + " trials | " + 
                           iFormat.format(numTags) + " tags | " +
                           EPCDistribution + " distribution | " +
                           "BER = " + BER + " | " +
                           "BW = " + iFormat.format(bw) + " bps | " +
                           "Time limit = " + iFormat.format(simTime) + " msec. |" );

        System.out.println("\n\tPrecision = \t\t" + dFormat.format(ciPrecision.getMean()*100.0) + "% +/- " 
                           + ciFormat.format(ciPrecision.getCI()*100.0) + "% at " + ciPrecision.getCIString());
        System.out.println("\tRecall = \t\t" + dFormat.format(ciRecall.getMean()*100.0) + "% +/- " 
                           + ciFormat.format(ciRecall.getCI()*100.0) + "% at " + ciRecall.getCIString());

        System.out.println("\n\tFalse negatives =\t" + dFormat.format(ciFalseNegatives.getMean()*100.0) + "% +/-"
                           + ciFormat.format(ciFalseNegatives.getCI()*100.0) + "% at " + ciFalseNegatives.getCIString());
        System.out.println("\tFalse positives =\t" + ciFalsePositives.getMean()*100.0 + "% +/-"
                           + ciFormat.format(ciFalsePositives.getCI()*100.0) + "% at " + ciFalsePositives.getCIString());

        System.out.println("\n\tAverage tag set size =\t" + dFormat.format(ciTagSetSize.getMean()) + " +/-"
                           + ciFormat.format(ciTagSetSize.getCI()) + " at " + ciTagSetSize.getCIString());

        System.out.println("\nAverage frame counts:");
        frame.RFIDFrame.FrameType allFrameTypes[] = frame.RFIDFrame.FrameType.values();
        for (frame.RFIDFrame.FrameType t : allFrameTypes ) {
            if ( t.ordinal() >= ciFrameCount.length ) break;
            System.out.println("\t" + t + "\t" + dFormat.format(ciFrameCount[t.ordinal()].getMean()) );
        }
    }
}
