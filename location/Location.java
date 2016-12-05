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
import lda.config.outputType.PrintOutputType;
import static lda.config.outputType.PrintOutputType.KML;
import static lda.config.outputType.PrintOutputType.PLAIN_TEXT;
import lda.place.Place;
import lda.place.PlaceType;
import org.apache.log4j.Logger;
import lda.landmark.LandmarkProxy;
import lda.landmark.LandmarkType;

/**
 *
 * @author Fernando Terroso-Saenz
 */
public class Location extends Place {

    static Logger LOG = Logger.getLogger(Location.class);
    String ID;
    long timestamp;
    double y;
    double x;
    double hgt;
    public double[] data;
    public int dimension = 2;
    Map<LandmarkType, Centroid> centroid = new HashMap<LandmarkType, Centroid>();
    boolean isCentroid;
    double[] measures;
    int index;

    public Location() {
        data = new double[2];
    }

    public Location(double y, double x) {
        this.y = y;
        this.x = x;
        isCentroid = false;
        data = new double[]{x, y};
        ID = "Loc_" + y + "_" + x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        data[1] = this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        data[0] = this.x = x;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public PlaceType getType() {
        return PlaceType.POINT;
    }

    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isCentroid() {
        return isCentroid;
    }

    public void setIsCentroid(boolean isCentroid) {
        this.isCentroid = isCentroid;
    }

    public Centroid getCentroid(LandmarkType type) {
        return centroid.get(type);
    }
    
    public boolean hasCentroid(LandmarkType type){
        return this.centroid.containsKey(type);
    }

    public void setCentroid(LandmarkType type, Centroid centroid) {
        this.centroid.put(type, centroid);
    }

    public double[] getMeasure() {
        return measures;
    }

    public void setMeasure(double[] measure) {
        this.measures = measure;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return toPlainText();
    }

    public String generateOutput(PrintOutputType type) {
        String output = "";

        switch (type) {
            case PLAIN_TEXT:
                output = toPlainText();
                break;
            case KML:
                output = toKML();
                break;
        }

        return output;
    }

    public String toPlainText() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        sb.append(y);
        sb.append(", ");
        sb.append(x);
        if (measures != null) {
            sb.append(", ");
            sb.append(Arrays.toString(measures));
        }

        sb.append("]]");

        return sb.toString();
    }

    public String toKML() {

        return getX() + "," + getY() + ",0";
    }

    @Override
    public boolean canBeMerged(Object o) {

        if (!(o instanceof Place)) {
            return false;
        }

        Place place = (Place) o;

        if (place.getType().equals(PlaceType.POINT)) {
            if (!this.equals(place)) {
                return false;
            }
        } else if (place.getType().equals(PlaceType.LANDMARK)) {
            LandmarkProxy cluster = (LandmarkProxy) place;
            if (!cluster.contains(this)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Place merge(Object o) {
        if (canBeMerged(o)) {
            Place place = (Place) o;
            if (place.getType().equals(PlaceType.POINT)) {
                return this;
            } else if (place.getType().equals(PlaceType.LANDMARK)) {
                return place;
            }
        }

        return null;
    }

    @Override
    public int hashCode() {

        long a = Double.doubleToLongBits(y);
        int hash = (int) (a ^ (a >>> 32));

        a = Double.doubleToLongBits(x);
        hash += (int) (a ^ (a >>> 32));

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
        final Location other = (Location) obj;

        if (other.getMeasure() != measures) {
            return false;
        }

        if ((other.y != y) || (other.x != x)) {
            return false;
        }

        return true;
    }

    public static Location createFromString(String s) {
        int i = s.indexOf("[");
        int j = s.indexOf(",");

        double n = Double.valueOf(s.substring(i + 1, j));

        i = s.lastIndexOf(",");
        j = s.indexOf("]");

        double e = Double.valueOf(s.substring(i + 2, j));

        return new Location(n, e);
    }
}
