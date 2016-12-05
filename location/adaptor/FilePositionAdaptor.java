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

import lda.config.LDAConfigProvider;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;
import lda.location.Location;

/**
 *
 * @author Fernando Terroso Saenz
 */
public class FilePositionAdaptor extends PositionAdaptor{

    static Logger LOG = Logger.getLogger(FilePositionAdaptor.class);
    
    protected BufferedReader br;
    protected LDAConfigProvider config;
    
    public FilePositionAdaptor(LDAConfigProvider config){
        this.config = config;
        init();
    }
    
    protected void init(){
        restartFileReader();
    }
    
    protected void restartFileReader(){
        
        try{
            File f = new File(config.getPositionFilePath());
            FileInputStream fstream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
        }catch(Exception e){
            LOG.error("Error while inicializing position adaptor ", e);
        }
    }
    
    @Override
    public Location getCurrentPosition() {
        Location currentPosition = null;
        try{
            String positionString = br.readLine();
            
            if(positionString != null){
                currentPosition = parseString(positionString);
            }
        }catch(Exception e){
            LOG.error("Error en getCurrentPosition ", e);
        }finally{
            LOG.info("Posicion leida " + currentPosition);
            return currentPosition;
        }
    }

    @Override
    public void serializePositions() {
        /*Do nothing*/
    }

}
