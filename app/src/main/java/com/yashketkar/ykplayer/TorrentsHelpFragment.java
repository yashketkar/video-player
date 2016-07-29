package com.yashketkar.ykplayer;

/**
 * Created by Yash on 1/13/2015.
 */

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */

public class TorrentsHelpFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static TorrentsHelpFragment create(int pageNumber) {
        TorrentsHelpFragment fragment = new TorrentsHelpFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TorrentsHelpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_torrents_help, container, false);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(R.id.text_torrents_help)).setText(
                getResources().getStringArray(R.array.torrents_help_items)[mPageNumber]);

        TypedArray torrentsHelpIcons = getActivity().getResources().obtainTypedArray(R.array.torrents_help_icons);
        ((ImageView) rootView.findViewById(R.id.image_torrents_help)).setImageResource(
                (torrentsHelpIcons.getResourceId(mPageNumber, -1)));

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
