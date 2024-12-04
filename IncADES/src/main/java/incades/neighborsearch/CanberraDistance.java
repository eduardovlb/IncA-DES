/*    
*    CanberraDistance.java 
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

package incades.neighborsearch;

import org.apache.commons.math3.util.FastMath;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import moa.classifiers.lazy.neighboursearch.NormalizableDistance;

public class CanberraDistance extends NormalizableDistance implements Cloneable {
	
	public CanberraDistance() {
	}
	
	public CanberraDistance(Instances data) {
		super(data);
	}
	
	@Override
	public double distance(Instance first, Instance second) {
		double[] x = first.toDoubleArray();
		double[] y = second.toDoubleArray();
		
		int classIndex = first.classIndex();

		double sum = 0;
		
		for (int i = 0; i < x.length; i++) {
			if (i != classIndex) {
				double numerator = FastMath.abs(x[i]-y[i]);
				double denominator = FastMath.abs(x[i]) + FastMath.abs(y[i]);
				if (denominator != 0)
					sum += numerator / denominator;
			}			
		}
		
	    return sum;
	  }
	
	@Override
	public double distance(Instance first, Instance second, double cutOffValue) {
		double[] x = first.toDoubleArray();
		double[] y = second.toDoubleArray();
		
		int classIndex = first.classIndex();

		double sum = 0;
		
		for (int i = 0; i < x.length; i++) {
			if (i != classIndex) {
				double numerator = FastMath.abs(x[i]-y[i]);
				double denominator = FastMath.abs(x[i]) + FastMath.abs(y[i]);
				if (denominator != 0)
					sum += numerator / denominator;
			}			
		}
		
	    return sum;
	  }
	
	public double canbDifference(int index, double val1, double val2) {
	    double val = difference(index, val1, val2);
	    return Math.abs(val);
	  }
	
	public double sqDifference(int index, double val1, double val2) {
	    double val = difference(index, val1, val2);
	    return val*val;
	  }
	
	protected double updateDistance(double currDist, double diff) {
				
		return 0;
	}

	public boolean valueIsSmallerEqual(Instance instance, 
			int dim, double value) {  //This stays
		return instance.value(dim) <= value;
	}
	
	public boolean valueIsSmaller(Instance instance, 
			int dim, double value) {  //This stays
		return instance.value(dim) < value;
	}
	
	public boolean valueIsSmaller(double instanceValue, 
			int dim, double value) {  //This stays
		return instanceValue == value;
	}
	
	public boolean valueIsEqual(Instance instance, 
			int dim, double value) {  //This stays
		return instance.value(dim) == value;
	}
	
	
	
	public boolean valueIsEqual(double instanceValue, 
			int dim, double value) {  //This stays
		return instanceValue == value;
	}
	
	@Override
	public String globalInfo() {
		return "Implementing Canberra Distance.";
	}
	
	public int closestPoint(Instance instance, Instances allPoints,
			  int[] pointList) throws Exception {
		double minDist = Integer.MAX_VALUE;
		int bestPoint = 0;
		for (int i = 0; i < pointList.length; i++) {
			double dist = distance(instance, allPoints.instance(pointList[i]), Double.POSITIVE_INFINITY);
			if (dist < minDist) {
				minDist = dist;
				bestPoint = i;
			}
		}
		return pointList[bestPoint];
	}
	

}