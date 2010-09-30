// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Player.java

package dplayer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import dplayer.About;
import dplayer.Settings;
import dplayer.lastfm.LastFmController;
import dplayer.media.MPlayer;
import dplayer.media.MediaPlayer;
import dplayer.media.MediaPlayerFactory;
import dplayer.media.MediaPlayerListener;
import dplayer.model.cache.CacheManager;
import dplayer.model.cache.SongManager;

// Referenced classes of package dplayer.model:
//            DirectoryListener, PlayerList, Location, Directory, 
//            Song, SongExt, RepeatMode, PlayerEventListener

public class Player
    implements DirectoryListener, MediaPlayerListener {
    public static enum State {PLAYING,PAUSED,STOPPED}

    public static enum StopReason {APP_EXITING_STOP_REASON,OTHER_STOP_REASON}


    private static final Logger logger = Logger.getLogger(Player.class);
    private Directory mSelectedDirectoryFlat;
    private List<Location> mLocationList;
    private Directory mSelectedDirectory;
    private PlayerList mSongList;
    private MediaPlayer mPlayerImpl;
    private Set<Object> mEventListenerSet;
    private static Player sPlayerInstance;

    public static Player getPlayer() {
        if(sPlayerInstance == null)
            sPlayerInstance = new Player();
        return sPlayerInstance;
    }

    private Player() {
        mLocationList = new ArrayList<Location>();
        mSongList = new PlayerList();
        mEventListenerSet = ((Set<Object>) (new HashSet<Object>()));
        logger.info(((Object) ((new StringBuilder("Creating player for OS ")).append(About.OS).toString())));
        MediaPlayerFactory.registerMediaPlayer("mplayer", MPlayer.class);
        mPlayerImpl = MediaPlayerFactory.getMediaPlayer(Settings.getString(Settings.PLAYER));
        mPlayerImpl.addMediaListener(((MediaPlayerListener) (this)));
    }

    public void init() {
        Location location;
        for(Iterator<Location> iterator = (new ArrayList<Location>(((java.util.Collection<Location>) (mLocationList)))).iterator(); iterator.hasNext(); fireLocationRemovedEvent(location)) {
            location = iterator.next();
            mLocationList.remove(((Object) (location)));
        }

        File roots[];
        if(Settings.getString(Settings.ROOTS).equals("")) {
            roots = File.listRoots();
        } else {
            String sa[] = Settings.getString(Settings.ROOTS).split(",");
            roots = new File[sa.length];
            for(int i = 0; i < sa.length; i++)
                roots[i] = new File(sa[i].trim());

        }
        File afile[];
        int k = (afile = roots).length;
        for(int j = 0; j < k; j++) {
            File root = afile[j];
            if(root.listFiles() != null) {
                logger.info(((Object) ((new StringBuilder("Adding root ")).append(root.getPath()).toString())));
                addLocation(new Location(root.getPath(), root));
            }
        }

    }

    private void addLocation(Location location) {
        if(location == null)
            throw new AssertionError();
        location.getDirectory().addDirectoryListener(this);
        mLocationList.add(location);
        fireLocationAddedEvent(location);
    }

    public List<Location> getLocationList() {
        return mLocationList;
    }

    public void selectDirectory(Directory directory, boolean flat) {
        if(directory == null)
            throw new AssertionError();
        mSelectedDirectory = directory;
        mSelectedDirectory.setDefaultSelectFlat(flat);
        mSelectedDirectoryFlat = flat ? directory : null;
        mSongList.set(mSelectedDirectory.getSongList());
        if(flat)
            addDirectoryListAsFlat(mSelectedDirectory.getDirectoryList());
        fireSongListChangedEvent(directory);
        fireDirectoryChangedEvent(directory);
        selectFirstSong();
    }

    private void selectFirstSong() {
        Song first = mSongList.first();
        if(first != null)
            selectSong(first);
    }

    private void addDirectoryListAsFlat(List<Directory> dirList) {
        Directory directory;
        for(Iterator<Directory> iterator = dirList.iterator(); iterator.hasNext(); addDirectoryListAsFlat(directory.getDirectoryList())) {
            directory = iterator.next();
            mSongList.add(directory.getSongList());
        }

    }

    public Directory getSelectedDirectory() {
        return mSelectedDirectory;
    }

    public void selectSong(Song song) {
        if(song == null)
            throw new AssertionError();
        mSongList.setSelected(song);
        firePlaylistPositionChangedEvent();
        if(mPlayerImpl.isPlaying())
            play();
        else
        if(mPlayerImpl.isPaused())
            stop(StopReason.OTHER_STOP_REASON);
    }

    public Song getSelectedSong() {
        return mSongList.getSelected();
    }

    public void play() {
        playFrom(0L);
    }

    public void playFrom(long offsetSeconds) {
        Song song = mSongList.getSelected();
        if(song != null) {
            logger.debug(((Object) ((new StringBuilder("Playing ")).append(((Object) (song))).toString())));
            SongExt.setLastPlayed(song, new Date());
            SongExt.incPlayCounter(song);
            SongManager sm = CacheManager.getSongManager();
            if(sm != null) {
                sm.set(song);
                sm.commit();
            }
            mPlayerImpl.setFile_playFrom(song.getFile(), offsetSeconds);
            fireStateChangedEvent(State.PLAYING);
            song.setSubmittedToLastFm(false);
            if(LastFmController.isSongSubmittableToLastFm(song))
                LastFmController.getQueue().trackStarted(song);
        }
    }

    public void stop(StopReason stopReason) {
        logger.debug(((Object) ((new StringBuilder("Stopping ")).append(((Object) (mSongList.getSelected()))).toString())));
        mPlayerImpl.stop();
        fireStateChangedEvent(State.STOPPED);
        if(stopReason != StopReason.APP_EXITING_STOP_REASON) {
            Song song = getSelectedSong();
            if(song != null)
                checkedSubmitToLastFm(song);
        }
    }

    public void checkedSubmitToLastFm(Song song) {
        if(song == null)
            throw new AssertionError();
        if(!song.isSubmittedToLastFm()) {
            if(getOffsetSeconds() >= 30L && LastFmController.isSongSubmittableToLastFm(song))
                LastFmController.getQueue().trackPlayed(song);
            song.setSubmittedToLastFm(true);
        }
    }

    public void pause() {
        if(mSongList.getSelected() != null)
            if(mPlayerImpl.isPlaying()) {
                logger.debug(((Object) ((new StringBuilder("Pausing ")).append(((Object) (mSongList.getSelected()))).toString())));
                mPlayerImpl.pause();
                fireStateChangedEvent(State.PAUSED);
            } else {
                logger.debug(((Object) ((new StringBuilder("Resuming ")).append(((Object) (mSongList.getSelected()))).toString())));
                mPlayerImpl.play();
                fireStateChangedEvent(State.PLAYING);
            }
    }

    public void prev() {
        Song prev = mSongList.prev();
        if(prev != null) {
            logger.debug(((Object) ((new StringBuilder("Moving backward to ")).append(((Object) (mSongList.getSelected()))).toString())));
            firePlaylistPositionChangedEvent();
            if(mPlayerImpl.isPlaying() || mPlayerImpl.isPaused())
                play();
        }
    }

    public void next() {
        Song next = mSongList.next();
        if(next != null) {
            logger.debug(((Object) ((new StringBuilder("Moving forward to ")).append(((Object) (mSongList.getSelected()))).toString())));
            firePlaylistPositionChangedEvent();
            if(mPlayerImpl.isPlaying() || mPlayerImpl.isPaused())
                play();
        }
    }

    public void toggleShuffle() {
        mSongList.setShuffle(!mSongList.isShuffle());
        fireSongListChangedEvent(mSelectedDirectory);
        fireShuffleModeChanged();
    }

    public void toggleRepeat() {
        switch(mSongList.getRepeat()) {
        case NONE:
            mSongList.setRepeat(RepeatMode.ONE);
            break;
        case ONE:
            mSongList.setRepeat(RepeatMode.ALL);
            break;
        case ALL:
            mSongList.setRepeat(RepeatMode.NONE);
            break;
        }
        fireRepeatModeChanged();
    }

    public void setTrackPosition(int position) {
        if((position < 0 || position > 100)) {
            throw new AssertionError();
        } else {
            double offset = (mSongList.getSelected().getDurationSeconds() * (long)position) / 100L;
            setOffsetSeconds((long)offset);
            return;
        }
    }

    public void setOffsetSeconds(long offsetSeconds) {
        mPlayerImpl.setOffsetSeconds(offsetSeconds);
    }

    public void toggleTrackSkipping(Song song) {
        if(song == null) {
            throw new AssertionError();
        } else {
            SongExt.setSkip(song, !SongExt.isSkip(song));
            fireSkipListChangedEvent(song);
            return;
        }
    }

    public long getOffsetSeconds() {
        return mPlayerImpl.getOffsetSeconds();
    }

    public File getFile() {
        return mPlayerImpl.getFile();
    }

    public Object getMutex() {
        return mPlayerImpl.getMutex();
    }

    public boolean isPlaying() {
        return mPlayerImpl.isPlaying();
    }

    public void directoryPopulated(Directory.PopulateRequest pr, Directory d) {
        fireDirectoryPopulatedEvent(pr, d);
    }

    public void songListCleared(Directory.PopulateRequest pr) {
        fireSongListCleared(pr);
    }

    public void songAdded(Directory.PopulateRequest pr, Song s) {
        firePlaylistItemAppended(pr, s);
    }

    public void trackFinished() {
        Song next = mSongList.next();
        if(next != null) {
            logger.debug(((Object) ((new StringBuilder("Moving forward to ")).append(((Object) (mSongList.getSelected()))).toString())));
            firePlaylistPositionChangedEvent();
            play();
        } else {
            fireStateChangedEvent(State.STOPPED);
        }
    }

    public void exceptionEvent(Throwable e) {
        fireStateChangedEvent(State.STOPPED);
        fireExceptionEvent(e);
    }

    public void addEventListener(PlayerEventListener listener) {
        if(listener == null) {
            throw new AssertionError();
        } else {
            mEventListenerSet.add(((Object) (listener)));
            return;
        }
    }

    private void fireExceptionEvent(Throwable e) {
        if(e == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.exceptionEvent(e))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireLocationAddedEvent(Location location) {
        if(location == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.locationAddedEvent(location))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireLocationRemovedEvent(Location location) {
        if(location == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.locationRemovedEvent(location))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireDirectoryChangedEvent(Directory directory) {
        if(directory == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.directoryChangedEvent(directory))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireDirectoryPopulatedEvent(Directory.PopulateRequest populateRequest, Directory d) {
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.directoryPopulatedEvent(populateRequest, d))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireSongListChangedEvent(Directory directory) {
        if(mSongList == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.songListChangedEvent(directory, mSongList.getList()))
            l = (PlayerEventListener)iterator.next();

    }

    private void firePlaylistItemAppended(Directory.PopulateRequest pr, Song s) {
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.playlistItemAppended(pr, s))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireSongListCleared(Directory.PopulateRequest pr) {
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.playlistCleared(pr))
            l = (PlayerEventListener)iterator.next();

    }

    private void firePlaylistPositionChangedEvent() {
        Song song = mSongList.getSelected();
        if(song == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.playlistPositionChangedEvent(song))
            l = (PlayerEventListener)iterator.next();

    }

    public void fireSongDetailsChangedEvent(Song song) {
        if(song == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.songDetailsChangedEvent(song))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireSkipListChangedEvent(Song song) {
        if(song == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.skipListChangedEvent(song))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireStateChangedEvent(State state) {
        if(state == null)
            throw new AssertionError();
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.stateChangedEvent(state))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireShuffleModeChanged() {
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.shuffleModeChanged(mSongList.isShuffle()))
            l = (PlayerEventListener)iterator.next();

    }

    private void fireRepeatModeChanged() {
        PlayerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.repeatModeChanged(mSongList.getRepeat()))
            l = (PlayerEventListener)iterator.next();

    }

    public long getPauseOffsetSeconds() {
        return mPlayerImpl.getPauseOffsetSeconds();
    }

    public Directory getSelectedDirectoryFlat() {
        return mSelectedDirectoryFlat;
    }

    public int getSongListSize() {
        return mSongList.getList().size();
    }
}
