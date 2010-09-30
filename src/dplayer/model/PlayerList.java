// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   PlayerList.java

package dplayer.model;

import dplayer.SettingsAdapter;
import java.util.*;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.model:
//            Song, SongExt, RepeatMode

public class PlayerList {

    private static final Logger logger = Logger.getLogger(PlayerList.class);
    private List<List<Song>> mLists;
    private List<Song> mFlatList;
    private Song mSelected;
    private boolean mShuffle;
    private RepeatMode mRepeat;

    public PlayerList() {
        mLists = ((List<List<Song>>) (new LinkedList<List<Song>>()));
        mFlatList = ((List<Song>) (new ArrayList<Song>()));
        mShuffle = SettingsAdapter.getInstance().getShuffleMode();
        mRepeat = SettingsAdapter.getInstance().getRepeatMode();
    }

    void set(List<Song> list) {
        if(list == null) {
            throw new AssertionError();
        } else {
            logger.debug("Dropping play list...");
            mLists.clear();
            add(list);
            mSelected = mFlatList.size() <= 0 ? null : mFlatList.get(0);
            return;
        }
    }

    void add(List<Song> list) {
        if(list == null) {
            throw new AssertionError();
        } else {
            Collections.sort(list);
            logger.debug("Adding to play list...");
            mLists.add(list);
            setShuffle(isShuffle());
            return;
        }
    }

    void setSelected(Song song) {
        if(!mFlatList.contains(song)) {
            throw new AssertionError();
        } else {
            logger.debug(((Object) ((new StringBuilder("Changing selected song: ")).append(((Object) (song))).toString())));
            mSelected = song;
            return;
        }
    }

    Song getSelected() {
        return mSelected;
    }

    List<Song> getList() {
        return mFlatList;
    }

    Song first() {
        return mFlatList.size() <= 0 ? null : mFlatList.get(0);
    }

    Song prev() {
        for(int index = mFlatList.indexOf(((Object) (mSelected))) - 1; index >= 0; index--) {
            Song s = mFlatList.get(index);
            if(!SongExt.isSkip(s)) {
                setSelected(s);
                return s;
            }
        }

        return null;
    }

    private Song nextNotSkipped(int startIndex) {
        for(int index = startIndex; index < mFlatList.size(); index++) {
            Song s = mFlatList.get(index);
            if(!SongExt.isSkip(s))
                return s;
        }

        return null;
    }

    Song next() {
        if(mRepeat == RepeatMode.ONE)
            return mSelected;
        Song s = nextNotSkipped(mFlatList.indexOf(((Object) (mSelected))) + 1);
        if(s != null) {
            setSelected(s);
            return s;
        }
        if(mRepeat == RepeatMode.ALL) {
            s = nextNotSkipped(0);
            if(s != null) {
                setSelected(s);
                return s;
            }
        }
        return null;
    }

    private void shuffle() {
        Collections.shuffle(mFlatList);
    }

    private void sort() {
        mFlatList.clear();
        List<Song> list;
        for(Iterator<List<Song>> iterator = mLists.iterator(); iterator.hasNext(); mFlatList.addAll(((java.util.Collection<Song>) (list))))
            list = iterator.next();

    }

    void setShuffle(boolean shuffle) {
        mShuffle = shuffle;
        sort();
        if(mShuffle)
            shuffle();
    }

    boolean isShuffle() {
        return mShuffle;
    }

    void setRepeat(RepeatMode repeat) {
        if(repeat == null) {
            throw new AssertionError();
        } else {
            mRepeat = repeat;
            return;
        }
    }

    RepeatMode getRepeat() {
        return mRepeat;
    }

}
