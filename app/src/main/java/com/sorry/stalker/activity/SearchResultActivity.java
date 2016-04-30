package com.sorry.stalker.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sorry.stalker.R;
import com.sorry.stalker.datastructure.ShowsInfor;
import com.sorry.stalker.tools.UnitConversion;
import com.sorry.stalker.widget.SmallSearchResultItem;
import com.sorry.stalker.widget.searchResultItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Cache;

public class SearchResultActivity extends AppCompatActivity {

    private String searchText;
    private OkHttpClient mClient;
    private ImageButton backButton;
    private ImageButton confirmButton;
    private ScrollView searchResltList;
    private List searchResultInforList;
    private RelativeLayout liLayout;
    private LinearLayout searchResultActivityLayout;
    private GridLayout searchResultLayout;
    private DisplayMetrics dm;
    private SpinKitView loadingAnimation;
    protected static final int GUIUPDATEIDENTIFIER = 0x101;
    protected static final int UPDATEIDENTIFIER = 0x102;
    protected static final int NORESULTIDENTIFIER = 0x103;
    protected static final int UPDATESINGLEIDENTIFIER = 0x104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(this.getCacheDir(), cacheSize);
        mClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        searchResltList = (ScrollView) findViewById(R.id.searchResltList);
        loadingAnimation = (SpinKitView) findViewById(R.id.loadingAnimation);
        liLayout = (RelativeLayout) findViewById(R.id.searchResltLayout);
        searchResultLayout = (GridLayout) findViewById(R.id.searchResultListLayout);
        searchResultActivityLayout = (LinearLayout) findViewById(R.id.searchResltActivityLayout);
        searchResultInforList = new ArrayList();
        Resources resources = this.getResources();
        dm = resources.getDisplayMetrics();
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
                case UPDATEIDENTIFIER:{
                    loadingAnimation.setVisibility(View.GONE);
                    ShowsInfor infor = (ShowsInfor) msg.obj;
                    updateUI(1, infor.name, infor.engname, infor.status, infor.overview, infor.imgUrl);
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
                .addHeader("Cache-Control","max-age=9600")
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


    public void parseInfor(String jsonString) {
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        Log.i("Size",jsonArray.size()+"");
        final Message flagMessage = new Message();

        if (jsonArray.size() != 1) {
            flagMessage.what = UPDATEIDENTIFIER;
        }
        else{
            flagMessage.what = UPDATESINGLEIDENTIFIER;
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
                            Message message = new Message();
                            message.what = flagMessage.what;
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
            final SmallSearchResultItem smallSearchResultItem = new SmallSearchResultItem(SearchResultActivity.this);
            smallSearchResultItem.setName(name);
            Space sp = new Space(this);
            Space sp2 = new Space(this);
            RelativeLayout.LayoutParams spacerp = new RelativeLayout.LayoutParams(((dm.widthPixels-2*UnitConversion.dip2px(this,160))/3),GridLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams spacerp2 = new RelativeLayout.LayoutParams(((dm.widthPixels-2*UnitConversion.dip2px(this,160))/6),GridLayout.LayoutParams.WRAP_CONTENT);
            if(searchResultLayout.getChildCount()%2 == 0) {
                sp.setLayoutParams(spacerp);
                sp2.setLayoutParams(spacerp2);
            }
            else{
                sp.setLayoutParams(spacerp2);
                sp2.setLayoutParams(spacerp);
            }

            Picasso.with(SearchResultActivity.this)
                    .load(imageUrl).placeholder(R.drawable.loading)
                    .error(R.drawable.holdorerror)
                    .resize(UnitConversion.dip2px(this,160f),UnitConversion.dip2px(this,90f))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            smallSearchResultItem.setImage(bitmap);

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.i("faile","noimage");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
            searchResultLayout.addView(sp);
            searchResultLayout.addView(smallSearchResultItem);
            searchResultLayout.addView(sp2);

        }
        if(type == 0){
            searchResultItem searchResultItem = new searchResultItem(SearchResultActivity.this, searchResultActivityLayout.getHeight());
            searchResultItem.setTextViewText(name, status, overview, engname);
            liLayout.addView(searchResultItem);
            Picasso.with(SearchResultActivity.this).load(imageUrl).error(R.drawable.holdorerror).resize(MainActivity.screenWidth, searchResultActivityLayout.getHeight()).centerCrop().into(searchResultItem.getImageView());

        }
    }

}
