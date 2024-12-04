/*    
*    DynseClassifierPruningMetrics.java 
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

import incades.classifier.IncADESClassifier;

public class PruningMetrics {
	
	public static final Double DEFAULT_INCREASE_FACTOR_STEP = 1.0;
	public static final Double DEFAULT_DECREASE_FACTOR_STEP = -1.0;
	
	private Long creationTime;
	private Double useageFactor;
	private IncADESClassifier<?> incadesClassifier;

	public PruningMetrics(IncADESClassifier<?> incadesClassifier) {
		this.creationTime = System.currentTimeMillis();
		this.useageFactor = 0.0;
		this.incadesClassifier = incadesClassifier;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public IncADESClassifier<?> getIncADESClassifier() {
		return incadesClassifier;
	}

	public Double getUseageFactor() {
		return useageFactor;
	}

	public void setUseageFactor(Double useageFactor) {
		this.useageFactor = useageFactor;
	}
	
	public void increaseUseageFactor(){
		this.useageFactor+=DEFAULT_INCREASE_FACTOR_STEP;
	}
	
	public void decreaseUseageFactor(){
		this.useageFactor+=DEFAULT_DECREASE_FACTOR_STEP;
	}

	@Override
	public String toString() {
		return "creationTime: " + creationTime + ", useageFactor: " + useageFactor;
	}
}