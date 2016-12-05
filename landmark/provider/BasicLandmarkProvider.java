/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * 
 */
package lda.landmark.provider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lda.config.LDAConfigProvider;
import lda.landmark.Landmark;
import lda.landmark.LandmarkType;
import lda.location.Centroid;
import lda.location.Location;
import lda.tools.SupportFunctions;

/**
 * Landmark provider that considers each single point as a landmark.
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class BasicLandmarkProvider extends LandmarkProvider{
    
    public BasicLandmarkProvider(LDAConfigProvider config){
        super(config);
    } 

    @Override
    protected Landmark inferLandmarkForPointAndType(
            Location location, 
            LandmarkType landmarkType, 
            boolean closeness,
            String... landmarkPrefixName) {
               
        Landmark l = null;
        
        Set<Location> closeLocations  = positionProvider.getPointsInRange(location, landmarkType.getRadius());
        Set<Centroid> centroids = positionProvider.getCentroidsFromPointsAndType(closeLocations, landmarkType);
        
        Centroid c = Centroid.createFromLocation(location);
        if(!centroids.isEmpty()){
            
            counter1++;
            
            Set<Landmark> closeLandmarks = new HashSet<Landmark>();
            
//            boolean first = true;
            Iterator<Centroid> it = centroids.iterator();
            while(it.hasNext()){
                Centroid centroid = it.next();
                Landmark landmark = getLandmarkWithName(centroid.getLandmarkNameForType(landmarkType));
                
                if(SupportFunctions.dist(
                        landmark.getMiddlePoint().getX(),
                        landmark.getMiddlePoint().getY(),
                        location.getX(),
                        location.getY())<landmarkType.getRadius()){
//                if(first){
//                    location.setCentroid(landmarkType,centroid);
//                    first = false;
//                }
                    closeLandmarks.add(landmark);       
                }
            }

            if(closeLandmarks.size() > 1){
                l = Landmark.mergeLandmarks(closeLandmarks);   
                removeLandmarks(closeLandmarks);
                l.addCentroid(c);
            }else if(closeLandmarks.size() == 1){
                Iterator<Landmark> itLand = closeLandmarks.iterator();                     
                l = itLand.next();
                l.addCentroid(c);
            }else{                    
                Set<Centroid> cs = new HashSet<Centroid>();
                cs.add(c);
                l = generateNewLandmark(landmarkType, cs);                
            }
                         
//            l.addPoint(location);
                        
        }else{
            counter2++;
            counter2b++;
            Set<Centroid> cs = new HashSet<Centroid>();
            cs.add(c);
            l = generateNewLandmark(landmarkType, cs);
            
        }
        addCentroid(c);
        return l;
    }
}
