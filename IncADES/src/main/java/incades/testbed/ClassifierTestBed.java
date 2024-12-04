package incades.testbed;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.yahoo.labs.samoa.instances.Instance;

import incades.core.IncADES;
import moa.core.TimingUtils;
import moa.streams.ArffFileStream;

public class ClassifierTestBed {
    public void runTestIncADES(int numExecs, ArffFileStream stream, String datasetName) {

        try {
        ArrayList<Double> accuracies = new ArrayList<Double>();
        ArrayList<Double> timeList = new ArrayList<Double>();
        ArrayList<Integer> numBuilds = new ArrayList<Integer>();
        ArrayList<Integer> numChanges = new ArrayList<Integer>();

        ArrayList<ArrayList<Double>> predictions = new ArrayList<ArrayList<Double>>();
        
        String output = datasetName + "_IncADES";

        System.out.println(output);

        int execs = 0;

        while (execs < numExecs) {
            ArrayList<Double> preds = new ArrayList<Double>();

            IncADES classifier = new IncADES();
            classifier.setRandomSeed(execs+1);
            
            classifier.prepareForUse();
            
            
            stream.prepareForUse();
            classifier.setModelContext(stream.getHeader());
            
            System.out.printf((execs+1) + " ");
            
            // Treinar em 200
            
            for (int i = 0; i < 200; i++) {
                Instance trainInst = (Instance) stream.nextInstance().getData();
                
                classifier.trainOnInstance(trainInst);
                }
            
            int numberSamplesCorrect = 0;
            int numberSamples = 0;
            TimingUtils.enablePreciseTiming();
            long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            
            while (stream.hasMoreInstances()) {

                Instance trainInst = (Instance) stream.nextInstance().getData();	    
                

                if (classifier.correctlyClassifies(trainInst)){
                        numberSamplesCorrect++;
                        preds.add(1.0);
                } else {
                    preds.add(0.0);
                }
                classifier.trainOnInstance(trainInst);
                
                numberSamples++;
            }
            predictions.add(preds);
            numBuilds.add(classifier.getNumRebuilds());
            numChanges.add(classifier.getNumChangesDetected());
            
            
            double accuracy = 100.0 * (double) numberSamplesCorrect/ (double) numberSamples;
            double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()- evaluateStartTime);
            timeList.add(time);		        
            
            accuracies.add(accuracy);
            
            execs++;
            classifier.resetLearning();
        }
        
        
        double sum = 0;
        
        for (int i = 0; i < accuracies.size(); i++) {
            sum += accuracies.get(i);
        }
        
        double average = sum / accuracies.size();
        
        sum = 0;
        for (int i = 0; i < timeList.size(); i++) {
            sum += timeList.get(i);
        }

        double avTime = sum / timeList.size();

        sum = 0;
        for (int i = 0; i < numBuilds.size(); i++) {
            sum += numBuilds.get(i);
        }

        double avBuilds = sum/numBuilds.size();

        sum = 0;
        for (int i = 0; i < numChanges.size(); i++) {
            sum += numChanges.get(i);
        }

        double avChanges = sum/numBuilds.size();
        
        
        
        System.out.println("\nAverage Accuracy after " + execs + " runs: " + average);
        System.out.println("SD: " + getSD(accuracies));
        System.out.println("Num Builds K-d Tree: " + avBuilds);
        System.out.println("Num Changes Detected: " + avChanges);
        
        System.out.println("\nAverage Time after " + execs + " runs: " + avTime);
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(output+"_predictions.txt"))) {
            for (int i = 0; i < predictions.size(); i++) {
                for (double line : predictions.get(i)) {
                    bw.write(line + ",");
                }
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
			
		} catch (Exception ie) {
			System.out.println(ie.getMessage());
		}

    }

    static double getSD(ArrayList<Double> accuracies) {
		double sum = 0;
		for (double acc : accuracies)
			sum += acc;
		double mean = sum / accuracies.size();		
		
		sum = 0;
		for (double val : accuracies) {
			double squrDiffToMean = Math.pow(val - mean, 2);
			
			sum += squrDiffToMean;
		}
		
		double meanOfDiffs = (double) sum / (double) (accuracies.size());
		
		return Math.sqrt(meanOfDiffs);
	}
}
