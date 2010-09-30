// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   CommandFactory.java

package dplayer.gui;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Slider;

import dplayer.Settings;
import dplayer.model.Mixer;
import dplayer.model.Player;
import dplayer.model.Song;
import dplayer.model.SongExt;

// Referenced classes of package dplayer.gui:
//            Controller, MainShell, ToolBar, AboutDialog, 
//            SettingsDialog

class CommandFactory {
    static class AboutCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            AboutDialog.show(e.display);
        }

        AboutCommand() {
        }
    }

    static class DisplaySkippedCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().toggleDisplaySkipped();
        }

        DisplaySkippedCommand() {
        }
    }

    static class ExitCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            exec();
        }

        public void exec() {
            MainShell.getShell().dispose();
        }

        ExitCommand() {
        }
    }

    static class NextCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().next();
        }

        NextCommand() {
        }
    }

    static class PauseCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().pause();
        }

        PauseCommand() {
        }
    }

    static class PlayCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().play();
        }

        PlayCommand() {
        }
    }

    static class PrevCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().prev();
        }

        PrevCommand() {
        }
    }

    static class RefreshCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().refresh();
        }

        RefreshCommand() {
        }
    }

    static class RepeatCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().toggleRepeat();
        }

        RepeatCommand() {
        }
    }

    static class SettingsCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            SettingsDialog.show(e.display);
        }

        SettingsCommand() {
        }
    }

    static class ShuffleCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().toggleShuffle();
        }

        ShuffleCommand() {
        }
    }

    static class StopCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().stop(dplayer.model.Player.StopReason.OTHER_STOP_REASON);
        }

        StopCommand() {
        }
    }

    static class ToggleMuteCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().toggleMute();
        }

        ToggleMuteCommand() {
        }
    }

    static class TrackPositionCommand extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            if(e == null)
                throw new AssertionError();
            if(e.widget == null)
                throw new AssertionError();
            if(e.widget instanceof Slider) {
                Slider vs = (Slider)e.widget;
                int detail = e.detail;
                MainShell.getInstance().getToolBar().setDraggingSlider(detail == 1);
                if(detail != 1)
                    setTrackPosition(vs.getSelection());
            }
        }

        private void setTrackPosition(int position) {
            if((position < 0 || position > 100)) {
                throw new AssertionError();
            } else {
                Controller.getController().setTrackPosition(position);
                return;
            }
        }


        TrackPositionCommand() {
        }
    }

    static class VolumeCommand extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            if(e == null)
                throw new AssertionError();
            if(e.widget == null)
                throw new AssertionError();
            if(e.widget instanceof Slider) {
                Slider vs = (Slider)e.widget;
                setVolume(vs.getSelection());
            }
        }

        private void setVolume(int level) {
            if((level < 0 || level > 81)) {
                throw new AssertionError();
            } else {
                level = (int)((((double)level + 0.0D) / 81D) * 65535D);
                CommandFactory.setVolumeFFFF(level);
                return;
            }
        }


        VolumeCommand() {
        }
    }

    static class VolumeDownCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().volumeDown();
            CommandFactory.refreshVolume();
        }

        VolumeDownCommand() {
        }
    }

    static class VolumeUpCommand extends SelectionAdapter {

        public void widgetSelected(SelectionEvent e) {
            Controller.getController().volumeUp();
            CommandFactory.refreshVolume();
        }

        VolumeUpCommand() {
        }
    }


    private static RefreshCommand sRefreshCommand;
    private static PlayCommand sPlayCommand;
    private static StopCommand sStopCommand;
    private static PauseCommand sPauseCommand;
    private static PrevCommand sPrevCommand;
    private static NextCommand sNextCommand;
    private static ShuffleCommand sShuffleCommand;
    private static RepeatCommand sRepeatCommand;
    private static VolumeCommand sVolumeCommand;
    private static TrackPositionCommand sTrackPositionCommand;
    private static DisplaySkippedCommand sDisplaySkippedCommand;
    private static VolumeDownCommand sVolumeDownCommand;
    private static VolumeUpCommand sVolumeUpCommand;
    private static ToggleMuteCommand sToggleMuteCommand;
    private static AboutCommand sAboutCommand;
    private static SettingsCommand sSettingsCommand;
    private static ExitCommand sExitCommand;
    public static final int VOLUME_SLIDER_MAX = 81;

    CommandFactory() {
    }

    static RefreshCommand getRefreshCommand() {
        if(sRefreshCommand == null)
            sRefreshCommand = new RefreshCommand();
        return sRefreshCommand;
    }

    static PlayCommand getPlayCommand() {
        if(sPlayCommand == null)
            sPlayCommand = new PlayCommand();
        return sPlayCommand;
    }

    static StopCommand getStopCommand() {
        if(sStopCommand == null)
            sStopCommand = new StopCommand();
        return sStopCommand;
    }

    static PauseCommand getPauseCommand() {
        if(sPauseCommand == null)
            sPauseCommand = new PauseCommand();
        return sPauseCommand;
    }

    static PrevCommand getPrevCommand() {
        if(sPrevCommand == null)
            sPrevCommand = new PrevCommand();
        return sPrevCommand;
    }

    static NextCommand getNextCommand() {
        if(sNextCommand == null)
            sNextCommand = new NextCommand();
        return sNextCommand;
    }

    static ShuffleCommand getShuffleCommand() {
        if(sShuffleCommand == null)
            sShuffleCommand = new ShuffleCommand();
        return sShuffleCommand;
    }

    static RepeatCommand getRepeatCommand() {
        if(sRepeatCommand == null)
            sRepeatCommand = new RepeatCommand();
        return sRepeatCommand;
    }

    static VolumeCommand getVolumeCommand() {
        if(sVolumeCommand == null)
            sVolumeCommand = new VolumeCommand();
        return sVolumeCommand;
    }

    static TrackPositionCommand getTrackPositionCommand() {
        if(sTrackPositionCommand == null)
            sTrackPositionCommand = new TrackPositionCommand();
        return sTrackPositionCommand;
    }

    static SelectionAdapter getSkipTrackCommand(final Song song) {
        return new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Controller.getController().toggleTrackSkipping(song);
                if(Player.getPlayer().getSelectedSong() == song && SongExt.isSkip(song))
                    Controller.getController().next();
            }
        };
    }

    static DisplaySkippedCommand getDisplaySkippedCommand() {
        if(sDisplaySkippedCommand == null)
            sDisplaySkippedCommand = new DisplaySkippedCommand();
        return sDisplaySkippedCommand;
    }

    static VolumeDownCommand getVolumeDownCommand() {
        if(sVolumeDownCommand == null)
            sVolumeDownCommand = new VolumeDownCommand();
        return sVolumeDownCommand;
    }

    static VolumeUpCommand getVolumeUpCommand() {
        if(sVolumeUpCommand == null)
            sVolumeUpCommand = new VolumeUpCommand();
        return sVolumeUpCommand;
    }

    static ToggleMuteCommand getToggleMuteCommand() {
        if(sToggleMuteCommand == null)
            sToggleMuteCommand = new ToggleMuteCommand();
        return sToggleMuteCommand;
    }

    static AboutCommand getAboutCommand() {
        if(sAboutCommand == null)
            sAboutCommand = new AboutCommand();
        return sAboutCommand;
    }

    static SettingsCommand getSettingsCommand() {
        if(sSettingsCommand == null)
            sSettingsCommand = new SettingsCommand();
        return sSettingsCommand;
    }

    static ExitCommand getExitCommand() {
        if(sExitCommand == null)
            sExitCommand = new ExitCommand();
        return sExitCommand;
    }

    public static SelectionAdapter getTreeSelectCommand() {
        return ((SelectionAdapter) (new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                Controller.getController().play();
            }

        }
));
    }

    private static void setVolumeFFFF(int level) {
        Settings.setInt(Settings.VOLUME, level);
        Controller.getController().setVolume(level);
    }

    private static void refreshVolume() {
        int vol = Mixer.getMixer().getVolume();
        setVolumeFFFF(vol);
        Slider vs = MainShell.getInstance().getToolBar().getVolumeSlider();
        if(vs != null) {
            vol = (int)((((double)vol + 0.0D) / 65535D) * 81D);
            vs.setSelection(Math.max(0, Math.min(vol, 81)));
        }
    }


}
