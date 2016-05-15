package com.sorry.stalker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sorry.stalker.R;
import com.sorry.stalker.datastructure.ShowsInfor;
import com.sorry.stalker.tools.UnitConversion;
import com.sorry.stalker.widget.SearchResultToolbar;
import com.sorry.stalker.widget.SmallSearchResultItem;
import com.sorry.stalker.widget.searchResultItem;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.Serializable;
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
    private ImageButton addButton;
    private ImageView background;
    private ScrollView searchResltList;
    private List<ShowsInfor> searchResultInforList;
    private RelativeLayout liLayout;
    private LinearLayout multiResultLayout;
    private LinearLayout searchResultActivityLayout;
    private RelativeLayout searchResultToolbarLayout;
    private SearchResultToolbar searchResultToolbar;
    private DisplayMetrics dm;
    private AVLoadingIndicatorView loadingAnimation;
    private Picasso picasso;
    protected static final int GUIUPDATEIDENTIFIER = 0x101;
    protected static final int UPDATEIDENTIFIER = 0x102;
    protected static final int NORESULTIDENTIFIER = 0x103;
    protected static final int UPDATESINGLEIDENTIFIER = 0x104;
    protected static final int NORMAL_RESULT = 0x10;
    protected static final int SMALL_RESULT = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchResultInforList = new ArrayList();
        searchResultInforList.clear();
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(this.getCacheDir(), cacheSize);
        mClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        picasso = new Picasso.Builder(this.getBaseContext()).build();
        searchResltList = (ScrollView) findViewById(R.id.searchResltList);
        loadingAnimation = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        liLayout = (RelativeLayout) findViewById(R.id.searchResltLayout);
        multiResultLayout = (LinearLayout) findViewById(R.id.searchResltListLayout);
        searchResultActivityLayout = (LinearLayout) findViewById(R.id.searchResltActivityLayout);
        backButton = (ImageButton) findViewById(R.id.searchActivityBackButton);
        addButton = (ImageButton) findViewById(R.id.searchActivityAddButton);
        searchResultToolbarLayout = (RelativeLayout) findViewById(R.id.searchResultToolbar);
        searchResultToolbar = (SearchResultToolbar) findViewById(R.id.search_toolbar);
        background = (ImageView) findViewById(R.id.background);


        Resources resources = this.getResources();
        dm = resources.getDisplayMetrics();
        Bundle extras = getIntent().getExtras();
        if(!isNetworkAvailable()){
            Toast toast = Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (extras != null) {
            searchText = extras.getString("searchText");
            getInforFromWeb(searchText);
        }
        backButton.setOnClickListener(backListener);
        addButton.setOnClickListener(addListener);
    }

    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SearchResultActivity.this.finish();
        }
    };

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
                    updateUI(0, infor);
                    break;
                }
                case UPDATEIDENTIFIER:{
                    loadingAnimation.setVisibility(View.GONE);
                    ShowsInfor infor = (ShowsInfor) msg.obj;
                    updateUI(1, infor);
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

    public void updateUI(int type, final ShowsInfor infor) {
        background.setImageResource(R.drawable.search_result_background);
        if(type == 1) {
            searchResultToolbar.setVisibility(View.VISIBLE);
            final SmallSearchResultItem smallSearchResultItem = new SmallSearchResultItem(SearchResultActivity.this);
            smallSearchResultItem.showsInfor = infor;
            final Target mTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                    Log.d("DEBUG", "onBitmapLoaded");
                    smallSearchResultItem.setImage(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable drawable) {
                    Log.d("DEBUG", "onBitmapFailed");
                }

                @Override
                public void onPrepareLoad(Drawable drawable) {
                    Log.d("DEBUG", "onPrepareLoad");
                }
            };
            smallSearchResultItem.setTag(mTarget);
            smallSearchResultItem.setName(infor.name).setEngName(infor.engname).setInfor(infor.status).setDetial(infor.overview);
            if(infor.imgUrl != null) {
                picasso.load(infor.imgUrl)
                        .config(Bitmap.Config.RGB_565)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .error(R.drawable.hold)
                        .into(mTarget);
                smallSearchResultItem.showMask();
            }
            smallSearchResultItem.setOnSelectActionListener(new SmallSearchResultItem.OnSelectListener() {
                @Override
                public void select() {
                    if(smallSearchResultItem.isSelected()){
                        searchResultInforList.add(smallSearchResultItem.showsInfor);
                    }
                    else {
                        searchResultInforList.remove(smallSearchResultItem.showsInfor);
                    }
                }
            });
            multiResultLayout.addView(smallSearchResultItem);
        }
        if(type == 0){
            final searchResultItem searchResultItem = new searchResultItem(SearchResultActivity.this, searchResultActivityLayout.getHeight());
            searchResultItem.setTextViewText(infor.name, infor.status, infor.overview, infor.engname);
            liLayout.addView(searchResultItem);
            picasso.load(infor.posterImgUrl).error(R.drawable.hold).resize(MainActivity.screenWidth, searchResultActivityLayout.getHeight()).centerCrop().into(searchResultItem.getImageView());

            findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchResultItem.isSelected()) {
                        searchResultInforList.add(infor);
                        dialog(searchResultInforList);
                    } else
                        SearchResultActivity.this.finish();
                }
            });
        }
    }

    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(searchResultInforList.size() != 0) {
                dialog(searchResultInforList);
            }
            else
            {
                Toast.makeText(SearchResultActivity.this, "请选择ADD后添加！" , Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void dialog(final List<ShowsInfor> showlist){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
        builder.setTitle("确认添加?");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent();
                intent.setClass(SearchResultActivity.this, MainActivity.class);
                intent.putExtra("Result", (Serializable)searchResultInforList);
                setResult(NORMAL_RESULT, intent);
                SearchResultActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private boolean isNetworkAvailable() {

        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
