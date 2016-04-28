package com.sorry.stalker.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

public class SmallSearchResultItem extends RelativeLayout {
    private TextView name;
    private TopCropImageView image;
    private ImageButton addButton;
    private RelativeLayout nameLayout;
    private boolean isSelected;

    public SmallSearchResultItem(Context context) {
        this(context, null);
    }

    public SmallSearchResultItem(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.search_result_item_samll, this, true);
        name = (TextView) findViewById(R.id.small_name);
        image = (TopCropImageView) findViewById(R.id.small_image);
        addButton = (ImageButton) findViewById(R.id.small_add_button);
        nameLayout = (RelativeLayout) findViewById(R.id.nameLayout);
        isSelected = false;
        nameLayout.setOnClickListener(addListener);
        addButton.setOnClickListener(addListener);

    }

    OnClickListener addListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isSelected){
                isSelected = false;
                addButton.setImageResource(R.mipmap.ic_add_gray);
            }
            else{
                isSelected = true;
                addButton.setImageResource(R.mipmap.ic_confirm_selected);
            }
        }
    };

    public boolean isSelected(){
        return isSelected;
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setImage(Bitmap bmp){
        this.image.setImageBitmap(bmp);
    }

}
