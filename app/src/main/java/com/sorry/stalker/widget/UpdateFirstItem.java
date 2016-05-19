package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/17.
 */
public class UpdateFirstItem extends RelativeLayout {

    private TextView episodeNameTextView;

    public UpdateFirstItem(Context context) {
        this(context, null);
    }

    public UpdateFirstItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.update_first_item, this, true);
        episodeNameTextView = (TextView) findViewById(R.id.episodesName);
    }

    public void setEpisodeName(String episodeName, String seasonNum, String episodeNum){
        String season = String.format("%02d", Integer.valueOf(seasonNum));
        String episode = String.format("%02d", Integer.valueOf(episodeNum));
        this.episodeNameTextView.setText("S" + season + " E" + episode + "   " + episodeName);
    }
}
