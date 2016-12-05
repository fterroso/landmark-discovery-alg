/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * 
 */
package lda.landmark.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lda.config.LDAConfigProvider;
import lda.landmark.Landmark;
import lda.landmark.LandmarkType;
import lda.location.Centroid;
import lda.location.Location;
import lda.tools.Constants;
import lda.tools.SupportFunctions;
import org.apache.log4j.Logger;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class DensityNoGrowingLandmarkProvider extends LandmarkProvider{
    
    static Logger LOG = Logger.getLogger(DensityNoGrowingLandmarkProvider.class);
    
    private int totalLocations = 0;
    private int storedLocations = 0;
    
    public DensityNoGrowingLandmarkProvider(LDAConfigProvider config){
        super(config);
    }
    
        @Override
    protected Landmark inferLandmarkForPointAndType(
            Location location, 
            LandmarkType landmarkType,
            boolean closeness,
            String... landmarkPrefixName){
        
        Landmark result = null;
                
        Set<Location> closeLocations  = positionProvider.getPointsInRange(location, landmarkType.getRadius());
        Set<Centroid> centroids = positionProvider.getCentroidsFromPointsAndType(closeLocations, landmarkType);
        
        if(!centroids.isEmpty()){

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
            
            for(Landmark l : closeLandmarks){
                if(SupportFunctions.dist(l.getMiddlePoint().getX(), l.getMiddlePoint().getY(), location.getX(), location.getY()) < landmarkType.getRadius()){
                    result = l;
                    break;
                }
            }
                
            result.addPoint(location);
            
        }else{
                        
            if(closeLocations.size() >= landmarkType.getMinPoints()){
                
                Centroid newCentroid = Centroid.createFromLocation(location);
                
                Set<Landmark> closeLandmarks = new HashSet<Landmark>();
                List<Location> closeLocationsWithNoCentroid = new ArrayList<Location>();
                             
                for(Location closeLocation: closeLocations){                    
                    if(closeLocation.getCentroid(landmarkType) != null){
                        Landmark l = getLandmarkWithName(closeLocation.getCentroid(landmarkType).getLandmarkNameForType(landmarkType));
                        if(l!=null){
                            if(SupportFunctions.dist(l.getMiddlePoint().getX(), l.getMiddlePoint().getY(), location.getX(), location.getY()) < landmarkType.getRadius()){
                                closeLandmarks.add(l);
                            }
                        }
                    }else{
                        closeLocationsWithNoCentroid.add(closeLocation);
                    }                       
                }
                
                if(!closeLandmarks.isEmpty()){

                    Iterator<Landmark> itLand = closeLandmarks.iterator();                     
                    result = itLand.next();
                    result.addCentroid(newCentroid);
                    
                }else{
                    for(Location closeLocation : closeLocationsWithNoCentroid){
                        closeLocation.setCentroid(landmarkType,newCentroid);
                        storedLocations--;
                    }
                    
                    Set<Centroid> centroidsForNewLandmark = new HashSet<Centroid>();
                    centroidsForNewLandmark.add(newCentroid);
                    result = generateNewLandmark(landmarkType, centroidsForNewLandmark, closeLocations);
                } 
                              
                addCentroid(newCentroid);

            }else{                
                addPosition(location);
                if(closeness && landmarkType.getLevel() == 1){
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
        if(result == null){
            storedLocations++;
        }
        
        if((totalLocations %1000)== 0){
            System.out.println(totalLocations + " "+ storedLocations);
        }
        
        return result;
    }      
}
