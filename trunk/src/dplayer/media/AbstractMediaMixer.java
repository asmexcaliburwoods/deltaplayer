// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   AbstractMediaMixer.java

package dplayer.media;

import java.util.*;

// Referenced classes of package dplayer.media:
//            MediaMixer, MediaMixerListener

abstract class AbstractMediaMixer
    implements MediaMixer {

    private Set<Object> mListenerSet;

    AbstractMediaMixer() {
        mListenerSet = ((Set<Object>) (new HashSet<Object>()));
    }

    public boolean implementsFeature(MediaMixer.Feature feature) {
        return false;
    }

    public int getVolume() {
        throw new RuntimeException("Not supported.");
    }

    public void setVolume(int level) {
        throw new RuntimeException("Not supported.");
    }

    public void toggleMute() {
        throw new RuntimeException("Not supported.");
    }

    public void volumeDown() {
        throw new RuntimeException("Not supported.");
    }

    public void volumeUp() {
        throw new RuntimeException("Not supported.");
    }

    public void addMediaListener(MediaMixerListener listener) {
        if(listener == null) {
            throw new AssertionError();
        } else {
            mListenerSet.add(((Object) (listener)));
            return;
        }
    }

    protected void fireExceptionEvent(Exception e) {
        if(e == null)
            throw new AssertionError();
        MediaMixerListener l;
        for(Iterator<Object> iterator = mListenerSet.iterator(); iterator.hasNext(); l.exceptionEvent(e))
            l = (MediaMixerListener)iterator.next();

    }

}
