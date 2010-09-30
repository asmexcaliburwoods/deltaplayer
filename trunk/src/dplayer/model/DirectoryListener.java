// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   DirectoryListener.java

package dplayer.model;


// Referenced classes of package dplayer.model:
//            Directory, Song

public interface DirectoryListener {

    public abstract void directoryPopulated(Directory.PopulateRequest populaterequest, Directory directory);

    public abstract void songListCleared(Directory.PopulateRequest populaterequest);

    public abstract void songAdded(Directory.PopulateRequest populaterequest, Song song);
}
