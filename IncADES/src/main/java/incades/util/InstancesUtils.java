/*    
*    InstancesUtils.java 
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

public class InstancesUtils {	
	
	public static Instances gerarDataset(List<Instance> instancias, String nomeDataset) throws Exception{
		ArrayList<Attribute> atributos = AttributesUtils.copyAtributes(instancias.get(0));
		
		Instances retorno = new Instances(nomeDataset, atributos,
				instancias.size());
		for(Instance inst : instancias){
			retorno.add(inst);
		}
		
		retorno.setClassIndex(retorno.numAttributes() - 1);
		
		return retorno;
	}
}