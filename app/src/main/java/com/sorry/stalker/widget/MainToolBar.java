package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/1.
 */
public class MainToolBar extends RelativeLayout {

    private RelativeLayout backgroundLayout;
    private TextView textView;
    private int count = 0;
    private long firClick = 0;
    private long secClick = 0;
    public MainToolBar(Context context) {
        this(context, null);
    }

    public MainToolBar(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局

        LayoutInflater.from(context).inflate(R.layout.main_toolbar, this, true);
        textView = (TextView) findViewById(R.id.TitleTextview);
    }

    public void ScrolltoTop(final ScrollView scrollView){
        textView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(MotionEvent.ACTION_DOWN == event.getAction()){
                    count++;
                    if(count == 1){
                        firClick = System.currentTimeMillis();
                    } else if (count == 2){
                        secClick = System.currentTimeMillis();
                        if(secClick - firClick < 1000){
                            scrollView.smoothScrollTo(0,0);

                        }
                        count = 0;
                        firClick = 0;
                        secClick = 0;

                    }
                }
                return false;
            }
        });

    }
}
