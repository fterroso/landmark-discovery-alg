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
package lda.location.provider;

import java.util.HashSet;
import java.util.Set;
import lda.landmark.LandmarkType;
import org.apache.log4j.Logger;
import lda.location.Centroid;
import lda.location.Location;
import lda.rtree.RTree;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class BasicPositionProvider implements PositionProvider{
   
    static Logger LOG = Logger.getLogger(PositionProvider.class);

    private static final int BLOCKLENGTH = 256;
    private static final int CACHESIZE = 128; 
    private static final int DIMENSION = 2;    
    private static final float MIN_DIST = 20;  
    
    Set<Location> normalPositions;
    Set<Centroid> centroids;

    RTree positionsRTree;
    
    public BasicPositionProvider() {
        normalPositions = new HashSet<Location>();
        centroids = new HashSet<Centroid>();
        positionsRTree = new RTree("pos", BLOCKLENGTH, CACHESIZE, DIMENSION, MIN_DIST);
    }    
    
    @Override
    public void addPosition(Location position){
        if(!normalPositions.contains(position)){
            positionsRTree.insertPoint(position);
        }
        normalPositions.add(position);
        
    }
    
    @Override
    public void addCentroid(Centroid centroid){
        if(!centroids.contains(centroid)){
            positionsRTree.insertPoint(centroid);
        }
        centroids.add(centroid);        
    }
    
    @Override
    public Set<Location> getPointsInRange(Location position, float range) {
        return positionsRTree.getPointsInRange(position, range);
    }

    @Override
    public Set<Centroid> getCentroidsFromPointsAndType(Set<Location> points, LandmarkType type) {
        Set<Centroid> result = new HashSet<Centroid>();
        
        for(Location point : points){
            if(point.isCentroid()){
                Centroid centroid = (Centroid) point;
                if(centroid.getLandmarkNameForType(type) != null) {
                    result.add(centroid);
                }
            }
        }        
        return result;    
    }         
    
}
