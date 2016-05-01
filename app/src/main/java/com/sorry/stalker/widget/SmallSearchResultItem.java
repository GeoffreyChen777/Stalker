package com.sorry.stalker.widget;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;
import com.sorry.stalker.tools.UnitConversion;

public class SmallSearchResultItem extends RelativeLayout {
    private TextView name;
    private TextView engName;
    private TextView infor;
    private TextView detial;
    private TopCropImageView image;
    private Button addButton;
    private ImageButton showDetialButton;
    private RelativeLayout nameLayout;
    private RelativeLayout firstPage;
    private LinearLayout secondPage;
    private ObservableHorizontalScrollView scrollView;
    private DisplayMetrics dm;
    private boolean isSelected;

    public SmallSearchResultItem(Context context) {
        this(context, null);
    }

    public SmallSearchResultItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.search_result_item_samll, this, true);
        name = (TextView) findViewById(R.id.small_showName);
        engName = (TextView) findViewById(R.id.small_showEngName);
        infor = (TextView) findViewById(R.id.small_showInfor);
        detial = (TextView) findViewById(R.id.small_showDetial);
        image = (TopCropImageView) findViewById(R.id.small_image);
        addButton = (Button) findViewById(R.id.small_addButton);
        firstPage = (RelativeLayout) findViewById(R.id.firstView);
        secondPage = (LinearLayout) findViewById(R.id.secondView);
        showDetialButton = (ImageButton) findViewById(R.id.showDetialButton);
        scrollView = (ObservableHorizontalScrollView) findViewById(R.id.hscrollView);
        isSelected = false;
        addButton.setOnClickListener(addListener);
        Resources resources = this.getResources();
        dm = resources.getDisplayMetrics();
        LinearLayout.LayoutParams newLp = new LinearLayout.LayoutParams(dm.widthPixels - UnitConversion.dip2px(this.getContext(),16), UnitConversion.dip2px(this.getContext(),190));
        firstPage.setLayoutParams(newLp);
        secondPage.setLayoutParams(newLp);
        showDetialButton.setOnClickListener(showDetialListener);
        scrollView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.startScrollerTask();
                }



                return false;
            }
        });
        scrollView.setOnScrollStoppedListener(new ObservableHorizontalScrollView.OnScrollStoppedListener() {

            public void onScrollStopped() {
                if(scrollView.getScrollX() <= scrollView.getWidth()/2) {
                    scrollView.smoothScrollTo(0, 0);
                }
                else {
                    scrollView.smoothScrollTo(scrollView.getWidth(), 0);
                }


            }
        });

        Typeface tf1 = Typeface.createFromAsset(getResources().getAssets(), "fonts/LithosPro-Regular.otf");
        engName.setTypeface(tf1);
        infor.setTypeface(tf1);
    }

    OnClickListener showDetialListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            scrollView.smoothScrollTo(scrollView.getWidth(), 0);
        }
    };

    OnClickListener addListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isSelected){
                isSelected = false;
            }
            else{
                isSelected = true;
            }
        }
    };

    public boolean isSelected(){
        return isSelected;
    }

    public SmallSearchResultItem setName(String name){
        this.name.setText(name);
        this.engName.setText(name);
        return this;
    }
    public SmallSearchResultItem setEngName(String engName){
        if(!engName.equals(""))
            this.engName.setText(engName);
        return this;
    }
    public SmallSearchResultItem setInfor(String infor){
        this.infor.setText(infor);
        return this;
    }

    public SmallSearchResultItem setDetial(String detial){
        this.detial.setText(detial);
        return this;
    }

    public SmallSearchResultItem setImage(Bitmap bmp){
        this.image.setImageBitmap(bmp);
        return this;
    }

}
