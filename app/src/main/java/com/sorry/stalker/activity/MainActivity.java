package com.sorry.stalker.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;
import com.sorry.stalker.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton menuButton = null;
    private ImageButton addButton = null;
    private SearchView mSearchView = null;
    protected static int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;
        mSearchView = (SearchView)findViewById(R.id.searchView);
        mSearchView.setVersion(SearchCodes.VERSION_MENU_ITEM);
        mSearchView.setStyle(SearchCodes.STYLE_MENU_ITEM_CLASSIC);
        mSearchView.setTheme(SearchCodes.THEME_DARK);
        mSearchView.setDivider(true);
        mSearchView.setHint("Search");
        mSearchView.setAnimationDuration(300);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {
                if (!searchText.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                    intent.putExtra("searchText", searchText);
                    startActivity(intent);
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
        TextView titleView = (TextView) findViewById(R.id.titleView);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/LunaITCStd-Bold.otf");
        titleView.setTypeface(tf);
        setToolBarButtonSelector();


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


}
