package com.example.models;

import java.util.Vector;

public class GlobalVars {
	
	//ID for alarm new +
	public static long alarm_id;
	public static boolean marker_added = true;
	
	public static double lat_destination;
	public static double lat_origin;
	public static double lon_destination;
	public static double lon_origin;
	public static Boolean isOrigin;
	public static String title_pick_dest = "Pick Destination";
	public static String title_pick_origin = "Pick Origin";

	public static String[] createArray(String original, String separator){

	       Vector nodes = new Vector();

	        // Parse nodes into vector
	        int index = original.indexOf(separator);
	        while(index>=0) {
	            nodes.addElement( original.substring(0, index) );
	            original = original.substring(index+separator.length());
	            index = original.indexOf(separator);
	        }
	        // Get the last node
	        nodes.addElement( original );

	        // Create splitted string array
	        String[] result = new String[nodes.size()-1];
	        if( nodes.size()>0 ) {
	            for(int loop=0; loop<nodes.size()-1; loop++)
	            {
	                result[loop] = (String)nodes.elementAt(loop);
	                //System.out.println(result[loop]); - uncomment this line to see the result on output console
	            }
	        }
	        return result;
	   }
}
