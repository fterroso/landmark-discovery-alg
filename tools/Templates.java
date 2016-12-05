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
package lda.tools;

/**
 * File templates to be used as system's output.
 *
 * @author Fernando Terroso-Saenz <fterroso@um.es>
 */
public class Templates {
    
    //KML   
    public static final String KML_GENERAL_HEAD_LINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:xal=\"urn:oasis:names:tc:ciq:xsdschema:xAL:2.0\">\n<Document>\n\t<name>Landmarks for ELEMENT_NAME</name>\n";
    public static final String KML_GENERAL_TAIL_LINE = "</Document>\n</kml>";
       
    public static final String KML_SPECIFIC_HEAD_LINE = "<Folder id=\"ELEMENT_NAME\">\n"+
                                                               "<name>ELEMENT_NAME</name>\n";
    
    public static final String KML_SPECIFIC_TAIL_LINE = "</Folder>\n"; 
  
    public static final String KML_TRACK_STYLE = "<Style id=\"linestyleNUM_LEVEL\">\n"+
                                                        "\t<LineStyle>\n"+
                                                            "\t\t<color>COLOR_CODE</color>\n"+
                                                            "\t\t<width>2</width>\n"+
                                                        "\t</LineStyle>\n"+
                                                    "</Style>\n";
    
    public static final String KML_LANDMARK_STYLE = "<Style id=\"landmarkstyleLANDMARK_TYPE\">\n"+
                                                  "\t<LabelStyle>\n"+
                                                  "\t\t<color>COLOR_CODE</color>\n"+
                                                  "\t\t<scale>0.7</scale>\n"+
                                                  "\t</LabelStyle>\n"+
                                                  "\t<PolyStyle>\n"+
                                                  "\t\t<color>COLOR_CODE</color>\n"+
                                                  "\t</PolyStyle>\n"+
                                                  "</Style>\n";
    
    public static final String KML_LANDMARK = "\t<Placemark> \n" +
                                              "\t\t<name>LANDMARK_NAME</name>\n"+
                                              "\t\t<styleUrl>#landmarkstyleLANDMARK_TYPE</styleUrl>\n"+
                                              "\t\t<description><![CDATA[DESCRIPTION]]></description>\n"+
                                              "\t\t<Polygon>\n"+
                                              "\t\t\t<extrude>1</extrude>\n" +
                                              "\t\t\t<altitudeMode>relativeToGround</altitudeMode>\n"+
                                              "\t\t\t<outerBoundaryIs><LinearRing>\n" +
                                              "\t\t\t\t<coordinates>\n" +
                                              "COORD"+
                                              "\t\t\t\t</coordinates>\n" +
                                              "\t\t\t</LinearRing></outerBoundaryIs>\n"+
                                              "\t\t</Polygon>\n"+
                                              "\t</Placemark>";
    
    
    public static final String KML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"; 
    public static final String KML_COORDINATES_FORMAT = "(-?\\d+\\.?\\d*)(\\s+)(-?\\d+\\.?\\d*)\\s+";

    //Lines to skip in a geolife file
    public static final int GEOLIFE_FILE_HEAD_LINES = 6;
    
    //PLAIN TEXT
    public static final String GPSVISUALIZER_WEB_HEAD_LINE = "name,desc,color,opacity,symbol,latitude,longitude\n"; 
    
    //GPX.
    public static final String GPX_HEAD_LINE = "<?xml version='1.0' encoding='UTF-8'?>\n<gpx version=\"1.1\" creator=\"JOSM GPX export\" xmlns=\"http://www.topografix.com/GPX/1/1\"\n xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n";
    public static final String GPX_TAIL_LINE = "</gpx>\n";    
    
}
