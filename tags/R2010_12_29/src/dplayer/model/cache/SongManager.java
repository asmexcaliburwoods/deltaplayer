// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   SongManager.java

package dplayer.model.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.db4o.ObjectSet;

import dplayer.model.Song;

// Referenced classes of package dplayer.model.cache:
//            CacheManager

public class SongManager {
    private static final Logger logger = Logger.getLogger(SongManager.class);
    private boolean mInitialized;
    private Map<File, Song> mSongMap;
    private List<Song> mModifiedSongList;
    private List<Song> mRemovedSongList;

    SongManager() {
        mSongMap = ((Map<File, Song>) (new HashMap<File, Song>()));
        mModifiedSongList = ((List<Song>) (new ArrayList<Song>()));
        mRemovedSongList = ((List<Song>) (new ArrayList<Song>()));
    }

    synchronized void init() {
        mSongMap.clear();
        mModifiedSongList.clear();
        Song s;
        for(ObjectSet<Song> songs = CacheManager.getObjectContainer().get(((Object) (Song.createNullSong()))); songs.hasNext(); mSongMap.put(s.getFile(), s)) {
            s = songs.next();
            s.setFile(new File(s.getFile().getPath()));
        }

        logger.debug(((Object) ((new StringBuilder("Song cache contains ")).append(mSongMap.size()).append(" entries.").toString())));
        mInitialized = true;
    }

    public synchronized Song get(File file) {
        if(file == null)
            throw new AssertionError();
        if(!mInitialized)
            throw new AssertionError();
        else
            return (Song)mSongMap.get(((Object) (file)));
    }

    public synchronized void set(Song song) {
        if(song == null)
            throw new AssertionError();
        if(!mInitialized) {
            throw new AssertionError();
        } else {
            mSongMap.put(song.getFile(), song);
            mModifiedSongList.add(song);
            return;
        }
    }

    public synchronized void remove(Song song) {
        if(song == null)
            throw new AssertionError();
        if(!mInitialized) {
            throw new AssertionError();
        } 
        mSongMap.remove(song.getFile());
        mModifiedSongList.remove(song);
        mRemovedSongList.add(song);
    }

    public void commit() {
        synchronized (CacheManager.class) {
	        if(!mInitialized)
	            throw new AssertionError();
	        CacheManager.open();
	        try{
		        Song s;
		        for(Iterator<Song> iterator = mModifiedSongList.iterator(); iterator.hasNext(); CacheManager.getObjectContainer().set(((Object) (s))))
		            s = iterator.next();
		
		        mModifiedSongList.clear();
		        for(Iterator<Song> iterator1 = mRemovedSongList.iterator(); iterator1.hasNext(); CacheManager.getObjectContainer().delete(((Object) (s))))
		            s = iterator1.next();
		
		        mRemovedSongList.clear();
	        }finally{
	        	CacheManager.close();
	        }
        }
    }

    public synchronized List<Song> getAllByPath(String path) {
        if((path == null || path.length() <= 0))
            throw new AssertionError();
        if(!mInitialized)
            throw new AssertionError();
        List<Song> result = ((List<Song>) (new ArrayList<Song>()));
        for(Iterator<File> iterator = mSongMap.keySet().iterator(); iterator.hasNext();) {
            File f = iterator.next();
            if(f.getParent().equalsIgnoreCase(path))
                result.add(((mSongMap.get(((Object) (f))))));
        }

        return result;
    }

}
