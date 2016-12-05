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
package lda.landmark.level;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lda.landmark.Landmark;
import lda.landmark.LandmarkType;
import org.apache.log4j.Logger;
import lda.tools.Constants;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class BasicLandmarkLevelProvider implements LandmarkLevelProvider, Serializable {

    static Logger LOG = Logger.getLogger(BasicLandmarkLevelProvider.class);
    
    protected float incParam = 10f;
    
    protected int m = 4;
    protected float p = 0.25f;
    
    Map<String, Map<Integer, LandmarkType>> candidateTypes;        
    Map<String, Map<Integer, LandmarkType>> types;    
    Map<String, Integer> attemps;
    Map<String, Float> increments;
    
    public BasicLandmarkLevelProvider() {
        types = new HashMap<String, Map<Integer, LandmarkType>>();
        candidateTypes = new HashMap<String, Map<Integer, LandmarkType>>();
        attemps = new HashMap<String,  Integer>();
        increments = new HashMap<String, Float>();
    }
            
    public LandmarkType getTypeForLevel(String userID, int level, Landmark previousLandmark) {
        LandmarkType result = null;
        
        Map<Integer, LandmarkType> typesForUser = types.get(userID);
        Map<Integer, LandmarkType> candidateTypesForUser = candidateTypes.get(userID);
        
        if(typesForUser == null){
            typesForUser = initTypesForUser(userID);
        }
        
        if(candidateTypesForUser == null){
            candidateTypesForUser = new HashMap<Integer, LandmarkType>();
        }
        
        if(typesForUser.containsKey(level)){
            result = typesForUser.get(level);
        }else{
            if(previousLandmark != null){
                                                                              
                if(candidateTypesForUser.containsKey(level)){
                    LandmarkType currentType = candidateTypesForUser.get(level);

                    int attemp = attemps.get(userID)+1;                      
                    float inc = increments.get(userID);
                    
                    if(attemp >= m){
                        LOG.info(m + " attemps without detecting landmark at level " + level +". Increase "+ inc + "-> "+ (inc * (1+p)));
                        inc = inc * (1+p);
                        attemp = 0;
                        
                        LandmarkType prevType = typesForUser.get(level-1);
                        float newRadius = prevType.getRadius() * (1+inc);
                        int numPoints = (int)(prevType.getMinPoints() * (1+inc)); 
                        
                        currentType.setMinPoints(numPoints);
                        currentType.setRadius(newRadius);
                        
                        candidateTypesForUser.put(level, currentType);
                        
                        increments.put(userID, inc); 
                    }
                    
                     result = currentType;
                    attemps.put(userID, attemp);
                }else{
                    LandmarkType prevType = typesForUser.get(level-1);

                    if(prevType == null){
                        prevType = candidateTypesForUser.get(level-1);
                        typesForUser.put(level-1, prevType); 
                    }

                    float newRadius = prevType.getRadius() * (1+incParam);
                    int numPoints = (int)(prevType.getMinPoints() * (1+incParam)); 
                    LandmarkType newType = new LandmarkType(level, newRadius, numPoints);

                    candidateTypesForUser.put(level, newType);  
                                        
                    attemps.put(userID, 0);
                    increments.put(userID, incParam);
                    
                    result = newType;
                }                               
            }
        }
        
        types.put(userID, typesForUser);
        candidateTypes.put(userID, candidateTypesForUser);
        
        return result;
    }
    
    protected Map<Integer, LandmarkType> initTypesForUser(String userID){
        Map<Integer, LandmarkType> typesForUser = new HashMap<Integer, LandmarkType>();
        LandmarkType initialType = new LandmarkType(0, Constants.MIN_RADIUS, Constants.MIN_POINTS);
        
        typesForUser.put(0, initialType);
        
        return typesForUser;
    }
    
}
