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
import lda.tools.SupportFunctions;
import org.apache.log4j.Logger;

/**
 * Landmark provider based on the KNN algorithm.
 *
 * @author fterroso
 */
public class KNNLandmarkProvider extends LandmarkProvider{

    static Logger LOG = Logger.getLogger(KNNLandmarkProvider.class);
    
    public KNNLandmarkProvider(LDAConfigProvider config) {
        super(config);
    }

    @Override
    protected Landmark inferLandmarkForPointAndType(Location location, LandmarkType landmarkType, boolean closeness, String... landmarkPrefixName) {
        
        Landmark r = null;
        
        Set<Location> closeLocations  = positionProvider.getPointsInRange(location, landmarkType.getRadius());
        Set<Centroid> centroids = positionProvider.getCentroidsFromPointsAndType(closeLocations, landmarkType);
        
        if(!centroids.isEmpty()){
            
            Set<Landmark> closeLandmarks = new HashSet<Landmark>();
            
            Iterator<Centroid> it = centroids.iterator();
            while(it.hasNext()){
                Centroid centroid = it.next();
                Landmark landmark = getLandmarkWithName(centroid.getLandmarkNameForType(landmarkType));
                if(landmark == null)
                    LOG.info("Landmark merge nulo: "+centroid.getLandmarkNameForType(landmarkType));
                closeLandmarks.add(landmark);                                           
            }
            
            r = mergeClosestLandmarks(closeLandmarks, landmarkType);
            
            if(r==null){
                double minDist = Double.MAX_VALUE;
                for(Landmark l : closeLandmarks){
                    
                    try{
                        double dist = SupportFunctions.dist(l.getMiddlePoint().getX(), l.getMiddlePoint().getY(), location.getX(),location.getY());
                        if(dist < minDist){
                            r = l;
                            minDist = dist;
                        }
                    }catch(Exception e){
                        System.out.println(l +","+location);
                        System.out.println(l.getMiddlePoint()+","+location.getX()+","+location.getY());
                    }
                }
                if(minDist < landmarkType.getRadius()){
                    r.addToMiddlePoint(location);                    
                }else{
                    r = null;
                }
            }
            
        }else {            
            Set<Location> closeLocationsNoAssigned = new HashSet<Location>();
            for(Location loc : closeLocations){
                if(!loc.hasCentroid(landmarkType)){
                    closeLocationsNoAssigned.add(loc);
                }                    
            }
            
            if(closeLocationsNoAssigned.size() >= landmarkType.getMinPoints()-1){
                
                Centroid newCentroid = Centroid.createFromLocation(location);
                                
                for(Location closeLocation : closeLocationsNoAssigned){
                    closeLocation.setCentroid(landmarkType,newCentroid);
                }

                Set<Centroid> centroidsForNewLandmark = new HashSet<Centroid>();
                centroidsForNewLandmark.add(newCentroid);
                if(landmarkPrefixName.length > 0)
                    r = generateNewLandmark(landmarkType, centroidsForNewLandmark, closeLocationsNoAssigned,landmarkPrefixName[0]);
                else
                    r = generateNewLandmark(landmarkType, centroidsForNewLandmark, closeLocationsNoAssigned);
                
                for(Location loc : closeLocationsNoAssigned){
                    r.addToMiddlePoint(loc);
                }
                addCentroid(newCentroid);
            }                  
        }
        
        if(r== null){
            addPosition(location);
        }

        return r;
    }
    
    protected Landmark mergeClosestLandmarks(Set<Landmark> landmarks, LandmarkType landmarkType){
    
        Landmark result = null;
        Set<Landmark> landmarksToMerge = new HashSet<Landmark>();
           
        List <Landmark> lList = new ArrayList<Landmark>();
        lList.addAll(landmarks);
        Landmark l1 = lList.get(0);
        for(int i= 1; i< lList.size(); i++){            
            Landmark l2 = lList.get(i);
            
            if(!landmarksToMerge.isEmpty()){
                boolean insert = true;
                for(Landmark laux : landmarksToMerge){
                    if(SupportFunctions.dist(l2.getMiddlePoint().getX(), l2.getMiddlePoint().getY(), laux.getMiddlePoint().getX(), laux.getMiddlePoint().getY()) >= landmarkType.getRadius()){
                        insert = false;
                        break;
                    }
                }
                if(insert){
                    landmarksToMerge.add(l2);
                }
            }else{
                if(SupportFunctions.dist(l1.getMiddlePoint().getX(), l1.getMiddlePoint().getY(), l2.getMiddlePoint().getX(), l2.getMiddlePoint().getY()) < landmarkType.getRadius()){
                    landmarksToMerge.add(l1);
                    landmarksToMerge.add(l2);                        
                 }
            }                        
        }
        
        if(!landmarksToMerge.isEmpty()){
            result = Landmark.mergeLandmarks(landmarksToMerge);
            if(result!=null)
                removeLandmarks(landmarksToMerge);
        }
        
        return result;        
    }
    
}
