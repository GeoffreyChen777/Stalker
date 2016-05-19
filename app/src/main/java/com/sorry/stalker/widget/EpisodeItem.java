package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/19.
 */
public class EpisodeItem extends RelativeLayout{

    private TextView episodeTextView;
    public String season;
    public String episode;

    public EpisodeItem(Context context) {
        this(context, null);
    }

    public EpisodeItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.episode_item, this, true);
        episodeTextView = (TextView) findViewById(R.id.episodeItemTextView);
    }

    public void setEpisodeItem(String season, String episode, String name){
        this.episodeTextView.setText(String.format("%02d",Integer.valueOf(episode)) + ".  " + name);
        this.season = season;
        this.episode = episode;
    }

    public String getSeason(){
        return season;
    }

    public String getEpisode(){
        return episode;
    }



}
