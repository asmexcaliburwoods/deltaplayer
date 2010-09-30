// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   GuiUtils.java

package dplayer.gui;

import dplayer.model.Directory;
import dplayer.model.Song;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

final class GuiUtils {

    protected static final String DATA_DIRECTORY = "DIRECTORY";
    protected static final String DATA_SONG = "SONG";

    GuiUtils() {
    }

    static String formatTimeSeconds(long timeSeconds) {
        String m = Long.toString(timeSeconds / 60L);
        String s = Long.toString(timeSeconds % 60L);
        StringBuffer sb = new StringBuffer(m);
        sb.append(':');
        if(s.length() == 1)
            sb.append('0');
        sb.append(s);
        return sb.toString();
    }

    static String formatSize(long size, char unit) {
        switch(unit) {
        case 77: // 'M'
        case 109: // 'm'
        {
            long mb = size / 1024L / 1024L;
            long kb = (size - mb * 1024L * 1024L) / 1024L;
            return (new StringBuilder(String.valueOf(mb))).append(".").append(kb).toString();
        }

        case 75: // 'K'
        case 107: // 'k'
        {
            long kb = size / 1024L;
            return Long.toString(kb);
        }
        }
        return Long.toString(size);
    }

    public static Directory getDirectory(TreeItem item) {
        return (Directory)item.getData("DIRECTORY");
    }

    public static Song getSong(TableItem item) {
        return (Song)item.getData("SONG");
    }
}
