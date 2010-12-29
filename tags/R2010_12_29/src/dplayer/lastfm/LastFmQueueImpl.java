// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LastFmQueueImpl.java

package dplayer.lastfm;

import com.googlecode.ascrblr.api.scrobbler.TrackInfo;
import dplayer.DPUtils;
import dplayer.StringUtil;
import dplayer.model.Song;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.lastfm:
//            LastFmQueue, LFMRunnable

public class LastFmQueueImpl
    implements LastFmQueue {
    static class QueueItem {

        public boolean havePlayed;
        public TrackInfo trackInfo;

        QueueItem() {
        }
    }


    private static final Logger logger = Logger.getLogger(LastFmQueueImpl.class);
    private LFMRunnable runnable;
    private Thread thread;
    private final Queue<QueueItem> songQueue = new LinkedBlockingQueue<QueueItem>();
    private String password;
    private String userName;
    private boolean enabled;

    public LastFmQueueImpl() {
        runnable = new LFMRunnable(this);
    }

    Queue<QueueItem> getSongQueue() {
        return songQueue;
    }

    public synchronized void init() {
        deinit();
        runnable.setTerminated(false);
        if(enabled) {
            thread = new Thread(((Runnable) (runnable)));
            thread.start();
        }
    }

    public void trackStarted(Song song) {
        song.setLastStartedMillis(System.currentTimeMillis());
        add(song, false);
    }

    private void add(Song song, boolean havePlayed) {
        if(enabled) {
            String artist = StringUtil.mkEmpty(song.getArtist());
            String album = StringUtil.mkEmpty(song.getAlbum());
            String title = StringUtil.mkEmpty(song.getTitle());
            logger.debug(((Object) ((new StringBuilder("Submitting/")).append(havePlayed ? "played" : "started").append(": song=").append(title).append(", artist=").append(artist).append(", album=").append(album).toString())));
            TrackInfo trackInfo = new TrackInfo(artist, album, title, song.getLastStartedMillis(), DPUtils.convertPositiveLongToInt(song.getDurationSeconds()), com.googlecode.ascrblr.api.scrobbler.TrackInfo.SourceType.P);
            add(trackInfo, havePlayed);
        }
    }

    public void trackPlayed(Song song) {
        add(song, true);
    }

    private void add(TrackInfo song, boolean havePlayed) {
        QueueItem item = new QueueItem();
        item.trackInfo = song;
        item.havePlayed = havePlayed;
        songQueue.add(item);
        runnable.wake();
    }

    public void deinit() {
        Thread thread1;
        synchronized(this) {
            runnable.setTerminated(true);
            thread1 = thread;
            thread = null;
        }
        if(thread1 != null) {
            thread1.interrupt();
            try {
                thread1.join();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public synchronized void configure(boolean enabled, String userName, String password) {
        logger.info(((Object) ((new StringBuilder("last.fm ")).append(enabled ? (new StringBuilder("enabled: user=")).append(userName).toString() : "disabled").toString())));
        this.userName = userName;
        this.password = password;
        this.enabled = userName != null && password != null && enabled;
        if(enabled) {
            Runnable runnable = new Runnable() {
                public void run() {
                    synchronized(LastFmQueueImpl.this) {
                        init();
                    }
                }
            };
            new Thread(runnable).start();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

}
