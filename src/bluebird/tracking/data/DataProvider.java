package bluebird.tracking.data;

import bluebird.tracking.enums.LogTags;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DataProvider extends ContentProvider {
	public static final String AUTHORITY = "bluebird.tracking.data";
	private static final int BOXES = 100;
	private static final int BOX_ID = 101;
	private static final int OBSERVATIONS = 102;
	private static final int OBSERVATIONS_ID = 103;
	private static final int OBSERVATIONS_BOX_ID = 104;
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	private DatabaseHelper db;
	
	static{
		uriMatcher.addURI(AUTHORITY, "boxes", BOXES);
		uriMatcher.addURI(AUTHORITY, "box/#", BOX_ID);
		uriMatcher.addURI(AUTHORITY, "observations", OBSERVATIONS);
		uriMatcher.addURI(AUTHORITY, "observation/#", OBSERVATIONS_ID);
		uriMatcher.addURI(AUTHORITY, "observations/box/#", OBSERVATIONS_BOX_ID);
	}
	
	public DataProvider() {}
	
	@Override
	public boolean onCreate() {
		db = new DatabaseHelper(getContext());		
		return true;
	}

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
			Log.e(LogTags.CONTENT_PROVIDER.toString(), "Bad type request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		return type;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String table = "";
		String where = null;
		String sort = null;
		switch(uriMatcher.match(uri)){
		case BOXES:
			table = "Box";
			sort = "_ID " + ((sortOrder == null) ? "ASC" : sortOrder);
			break;
		case OBSERVATIONS:
			table = "Observation";
			sort = "time " + ((sortOrder == null) ? "DESC" : sortOrder);
			sort += ", _ID " +  "ASC";
			break;
		case OBSERVATIONS_BOX_ID:
			table = "Box b JOIN Observation o ON b._ID = o.box_key";
			where = "box_key = " + uri.getLastPathSegment().toString();
			sort = "time " + ((sortOrder == null) ? "DESC" : sortOrder);
			break;
		case BOX_ID:
			table = "Box";
			where = "_ID = " + uri.getLastPathSegment().toString();
			break;
		case OBSERVATIONS_ID:
			table = "Observation";
			where = "_ID = " + uri.getLastPathSegment().toString();
			break;
		default:
			Log.e(LogTags.CONTENT_PROVIDER.toString(), "Bad query request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		queryBuilder.setTables(table);
		if(where != null)
			queryBuilder.appendWhere(where);
		Cursor c = queryBuilder.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sort);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch(uriMatcher.match(uri)){
		case BOXES:
			break;
		case OBSERVATIONS:
			break;
		case OBSERVATIONS_BOX_ID:
			break;
		case BOX_ID:
			break;
		case OBSERVATIONS_ID:
			break;
		default:
			Log.e(LogTags.CONTENT_PROVIDER.toString(), "Bad insert request for " + uri.toString());
			throw new IllegalArgumentException("Unknown URI " + uri.toString());
		}
		return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void shutdown(){
		super.shutdown();
	}
}
