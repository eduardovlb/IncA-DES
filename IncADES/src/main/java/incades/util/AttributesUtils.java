/*    
*    AttributesUtils.java 
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
package incades.util;

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

public class AttributesUtils {
	public static List<Attribute> copyAtributes(Instances originalDataset){
		List<Attribute> atributos = new ArrayList<Attribute>(originalDataset.numAttributes());
		for(int i =0; i < originalDataset.numAttributes(); i++){
				Attribute atribOriginal = originalDataset.attribute(i);
				atributos.add(atribOriginal);
		}
		return atributos;
	}
	
	public static ArrayList<Attribute> copyAtributes(Instance instance){
		ArrayList<Attribute> atributes = new ArrayList<Attribute>(instance.numAttributes());
		for(int i =0; i < instance.numAttributes(); i++){
				Attribute atribOriginal = instance.attribute(i);
				atributes.add(atribOriginal);
		}
		return atributes;
	}
}