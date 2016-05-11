package com.sorry.stalker.tools;

import android.view.View;

import com.sorry.stalker.widget.MainItem;

/**
 * Created by sorry on 2016/5/10.
 */
public class OnClickListenerWithItem implements View.OnClickListener{

    private MainItem item;

    public OnClickListenerWithItem(MainItem item){
        super();
        this.item = item;
    }

    @Override
    public void onClick(View v) {

    }

    public MainItem getItem(){
        return item;
    }
}
