package com.yashketkar.ykplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    String httpLiveUrl;
    VideoView videoView;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideUI();

        // Create a progressbar
        pDialog = new ProgressDialog(VideoPlayerActivity.this);
        // Set progressbar title
        pDialog.setTitle(getString(R.string.progress_dialog_title));
        // Set progressbar message
        pDialog.setMessage(getString(R.string.progress_dialog_message));
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        setContentView(R.layout.activity_videoplayer);

        Intent intent = getIntent();
        httpLiveUrl = intent.getStringExtra("EXTRA_URL");
        videoView = (VideoView) findViewById(R.id.VideoView);
        videoView.setVideoURI(Uri.parse(httpLiveUrl));
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoView.start();
                hideUI();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            // Close the progress bar and play the video
            public boolean onError(MediaPlayer mp, int x, int y) {
                // if (y == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {
                pDialog.dismiss();
                // }
                return false;
            }
        });

        videoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    //hideUI();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_player, menu);
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

    public void hideUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                // set immersive mode sticky
                            }
                        }
                    });
        }
    }
}
