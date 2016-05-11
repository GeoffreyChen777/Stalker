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
    private PtrFrameLayout ptrFrame;
    private OkHttpClient mClient;
    protected static int screenWidth;
    protected final int ADDVIEW = 0x0;
    protected final int SEARCH = 1;
    private Picasso picasso;


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
        selectedListLayout = (LinearLayout) findViewById(R.id.selectedListLayout);
        selectedScrollLayout = (ScrollView) findViewById(R.id.selectListScroll);
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
        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                RefreshUI();
                /*ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //ptrFrame.refreshComplete();
                    }
                }, 1500);*/
            }
        });

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
        Log.i("UpdfateUI",newList.size()+"");
        for(int i =0; i < newList.size(); i++) {
            MainItem item = new MainItem(this);
            ShowsInfor infor = newList.get(i);
            item.setName(infor.name).setEngName(infor.engname).setStatus(infor.status).setShowInfor(infor);
            picasso.load(infor.imgUrl)
                    .config(Bitmap.Config.RGB_565)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(item.getImageView());
            selectedList.add(newList.get(i));
            item.getDataFromServer(mClient);
            item.dayNum.addTextChangedListener(new TextWatcherWithItem(item) {
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

        }
    }

    private void RefreshUI(){
        selectedListLayout.removeAllViews();
        for(int i =0; i < selectedList.size(); i++) {
            MainItem item = new MainItem(this);
            ShowsInfor infor = selectedList.get(i);
            item.setName(infor.name).setEngName(infor.engname).setStatus(infor.status).setShowInfor(infor);
            picasso.load(infor.imgUrl)
                    .config(Bitmap.Config.RGB_565)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.hold)
                    .into(item.getImageView());
            item.getDataFromServer(mClient);
            item.dayNum.addTextChangedListener(new TextWatcherWithItem(item) {
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

        }
        ptrFrame.refreshComplete();
    }


    private void setToolBarButtonSelector(){
        menuButton.setOnTouchListener(new ImageButton.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    menuButton.setImageResource(R.mipmap.ic_menu_gray);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    menuButton.setImageResource(R.mipmap.ic_menu);
                }
                return false;
            }
        });
        addButton.setOnTouchListener(new ImageButton.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addButton.setImageResource(R.mipmap.ic_add_gray);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    addButton.setImageResource(R.mipmap.ic_add);
                }
                return false;
            }
        });
    }

    private void sortAdd(MainItem item){
        Log.i("sortAdd",selectedListLayout.getChildCount()+"");
        if(selectedListLayout.getChildCount()!=0) {
            int flag = 0;
            for (int i = 0; i < selectedListLayout.getChildCount(); i++) {
                MainItem titem = (MainItem) selectedListLayout.getChildAt(i);
                if (Integer.valueOf(titem.showInfor.airDate) > Integer.valueOf(item.showInfor.airDate)) {
                    Log.i("index",i+"");
                    selectedListLayout.addView(item,i);
                    flag = 1;
                    Log.i("name",item.name.getText().toString());
                    break;
                }
            }
            if(flag == 0){
                selectedListLayout.addView(item);
            }
        }
        else{
            selectedListLayout.addView(item);
        }
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
                        selectedListLayout.removeView(getItem());
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
                case ADDVIEW: {
                    break;
                }

            }
            super.handleMessage(msg);
        }
    };

    public ArrayList<ShowsInfor> getData(){
        ArrayList<ShowsInfor> ShowList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from SelectedShow",null);

        if(cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++){
                cursor.move(i);
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
