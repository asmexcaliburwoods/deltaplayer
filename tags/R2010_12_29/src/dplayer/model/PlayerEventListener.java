// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   PlayerEventListener.java

package dplayer.model;

import java.util.List;

// Referenced classes of package dplayer.model:
//            Directory, Player, Location, Song, 
//            RepeatMode

public interface PlayerEventListener {

    public abstract void locationAddedEvent(Location location);

    public abstract void locationRemovedEvent(Location location);

    public abstract void directoryChangedEvent(Directory directory);

    public abstract void directoryPopulatedEvent(Directory.PopulateRequest populaterequest, Directory directory);

    public abstract void stateChangedEvent(Player.State state);

    public abstract void playlistPositionChangedEvent(Song song);

    public abstract void playlistCleared(Directory.PopulateRequest populaterequest);

    public abstract void playlistItemAppended(Directory.PopulateRequest populaterequest, Song song);

    public abstract void songDetailsChangedEvent(Song song);

    public abstract void songListChangedEvent(Directory directory, List<Song> list);

    public abstract void skipListChangedEvent(Song song);

    public abstract void shuffleModeChanged(boolean flag);

    public abstract void repeatModeChanged(RepeatMode repeatmode);

    public abstract void exceptionEvent(Throwable throwable);
}
