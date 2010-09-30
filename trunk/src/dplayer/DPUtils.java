// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   DPUtils.java

package dplayer;

import java.io.File;
import java.io.IOException;

public class DPUtils {

    public DPUtils() {
    }

    public static int convertPositiveLongToInt(long l) {
        if(l > 0x7fffffffL)
            throw new ArithmeticException("long cannot fit to int");
        else
            return (int)l;
    }

    public static String getAbsolutePath(File f) {
        try {
            return f.getCanonicalPath();
        }
        catch(IOException e) {
            return f.getAbsolutePath();
        }
    }
}
