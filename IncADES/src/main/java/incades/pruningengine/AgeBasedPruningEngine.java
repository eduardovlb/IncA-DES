/*    
*    AgeBasedPruningEngine.java 
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
package incades.pruningengine;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.yahoo.labs.samoa.instances.Instance;

import incades.classifier.IncADESClassifier;

public class AgeBasedPruningEngine extends AbstractDefaultPruningEngine{
	
	private int maxPoolSize;

	public AgeBasedPruningEngine(int maxPoolSize) throws Exception{
		this.maxPoolSize = maxPoolSize;
		if(maxPoolSize < 1)
			throw new Exception("The max pool size must be greater than 0.");
	}

	@Override
	public List<IncADESClassifier<PruningMetrics>> pruneClassifiers(IncADESClassifier<PruningMetrics> newClassifier,
			List<IncADESClassifier<PruningMetrics>> currentPool, List<Instance> accuracyEstimationInstances) {
		if(currentPool.size() + 1 <= maxPoolSize)//sum 1, since a new classifier (newClassifier) will be added in the pool
			return new ArrayList<IncADESClassifier<PruningMetrics>>();
		int numClassifiers = currentPool.size() + 1 - maxPoolSize;
		List<IncADESClassifier<PruningMetrics>> classifiesToPrune = 
				new ArrayList<IncADESClassifier<PruningMetrics>>(numClassifiers);
		SortedSet<Long> agesSet = new TreeSet<Long>();
		for(IncADESClassifier<PruningMetrics> ic : currentPool)
			agesSet.add(ic.getIncADESClassifierMetrics().getCreationTime());
		
		Long prunningAge = -1L;
		int agesChecked = 0;
		for(Long age : agesSet){
			prunningAge = age;
			agesChecked++;
			if(agesChecked == numClassifiers)
				break;
		}
		for(IncADESClassifier<PruningMetrics> dc : currentPool){
			if(dc.getIncADESClassifierMetrics().getCreationTime() <= prunningAge){
				classifiesToPrune.add(dc);
				if(classifiesToPrune.size() == numClassifiers)
					break;
			}
		}
		return classifiesToPrune;
	}
	
	@Override
	public void getPrunningEngineDescription(StringBuilder out) {
		out.append("Age Based Prunning Engine\n");
		out.append("Max Pool Size: ");
		out.append(maxPoolSize);
		out.append("\n");
	}
	
	@Override
	public void getPrunningEngineShortDescription(StringBuilder out) {
		out.append("AgePrun" + maxPoolSize);
	}
}