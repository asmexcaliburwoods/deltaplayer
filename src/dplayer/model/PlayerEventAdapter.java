// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   PlayerEventAdapter.java

package dplayer.model;

import java.util.List;

// Referenced classes of package dplayer.model:
//            PlayerEventListener, Directory, Player, Location, 
//            Song, RepeatMode

public abstract class PlayerEventAdapter
    implements PlayerEventListener {

    public PlayerEventAdapter() {
    }

    public void exceptionEvent(Throwable throwable) {
    }

    public void directoryChangedEvent(Directory directory1) {
    }

    public void directoryPopulatedEvent(Directory.PopulateRequest populaterequest, Directory directory) {
    }

    public void locationAddedEvent(Location location1) {
    }

    public void locationRemovedEvent(Location location1) {
    }

    public void playlistPositionChangedEvent(Song song1) {
    }

    public void playlistCleared(Directory.PopulateRequest populaterequest) {
    }

    public void songDetailsChangedEvent(Song song1) {
    }

    public void songListChangedEvent(Directory directory1, List<Song> list) {
    }

    public void skipListChangedEvent(Song song1) {
    }

    public void stateChangedEvent(Player.State state1) {
    }

    public void shuffleModeChanged(boolean flag) {
    }

    public void repeatModeChanged(RepeatMode repeatmode) {
    }

    public void playlistItemAppended(Directory.PopulateRequest populaterequest, Song song) {
    }
}
