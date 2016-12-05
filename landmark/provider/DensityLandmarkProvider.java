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

import lda.landmark.Landmark;
import lda.config.LDAConfigProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import lda.location.Location;
import lda.landmark.LandmarkType;
import lda.location.Centroid;
import lda.tools.Constants;
import lda.tools.SupportFunctions;


/**
 * Landmark provider that uses a density-based approach to make up the
 * clusters.
 *
 * @author Fernando Terroso-Saenz.
 */
public class DensityLandmarkProvider extends LandmarkProvider {
    
    static Logger LOG = Logger.getLogger(DensityLandmarkProvider.class);
    
        
    private int totalLocations = 0;
    private int storedLocations = 0;
    private boolean isNewLandmark = false;
    
    public DensityLandmarkProvider(LDAConfigProvider config){
        super(config);
    }
    
    @Override
    protected Landmark inferLandmarkForPointAndType(
            Location location, 
            LandmarkType landmarkType,
            boolean closeness,
            String... landmarkPrefixName){
        
        isNewLandmark = false;
        Landmark result = null;
                
        Set<Location> closeLocations  = positionProvider.getPointsInRange(location, landmarkType.getRadius());
        Set<Centroid> centroids = positionProvider.getCentroidsFromPointsAndType(closeLocations, landmarkType);
                
        if(!centroids.isEmpty()){

            counter1++;
            addPosition(location);
            
            Set<Landmark> closeLandmarks = new HashSet<Landmark>();
            
            boolean first = true;
            Iterator<Centroid> it = centroids.iterator();
            while(it.hasNext()){
                Centroid centroid = it.next();
                Landmark landmark = getLandmarkWithName(centroid.getLandmarkNameForType(landmarkType));
                
                if(first){
                    location.setCentroid(landmarkType,centroid);
                    first = false;
                }
                closeLandmarks.add(landmark);                                           
            }

//            if(closeLandmarks.size() > 1){
//                result = Landmark.mergeLandmarks(closeLandmarks);   
//                removeLandmarks(closeLandmarks);
//            }else{
                Iterator<Landmark> itLand = closeLandmarks.iterator();                     
                result = itLand.next();
//            }
                
            result.addPoint(location);
            
        }else{
//               System.out.println("Antse  en condicion" + closeLocations.size() + " > "+landmarkType.getMinPoints());         
            if(closeLocations.size() >= landmarkType.getMinPoints()){
//                System.out.println("Entra en condicion");
                counter2++;
                
                Centroid newCentroid = Centroid.createFromLocation(location);
                
                Set<Landmark> closeLandmarks = new HashSet<Landmark>();
                Map<Landmark,Integer> intersectPoinsForLandmark = new HashMap<Landmark,Integer>();
                List<Location> closeLocationsWithNoCentroid = new ArrayList<Location>();
                             
                boolean intersect = false;
                for(Location closeLocation: closeLocations){
                    
                    Landmark l = null;
                    if(closeLocation.getCentroid(landmarkType) != null){
                        l = getLandmarkWithName(closeLocation.getCentroid(landmarkType).getLandmarkNameForType(landmarkType));
                    }else{
                        closeLocationsWithNoCentroid.add(closeLocation);
                    }   
                    
                    if(l != null){
                        intersect = true;
                        int nIntersectPoints= 0;
                        if(intersectPoinsForLandmark.containsKey(l)){
                            nIntersectPoints = intersectPoinsForLandmark.get(l);
                        }                        
                        if(++nIntersectPoints > (landmarkType.getMinPoints() * 0.5)){                        
                            closeLandmarks.add(l);
                        }
                        
                        intersectPoinsForLandmark.put(l, nIntersectPoints);
                    }
                }
                
                if(!closeLandmarks.isEmpty()){
                    counter2a++;
//                    if(closeLandmarks.size() > 1){
//                        result = Landmark.mergeLandmarks(closeLandmarks);
//                        removeLandmarks(closeLandmarks);
//                    }else{
                    Iterator<Landmark> itLand = closeLandmarks.iterator();                     
                    result = itLand.next();
//                    }
                    result.addCentroid(newCentroid);
                }else if(!intersect){
                    counter2b++;
                    for(Location closeLocation : closeLocationsWithNoCentroid){
                        closeLocation.setCentroid(landmarkType,newCentroid);
                        storedLocations--;
                    }
                    
                    Set<Centroid> centroidsForNewLandmark = new HashSet<Centroid>();
                    centroidsForNewLandmark.add(newCentroid);
                    if(landmarkPrefixName.length > 0)
                        result = generateNewLandmark(landmarkType, centroidsForNewLandmark, closeLocations,landmarkPrefixName[0]);
                    else
                        result = generateNewLandmark(landmarkType, centroidsForNewLandmark, closeLocations);
//                    System.out.println("Result: " + result);
                    isNewLandmark = true;
                } 
                              
                addCentroid(newCentroid);

            }else{                
                addPosition(location);
                if(closeness && landmarkType.getLevel() == 1){
                    counter3++;
                    double minDist = Double.POSITIVE_INFINITY;
                    String closestLandmarkName = null;
                    for(Location closeLocation : closeLocations){
                        if(closeLocation.getCentroid(landmarkType) != null){
                            double dist = SupportFunctions.dist(location.getX(),location.getY(),closeLocation.getX(),closeLocation.getY());
                            if(dist< (landmarkType.getRadius()*Constants.CLOSENESS_FACTOR) && dist < minDist){
                                  minDist = dist;
                                  closestLandmarkName = closeLocation.getCentroid(landmarkType).getLandmarkNameForType(landmarkType);
                            }
                           
                        }
                    }
                    if(closestLandmarkName!= null){                        
                        result = getLandmarkWithName(closestLandmarkName);
                    }
                }
            }           
        }
 
        totalLocations++;
        if(result ==null || isNewLandmark){
            storedLocations++;
        }
        if((totalLocations%1000)== 0){
            System.out.println(totalLocations + " "+storedLocations);
        }

        return result;
    }      
}
