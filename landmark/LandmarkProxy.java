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

import lda.place.Place;
import lda.place.PlaceType;
import lda.landmark.provider.LandmarkProvider;
import org.apache.log4j.Logger;
import lda.location.Location;

/**
 *
 * @author fernando
 */
public class LandmarkProxy extends Place{
    
    static Logger LOG = Logger.getLogger(LandmarkProxy.class);
    
    private final String landmarkName;
    LandmarkProvider landmarks;

    public LandmarkProxy(String landmarkName, LandmarkProvider clusters) {
        this.landmarkName = landmarkName;
        this.landmarks = clusters;
    }

    @Override
    public String getID() {
       return landmarkName;
    }

    @Override
    public PlaceType getType() {
        return PlaceType.LANDMARK;
    }
    
    public Location getMiddlePoint(){
        return this.getUnderlayingLandmark().getMiddlePoint();
    }
    
    public boolean contains(Location point){
        Landmark cluster = getUnderlayingLandmark();
        
        return cluster.containsPoint(point);
    }
    
    @Override
    public String toString(){
        return landmarkName;
    }
    
    public String underlayingLandmarkToString(){
        return getUnderlayingLandmark().toString();
    }

    @Override
    public boolean canBeMerged(Object o) {
                
        if(!(o instanceof Place)){
            return false;
        }
            
        Place place = (Place) o;
        Landmark underlayingCluster = getUnderlayingLandmark();
        
        if(place.getType().equals(PlaceType.POINT)){

            Location pos = (Location) place;
            if(!underlayingCluster.containsPoint(pos)){
                return false;
            }
        }else if(place.getType().equals(PlaceType.LANDMARK)){
            LandmarkProxy otherClusterProxy = (LandmarkProxy) place;
            Landmark otherCluster = landmarks.getLandmarkWithName(otherClusterProxy.getID());
            
            Landmark father = underlayingCluster.getFather();
            Landmark otherFather = otherCluster.getFather();
            
            if(father != null && otherFather != null){
                if(!father.equals(otherFather)){
                    return false;
                }
            }else{
                return false;
            }                        
        }
        
        return true;
    }

    @Override
    public Place merge(Object o) {
       
        Place result = null;
        if(canBeMerged(o)){
            Place place = (Place) o;
            Landmark underlayingCluster = getUnderlayingLandmark();
            
            if(place.getType().equals(PlaceType.POINT)){
                result= this;
            }else if(place.getType().equals(PlaceType.LANDMARK)){
                
                LandmarkProxy otherClusterProxy = (LandmarkProxy) place;
                
                if(otherClusterProxy.getID().equals(getID())){
                    result = this;
                    
                }else{
                    Landmark father = underlayingCluster.getFather();
                    result = landmarks.insertLandmarkOfType(father.getType(), father);
                }
            }
        }
        
        return result;
    }
    
    private Landmark getUnderlayingLandmark(){
        return landmarks.getLandmarkWithName(landmarkName);
    }
    
}
