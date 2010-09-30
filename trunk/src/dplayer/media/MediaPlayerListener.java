// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MediaPlayerListener.java

package dplayer.media;


public interface MediaPlayerListener {

    public abstract void exceptionEvent(Throwable throwable);

    public abstract void trackFinished();
}
