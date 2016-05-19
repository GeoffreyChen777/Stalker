package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/19.
 */
public class EpisodeListItem extends RelativeLayout{

    private TextView seasonTitleTextView;
    public String season;
    private LinearLayout episodeItemLayout;

    public EpisodeListItem(Context context) {
        this(context, null);
    }

    public EpisodeListItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.episode_list_item, this, true);
        seasonTitleTextView = (TextView) findViewById(R.id.seasonTitleTextView);
        episodeItemLayout = (LinearLayout) findViewById(R.id.episodeItemLayout);
        season = "";

    }

    public void setSeasonTitleTextView(String season){
        this.seasonTitleTextView.setText("Season   " + season);
        this.season = season;
    }

    public void addEpisodeItem(EpisodeItem episodeItem){
        episodeItemLayout.addView(episodeItem);
    }

}
