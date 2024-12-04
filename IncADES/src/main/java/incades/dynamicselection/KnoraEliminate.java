/*    
*    KnoraEliminate.java 
*    Copyright (C) 2024 Ecole de Techonologie Superieure, Montreal, Quebec, Canada
*    @Author Eduardo Victor Lima Barboza (eduardo.lima-barboza.1@ens.etsmtl.ca)
*    
*
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

package incades.dynamicselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import incades.classifier.MajorityVoting;
import moa.classifiers.Classifier;

public class KnoraEliminate {
    
    public double[] classify(Classifier[] classifiers, Instances roc, Instance target) throws Exception {

        ArrayList<Classifier> chosenEnsemble = new ArrayList<Classifier>();

        Map<Classifier, Integer> hitMap = new HashMap<Classifier, Integer>();
        

        for (int i = 0; i < classifiers.length; ++i) {

            Classifier classifier = classifiers[i];
            int numHits = 0;

            int neighborsPredictedCorrectly = 0;
            for (int j = 0; j < roc.size(); ++j) {
                Instance testInst = roc.get(j);
                if (classifier.correctlyClassifies(testInst)) {
                    neighborsPredictedCorrectly++;
                    numHits++;
                }
            }
            hitMap.put(classifier, numHits);
            if (neighborsPredictedCorrectly == roc.size()) {
                chosenEnsemble.add(classifier);
            }
            hitMap.put(classifier, numHits);
        }

        if (chosenEnsemble.size() == 0) {
            int numNeighbors = roc.size() - 1;
            while (numNeighbors > 0) {
                for (Classifier classifier : classifiers) {
                    int numHits = hitMap.get(classifier);
                    if (numHits == numNeighbors) {
                        chosenEnsemble.add(classifier);
                    }
                }
                if (chosenEnsemble.size() > 0) {
                    break;
                }
            numNeighbors--;
            }
        }

        if (chosenEnsemble.size() == 0) {
            for (int i = 0; i < classifiers.length; ++i) {
                chosenEnsemble.add(classifiers[i]);
            }
        }

        MajorityVoting<Classifier> combiner = new MajorityVoting<Classifier>();

        double[] votes = combiner.distributionForInstance(target, chosenEnsemble);

        return votes;
    }

}
