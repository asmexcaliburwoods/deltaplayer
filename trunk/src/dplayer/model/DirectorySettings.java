// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   DirectorySettings.java

package dplayer.model;


public class DirectorySettings {

    private String file;
    private boolean defaultFlat;

    public DirectorySettings() {
    }

    public boolean isDefaultFlat() {
        return defaultFlat;
    }

    public void setDefaultFlat(boolean defaultFlat) {
        this.defaultFlat = defaultFlat;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
