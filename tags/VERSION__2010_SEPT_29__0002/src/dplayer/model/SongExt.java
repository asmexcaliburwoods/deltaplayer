// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   SongExt.java

package dplayer.model;

import java.text.*;
import java.util.Date;

// Referenced classes of package dplayer.model:
//            Song

public final class SongExt {

//    private static final String INSERT_DATE = "insert.date";
//    private static final String SKIP = "skip";
//    private static final String LAST_PLAYED = "play.last";
//    private static final String PLAY_COUNTER = "play.counter";
//    private static final String LAST_SCANNED = "scan.last";
    private static final DateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public SongExt() {
    }

    private static Date getDate(String key, Song song) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null)
            throw new AssertionError();
        Date result = null;
        String info = song.getExtInfo(key);
        if(info != null)
            try {
                result = sDateFormat.parse(info);
            }
            catch(ParseException e) {
                song.removeExtInfo(key);
            }
        return result;
    }

    private static void setDate(String key, Song song, Date date) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null)
            throw new AssertionError();
        if(date == null) {
            throw new AssertionError();
        } else {
            song.setExtInfo(key, sDateFormat.format(date));
            return;
        }
    }

    private static boolean getBoolean(String key, Song song, boolean def) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null)
            throw new AssertionError();
        String info = song.getExtInfo(key);
        if(info != null)
            return Boolean.parseBoolean(info);
        else
            return def;
    }

    private static void setBoolean(String key, Song song, boolean b) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null) {
            throw new AssertionError();
        } else {
            song.setExtInfo(key, Boolean.toString(b));
            return;
        }
    }

    private static int getInt(String key, Song song, int def) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null)
            throw new AssertionError();
        String info = song.getExtInfo(key);
        if(info != null)
            return Integer.parseInt(info);
        else
            return def;
    }

    private static void setInt(String key, Song song, int i) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null) {
            throw new AssertionError();
        } else {
            song.setExtInfo(key, Integer.toString(i));
            return;
        }
    }

    private static long getLong(String key, Song song, long def) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null)
            throw new AssertionError();
        String info = song.getExtInfo(key);
        if(info != null)
            return Long.parseLong(info);
        else
            return def;
    }

    private static void setLong(String key, Song song, long l) {
        if((key == null || key.length() <= 0))
            throw new AssertionError();
        if(song == null) {
            throw new AssertionError();
        } else {
            song.setExtInfo(key, Long.toString(l));
            return;
        }
    }

    public static Date getInsertDate(Song song) {
        return getDate("insert.date", song);
    }

    public static void setInsertDate(Song song, Date date) {
        setDate("insert.date", song, date);
    }

    public static Date getLastPlayed(Song song) {
        return getDate("play.last", song);
    }

    public static void setLastPlayed(Song song, Date date) {
        setDate("play.last", song, date);
    }

    public static boolean isSkip(Song song) {
        return getBoolean("skip", song, false);
    }

    public static void setSkip(Song song, boolean skip) {
        setBoolean("skip", song, skip);
    }

    public static int getPlayCounter(Song song) {
        return getInt("play.counter", song, 0);
    }

    public static void incPlayCounter(Song song) {
        setInt("play.counter", song, getPlayCounter(song) + 1);
    }

    public static long getLastScanned(Song song) {
        return getLong("scan.last", song, 0L);
    }

    public static void setLastScanned(Song song, Long lastScanned) {
        setLong("scan.last", song, lastScanned.longValue());
    }

}
