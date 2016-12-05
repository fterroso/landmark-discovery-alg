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
package lda.location.deliver;

import lda.config.LDAConfigProviderFactory;
import lda.landmark.provider.LandmarkProvider;
import lda.landmark.provider.LandmarkProviderFactory;
import lda.location.Location;
import lda.location.adaptor.PositionAdaptorFactory;
import lda.location.adaptor.PositionAdaptorWithThreashold;
import static lda.location.deliver.PositionDeliverer.LOG;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class PositionWithThreasholdDeliverer extends PositionDeliverer{
        public static void deliverPositions(){
        
        try{
            PositionAdaptorWithThreashold adaptor = (PositionAdaptorWithThreashold) PositionAdaptorFactory.getPositionProvider(LDAConfigProviderFactory.getCurrentConfigProvider());
                   
            LandmarkProvider landmarkProvider = LandmarkProviderFactory.getCurrentLandmarkProvider("test");
            
            Location pos = adaptor.getCurrentPosition();
            int index= 1;
            while(pos!=null){   
                pos.setIndex(index++);
                
                boolean mustBeSent = true;
                for(int i = 0; i < pos.getMeasure().length; i++){
                    if(pos.getMeasure()[i]< adaptor.getLowerBounds().get(i) || 
                            pos.getMeasure()[i]> adaptor.getUpperBounds().get(i)){
                        LOG.info("Not sent "+pos.getMeasure()[i] + " ["+adaptor.getLowerBounds().get(i)+":"+adaptor.getUpperBounds().get(i)+"]");
                        mustBeSent = false;
                        break;
                    }
                }
                if(mustBeSent){
                    LOG.info("Sent "+pos);
                    landmarkProvider.inferLandmarkForPoint(pos,false);
                }
                pos = adaptor.getCurrentPosition();                
            }
        }catch(Exception e){
            LOG.error("Error delivering positions ", e);
        }
    }
}
