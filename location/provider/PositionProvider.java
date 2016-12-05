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

import java.io.Serializable;
import java.util.Set;
import lda.landmark.LandmarkType;
import lda.location.Centroid;
import lda.location.Location;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public interface PositionProvider extends Serializable{
    
    public void addPosition(Location position);
    
    public void addCentroid(Centroid centroid);
        
    public Set<Location> getPointsInRange(Location position, float range);
    
    public Set<Centroid> getCentroidsFromPointsAndType(Set<Location> points, LandmarkType type);
    
}
