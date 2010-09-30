// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Mixer.java

package dplayer.model;

import dplayer.About;
import dplayer.OSUtil;
import dplayer.Settings;
import dplayer.media.*;
import java.util.*;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.model:
//            MixerEventListener

public class Mixer
    implements MediaMixerListener {

    private static final Logger logger = Logger.getLogger(Mixer.class);
    private MediaMixer mMixer;
    private Set<Object> mEventListenerSet;
    private static Mixer sInstance;

    public static Mixer getMixer() {
        if(sInstance == null)
            sInstance = new Mixer();
        return sInstance;
    }

    private Mixer() {
        mEventListenerSet = ((Set<Object>) (new HashSet<Object>()));
        logger.info(((Object) ((new StringBuilder("Creating mixer for OS ")).append(About.OS).toString())));
        if(OSUtil.isLinux())MediaMixerFactory.registerMediaMixer("amixer", AlsaMixer.class);
        else if(OSUtil.isWindows())
        	MediaMixerFactory.registerMediaMixer("WinExtMixer", WinExtMixer.class);
        else
        	new Exception("no mixer for OS "+About.OS).printStackTrace();
        mMixer = MediaMixerFactory.getMediaMixer(Settings.getString(Settings.MIXER));
        if(mMixer != null)
            mMixer.addMediaListener(((MediaMixerListener) (this)));
    }

    public boolean implementsSlider() {
        return mMixer != null && mMixer.implementsFeature(dplayer.media.MediaMixer.Feature.SLIDER);
    }

    public boolean implementsKeys() {
        return mMixer != null && mMixer.implementsFeature(dplayer.media.MediaMixer.Feature.KEYS);
    }

    public boolean implementsMute() {
        return mMixer != null && mMixer.implementsFeature(dplayer.media.MediaMixer.Feature.MUTE);
    }

    public void setVolume(int level) {
        if((level < 0 || level > 65535))
            throw new AssertionError();
        if(implementsSlider())
            mMixer.setVolume(level);
    }

    public int getVolume() {
        if(implementsSlider())
            return mMixer.getVolume();
        else
            return 65535;
    }

    public void toggleMute() {
        if(implementsMute())
            mMixer.toggleMute();
    }

    public void volumeUp() {
        if(implementsKeys())
            mMixer.volumeUp();
    }

    public void volumeDown() {
        if(implementsKeys())
            mMixer.volumeDown();
    }

    public void exceptionEvent(Exception e) {
        fireExceptionEvent(e);
    }

    public void addEventListener(MixerEventListener listener) {
        if(listener == null) {
            throw new AssertionError();
        } else {
            mEventListenerSet.add(((Object) (listener)));
            return;
        }
    }

    private void fireExceptionEvent(Exception e) {
        if(e == null)
            throw new AssertionError();
        MixerEventListener l;
        for(Iterator<Object> iterator = mEventListenerSet.iterator(); iterator.hasNext(); l.exceptionEvent(e))
            l = (MixerEventListener)iterator.next();

    }

}
