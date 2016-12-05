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

package lda.location.adaptor;

import java.util.StringTokenizer;
import lda.location.Location;

/**
 * Class that provides the current position of the simulated vehicle.
 *
 * @author Fernando Terroso-Saenz
 */
public abstract class PositionAdaptor {

    protected Location parseString(String posStr){
        
        int indexFirst = posStr.indexOf("[");
        String aux = posStr.substring(indexFirst+1, posStr.length()-1);
        
        StringTokenizer st = new StringTokenizer(aux, ",");
        float y = Float.valueOf(st.nextToken());
        float x = Float.valueOf(st.nextToken());
        
//        /*Convert from LAT-LONG to UTM coordenates */
//        LatLng latPoint = new LatLng(north, east);        
//        UTMRef utmPoint = latPoint.toUTMRef();
        
        return new Location(y, x);//, utmPoint.getLngZone());
    }
    
    public abstract Location getCurrentPosition();
    
    public abstract void serializePositions();

}
