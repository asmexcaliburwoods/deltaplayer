// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   SettingsAdapter.java

package dplayer;

import dplayer.model.RepeatMode;
import dplayer.model.cache.CacheManager;

// Referenced classes of package dplayer:
//            Settings

public class SettingsAdapter {

    private static final SettingsAdapter instance = new SettingsAdapter();

    public SettingsAdapter() {
    }

    public boolean isDisplayCovers() {
        return Settings.getBoolean(Settings.DISPLAY_COVER);
    }

    public void setDisplayCovers(boolean displayCover) {
        Settings.setBoolean(Settings.DISPLAY_COVER, displayCover);
    }

    public boolean isCacheEnabled() {
        return Settings.getBoolean(Settings.CACHE_ENABLED);
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        Settings.setBoolean(Settings.CACHE_ENABLED, cacheEnabled);
        CacheManager.setEnabled(cacheEnabled);
    }

    public void commit() {
        Settings.save();
    }

    public String getAmixerCommand() {
        return Settings.getString(dplayer.media.AlsaMixer.AlsaMixerSettings.COMMAND);
    }

    public void setAmixerCommand(String amixerCommand) {
        Settings.setString(dplayer.media.AlsaMixer.AlsaMixerSettings.COMMAND, amixerCommand);
    }

    public String getMPlayerCommand() {
        return Settings.getString(dplayer.media.MPlayer.MPlayerSettings.COMMAND);
    }

    public void setMPlayerCommand(String mplayerCommand) {
        Settings.setString(dplayer.media.MPlayer.MPlayerSettings.COMMAND, mplayerCommand);
    }

    public static SettingsAdapter getInstance() {
        return instance;
    }

    public RepeatMode getRepeatMode() {
        return RepeatMode.valueOf(Settings.getString(Settings.HISTORY_LAST_REPEAT));
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        Settings.setString(Settings.HISTORY_LAST_REPEAT, repeatMode.name());
    }

    public boolean getShuffleMode() {
        return Settings.getBoolean(Settings.HISTORY_LAST_SHUFFLE);
    }

    public void setShuffleMode(boolean shuffleMode) {
        Settings.setBoolean(Settings.HISTORY_LAST_SHUFFLE, shuffleMode);
    }

    public dplayer.media.MPlayer.ProcessPriority getMPlayerProcessPriority() {
        return dplayer.media.MPlayer.ProcessPriority.valueOf(Settings.getString(dplayer.media.MPlayer.MPlayerSettings.PRIORITY));
    }

    public void setMPlayerProcessPriority(dplayer.media.MPlayer.ProcessPriority _MPlayerProcessPriority) {
        Settings.setString(dplayer.media.MPlayer.MPlayerSettings.PRIORITY, _MPlayerProcessPriority.name());
    }

}
