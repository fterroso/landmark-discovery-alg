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
package lda.location;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lda.landmark.LandmarkType;

/**
 * Centroid of a cluster which is one of the parts of a landmkar.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Centroid extends Location{
    
    Map<LandmarkType, String> landmarkNames;
    
    public Centroid(double UTMNorth, double UTMEast) {
        super(UTMNorth,UTMEast);
        isCentroid = true;
        landmarkNames = new HashMap<LandmarkType, String>();
    }

    public String getLandmarkNameForType(LandmarkType type) {
        return landmarkNames.get(type);
    }

    public void setLandmarkNameForType(LandmarkType type, String landmarkNameForType) {
        this.landmarkNames.put(type, landmarkNameForType);
    }
    
    public static Centroid createFromLocation(Location position){
        Centroid c = new Centroid(position.y, position.x);
        c.setMeasure(position.getMeasure());
        c.setIndex(position.getIndex());
        return c;
    }
    
   
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
       
        sb.append("[");
        sb.append(y);
        sb.append(", ");
        sb.append(x);
        sb.append(", ");
        if(measures != null){
            sb.append(measures);
            sb.append(", ");
        }
        sb.append(Arrays.deepToString(landmarkNames.entrySet().toArray()));
        sb.append("]]");
                
        return sb.toString();
    }
        
}
