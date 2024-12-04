/*    
*    MajorityVoting.java 
*    Copyright (C) 2017 Universidade Federal do Paraná, Curitiba, Paraná, Brasil
*    @Author Paulo Ricardo Lisboa de Almeida (prlalmeida@inf.ufpr.br)
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*    
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*    
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package incades.classifier;

import java.util.Collection;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.Classifier;

public class MajorityVoting<T extends Classifier> {

	protected Random random = ThreadLocalRandom.current();

	public double[] distributionForInstance(Instance instance, Collection<T> classifiers)
			throws Exception {

		double[] probs;
		double[] votes = new double[instance.classAttribute().numValues()];

		for (Classifier classifier : classifiers) {
			probs = classifier.getVotesForInstance(instance);
			int maxIndex = 0;
			for (int j = 0; j < probs.length; j++) {
				if (probs[j] > probs[maxIndex])
					maxIndex = j;
			}

			// Consider the cases when multiple classes happen to have the same
			// probability
			for (int j = 0; j < probs.length; j++) {
				if (probs[j] == probs[maxIndex])
					votes[j]++;
			}
		}

		int tmpMajorityIndex = 0;
		for (int k = 1; k < votes.length; k++) {
			if (votes[k] > votes[tmpMajorityIndex])
				tmpMajorityIndex = k;
		}

		// Consider the cases when multiple classes receive the same amount of
		// votes
		Vector<Integer> majorityIndexes = new Vector<Integer>();
		for (int k = 0; k < votes.length; k++) {
			if (votes[k] == votes[tmpMajorityIndex])
				majorityIndexes.add(k);
		}
		// Resolve the ties according to a uniform random distribution
		int majorityIndex = majorityIndexes.get(random.nextInt(majorityIndexes.size()));

		// set probs to 0
		probs = new double[instance.classAttribute().numValues()];
		probs[majorityIndex] = 1; // the class that have been voted the most receives 1

		return probs;
	}

	public int maxIndex(double[] doubles) {

		double maximum = 0;
		int maxIndex = 0;

		for (int i = 0; i < doubles.length; i++) {
			if ((i == 0) || (doubles[i] > maximum)) {
				maxIndex = i;
				maximum = doubles[i];
			}
		}

		return maxIndex;
	}

	public double classifyInstance(Instance instance, Collection<T> classifiers) throws Exception {
		double result;
		double[] dist;
		int index;

		dist = distributionForInstance(instance, classifiers);
		if (instance.classAttribute().isNominal()) {
			index = this.maxIndex(dist);
			if (dist[index] == 0)
				throw new Exception("Impossible to calculate the Majority Voting.");
			else
				result = index;
		} else if (instance.classAttribute().isNumeric()) {
			result = dist[0];
		} else {
			throw new Exception("Impossible to calculate the Majority Voting.");
		}

		return result;
	}
}
