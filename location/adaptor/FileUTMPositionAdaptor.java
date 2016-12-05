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

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import lda.config.LDAConfigProvider;
import lda.location.Location;
import org.apache.log4j.Logger;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

/**
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class FileUTMPositionAdaptor extends PositionAdaptor{

    static Logger LOG = Logger.getLogger(FileUTMPositionAdaptor.class);
    private static final long TIME_BETWEEN_ITINERARIES = 7200000;
       
    private Scanner scanner;
    PrintWriter writer= null;
    
    LDAConfigProvider config;
    
    long counter = 0;
    
    String parsedFilePath;
    
    public FileUTMPositionAdaptor(LDAConfigProvider config){
        try{
                       
            this.config = config;
           
            File f = new File(config.getPositionFilePath());
            scanner = new Scanner(f);
            //skip head file
            scanner.nextLine();

        }catch(Exception e){
            LOG.error("Error while inicializing position provider ", e);
        }
    }
    
    
    @Override
    public Location getCurrentPosition() {
        
        Location result = null;
        
        if(scanner.hasNext()){
            String line = scanner.nextLine();
            result = parseString(line);
            if(config.shouldSerializePositions()){
                writer.println(counter+","+result.toPlainText());
                counter++;
            }
        }
        
        return result;
    }

    @Override
    public void serializePositions() {
        writer.close();        
    }
    
    private String detectItineraries(){
        
        String output = "";
        try{
                        
            String outFilePath = config.getPositionFilePath();            
            outFilePath = outFilePath.replace(".csv", "");
                                   
            File f3 = new File(outFilePath+".trace");
            PrintWriter writerTrace  = new PrintWriter(f3);
            
            output = config.getPositionFilePath();
            output = output.replace(".csv", "_2.csv");
            
            File f4 = new File(output);
            PrintWriter writer2CSV  = new PrintWriter(f4);
            writer2CSV.println("Cabecera");
            
            int trackIndex = 0;
            long previousTimestamp = 0;
            CVSLine previousLine = null;

            int numStretches = 0;
            
            boolean isFirst = true;
            StringBuilder timestamps = new StringBuilder();
            
            while(scanner.hasNext()){
                String str = scanner.nextLine();
                CVSLine line = new CVSLine(str);
            
                if(isFirst){
                   writer2CSV.println(line); 
                   isFirst = false;
                }
                
                if((previousTimestamp != 0) && (line.getTimestamp() - previousTimestamp) >= 7200000){
                                                    
                    StringBuilder sb = new StringBuilder();
                    sb.append(trackIndex++);
                    sb.append("_");
                    sb.append(numStretches);
                    sb.append(",");
                    sb.append(getTypeOccupancy(numStretches));
                    sb.append(",");
                    sb.append(timestamps.toString());                                        
                    
                    writerTrace.println(sb.toString());
                    
                    timestamps = new StringBuilder();
                    timestamps.append(line.getTimestamp());
                    numStretches = 0;
                                        
                    previousLine.setTimestamp(line.getTimestamp());
                    writer2CSV.println(previousLine);
                    
                }
                
                if(timestamps.length() > 0){
                    timestamps.append(",");
                }
                
                timestamps.append(line.getTimestamp());
                
                previousTimestamp = line.getTimestamp();
                previousLine = line;
                numStretches++;
                
                writer2CSV.println(str);

            }
            long lastTimestamp = previousTimestamp + (long) TIME_BETWEEN_ITINERARIES+1;
            
            StringBuilder finalLine = new StringBuilder();
            finalLine.append(trackIndex);
            finalLine.append(",0,");
            finalLine.append(lastTimestamp);
            
            writerTrace.println(finalLine.toString());            
            writerTrace.close();
            
            writer2CSV.close();
            
        }catch(Exception e){
            LOG.error("Error en CVS Pos. Provider ", e);
        }
        return output;
    }
    
    private void generateGPXFile(String outFilePath){
        try{
            
            restartScanner(); 
            
            outFilePath = outFilePath.replace(".csv", "");
                        
            File f2 = new File(outFilePath+".gpx");
            writer = new PrintWriter(f2);
            writer.println("<?xml version='1.0' encoding='UTF-8'?>\n<gpx version=\"1.1\" creator=\"JOSM GPX export\" xmlns=\"http://www.topografix.com/GPX/1/1\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">"); 
                        
            int trackIndex = 0;
            long previousTimestamp = 0;
            
            writer.println("<trk><name>"+trackIndex+"</name>\n<trkseg>");
            int numStretches = 0;
            StringBuilder timestamps = new StringBuilder();
            
            while(scanner.hasNext()){
                String str = scanner.nextLine();
                CVSLine line = new CVSLine(str);
                String gpxElement = getGPXElement(line);
                                               
                if((previousTimestamp != 0) && (line.getTimestamp() - previousTimestamp) >= TIME_BETWEEN_ITINERARIES){                                                                             
                                        
                    timestamps = new StringBuilder();
                    numStretches = 0;
                    
                    writer.println("</trkseg>\n</trk>");
                    trackIndex += 1;
                    writer.println("<trk><name>"+trackIndex+"</name>\n<trkseg>");
                                        
                }
                
                if(timestamps.length() > 0){
                    timestamps.append(",");
                }
                
                timestamps.append(line.getTimestamp());
                
                previousTimestamp = line.getTimestamp();
                writer.println(gpxElement);
                numStretches++;
                
            }
            
            writer.println("</trkseg>\n</trk>\n</gpx>");            
            writer.close();
            
        }catch(Exception e){
            LOG.error("Error en CVS Pos. Provider ", e);
        }        
    }
    
    private int getTypeOccupancy(int numStretches){
        int result = 1;
        if((numStretches <=15)){// && (numStretches < 5)){
            result = 1;
        }else if(numStretches <= 20){
            result = 2;
        }else{
            result = 3;
        }
        
        return result;
    }
    
    @Override
    protected Location parseString(String posStr){              
        CVSLine line = new CVSLine(posStr);
               
        UTMRef utmPoint = getUTMRefFromString(line);
        
        return new Location(utmPoint.toLatLng().getLat(), utmPoint.toLatLng().getLng());
    }
    
    private void restartScanner() throws Exception{
        File f = new File(parsedFilePath);
        scanner = new Scanner(f);
        //skip head file
        scanner.nextLine();
    }
    
    private UTMRef getUTMRefFromString(CVSLine line){
        
        UTMRef utmPoint = new UTMRef(line.getX(),line.getY(),'S',30);
        
        return utmPoint;
    }
    
    private String getGPXElement(CVSLine line){
        
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        UTMRef utmPoint = getUTMRefFromString(line);
        LatLng latLng = utmPoint.toLatLng();
        
        StringBuilder element = new StringBuilder();
        element.append("<trkpt lat=\"");
        element.append(latLng.getLat());
        element.append("\"  lon=\"");
        element.append(latLng.getLng());
        element.append("\">\n\t<time>");
        element.append(formatter.format(new Date(line.getTimestamp())));
        element.append("</time>\n\t<ele>0</ele>\n</trkpt>");
                
        return element.toString();
    }
    
    private class CVSLine{
        
        String routeID;
        long numSeq;
        double x;
        double y;
        long timestamp;

        public CVSLine(String line){
            StringTokenizer st = new StringTokenizer(line, ";");
            
            routeID = st.nextToken();
            numSeq = Long.valueOf(st.nextToken());

            x = Double.valueOf(st.nextToken());
            y = Double.valueOf(st.nextToken());
            
            timestamp = Long.valueOf(st.nextToken());
        }

        public long getNumSeq() {
            return numSeq;
        }

        public String getRouteID() {
            return routeID;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }                

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }   
        
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(routeID);
            sb.append(";");
            sb.append(numSeq);
            sb.append(";");
            sb.append(x);
            sb.append(";");
            sb.append(y);
            sb.append(";");
            sb.append(timestamp);            

            return sb.toString();
        }
        
    }
        
}
