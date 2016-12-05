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
import org.apache.log4j.Logger;

/**
 *
 * @author Fernando Terroso-Saenz
 */
public class PositionAdaptorFactory {
    
    static Logger LOG = Logger.getLogger(PositionAdaptorFactory.class);
    
    public static PositionAdaptor getPositionProvider(LDAConfigProvider config) throws Exception{        

        PositionAdaptor posProvider = null;
        switch(config.getPosInputType()){
            case FILE:
                LOG.info("Position adaptor with threashold is going to be used.");
                posProvider = new FileUTMPositionAdaptor(config);
                break;
            default:
                throw new Exception("Position adaptor type not supported: "+config.getPosInputType());
        }
        
        return posProvider;        
    }
}
