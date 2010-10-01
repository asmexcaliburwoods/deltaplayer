// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Controller.java

package dplayer.gui;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import dplayer.Settings;
import dplayer.lastfm.LastFmController;
import dplayer.model.Directory;
import dplayer.model.Location;
import dplayer.model.Mixer;
import dplayer.model.Player;
import dplayer.model.Song;
import dplayer.model.cache.CacheManager;
import dplayer.model.cache.SongManager;

// Referenced classes of package dplayer.gui:
//            MainShell, ToolBar, MainArea

class Controller {
    private static final Logger logger = Logger.getLogger(Controller.class);
    private Directory mSelectedDirectory;
    private boolean mFlat;
    private static Controller sInstance;
    private volatile boolean mApplicationExiting;

    public static boolean isApplicationExiting() {
        return getController().mApplicationExiting;
    }

    public static Controller getController() {
        if(sInstance == null)
            sInstance = new Controller();
        return sInstance;
    }

    private Controller() {
    }

    void init(List<File> files) {
        LastFmController.init();
        LastFmController.getQueue().configure(Settings.getBoolean(Settings.LAST_FM_ENABLED), Settings.getString(Settings.LAST_FM_USER_NAME), Settings.getString(Settings.LAST_FM_PASSWORD));
        Player player = Player.getPlayer();
        player.init();
        if(files.size() > 0) {
            File file = files.get(0);
            if(file.isDirectory()) {
                String dir = file.getPath().replaceAll("[\\\\]", "/");
                selectDirectoryConditional(dir, false, false);
                play();
            } else
            if(file.isFile()) {
                String dir = file.getParent().replaceAll("[\\\\]", "/");
                String name = file.getName();
                selectDirectory(dir, false, false);
                selectSong(name);
                play();
            }
        } else {
            String lastFlatDir = Settings.getString(Settings.HISTORY_LAST_FLAT_DIR);
            String lastFile = Settings.getString(Settings.HISTORY_LAST_FILE);
            boolean flat = lastFlatDir != null && lastFlatDir.length() > 0;
            if(flat) {
                lastFlatDir = Directory.normalizePath(lastFlatDir);
                Directory d = findDirectory(lastFlatDir);
                if(d != null)
                    selectDirectory(d, true, false, false);
            } else
            if(lastFile != null && lastFile.length() > 0) {
                File f = new File(lastFile);
                File folder = f.getParentFile();
                String parent = folder.getPath();
                parent = Directory.normalizePath(parent);
                selectDirectory(parent, false, false);
            }
            if(lastFile != null && lastFile.length() > 0)
                selectSongByPath(new File(lastFile), flat);
            long offset = Settings.getLong(Settings.HISTORY_LAST_OFFSET_SECONDS);
            player.playFrom(offset);
            updateToolbarSlider();
        }
    }

    public void updateToolbarSlider() {
        MainShell.getInstance().getToolBar().updateSlider();
    }

    public void deinit() {
        Player player = Player.getPlayer();
        synchronized(player.getMutex()) {
            long offset = player.getOffsetSeconds();
            if(offset == 0L)
                offset = player.getPauseOffsetSeconds();
            Settings.setLong(Settings.HISTORY_LAST_OFFSET_SECONDS, offset);
            File song = player.getFile();
            if(song != null)
                Settings.setString(Settings.HISTORY_LAST_FILE, song.getAbsolutePath());
        }
        getController().stop(dplayer.model.Player.StopReason.APP_EXITING_STOP_REASON);
        Directory.setApplicationExiting(true);
        LastFmController.deinit();
        Shell shell = MainShell.getShell();
        logger.info(((Object) ((new StringBuilder("Saving settings to ")).append(Settings.getFilename()).toString())));
        long startTime = System.currentTimeMillis();
        Settings.save();
        logger.info(((Object) ((new StringBuilder("Saved (")).append(System.currentTimeMillis() - startTime).append(" ms).").toString())));
        if(!shell.isDisposed())
            shell.dispose();
    }

    void expand(Directory directory) {
        if(directory == null) {
            throw new AssertionError();
        } else {
            logger.debug(((Object) ((new StringBuilder("Expand: ")).append(((Object) (directory))).toString())));
            directory.populate(false, false, new dplayer.model.Directory.PopulateRequest(directory, false, true));
            return;
        }
    }

    void refresh() {
        Player player = Player.getPlayer();
        File lastPlaying = player.getFile();
        long ofs = player.getOffsetSeconds();
        if(ofs == 0L)
            ofs = player.getPauseOffsetSeconds();
        Directory flat = player.getSelectedDirectoryFlat();
        Directory dir = null;
        if(flat == null)
            dir = player.getSelectedDirectory();
        player.stop(dplayer.model.Player.StopReason.OTHER_STOP_REASON);
        player.init();
        boolean bFlat = flat != null;
        String selectDirPath;
        if(bFlat)
            selectDirPath = flat.getPath();
        else
            selectDirPath = dir.getPath();
        Directory d = findDirectory(selectDirPath);
        if(d != null) {
            selectDirectory(d, bFlat, false, false);
            selectSongByPath(lastPlaying, bFlat);
            if(ofs != 0L)
                player.playFrom(ofs);
        }
    }

    void selectDirectoryConditional(Directory directory, boolean async, boolean selectFirstSong) {
        selectDirectory(directory, directory.isDefaultSelectFlat(), async, selectFirstSong);
    }

    void selectDirectory(Directory directory, boolean flat, boolean async, boolean selectFirstSong) {
        if(directory == null)
            throw new AssertionError();
        logger.debug(((Object) ((new StringBuilder("Select directory: ")).append(directory.getName()).append(", flat=").append(flat).toString())));
        if(mSelectedDirectory == directory && flat == mFlat) {
            logger.debug("  directory already selected.");
            return;
        } else {
            mSelectedDirectory = directory;
            mFlat = flat;
            Settings.setString(Settings.HISTORY_LAST_FLAT_DIR, flat ? directory.getPath() : "");
            Player.getPlayer().selectDirectory(directory, flat);
            MainShell.setWaitCursor();
            directory.populate(flat, async, new dplayer.model.Directory.PopulateRequest(directory, selectFirstSong, false));
            MainShell.setNormalCursor();
            return;
        }
    }

    private void selectDirectory(String path, boolean async, boolean selectFirstSong) {
        Directory loc = findDirectory(path);
        if(loc != null) {
            logger.debug(((Object) ((new StringBuilder("Found directory ")).append(loc.getPath()).toString())));
            selectDirectory(loc, false, async, selectFirstSong);
        }
    }

    private void selectDirectoryConditional(String path, boolean async, boolean selectFirstSong) {
        Directory loc = findDirectory(path);
        if(loc != null) {
            logger.debug(((Object) ((new StringBuilder("Found directory ")).append(loc.getPath()).toString())));
            selectDirectoryConditional(loc, async, selectFirstSong);
        }
    }

    private Directory findDirectory(String path) {
        if((path == null || path.length() <= 0))
            throw new AssertionError();
        String pa[] = path.split("[/]");
        Directory loc = null;
        String find = pa.length <= 0 ? "/" : (new StringBuilder(String.valueOf(((Object) (pa[0]))))).append("/").toString();
        for(Iterator<Location> iterator = Player.getPlayer().getLocationList().iterator(); iterator.hasNext();) {
            Location l = iterator.next();
            if(l.getDirectory().getPath().equalsIgnoreCase(find)) {
                loc = l.getDirectory();
                break;
            }
        }

        for(int depth = 1; loc != null && depth < pa.length; depth++) {
            find = (new StringBuilder(String.valueOf(((Object) (find))))).append(pa[depth]).toString();
            selectDirectory(loc, false, false, false);
            List<Directory> dirList = loc.getDirectoryList();
            loc = null;
            for(Iterator<Directory> iterator1 = dirList.iterator(); iterator1.hasNext();) {
                Directory d = iterator1.next();
                if(d.getPath().equalsIgnoreCase(find)) {
                    loc = d;
                    break;
                }
            }

            find = (new StringBuilder(String.valueOf(((Object) (find))))).append("/").toString();
        }

        return loc;
    }

    void selectSong(Song song) {
        if(song == null) {
            throw new AssertionError();
        } else {
            logger.debug(((Object) ((new StringBuilder("Select song: ")).append(((Object) (song))).toString())));
            Player.getPlayer().selectSong(song);
            return;
        }
    }

    private void selectSong(String name) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        if(mSelectedDirectory != null) {
            Song song = mSelectedDirectory.findSong(name);
            if(song != null)
                selectSong(song);
        }
    }

    private void selectSongByPath(File path, boolean recurse) {
        if((path == null || path.length() <= 0L))
            throw new AssertionError();
        if(mSelectedDirectory != null) {
            Song song = mSelectedDirectory.findSongByPath(path, recurse);
            if(song != null)
                selectSong(song);
        }
    }

    void play() {
        Player.getPlayer().play();
    }

    void stop(dplayer.model.Player.StopReason stopReason) {
        Player.getPlayer().stop(stopReason);
    }

    void pause() {
        Player.getPlayer().pause();
    }

    void checkPlayedTime() {
        Player p = Player.getPlayer();
        Song song = p.getSelectedSong();
        if(song != null) {
            long playTimeSeconds = p.getOffsetSeconds();
            if(playTimeSeconds >= 240L || playTimeSeconds >= song.getDurationSeconds() / 2L)
                p.checkedSubmitToLastFm(song);
        }
    }

    void prev() {
        Player.getPlayer().prev();
    }

    void next() {
        Player.getPlayer().next();
    }

    void toggleShuffle() {
        Player.getPlayer().toggleShuffle();
    }

    void toggleRepeat() {
        Player.getPlayer().toggleRepeat();
    }

    void setVolume(int level) {
        Mixer.getMixer().setVolume(level);
    }

    void volumeDown() {
        Mixer.getMixer().volumeDown();
    }

    void volumeUp() {
        Mixer.getMixer().volumeUp();
    }

    void toggleMute() {
        Mixer.getMixer().toggleMute();
    }

    void setTrackPosition(int position) {
        if((position < 0 || position > 100)) {
            throw new AssertionError();
        } else {
            Player.getPlayer().setTrackPosition(position);
            return;
        }
    }

    void toggleTrackSkipping(Song song) {
        if(song == null)
            throw new AssertionError();
        Player.getPlayer().toggleTrackSkipping(song);
        if(CacheManager.isEnabled()) {
            SongManager sm = CacheManager.getSongManager();
            sm.set(song);
            sm.commit();
        }
    }

    void toggleDisplaySkipped() {
        Settings.setBoolean(Settings.DISPLAY_SKIPPED, !Settings.getBoolean(Settings.DISPLAY_SKIPPED));
        MainArea.getInstance().refreshTable();
    }

    public void setApplicationExiting(boolean applicationExiting) {
        mApplicationExiting = applicationExiting;
    }

}
