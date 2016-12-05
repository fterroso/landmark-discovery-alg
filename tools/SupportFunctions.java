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
package lda.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import lda.location.Location;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class SupportFunctions {
    
    public static float dist(Location l1, Location l2){
        return dist(l1.getX(), l1.getY(), l2.getX(), l2.getY());
    }
    
    public static float dist(double x1, double y1, double x2, double y2){

        return (float) Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
    }
    
    // builds a convex hull around the given points
    // using the Graham scan algorithm
    public static List<Location> buildConvexHull(List<Location> points) {
            if (points.size() < 3){
                Stack<Location> hull = new Stack<Location>();
                for(Location l : points){
                    hull.add(l);
                }
                return hull;
                    
            }

            // Find the point with the least y, then x coordinate
            Location p0 = null;
            for (int i = 0; i < points.size(); ++i) {
                    Location curr = points.get(i);
                    if (p0 == null || curr.getY() < p0.getY() || (curr.getY() == p0.getY() && curr.getX() < p0.getX()))
                            p0 = curr;
            }

            // Sort the points by angle around p0
            class PointAngleComparator implements Comparator<Location> {
                    private Location p0;

                    public PointAngleComparator(Location p0) {
                            this.p0 = p0;
                    }

                    private float angle(Location pt) {
                            return (float)Math.atan2(pt.getY() - p0.getY(), pt.getX() - p0.getX());
                    }

                    @Override
                    public int compare(Location p1, Location p2) {
                            float a1 = angle(p1), a2 = angle(p2);
                            if (a1 > a2)
                                    return 1;
                            if (a1 < a2)
                                    return -1;
                            return Float.compare(dist(p0.getX(), p0.getY(), p1.getX(), p1.getY()),
                                            dist(p0.getX(), p0.getY(), p2.getX(), p2.getY()));
                    }
            }
            Collections.sort(points, new PointAngleComparator(p0));

            // build the hull
            Stack<Location> hull = new Stack<Location>();
            hull.push(points.get(0));
            hull.push(points.get(1));
            hull.add(points.get(2));
            for (int i = 3; i < points.size(); ++i) {
                    Location cur = points.get(i);
                    while (hull.size() >= 3) {
                            Location snd = hull.get(hull.size() - 2);
                            Location top = hull.peek();
                            double crossproduct = (top.getX() - snd.getX()) * (cur.getY() - snd.getY()) - (cur.getX() - snd.getX())
                                            * (top.getY() - snd.getY());
                            if (crossproduct > 0)
                                    break;
                            hull.pop();
                    }
                    hull.push(cur);
            }

            return hull;
    }
    
}
