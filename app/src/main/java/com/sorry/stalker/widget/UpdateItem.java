package com.sorry.stalker.widget;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/17.
 */
public class UpdateItem extends RelativeLayout {

    private TextView episodeNameTextView;
    private ImageButton playButton;

    public UpdateItem(Context context) {
        this(context, null);
    }

    public UpdateItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.update_item, this, true);
        episodeNameTextView = (TextView) findViewById(R.id.episodesName);
        playButton = (ImageButton) findViewById(R.id.playButton);
    }

    public void setEpisodeName(String episodeName, String seasonNum, String episodeNum){
        String season = String.format("%02d", Integer.valueOf(seasonNum));
        String episode = String.format("%02d", Integer.valueOf(episodeNum));
        this.episodeNameTextView.setText("S" + season + " E" + episode + "   " + episodeName);
    }

    public ImageButton getButton(){
        return playButton;
    }
}
