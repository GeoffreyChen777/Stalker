package com.sorry.stalker.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frostwire.jlibtorrent.LibTorrent;
import com.frostwire.jlibtorrent.Session;
import com.frostwire.jlibtorrent.TorrentAlertAdapter;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import com.frostwire.jlibtorrent.swig.libtorrent;
import com.sorry.stalker.R;
import com.sorry.stalker.datastructure.EpisodeInfor;
import com.sorry.stalker.datastructure.ShowsInfor;
import com.sorry.stalker.tools.Downloader;
import com.sorry.stalker.tools.TransBitmap;
import com.sorry.stalker.tools.UnitConversion;
import com.sorry.stalker.widget.EpisodeItem;
import com.sorry.stalker.widget.EpisodeListItem;
import com.sorry.stalker.widget.EpisodesList;
import com.sorry.stalker.widget.UpdateFirstItem;
import com.sorry.stalker.widget.UpdateItem;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sorry on 2016/5/17.
 */
public class ShowMainActivity extends AppCompatActivity {
    private TextView rankTextView;
    private TextView showNameTextView;
    private TextView showGenresTextView;
    private TextView showEngNameTextView;
    private TextView showCastsTextView;
    private ImageButton backButton;
    private ImageButton listButton;
    private ImageButton hideListButton;
    private ShowsInfor showInfor;
    private LinearLayout scrollLayout;
    private LinearLayout scrollListLayout;
    private ImageView showPoster;
    private View shadow;
    private EpisodesList episodesList;
    private Picasso picasso;
    private Context context;
    private OkHttpClient mClient;
    private List<EpisodeInfor> episodeInforList;
    private RelativeLayout mainInforLayout;
    private RelativeLayout episodesListLayout;
    private DisplayMetrics dm;
    private final int UPDATELIST = 0x0;
    private final int UPDATEUI = 0x1;
    private final int GETTORRENT = 0x2;
    public final int DOWNLOADEDTORRENT = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_show);
        context = this;
        picasso = new Picasso.Builder(this.getBaseContext()).build();
        mClient = new OkHttpClient();

        if (getIntent().getExtras() != null) {

            this.showInfor = (ShowsInfor) getIntent().getExtras().getSerializable("ShowInfor");
        }
        if(!isNetworkAvailable()){
            Toast toast = Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_SHORT);
            toast.show();
        }
        updateEpisodes();
        showPoster = (ImageView) findViewById(R.id.showPoster);
        picasso.load(showInfor.posterImgUrl)
                .placeholder(R.drawable.hold_ver)
                .config(Bitmap.Config.RGB_565)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(showPoster);


        rankTextView = (TextView) findViewById(R.id.rankTextView);
        showNameTextView = (TextView) findViewById(R.id.showNameTextView);
        showEngNameTextView = (TextView) findViewById(R.id.showEngNameTextView);
        showGenresTextView = (TextView) findViewById(R.id.showGenresTextView);
        showCastsTextView = (TextView) findViewById(R.id.showCastsTextView);
        scrollLayout = (LinearLayout) findViewById(R.id.updateScrollLayout);
        backButton = (ImageButton) findViewById(R.id.backToMainButton);
        hideListButton = (ImageButton) findViewById(R.id.hideListButton);
        listButton = (ImageButton) findViewById(R.id.listButton);
        mainInforLayout = (RelativeLayout) findViewById(R.id.mainInforLayout);
        episodesListLayout = (RelativeLayout) findViewById(R.id.episodesListLayout);
        episodesList = (EpisodesList) findViewById(R.id.episodesListLayout);
        shadow = (View) findViewById(R.id.view_shadow);

        episodeInforList = new ArrayList<>();

        Typeface rankNumFont = Typeface.createFromAsset(getResources().getAssets(), "fonts/rank_number_font.otf");
        Typeface showEngNameFont = Typeface.createFromAsset(getResources().getAssets(), "fonts/LithosPro-Regular.otf");
        rankTextView.setTypeface(rankNumFont,Typeface.ITALIC);
        showEngNameTextView.setTypeface(showEngNameFont);

        if(showInfor.ifHasAllInfor) {
            UPdateUI();
        }
        else{
            getAllInfor();
        }



        Resources resources = this.getResources();
        dm = resources.getDisplayMetrics();
        backButton.setOnClickListener(backListener);
        listButton.setOnClickListener(showListListener);
        hideListButton.setOnClickListener(hideListListener);


        if(!showInfor.updateEpisodeInfor[0].name.equals("")){
            UpdateFirstItem updateFirstItem = new UpdateFirstItem(this);
            updateFirstItem.setEpisodeName(showInfor.updateEpisodeInfor[0].name,showInfor.updateEpisodeInfor[0].Season,showInfor.updateEpisodeInfor[0].Episode);
            scrollLayout.addView(updateFirstItem);
        }

        if(!showInfor.updateEpisodeInfor[1].name.equals("")){
            UpdateItem updateItem = new UpdateItem(this);
            updateItem.setEpisodeName(showInfor.updateEpisodeInfor[1].name,showInfor.updateEpisodeInfor[1].Season,showInfor.updateEpisodeInfor[1].Episode);
            scrollLayout.addView(updateItem);
        }
        if(!showInfor.updateEpisodeInfor[2].name.equals("")){
            UpdateItem updateItem = new UpdateItem(this);
            updateItem.setEpisodeName(showInfor.updateEpisodeInfor[2].name,showInfor.updateEpisodeInfor[2].Season,showInfor.updateEpisodeInfor[2].Episode);
            scrollLayout.addView(updateItem);
        }

    }

    private void UPdateUI(){
        rankTextView.setText(showInfor.rank);
        showNameTextView.setText(showInfor.name);
        showEngNameTextView.setText(showInfor.engname);
        showGenresTextView.setText(showInfor.genres);
        showCastsTextView.setText(showInfor.casts);
    }

    private void getAllInfor(){
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
                    Message message = new Message();
                    message.what = UPDATEUI;
                }
            }
        });
    }


    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ShowMainActivity.this.finish();
        }
    };

    private View.OnClickListener showListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            episodesListLayout.setVisibility(View.VISIBLE);
            shadow.setVisibility(View.VISIBLE);
            shadow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideEpisodeList();
                }
            });
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            alphaAnimation.setDuration(200);
            shadow.startAnimation(alphaAnimation);
            ObjectAnimator.ofFloat(episodesListLayout, "translationX", dm.widthPixels, dm.widthPixels - UnitConversion.dip2px(context,300)).setDuration(200).start();//X轴平移旋转
        }
    };

    private void hideEpisodeList(){
        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(episodesListLayout, "translationX", dm.widthPixels - UnitConversion.dip2px(context,300), dm.widthPixels).setDuration(200);
        fadeAnim.start();
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(200);
        shadow.startAnimation(alphaAnimation);
        fadeAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                shadow.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }


    private View.OnClickListener hideListListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideEpisodeList();
        }
    };

    public void updateEpisodesListUI(){
        String defalutSeason = "-1";
        for(int i = 0; i < episodeInforList.size(); i++){
            EpisodeItem episodeItem = new EpisodeItem(this);
            episodeItem.setEpisodeItem(episodeInforList.get(i).Season,episodeInforList.get(i).Episode,episodeInforList.get(i).name);
            final String season = episodeItem.season;
            final String episode = episodeItem.episode;
            episodeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getTorrentUrl(showInfor.mazeID, season, episode);
                }
            });
            if(!episodeInforList.get(i).Season.equals(defalutSeason)){
                EpisodeListItem episodeListItem = new EpisodeListItem(this);
                episodeListItem.setSeasonTitleTextView(episodeInforList.get(i).Season);
                defalutSeason = episodeInforList.get(i).Season;
                episodesList.addEpisodeListItem(episodeListItem);
            }
            EpisodeListItem episodeListItem = episodesList.getEpisodeListItemAt(episodesList.getEpisodeListItemCount() - 1);
            episodeListItem.addEpisodeItem(episodeItem);
        }
    }

    public void updateEpisodes() {
        Request request = new Request.Builder().url("http://api.tvmaze.com/shows/" + showInfor.mazeID + "?embed[]=episodes")
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
                    JSONArray episodesJsonArray = responseJson.getJSONObject("_embedded").getJSONArray("episodes");
                    int flag = 0;
                    for (int i = episodesJsonArray.size() - 1; i >= 0; i--) {
                        if (episodesJsonArray.getJSONObject(i).getString("season").equals(showInfor.airedSeason) && episodesJsonArray.getJSONObject(i).getString("number").equals(showInfor.airedEpisodeNumber)) {
                            flag = i;
                            break;
                        }
                    }
                    for (int i = flag; i >= 0; i--) {
                        episodeInforList.add(new EpisodeInfor(episodesJsonArray.getJSONObject(i).getString("name"), episodesJsonArray.getJSONObject(i).getString("season"), episodesJsonArray.getJSONObject(i).getString("number"), ""));
                    }
                    Message message = new Message();
                    message.what = UPDATELIST;
                    mainShowHandler.sendMessage(message);
                }

            }
        });
    }


    public Handler mainShowHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //判断发送的消息
                case UPDATELIST: {
                    updateEpisodesListUI();
                    break;
                }
                case UPDATEUI:{
                    UPdateUI();
                    break;
                }
                case GETTORRENT:{
                    downloadTorrent((String)msg.obj);
                }
                case DOWNLOADEDTORRENT:{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //downloadVideo((String) msg.obj);
                        }}).start();
                }

            }
            super.handleMessage(msg);
        }
    };

    private void downloadVideo(String torrentName){
        File torrentFile = new File("/storage/emulated/0/torrent/"+torrentName);

        System.out.println("Using libtorrent version: " + LibTorrent.version());

        final Session s = new Session();

        final TorrentHandle th = s.addTorrent(torrentFile, torrentFile.getParentFile());

        final CountDownLatch signal = new CountDownLatch(1);

        s.addListener(new TorrentAlertAdapter(th) {
            @Override
            public void blockFinished(BlockFinishedAlert alert) {
                int p = (int) (th.getStatus().getProgress() * 100);
                System.out.println("Progress: " + p + " for torrent name: " + alert.torrentName());
                System.out.println(s.getStats().download());
            }

            @Override
            public void torrentFinished(TorrentFinishedAlert alert) {
                System.out.print("Torrent finished");
                signal.countDown();
            }
        });

        th.resume();

        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        libtorrent.default_storage_disk_write_access_log(false);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    private void getTorrentUrl(String mazeID, String season, String episode){
        String url = "https://kat.cr/json.php?q=tv:" + mazeID + "%20season:" + season + "%20episode:" + episode + "&field=seeders&sorter=desc&page=1&";
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String jsonStr = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    JSONArray torrentArray = jsonObject.getJSONArray("list");
                    String torrentUrl = torrentArray.getJSONObject(0).getString("torrentLink");
                    Message msg = new Message();
                    msg.what = GETTORRENT;
                    msg.obj = torrentUrl;
                    mainShowHandler.sendMessage(msg);
                }
            }
        });
    }

    private void downloadTorrent(String torrentUrl){
        Log.i("Torrent",torrentUrl);
        Downloader._downloadAsyn(torrentUrl, "/storage/emulated/0/Stalker/torrent", mClient, mainShowHandler);
    }

}
