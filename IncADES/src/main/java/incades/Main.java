package incades;

import java.util.ArrayList;
import incades.testbed.ClassifierTestBed;
import moa.streams.ArffFileStream;

public class Main {
	
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

	public static void main(String[] args) {
		
		try {
			ClassifierTestBed runner = new ClassifierTestBed();

			ArffFileStream stream = new ArffFileStream("datasets/adult_nonull.arff", -1);
			
			String name = "Adult";

			runner.runTestIncADES(10, stream, name);
						
		} catch (Exception ie) {
			System.out.println(ie.getMessage());
		}

	}

}
