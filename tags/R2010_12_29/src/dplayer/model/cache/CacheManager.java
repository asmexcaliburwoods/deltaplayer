// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   CacheManager.java

package dplayer.model.cache;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import dplayer.Settings;
import dplayer.SettingsAdapter;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.model.cache:
//            SongManager, DirectoryManager

public class CacheManager {

    private static final Logger logger = Logger.getLogger(CacheManager.class);
    private static ObjectContainer mObjectContainer;
    private static SongManager sSongManager;
    private static DirectoryManager sDirectoryManager;

    public CacheManager() {
    }

    static synchronized void open() {
        if(mObjectContainer != null) {
            throw new AssertionError();
        } else {
            String fileName = Settings.getString(Settings.CACHE_SONGS_FILE);
            logger.debug(((Object) ((new StringBuilder("Opening cache file ")).append(fileName).append("...").toString())));
            mObjectContainer = Db4o.openFile(fileName);
            return;
        }
    }

    static synchronized void close() {
        if(mObjectContainer != null) {
            logger.debug("Closing cache file.");
            mObjectContainer.close();
            mObjectContainer = null;
        }
    }

    public static boolean isEnabled() {
        return SettingsAdapter.getInstance().isCacheEnabled();
    }

    public static void init() {
        if(!isEnabled())
            throw new AssertionError();
        open();
        try{
	        getSongManager().init();
	        getDirectoryManager().init();
        }finally{
        	close();
        }
    }

    public static synchronized SongManager getSongManager() {
        if(isEnabled() && sSongManager == null)
            sSongManager = new SongManager();
        return sSongManager;
    }

    public static synchronized DirectoryManager getDirectoryManager() {
        if(isEnabled() && sDirectoryManager == null)
            sDirectoryManager = new DirectoryManager();
        return sDirectoryManager;
    }

    public static synchronized void setEnabled(boolean enabled) {
        if(enabled) {
            init();
        } else {
            sSongManager = null;
            sDirectoryManager = null;
        }
    }

    static ObjectContainer getObjectContainer() {
        return mObjectContainer;
    }

}
