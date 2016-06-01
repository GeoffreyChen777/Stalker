package com.sorry.stalker.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;
import com.sorry.stalker.R;
import com.sorry.stalker.datastructure.ShowsInfor;
import com.sorry.stalker.tools.Database;
import com.sorry.stalker.tools.OnClickListenerWithItem;
import com.sorry.stalker.tools.TextWatcherWithItem;
import com.sorry.stalker.tools.UnitConversion;
import com.sorry.stalker.widget.CustomPtrHeader;
import com.sorry.stalker.widget.MainItem;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.sorry.stalker.widget.MainItemLayout;
import com.sorry.stalker.widget.MainToolBar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private ImageButton menuButton = null;
    private ImageButton addButton = null;
    private SearchView mSearchView = null;
    private ArrayList<ShowsInfor> selectedList;
    private ArrayList<ShowsInfor> newList;
    private LinearLayout selectedListLayout;
    private ScrollView selectedScrollLayout;
    private MainToolBar mainToolBar;
    private PtrFrameLayout ptrFrame;
    private OkHttpClient mClient;
    protected static int screenWidth;
    protected final int NONET = 0x0;
    protected final int SEARCH = 1;
    private Picasso picasso;
    private MainItemLayout zeroDaylayout;
    private MainItemLayout oneDaylayout;
    private MainItemLayout twoDaylayout;
    private MainItemLayout threeDaylayout;
    private MainItemLayout fourDaylayout;
    private MainItemLayout fiveDaylayout;
    private MainItemLayout sixDaylayout;
    private MainItemLayout notReturningDaylayout;
    private ArrayList<MainItemLayout> mainItemLayoutArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("dir",Environment.getDataDirectory()+"");
        db = openOrCreateDatabase("ShowInfor.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS SelectedShow (id VARCHAR PRIMARY KEY, name VARCHAR, engname VARCHAR, status VARCHAR, overview VARCHAR, imgUrl VARCHAR, posterImgUrl VARCHAR, airDate VARCHAR, airedSeason VARCHAR, airedEpisodeNumber VARCHAR)");

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        selectedList = new ArrayList<>();
        newList = new ArrayList<>();
        newList = getData();
        mClient = new OkHttpClient();
        picasso = new Picasso.Builder(this.getBaseContext()).build();

        mainToolBar = (MainToolBar) findViewById(R.id.toolbar);

        selectedListLayout = (LinearLayout) findViewById(R.id.selectedListLayout);
        selectedScrollLayout = (ScrollView) findViewById(R.id.selectListScroll);
        mainToolBar.ScrolltoTop(selectedScrollLayout);
        zeroDaylayout = (MainItemLayout) findViewById(R.id.zeroDay);
        oneDaylayout = (MainItemLayout) findViewById(R.id.oneDay);
        twoDaylayout = (MainItemLayout) findViewById(R.id.twoDay);
        threeDaylayout = (MainItemLayout) findViewById(R.id.threeDay);
        fourDaylayout = (MainItemLayout) findViewById(R.id.fourDay);
        fiveDaylayout = (MainItemLayout) findViewById(R.id.fiveDay);
        sixDaylayout = (MainItemLayout) findViewById(R.id.sixDay);
        notReturningDaylayout = (MainItemLayout) findViewById(R.id.NotReturning);
        setDate();
        notReturningDaylayout.setDayNum("Not Airing");
        mainItemLayoutArray = new ArrayList<>();
        mainItemLayoutArray.add(zeroDaylayout);
        mainItemLayoutArray.add(oneDaylayout);
        mainItemLayoutArray.add(twoDaylayout);
        mainItemLayoutArray.add(threeDaylayout);
        mainItemLayoutArray.add(fourDaylayout);
        mainItemLayoutArray.add(fiveDaylayout);
        mainItemLayoutArray.add(sixDaylayout);
        mainItemLayoutArray.add(notReturningDaylayout);

        mSearchView = (SearchView)findViewById(R.id.searchView);
        mSearchView.setVersion(SearchCodes.VERSION_MENU_ITEM);
        mSearchView.setStyle(SearchCodes.STYLE_MENU_ITEM_CLASSIC);
        mSearchView.setTheme(SearchCodes.THEME_LIGHT);
        mSearchView.setDivider(true);
        mSearchView.setHint("Search");
        mSearchView.setAnimationDuration(300);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {
                if (!searchText.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                    intent.putExtra("searchText", searchText);
                    startActivityForResult(intent,SEARCH);
                    mSearchView.hide(true);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        menuButton = (ImageButton)findViewById(R.id.menuButton);
        addButton = (ImageButton)findViewById(R.id.addMenu);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.show(true);
            }
        });

        //setToolBarButtonSelector();

        UpdateUI();

        ptrFrame = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);

        CustomPtrHeader header = new CustomPtrHeader(this);
        header.setPadding(0, 0, 0, 0);

        header.invalidate();
        ptrFrame.setDurationToCloseHeader(800);
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);
        ptrFrame.setPinContent(true);
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return (PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header));

            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setDate();
                        RefreshUI();
                    }
                }, 800);



            }
        });

    }

    public void setDate(){
        String[] Week = {"Saturday","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday"};
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        int SmWay = c.get(Calendar.DAY_OF_WEEK);
        zeroDaylayout.setDayNum("Today");
        oneDaylayout.setDayNum("Tomorrow");
        twoDaylayout.setDayNum(Week[(SmWay+2)%7]);
        threeDaylayout.setDayNum(Week[(SmWay+3)%7]);
        fourDaylayout.setDayNum(Week[(SmWay+4)%7]);
        fiveDaylayout.setDayNum(Week[(SmWay+5)%7]);
        sixDaylayout.setDayNum(Week[(SmWay+6)%7]);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SEARCH)
        {
            if (resultCode == SearchResultActivity.NORMAL_RESULT)
            {
                List showlist =  (List<ShowsInfor>) data.getSerializableExtra("Result");
                newList.clear();
                for(int i = 0; i < showlist.size(); i++ ){
                    if(!selectedList.contains(showlist.get(i))){
                        newList.add((ShowsInfor) showlist.get(i));
                    }
                }
                UpdateUI();
            }
        }
    }


    private void UpdateUI(){
        if(isNetworkAvailable()) {
            for (int i = 0; i < newList.size(); i++) {
                MainItem item = new MainItem(this);
                ShowsInfor infor = newList.get(i);
                item.setName(infor.name).setEngName(infor.engname).setStatus(infor.status).setShowInfor(infor);
                picasso.load(infor.imgUrl)
                        .config(Bitmap.Config.RGB_565)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(item.getImageView());
                selectedList.add(newList.get(i));
                item.getDataFromServer(mClient);
                item.status.addTextChangedListener(new TextWatcherWithItem(item) {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        sortAdd(getItem());
                    }
                });
                item.updateCastandPoster();
            }

        }else{
            Message message = new Message();
            message.what = NONET;
            mainHandler.sendMessage(message);
            for (int i = 0; i < newList.size(); i++) {
                MainItem item = new MainItem(this);
                ShowsInfor infor = newList.get(i);
                item.setName(infor.name).setEngName(infor.engname).setStatus(infor.status).setStatus(infor.status+" S"+infor.airedSeason+" E"+infor.airedEpisodeNumber).setDayNum(infor.airDate).setShowInfor(infor);
                picasso.load(infor.imgUrl)
                        .config(Bitmap.Config.RGB_565)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(item.getImageView());
                sortAdd(item);
            }
        }
    }

    private void RefreshUI(){
        if(isNetworkAvailable()) {
            for(int i = 0; i < 7; i++){
                MainItemLayout mainItemLayout = (MainItemLayout) selectedListLayout.getChildAt(i);
                for(int j = 1; j < mainItemLayout.getChildCount(); j++){
                    MainItem item = new MainItem(this);
                    final MainItem mainItem = (MainItem) mainItemLayout.getChildAt(j);
                    item.setName(mainItem.showInfor.name).setEngName(mainItem.showInfor.engname).setShowInfor(mainItem.showInfor);
                    picasso.load(mainItem.showInfor.imgUrl)
                            .config(Bitmap.Config.RGB_565)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .placeholder(R.drawable.hold)
                            .into(item.getImageView());
                    item.getDataFromServer(mClient);
                    item.status.addTextChangedListener(new TextWatcherWithItem(item) {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            sortAdd(getItem());
                        }
                    });
                    item.updateCastandPoster();
                    mainItemLayout.remove(mainItem);
                }
            }


        }
        ptrFrame.refreshComplete();
    }




    private void sortAdd(MainItem item){
        MainItemLayout mainItemLayout = (MainItemLayout) selectedListLayout.getChildAt(Integer.valueOf(item.showInfor.airDate));
        mainItemLayout.showDayViewLayout(true);
        mainItemLayout.addView(item);
        insertData(item.showInfor);
        item.deleteButton.setOnClickListener(new OnClickListenerWithItem(item){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("确认删除?");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        mainItemLayoutArray.get(Integer.valueOf(getItem().showInfor.airDate)).remove(getItem());

                        selectedList.remove(getItem().showInfor);
                        deleteData(getItem().showInfor);
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
        });
    }

    public Handler mainHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //判断发送的消息
                case NONET: {
                    NoNetToast();
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };

    public void NoNetToast(){
        Toast.makeText(this,"请检查网络连接",Toast.LENGTH_SHORT);
    }

    public ArrayList<ShowsInfor> getData(){
        ArrayList<ShowsInfor> ShowList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from SelectedShow",null);
        Log.i("dbcount",cursor.getCount()+"================================");

            while(cursor.moveToNext()){
                ShowsInfor showsInfor = new ShowsInfor();
                showsInfor.ID = cursor.getString(0);
                showsInfor.name = cursor.getString(1);
                showsInfor.engname = cursor.getString(2);
                showsInfor.status = cursor.getString(3);
                showsInfor.overview = cursor.getString(4);
                showsInfor.imgUrl = cursor.getString(5);
                showsInfor.posterImgUrl = cursor.getString(6);
                showsInfor.airDate = cursor.getString(7);
                showsInfor.airedSeason = cursor.getString(8);
                showsInfor.airedEpisodeNumber = cursor.getString(9);
                ShowList.add(showsInfor);
            }

        cursor.close();
        return ShowList;
    }

    public void insertData(ShowsInfor showsInfor){
        ContentValues cValue = new ContentValues();
        cValue.put("id",showsInfor.ID);
        cValue.put("name",showsInfor.name);
        cValue.put("engname",showsInfor.engname);
        cValue.put("status",showsInfor.status);
        cValue.put("overview",showsInfor.overview);
        cValue.put("imgUrl",showsInfor.imgUrl);
        cValue.put("posterImgUrl",showsInfor.posterImgUrl);
        cValue.put("airDate",showsInfor.airDate);
        cValue.put("airedSeason",showsInfor.airedSeason);
        cValue.put("airedEpisodeNumber",showsInfor.airedEpisodeNumber);
        db.replace("SelectedShow", null, cValue);
    }

    public void deleteData(ShowsInfor showsInfor){
        String sql = "delete from SelectedShow where id ='" + showsInfor.ID +"'";
        db.execSQL(sql);
    }

    public void deleteAllData(){
        String sql = "delete from SelectedShow";
        db.execSQL(sql);
    }
}
