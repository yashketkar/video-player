package com.yashketkar.ykplayer;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, VideosFragment.OnFragmentInteractionListener, TorrentsFragment.OnFragmentInteractionListener, LiveTVFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private static final String PREF_USER_LEARNED_TORRENT = "torrent_learned";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */

    private InterstitialAd interstitial;

    private CharSequence mTitle;
    private String versioncode;
    private String valert_title;
    private String valert_message;
    private String valert_message_big;
    private String downloadurl;
    private boolean mUserLearnedTorrents;


    public void onVideosFragmentInteraction(String id) {
        playvideo(id);
    }

    public void onLiveTVFragmentInteraction(String id) {
        playvideo(id);
    }

    public void onTorrentsFragmentInteraction(String id) {
        playvideo(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get tracker.
        Tracker t = ((AppController) MainActivity.this.getApplication()).getTracker(
                AppController.TrackerName.APP_TRACKER);
        // Set screen name.
        t.setScreenName(getString(R.string.main_screen));
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        makeJsonObjectRequest();

        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();
        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
    }

    public Toolbar getToolbarRef() {
        return mToolbar;
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();

        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new VideosFragment().newInstance(position);
                switchfragments(fragment);
                break;
            case 1:
                //fragment = new VideosFragment().newInstance(position);
                AlertDialog.Builder nwalert = new AlertDialog.Builder(this);
                nwalert.setTitle(getString(R.string.nw_alert_title));
                nwalert.setMessage(getString(R.string.nw_alert_message));
                final EditText nwinput = new EditText(this);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    // only for gingerbread and newer versions
                    nwinput.setTextColor(Color.WHITE);
                }
                nwinput.setSingleLine();
                nwalert.setView(nwinput);
                nwalert.setPositiveButton(getString(R.string.nw_alert_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                playvideo(nwinput.getText().toString()
                                        .replaceAll("[\\t\\n\\r]", ""));
                            }
                        });
                nwalert.setNegativeButton(getString(R.string.nw_alert_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // Canceled.
                            }
                        });
                nwalert.show();
                break;
            case 2:
                fragment = new TorrentsFragment().newInstance(position);
                switchfragments(fragment);
                SharedPreferences sp = this.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                mUserLearnedTorrents = sp.getBoolean(PREF_USER_LEARNED_TORRENT, false);
                if (mUserLearnedTorrents == false) {
                    Intent intent = new Intent(this,
                            TorrentsHelpActivity.class);
                    startActivity(intent);
                }
                break;
            case 3:
                fragment = new LiveTVFragment().newInstance(position);
                switchfragments(fragment);
                break;
            case 4:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_message));
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_title));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser)));
                break;
            default:
                break;
        }
    }

    public void switchfragments(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void playvideo(String id) {
        displayInterstitial();
        Intent intent = new Intent(MainActivity.this,
                VideoPlayerActivity.class);
        intent.putExtra("EXTRA_URL", id);
        startActivity(intent);
    }

    private void makeJsonObjectRequest() {

        String urlJsonObj = getString(R.string.version_link);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    versioncode = response.getString("versioncode");
                    valert_title = response.getString("valert_title");
                    valert_message = response.getString("valert_message");
                    valert_message_big = response.getString("valert_message_big");
                    downloadurl = response.getString("downloadurl");

                    if (BuildConfig.VERSION_CODE < Integer.parseInt(versioncode)) {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(MainActivity.this)
                                        .setSmallIcon(R.drawable.ic_update)
                                        .setContentTitle(valert_title)
                                        .setContentText(valert_message)
                                        .setColor(getResources().getColor(R.color.play_blue));
                        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadurl));

                        // Because clicking the notification opens a new ("special") activity, there's
                        // no need to create an artificial back stack.
                        PendingIntent resultPendingIntent =
                                PendingIntent.getActivity(
                                        MainActivity.this,
                                        0,
                                        resultIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );

                        mBuilder.setContentIntent(resultPendingIntent);
                        mBuilder.setAutoCancel(true);
                        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                        mBuilder.setDefaults(Notification.DEFAULT_ALL);
                        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(valert_message_big));

                        Notification note = mBuilder.build();
                        note.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

                        // Sets an ID for the notification
                        int mNotificationId = 1;
                        // Gets an instance of the NotificationManager service
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // Builds the notification and issues it.
                        mNotifyMgr.notify(mNotificationId, note);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    /*Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();*/
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                /*Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();*/
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public void onSectionAttached(int number) {
        mTitle = getResources().getStringArray(R.array.nav_drawer_items)[number];
    }

    public void restoreActionBar() {
        /*
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        */
        mToolbar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}