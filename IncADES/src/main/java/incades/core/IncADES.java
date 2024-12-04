/*    
*    IncADES.java 
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

/*
*    Part of this code was leveraged from the Dynse framework (Almeida et al 2018)
*    and adapted to this framework. Credits are in the .java files. 
*/

package incades.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import incades.classifier.IncADESClassifier;
import incades.classifier.OverlapMeasurer;
import incades.classifier.factory.AbstractClassifierFactory;
import incades.classifier.factory.HoeffdingTreeFactory;
import incades.concept.Concept;
import incades.dynamicselection.KnoraEliminate;
import incades.neighborsearch.StreamNeighborSearch;
import incades.pruningengine.AgeBasedPruningEngine;
import incades.pruningengine.IPruningEngine;
import incades.pruningengine.PruningMetrics;
import incades.util.InstancesUtils;
import datastructures.KDTreeCanberra;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.core.driftdetection.ChangeDetector;
import moa.classifiers.core.driftdetection.RDDM;
import moa.core.Measurement;

public class IncADES extends AbstractClassifier {

	private int MAX_INSTANCES_ACCUMULATED = 1000000;
	private boolean changeWasDetected = false;
	private int numMinInstancesTrained = 200;
	private int instanceCount = 0;
	private int trainingCount = 0;
	private Random random = ThreadLocalRandom.current();
	private ArrayList<Integer> detectionPoints = new ArrayList<Integer>();

	protected LinkedHashMap<Concept<?>, Integer> classifiersByConcept = new LinkedHashMap<Concept<?>, Integer>();

	private int TRAINING_SIZE = 200;

	private LinkedList<Instance> DSEW = new LinkedList<Instance>();

    private KnoraEliminate knorae = new KnoraEliminate();

	private IPruningEngine<PruningMetrics> pruningEngine;

	private AbstractClassifierFactory classifierFactory;

	private int numNeighbors = 5;

	private OverlapMeasurer overlap = new OverlapMeasurer();
	
	private int changesDetected;

	private boolean updateNNSearch = true;
	
	private ChangeDetector changeDetector;

    private KDTreeCanberra neighborSearch;

    private boolean knnWasSetUp = false;

	private boolean warning = false;
	private int warningLevel = 0;
	private int numBuilds = 0;

	private List<IncADESClassifier<PruningMetrics>> pool = new LinkedList<IncADESClassifier<PruningMetrics>>();

    public IncADES() throws Exception {
        this.changeDetector = new RDDM();
		this.pruningEngine = new AgeBasedPruningEngine(75);
		this.classifierFactory = new HoeffdingTreeFactory();
	}

	public void trainOnInstanceImpl(Instance instance) {
		try {

            // this.updateNNSearch = true;

            if (this.instanceCount == 0) {
                this.createNNSearch();
            }

			if (this.instanceCount > this.numMinInstancesTrained)
				this.updateDetector(instance);

			Instance removedInstance = null;
			this.getAccuracyEstimationInstances().addLast(instance);
            
			if (knnWasSetUp) {
                this.neighborSearch.update(instance);
            }
			
			if (this.getAccuracyEstimationInstances().size() > this.MAX_INSTANCES_ACCUMULATED) {
				removedInstance = this.getAccuracyEstimationInstances().getFirst();
				this.getAccuracyEstimationInstances().removeFirst();
                this.neighborSearch.removeInstance(removedInstance);
			}

            if (neighborSearch.isToRebuild()) {
                this.knnWasSetUp = false;
                this.updateNNSearch();
            }

			if (this.changeDetector.getWarningZone() && this.warning == false) {
				this.warning = true;
				this.warningLevel = this.instanceCount;
			}
			
			if(this.getNumClassifiersPool() == 0 || this.isChangeDetected())
				this.addNewIncrementalClassifier(instance, getAccuracyEstimationInstances());
			else
				this.updateIncADES(instance);
			
			if (this.changeWasDetected && (this.warningLevel != this.instanceCount)) {
				this.resetDetector();
				this.shrinkAccuracyEstimationWindow();
                this.updateNNSearch();
				this.warning = false;
				this.warningLevel = 0;
				this.changesDetected++;
				this.changeWasDetected = false;
				this.detectionPoints.add(instanceCount);
				this.trainingCount = 0;
			}

			if (!this.changeDetector.getWarningZone() && this.warning == true) {
				this.warning = false;
				this.warningLevel = 0;
			}
			
			trainingCount++;
			instanceCount++;


		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void updateLastClassifier(Instance instance) throws Exception {
		IncADESClassifier<PruningMetrics> lastClassifier =  this.getClassifiers().get(this.getNumClassifiersPool()-1);
		lastClassifier.trainOnInstance(instance);
}
	
	protected boolean classifierTrainedMaxInstances() {
		if (this.instanceCount % this.TRAINING_SIZE == 0)
			return true;
		return false;
	}
	
	public int getNumChangesDetected() {
		return this.changesDetected;
	}
	
	private void updateDetector(Instance instance) {
		if (this.getNumClassifiersPool() > 0) {
			if (super.correctlyClassifies(instance)) {
				this.changeDetector.input(0);
			} else {
				this.changeDetector.input(1);
			}
		}
	}

    protected void createNNSearch() throws Exception {
            this.neighborSearch = new KDTreeCanberra();
    }

    protected StreamNeighborSearch getLinearNNSearch() {
        return (StreamNeighborSearch) this.neighborSearch;
    }
	
	private void updateNNSearch() {
		try {
			this.createNNSearch();
            this.neighborSearch.setInstances(InstancesUtils.gerarDataset(this.getAccuracyEstimationInstances(), "Validation Instances"));
			this.updateNNSearch = false;
            this.knnWasSetUp = true;
			this.numBuilds++;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double[] getVotesForInstance(Instance instance) {
		try {
			if (updateNNSearch == true){
				this.updateNNSearch();
			} else {
				if(this.getNumClassifiersPool() < 1) {
					//No classifier trained
					int majorityIndex = random.nextInt(instance.classAttribute().numValues());
					double[] probs = new double[instance.classAttribute().numValues()];
					probs[majorityIndex] = 1; // guess randomly
					return probs;
				}
			}
			Instances neighborhood = this.getLinearNNSearch().kNearestNeighbours(instance, this.numNeighbors);

			double complexity = this.overlap.measureOverlap(neighborhood);


			if (complexity >= 1.0) {
				double[] classes = new double[instance.numClasses()];
				for (int i = 0; i < neighborhood.size(); i++) {
					int neighborClass = (int) neighborhood.get(i).classValue();
					classes[neighborClass]++;
				}
				return classes;
			}
				
			Classifier[] classifiers = new Classifier[this.getNumClassifiersPool()];

			for (int i = 0; i < this.getNumClassifiersPool(); ++i) {
				classifiers[i] = this.getClassifiers().get(i);
			}


            double[] retorno = knorae.classify(classifiers, neighborhood, instance);
						
			return retorno;
		} catch (Exception e) {
			StringBuilder builder = new StringBuilder();
			getModelDescription(builder, 0);
			throw new RuntimeException(e);
		}
		
	}

	protected IncADESClassifier<PruningMetrics> addNewIncrementalClassifier(Instance instance, 
			List<Instance> DSEL) throws Exception {
		
		Classifier newClassifier = classifierFactory.buildClassifier(instance);
		
		IncADESClassifier<PruningMetrics> measuredNewClassifier =  new IncADESClassifier<PruningMetrics>(newClassifier);
		measuredNewClassifier.setIncADESClassifierMetrics(new PruningMetrics(measuredNewClassifier));
		
		List<IncADESClassifier<PruningMetrics>> classifiersToPrune = 
				this.pruningEngine.pruneClassifiers(measuredNewClassifier, getClassifiers(), DSEL);
		
		for(IncADESClassifier<PruningMetrics> ic : classifiersToPrune ){
			if(ic != measuredNewClassifier){
				this.pruneClassifier(ic);
			}else{
				measuredNewClassifier = null;
			}
		}
		
		if(measuredNewClassifier != null){
			this.getClassifiers().add(measuredNewClassifier);
		}
		
		this.trainingCount = 0;

		return measuredNewClassifier;
		
	}

	public int getNumRebuilds() {
		return this.numBuilds;
	}

	private void pruneClassifier(IncADESClassifier<PruningMetrics> classifierToPrune) {
		this.pool.remove(classifierToPrune);
	}

    protected void shrinkAccuracyEstimationWindow() throws Exception {
		
		int diff = this.instanceCount - this.warningLevel;

		if (diff < 5 || diff == instanceCount)
			diff = 5;

		while (this.getAccuracyEstimationInstances().size() > diff) {
			this.getAccuracyEstimationInstances().removeFirst();
		}		
	}

    protected void updateIncADES(Instance instance) throws Exception {

		
		if (this.trainingCount >= this.TRAINING_SIZE) {
			this.addNewIncrementalClassifier(instance,
					this.getAccuracyEstimationInstances());
			this.trainingCount = 0;
		} else {
			this.updateLastClassifier(instance);
		}
		
	}

	protected LinkedList<Instance> getAccuracyEstimationInstances() {
		return this.DSEW;
	}

	private List<IncADESClassifier<PruningMetrics>> getClassifiers() {
		return this.pool;
	}

	private void resetDetector() {
		if (this.changeDetector != null) 
			this.changeDetector.resetLearning();
	}

	private int getNumClassifiersPool() {
		return this.pool.size();
	}

	private boolean isChangeDetected() {		
		if (this.changeDetector.getChange()) {
			this.changeWasDetected = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}

	@Override
	public void resetLearningImpl() {
		this.DSEW.clear();
		this.resetDetector();
		this.pool.clear();
		this.changesDetected = 0;
		this.knnWasSetUp = false;
		this.updateNNSearch = true;
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getModelMeasurementsImpl'");
	}

	@Override
	public void getModelDescription(StringBuilder out, int indent) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getModelDescription'");
	}
}