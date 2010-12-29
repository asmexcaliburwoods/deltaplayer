// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   WinExtMixer.java

package dplayer.media;

import dplayer.ext.ExtException;
import dplayer.ext.win.WinExt;

// Referenced classes of package dplayer.media:
//            AbstractMediaMixer, MediaMixer

public class WinExtMixer extends AbstractMediaMixer {

    public static final String NAME = "WinExtMixer";

    public WinExtMixer() {
    }

    public boolean implementsFeature(MediaMixer.Feature feature) {
        return true;
    }

    public void toggleMute() {
        try {
            WinExt.toggleMute();
        }
        catch(ExtException e) {
            fireExceptionEvent(((Exception) (e)));
        }
    }

    public void volumeDown() {
        setVolume(Math.max(0, getVolume() - 4095));
    }

    public void volumeUp() {
        setVolume(Math.min(65535, getVolume() + 4095));
    }

    public int getVolume() {
        try {
            WinExt winExt = WinExt.getWinExt();
            return (winExt.getLeftVolume() + winExt.getRightVolume()) / 2;
        }
        catch(ExtException e) {
            fireExceptionEvent(((Exception) (e)));
        }
        return 65535;
    }

    public void setVolume(int level) {
        try {
            WinExt winExt = WinExt.getWinExt();
            winExt.setLeftVolume(level);
            winExt.setRightVolume(level);
        }
        catch(ExtException e) {
            fireExceptionEvent(((Exception) (e)));
        }
    }
}
