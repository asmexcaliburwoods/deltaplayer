// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MediaPlayer.java

package dplayer.media;

import java.io.File;

// Referenced classes of package dplayer.media:
//            MediaPlayerListener

public interface MediaPlayer {

    public abstract void setFile(File file);

    public abstract File getFile();

    public abstract void play();

    public abstract void stop();

    public abstract void pause();

    public abstract void setOffsetSeconds(long l);

    public abstract long getOffsetSeconds();

    public abstract long getPauseOffsetSeconds();

    public abstract void setFile_playFrom(File file, long l);

    public abstract boolean isPlaying();

    public abstract boolean isPaused();

    public abstract void addMediaListener(MediaPlayerListener mediaplayerlistener);

    public abstract Object getMutex();
}
