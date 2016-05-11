package com.sorry.stalker.tools;

import android.text.Editable;
import android.text.TextWatcher;

import com.sorry.stalker.widget.MainItem;

/**
 * Created by sorry on 2016/5/10.
 */
public class TextWatcherWithItem implements TextWatcher {
    private MainItem item;
    public TextWatcherWithItem(MainItem item){
        super();
        this.item = item;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public MainItem getItem(){
        return item;
    }
}
