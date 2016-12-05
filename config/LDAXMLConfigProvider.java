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
import static lda.config.inputType.PositionInputType.CVS;
import static lda.config.inputType.PositionInputType.FILE;
import static lda.config.inputType.PositionInputType.SIMULATOR;
import org.apache.log4j.Logger;
import org.jdom.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import lda.config.outputType.PrintOutputType;
import lda.landmark.LandmarkType;
import lda.landmark.level.LandmarkLevelProvider;
import lda.landmark.level.LandmarkLevelProviderFactory;
import lda.landmark.provider.LandmarkProviderFactory.LandmarkProviderType;

/**
 * Class that provides the configuration of the IvCA module whereby a XML file.
 *
 * @author Fernando Terroso Saenz
 */
public class LDAXMLConfigProvider implements LDAConfigProvider, Serializable{

    static Logger LOG = Logger.getLogger(LDAXMLConfigProvider.class);
    
    //Environment
    String rootPath;
    
    //User
    String userID;
    
    LDASpaceType spaceType;
    
    //Input-positions
    PositionInputType posInputType;
    String positionFilePath;
    boolean shouldSerializePositions;
    boolean shouldSerializeClusterPositions;
    List<LandmarkType> clusterTypes;
    
    //Output
    String outputPath;
    PrintOutputType outputType;
    
    LandmarkLevelProvider landmarkLevelProvider;
    LandmarkProviderType landmarkProviderType;
        
    public LDAXMLConfigProvider(Element configXMLFile){
        init(configXMLFile);
    }
    
    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public String getOutputFilePath() {
        return outputPath;
    }
    
    private void init(Element root){

        try{
            rootPath = getRootPath();                           
            
            parseUserElement(root);
            
            Element output = root.getChild("output");
            parseOutputElement(output);
            
            Element input = root.getChild("input");
            parseInputElement(input);
            
            landmarkLevelProvider = LandmarkLevelProviderFactory.getLandmarkLevelProvider(this);
    
        }catch(Exception e){
            LOG.error("Parse fail ", e);
        }
    }
    
    private String getRootPath() throws Exception{
        
        String oS = System.getProperty("os.name"); 
        oS = oS.toLowerCase();
        oS = oS.replace(" ", "_");
       
       Properties p = new Properties();
       
       p.load(new FileInputStream("operating_system.properties"));
       String path = p.getProperty(oS+".config.path");
       
       if(!path.endsWith(File.separator)){
           path = path.concat(File.separator);
       }
       
       return path;
    }

    protected void parseUserElement(Element root){
       
        try{
            userID = root.getChild("user").getText();
        }catch(Exception e){
            LOG.warn("<user> element not found in LDA config.");
            userID ="";
        }
    }
    
    protected void parseInputElement(Element input){

        Element positions = input.getChild("locations");
        parseLocationsElement(positions);
    }
    
    protected void parseLocationsElement(Element positions){
        
        try{
            Element source = positions.getChild("source");    
            posInputType= PositionInputType.valueOf(source.getAttributeValue("type").toUpperCase());
            switch(posInputType){
                case CVS:
                    Element cvs=  source.getChild("cvs");
                    positionFilePath = cvs.getAttributeValue("path");
                    shouldSerializePositions = cvs.getAttributeValue("serialize").equals("YES");
                    break;
                case FILE:
                    positionFilePath = source.getChild("file").getAttributeValue("path");
                    break;
                case SIMULATOR:
                    Element simulator=  source.getChild("simulator");
                    shouldSerializePositions = simulator.getAttributeValue("serialize").equals("YES");
                    break;                
            }   
        }catch(Exception e){
            LOG.warn("<source> element not found in LDA config.");
        }

        Element landmarkPType = positions.getChild("landmark_provider_type");
        this.landmarkProviderType = LandmarkProviderType.valueOf(landmarkPType.getText().toUpperCase());
        
        Element cluster = positions.getChild("clustering");  
        shouldSerializeClusterPositions = cluster.getAttributeValue("serialize").equals("YES");
        
        Element clusterTypesElement = cluster.getChild("types");
        
        List<Element> types = clusterTypesElement.getChildren("type");
        
        clusterTypes = new LinkedList<LandmarkType>();
        for(Element type : types){
            int level = Integer.valueOf(type.getAttributeValue("level"));
            Element radius = type.getChild("radius");
            float radiusValue = Float.valueOf(radius.getAttributeValue("value"));
            
            Element minNPoints = type.getChild("minNPoints");
            int minPoints = Integer.valueOf(minNPoints.getAttributeValue("value"));            
            
            LandmarkType cType = new LandmarkType(level, radiusValue, minPoints);
            clusterTypes.add(cType);
        }
        
    }
    
    protected void parseOutputElement(Element output){
        
        outputPath = output.getChild("path").getText();
        
        if(!outputPath.endsWith(File.separator)){
            outputPath += File.separator;
        }
        
        outputType = PrintOutputType.valueOf(output.getChild("type").getText());
        
    }

    @Override
    public boolean shouldSerializePositions() {
        return shouldSerializePositions;
    }

    @Override
    public boolean shouldSerializeClusterPositions() {
        return shouldSerializeClusterPositions;
    }

    @Override
    public List<LandmarkType> getClusterTypes() {
        return Collections.unmodifiableList(clusterTypes);
    }
    
    @Override
    public LandmarkType getClusterTypeWithLevel(int level){
        List<LandmarkType> types = getClusterTypes();
        for(LandmarkType type : types){
            if(type.getLevel() == level){
                return type;
            }
        }
        
        return null;       
    }

    @Override
    public LandmarkProviderType getLandmarkProviderType(){
        return landmarkProviderType;
    }
    
    @Override
    public LandmarkLevelProvider getLandmarkLevelProvider(){
        return landmarkLevelProvider;
    }

    @Override
    public String getPositionFilePath() {
        return positionFilePath;
    }

    @Override
    public PositionInputType getPosInputType() {
        return posInputType;      
    }

    @Override
    public PrintOutputType getOutputType() {
        return outputType;
    }
    
    @Override
    public LDASpaceType getSpaceType(){
        return spaceType;
    }

    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    @Override
    public void setSpaceType(String pSpaceType){
        spaceType = LDASpaceType.valueOf(pSpaceType.toLowerCase());
    }


}
