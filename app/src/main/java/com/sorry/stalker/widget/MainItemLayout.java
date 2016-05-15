package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/12.
 */

public class MainItemLayout extends LinearLayout{

    private LinearLayout mainItemLayout;
    private RelativeLayout dayViewLayout;
    private TextView dayView;
    public int dayNum;

    public MainItemLayout(Context context) {
        this(context, null);
    }

    public MainItemLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.main_item_layout, this, true);
        mainItemLayout = (LinearLayout) findViewById(R.id.mainItemLayout);
        dayViewLayout = (RelativeLayout) findViewById(R.id.dayViewLayout);
        dayView = (TextView) findViewById(R.id.dayView);
    }

    public void setDayView(String str){
        this.dayView.setText(str);
    }

    public void setDayNum(String dayNum){
        this.setDayView(dayNum);
    }

    public void addView(MainItem item){
        mainItemLayout.addView(item);
    }

    public void showDayViewLayout(boolean bool){
        if(bool) {
            this.dayViewLayout.setVisibility(View.VISIBLE);
        }
        else{
            this.dayViewLayout.setVisibility(View.GONE);
        }
    }

    public void remove(MainItem item){
        mainItemLayout.removeView(item);
        if(mainItemLayout.getChildCount() == 1){
            showDayViewLayout(false);
        }
    }

}
