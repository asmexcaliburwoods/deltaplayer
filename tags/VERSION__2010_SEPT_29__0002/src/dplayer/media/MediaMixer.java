// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MediaMixer.java

package dplayer.media;


// Referenced classes of package dplayer.media:
//            MediaMixerListener

public interface MediaMixer {
    public static enum Feature {MUTE,KEYS,SLIDER}

    public abstract boolean implementsFeature(Feature feature);

    public abstract void toggleMute();

    public abstract void volumeUp();

    public abstract void volumeDown();

    public abstract void setVolume(int i);

    public abstract int getVolume();

    public abstract void addMediaListener(MediaMixerListener mediamixerlistener);
}
