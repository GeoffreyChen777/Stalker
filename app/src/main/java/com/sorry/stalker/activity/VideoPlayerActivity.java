package com.sorry.stalker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sorry.stalker.R;

import org.w3c.dom.Text;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import android.view.SurfaceHolder.Callback;

import java.io.IOException;

/**
 * Created by sorry on 2016/5/19.
 */
public class VideoPlayerActivity extends Activity{

    private String path;
    private String subtitlePath;
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_video_player);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        path = getIntent().getExtras().getString("videoPath");
        subtitlePath = getIntent().getExtras().getString("subtitlePath");
        mVideoView.setVideoPath(path);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.addTimedTextSource(subtitlePath);
                mVideoView.setTimedTextShown(true);
                mVideoView.setSubTrack(MediaPlayer.SUBTITLE_EXTERNAL);
                mVideoView.setTimedTextEncoding(null);
                mVideoView.start();
            }
        });
        mVideoView.setOnTimedTextListener(new OnTimedTextListener() {
            @Override
            public void onTimedText(String text) {
                Log.i("==subtitle==",text);
            }

            @Override
            public void onTimedTextUpdate(byte[] pixels, int width, int height) {
                Log.i("==subtitle==","Update");
            }
        });
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
    }

    private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }

}
