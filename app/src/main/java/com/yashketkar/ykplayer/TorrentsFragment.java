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
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TorrentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TorrentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class TorrentsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ProgressBar tProgressBar;
    private WebView tWebView;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YTFragment.
     */

    public static TorrentsFragment newInstance(int sectionNumber) {
        TorrentsFragment fragment = new TorrentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TorrentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get tracker.
        Tracker t = ((AppController) getActivity().getApplication()).getTracker(
                AppController.TrackerName.APP_TRACKER);
        // Set screen name.
        t.setScreenName(getString(R.string.torrents_screen));
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        /*
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
      */


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity activity = (MainActivity) getActivity();
        activity.getToolbarRef().setBackgroundColor(getResources().getColor(R.color.play_green));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.play_green_dark));
            getActivity().setTaskDescription(new ActivityManager.TaskDescription(null/*activity.getmTitle().toString()*/, null, activity.getResources().getColor(R.color.play_green)));
        }

        // Inflate the layout for this fragment
        View InputFragmentView = inflater.inflate(R.layout.fragment_torrents, container, false);
        tWebView = (WebView) InputFragmentView.findViewById(R.id.torrents_web_view);
        tProgressBar = (ProgressBar) InputFragmentView.findViewById(R.id.torrents_progress_bar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tWebView.getSettings().setAllowContentAccess(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            tWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        }

        tWebView.getSettings().setAppCacheEnabled(true);
        tWebView.getSettings().setAllowFileAccess(true);
        tWebView.getSettings().setDatabaseEnabled(true);
        tWebView.getSettings().setDomStorageEnabled(true);
        tWebView.getSettings().setSaveFormData(true);
        tWebView.getSettings().setJavaScriptEnabled(true);
        tWebView.setWebViewClient(new WebViewClient());

        tWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                mListener.onTorrentsFragmentInteraction(url);
            }
        });

        tWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && tProgressBar.getVisibility() == ProgressBar.GONE) {
                    tProgressBar.setVisibility(ProgressBar.VISIBLE);
                    tWebView.setVisibility(WebView.GONE);
                }
                tProgressBar.setProgress(progress);
                if (progress == 100) {
                    tProgressBar.setVisibility(ProgressBar.GONE);
                    tWebView.setVisibility(WebView.VISIBLE);
                }
            }
        });

        tWebView.loadUrl("http://m.zbigz.com/");
        return InputFragmentView;
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
        public void onTorrentsFragmentInteraction(String id);

        public void onSectionAttached(int number);
    }
}