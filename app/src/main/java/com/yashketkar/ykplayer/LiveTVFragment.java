package com.yashketkar.ykplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yash on 1/4/2015.
 */
public class LiveTVFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private String url;
    private ProgressBar pDialog;
    private List<TV> tvList = new ArrayList<TV>();
    private ListView listView;
    private LiveTVAdapter adapter;

    public static LiveTVFragment newInstance(int sectionNumber) {
        LiveTVFragment fragment = new LiveTVFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LiveTVFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Get tracker.
        Tracker t = ((AppController) getActivity().getApplication()).getTracker(
                AppController.TrackerName.APP_TRACKER);
        // Set screen name.
        t.setScreenName(getString(R.string.live_tv_screen));
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        adapter = new LiveTVAdapter(getActivity(), tvList);

        mAdapter = adapter;

        url = getResources().getString(R.string.channels_link);

        // Showing progress dialog before making http request
        //pDialog.setMessage("Loading...");
        //pDialog.show();

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());
                        hidePDialog();
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                TV tv = new TV();
                                tv.setTitle(obj.getString("title"));
                                tv.setThumbnailUrl(obj.getString("image"));
                                tv.setPlaybackUrl(obj.getString("play"));
                                // adding movie to movies array
                                tvList.add(tv);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(tvList, new Comparator<TV>() {
                            @Override
                            public int compare(TV a, TV b) {
                                String valA = new String();
                                String valB = new String();

                                try {
                                    valA = (String) a.getTitle();
                                    valB = (String) b.getTitle();
                                } catch (Exception e) {
                                    // Log.e(LOG_TAG, "JSONException in combineJSONArrays sort section", e);
                                }

                                int comp = valA.compareTo(valB);

                                if (comp > 0)
                                    return 1;
                                if (comp < 0)
                                    return -1;
                                return 0;
                            }
                        });

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d(TAG, "Error: " + error.getMessage());
                /*Toast.makeText(getActivity(), "" + error.getMessage(),
                        Toast.LENGTH_SHORT).show();*/
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.setVisibility(ProgressBar.GONE);
            pDialog = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity activity = (MainActivity) getActivity();
        activity.getToolbarRef().setBackgroundColor(getResources().getColor(R.color.play_red));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.play_red_dark));
            getActivity().setTaskDescription(new ActivityManager.TaskDescription(null/*activity.getmTitle().toString()*/, null, activity.getResources().getColor(R.color.play_red)));
        }

        View view = inflater.inflate(R.layout.fragment_livetv, container, false);

        pDialog = (ProgressBar) view.findViewById(R.id.livetv_progress_bar);

        pDialog.setVisibility(ProgressBar.VISIBLE);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onLiveTVFragmentInteraction(tvList.get(position).getPlaybackUrl());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();
        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onLiveTVFragmentInteraction(String id);

        public void onSectionAttached(int number);
    }

}
