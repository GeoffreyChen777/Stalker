package com.sorry.api.util;

import android.content.Context;

import com.squareup.picasso.Picasso;

/**
 * Created by sorry on 9/5/16.
 */
public class PicassoEngine {

    private static PicassoEngine instance = null;
    private static Picasso picasso;

    private PicassoEngine(Context context){
        picasso = new Picasso.Builder(context).build();
    }

    public static PicassoEngine getInstance(Context context){
        if(instance == null){
            instance = new PicassoEngine(context);
        }
        return instance;
    }
}
