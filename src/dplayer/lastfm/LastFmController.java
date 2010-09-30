// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LastFmController.java

package dplayer.lastfm;

import dplayer.StringUtil;
import dplayer.model.Song;

// Referenced classes of package dplayer.lastfm:
//            LastFmQueueImpl, LastFmQueue

public class LastFmController {

    private static final LastFmQueue queue = new LastFmQueueImpl();
    static final String LASTFM_CLIENT_ID = "tst";
    static final String LASTFM_CLIENT_VERSION = "1.0";

    public LastFmController() {
    }

    public static void init() {
        getQueue().init();
    }

    public static LastFmQueue getQueue() {
        return queue;
    }

    public static void deinit() {
        getQueue().deinit();
    }

    public static boolean isSongSubmittableToLastFm(Song song) {
        return !StringUtil.isTrimmedEmpty(song.getTitle());
    }

}
