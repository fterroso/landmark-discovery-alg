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

import java.io.Serializable;

/**
 * Type of cluster the module must find.
 *
 * @author Fernando Terroso-Saenz
 */
public class LandmarkType implements Serializable, Comparable{
    
    int level;
    float radius;
    int minPoints;

    public LandmarkType(int level, float radius, int minPoints) {
        this.level = level;
        this.radius = radius;
        this.minPoints = minPoints;
    }       

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    } 
    
    @Override
    public int hashCode(){
        int hash = level;
        hash += minPoints;
        hash += Float.floatToIntBits(radius);
    
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
        final LandmarkType other = (LandmarkType) obj;
        if (level != other.level) {
            return false;
        }
        if (Float.floatToIntBits(radius) != Float.floatToIntBits(other.radius)) {
            return false;
        }
        if (minPoints != other.minPoints) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[L:");
        sb.append(level);
        sb.append(", R:");
        sb.append(radius);
        sb.append(", MP:");
        sb.append(minPoints);
        sb.append("]");
        
        return sb.toString();
    }

    @Override
    public int compareTo(Object o) {
        LandmarkType type = (LandmarkType) o;
        
        Integer i = new Integer(type.getLevel());
        Integer j = new Integer(getLevel());
        
        return i.compareTo(j);

    }
}
