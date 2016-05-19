package com.sorry.stalker.datastructure;

import java.io.Serializable;

/**
 * Created by sorry on 2016/5/17.
 */
public class EpisodeInfor implements Serializable {
    public String name;
    public String Season;
    public String Episode;
    public String btTorrent;
    public EpisodeInfor(){
        name = "";
        Season = "";
        Episode = "";
        btTorrent = "";
    }
    public EpisodeInfor(String name, String Season, String Episode, String btTorrent){
        this.name = name;
        this.Season = Season;
        this.Episode = Episode;
        this.btTorrent = btTorrent;
    }
}
