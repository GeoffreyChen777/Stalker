package com.sorry.stalker.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sorry.stalker.R;
import com.sorry.stalker.datastructure.ShowsInfor;
import com.sorry.stalker.tools.UnitConversion;
import com.sorry.stalker.widget.searchResultItem;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultActivity extends AppCompatActivity {

    private String searchText;
    private OkHttpClient mClient;
    private ImageButton backButton;
    private ImageButton confirmButton;
    private ScrollView searchResltList;
    private List searchResultInforList;
    private LinearLayout liLayout;
    private LinearLayout searchResultActivityLayout;
    private HashMap nameMap;
    private int flag;
    private SpinKitView loadingAnimation;
    protected static final int GUIUPDATEIDENTIFIER = 0x101;
    protected static final int UPDATEIDENTIFIER = 0x102;
    protected static final int NORESULTIDENTIFIER = 0x103;
    protected static final int UPDATESINGLEIDENTIFIER = 0x104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mClient = new OkHttpClient();
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchResultToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        backButton = (ImageButton) findViewById(R.id.backButton);
        confirmButton = (ImageButton) findViewById(R.id.confirmButton);
        searchResltList = (ScrollView) findViewById(R.id.searchResltList);
        loadingAnimation = (SpinKitView) findViewById(R.id.loadingAnimation);
        liLayout = (LinearLayout) findViewById(R.id.searchResltListLayout);
        searchResultActivityLayout = (LinearLayout) findViewById(R.id.searchResltActivityLayout);
        searchResultInforList = new ArrayList();
        flag = 0;
        nameMap = new HashMap();
        backButton.setOnClickListener(toolBarClickListener);
        confirmButton.setOnClickListener(toolBarClickListener);
        TextView titleView = (TextView) findViewById(R.id.searchResultTitleView);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/opensans.ttf");
        titleView.setTypeface(tf);
        setToolBarButtonSelector();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            searchText = extras.getString("searchText");
            getInforFromWeb(searchText);
        }

    }

    Handler myHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //判断发送的消息
                case GUIUPDATEIDENTIFIER: {
                    parseInfor(msg.obj.toString());
                    break;
                }
                case NORESULTIDENTIFIER:{
                    loadingAnimation.setVisibility(View.GONE);
                    break;
                }
                case UPDATESINGLEIDENTIFIER: {
                    loadingAnimation.setVisibility(View.GONE);
                    ShowsInfor infor = (ShowsInfor) msg.obj;
                    updateUI(0, infor.name, infor.engname, infor.status, infor.overview, infor.posterImgUrl);
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public void getInforFromWeb(String searchText){
        Request request = new Request.Builder()
                .url("https://api-v2launch.trakt.tv/search?query=" + searchText + "&type=show")
                .header("Content-Type","application/json")
                .addHeader("trakt-api-version","2")
                .addHeader("trakt-api-key","1eb6e36ce7199d857bbaefbef849c3eee049727e67da910c9b45a2269ae13089")
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Stalker","error");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i("Stalker","Unexpected code " + response);
                    throw new IOException("Unexpected code " + response);

                }
                if (response.isSuccessful()) {
                    Message message = new Message();
                    String responseStr = response.body().string();
                    if(!responseStr.equals("[]")) {
                        Log.i("Stalker","noresult");
                        message.what = GUIUPDATEIDENTIFIER;
                        message.obj = responseStr;
                        myHandler.sendMessage(message);
                    }
                    else{
                        message.what = NORESULTIDENTIFIER;
                        myHandler.sendMessage(message);
                    }

                }
            }
        });
    }

    private void setToolBarButtonSelector(){
        backButton.setOnTouchListener(new ImageButton.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    backButton.setImageResource(R.mipmap.ic_back_selected);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    backButton.setImageResource(R.mipmap.ic_back);
                }
                return false;
            }
        });
        confirmButton.setOnTouchListener(new ImageButton.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    confirmButton.setImageResource(R.mipmap.ic_confirm_selected);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    confirmButton.setImageResource(R.mipmap.ic_confirm);
                }
                return false;
            }
        });
    }

    View.OnClickListener toolBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.backButton:{
                    SearchResultActivity.this.finish();
                    break;
                }
                case R.id.confirmButton:{
                    break;
                }
                default:break;
            }
        }
    };

    public void parseInfor(String jsonString) {
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        final Message message = new Message();

        if (jsonArray.size() != 1) {
            message.what = UPDATEIDENTIFIER;
        }
        else{
            message.what = UPDATESINGLEIDENTIFIER;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            final ShowsInfor showInfor = new ShowsInfor();
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            JSONObject showObject = jsonObject.getJSONObject("show");
            JSONObject imageObject = showObject.getJSONObject("images");
            JSONObject posterObject = imageObject.getJSONObject("poster");
            JSONObject fanartObject = imageObject.getJSONObject("fanart");
            final JSONObject idObject = showObject.getJSONObject("ids");
            showInfor.ID = idObject.getString("imdb");
            showInfor.status = showObject.getString("status");
            showInfor.engname = showObject.getString("title");
            showInfor.overview = showObject.getString("overview");
            showInfor.imgUrl = fanartObject.getString("thumb");
            showInfor.posterImgUrl = posterObject.getString("medium");
            if (fanartObject.getString("thumb") != null || posterObject.getString("thumb") != null) {
                Request request = new Request.Builder()
                        .url("https://api-v2launch.trakt.tv/shows/" + idObject.getString("imdb") + "/translations/zh")
                        .header("Content-Type", "application/json")
                        .addHeader("trakt-api-version", "2")
                        .addHeader("trakt-api-key", "1eb6e36ce7199d857bbaefbef849c3eee049727e67da910c9b45a2269ae13089")
                        .build();

                mClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("Stalker", "error");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.i("Stalker", "Unexpected code " + response);
                            throw new IOException("Unexpected code " + response);
                        }
                        if (response.isSuccessful()) {
                            String text = response.body().string();

                            if (!text.equals("[]")) {
                                JSONArray nameArray = JSONArray.parseArray(text);
                                JSONObject nameObject = nameArray.getJSONObject(0);
                                showInfor.name = nameObject.getString("title");
                                showInfor.overview = nameObject.getString("overview").replace("\n","");
                            }
                            else
                            {
                                showInfor.name = showInfor.engname;
                                showInfor.engname = "";
                            }
                            message.obj = showInfor;
                            myHandler.sendMessage(message);
                        }
                    }
                });
            }
        }


    }

    public void updateUI(int type, String name, String engname, String status, String overview, String imageUrl) {
        if(type == 1) {
            searchResultItem searchResultItem = new searchResultItem(SearchResultActivity.this);
            searchResultItem.setTextViewText(name, status, overview, name);
            Log.i("Size11", searchResultItem.getImgWidth() + searchResultItem.getImgHeight() + "");
            Picasso.with(SearchResultActivity.this).load(imageUrl).placeholder(R.drawable.holdorerror).error(R.drawable.holdorerror).resize(MainActivity.screenWidth, UnitConversion.dip2px(this, 175)).centerCrop().into(searchResultItem.getImageView());
            liLayout.addView(searchResultItem);
        }
        if(type == 0){
            Log.i("Stalker", "UpdateUI!"+name+status+overview+name);
            searchResultItem searchResultItem = new searchResultItem(SearchResultActivity.this);
            searchResultItem.setTextViewText(name, status, overview, engname);
            Picasso.with(SearchResultActivity.this).load(imageUrl).placeholder(R.drawable.holdorerror).error(R.drawable.holdorerror).resize(MainActivity.screenWidth, searchResultActivityLayout.getHeight()).centerCrop().into(searchResultItem.getImageView());
            liLayout.addView(searchResultItem);
        }
    }

}
