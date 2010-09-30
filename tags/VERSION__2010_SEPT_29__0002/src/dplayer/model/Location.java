// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Location.java

package dplayer.model;

import java.io.File;

// Referenced classes of package dplayer.model:
//            Directory

public class Location {
    private String mName;
    private Directory mDirectory;

    public Location(String name, File file) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        if(file == null) {
            throw new AssertionError();
        } else {
            mName = name;
            mDirectory = new Directory(file);
            return;
        }
    }

    public String getName() {
        return mName;
    }

    public Directory getDirectory() {
        return mDirectory;
    }

}
