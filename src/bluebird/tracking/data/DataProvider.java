package bluebird.tracking.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import bluebird.tracking.constants.Constants;

/*
 * A ContentProvider used to query, update, insert into and delete from our database using URI's
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * 
 * @author Matthew Stratton
 * @version 1.0 May 31, 2013
 */
public class DataProvider extends ContentProvider {
	/* The symbolic name of the entire content provider */
	public static final String AUTHORITY = "bluebird.tracking.data";
	
	/* Used to match URI requests we get to request we know we can handle */
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	/* DatabaseHelper for accessing our database */
	private DatabaseHelper db;
	
	private static final int BOXES = 100;
	private static final int BOX_ID = 101;
	private static final int OBSERVATIONS = 102;
	private static final int OBSERVATIONS_ID = 103;
	private static final int OBSERVATIONS_BOX_ID = 104;
	
	
	static{
		uriMatcher.addURI(AUTHORITY, "boxes", BOXES);
		uriMatcher.addURI(AUTHORITY, "box/#", BOX_ID);
		uriMatcher.addURI(AUTHORITY, "observations", OBSERVATIONS);
		uriMatcher.addURI(AUTHORITY, "observation/#", OBSERVATIONS_ID);
		uriMatcher.addURI(AUTHORITY, "observations/box/#", OBSERVATIONS_BOX_ID);
	}
	
	public DataProvider() {}
	
	/* 
	 * @see android.content.ContentProvider#onCreate()
	 * 
	 * Called by system when creating the content provider
	 * 
	 * @return	Boolean value indicating success or failure or creation
	 */
	@Override
	public boolean onCreate() {
		Log.d(Constants.LogTags.CONTENT_PROVIDER, "Creating DataProvider");
		db = new DatabaseHelper(getContext());		
		return true;
	}

	/*
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 * 
	 * Tells users the type of data returned from a URI request
	 * 
	 * @return	String indicating type of data in a standard format
	 */
	@Override
	public String getType(Uri uri){
		String type = "";
		switch(uriMatcher.match(uri)){
		case BOXES:
			type = "vnd.android.cursor.dir/com.bluebird.tracking.data.Box";
			break;
		case OBSERVATIONS:
			type = "vnd.android.cursor.dir/com.bluebird.tracking.data.Observation";
			break;
		case OBSERVATIONS_BOX_ID:
			type = "vnd.android.cursor.dir/com.bluebird.tracking.data.BoxObservation";
			break;
		case BOX_ID:
			type = "vnd.android.cursor.item/com.bluebird.tracking.data.Box";
			break;
		case OBSERVATIONS_ID:
			type = "vnd.android.cursor.item/com.bluebird.tracking.data.Observation";
			break;
		default:
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Bad type request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		return type;
	}

	/*
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 * 
	 * Query the database based on a given URI and selection parameters
	 * 
	 * @param uri			URI requested
	 * @param projection	The columns of data the user wants
	 * @param selection		The where clause requested ("_ID = ? and name = ? ...")
	 * @param selectionArgs	Strings to replace ? with in selection (["1", "Bob" ...])
	 * @param sortOrder		Order in which to return the data ("_ID DESC")
	 * 
	 * @return 				Cursor object that can be used to retrieve data matching the query 
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String table = "";
		String where = null;
		String sort = null;
		
		switch(uriMatcher.match(uri)){
		case BOXES:
			table = "Box";
			sort = "_id " + ((sortOrder == null) ? "ASC" : sortOrder);
			break;
		case OBSERVATIONS:
			table = "Observation";
			sort = "obs_date " + ((sortOrder == null) ? "DESC" : sortOrder);
			sort += ", _id " +  "ASC";
			break;
		case OBSERVATIONS_BOX_ID:
			table = "Box b JOIN Observation o ON b._id = o.box_id";
			where = "box_key = " + uri.getLastPathSegment().toString();
			sort = "obs_date " + ((sortOrder == null) ? "DESC" : sortOrder);
			break;
		case BOX_ID:
			table = "Box";
			where = "_id = " + uri.getLastPathSegment().toString();
			break;
		case OBSERVATIONS_ID:
			table = "Observation";
			where = "_id = " + uri.getLastPathSegment().toString();
			break;
		default:
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Bad query request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		
		queryBuilder.setTables(table);
		if(where != null)
			queryBuilder.appendWhere(where);
		
		try{
			Cursor c = queryBuilder.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sort);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		} catch(SQLiteException e){
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Error opening database connection " + e.toString());
			throw new RuntimeException("Error opening database connection", e);
		}
	}

	/*
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 * 
	 * Inserting the given data into our database based on a URI.
	 * 
	 * @param uri		URI requested, the table to insert into
	 * @param values	Data values to insert into the database
	 * 
	 * @return 			URI of newly created entry
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = "";
		
		switch(uriMatcher.match(uri)){
		case BOXES:
			table = "Box";
			break;
		case OBSERVATIONS:
			table = "Observation";
			break;
		default:
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Bad insert request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		
		try{
			SQLiteDatabase writableDB = db.getWritableDatabase();
			long newRowID = (int)writableDB.insert(table, null, values);
		
			if(newRowID < -1)
				throw new RuntimeException("An error occured inserting into the database");
		
			Uri newUri = Uri.parse("content://" + AUTHORITY + table.toLowerCase() + "/" + Long.toString(newRowID)); //create URI of new entry
			getContext().getContentResolver().notifyChange(newUri, null); //notify any listeners of change to database
			return newUri;
		} catch(SQLiteException e){
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Error opening database connection " + e.toString());
			throw new RuntimeException("Error opening database connection", e);
		}
	}
	
	/*
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 * 
	 * Update the database given a URI, selection parameters and new data values
	 * 
	 * @param uri				URI requested, the table or entity to update
	 * @param values			New data values to set
	 * @param selection			The where clause requested ("_ID = ? and name = ? ...")
	 * @param selectionArgs		Strings to replace ? with in selection (["1", "Bob" ...])
	 * 
	 * @return 					Number of rows affected
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		String table = "";
		
		switch(uriMatcher.match(uri)){
		case BOXES:
			table = "Box";
			break;
		case OBSERVATIONS:
			table = "Observation";
			break;
		case BOX_ID:
			table = "Box";
			//ignore the user's selection clause and values, the URI specifies the where clause we want
			selection = "_id = ?"; 
			selectionArgs = new String[] {uri.getLastPathSegment().toString()};
			break;
		case OBSERVATIONS_ID:
			table = "Observation";
			//ignore the user's selection clause and values, the URI specifies the where clause we want
			selection = "_id = ?";
			selectionArgs = new String[] {uri.getLastPathSegment().toString()};
			break;
		default:
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Bad update request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		
		try{
			SQLiteDatabase writableDB = db.getWritableDatabase();
			int changed = writableDB.update(table, values, selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null); //notify any listeners of change to database
			return changed;
		} catch(SQLiteException e){
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Error opening database connection " + e.toString());
			throw new RuntimeException("Error opening database connection", e);
		}
	}
	
	/*
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 * 
	 * Delete rows from the database based on a URI and/or selection arguments
	 * 
	 * @param uri				URI requested, the table or entity to update
	 * @param selection			The where clause requested ("_ID = ? and name = ? ...")
	 * @param selectionArgs		Strings to replace ? with in selection (["1", "Bob" ...])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = "";
		
		switch(uriMatcher.match(uri)){
		case BOXES:
			table = "Box";
			break;
		case OBSERVATIONS:
			table = "Observation";
			break;
		case BOX_ID:
			table = "Box";
			//ignore the user's selection clause and values, the URI specifies the where clause we want
			selection = "_id = ?";
			selectionArgs = new String[] {uri.getLastPathSegment().toString()};
			break;
		case OBSERVATIONS_ID:
			table = "Observation";
			//ignore the user's selection clause and values, the URI specifies the where clause we want
			selection = "_id = ?";
			selectionArgs = new String[] {uri.getLastPathSegment().toString()};
			break;
		default:
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Bad delete request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		
		try{
			SQLiteDatabase writableDB = db.getWritableDatabase();
			int deleted = writableDB.delete(table, selection, selectionArgs);
			getContext().getContentResolver().notifyChange(uri, null); //notify any listeners of change to database
			return deleted;
		} catch(SQLiteException e){
			Log.e(Constants.LogTags.CONTENT_PROVIDER, "Error opening database connection " + e.toString());
			throw new RuntimeException("Error opening database connection", e);
		}
	}
	
	/*
	 * @see android.content.ContentProvider#shutdown()
	 * 
	 * Called when shutting down the content provider, used to free up any resources or persist any data before the OS
	 * shuts us down. Just a stub for now
	 */
	@Override
	public void shutdown(){
		super.shutdown();
	}
}
