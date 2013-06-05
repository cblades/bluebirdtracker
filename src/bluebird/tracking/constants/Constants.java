package bluebird.tracking.constants;

/*
 * Class containing constants used throughout the application. All
 * constants should be declared as static final so they cannot be modified
 * and can be organized by creating a final inner class to wrap like
 * constants
 * 
 * @author Matthew Stratton
 * @version 1.0 June 6, 2013
 */
public final class Constants {	
	
	/*
	 * Used by an Activity to load data from database via a CursorLoader
	 */
	public final class DataLoaderID{
		public static final int BOX_LIST_LOADER = 0;
		public static final int OBSERVATION_LIST_LOADER = 1;
		public static final int OBSERVATIONS_LIST_BY_BOX_LOADER = 2;
		public static final int BOX_LOADER = 3;
		public static final int OBSERVAITON_LOADER = 4;
	}

	/*
	 * Used whenever logging throughout the app for consistency purposes
	 */
	public final class LogTags{
		public static final String DATABASE = "Database";
		public static final String CONTENT_PROVIDER = "Content Provider";
	}
}
