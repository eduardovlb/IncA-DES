/*    
*    IncADESClassifier.java 
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

import java.util.HashSet;
import java.util.Set;

import incades.concept.Concept;
import incades.pruningengine.PruningMetrics;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;

public class IncADESClassifier<T extends PruningMetrics> extends AbstractClassifier{
	
	private static final long serialVersionUID = 1L;

	private Classifier classifier;

	private T dynseClassifierMetrics;
	private Set<Concept<?>> trainingConcepts;

	public IncADESClassifier(Classifier classifier) {
		this(classifier, new HashSet<Concept<?>>());
	}
	
	public IncADESClassifier(Classifier classifier, Set<Concept<?>> trainingConcepts) {
		super();
		this.classifier = classifier;
		this.trainingConcepts = trainingConcepts;
	}

	public Set<Concept<?>> getTrainingConcepts() {
		return trainingConcepts;
	}

	public void setIncADESClassifierMetrics(T dynseClassifierMetrics) {
		this.dynseClassifierMetrics = dynseClassifierMetrics;
	}

	public boolean isRandomizable() {
		return false;
	}

	@Override
	public void getModelDescription(StringBuilder out, int arg1) {
		out.append("Dynse Single Classifier using a " + classifier.getClass());
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		return classifier.getModelMeasurements();
	}

	@Override
	public double[] getVotesForInstance(com.yahoo.labs.samoa.instances.Instance arg0) {
		return classifier.getVotesForInstance(arg0);
	}

	@Override
	public void resetLearningImpl() {
		classifier.resetLearning();
	}

	@Override
	public void trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instance arg0) {
		classifier.trainOnInstance(arg0);
	}

	public T getIncADESClassifierMetrics() {
		return dynseClassifierMetrics;
	}
}