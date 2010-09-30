// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   I18N.java

package dplayer.gui.i18n;

import java.text.MessageFormat;
import java.util.*;

public final class I18N {

    private static String BUNDLE_NAME = (new StringBuilder(String.valueOf(((Object) (I18N.class.getPackage().getName()))))).append(".dplayer").toString();
    private static Locale sLocale;
    private static ResourceBundle sBundle;

    public static void setLocale(Locale locale) {
        sLocale = locale;
        try {
            sBundle = ResourceBundle.getBundle(BUNDLE_NAME);
        }
        catch(MissingResourceException missingresourceexception) { }
    }

    public static String get(String k) {
        if((k == null || k.length() <= 0))
            throw new AssertionError();
        if(sBundle == null)
            throw new RuntimeException((new StringBuilder("I18N error: no language support for locale ")).append(((Object) (sLocale))).toString());
        else
            return sBundle.getString(k);
    }

    public static String get(String k, String text) {
        if((k == null || k.length() <= 0))
            throw new AssertionError();
        if(text == null)
            throw new AssertionError();
        else
            return sBundle == null ? text : sBundle.getString(k);
    }

    public static String get(String k, String args[]) {
        if((k == null || k.length() <= 0))
            throw new AssertionError();
        if((args == null || args.length <= 0))
            throw new AssertionError();
        else
            return MessageFormat.format(get(k), ((Object []) (args)));
    }

    public static String get(String k, String text, String args[]) {
        if((k == null || k.length() <= 0))
            throw new AssertionError();
        if(text == null)
            throw new AssertionError();
        if((args == null || args.length <= 0))
            throw new AssertionError();
        else
            return MessageFormat.format(get(k, text), ((Object []) (args)));
    }

    private I18N() {
        throw new AssertionError("Do not instantiate I18N class.");
    }

    static  {
        setLocale(Locale.getDefault());
    }
}
