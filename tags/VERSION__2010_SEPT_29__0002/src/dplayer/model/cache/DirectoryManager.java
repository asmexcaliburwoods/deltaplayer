// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   DirectoryManager.java

package dplayer.model.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.db4o.ObjectSet;

import dplayer.DPUtils;
import dplayer.model.DirectorySettings;

// Referenced classes of package dplayer.model.cache:
//            CacheManager

public class DirectoryManager {

    private static final Logger logger = Logger.getLogger(DirectoryManager.class);
    private boolean mInitialized;
    private Map<Object, Object> mMap;
    private List<Object> mModifiedList;
    private List<Object> mRemovedList;

    DirectoryManager() {
        mMap = ((Map<Object, Object>) (new HashMap<Object, Object>()));
        mModifiedList = ((List<Object>) (new ArrayList<Object>()));
        mRemovedList = ((List<Object>) (new ArrayList<Object>()));
    }

    synchronized void init() {
        mMap.clear();
        mModifiedList.clear();
        DirectorySettings s;
        for(ObjectSet<DirectorySettings> set = CacheManager.getObjectContainer().get(((Object) (new DirectorySettings()))); set.hasNext(); mMap.put(((Object) (s.getFile())), ((Object) (s))))
            s = set.next();

        logger.debug(((Object) ((new StringBuilder("Dir cache contains ")).append(mMap.size()).append(" entries.").toString())));
        mInitialized = true;
    }

    public synchronized DirectorySettings get(File file) {
        if(file == null)
            throw new AssertionError();
        if(!mInitialized)
            throw new AssertionError();
        else
            return (DirectorySettings)mMap.get(((Object) (DPUtils.getAbsolutePath(file))));
    }

    public synchronized void set(DirectorySettings ds) {
        if(ds == null)
            throw new AssertionError();
        if(!mInitialized) {
            throw new AssertionError();
        } else {
            mMap.put(((Object) (ds.getFile())), ((Object) (ds)));
            mModifiedList.add(((Object) (ds)));
        }
    }

    public synchronized void remove(DirectorySettings ds) {
        if(ds == null)
            throw new AssertionError();
        if(!mInitialized) {
            throw new AssertionError();
        } else {
            mMap.remove(((Object) (ds.getFile())));
            mModifiedList.remove(((Object) (ds)));
            mRemovedList.add(((Object) (ds)));
        }
    }

    public void commit() {
        synchronized (CacheManager.class) {
	        if(!mInitialized)throw new AssertionError();
	        CacheManager.open();
        	try{
		        DirectorySettings s;
		        for(Iterator<Object> iterator = mModifiedList.iterator(); iterator.hasNext(); CacheManager.getObjectContainer().set(((Object) (s))))
		            s = (DirectorySettings)iterator.next();
		        mModifiedList.clear();
		        for(Iterator<Object> iterator1 = mRemovedList.iterator(); iterator1.hasNext(); CacheManager.getObjectContainer().delete(((Object) (s))))
		            s = (DirectorySettings)iterator1.next();
		        mRemovedList.clear();
        	}finally{
	        	CacheManager.close();
	        }
	    }
	}
}