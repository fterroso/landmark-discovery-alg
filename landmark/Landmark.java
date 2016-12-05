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

import lda.config.outputType.PrintOutputType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import lda.location.Centroid;
import lda.location.Location;
import lda.tools.SupportFunctions;
import lda.tools.Templates;

/**
 * Class that represents a cluster (aka "place) of a itinerary.
 *
 * @author Fernando Terroso-Saenz
 */
public class Landmark implements Serializable, Comparable{
    
    
    static Logger LOG = Logger.getLogger(Landmark.class);
    
    private String name;
    private LandmarkType type;
    private Landmark father;
    private Set<Landmark> sons;
    private Set<Location> points;
    private Set<Centroid> centroids;  
    
    //This point represents the middle point of the centroids.
    private Location middlePoint;
    
    
    public Landmark(String name, LandmarkType type){
        this.name = name;
        this.type = type;
   
        sons = new HashSet<Landmark>();
        points = new HashSet<Location>();
        centroids = new HashSet<Centroid>();
        
        middlePoint = new Location(0,0);
    }
    
    public Landmark(String name, LandmarkType type, Landmark father){
        this.name = name;
        this.type = type;
        this.father = father;

        sons = new HashSet<Landmark>();
        points = new HashSet<Location>();
        centroids = new HashSet<Centroid>();
        
        middlePoint = new Location(0,0);
    }   
    
    public void addSon(Landmark son){       
        son.setFather(this);        
        sons.add(son);                
    }
    
    public void removeSon(Landmark son){
        sons.remove(son);
    }
    
    public void addPoint(Location point){        
        points.add(point);
    }    
       
    public void setCentroid(Centroid centroid){
        centroid.setLandmarkNameForType(type,name);
        centroids = new HashSet<Centroid>();
        centroids.add(centroid);
    }
    
    public void addCentroid(Centroid centroid){        
        centroid.setLandmarkNameForType(type,name);
        centroids.add(centroid);
        addToMiddlePoint(centroid);
       
    }
    
    public void addToMiddlePoint(Location l){
        double avgX = middlePoint.getX() + ((l.getX()-middlePoint.getX())/centroids.size());
        double avgY = middlePoint.getY() + ((l.getY()-middlePoint.getY())/centroids.size());
        
        middlePoint.setX(avgX);
        middlePoint.setY(avgY);        
    }
    
    public void addPoints(Set<Location> points){
        this.points.addAll(points);
    }
    
    public void addCentroids(Set<Centroid> centroids){  
        for(Centroid centroid : centroids){
            addCentroid(centroid);
        } 
    }
    
    public boolean containsPoint(Location point){
        return points.contains(point);
    }
    
    public boolean containsCentroid(Centroid centroid){
        return centroids.contains(centroid);
    }

    public void setFather(Landmark father) {
        
        this.father = father;
    }

    public Landmark getFather() {

        return father;
    }

    public String getName() {
        return name;
    }

    public Set<Location> getPoints() {
        return points;
    }

    public Set<Centroid> getCentroids() {
        return centroids;
    } 

    public Set<Landmark> getSons() {
        return sons;
    }

    public LandmarkType getType() {
        return type;
    }

    public Location getMiddlePoint() {
        return middlePoint;
    }


    public void setType(LandmarkType type) {
        this.type = type;
    }
    
    public boolean intersectWithLandmark(Landmark otherLandmark){
        Set<Location> otherPoints = otherLandmark.getPoints();
        
        for(Location otherPoint : otherPoints){
            if(containsPoint(otherPoint)){
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean canBeMergedWithLandmark(Landmark otherCluster){
        
        if(!type.equals(otherCluster.getType())){
            Landmark otherFather = otherCluster.getFather();
            if(otherFather != null && getFather() != null){
                if(!otherFather.equals(this) && 
                   !getFather().equals(otherCluster)){
                    return false;
                }
            }else if(otherFather != null){
                if(!otherFather.equals(this)){
                    return false;
                }
            }else if(getFather()!= null){
                if(!getFather().equals(otherCluster)){
                    return false;
                }
            }
        }else{
            Landmark otherFather = otherCluster.getFather();
            if(father != null && otherFather != null){
                if(!father.equals(otherFather)){
                    return false;
                }
            }else if((father == null) ? (otherFather != null) : (otherFather == null)){
                return false;
            }                        
        }                
        
        return true;
    }
    
    protected Landmark mergeWithLandmark(Landmark otherLandmark){
        Landmark result = null;
        if(canBeMergedWithLandmark(otherLandmark)){            
            if(getName().equals(otherLandmark.getName())){
                result = this;
            }else{
                if(otherLandmark.getType().equals(getType())){
                    String newName= name +"-" +otherLandmark.getName();

                    result = new Landmark(newName, type, father);
                    
                    for(Landmark son : getSons()){
                        result.addSon(son);
                    }
                                        
                    for(Landmark son : otherLandmark.getSons()){
                        result.addSon(son);
                    }
                                                                            
                    result.addPoints(points);
                    result.addPoints(otherLandmark.getPoints());  
                    result.addCentroids(centroids);
                    result.addCentroids(otherLandmark.getCentroids());                                        
                    
                    Landmark f = getFather();
                    if(f!=null){
                        f.removeSon(this);
                        f.addSon(result);
                    }
                    
                    f = otherLandmark.getFather();
                    if(f!=null){
                        f.removeSon(otherLandmark);
                        f.addSon(result);
                    }
                }else{

                    Landmark otherFather = otherLandmark.getFather();
                    
                    if(otherFather != null && getFather() != null){
                        if(otherFather.getName().equals(getName())){
                            result = this;
                        }else{
                            result = otherLandmark;
                        }
                    }else if(otherFather != null){
                        result = this;
                    }else if(getFather()!= null){
                        result = otherLandmark;
                    }                                        
                }
            }
        }
        
        return result;        
    }
    
    @Override
    public int hashCode(){
        
        int hash;
        if(name!= null && type != null){
            hash = name.hashCode();
            hash += type.hashCode();
        }else{
            hash = super.hashCode();
        }
    
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Landmark other = (Landmark) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }
        
    @Override
    public String toString(){
        return toPlainText();
    }
    
    public String generateOutput(PrintOutputType type){
        String output = "";
        
        switch(type){
            case PLAIN_TEXT:
                output = toPlainText();
                break;
            case KML:
                output = toKML();
                break;  
            case CIRCLED_KML:
                output = toCircleKML();
        }
        
        return output;
    }
    
    protected String toPlainText(){
        
        StringBuilder plainText = new StringBuilder();
        
        plainText.append("[");
        plainText.append(getName());
        plainText.append("; father:");
        String f = (getFather() != null) ? getFather().getName() : "NO";
        plainText.append(f);       
        
        plainText.append("; sons:");
        if(!getSons().isEmpty()){
            for(Landmark l : getSons()){
                plainText.append(l.getName());
                plainText.append("|");
            }
        }else{
            plainText.append("NO");
        }
        plainText.append("; num. locations:");
        plainText.append(getPoints().size());
        plainText.append("; num. centroids:");
        plainText.append(getCentroids().size());
        
        plainText.append("]");
        
//        plainText.append("\t[");       
////        for(Location point : getPoints()){            
////            plainText.append(point.getIndex());
////            plainText.append(",\t");
////        }        
//        plainText.append("]");
        
        return plainText.toString();        
    }
    
    protected String toKML(){        
        
        String template = Templates.KML_LANDMARK;        
        template = template.replace("LANDMARK_NAME", getName());
        template = template.replace("LANDMARK_TYPE", String.valueOf(getType().getLevel()));        
        template = template.replace("DESCRIPTION", toPlainText());        
        
        List aux = new LinkedList<Location>();
        aux.addAll(getPoints());
        
        if(aux.isEmpty()){
            aux.addAll(getCentroids());
        }
        
//        System.out.println(aux.size() + " "+getCentroids());
        
        List<Location> convexHullPoints = SupportFunctions.buildConvexHull(aux);
 
        StringBuilder coordinates = new StringBuilder();
        for(Location l : convexHullPoints){
            coordinates.append(l.toKML());
            coordinates.append("\n");
        }
        coordinates.append(convexHullPoints.get(0).toKML());
        coordinates.append("\n");
                        
        template = template.replace("COORD", coordinates);        
        return template;
    }
    
   
    /* Prints the landmark shape as a circle */ 
    protected String toCircleKML(){

        double centerLat = Math.toRadians(getMiddlePoint().getY());
        double centerLng = Math.toRadians(getMiddlePoint().getX());
        double diameter = this.getType().getRadius() / 1000; // diameter of circle in km
        double dist = diameter / 6371.0; 

        // start generating KML
        StringBuilder output =  new StringBuilder("<Placemark>\n");
        
        output.append("<name>"+getName()+"</name>\n");
        output.append("<styleUrl>#landmarkstyle"+String.valueOf(getType().getLevel())+"</styleUrl>\n");
        output.append("<description><![CDATA["+toPlainText()+"]]></description>\n");        
        output.append("<Polygon><outerBoundaryIs><LinearRing><coordinates>");        

        for (int x = 0; x <= 360; x ++)
        {
            double brng = Math.toRadians(x);
            double latitude = Math.asin(Math.sin(centerLat) * Math.cos(dist) + Math.cos(centerLat) * Math.sin(dist) * Math.cos(brng));
            double longitude = 
            centerLng + Math.atan2(Math.sin(brng) * Math.sin(dist)* Math.cos(centerLat), Math.cos(dist) - Math.sin(centerLat)
                * Math.sin(latitude)) ;
            output.append(Math.toDegrees(longitude)+","+ Math.toDegrees(latitude)+"\n");
        }
        output.append("</coordinates></LinearRing></outerBoundaryIs></Polygon>\n");
        output.append("</Placemark>\n");
        
        return output.toString();
        
    }
    
    public static Landmark mergeLandmarks(Set<Landmark> landmarks){
        
        Landmark mergeLandmark = null;
        Landmark[] landmarkArray = landmarks.toArray(new Landmark[landmarks.size()]);

        mergeLandmark = landmarkArray[0];
        for(int i = 1; i< landmarkArray.length; i++){
            if(!landmarkArray[i].canBeMergedWithLandmark(mergeLandmark)){
                return null;
            }
        }
        
        mergeLandmark = landmarkArray[0];
        for(int i = 1; i< landmarkArray.length; i++){
//            if(landmarkArray[i].canBeMergedWithLandmark(mergeLandmark)){
//                LOG.info("Landmarks " + landmarkArray[i].getName() + " can be merged with "+ mergeLandmark.name);
            mergeLandmark = mergeLandmark.mergeWithLandmark(landmarkArray[i]);
//            }else{
////                LOG.info("Landmarks " + landmarkArray[i].getName() + " can NOT be merged with "+ mergeLandmark.name);
//                mergeLandmark = null;
//                break;
//            }
        }
        
//        if(mergeLandmark != null){
//            LOG.info("New landmark " + mergeLandmark + " from landmarks: "+ Arrays.toString(landmarkArray));
//        }
        
        return mergeLandmark;                
    }

    @Override
    public int compareTo(Object t) {
        if(t == null){
            return 1;
        }
        
        if(t.getClass() != this.getClass()){
            return 0;
        }
        
        Landmark l = (Landmark) t;
        
        if(l.getType().getLevel() != getType().getLevel()){
            return new Integer(l.getType().getLevel()).compareTo(new Integer(getType().getLevel()));
        }
        
        return l.getName().compareTo(getName());
    }
}
