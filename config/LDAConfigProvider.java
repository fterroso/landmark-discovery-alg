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
package lda.config;

import lda.config.inputType.PositionInputType;
import java.util.List;
import lda.config.outputType.PrintOutputType;
import lda.landmark.LandmarkType;
import lda.landmark.level.LandmarkLevelProvider;
import lda.landmark.provider.LandmarkProviderFactory.LandmarkProviderType;

/**
 * 
 * Interface for the classes that provide the config data of the IvCA module.
 *
 * @author Fernando Terroso-Saenz
 */
public interface LDAConfigProvider {
    
    public String getUserID();
                               
    public String getPositionFilePath();
    
    public boolean shouldSerializePositions();
    
    public boolean shouldSerializeClusterPositions();
    
    public List<LandmarkType> getClusterTypes();
    
    public LandmarkType getClusterTypeWithLevel(int level);
        
    public String getOutputFilePath();
    
    public PrintOutputType getOutputType();
    
    public PositionInputType getPosInputType();
    
    public LandmarkProviderType getLandmarkProviderType();
    
    public LandmarkLevelProvider getLandmarkLevelProvider();
    
    public LDASpaceType getSpaceType();
    
    public void setUserID(String userID);
    
    public void setSpaceType(String spaceType);
    
}
