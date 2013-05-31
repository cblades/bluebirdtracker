package bluebird.tracking.data;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import bluebird.tracking.enums.LogTags;

/*
 * A class to help with opening, creating and establishing a connection with the applicaiton's
 * SQLite database
 * 
 * @author Matthew Stratton
 * @version 1.0 May 31, 2013
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE = "bluebird";
	private static final String DDL_FILENAME = "bluebird.sql";
	private Context context;
	private AssetManager assetManager;
	
	/*
	 * Creates a DatabaseHelper object using the given context
	 * 
	 * @param context 	Application context to use
	 * @return 			DatabaseHelper instance
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
		this.context = context;
	}
	
	/*
	 * Creates a DatabaseHelper object using the given context and AssetManager, for testing purposes.
	 * The Test framework for content providers uses a weird (isolated) Context which has issues with 
	 * getAssets(), so we jump through some hoops to get the asset manager in the test class and pass 
	 * it to the constructor, for more info @see bluebird.tracking.test.DataProviderTest 
	 * 
	 * @param context 		Application context to use
	 * @param assetManager	AsetManager to use (used to get database schema file later)
	 */
	public DatabaseHelper(Context context, AssetManager assetManager){
		super(context, DATABASE, null, DATABASE_VERSION);
		this.context = context;
		this.assetManager = assetManager;
	}

	/*
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 * 
	 * Called the first time getReadableDatabase() or getWritableDatabase() is called and the database does
	 * not exist. Creates our database on the device using the schema found in the assets folder
	 * 
	 * @param db	The established database connection to use
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			if(assetManager == null)
				assetManager = context.getAssets();
			BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(DDL_FILENAME)));
			String line;
			String stmtBuffer = "";
			
			//read the file line by line
			while((line = reader.readLine()) != null){
				stmtBuffer += line;
				
				//end of the sql statement, execute it and clear the buffer
				if(stmtBuffer.endsWith(";")){
					Log.d(LogTags.DATABASE.toString(), stmtBuffer);
					db.execSQL(stmtBuffer);
					stmtBuffer = "";
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(LogTags.DATABASE.toString(), e.toString());
		}
	}

	/*
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 * 
	 * Called the first time getReadableDatabase() or getWritableDatabase() is called and the database does
	 * exist. Creates our database on the device using the schema found in the assets folder
	 * 
	 * @param db			The established database connection to use
	 * @param oldVersion	The previous database version
	 * @param newVersion	The new database version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO actually implement this method
		onCreate(db);
	}

}
