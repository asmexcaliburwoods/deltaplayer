// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Directory.java

package dplayer.model;

import dplayer.DPUtils;
import dplayer.gui.ExceptionUtil;
import dplayer.gui.MainArea;
import dplayer.model.cache.CacheManager;
import dplayer.model.cache.DirectoryManager;
import dplayer.model.cache.SongManager;
import entagged.audioformats.*;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.generic.Utils;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.model:
//            DirectorySettings, Song, Player, SongExt, 
//            DirectoryListener

public class Directory {
    private static class DirectoryRunnable
        implements Runnable {

        public void run() {
        	QueueItem item;
			while(true){
				try{
		            for(;;){
		            	item = (QueueItem)Directory.queue.poll();
		            	if(item != null)
		            		break;
			            SongManager sm = CacheManager.getSongManager();
			            if(sm != null)sm.commit();
			            synchronized(Directory.queue) {
			                if(Directory.queue.peek() == null)
			                    Directory.queue.wait();
			            }
		            }
		            Directory.processQueueItem(item);
				}catch(Throwable tr){
					ExceptionUtil.handleException(tr);
				}
	            try {
	                Thread.sleep(10L);
	            }
	            catch(InterruptedException e) {
	                ExceptionUtil.handleException(e);
	            }
	            if(!Directory.applicationExiting) continue; else return;
			}
        }

        private DirectoryRunnable() {}
    }

    public static class PopulateRequest {
        public final Directory rootPopulateCall;
        public final boolean selectFirstSong;
        public volatile boolean aborted;
        public boolean finished;
        public final boolean expand;

        public synchronized void finished() {
            finished = true;
            ((Object)this).notifyAll();
        }

        private synchronized void abort() {
            aborted = true;
            ((Object)this).notifyAll();
        }

        public synchronized void waitUntilFinished() throws InterruptedException {
            while(!finished && !aborted) 
                ((Object)this).wait();
        }


        public PopulateRequest(Directory rootPopulateCall, boolean selectFirstSong, boolean expand) {
            this.rootPopulateCall = rootPopulateCall;
            this.selectFirstSong = selectFirstSong;
            this.expand = expand;
        }
    }

    private static class QueueItem {

        public Song song;

        private QueueItem() {
        }

        QueueItem(QueueItem queueitem) {
            this();
        }
    }


    private static final Logger logger = Logger.getLogger(Directory.class);
    private File mFile;
    private boolean mPopulated;
    private List<Directory> mDirectoryList;
    private List<Song> mSongList;
    private Set<DirectoryListener> mDirectoryListenerSet;
    private boolean mPopulatedFlat;
    private static PopulateRequest currentPopulateRequest;
    private static final Queue<Object> queue = new LinkedBlockingQueue<Object>();
    private static volatile boolean applicationExiting;
    private static final DirectoryRunnable directoryRunnable;

    public Directory(File file) {
        this(file, (Set<DirectoryListener>)null);
    }

    private Directory(File file, Set<DirectoryListener> changedListenerSet) {
        mDirectoryList = ( (new ArrayList<Directory>()));
        mSongList = ((new ArrayList<Song>()));
        if(file == null)
            throw new AssertionError();
        mFile = file;
        if(changedListenerSet==null)changedListenerSet=new HashSet<DirectoryListener>();
        mDirectoryListenerSet = changedListenerSet;
    }

    public boolean isDefaultSelectFlat() {
        DirectoryManager directoryManager = CacheManager.getDirectoryManager();
        if(directoryManager == null) {
            return false;
        } else {
            DirectorySettings directorySettings = directoryManager.get(new File(getPath()));
            return directorySettings != null ? directorySettings.isDefaultFlat() : false;
        }
    }

    public void setDefaultSelectFlat(boolean flat) {
        DirectoryManager directoryManager = CacheManager.getDirectoryManager();
        if(directoryManager != null) {
            DirectorySettings ds = new DirectorySettings();
            ds.setFile(DPUtils.getAbsolutePath(new File(getPath())));
            if(flat) {
                ds.setDefaultFlat(flat);
                directoryManager.set(ds);
            } else {
                directoryManager.remove(ds);
            }
            directoryManager.commit();
        }
    }

    public boolean isPopulatedFlat() {
        return mPopulatedFlat;
    }

    public synchronized void populate(final boolean flat, boolean async, final PopulateRequest populateRequest) {
        if(!populateRequest.expand)
            synchronized(PopulateRequest.class) {
                if(currentPopulateRequest != null)
                    currentPopulateRequest.abort();
                currentPopulateRequest = populateRequest;
            }
        if(flat && !isPopulatedFlat() || !flat && !isPopulated()) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        if(flat && !isPopulatedFlat() || !flat && !isPopulated())
                            populate(flat, populateRequest);
                    }
                    catch(Exception e) {
                        ExceptionUtil.handleException(((Throwable) (e)));
                    }
                }
            };
            if(async) {
                new Thread(r).start();
            } else {
                r.run();
                try {
                    populateRequest.waitUntilFinished();
                }
                catch(InterruptedException e) {
                    ExceptionUtil.handleException(((Throwable) (e)));
                }
            }
        }
    }

    public static void clearQueue() {
        queue.clear();
    }

    public static void setApplicationExiting(boolean applicationExiting) {
        Directory.applicationExiting = applicationExiting;
        notifyQueue();
    }

    private static void addToQueue(QueueItem queueItem) {
        queue.add(((Object) (queueItem)));
        notifyQueue();
    }

    private static void notifyQueue() {
        synchronized(queue) {
            ((Object) (queue)).notify();
        }
    }

    private static void processQueueItem(QueueItem item) throws Exception {
        Song s = item.song;
        File f = s.getFile();
        try {
            SongManager sm = CacheManager.getSongManager();
            AudioFile audioFile = AudioFileIO.read(f);
            s.setDurationSeconds(audioFile.getLength());
            s.setBitRate(audioFile.getBitrate());
            s.setSampleRate(audioFile.getSamplingRate());
            Tag tag = audioFile.getTag();
            if(!tag.isEmpty()) {
                logger.debug(((Object) ((new StringBuilder("  Adding ")).append(f.getPath()).append(" using tag information.").toString())));
                s.setAlbum(tag.getFirstAlbum());
                s.setArtist(tag.getFirstArtist());
                s.setTitle(tag.getFirstTitle());
                try {
                    String track = tag.getFirstTrack();
                    StringTokenizer st = new StringTokenizer(track, "/");
                    if(st.hasMoreTokens())
                        s.setTrack(Integer.parseInt(st.nextToken()));
                }
                catch(NumberFormatException numberformatexception) { }
                s.setYear(tag.getFirstYear());
            } else {
                logger.debug(((Object) ((new StringBuilder("  Adding ")).append(f.getPath()).append(" due to its file extension.").toString())));
            }
            Player.getPlayer().fireSongDetailsChangedEvent(s);
            if(sm != null) {
                SongExt.setLastScanned(s, Long.valueOf(f.lastModified()));
                if(SongExt.getInsertDate(s) == null)
                    SongExt.setInsertDate(s, new Date());
                sm.set(s);
            }
        }
        catch(CannotReadException e) {
            logger.debug(((Object) ((new StringBuilder("  Ignoring ")).append(f.getPath()).append(" due to problem: ").append(e.getMessage()).toString())));
        }
    }

    private synchronized void populate(boolean flat, PopulateRequest populateRequest) {
        logger.debug(((Object) ((new StringBuilder("Populating directory ")).append(mFile.getPath()).append(" (flat = ").append(flat).append(")").toString())));
        long startTimestamp = System.currentTimeMillis();
        mDirectoryList.clear();
        synchronized(this) {
            mSongList.clear();
        }
        if(populateRequest.aborted)
            return;
        if(populateRequest.rootPopulateCall == this && !populateRequest.expand)
            fireSongListCleared(populateRequest);
        SongManager sm = CacheManager.getSongManager();
        FileFilter ff = ((FileFilter) (new AudioFileFilter()));
        if(populateRequest.aborted)
            return;
        File list[] = mFile.listFiles(ff);
        if(populateRequest.aborted)
            return;
        Arrays.sort(list, new Comparator<Object>() {
            public int compare(File o1, File o2) {
                int dir1 = o1.isDirectory() ? 1 : 0;
                int dir2 = o2.isDirectory() ? 1 : 0;
                if(dir1 != dir2) {
                    return dir1 - dir2;
                } else {
                    String n1 = o1.getName();
                    String n2 = o2.getName();
                    return n1.compareToIgnoreCase(n2);
                }
            }

            public int compare(Object obj, Object obj1) {
                return compare((File)obj, (File)obj1);
            }
        });
        File afile[];
        int j = (afile = list).length;
        for(int i = 0; i < j; i++) {
            File f = afile[i];
            if(populateRequest.aborted)
                return;
            if(f.isDirectory())
                populateWithDirectory(f, flat, populateRequest);
            else
            if(f.isFile())
                populateWithFile(f, sm, populateRequest);
        }

        cleanupStaleEntriesInCache(sm);
        logger.debug(((Object) ((new StringBuilder("  Populating took ")).append(System.currentTimeMillis() - startTimestamp).append("ms.").toString())));
        mPopulated = true;
        if(flat)
            mPopulatedFlat = true;
        fireDirectoryPopulated(populateRequest);
    }

    private synchronized void cleanupStaleEntriesInCache(SongManager sm) {
        if(sm != null) {
            List<Song> cachedSongs = sm.getAllByPath(mFile.getPath());
            for(Iterator<Song> iterator = cachedSongs.iterator(); iterator.hasNext();) {
                Song s = iterator.next();
                if(!mSongList.contains(((Object) (s)))) {
                    logger.debug(((Object) ((new StringBuilder("  Deleting stale file ")).append(s.getFilename()).append(" from cache.").toString())));
                    sm.remove(s);
                }
            }

        }
    }

    private void populateWithFile(File f, SongManager sm, PopulateRequest populateRequest) {
        if(populateRequest.aborted)
            return;
        Song s;
        if(sm != null) {
            s = sm.get(f);
            if(s != null) {
                addSong(s, populateRequest);
                return;
            }
        }
        s = new Song(f);
        addSong(s, populateRequest);
    }

    public static void addToFetchInfoQueue(Song s) {
        QueueItem item = new QueueItem(((QueueItem) (null)));
        item.song = s;
        addToQueue(item);
    }

    private void populateWithDirectory(File f, boolean flat, PopulateRequest populateRequest) {
        Directory d = new Directory(f, mDirectoryListenerSet);
        if(flat)
            d.populate(flat, populateRequest);
        if(populateRequest.aborted) {
            return;
        } else {
            addDirectory(d);
            return;
        }
    }

    private void addDirectory(Directory directory) {
        if(directory == null) {
            throw new AssertionError();
        } else {
            mDirectoryList.add(directory);
            return;
        }
    }

    private void addSong(Song song, PopulateRequest populateRequest) {
        if(song == null)
            throw new AssertionError();
        synchronized(this) {
            mSongList.add(song);
        }
        if(populateRequest.aborted)
            return;
        if(!populateRequest.expand)
            fireSongAdded(populateRequest, song);
    }

    public String getName() {
        return mFile.getName();
    }

    public String getPath() {
        String path = mFile.getPath();
        return normalizePath(path);
    }

    public static String normalizePath(String path) {
        return path.replaceAll("[\\\\]", "/");
    }

    public boolean isPopulated() {
        return mPopulated;
    }

    public List<Directory> getDirectoryList() {
        return mDirectoryList;
    }

    public boolean hasDirectories() {
        return mDirectoryList.size() > 0;
    }

    public synchronized List<Song> getSongList() {
        return mSongList;
    }

    public boolean hasSongs() {
        return hasSongs(false);
    }

    public boolean hasSongs(boolean recursive) {
        boolean result;
        synchronized(this) {
            result = mSongList.size() > 0;
        }
        if(!result && recursive) {
            for(Iterator<Directory> iterator = mDirectoryList.iterator(); iterator.hasNext();) {
                Directory d = iterator.next();
                result = d.hasSongs(true);
                if(result)
                    break;
            }

        }
        return result;
    }

    public synchronized Song findSong(String name) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        for(Iterator<Song> iterator = mSongList.iterator(); iterator.hasNext();) {
            Song song = iterator.next();
            if(song.getFilename().equalsIgnoreCase(name))
                return song;
        }

        return null;
    }

    public synchronized Song findSongByPath(File path, boolean recurse) {
        if(path==null)return null;
        for(Song song : mSongList){
            if(song.getFile().equals(path))return song;
        }

        if(recurse) {
            for(Iterator<Directory> iterator1 = mDirectoryList.iterator(); iterator1.hasNext();) {
                Directory d = iterator1.next();
                Song s = d.findSongByPath(path, recurse);
                if(s != null)
                    return s;
            }

        }
        return null;
    }

    public File[] getImageFiles() {
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                if(f.isHidden() || !f.canRead() || f.isDirectory())
                    return false;
                String ext = Utils.getExtension(f).toLowerCase();
                return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("bmp");
            }
        };
        File files[] = mFile.listFiles(filter);
        File metaDir = new File(mFile, "!META");
        File metaDir2 = new File(mFile, "..");
        File metaDir3 = new File(mFile, "../!META");
        File filesMeta[] = metaDir.listFiles(filter);
        File filesMeta2[] = metaDir2.listFiles(filter);
        File filesMeta3[] = metaDir3.listFiles(filter);
        List<Object> list1 = ((List<Object>) (new ArrayList<Object>((Collection<Object>)Arrays.asList(((Object []) (files != null ? ((Object []) (files)) : ((Object []) (new File[0]))))))));
        List<Object> list2 = Arrays.asList(((Object []) (filesMeta != null ? ((Object []) (filesMeta)) : ((Object []) (new File[0])))));
        List<Object> list3 = Arrays.asList(((Object []) (filesMeta2 != null ? ((Object []) (filesMeta2)) : ((Object []) (new File[0])))));
        List<Object> list4 = Arrays.asList(((Object []) (filesMeta3 != null ? ((Object []) (filesMeta3)) : ((Object []) (new File[0])))));
        list1.addAll(((Collection<Object>) (list2)));
        list1.addAll(((Collection<Object>) (list3)));
        list1.addAll(((Collection<Object>) (list4)));
        return (File[])list1.toArray(((Object []) (new File[list1.size()])));
    }

    public void addDirectoryListener(DirectoryListener listener) {
        if(listener == null) {
            throw new AssertionError();
        } else {
            mDirectoryListenerSet.add(listener);
            return;
        }
    }

    private void fireDirectoryPopulated(PopulateRequest populateRequest) {
        DirectoryListener l;
        for(Iterator<DirectoryListener> iterator = mDirectoryListenerSet.iterator(); iterator.hasNext(); l.directoryPopulated(populateRequest, this))
            l = iterator.next();

    }

    private void fireSongAdded(PopulateRequest populateRequest, Song s) {
        DirectoryListener l;
        for(Iterator<DirectoryListener> iterator = mDirectoryListenerSet.iterator(); iterator.hasNext(); l.songAdded(populateRequest, s))
            l = iterator.next();

    }

    private void fireSongListCleared(PopulateRequest populateRequest) {
        DirectoryListener l;
        for(Iterator<DirectoryListener> iterator = mDirectoryListenerSet.iterator(); iterator.hasNext(); l.songListCleared(populateRequest))
            l = iterator.next();

    }

    public String toString() {
        return mFile.getName();
    }

    static  {
        directoryRunnable = new DirectoryRunnable();
        (new Thread(((Runnable) (directoryRunnable)))).start();
    }




}
