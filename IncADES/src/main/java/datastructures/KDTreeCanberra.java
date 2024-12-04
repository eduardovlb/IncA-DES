/*    
*    KDTreeCanberra.java 
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

package datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.management.InstanceNotFoundException;

import org.apache.commons.math3.util.FastMath;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import incades.neighborsearch.CanberraDistance;
import incades.util.InstancesUtils;
import incades.neighborsearch.StreamNeighborSearch;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;
import moa.classifiers.lazy.neighboursearch.NormalizableDistance;

public class KDTreeCanberra extends NearestNeighbourSearch implements StreamNeighborSearch {

	private static final long serialVersionUID = 1L;

	private int nDims = 0;
	private int numInstances = 0;
	int numNeighbours = 5;

	int factor = 20;

	double a;
	
	protected ArrayList<Instance> instancesList = new ArrayList<>();
	
	ArrayList<Double> distancesList = new ArrayList<Double>();
	
	protected int[] m_InstList;
	
	double[][] ranges;
	
	private KDTreeNode m_Root = null;

	private int numNodesDeactivated = 0;
	
	NormalizableDistance distanceFunction;
	
	private int initialNumInstances = 0;
	
	
	public KDTreeCanberra() {
		super();
		this.distanceFunction = new CanberraDistance();
	}

	public KDTreeCanberra(Instances instances) throws Exception {
		super(instances);
		this.nDims = instances.get(0).numAttributes()-1;
		this.buildKDTree(instances);
		this.distanceFunction = new CanberraDistance();
		this.a = this.nDims/this.factor;
		this.a = FastMath.max(a, 1);
	}
	
	public KDTreeCanberra(Instances instances, int numNeighbours) {
		super(instances);
		this.nDims = instances.get(0).numAttributes()-1;
		this.distanceFunction = new CanberraDistance();
		this.numNeighbours = numNeighbours;
		this.a = this.nDims/this.factor;
		this.a = FastMath.max(a, 1);
	}
	
	@Override
	public Instance nearestNeighbour(Instance target) throws Exception {

		Instances dist = kNearestNeighbours(target, 1);
		
		return dist.get(0);
	}
	
	public int getNumInstances() {
		return this.numInstances;
	}
	
	protected ArrayList<Double> getDistancesOfBranches(KDTreeNode node, Instance target) {
		
		ArrayList<Double> distances = new ArrayList<Double>();

		this.instancesList.clear();
		
		double[] targetInfo = target.toDoubleArray();

		if (node.isNodeActive()) {
			double distanceToNode = this.distanceFunction.distance(node.getInstance(), target);
				distances.add(distanceToNode);
				this.instancesList.add(node.getInstance());
		}
		
		KDTreeNode best = null;
		KDTreeNode other = null;
		
		if (node.m_Right != null && node.m_Left != null) {

			if (targetInfo[node.splitDim] >= node.getInfo()[node.splitDim]) {
				best = node.m_Right;
				other = node.m_Left;
			} else {
				best = node.m_Left;
				other = node.m_Right;
			}
		} else if (node.m_Right != null) {
			best = node.m_Right;
		} else if (node.m_Left != null) {
			best = node.m_Left;
		} else return distances;
	
	
		distances = getDistancesOfBranches(best, target, distances);

		double maximum = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < distances.size(); i++) {
			double toTest = distances.get(i);
			if (toTest > maximum)
				maximum = toTest;
		}

		if (other != null) {
			if (isToSearchNode(node, target, node.splitDim, maximum) || distances.size() < this.numNeighbours) {
				distances = getDistancesOfBranches(other, target, distances);
			}
		}
		
		return distances;
	}
	
protected ArrayList<Double> getDistancesOfBranches(KDTreeNode node, Instance target, ArrayList<Double> distances) {
		
		if (node == null)
			return distances;
	

		// this.nodesSearched++;
		
		
		if (node.isNodeActive()) {
			double distanceToNode = this.distanceFunction.distance(node.getInstance(), target);
			if (distances.size() < this.numNeighbours) {
				distances.add(distanceToNode);
				this.instancesList.add(node.getInstance());			
		}
		else {
			double maximum = distances.get(0);
			int maxIndex = 0;
			for (int i = 0; i < distances.size(); i++) {
				double toTest = distances.get(i);
				if (toTest > maximum) {
					maximum = toTest;
					maxIndex = i;
				}
			}
			if (distanceToNode <= distances.get(maxIndex)) {
				distances.remove(maxIndex);
				distances.add(distanceToNode);
				this.instancesList.remove(maxIndex);
				this.instancesList.add(node.getInstance());				
			}
		}
		}
		
		double[] targetInfo = target.toDoubleArray();
		
		if (node.m_Right != null && node.m_Left != null) {
						
			KDTreeNode best = null;
			KDTreeNode other = null;
			
			if (targetInfo[node.splitDim] >= node.getInfo()[node.splitDim]) {
				best = node.m_Right;
				other = node.m_Left;
			} else {
				best = node.m_Left;
				other = node.m_Right;
			}
			
			double maximum = Double.NEGATIVE_INFINITY;

			for (int i = 1; i < distances.size(); i++) {
				double toTest = distances.get(i);
				if (toTest > maximum)
					maximum = toTest;
			}

			if (isToSearchNode(node, target, node.splitDim, maximum) || distances.size() < this.numNeighbours) {
				distances = getDistancesOfBranches(best, target, distances);
			}

			maximum = Double.NEGATIVE_INFINITY;

			for (int i = 1; i < distances.size(); i++) {
				double toTest = distances.get(i);
				if (toTest > maximum)
					maximum = toTest;
			}

			if (other != null) {
				if (isToSearchNode(node, target, node.splitDim, maximum) || distances.size() < this.numNeighbours) {
					distances = getDistancesOfBranches(other, target, distances);
				}
			}
		} else if (node.m_Right != null) {
			distances = getDistancesOfBranches(node.m_Right, target, distances);
		} else if (node.m_Left != null) {
			distances = getDistancesOfBranches(node.m_Left, target, distances);
		}
		
		return distances;
	}

	@Override
	public Instances kNearestNeighbours(Instance target, int k) throws Exception {

		if (this.numInstances == 0) {
			throw new Exception("The K-d tree was not initialized. Please use the method setInstances(Instances)");
		}
		
		this.numNeighbours = k;
		
		ArrayList<Double> distances =  this.getDistancesOfBranches(m_Root, target);
		
		LinkedList<Instance> instances = new LinkedList<Instance>();
		
		int kNeighbors;
		
		if (distances.size() < this.numNeighbours)
			kNeighbors = distances.size();
		else
			kNeighbors = this.numNeighbours;
		
		
		for (int i = 0; i < kNeighbors; i++) {
			instances.add(this.instancesList.get(i));
		}
		
		Instances insts = InstancesUtils.gerarDataset(instances, "Neighbors found");
		
		return insts;
		
	}

	@Override
	public double[] getDistances() throws Exception {
		return null;
	}

	public void printDistances() {
		for (int i = 0; i < this.distancesList.size(); i++) {
			System.out.println(this.distancesList.get(i));
		}
	}
	
	@Override
	public void update(Instance ins) throws Exception {
		if (this.numInstances == 0) {
			throw new InitializationException("Tree was not created. "
					+ "Please call the BuildKDTree method first");
		}
		// this.distanceFunction.getInstances().add(ins);
		this.insert(ins);		
	}
	
	private void buildKDTreeBalanced(ArrayList<Instance> insts, int depth) throws Exception {
		ArrayList<Double> values = new ArrayList<Double>();
		
		if (insts.size() == 0)
			return;
		
		if (insts.size() == 1){
			this.insert(insts.get(0));;
			return;
		}

			if (insts.size() == 2) {
				Instance inst1 = insts.get(0);
				Instance inst2 = insts.get(1);
	
				if (inst1.toDoubleArray()[depth] >= inst2.toDoubleArray()[depth]) {
					this.insert(inst1);
					this.insert(inst2);
					return;
				} else {
					this.insert(inst2);
					this.insert(inst1);
					return;
				}
	
			}
		
		
		for (int i = 0; i < insts.size(); i++) {
			values.add(insts.get(i).toDoubleArray()[depth]);
		}
		
		double median = getMedian(values);
		
		Instance medianInstance = null;
		
		ArrayList<Instance> instancesToTheLeft = new ArrayList<Instance>();
		ArrayList<Instance> instancesToTheRight = new ArrayList<Instance>();
		
			for (int i = 0; i < insts.size(); i++) {
				if(insts.get(i).toDoubleArray()[depth] == median && medianInstance == null) {
					medianInstance = insts.get(i);
				}
				else if (insts.get(i).toDoubleArray()[depth] < median)
					instancesToTheLeft.add(insts.get(i));
				else
					instancesToTheRight.add(insts.get(i));
			}
		
		this.insert(medianInstance);

		buildKDTreeBalanced(instancesToTheLeft, (depth+1)%this.nDims);
		buildKDTreeBalanced(instancesToTheRight, (depth+1)%this.nDims);
		
	}
	
	private void buildKDTreeBalanced(Instances insts, int depth) throws Exception {
		ArrayList<Double> values = new ArrayList<Double>();
		
		if (insts.size() == 0)
			throw new InstanceNotFoundException("Instance list is empty.");
		
		if (insts.size() == 1){
			this.insert(insts.get(0));
			return;
		}
		
		if (insts.size() == 2) {
			Instance inst1 = insts.get(0);
			Instance inst2 = insts.get(1);

			if (inst1.toDoubleArray()[depth] >= inst2.toDoubleArray()[depth]) {
				this.insert(inst1);
				this.insert(inst2);
			} else {
				this.insert(inst2);
				this.insert(inst1);
			}
			return;
		}
		
		for (int i = 0; i < insts.size(); i++) {
			values.add(insts.get(i).toDoubleArray()[depth]);
		}
		
		double median = getMedian(values);
		
		Instance medianInstance = null;
		
		ArrayList<Instance> instancesToTheLeft = new ArrayList<Instance>();
		ArrayList<Instance> instancesToTheRight = new ArrayList<Instance>();
		
		for (int i = 0; i < insts.size(); i++) {
			if(insts.get(i).toDoubleArray()[depth] == median && medianInstance == null) {
				medianInstance = insts.get(i);
			}
			else if (insts.get(i).toDoubleArray()[depth] < median)
				instancesToTheLeft.add(insts.get(i));
			else
				instancesToTheRight.add(insts.get(i));
		}
		
		this.insert(medianInstance);

		buildKDTreeBalanced(instancesToTheLeft, (depth+1)%this.nDims);
		buildKDTreeBalanced(instancesToTheRight, (depth+1)%this.nDims);		
	}
	
	protected double getMedian(ArrayList<Double> values) {
		
		Collections.sort(values);
        return values.get( (int) (values.size() + 1) / 2 - 1);
		
	}
	
	
	@Override
	public void setInstances(Instances instances) throws Exception {
		super.setInstances(instances);
		if (this.nDims == 0){
			this.nDims = instances.get(0).numAttributes()-1;
			this.a = this.nDims/this.factor;
			this.a = FastMath.max(a, 1);
		}
		this.distanceFunction.setInstances(instances);
		this.buildKDTree(instances);
	}
	
	public void buildKDTree(Instances instances) throws Exception {
		
		this.buildKDTreeBalanced(instances, 0);

		this.initialNumInstances = this.numInstances;
	}
	
	protected void insert(Instance inst) throws Exception {
		
		KDTreeNode p = this.m_Root;
		KDTreeNode prev = null;
		double[] info = inst.toDoubleArray();
		
		int i = 0;
		
		while (p != null) {
			prev = p;
			if (info[i] < p.getInfo()[i])
				p = p.m_Left;
			else
				p = p.m_Right;
			i = (i+1) % nDims;
		}
		
		int index = (i-1)%nDims;
		if (index < 0)
			index = nDims-1;
		
		if (this.m_Root == null)
			this.m_Root = new KDTreeNode(inst, i);
		else if (info[index] < prev.getInfo()[index]) 
			prev.m_Left = new KDTreeNode(inst, i);
		else
			prev.m_Right = new KDTreeNode(inst, i);
		this.numInstances++;		
		}
	
	public void removeInstance(Instance inst) throws Exception {
		
		KDTreeNode nodeToRemove = search(inst, m_Root);
		
		if (nodeToRemove == null)
			throw new InstanceNotFoundException("Instance not found on KDTree. Is there any missing data on the dataset?");
		
		delete(nodeToRemove);
		this.numInstances--;
	}
	
	public KDTreeNode search(Instance inst, KDTreeNode node) throws Exception {
		double[] instInfo = inst.toDoubleArray();
		
		if (node == null)
			return null;
		
		if (isInstanceEqual(inst, node.getInstance()) && node.isNodeActive())
			return node;
		


		KDTreeNode nodeToReturn = null;
		
		if (instInfo[node.splitDim] < node.getInfo()[node.splitDim]) {
			nodeToReturn = search(inst, node.m_Left);
		}
		else {
			nodeToReturn = search(inst, node.m_Right);
		}
		
		return nodeToReturn;
		
	}

	public boolean isToRebuild() {
		boolean retorno = false;

		if (((double) this.numNodesDeactivated / (double) this.numInstances >= 0.3)) {
			retorno = true;
		}

		if (this.numInstances > this.initialNumInstances*2) {
			retorno = true;
		}


		return retorno;
	}
	
	private boolean isInstanceEqual(Instance inst1, Instance inst2) {	
		
		boolean found = true;
		
		double[] infoInst1 = inst1.toDoubleArray();
		double[] infoInst2 = inst2.toDoubleArray();
		
		for (int i = 0; i < infoInst1.length; i++) {
			if (infoInst1[i] != infoInst2[i]) {
				found = false;
				break;
			}
		}
		return found;
	}
	
	protected void delete(KDTreeNode p) throws Exception {
		
		if (p.isALeaf()) {
			p = null;
			return;
		}
			p.setFlagFalse();
			this.numNodesDeactivated++;
	}
	
	@Override
	public Instances getInstances() {
		Instances insts = new Instances();
		for (Instance inst : this.instancesList) {
			insts.add(inst);
		}
	    return insts;
	  }

	private boolean isToSearchNode(KDTreeNode root, Instance target, int splitDim, double maximum) {

		double minimumDistance = target.toDoubleArray()[splitDim] - root.getInfo()[splitDim];
		double denominator = FastMath.abs(target.toDoubleArray()[splitDim]) + FastMath.abs(root.getInfo()[splitDim]);

		double modDist = 0;

		if (denominator != 0) {
			modDist = FastMath.abs(minimumDistance) / denominator;
		}		


		// double compare = a*modDist;
		double compare = 1*modDist;

		if (compare <= maximum)
			return true;

		return false;
	}

}
