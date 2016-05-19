package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/19.
 */
public class EpisodesList extends RelativeLayout{

    private ImageButton hideButton;
    private LinearLayout scrollListLayout;

    public EpisodesList(Context context) {
        this(context, null);
    }


    public EpisodesList(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.episode_list, this, true);
        hideButton = (ImageButton) findViewById(R.id.hideListButton);
        scrollListLayout = (LinearLayout) findViewById(R.id.scrollListLayout);
    }

    public void addEpisodeListItem(EpisodeListItem episodeListItem){
        scrollListLayout.addView(episodeListItem);
    }

    public int getEpisodeListItemCount(){
        return scrollListLayout.getChildCount();
    }

    public EpisodeListItem getEpisodeListItemAt(int i){
        return (EpisodeListItem)scrollListLayout.getChildAt(i);
    }

    public ImageButton getHideButton(){
        return hideButton;
    }



}
