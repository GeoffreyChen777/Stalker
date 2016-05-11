package com.sorry.stalker.widget;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.sorry.stalker.R;


public class CustomPtrHeader extends FrameLayout implements PtrUIHandler {
    public CustomPtrHeader(Context context) {
        super(context);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    public CustomPtrHeader(Context context, AttributeSet attrs,
                           int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_ptr_header, this);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch,
                                   byte status, PtrIndicator ptrIndicator) {
        float percent = Math.min(1f, ptrIndicator.getCurrentPercent());
        invalidate();

    }


}