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
    private ImageButton checkBoxButton;
    private ObservableHorizontalScrollView scrollView;
    private FrameLayout relayout;
    private boolean isSelected;
    private Triangle triangle;
    private DisplayMetrics dm;
    final ScaleAnimation selectScaleAnimation;
    final AlphaAnimation selectAlphaAnimation;
    final AnimationSet selectAnimation;
    final ScaleAnimation selectImgScaleAnimation;

    public searchResultItem(Context context) {
        this(context, null);
    }

    public searchResultItem(Context context, AttributeSet attrs) {
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

        checkBoxButton = (ImageButton) findViewById(R.id.checkBoxButton);
        isSelected = false;

        Typeface tf1 = Typeface.createFromAsset(getResources().getAssets(), "fonts/HanHeiSC-Thin.otf");
        Typeface tf2 = Typeface.createFromAsset(getResources().getAssets(), "fonts/MicrosoftYaHeiGB.ttf");
        Typeface tf3 = Typeface.createFromAsset(getResources().getAssets(), "fonts/HanHeiSC-Thin.otf");
        //this.setFont(1, tf2);
        this.setFont(4, tf1);
        this.setFont(2, tf1);
        this.setFont(3, tf3);


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
        image.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void  onDraw() {
                Log.i("Img","Pre");
                Bitmap bmp = loadBitmapFromView(image);
                if(bmp == null)
                    Log.i("IMG","Null");
                imageBlur.setImageBitmap(FastBlur.doBlur(bmp,20,true));
            }
        });


    }
    public static Bitmap loadBitmapFromView(View v)
    {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }


    static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    private OnClickListener checkBoxListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isSelected) {
                isSelected = true;
                checkBoxButton.setImageResource(R.mipmap.ic_checkbox);
                image.startAnimation(selectImgScaleAnimation);
            }
            else{
                isSelected = false;
                checkBoxButton.setImageResource(R.mipmap.ic_checkbox_uncheck);
                image.startAnimation(selectImgScaleAnimation);

            }
        }
    };

    private void applyBlur() {
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                image.setDrawingCacheEnabled(true);
                image.buildDrawingCache();
                Log.i("Img","Pre");
                Bitmap bmp = image.getDrawingCache();
                blur(bmp, image);
                return true;
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg, View view) {
        float radius = 20;
        float scaleFactor = 1;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        if(bkg == null)
            Log.i("Img","null");
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int)radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
    }

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