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
    public byte[] bitmap;
    public String rank;
    public String genres;
    public String casts;
    public EpisodeInfor[] updateEpisodeInfor;
    public boolean ifHasAllInfor;

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
        rank = "";
        genres = "";
        casts = "";
        updateEpisodeInfor = new EpisodeInfor[3];
        ifHasAllInfor = false;

        for(int i = 0; i < 3; i++) {
            EpisodeInfor episodeInfor = new EpisodeInfor();
            updateEpisodeInfor[i] = episodeInfor;
            updateEpisodeInfor[i].name = "";
            updateEpisodeInfor[i].Episode = "";
            updateEpisodeInfor[i].Season = "";
            updateEpisodeInfor[i].btTorrent = "";
        }
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
