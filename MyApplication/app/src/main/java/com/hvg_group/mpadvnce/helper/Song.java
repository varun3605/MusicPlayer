package com.hvg_group.mpadvnce.helper;

/**
 * Created by Varun on 27-10-2017.
 */

public class Song
{
    private long id;
    private String title;
    private String artist;
    private int duration;
    private int ismusic;

    public Song(long id, String title, String artist, int duration, int ismusic) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.ismusic = ismusic;
    }

    public Song() {
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getId() {

        return id;
    }

    public int getIsmusic() {
        return ismusic;
    }

    public void setIsmusic(int ismusic) {
        this.ismusic = ismusic;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
