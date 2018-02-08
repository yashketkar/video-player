package com.yashketkar.ykplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VideosFragment.OnFragmentInteractionListener, TorrentsFragment.OnFragmentInteractionListener, LiveTVFragment.OnFragmentInteractionListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private int mCurrentSelectedPosition;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private boolean mUserLearnedTorrents;
    private static final String PREF_USER_LEARNED_TORRENT = "torrent_learned";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static Snackbar permissionsSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        } else {
            mCurrentSelectedPosition = R.id.nav_videos;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder nwalert = new AlertDialog.Builder(view.getContext());
                nwalert.setTitle(getString(R.string.nw_alert_title));
                nwalert.setMessage(getString(R.string.nw_alert_message));
                final EditText nwinput = new EditText(view.getContext());
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
                                playVideo(nwinput.getText().toString()
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
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(mCurrentSelectedPosition==R.id.nav_videos){
            navigationView.setCheckedItem(R.id.nav_videos);
        }

        loadFragment(mCurrentSelectedPosition);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // videos-related task you need to do.
                    if (mCurrentSelectedPosition == R.id.nav_videos) {
                        loadFragment(mCurrentSelectedPosition);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (mCurrentSelectedPosition == R.id.nav_videos) {
                        loadFragment(mCurrentSelectedPosition);
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mCurrentSelectedPosition = id;
        loadFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onSectionAttached(int number) {
        String titles[] = new String[3];
        titles[0] = getString(R.string.nav_local_videos);
        titles[1] = getString(R.string.nav_torrent_stream);
        titles[2] = getString(R.string.nav_live_tv);
        mTitle = titles[number];
    }

    public void onVideosFragmentInteraction(String id) {
        playVideo(id);
    }

    public void onLiveTVFragmentInteraction(String id) {
        playVideo(id);
    }

    public void onTorrentsFragmentInteraction(String id) {
        playVideo(id);
    }

    public void restoreActionBar() {
        MainActivity.this.setTitle(mTitle);
//        toolbar.setTitle(mTitle);
    }

    private void playVideo(String id) {
        Intent intent = new Intent(MainActivity.this,
                VideoPlayerActivity.class);
        intent.putExtra("EXTRA_URL", id);
        startActivity(intent);
    }

    private void loadFragment(int fragmentID) {
        if(MainActivity.permissionsSnackbar != null){
            MainActivity.permissionsSnackbar.dismiss();
        }
        Fragment fragment = null;
        switch (fragmentID) {
            case R.id.nav_videos:
                fragment = VideosFragment.newInstance();
                switchFragment(fragment);
                mTitle = getString(R.string.nav_local_videos);
                restoreActionBar();
                break;
            case R.id.nav_torrent_stream:
                fragment = TorrentsFragment.newInstance();
                switchFragment(fragment);
                SharedPreferences sp = this.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                mUserLearnedTorrents = sp.getBoolean(PREF_USER_LEARNED_TORRENT, false);
                if (mUserLearnedTorrents == false) {
                    Intent intent = new Intent(this,
                            TorrentsHelpActivity.class);
                    startActivity(intent);
                }
                mTitle = getString(R.string.nav_torrent_stream);
                restoreActionBar();
                break;
            case R.id.nav_live_tv:
                fragment = LiveTVFragment.newInstance();
                switchFragment(fragment);
                mTitle = getString(R.string.nav_live_tv);
                restoreActionBar();
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_message));
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_title));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser)));
                break;
            case R.id.nav_website:
                Uri webpage = Uri.parse(getString(R.string.website_link));
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            default:
                break;
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}