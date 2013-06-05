package bluebird.tracking.data;

import android.net.Uri;

/*
 * Static class to help with creating and using correct URI's to access our DataProvider.
 * All methods are static, no need to instantiate an instance of the class anywhere:
 * 		DataURI.getAllBoxesURI()
 * 
 * @author Matthew Stratton
 * @version 1.0 June 6, 2013
 */
public final class DataURI {
	
	private static Uri ALL_BOXES;
	private static Uri ALL_OBSERVATIONS;
	
	private DataURI() {}
	
	/*
	 * Creates the URI needed to access all boxes in the database
	 * 
	 * @return 	The content URI referring to all boxes
	 */
	public static Uri getAllBoxesURI(){
		if(ALL_BOXES == null)
			ALL_BOXES = Uri.parse("content://" + DataProvider.AUTHORITY + "/boxes");
		return ALL_BOXES;
	}
	
	/*
	 * Creates the URI needed to access all observations in the database (regardless
	 * of the box that owns the observation
	 * 
	 * @return	The content URI referring to all observations
	 */
	public static Uri getAllObservationsURI(){
		if(ALL_OBSERVATIONS == null)
			ALL_OBSERVATIONS = Uri.parse("content://" + DataProvider.AUTHORITY + "/observations");
		return ALL_OBSERVATIONS;
	}
	
	/*
	 * Creates the URI needed to access a box given the boxes' id. Does not ensure that a box with
	 * that id actually exists
	 * 
	 * @param	boxId, the _id of the desired box
	 * @return	The content URI referring to the specified box
	 */
	public static Uri getBoxURIById(int boxId){
		return Uri.parse("content://" + DataProvider.AUTHORITY + "/box/" + Integer.toString(boxId));
	}
	
	
	/*
	 * Creates the URI needed to access an observation given the observation's id. Does not ensure that an
	 * observation with that id actually exists
	 * 
	 * @param	observationId, the _id of the desired observation
	 * @return	The content URI referring to the specified observation
	 */
	public static Uri getObservationURIById(int observationId){
		return Uri.parse("content://" + DataProvider.AUTHORITY + "/observation/" + Integer.toString(observationId));
	}
	
	/*
	 * Creates the URI needed to access all observations for a given box. Does not ensure that any observations
	 * for the box with that id actually exist
	 * 
	 * @param	boxId, the _id of the box whose observations we want
	 * @return	The content URI referring to the observations of the specified box
	 */
	public static Uri getBoxObservationsURIById(int boxId){
		return Uri.parse("content://" + DataProvider.AUTHORITY + "/observations/box/" + Integer.toString(boxId));
	}
}
