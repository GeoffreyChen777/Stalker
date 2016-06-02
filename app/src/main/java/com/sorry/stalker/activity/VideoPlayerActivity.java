
package com.sorry.stalker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sorry.stalker.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayerActivity extends Activity {

    private String path = "";
    private String subtitle_path = "";
    private VideoView mVideoView;
    private TextView mSubtitleView;
    private long mPosition = 0;
    private int mVideoLayout = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_video_player);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mSubtitleView = (TextView) findViewById(R.id.subtitle_view);
        path = getIntent().getExtras().getString("videoPath");
        subtitle_path = getIntent().getExtras().getString("subtitlePath");

        if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(VideoPlayerActivity.this, "Please edit VideoViewSubtitle Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
            return;
        } else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */

            mVideoView.setVideoPath(path);
            // mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    Log.i("==prepare==","!");
                    mediaPlayer.setPlaybackSpeed(1.0f);
                    mVideoView.setSubTrack(MediaPlayer.SUBTITLE_EXTERNAL);
                    mVideoView.addTimedTextSource("/storage/emulated/0/Stalker/subtitle/Arrow S04E22/Arrow.S04E22.srt");
                    mVideoView.setTimedTextShown(true);
                    mVideoView.setTimedTextEncoding(null);
                    Log.i("getSubTrackMap",mVideoView.getTimedTextTrack()+mVideoView.getTimedTextPath());
                }
            });

            mVideoView.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {

                @Override
                public void onTimedText(String text) {
                    Log.i("==Subtitle==",text);
                }

                @Override
                public void onTimedTextUpdate(byte[] pixels, int width, int height) {
                    Log.i("==Subtitle==","!");
                }
            });

        }

        String str = null==icicle?"1":"2";
        Log.i("", "onCreate"+str);
    }
    private int findTrackIndexFor(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfo) {
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    @Override
    protected void onPause() {
        Log.i("", "onPause"+mPosition);
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i("", "onResume"+mPosition);
        if (mPosition > 0) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("", "onSaveInstanceState"+mPosition);
        outState.putLong("position", mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("", "onRestoreInstanceState");
        if(null!=savedInstanceState){
            mPosition = savedInstanceState.getLong("position",0);
            Log.i("", "onRestoreInstanceState"+mPosition);
            if (mPosition > 0) {
                mVideoView.seekTo(mPosition);
                mPosition = 0;
            }
            mVideoView.start();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void changeLayout(View view) {
        mVideoLayout++;
        if (mVideoLayout == 4) {
            mVideoLayout = 0;
        }
        switch (mVideoLayout) {
            case 0:
                mVideoLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
                view.setBackgroundResource(R.drawable.mediacontroller_sreen_size_100);
                break;
            case 1:
                mVideoLayout = VideoView.VIDEO_LAYOUT_SCALE;
                view.setBackgroundResource(R.drawable.mediacontroller_screen_fit);
                break;
            case 2:
                mVideoLayout = VideoView.VIDEO_LAYOUT_STRETCH;
                view.setBackgroundResource(R.drawable.mediacontroller_screen_size);
                break;
            case 3:
                mVideoLayout = VideoView.VIDEO_LAYOUT_ZOOM;
                view.setBackgroundResource(R.drawable.mediacontroller_sreen_size_crop);

                break;
        }
        mVideoView.setVideoLayout(mVideoLayout, 0);
    }

}
