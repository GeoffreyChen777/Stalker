package com.sorry.model;

import java.io.Serializable;

public class EpisodeInfor implements Serializable {
    public String name;
    public String season;
    public String episode;
    public String btTorrent;
    public EpisodeInfor(){
        name = "";
        season = "";
        episode = "";
        btTorrent = "";
    }
    public EpisodeInfor(String name, String season, String episode, String btTorrent){
        this.name = name;
        this.season = season;
        this.episode = episode;
        this.btTorrent = btTorrent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getBtTorrent() {
        return btTorrent;
    }

    public void setBtTorrent(String btTorrent) {
        this.btTorrent = btTorrent;
    }
}
