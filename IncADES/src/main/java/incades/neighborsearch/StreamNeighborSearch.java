/*    
*    StreamNeighborSearch.java 
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

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

public interface StreamNeighborSearch {
    public Instances kNearestNeighbours(Instance target, int k) throws Exception;
    public void update(Instance inst) throws Exception;
    public void removeInstance(Instance inst) throws Exception;
    public void setInstances(Instances insts) throws Exception;
    public boolean isToRebuild();
}
