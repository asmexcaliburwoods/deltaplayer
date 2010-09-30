// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LastFmQueue.java

package dplayer.lastfm;

import dplayer.model.Song;

public interface LastFmQueue {

    public abstract void init();

    public abstract void trackStarted(Song song);

    public abstract void trackPlayed(Song song);

    public abstract void configure(boolean flag, String s, String s1);

    public abstract String getPassword();

    public abstract String getUserName();

    public abstract boolean isEnabled();

    public abstract void deinit();
}
