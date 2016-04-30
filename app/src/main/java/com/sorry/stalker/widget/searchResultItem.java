package com.sorry.stalker.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;
import com.sorry.stalker.activity.MainActivity;
import com.sorry.stalker.tools.FastBlur;
import com.sorry.stalker.tools.UnitConversion;


public class searchResultItem extends LinearLayout{

    private ImageView image;
    private ImageView imageBlur;
    private TextView  name;
    private TextView engname;
    private TextView  infor;
    private TextView  detial;
    private LinearLayout first;
    private LinearLayout second;
    private LinearLayout scrollLayout;
    private RelativeLayout outLayout;
    private Button checkBoxButton;
    private ObservableHorizontalScrollView scrollView;
    private FrameLayout relayout;
    private boolean isSelected;
    private Triangle triangle;
    private DisplayMetrics dm;
    final ScaleAnimation selectScaleAnimation;
    final AlphaAnimation selectAlphaAnimation;
    final AnimationSet selectAnimation;
    final ScaleAnimation selectImgScaleAnimation;

    public searchResultItem(Context context,int height) {
        this(context, height, null);
    }

    public searchResultItem(final Context context, int height, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局

        LayoutInflater.from(context).inflate(R.layout.search_result_item, this, true);
        image = (ImageView) findViewById(R.id.image);
        imageBlur = (ImageView) findViewById(R.id.imageBlur);
        name = (TextView) findViewById(R.id.showName);
        engname = (TextView) findViewById(R.id.showEngName);
        infor = (TextView) findViewById(R.id.showInfor);
        detial = (TextView) findViewById(R.id.showDetial);
        relayout = (FrameLayout) findViewById(R.id.resultItemLayout);
        outLayout = (RelativeLayout) findViewById(R.id.outLayout);
        outLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
        checkBoxButton = (Button) findViewById(R.id.checkBoxButton);
        isSelected = false;

        Typeface tf1 = Typeface.createFromAsset(getResources().getAssets(), "fonts/LithosPro-Regular.otf");
        Typeface tf2 = Typeface.createFromAsset(getResources().getAssets(), "fonts/MicrosoftYaHeiGB.ttf");
        this.setFont(4, tf1);
        this.setFont(2, tf1);



        selectScaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        selectScaleAnimation.setDuration(120);//设置动画持续时间
        selectScaleAnimation.setRepeatCount(1);//设置重复次数
        selectScaleAnimation.setFillAfter(true);
        selectScaleAnimation.setFillEnabled(true);
        selectScaleAnimation.setRepeatMode(Animation.REVERSE);
        selectAlphaAnimation = new AlphaAnimation(1f, 0.5f);
        selectAlphaAnimation.setDuration(120);//设置动画持续时间
        selectAlphaAnimation.setRepeatCount(1);//设置重复次数
        selectAlphaAnimation.setFillAfter(true);
        selectAlphaAnimation.setFillEnabled(true);
        selectAlphaAnimation.setRepeatMode(Animation.REVERSE);
        selectAnimation = new AnimationSet(true);
        selectAnimation.addAnimation(selectAlphaAnimation);
        selectAnimation.addAnimation(selectScaleAnimation);

        selectImgScaleAnimation = new ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        selectImgScaleAnimation.setDuration(120);//设置动画持续时间
        selectImgScaleAnimation.setRepeatCount(1);//设置重复次数
        selectImgScaleAnimation.setFillAfter(true);
        selectImgScaleAnimation.setFillEnabled(true);
        selectImgScaleAnimation.setRepeatMode(Animation.REVERSE);


        Resources resources = this.getResources();
        dm = resources.getDisplayMetrics();
         /*
        LinearLayout.LayoutParams newLp = new LinearLayout.LayoutParams(dm.widthPixels, UnitConversion.dip2px(this.getContext(),200));
        RelativeLayout.LayoutParams newRp = new RelativeLayout.LayoutParams(dm.widthPixels,UnitConversion.dip2px(this.getContext(),200));
        first.setLayoutParams(newLp);
        second.setLayoutParams(newLp);
        relayout.setLayoutParams(newRp);*/
        /*
        TextPaint nameTp = name.getPaint();
        nameTp.setFakeBoldText(true);
        TextPaint inforTp = infor.getPaint();
        inforTp.setFakeBoldText(true);绿箭侠
        TextPaint detialTp = detial.getPaint();
        detialTp.setFakeBoldText(true);*/
        checkBoxButton.setOnClickListener(checkBoxListener);

        image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(image.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    Drawable drawable = new BitmapDrawable(getResources(), FastBlur.doBlur(Bitmap.createBitmap(bitmap, 0, image.getHeight() - UnitConversion.dip2px(context, 220), image.getWidth(), UnitConversion.dip2px(context, 220), null, false), 80, false));

                    imageBlur.setBackground(drawable);
                }
            }
        });



    }

    private OnClickListener checkBoxListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isSelected) {
                isSelected = true;

                image.startAnimation(selectImgScaleAnimation);
            }
            else{
                isSelected = false;

                image.startAnimation(selectImgScaleAnimation);

            }
        }
    };

    public void setImageResource(int resId) {
        image.setImageResource(resId);
    }

    public ImageView getImageView(){
        return image;
    }

    public int getImgHeight(){
        return relayout.getHeight();
    }

    public int getImgWidth(){
        return relayout.getWidth();
    }
    public void setTextViewText(String text1, String text2, String text3, String text4) {
        name.setText(text1);
        infor.setText(text2);
        detial.setText(text3);
        engname.setText(text4);
    }


    public void setHeight(int height){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dm.widthPixels, height);
        relayout.setLayoutParams(lp);
    }

    public void setFont(int index, Typeface tf){
        switch (index){
            case 1: {name.setTypeface(tf);break;}
            case 2: {infor.setTypeface(tf);break;}
            case 3: {detial.setTypeface(tf);break;}
            case 4: {engname.setTypeface(tf);break;}
            default:break;
        }
    }

    public void setAllFont(Typeface tf){
        name.setTypeface(tf);
        engname.setTypeface(tf);
        infor.setTypeface(tf);
        detial.setTypeface(tf);
    }

    public boolean isSelected(){
        return isSelected;
    }



}