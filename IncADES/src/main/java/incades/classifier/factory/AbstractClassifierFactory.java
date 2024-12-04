/*    
*    AbstractClassifierFactory.java 
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
package incades.classifier.factory;

import java.io.Serializable;
import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import moa.classifiers.Classifier;
import moa.core.InstanceExample;

public abstract class AbstractClassifierFactory implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public abstract Classifier createClassifier() throws Exception;
	
	public Classifier buildClassifier(Instance instance) throws Exception {
		Classifier classifier = this.createClassifier();
		classifier.trainOnInstance(instance);
		
		return classifier;
	}
	
	public Classifier buildClassifier(Instances trainBatch) throws Exception {
		Classifier classifier = this.createClassifier();
		for(int i = 0; i < trainBatch.size(); i++){
			classifier.trainOnInstance(trainBatch.get(i));
		}
		
		return classifier;
	}
	
	public Classifier buildClassifier(List<Instance> trainBatch) throws Exception {
		Classifier classifier = this.createClassifier();
		for(int i = 0; i < trainBatch.size(); i++){
			classifier.trainOnInstance(trainBatch.get(i));
		}
		
		return classifier;
	}
	
	public Classifier buildClassifierInstanceExamples(List<InstanceExample> trainBatch) throws Exception {
		Classifier classifier = this.createClassifier();
		for(int i = 0; i < trainBatch.size(); i++){
			classifier.trainOnInstance(trainBatch.get(i));
		}
		
		return classifier;
	}
	
	public abstract void getDescription(StringBuilder out);
	
	public abstract void getShortDescription(StringBuilder out);
}