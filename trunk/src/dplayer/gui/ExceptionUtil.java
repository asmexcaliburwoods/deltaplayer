// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   ExceptionUtil.java

package dplayer.gui;


public class ExceptionUtil {

    public ExceptionUtil() {
    }

    public static void handleException(Throwable e) {
        e.printStackTrace();
    }
}
