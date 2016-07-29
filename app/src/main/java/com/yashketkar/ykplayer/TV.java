package com.yashketkar.ykplayer;

/**
 * Created by Yash on 1/4/2015.
 */

public class TV {

    private String title, thumbnailUrl, playbackUrl;

    public TV() {
    }

    public TV(String name, String thumbnailUrl, String playbackUrl) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.playbackUrl = playbackUrl;
    }

    public String getPlaybackUrl() {
        return playbackUrl;
    }

    public void setPlaybackUrl(String playbackUrl) {
        this.playbackUrl = playbackUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}