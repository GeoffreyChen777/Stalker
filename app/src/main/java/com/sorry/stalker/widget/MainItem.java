package com.sorry.stalker.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.sorry.stalker.R;
import com.sorry.stalker.activity.MainActivity;
import com.sorry.stalker.datastructure.ShowsInfor;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

/**
 * Created by sorry on 2016/5/8.
 */
public class MainItem extends RelativeLayout {
    public TextView name;
    private TextView engName;
    public TextView status;
    public TextView dayNum;
    public ShowsInfor showInfor;
    private RelativeLayout optionLayout;
    private RelativeLayout mainLayout;
    protected final int UPDATETIME = 0x01;
    protected final int UPDATEUI = 0x02;
    final ScaleAnimation optionScaleAnimation;
    final AlphaAnimation optionAlphaAnimation;
    final AnimationSet optionAnimation;
    final AnimationSet optionAnimation2;
    private Context context;
    public Button starButton;
    public Button deleteButton;
    private OkHttpClient mClient;

    public MainItem(Context context) {
        this(context, null);
    }

    public MainItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.main_item, this, true);
        name = (TextView) findViewById(R.id.mainName);
        engName = (TextView) findViewById(R.id.mainEngName);
        status = (TextView) findViewById(R.id.mainShowStatus);
        dayNum = (TextView) findViewById(R.id.mainDayNum);
        starButton = (Button) findViewById(R.id.mainStar);
        deleteButton = (Button) findViewById(R.id.mainDelete);
        //dayNum.setShadowLayer(5F, 20F,20F, getResources().getColor(R.color.colorImageMask));
        Typeface tf1 = Typeface.createFromAsset(getResources().getAssets(), "fonts/PingFang-SC-UltraLight.ttf");
        dayNum.setTypeface(tf1);


        optionLayout = (RelativeLayout) findViewById(R.id.mainOptionLayout);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        this.context = context;
        showInfor = null;
        mainLayout.setOnLongClickListener(optionListener);
        optionLayout.setOnClickListener(hideOptionListener);

        optionScaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        optionScaleAnimation.setDuration(80);//设置动画持续时间
        optionScaleAnimation.setRepeatCount(1);//设置重复次数
        optionScaleAnimation.setRepeatMode(Animation.REVERSE);
        optionAlphaAnimation = new AlphaAnimation(1f, 0.5f);
        optionAlphaAnimation.setDuration(80);//设置动画持续时间
        optionAlphaAnimation.setRepeatCount(1);//设置重复次数
        optionAlphaAnimation.setRepeatMode(Animation.REVERSE);
        optionAnimation = new AnimationSet(true);
        optionAnimation2 = new AnimationSet(true);
        optionAnimation.addAnimation(optionScaleAnimation);
        optionAnimation.addAnimation(optionAlphaAnimation);
        optionAnimation2.addAnimation(optionScaleAnimation);
        optionAnimation2.addAnimation(optionAlphaAnimation);
    }

    private OnLongClickListener optionListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Vibrator vibrator = (Vibrator) context.getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(70);
            optionLayout.setVisibility(View.VISIBLE);
            starButton.startAnimation(optionAnimation);
            deleteButton.startAnimation(optionAnimation2);
            return false;
        }
    };

    private OnClickListener hideOptionListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AlphaAnimation hideOptionAlphaAnimation = new AlphaAnimation(1f, 0f);
            hideOptionAlphaAnimation.setDuration(120);//设置动画持续时间
            hideOptionAlphaAnimation.setRepeatCount(0);//设置重复次数
            optionLayout.startAnimation(hideOptionAlphaAnimation);
            optionLayout.setVisibility(View.INVISIBLE);
        }
    };

    public MainItem setName(String name){
        this.name.setText(name);
        return MainItem.this;
    }

    public MainItem setShowInfor(ShowsInfor showInfor){
        this.showInfor = showInfor;
        return MainItem.this;
    }

    public MainItem setEngName(String engName){
        this.engName.setText(engName);
        return MainItem.this;
    }

    public MainItem setStatus(String status){
        this.status.setText(status);
        return MainItem.this;
    }



    public MainItem setDayNum(String dayNum){
        this.dayNum.setText(dayNum);
        return MainItem.this;
    }

    public ImageView getImageView(){
        return (ImageView) findViewById(R.id.mainImage);
    }

    public void getDataFromServer(OkHttpClient mClient){
        this.mClient = mClient;
        Request request = new Request.Builder().url("http://api.tvmaze.com/lookup/shows?imdb=" + this.showInfor.ID)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Stalker", "error");
                Toast.makeText(MainItem.this.getContext(), "请检查网络连接", Toast.LENGTH_SHORT);
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();

                    JSONObject responseJson = JSONObject.parseObject(responseStr);
                    showInfor.mazeID = responseJson.getString("id");

                    showInfor.airDate = responseJson.getJSONObject("schedule").getJSONArray("days").get(0).toString();
                    final Calendar c = Calendar.getInstance();
                    c.setTimeZone(TimeZone.getTimeZone("GMT-4"));
                    int SmWay = c.get(Calendar.DAY_OF_WEEK);

                    int airdate;
                    if("Sunday".equals(showInfor.airDate)){
                        airdate = 2;
                    }else if("Monday".equals(showInfor.airDate)){
                        airdate = 3;
                    }else if("Tuesday".equals(showInfor.airDate)){
                        airdate = 4;
                    }else if("Wednesday".equals(showInfor.airDate)){
                        airdate = 5;
                    }else if("Thursday".equals(showInfor.airDate)){
                        airdate = 6;
                    }else if("Friday".equals(showInfor.airDate)){
                        airdate = 7;
                    }else{
                        airdate = 1;
                    }

                    if(SmWay <= airdate){
                        showInfor.airDate = String.valueOf(airdate-SmWay);
                    }
                    else{
                        showInfor.airDate = String.valueOf(7 + airdate - SmWay);
                    }

                    Message message = new Message();
                    message.what = UPDATETIME;
                    message.obj = responseJson.getJSONObject("_links").getJSONObject("previousepisode").getString("href");
                    myHandler.sendMessage(message);
                }
            }
        });

    }

    public void UpdateTime(String url){
        Request request = new Request.Builder().url(url)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Stalker", "error");
                Toast.makeText(MainItem.this.getContext(), "请检查网络连接", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    JSONObject responseJson = JSONObject.parseObject(responseStr);
                    showInfor.airedSeason = responseJson.getString("season");
                    showInfor.airedEpisodeNumber = responseJson.getString("number");
                    Message message = new Message();
                    message.what = UPDATEUI;
                    myHandler.sendMessage(message);
                }
            }
        });
    }

    public void sendIDToServer(String mazeID){
        Request request = new Request.Builder().url("http://115.159.29.107/stalker_maze.php?id=" + mazeID)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    private void UpdateUI(){
        if(this.showInfor.status.equals("returning series")) {
            this.status.setText("returning series "+"S" + String.format("%02d", Integer.valueOf(showInfor.airedSeason)) + " E" + String.format("%02d", Integer.valueOf(showInfor.airedEpisodeNumber)));
        }
        else{
            this.showInfor.airDate = "7";
            this.status.setText(showInfor.status);
        }

    }

    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //判断发送的消息
                case UPDATETIME: {
                    UpdateTime(msg.obj.toString());
                    break;
                }
                case UPDATEUI:{
                    UpdateUI();
                    sendIDToServer(showInfor.mazeID);
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };
}
