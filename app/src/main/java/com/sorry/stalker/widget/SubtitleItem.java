package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sorry on 2016/5/29.
 */
public class SubtitleItem extends LinearLayout{

    private TextView subtitleTextView;
    private LinearLayout subtitleItemLayout;
    public String subtitleID;
    private OkHttpClient mClient;

    public SubtitleItem(Context context) {
        this(context, null);
    }

    public SubtitleItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.subtitle_item, this, true);
        subtitleTextView = (TextView) findViewById(R.id.subtitleTextView);
        subtitleItemLayout = (LinearLayout) findViewById(R.id.subtitleItemLayout);
        subtitleID = "";
        mClient = new OkHttpClient();

    }

    public LinearLayout getLayout(){
        return subtitleItemLayout;
    }

    public void setSubtitleTextView(String name){
        subtitleTextView.setText(name);
    }


}
