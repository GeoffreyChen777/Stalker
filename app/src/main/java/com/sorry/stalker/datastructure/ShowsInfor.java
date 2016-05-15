package com.sorry.stalker.datastructure;


import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by sorry on 2016/4/22.
 */
public class ShowsInfor implements Serializable {
    public String ID;
    public String mazeID;
    public String name;
    public String engname;
    public String status;
    public String overview;
    public String imgUrl;
    public String posterImgUrl;
    public String airDate;
    public String airedSeason;
    public String airedEpisodeNumber;
    public Bitmap bitmap;

    public ShowsInfor() {
        ID = "";
        mazeID = "";
        name = "";
        engname = "";
        status = "";
        overview = "";
        imgUrl = "";
        posterImgUrl = "";
        airDate = "";
        airedSeason = "";
        airedEpisodeNumber = "";
        bitmap = null;

    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ShowsInfor){
            ShowsInfor infor = (ShowsInfor) obj;
            return this.name.equals(infor.name)  && this.engname.equals(infor.engname);
        }
        return super.equals(obj);
    }
}
