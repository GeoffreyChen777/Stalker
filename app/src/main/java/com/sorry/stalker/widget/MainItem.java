package com.sorry.stalker.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sorry.stalker.R;
import com.sorry.stalker.activity.MainActivity;
import com.sorry.stalker.activity.SearchResultActivity;
import com.sorry.stalker.activity.ShowMainActivity;
import com.sorry.stalker.datastructure.ShowsInfor;
import com.sorry.stalker.tools.TransBitmap;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

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
    private Map<String,String> genres;
    private Picasso picasso;

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

        initGenres();

        optionLayout = (RelativeLayout) findViewById(R.id.mainOptionLayout);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        this.context = context;
        picasso = new Picasso.Builder(context).build();
        showInfor = null;
        mainLayout.setOnLongClickListener(optionListener);
        mainLayout.setOnClickListener(openShowListener);
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

    private void initGenres(){
        genres = new HashMap<>();
        genres.put("Action","动作");
        genres.put("Adult","成人");
        genres.put("Adventure","冒险");
        genres.put("Animals","动物");
        genres.put("Anime","动漫");
        genres.put("Children","儿童");
        genres.put("Comedy","喜剧");
        genres.put("Cooking","厨艺");
        genres.put("Crime","犯罪");
        genres.put("DIY","DIY");
        genres.put("Drama","剧情");
        genres.put("Espionage","谍战");
        genres.put("Family","家庭");
        genres.put("Fantasy","幻想");
        genres.put("History","历史");
        genres.put("Horror","恐怖");
        genres.put("Medical","医学");
        genres.put("Music","音乐");
        genres.put("Mystery","悬疑");
        genres.put("Romance","浪漫");
        genres.put("Science-Fiction","科幻");
        genres.put("Thriller","惊悚");
        genres.put("Travel","旅行");
        genres.put("War","战争");
        genres.put("Western","西部");
    }

    private OnLongClickListener optionListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Vibrator vibrator = (Vibrator) context.getSystemService(MainActivity.VIBRATOR_SERVICE);
            vibrator.vibrate(70);
            optionLayout.setVisibility(View.VISIBLE);
            starButton.startAnimation(optionAnimation);
            deleteButton.startAnimation(optionAnimation2);
            return true;
        }
    };

    private OnClickListener openShowListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ShowMainActivity.class);
            intent.putExtra("ShowInfor",(Serializable) showInfor);
            context.startActivity(intent);
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
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();

                    JSONObject responseJson = JSONObject.parseObject(responseStr);
                    showInfor.mazeID = responseJson.getString("id");

                    String genresStr = responseJson.getString("genres").replaceAll("\"","");
                    genresStr = genresStr.substring(1, genresStr.length()-1);
                    String[] genresArray = genresStr.split(",");
                    genresStr = "";
                    for(int i =0; i < genresArray.length; i++){
                        genresStr = genresStr + genres.get(genresArray[i]) + "/";
                    }
                    genresStr = genresStr.substring(0, genresStr.length()-1);
                    showInfor.genres = genresStr;
                    showInfor.rank = responseJson.getJSONObject("rating").getString("average");
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

    public void updateCastandPoster(){
        Request request = new Request.Builder().url("http://api.tvmaze.com/shows/" + showInfor.mazeID + "?embed[]=episodes&embed[]=cast")
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Stalker", "error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    JSONObject responseJson = JSONObject.parseObject(responseStr);
                    JSONArray castJsonArray = responseJson.getJSONObject("_embedded").getJSONArray("cast");
                    JSONArray episodesJsonArray = responseJson.getJSONObject("_embedded").getJSONArray("episodes");

                    String cast = "";
                    for(int i = 0; i < castJsonArray.size(); i++){
                        cast = cast + castJsonArray.getJSONObject(i).getJSONObject("person").getString("name").replace("\"","") + " / ";
                    }
                    showInfor.casts = cast.substring(0, cast.length()-3);
                    for(int i = episodesJsonArray.size()-1; i >= 0; i--){
                        if(episodesJsonArray.getJSONObject(i).getString("season").equals(showInfor.airedSeason)
                                && episodesJsonArray.getJSONObject(i).getString("number").equals(showInfor.airedEpisodeNumber)){
                            showInfor.updateEpisodeInfor[0].Season = showInfor.airedSeason;
                            showInfor.updateEpisodeInfor[0].Episode = showInfor.airedEpisodeNumber;
                            showInfor.updateEpisodeInfor[0].name = episodesJsonArray.getJSONObject(i).getString("name");
                            if(i >= 1){
                                showInfor.updateEpisodeInfor[1].Season = episodesJsonArray.getJSONObject(i-1).getString("season");
                                showInfor.updateEpisodeInfor[1].Episode = episodesJsonArray.getJSONObject(i-1).getString("number");
                                showInfor.updateEpisodeInfor[1].name = episodesJsonArray.getJSONObject(i-1).getString("name");
                            }
                            if(i >= 2){
                                showInfor.updateEpisodeInfor[2].Season = episodesJsonArray.getJSONObject(i-2).getString("season");
                                showInfor.updateEpisodeInfor[2].Episode = episodesJsonArray.getJSONObject(i-2).getString("number");
                                showInfor.updateEpisodeInfor[2].name = episodesJsonArray.getJSONObject(i-2).getString("name");
                            }
                        }
                    }
                    showInfor.ifHasAllInfor = true;

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
                    updateCastandPoster();
                    UpdateUI();
                    //sendIDToServer(showInfor.mazeID);
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };
}
