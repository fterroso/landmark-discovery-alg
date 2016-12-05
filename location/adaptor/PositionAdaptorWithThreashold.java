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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lda.location.Location;
import static lda.location.adaptor.FilePositionAdaptor.LOG;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class PositionAdaptorWithThreashold extends FilePositionAdaptor{
    
    private final static String UPPER_BOUNDS_PATTERN = "Umbral Superior: ((((-?)\\d+\\.\\d+)|(\\s))+)";
    private final static String LOWER_BOUNDS_PATTERN = "Umbral Inferior: ((((-?)\\d+\\.\\d+)|(\\s))+)";
    
    private List<Double> upperBounds = new ArrayList<Double>();
    private List<Double> lowerBounds = new ArrayList<Double>();
    
    
    public PositionAdaptorWithThreashold(LDAConfigProvider config){
        super(config);
        setUpperBounds();
        restartFileReader();
        setLowerBounds();
        init();
    }

    @Override
    public Location getCurrentPosition() {
        Location currentPosition = null;
        try{
            String lineString = br.readLine();
            
            if(lineString != null){
                currentPosition = parseString(lineString);
            }
        }catch(Exception e){
            LOG.error("Error in getCurrentPosition ", e);
        }finally{
            LOG.info("Read position: " + currentPosition);
            return currentPosition;
        }
    }

    public List<Double> getUpperBounds() {
        return upperBounds;
    }

    public List<Double> getLowerBounds() {
        return lowerBounds;
    }
    
    @Override
    protected Location parseString(String str){

        int indexFirst = str.indexOf("[");
        String aux = str.substring(indexFirst+1, str.length()-1);
        
        StringTokenizer st = new StringTokenizer(aux, " \t");
        float y = Float.valueOf(st.nextToken());
        float x = Float.valueOf(st.nextToken());
        
        List<Double> measures = new LinkedList<Double>();
        while(st.hasMoreTokens()){            
            double measure = Double.valueOf(st.nextToken());
            measures.add(measure);
        }
        
        double[] ms = new double[measures.size()];
        for(int i = 0; i< measures.size(); i++){
            ms[i] = measures.get(i);
        }
        
        Location pos = new Location(y, x);//, (char)0, (char)0);
        
        pos.setMeasure(ms);
        
        return pos;
    }
    
    private void setUpperBounds(){

        try{
            String lineString = br.readLine(); 
            while(lineString != null){
                
                Matcher m = Pattern.compile(UPPER_BOUNDS_PATTERN).matcher(lineString);
        
                if(m.find()){
            
                    String bounds = m.group(1).replace(".", ",");
                    Scanner scanner = new Scanner(bounds).useDelimiter("\\s");            

                    while(scanner.hasNextDouble()){
//                        System.out.println(scanner.nextDouble());
                        upperBounds.add(scanner.nextDouble());
                    } 

                }
                lineString = br.readLine(); 
            }
        }catch(Exception e){
            LOG.error("Error in setUpperBounds ", e);
        }
    }
    
    private void setLowerBounds(){
        try{
            String lineString = br.readLine(); 
            while(lineString != null){
                
                Matcher m = Pattern.compile(LOWER_BOUNDS_PATTERN).matcher(lineString);
        
                if(m.find()){
            
                    String bounds = m.group(1).replace(".", ",");
                    Scanner scanner = new Scanner(bounds).useDelimiter("\\s");            

                    while(scanner.hasNextDouble()){
//                        System.out.println(scanner.nextDouble());
                        lowerBounds.add(scanner.nextDouble());
                    } 

                }
                lineString = br.readLine(); 
            }
        }catch(Exception e){
            LOG.error("Error in setLowerBounds ", e);
        }
    }
}
