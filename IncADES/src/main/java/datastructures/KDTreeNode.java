package datastructures;

import java.io.Serializable;
import java.util.ArrayList;

import com.yahoo.labs.samoa.instances.Instance;

public class KDTreeNode implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public Instance m_InstanceInfo;
	public int splitDim;
	public KDTreeNode m_Left = null;
	public KDTreeNode m_Right = null;
	private boolean flag = true;
	public double[] lVex;
	public double[] uVex;
	public double[][] rect = {lVex, uVex};
	public ArrayList<KDTreeNode> children;
	
	public double[][] m_nodeRanges;
	
	public KDTreeNode() throws Exception {
	}
	
	public KDTreeNode(Instance inst, int splitDim) throws Exception {
		this.m_InstanceInfo = inst;
		this.splitDim = splitDim;
		if (this.splitDim < 0)
			this.splitDim = 0;
	}
	
	public void setInfo(Instance inst) {
		this.m_InstanceInfo = inst;
		}
	
	public Instance getInstance() {
		return this.m_InstanceInfo;
	}
	
	public double getSplitVal(int splitDim) {
		return this.m_InstanceInfo.toDoubleArray()[splitDim];
	}

	
	public double[] getInfo() {
		return this.m_InstanceInfo.toDoubleArray();
	}

	public void setFlagFalse() {
		this.flag = false;
	}

	public void setFlagTrue() {
		this.flag = true;
	}

	public boolean isNodeActive() {
		return this.flag;
	}
	
	public boolean isALeaf() {
		return (this.m_Left == null && this.m_Right == null);
	}

	public void updateRange(Instance inst) {
		double[] instInfo = inst.toDoubleArray();
		if (lVex == null){
			lVex = instInfo;
			uVex = instInfo;
		} else {
			for (int i = 0; i < instInfo.length; i++) {
				if (instInfo[i] < this.lVex[i]) {
					this.lVex[i] = instInfo[i];
				}
				if (instInfo[i] > this.uVex[i]) {
					this.uVex[i] = instInfo[i];
				}
			}
		}
	}

	public boolean isInRange(Instance inst) {
		double[] instInfo = inst.toDoubleArray();
		boolean inRange = true;

		for (int i = 0; i < instInfo.length; i++) {
			if ((instInfo[i] < lVex[i]) || instInfo[i] > uVex[i]){
				inRange = false;
				break;
			}
		}

		return inRange;
	}

	public double uDist(double[] p) {
		double[] v = getV(p);

		double[] sub = new double[v.length];

		for (int i = 0; i < sub.length; ++i) {
			sub[i] = v[i] - p[i];
		}

		double sum = 0;

		for (int i = 0; i < v.length; ++i) {
			double dat = sub[i];
			sum += dat*dat;
		}

		return Math.sqrt(sum);

	}

	public double[] getV(double[] p) {
		double[] cent = this.getCent();

		double[] v = this.getU(p);

		double[] dVec = this.getDVec();

		double[]u = this.getU(p);

		for (int i = 0; i < v.length; ++i) {
			v[i] = cent[i] + (dVec[i] * this.Gx(u[i]));
		}

		return v;
	}

	public double[] getDVec() {
		double[] cent = this.getCent();

		double[] dVec = new double[cent.length];

		for (int i = 0; i < cent.length; ++i) {
			dVec[i] = uVex[i] - cent[i];
		}
		return dVec;
	}

	public double[] getU(double[] p) {
		double[] cent = this.getCent();

		double[] u = new double[p.length];

		for (int i = 0; i < u.length; ++i) {
			u[i] = cent[i] - p[i];
		}
		return u;
	}

	public double[] getCent() {

		double[] cent = new double[this.lVex.length];

		for (int i = 0; i < cent.length; ++i) {
			cent[i] = (uVex[i] + lVex[i]) / 2;
		}

		return cent;
	}

	public double Gx(double x) {
		if (x >= 0)
			return 1;
		else
			return -1;
	}

	public double Ix(double x) {
		if (x >= 0)
			return 1;
		else
			return 0;
	}

	public double[][] getNodeRange() {

		double[][] range = {this.lVex, this.uVex};

		return range;
	}
	
}
