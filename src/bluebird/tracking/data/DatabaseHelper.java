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

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE = "bluebird";
	private static final String DDL_FILENAME = "bluebird.sql";
	private Context context;
	private AssetManager assetManager;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE, null, DATABASE_VERSION);
		this.context = context;
	}
	
	/*
	 * for testing purposes, the Test framework for content providers uses a weird (isolated) Context which
	 * has issues with getAssets(), so we jump through some hoops to get the asset manager in the test
	 * class and pass it to the constructor, see DataProviderTest for more info
	 */
	public DatabaseHelper(Context context, AssetManager assetManager){
		super(context, DATABASE, null, DATABASE_VERSION);
		this.context = context;
		this.assetManager = assetManager;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			if(assetManager == null)
				assetManager = context.getAssets();
			BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(DDL_FILENAME)));
			String line;
			String stmtBuffer = "";
			while((line = reader.readLine()) != null){
				stmtBuffer += line;
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

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
