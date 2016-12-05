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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lda.config.LDAConfigProvider;
import lda.config.outputType.PrintOutputType;
import lda.landmark.Landmark;
import lda.landmark.LandmarkName;
import lda.location.Location;
import lda.landmark.LandmarkProxy;
import lda.landmark.LandmarkType;
import static lda.landmark.provider.DensityLandmarkProvider.LOG;
import lda.location.Centroid;
import lda.location.provider.BasicPositionProvider;
import lda.location.provider.PositionProvider;
import lda.tools.Color;
import lda.tools.SupportFunctions;
import lda.tools.Templates;

/**
 *
 * @author fernando
 */
public abstract class LandmarkProvider implements Serializable{

    protected static final String LANDMARK_PATH = "landmarks.data";
    protected static final String POS_PATH = "positions_provider.data";
    protected static final String LANDMARK_NAME_PATH = "landmark_names.data";
    
    protected LDAConfigProvider config;
    protected LandmarkName nameGenerator;

    protected PositionProvider positionProvider;

    protected String userID = null;

    protected Map<LandmarkType, Set<Landmark>> landmarks;

    protected LandmarkProviderStats stats = null;
    double counter1, counter2, counter2a, counter2b, counter3;    
        
    
    public LandmarkProvider(LDAConfigProvider config){
        try{
            this.config = config;
            init();
        }catch(Exception e){
            LOG.error("Error getting data from serialization ", e);
        }
    }
    
    protected void init(){
        
        FileInputStream fis = null;
        ObjectInputStream in = null;
        
        String prefix;
        
        if(userID == null){
            prefix = config.getUserID();
        }else{
            prefix = userID;
        }
        
        try {
            fis = new FileInputStream(prefix+"_"+LANDMARK_PATH);
            in = new ObjectInputStream(fis);
            landmarks = (HashMap<LandmarkType, Set<Landmark>>) in.readObject();
            in.close(); 
            LOG.info("Landmarks recovered from file.");
        } catch (Exception ex) {
//            LOG.error("Error while getting landmarks. A new one will be generated ", ex);
            landmarks = new HashMap<LandmarkType, Set<Landmark>>();                              

        }              
        
        try {
            fis = new FileInputStream(prefix+"_"+POS_PATH);
            in = new ObjectInputStream(fis);
            positionProvider = (BasicPositionProvider) in.readObject();
            in.close();   
            LOG.info("Position provider recovered from file.");
        } catch (Exception ex) {
//            LOG.error("Error while getting position provider. A new one will be generated ", ex);
            positionProvider = new BasicPositionProvider();
        }        
        
        try {                       
            fis = new FileInputStream(prefix+"_"+LANDMARK_NAME_PATH);
            in = new ObjectInputStream(fis);
            nameGenerator = (LandmarkName) in.readObject();
            in.close();  
            LOG.info("Landmark's names recovered from file.");
        } catch (Exception ex) {
//            LOG.error("Error while getting landmark's names. A new one will be generated ", ex);
            nameGenerator = new LandmarkName();              
        }        
    }
    
    public LandmarkProxy inferLandmarkForPoint(Location position, boolean closeness) {

        List<LandmarkType> landmarkTypes = config.getClusterTypes();
        
        return inferLandmarkForPoint(position, closeness, landmarkTypes);
    }
    
    public int getTotalNumberOfLandmarks(){
        int totalNum = 0;
        for(LandmarkType t : landmarks.keySet()){
            totalNum += landmarks.get(t).size();
        }
        
        return totalNum;
    }
    
    
    public LandmarkProxy inferLandmarkForPoint(
            Location position, 
            boolean closeness,
            List<LandmarkType> landmarkTypes,
            String... landmarkNamePrefix) {
       
        Landmark finalLandmark = null;                
        
        Map<LandmarkType, Landmark> currentLandmarks = new HashMap<LandmarkType, Landmark>();                
                
        for(LandmarkType type : landmarkTypes){ 
            Landmark landmark;
            
            if(landmarkNamePrefix.length > 0)                   
                landmark = inferLandmarkForPointAndType(position, type, closeness,landmarkNamePrefix[0]);
            else
                landmark = inferLandmarkForPointAndType(position, type, closeness);
            
            if(landmark != null){
                currentLandmarks.put(type, landmark);
            }            
        }
              
        if(!currentLandmarks.isEmpty()){
            Set<LandmarkType> types = currentLandmarks.keySet();

            List<LandmarkType> typesList = new ArrayList<LandmarkType>(types);        
            Collections.sort(typesList);        
            
            Landmark currentLandmark = currentLandmarks.get(typesList.get(0));
            insertLandmarkOfType(typesList.get(0), currentLandmark);
                        
            for(int i= 1; i< typesList.size(); i++){
                Landmark prev = currentLandmark;
                currentLandmark = currentLandmarks.get(typesList.get(i));
                if(currentLandmark.getFather() != null){
                    currentLandmark.getFather().removeSon(currentLandmark);
                }
                currentLandmark.setFather(prev);
                prev.addSon(currentLandmark);
                insertLandmarkOfType(typesList.get(i), currentLandmark);
            }
            finalLandmark = currentLandmark;
        }
        
        LandmarkProxy proxy = null;
        if(finalLandmark != null){
            proxy = new LandmarkProxy(finalLandmark.getName(), this);
        }
                
        return proxy;
    }
    
    public void setUserID(String userID){
        this.userID = userID;
    }


    public String getUserID(){
        return userID;
    }
    
    public void removeLandmark(Landmark cluster) {
        if(cluster != null){
            LandmarkType type = cluster.getType();
            Set<Landmark> clusters = landmarks.get(type);
            if(clusters == null){
                clusters = new HashSet<Landmark>();                        
            }else{
                clusters.remove(cluster);
//                LOG.info("Cluster " + cluster.getName() +" has been removed");
            }
            
            landmarks.put(type, clusters);
        }else{
            LOG.error("Watch out! You have tried to remove a null cluster");
        }
    }

    public void removeLandmarks(Set<Landmark> landmarks){
        for(Landmark landmark : landmarks){
            removeLandmark(landmark);
        }
    }
        
    public Landmark getLandmarkWithName(String landmarkName) {
        
        Landmark alternativeLandmark = null;
        if(landmarkName != null){
            Set<LandmarkType> types = landmarks.keySet();

            for(LandmarkType type : types){
                Set<Landmark> landmarksOfType = landmarks.get(type);
                for(Landmark landmark : landmarksOfType){
                    
                    if(landmark.getName().equals(landmarkName)){
                        return landmark;
                    }
                    
                    if(landmark.getName().contains(landmarkName)){
                        alternativeLandmark= landmark;
                    }
                }
            }
        }
        return alternativeLandmark;
    }  
    
    public void addPosition(Location position) {
        positionProvider.addPosition(position);        
    }
    
    public void addCentroid(Centroid centroid) {
        positionProvider.addCentroid(centroid);        
    }
   
    public Set<Landmark> getLandmarksOfType(LandmarkType type) {
        return landmarks.get(type);
    }

    public LandmarkProxy insertLandmarkOfType(LandmarkType type, Landmark landmark) {
        Set<Landmark> clusters = new HashSet<Landmark>();

        boolean isNew = true;
        Set<Landmark> landmarksOfType = landmarks.get(type);
        if(landmarksOfType != null){
            Iterator<Landmark> clustersIt = landmarksOfType.iterator();
            while(clustersIt.hasNext()){
                Landmark currentLandmark = clustersIt.next();
                String ID = currentLandmark.getName();
                if(!ID.equals(landmark.getName())){
                    clusters.add(currentLandmark);        
                }else{
                    isNew = false;
                }
            }
        }
        
//        if(isNew){
//            LOG.debug(landmark+" generated");
//        }
        
        clusters.add(landmark);  

        landmarks.remove(type);
        landmarks.put(type, clusters);
        
        LandmarkProxy proxy = new LandmarkProxy(landmark.getName(), this);
                
        return proxy;
                
    }
    
    public Landmark getLandmarkWithCentroid(Centroid centroid, LandmarkType type){

        Set<Landmark> landmarksOfType = this.landmarks.get(type);

        if((landmarksOfType != null) && !landmarksOfType.isEmpty()){

            Iterator<Landmark> itLandmarks = landmarksOfType.iterator();
            while(itLandmarks.hasNext()){
                Landmark landmark = itLandmarks.next();
                if(landmark.containsCentroid(centroid)){
                    return landmark;
                }
            }
        }

        return null;               
    }
    
    public Landmark getLandmarkWithPoint(Location point, LandmarkType type){

        Set<Landmark> landmarksOfType = this.landmarks.get(type);

        if((landmarksOfType != null) && !landmarksOfType.isEmpty()){

            Iterator<Landmark> itLandmarks = landmarksOfType.iterator();
            while(itLandmarks.hasNext()){
                Landmark landmark = itLandmarks.next();
                if(landmark.containsPoint(point)){
                    return landmark;
                }
            }
        }

        return null;               
    } 
    
    public void serializeLandmarks(){
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        
        String prefix;        
        if(userID == null){
            prefix = config.getUserID();
        }else{
            prefix = userID;
        }
        
        try {
            fos = new FileOutputStream(prefix+"_"+LANDMARK_PATH);
            out = new ObjectOutputStream(fos);
            out.writeObject(landmarks);
            out.close();
            
            fos = new FileOutputStream(prefix+"_"+POS_PATH);
            out = new ObjectOutputStream(fos);
            out.writeObject(positionProvider);
            out.close();            
                
            fos = new FileOutputStream(prefix+"_"+LANDMARK_NAME_PATH);
            out = new ObjectOutputStream(fos);
            out.writeObject(nameGenerator);
            out.close();
                
            LOG.info("Landmarks for "+prefix+" persisted.");
        } catch (Exception ex) {
                LOG.error("Exception ", ex);
        }
    }
    
    protected String getGeneralHeaderForOutputType(PrintOutputType type, String id){
        String header = "";
        
        switch(type){
            case PLAIN_TEXT:
                header = Templates.GPSVISUALIZER_WEB_HEAD_LINE;
                break;
            case KML:
            case CIRCLED_KML:                
                header = Templates.KML_GENERAL_HEAD_LINE;
                header = header.replace("ELEMENT_NAME", id);                        
               
                int i = 0;
                for(LandmarkType landmarkType : config.getClusterTypes()){
                    String aux = Templates.KML_LANDMARK_STYLE;
                    aux = aux.replace("LANDMARK_TYPE", String.valueOf(landmarkType.getLevel()));
                    aux = aux.replace("COLOR_CODE", Color.values()[i++].getHexCode());
                    
                    header += aux;
                }
                
                break;
        }
                
        return header;
    }
    
    protected String getSpecificHeaderForOutputType(
            PrintOutputType typeOfOutput, 
            int level){
        String header = "";
        
        switch(typeOfOutput){
            case KML:
            case CIRCLED_KML:
                header = Templates.KML_SPECIFIC_HEAD_LINE;
                String elementName = "LEVEL_" + level;
                header = header.replace("ELEMENT_NAME", elementName);
                break;                

        }
        
        return header;
    }
    
    protected String getGeneralTailForOutputType(
            PrintOutputType typeOfOutput){
        
        String header = "";
        
        switch(typeOfOutput){
            case KML:
            case CIRCLED_KML:
                header = Templates.KML_GENERAL_TAIL_LINE;
                break;
        }
        
        return header;
    }
    
    protected String getSpecificTailForOutputType(
            PrintOutputType typeOfOutput){
        
        String header = "";
        
        switch(typeOfOutput){
            case KML:
            case CIRCLED_KML:                
                header = Templates.KML_SPECIFIC_TAIL_LINE;
        }
        return header;
    }
    
    public void printLandmarks(LandmarkType... types){                       
        
        StringBuilder path = new StringBuilder();
        
        path.append(config.getOutputFilePath());
        
        if(userID != null){
            path.append(userID);
        }else{
            path.append(config.getUserID());
        }
        
        path.append("_landmarks.");
        path.append(config.getOutputType().getFileExtension());
                       
        try{
            PrintWriter printer = new PrintWriter(path.toString());        
            
            if(userID != null){
                printer.print(getGeneralHeaderForOutputType(config.getOutputType(),userID));
            }else{
                printer.print(getGeneralHeaderForOutputType(config.getOutputType(),config.getUserID()));
            }
            
            List<LandmarkType> landmarkTypes;
            if(types.length > 0){
                landmarkTypes = new ArrayList<LandmarkType>(Arrays.asList(types));
            }else{
                landmarkTypes = config.getClusterTypes();
            }

            for(LandmarkType type : landmarkTypes){ 
                Set<Landmark> landmarkSet = getLandmarksOfType(type);
                
                if(landmarkSet != null && !landmarkSet.isEmpty()){
                    printer.print(getSpecificHeaderForOutputType(config.getOutputType(), type.getLevel()));
                    
                    List<Landmark> sortedLandmarks = new ArrayList(landmarkSet);
                    Collections.sort(sortedLandmarks);
                    
                    for(Landmark landmark : sortedLandmarks){
                        printer.println(landmark.generateOutput(config.getOutputType()));
                    }
                    printer.print(getSpecificTailForOutputType(config.getOutputType()));
                }
            }
            
            printer.print(getGeneralTailForOutputType(config.getOutputType()));
            printer.close();
                                                
        }catch(Exception e){
             LOG.error("Error while printing landmarks ", e);
         }                       
    }
    
    protected Landmark generateNewLandmark(
            LandmarkType type, 
            Set<Centroid> centroids){
        
        String name = nameGenerator.generateNewNameForType(type);
        Landmark newLandmark = new Landmark(name, type);
        
        newLandmark.addCentroids(centroids);  
                
        return newLandmark;        
    }
    
    protected Landmark generateNewLandmark(
            LandmarkType type, 
            Set<Centroid> centroids,
            Set<Location> points,
            String... prefix){
        
        String name;
        if(prefix.length> 0)
            name = nameGenerator.generateNewNameForType(type,prefix[0]);
        else
            name = nameGenerator.generateNewNameForType(type);
            
        Landmark newLandmark = new Landmark(name, type);
        
        newLandmark.addPoints(points);
        newLandmark.addCentroids(centroids);  
                
        return newLandmark;        
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        Set<LandmarkType> types = landmarks.keySet();
        
        for(LandmarkType type : types){
            sb.append(type);
            sb.append("\n---------------------\n");
            
            for(Landmark landmark : landmarks.get(type)){
                sb.append(landmark);
                sb.append("\n---------------------\n");
            }
            sb.append("---------------------\n\n");
        }
                                        
        return sb.toString();
    }
    
    public LandmarkProviderStats getStats(){
        
        if(stats == null){
            stats = new LandmarkProviderStats();
            
            double total = counter1 + counter2+ counter3;
            stats.part1Perc = counter1/total;
            stats.part2Perc = counter2/total;
            stats.part3Perc = counter3/total;
            
            stats.numLandmarks = new HashMap<LandmarkType,Integer>();
            stats.avgDistAmongLandmarks = new HashMap<LandmarkType,Double>();
            for(LandmarkType type : landmarks.keySet()){
                Set<Landmark> targetLandmarks = landmarks.get(type);
                stats.numLandmarks.put(type, targetLandmarks.size());
                double totalAvgDist = 0;
                for(Landmark l1 : targetLandmarks){
                    double avgDist = 0;
                    Location loc1 = l1.getMiddlePoint();
                    for(Landmark l2 : targetLandmarks){
                        if(!l1.equals(l2)){
                            Location loc2 = l2.getMiddlePoint();
                            double dist = SupportFunctions.dist(
                                    loc1.getX(), 
                                    loc1.getY(), 
                                    loc2.getX(), 
                                    loc2.getY());
                            
                            avgDist += dist;
                        }
                    }
                    avgDist /= (targetLandmarks.size()-1);                    
                    totalAvgDist += avgDist;
                }
                stats.avgDistAmongLandmarks.put(type, totalAvgDist);
            }

        }
        
        return stats;
    }

    protected abstract Landmark inferLandmarkForPointAndType(Location location, LandmarkType landmarkType, boolean closeness,
            String... landmarkPrefixName);
                         
    public class LandmarkProviderStats{
        
        double part1Perc;
        double part2Perc;
        double part3Perc;
        
        Map<LandmarkType,Integer> numLandmarks;
        Map<LandmarkType,Double> avgDistAmongLandmarks;
        


        public double getPart1Perc() {
            return part1Perc;
        }

        public double getPart2Perc() {
            return part2Perc;
        }

        public double getPart3Perc() {
            return part3Perc;
        }
        
        public int getNumLandmarksForLevel(int level){
            for(LandmarkType type : numLandmarks.keySet()){
                if(type.getLevel() == level){
                    return numLandmarks.get(type);
                }
            }
            return Integer.MIN_VALUE;
        }
        
        public double getAvgDistForLevel(int level){
            for(LandmarkType type : numLandmarks.keySet()){
                if(type.getLevel() == level){
                    return avgDistAmongLandmarks.get(type);
                }
            }
            return Double.NEGATIVE_INFINITY;
        }
        
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(part1Perc);
            sb.append("\n");
            sb.append(part2Perc);
            sb.append("\n");
            sb.append(part3Perc);
            sb.append("\n");
            
            for(LandmarkType type : numLandmarks.keySet()){
                sb.append(type.getLevel());
                sb.append(":");
                sb.append(numLandmarks.get(type));
                sb.append("\n");
            }
            
            for(LandmarkType type : avgDistAmongLandmarks.keySet()){
                sb.append(type.getLevel());
                sb.append(":");
                sb.append(String.format(Locale.US, "%.2f", avgDistAmongLandmarks.get(type)));
                sb.append("\n");
            }
            
            return sb.toString();

        }
    }
}
