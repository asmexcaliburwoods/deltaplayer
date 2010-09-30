// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   AlsaMixer.java

package dplayer.media;

import dplayer.Settings;
import dplayer.ext.ExtException;
import dplayer.ext.linux.LinuxExt;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.media:
//            AbstractMediaMixer, MediaMixer

public class AlsaMixer extends AbstractMediaMixer {
    public static class AlsaMixerSettings extends Settings {

        public static final dplayer.Settings.Property COMMAND = new dplayer.Settings.Property("amixer.command", "Path and name of amixer executable.", "amixer");

        static void registerSettings() {
            addSection("amixer related settings.", 50, new dplayer.Settings.Property[] {
                COMMAND
            });
        }


        public AlsaMixerSettings() {
        }
    }


    protected static final Logger logger = Logger.getLogger(AlsaMixer.class);
    public static final String NAME = "amixer";

    public AlsaMixer() {
        AlsaMixerSettings.registerSettings();
    }

    public boolean implementsFeature(MediaMixer.Feature feature) {
        if(feature == null)
            throw new AssertionError();
        return feature == MediaMixer.Feature.MUTE || feature == MediaMixer.Feature.KEYS;
    }

    public void toggleMute() {
        controlMaster("toggle");
    }

    public void volumeDown() {
        controlMaster("10%-");
    }

    public void volumeUp() {
        controlMaster("10%+");
    }

    private void controlMaster(String command) {
        try {
            LinuxExt.exec((new StringBuilder(String.valueOf(((Object) (Settings.getString(AlsaMixerSettings.COMMAND)))))).append(" set Master ").append(command).toString());
        }
        catch(ExtException e) {
            fireExceptionEvent(((Exception) (e)));
        }
    }

}
