// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   StringUtil.java

package dplayer;


public class StringUtil {

    public StringUtil() {
    }

    public static String mkEmpty(String s) {
        if(s == null)
            return "";
        else
            return s;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isTrimmedEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
