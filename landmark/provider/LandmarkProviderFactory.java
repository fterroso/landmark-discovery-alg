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
package lda.landmark.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lda.config.LDAConfigProviderFactory;

/**
 *
 * @author Fernando Terroso-Saenz
 */
public class LandmarkProviderFactory {
    
    static Map<String,LandmarkProvider> landmarkProviders = new HashMap<String,LandmarkProvider>();
        
    public static LandmarkProvider getCurrentLandmarkProvider(String userID) {
        
        LandmarkProvider landmarkProvider = null;
        if(!landmarkProviders.containsKey(userID)){
            switch(LDAConfigProviderFactory.getCurrentConfigProvider().getLandmarkProviderType()){                
                case KNN_BASED:
                    landmarkProvider = new KNNLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;                
                case NO_GROWING_DENSITY_BASED:
                    landmarkProvider = new DensityNoGrowingLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;
                case DENSITY_BASED:
                    landmarkProvider = new DensityLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;
                case BASIC:
                    landmarkProvider = new BasicLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;
            }
            landmarkProvider.setUserID(userID);
            landmarkProviders.put(userID, landmarkProvider);
        }          
                    
        return landmarkProviders.get(userID);        
    }  
    
    public static LandmarkProvider getCurrentLandmarkProvider(
            String userID,
            LandmarkProviderType type) {
        
        LandmarkProvider landmarkProvider = null;
        if(!landmarkProviders.containsKey(userID)){
            switch(type){
                case KNN_BASED:
                    landmarkProvider = new KNNLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;                   
                case NO_GROWING_DENSITY_BASED:
                    landmarkProvider = new DensityNoGrowingLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;
                case DENSITY_BASED:
                    landmarkProvider = new DensityLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;
                case BASIC:
                    landmarkProvider = new BasicLandmarkProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                    break;
            }
            
            landmarkProvider.setUserID(userID);
            landmarkProviders.put(userID, landmarkProvider);
        }          
                    
        return landmarkProviders.get(userID); 

    }

    public static int getTotalNumberOfLandmarks(){
        int total =0;
        for(String lp : landmarkProviders.keySet()){
            total += landmarkProviders.get(lp).getTotalNumberOfLandmarks();
            
        }
        return total;
    }
    
    public static Set<String> getUsersWithLandmarks(){
        return landmarkProviders.keySet();
    }
    
    public enum LandmarkProviderType{
        BASIC,
        DENSITY_BASED,
        NO_GROWING_DENSITY_BASED,
        KNN_BASED;
    }
}
