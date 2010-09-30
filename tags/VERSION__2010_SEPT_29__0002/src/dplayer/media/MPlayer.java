// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MPlayer.java

package dplayer.media;

import dplayer.*;
import dplayer.gui.i18n.I18N;
import java.io.File;
import java.util.*;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.media:
//            MediaPlayer, MPlayerThread, MediaPlayerListener

public class MPlayer
    implements MediaPlayer {
    public static class MPlayerSettings extends Settings {

        public static final dplayer.Settings.Property COMMAND = new dplayer.Settings.Property("mplayer.command", "Path and name of mplayer executable.", new String[] {
            "mplayer", "mplayer", "m.exe"
        });
        public static final dplayer.Settings.Property PRIORITY;

        static void registerSettings() {
            addSection("mplayer related settings.", 50, new dplayer.Settings.Property[] {
                COMMAND, PRIORITY
            });
        }

        static  {
            PRIORITY = new dplayer.Settings.Property("mplayer.priority", "The process priority for mplayer.", ProcessPriority.abovenormal.name());
        }

        public MPlayerSettings() {
        }
    }

    public static enum ProcessPriority {
        idle("idle", 0, I18N.get("PROCESS_PRIORITY_idle", "Idle")),
        belownormal("belownormal", 1, I18N.get("PROCESS_PRIORITY_belownormal", "Below Normal")),
        normal("normal", 2, I18N.get("PROCESS_PRIORITY_normal", "Normal")),
        abovenormal("abovenormal", 3, I18N.get("PROCESS_PRIORITY_abovenormal", "Above Normal")),
        high("high", 4, I18N.get("PROCESS_PRIORITY_high", "High")),
        realtime("realtime", 5, I18N.get("PROCESS_PRIORITY_realtime", "Realtime"));
        private String displayName;

        public String getDisplayName() {
            return displayName;
        }

        private ProcessPriority(String s, int i, String displayName) {
            this.displayName = displayName;
        }
    }


    private static final Logger logger = Logger.getLogger(MPlayer.class);
    public static final String NAME = "mplayer";
    public static final ProcessPriority priorities[];
    protected File mFile;
    private long mPauseOffsetSeconds;
    private long mTimestampSeconds;
    private MPlayerThread mThread;
    private Set<Object> mListenerSet;

    public File getFile() {
        return mFile;
    }

    public static boolean isChangingProcessPrioritySupported() {
        return About.OS.startsWith("Win");
    }

    public MPlayer() {
        MPlayerSettings.registerSettings();
        mListenerSet = ((Set<Object>) (new HashSet<Object>()));
    }

    public void addMediaListener(MediaPlayerListener listener) {
        if(listener == null) {
            throw new AssertionError();
        } else {
            mListenerSet.add(((Object) (listener)));
            return;
        }
    }

    public synchronized void setFile_playFrom(File file, long offsetSeconds) {
        setFile(file);
        setPauseOffsetSeconds(offsetSeconds);
        play();
    }

    public synchronized void setFile(File file) {
        if(file == null) {
            throw new AssertionError();
        } else {
            logger.debug(((Object) ((new StringBuilder("MPLayer.setFile: ")).append(DPUtils.getAbsolutePath(file)).toString())));
            mFile = file;
            setPauseOffsetSeconds(0L);
            return;
        }
    }

    public synchronized void play() {
        if(isPlaying())
            stop();
        mThread = new MPlayerThread(this, mPauseOffsetSeconds);
        mThread.start();
        mTimestampSeconds = getCurrentTimeSeconds() - mPauseOffsetSeconds;
        setPauseOffsetSeconds(0L);
    }

    public synchronized void pause() {
        if(isPlaying()) {
            setPauseOffsetSeconds(getOffsetSeconds());
            stop();
        }
    }

    public synchronized void stop() {
        if(isPlaying()) {
            mThread.terminate();
            while(isPlaying()) 
                try {
                    Thread.sleep(10L);
                }
                catch(InterruptedException interruptedexception) { }
        }
        mThread = null;
        mTimestampSeconds = 0L;
    }

    public Object getMutex() {
        return ((Object) (this));
    }

    public boolean isPlaying() {
        return mThread != null && !mThread.isTerminated();
    }

    public boolean isPaused() {
        return !isPlaying() && mPauseOffsetSeconds > 0L;
    }

    public synchronized void setPauseOffsetSeconds(long offset) {
        mPauseOffsetSeconds = offset;
    }

    public long getPauseOffsetSeconds() {
        return mPauseOffsetSeconds;
    }

    public synchronized void setOffsetSeconds(long offsetSeconds) {
        if(isPlaying()) {
            stop();
            setPauseOffsetSeconds(offsetSeconds);
            play();
        } else {
            setPauseOffsetSeconds(offsetSeconds);
        }
    }

    public synchronized long getOffsetSeconds() {
        return isPlaying() ? getCurrentTimeSeconds() - mTimestampSeconds : 0L;
    }

    private long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    protected void fireExceptionEvent(Throwable e) {
        if(e == null)
            throw new AssertionError();
        logger.debug("Stopping player due to exception.");
        stop();
        MediaPlayerListener l;
        for(Iterator<Object> iterator = mListenerSet.iterator(); iterator.hasNext(); l.exceptionEvent(e))
            l = (MediaPlayerListener)iterator.next();

    }

    protected void fireTrackFinished() {
        logger.debug("Track finished.");
        stop();
        MediaPlayerListener l;
        for(Iterator<Object> iterator = mListenerSet.iterator(); iterator.hasNext(); l.trackFinished())
            l = (MediaPlayerListener)iterator.next();

    }

    static  {
        priorities = (new ProcessPriority[] {
            ProcessPriority.idle, ProcessPriority.belownormal, ProcessPriority.normal, ProcessPriority.abovenormal, ProcessPriority.high, ProcessPriority.realtime
        });
    }
}
