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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Factory that provides the right ConfigProvider for each simulation
 *
 * @author Feranando Terroso-Saenz
 */
public class LDAConfigProviderFactory {
    
    static LDAConfigProvider configProvider = null;
    
    public static LDAConfigProvider createConfigProvider(String path)throws Exception{
        
        if(path.endsWith(".xml")){
            SAXBuilder builder=new SAXBuilder(false);
            Document doc=builder.build(path);
            Element root =doc.getRootElement();  
            createConfigProvider(root);
        }
        
        return configProvider;        
    }
    
    public static LDAConfigProvider createConfigProvider(Element root)throws Exception{
        
        configProvider = new LDAXMLConfigProvider(root);
        return configProvider;
    }


    public static LDAConfigProvider getCurrentConfigProvider() {
        return configProvider;
    }    
    
}
