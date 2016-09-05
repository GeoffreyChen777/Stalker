
package com.sorry.stalker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sorry.stalker.R;
import com.sorry.stalker.tools.ConverEncoding;
import com.sorry.stalker.widget.MyMediaController;
import com.sorry.stalker.widget.SubtitleItem;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayerActivity extends Activity {

    private String path = "";
    private String subtitle_path = "";
    private VideoView mVideoView;
    private TextView mSubtitleView;
    private long mPosition = 0;
    private int mVideoLayout = 0;
    private RelativeLayout controlLayout;
    private RelativeLayout volumeLayout;
    private RelativeLayout brightnessLayout;
    private RelativeLayout playControlLayout;
    private RelativeLayout titleLayout;
    private LinearLayout subtitleListScrollLayout;
    private RelativeLayout subtitleListLayout;
    private TextView volumeTextView;
    private TextView brightnessTextView;
    private TextView titleTextView;
    private ImageButton playPauseButton;
    private ImageButton subtitlelistButton;
    private AudioManager mAudioManager;
    private GestureDetector mGestureDetector;
    private MediaController mMediaController;
    private int mMaxVolume;
    /** 当前声音 */
    private int mVolume = -1;
    /** 当前亮度 */
    private float mBrightness = -1f;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_video_player);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        mSubtitleView = (TextView) findViewById(R.id.subtitle_view);
        volumeTextView = (TextView) findViewById(R.id.volumeTextView);
        brightnessTextView = (TextView) findViewById(R.id.brightnessTextView);

        controlLayout = (RelativeLayout) findViewById(R.id.controlLayout);
        volumeLayout = (RelativeLayout) findViewById(R.id.volumeLayout);
        brightnessLayout = (RelativeLayout) findViewById(R.id.brightnessLayout);

        subtitlelistButton = (ImageButton) findViewById(R.id.subtitlelistButton);
        subtitleListScrollLayout = (LinearLayout) findViewById(R.id.subtitleListScrollLayout);
        subtitleListLayout = (RelativeLayout) findViewById(R.id.subtitleListLayout);
        path = getIntent().getExtras().getString("videoPath");
        subtitle_path = getIntent().getExtras().getString("subtitlePath");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        mMediaController = new MediaController(this);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
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
                    //Log.i("subtitle_path",subtitle_path);


/*
                    String srcDir = subtitle_path.substring(0,subtitle_path.lastIndexOf("/"));
                    File file = new File(srcDir+ "/subtitleEncoded.srt");
                    if(!file.exists()) {
                        List<String> files = new ArrayList<String>();
                        ConverEncoding.fetchFileList(srcDir, files, FILE_SUFFIX);
                        String filecode = "";
                        int index = 0;
                        for (String fileName : files) {
                            index++;
                            try {
                                filecode = ConverEncoding.codeString(fileName);
                            }catch (Exception e){
                                System.out.print(e.getStackTrace());
                            }
                            if (!filecode.equals(CODE)) {
                                System.out.print("=="+fileName+"==");

                                ConverEncoding.convert(fileName, filecode, srcDir+ "/subtitleEncoded.srt" , CODE);
                            }
                        }
                    }


                    mVideoView.addTimedTextSource(srcDir+ "/subtitleEncoded.srt");
                    mVideoView.setTimedTextShown(true);
                    mVideoView.setTimedTextEncoding(null);
*/
                    Log.i("getSubTrackMap",mVideoView.getMetaEncoding());
                }
            });

            mVideoView.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {

                @Override
                public void onTimedText(String text) {
                    mSubtitleView.setText(text);
                }

                @Override
                public void onTimedTextUpdate(byte[] pixels, int width, int height) {
                }
            });

        }

        View.OnClickListener subtitleButton = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtitleListScrollLayout.removeAllViews();
                subtitleListLayout.setVisibility(View.VISIBLE);
                File f = new File(subtitle_path);
                File[] files = f.listFiles();
                for(int i = 0; i < files.length; i++){
                    if((files[i].getName().endsWith(".srt") || files[i].getName().endsWith(".ass")) && !files[i].getName().contains("subtitleEncoded")) {
                        SubtitleItem subtitle = new SubtitleItem(VideoPlayerActivity.this);
                        StringBuffer result = new StringBuffer();
                        for(int j = 0;j<files[i].getName().length();j++){
                            char a = files[i].getName().charAt(j);
                            if((((int)a)>=65&&((int)a)<=90)||(((int)a)>=97&&((int)a)<=122)||(((int)a)>=48&&((int)a)<=57)||a==' '||a=='.'){
                                result.append(a);
                            }
                        }
                        final String subtitlePath = files[i].getAbsolutePath();
                        final int index = i;
                        subtitle.setSubtitleTextView(result.toString());
                        subtitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String CODE = "UTF-8";
                                String FILE_SUFFIX = subtitlePath.substring(subtitlePath.length()-4,subtitlePath.length());
                                List<String> files = new ArrayList<String>();
                                //ConverEncoding.fetchFileList(subtitle_path, files, FILE_SUFFIX);
                                String filecode = "";
                                try {
                                    filecode = ConverEncoding.codeString(subtitlePath);
                                }catch (Exception e){
                                    System.out.print(e.getStackTrace());
                                }
                                if (!filecode.equals(CODE)) {
                                    try {
                                        ConverEncoding.convert(subtitlePath, filecode, subtitle_path + "/subtitleEncoded/subtitleEncoded" + index + FILE_SUFFIX, CODE);
                                        mVideoView.addTimedTextSource(subtitle_path+ "/subtitleEncoded/subtitleEncoded"+index+FILE_SUFFIX);
                                    }catch (Exception e){
                                        System.out.print(e.getStackTrace());
                                    }
                                }
                                else{
                                    mVideoView.addTimedTextSource(subtitlePath);
                                }



                                mVideoView.setTimedTextShown(true);
                                mVideoView.setTimedTextEncoding(null);

                            }
                        });
                        subtitleListScrollLayout.addView(subtitle);
                    }
                }
            }
        };
        MyMediaController mc = new MyMediaController(VideoPlayerActivity.this, mVideoView,VideoPlayerActivity.this, subtitleButton);
        mVideoView.setMediaController(mc);

    }

    @Override
    protected void onPause() {
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mPosition > 0) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                subtitleListLayout.setVisibility(View.INVISIBLE);
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /** 手势结束 */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 1500);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {



        /** 滑动 */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowHeight = disp.getWidth();
            int windowWidth = disp.getHeight();

            if (mOldX > windowWidth * 3/ 5)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth * 2/ 5)// 左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /** 定时隐藏 */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            controlLayout.setVisibility(View.INVISIBLE);
            volumeLayout.setVisibility(View.INVISIBLE);
            brightnessLayout.setVisibility(View.INVISIBLE);

        }
    };

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;


            controlLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        volumeLayout.setVisibility(View.VISIBLE);
        volumeTextView.setVisibility(View.VISIBLE);
        volumeTextView.setText((int)(((double)index/mMaxVolume)*100)+"");

    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;


            controlLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        brightnessLayout.setVisibility(View.VISIBLE);
        brightnessTextView.setVisibility(View.VISIBLE);
        brightnessTextView.setText((int)(lpa.screenBrightness*100)+"");
    }
}
