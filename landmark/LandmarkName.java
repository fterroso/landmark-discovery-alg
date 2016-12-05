/*
 * Copyright 2014 University of Murcia (Fernando Terroso-Saenz (fterroso@um.es), Mercedes Valdes-Vela, Antonio F. Skarmeta)
 * 
 * This file is part of Landmark Discovery Algorithm.
 * 
 * Landmark Discovery Algorithm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Landmark Discovery Algorithm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see http://www.gnu.org/licenses/.
 * 
 */
package lda.landmark;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that automatically makes up the names of the new landmarks
 *
 * @author fernando
 */
    
public class LandmarkName implements Serializable{

    Map<Integer, Integer> indexLevels;

    public LandmarkName() {            
        indexLevels = new HashMap<Integer, Integer>();            
    }

    public String generateNewNameForType(LandmarkType type, String... prefix){
        String newName;
        if(indexLevels.containsKey(type.getLevel())){
            int index = indexLevels.get(type.getLevel()) +1;
            newName = String.valueOf(index) + "_"+type.getLevel();
            indexLevels.put(type.getLevel(), index);
            
        }else{
            newName = "1_"+ type.getLevel();
            indexLevels.put(type.getLevel(), 1);
        }
        if(prefix.length > 0){
            newName = prefix[0]+"_"+newName;
        }

        return newName;
    }    
    
    
}
    

