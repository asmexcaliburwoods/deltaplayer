// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Song.java

package dplayer.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Song implements Comparable<Song> {
    private File mFile;
    private String mTitle;
    private String mAlbum;
    private String mArtist;
    private String mYear;
    private int mTrack;
    private long mDurationSeconds;
    private int mBitRate;
    private int mSampleRate;
    private Map<Object, Object> mExtMap;
    private long lastStartedMillis;
    private boolean submittedToLastFm;

    public Song(File file) {
        mDurationSeconds = -1L;
        mFile = file;
        mExtMap = ((Map<Object, Object>) (new HashMap<Object, Object>()));
    }

    private Song() {
        mDurationSeconds = -1L;
    }

    public static Song createNullSong() {
        return new Song();
    }

    public String getFilename() {
        return mFile.getName();
    }

    public long getSize() {
        return mFile.length();
    }

    public File getFile() {
        return mFile;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public int getTrack() {
        return mTrack;
    }

    public void setTrack(int track) {
        mTrack = track;
    }

    public long getDurationSeconds() {
        return mDurationSeconds;
    }

    public void setDurationSeconds(long duration) {
        mDurationSeconds = duration;
    }

    public int getBitRate() {
        return mBitRate;
    }

    public void setBitRate(int bitRate) {
        mBitRate = bitRate;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public void setSampleRate(int sampleRate) {
        mSampleRate = sampleRate;
    }

    public String getExtInfo(String key) {
        return (String)mExtMap.get(((Object) (key)));
    }

    public void setExtInfo(String key, String value) {
        mExtMap.put(((Object) (key)), ((Object) (value)));
    }

    public void removeExtInfo(String key) {
        mExtMap.remove(((Object) (key)));
    }

    public String toString() {
        return mFile != null ? mFile.getName() : "null Song";
    }

    public boolean equals(Object obj) {
        return (obj instanceof Song) && mFile.getAbsoluteFile().equals(((Object) (((Song)obj).getFile().getAbsoluteFile())));
    }

    public int compareTo(Song song) {
        return mFile.compareTo(song.getFile());
    }

    public void setLastStartedMillis(long lastStartedMillis) {
        this.lastStartedMillis = lastStartedMillis;
    }

    public long getLastStartedMillis() {
        return lastStartedMillis;
    }

    public void setSubmittedToLastFm(boolean b) {
        submittedToLastFm = b;
    }

    public boolean isSubmittedToLastFm() {
        return submittedToLastFm;
    }

    public void setFile(File file) {
        mFile = file;
    }
}
