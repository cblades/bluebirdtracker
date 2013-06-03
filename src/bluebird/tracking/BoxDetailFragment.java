package bluebird.tracking;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import bluebird.tracking.dummy.DummyContent;

/**
 * A fragment representing a single Box detail screen.
 * This fragment is either contained in a {@link BoxListActivity}
 * in two-pane mode (on tablets) or a {@link BoxDetailActivity}
 * on handsets.
 */
public class BoxDetailFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    
    private static final String TAG = BoxDetailFragment.class.toString();
    
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The dummy content this fragment is presenting.
     */
    private String boxId;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BoxDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            boxId = getArguments().getString(ARG_ITEM_ID);
        }
        getLoaderManager().initLoader(0, null, (LoaderCallbacks<Cursor>) this);
    }

    /*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_box_detail, container, false);


        return rootView;
    }
    //*/
    
    
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated()");
        //*/
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        //*/
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    	Log.i(TAG, "onCreateLoader()");
        return new CursorLoader(getActivity(),
                Uri.parse("content://bluebird.tracking.data/observations/box/" + boxId)
                , null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    	Log.i(TAG, "onLoadFinished()");
    	this.setListAdapter(new SimpleCursorAdapter(this.getActivity(), 
    												R.layout.box_list_item, 
    												cursor, 
    												new String[] {"problem"}, 
    												new int[] {R.id.box_list_item_title}, 
    												0));
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
    
    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
