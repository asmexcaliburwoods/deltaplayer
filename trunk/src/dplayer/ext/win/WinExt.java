// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   WinExt.java

package dplayer.ext.win;

import dplayer.ext.ExtException;
import dplayer.gui.AboutDialog;
import dplayer.gui.i18n.I18N;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

public class WinExt {

    protected static final Logger logger = Logger.getLogger(WinExt.class);
    private static WinExt sInstance;

    public WinExt() {
    }

    public native void launchDefaultBrowser(String s) throws Exception;

    private native void nativeSetHWND(int i);

    private native boolean nativeIsRunning(String s);

    private native void nativeVolumeMute();

    private native void nativeVolumeUp();

    private native void nativeVolumeDown();

    public native int getLeftVolume();

    public native int getRightVolume();

    public native void setLeftVolume(int i);

    public native void setRightVolume(int i);

    public static void init(Shell mainWindow) throws ExtException {
        getWinExt().nativeSetHWND(mainWindow.handle);
    }

    public static WinExt getWinExt() throws ExtException {
        if(sInstance == null)
            try {
                logger.debug(((Object) ((new StringBuilder("Trying to load ext library from this path: ")).append(System.getProperty("java.library.path")).toString())));
                System.loadLibrary("dplayer");
                sInstance = new WinExt();
            }
            catch(Throwable t) {
                logger.error(((Object) (t)));
                throw new ExtException(I18N.get("ERROR_WINEXT_FAILED", "Unable to load WinExt library {0}.", new String[] {
                    "dplayer.dll"
                }), t);
            }
        return sInstance;
    }

    public static boolean isRunning() throws ExtException {
        return getWinExt().nativeIsRunning(AboutDialog.getSplashTitle());
    }

    public static void volumeUp() throws ExtException {
        getWinExt().nativeVolumeUp();
    }

    public static void volumeDown() throws ExtException {
        getWinExt().nativeVolumeDown();
    }

    public static void toggleMute() throws ExtException {
        getWinExt().nativeVolumeMute();
    }

}
