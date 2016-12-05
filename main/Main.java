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
package lda.main;

import lda.config.LDAConfigProviderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import lda.landmark.provider.LandmarkProviderFactory;
import org.apache.log4j.Logger;
import lda.location.deliver.PositionDeliverer;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Main {
    
    static Logger LOG = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        try{
            String hostOperatingSystem = System.getProperty("os.name");     
            String configPath = getConfigPath(hostOperatingSystem, args[0]);
            LDAConfigProviderFactory.createConfigProvider(configPath);

            PositionDeliverer.deliverPositions();
            
            printLandmarks();
        }catch(Exception e){
            LOG.error("Error in main method ", e);
        }
    }    
    
    public static String getConfigPath(String oS, String sufixPath) throws Exception{
        
       oS = oS.toLowerCase();
       oS = oS.replace(" ", "_");
       
       Properties p = new Properties();
       
       p.load(new FileInputStream("operating_system.properties"));
       String prefixPath = p.getProperty(oS+".config.path");
       
       if(!prefixPath.endsWith(File.separator)){
           prefixPath = prefixPath.concat(File.separator);
       }
       
       return prefixPath + sufixPath;
        
    }
    
    public static void printLandmarks(){

        try{
            for(String user : LandmarkProviderFactory.getUsersWithLandmarks()){
                LandmarkProviderFactory.getCurrentLandmarkProvider(user).printLandmarks();
            }
            LOG.info("The landmarks have been serialized");
        }catch(Exception e){
            LOG.error("Error while printing landmarks",e);
        }
    }
    
}
