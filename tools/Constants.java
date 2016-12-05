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

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Constants {
    //Header of the file to visilize traces and events on the GPS Viewer website.
    public static final String GPSVISUALIZER_WEB_HEAD_LINE = "name,desc,color,opacity,symbol,latitude,longitude\n";   
    
    public static final int NUM_LETTERS = 27;
    
    public static final float MIN_RADIUS = 50;
    
    public static final int MIN_POINTS = 5;
    
    public static final double CLOSENESS_FACTOR = 0.25;

}
