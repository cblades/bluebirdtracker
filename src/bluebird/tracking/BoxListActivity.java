package bluebird.tracking;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import bluebird.tracking.constants.Constants;
import bluebird.tracking.data.DataURI;


/**
 * An activity representing a list of Boxes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BoxDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link BoxListFragment} and the item details
 * (if present) is a {@link BoxDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link BoxListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class BoxListActivity extends FragmentActivity implements 
	BoxListFragment.Callbacks, 
	LoaderManager.LoaderCallbacks<Cursor> 
{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    
    /*
     * Used to map data in a database (accessed via a Cursor) to our listview. Basically you
     * tell how to map the data columns to views id's in the constructor (column A maps to 
     * R.id.TextViewA...) 
     */
    SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_list);
        
        //instantiate the cursor adapter, telling it which data columns map to which view component id's
        mAdapter = new SimpleCursorAdapter(
	            this,                							// Current context
	            android.R.layout.simple_list_item_activated_1,  // Layout for a single row
	            null,                							// No Cursor yet
	            new String[]{"box_number"},        				// Cursor columns to use
	            new int[]{android.R.id.text1},           		// Layout fields to use
	            0                   							 // No flags
	    );
        
        /*
         * get this activities fragment (which is the ListView) and set the ListAdapter to our newly
         * created CursorAdapter. The ListView knows how to refresh the UI whenever it's data changes, 
         * saving us alot of work.
         */
        ((BoxListFragment)getSupportFragmentManager().findFragmentById(R.id.box_list)).setListAdapter(mAdapter);
        
        /*
         * Create a CursorLoader for querying our database. Longer running queries will cause the UI thread to 
         * hang. The CursorLoader has it's own thread to run queries on so the UI doesnt hang. Whenever results 
         * are ready from the query, this activities' onLoadFinished() method is called and we can do whatever 
         * we need to do with the Cursor (data). See https://developer.android.com/training/load-data-background/setup-loader.html
         * for more info
         */
        getSupportLoaderManager().initLoader(
        		Constants.DataLoaderID.BOX_LIST_LOADER,		//ID of the Loader to use
        		null, 										//arguments to pass to the CursorLoader
        		this										//Object whose onLoadFinished() method to call
        );

        if (findViewById(R.id.box_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((BoxListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.box_list))
                    .setActivateOnItemClick(true);
        }
    }

    /**
     * Callback method from {@link BoxListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(BoxDetailFragment.ARG_ITEM_ID, id);
            BoxDetailFragment fragment = new BoxDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.box_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, BoxDetailActivity.class);
            detailIntent.putExtra(BoxDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
    
    /*
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
     * 
     * Creates a CursorLoader object based on the loaderID. We only care about creating a loader for
     * boxes in this case, so we only handle the BOX_LIST_LOADER id.
     * 
     * @param loaderID	The ID of the loader to create
     * @param bundle	Any extra arguments to pass to the method
     * 
     * @return			A CursorLoader which will asynchronously query our DataProvider
     */
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		switch(loaderID){
		case Constants.DataLoaderID.BOX_LIST_LOADER:
			return new CursorLoader(this, DataURI.getAllBoxesURI(), null, null,null, null);
		default:
			return null;
		}
	}

	/*
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 *
	 * Called when the CursorLoader's query returns with data
	 * 
	 * @param loader	The CursorLoader which began the query
	 * @param cursor	Cursor containing the requested data
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor); //we have our data now so we give it to our SimpleCursorAdapter 
		
	}

	/*
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 * 
	 * Called the the Cursor becomes invalid (usually because the data associated with it has changed)
	 * 
	 * @param loader	The CursorLoader which began the query
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null); //clear adapter's reference to the cursor (helps with memory leaks)
	}
}
