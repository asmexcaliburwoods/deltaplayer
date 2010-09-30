// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   ToolBar.java

package dplayer.gui;

import dplayer.SettingsAdapter;
import dplayer.gui.i18n.I18N;
import dplayer.gui.icons.Icons;
import dplayer.model.Mixer;
import dplayer.model.Player;
import dplayer.model.PlayerEventAdapter;
import dplayer.model.RepeatMode;
import dplayer.model.Song;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.ToolItem;

// Referenced classes of package dplayer.gui:
//            MenuFactory, CommandFactory, Controller

class ToolBar {
    protected class PlayerEventHandler extends PlayerEventAdapter {
        protected boolean mRunTimer;

        public void stateChangedEvent(final dplayer.model.Player.State state) {
            if(!Controller.isApplicationExiting())
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        switch(state) {
                        case PLAYING:
                            mPlayTool.setImage(Icons.PLAY_PLAYING);
                            mPauseTool.setImage(Icons.PAUSE);
                            mTrackPositionSlider.setEnabled(true);
                            mRunTimer = true;
                            final Display display = Display.getDefault();
                            Runnable timer = new Runnable() {
                                public void run() {
                                    if(!mTrackPositionSlider.isDisposed()) {
                                        updateSlider();
                                        if(!display.isDisposed() && mRunTimer)
                                            display.timerExec(100, ((Runnable) (this)));
                                    }
                                }
                            };
                            display.timerExec(100, timer);
                            break;

                        case PAUSED:
                            mPlayTool.setImage(Icons.PLAY);
                            mPauseTool.setImage(Icons.PAUSE_PAUSED);
                            mRunTimer = false;
                            break;

                        case STOPPED:
                            mPlayTool.setImage(Icons.PLAY);
                            mPauseTool.setImage(Icons.PAUSE);
                            mTrackPositionSlider.setEnabled(false);
                            mRunTimer = false;
                            break;
                        }
                    }
                });
        }

        public void shuffleModeChanged(final boolean shuffle) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    SettingsAdapter.getInstance().setShuffleMode(shuffle);
                    if(shuffle) {
                        mShuffleTool.setImage(Icons.SHUFFLE_ON);
                        mShuffleTool.setToolTipText(I18N.get("SHUFFLE_ON", "Shuffle on"));
                    } else {
                        mShuffleTool.setImage(Icons.SHUFFLE_OFF);
                        mShuffleTool.setToolTipText(I18N.get("SHUFFLE_OFF", "Shuffle off"));
                    }
                }
            });
        }

        public void repeatModeChanged(final RepeatMode repeat) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    SettingsAdapter.getInstance().setRepeatMode(repeat);
                    switch(repeat) {
                    case NONE:
                        mRepeatTool.setImage(Icons.REPEAT_NONE);
                        mRepeatTool.setToolTipText(I18N.get("REPEAT_NONE", "Repeat none"));
                        break;

                    case ONE:
                        mRepeatTool.setImage(Icons.REPEAT_ONE);
                        mRepeatTool.setToolTipText(I18N.get("REPEAT_ONE", "Repeat one"));
                        break;

                    case ALL:
                        mRepeatTool.setImage(Icons.REPEAT_ALL);
                        mRepeatTool.setToolTipText(I18N.get("REPEAT_ALL", "Repeat all"));
                        break;
                    }
                }
            });
        }

        public void playlistPositionChangedEvent(Song song) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    mTrackPositionSlider.setSelection(0);
                }
            });
        }

        protected PlayerEventHandler() {}
    }


    protected static final Logger logger = Logger.getLogger(ToolBar.class);
    protected ToolItem mPlayTool;
    protected ToolItem mPauseTool;
    protected ToolItem mRepeatTool;
    protected ToolItem mShuffleTool;
    protected Slider mTrackPositionSlider;
    private Slider volumeSlider;
    private volatile boolean draggingSlider;

    ToolBar(Shell shell) {
        PlayerEventHandler playerEventHandler = new PlayerEventHandler();
        Composite composite = new Composite(((Composite) (shell)), 0);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(((Object) (gd)));
        RowLayout layout = new RowLayout();
        layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = 0;
        layout.spacing = SWT.FILL;
        composite.setLayout(((org.eclipse.swt.widgets.Layout) (layout)));
        composite.setMenu(MenuFactory.getToolbarPopupMenu(((org.eclipse.swt.widgets.Control) (composite))));
        org.eclipse.swt.widgets.ToolBar tb = new org.eclipse.swt.widgets.ToolBar(composite, 0x800100);
        mPlayTool = new ToolItem(tb, 0);
        mPlayTool.setImage(Icons.PLAY);
        mPlayTool.setToolTipText(I18N.get("CONTROL_PLAY", "Play track from beginning"));
        mPlayTool.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getPlayCommand())));
        mPauseTool = new ToolItem(tb, 0);
        mPauseTool.setImage(Icons.PAUSE);
        mPauseTool.setToolTipText(I18N.get("CONTROL_PAUSE", "Pause / Resume (Unpause)"));
        mPauseTool.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getPauseCommand())));
        ToolItem item = new ToolItem(tb, 0);
        item.setImage(Icons.PREV);
        item.setToolTipText(I18N.get("CONTROL_PREV", "Play previous track"));
        item.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getPrevCommand())));
        item = new ToolItem(tb, 0);
        item.setImage(Icons.NEXT);
        item.setToolTipText(I18N.get("CONTROL_NEXT", "Play next track"));
        item.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getNextCommand())));
        new ToolItem(tb, 2);
        mRepeatTool = new ToolItem(tb, 0);
        playerEventHandler.repeatModeChanged(SettingsAdapter.getInstance().getRepeatMode());
        mRepeatTool.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getRepeatCommand())));
        mShuffleTool = new ToolItem(tb, 0);
        playerEventHandler.shuffleModeChanged(SettingsAdapter.getInstance().getShuffleMode());
        mShuffleTool.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getShuffleCommand())));
        new ToolItem(tb, 2);
        boolean hasKeys = false;
        if(Mixer.getMixer().implementsKeys()) {
            item = new ToolItem(tb, 0);
            item.setImage(Icons.VOLUME_DOWN);
            item.setToolTipText(I18N.get("VOLUME_DOWN", "Volume down"));
            item.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getVolumeDownCommand())));
            hasKeys = true;
        }
        if(Mixer.getMixer().implementsMute()) {
            item = new ToolItem(tb, 32);
            item.setImage(Icons.VOLUME_MUTE);
            item.setToolTipText(I18N.get("VOLUME_MUTE", "Mute on / off"));
            item.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getToggleMuteCommand())));
            hasKeys = true;
        }
        if(Mixer.getMixer().implementsKeys()) {
            item = new ToolItem(tb, 0);
            item.setImage(Icons.VOLUME_UP);
            item.setToolTipText(I18N.get("VOLUME_UP", "Volume up"));
            item.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getVolumeUpCommand())));
            hasKeys = true;
        }
        if(hasKeys)
            new ToolItem(tb, 2);
        RowData rowData = new RowData(tb.getItemCount() * 20, 24);
        tb.setLayoutData(((Object) (rowData)));
        tb.setMenu(MenuFactory.getToolbarPopupMenu(((org.eclipse.swt.widgets.Control) (composite))));
        mTrackPositionSlider = new Slider(composite, SWT.HORIZONTAL);
        mTrackPositionSlider.setToolTipText(I18N.get("CONTROL_POSITION", "Track position"));
        mTrackPositionSlider.setMinimum(0);
        mTrackPositionSlider.setMaximum(101);
        mTrackPositionSlider.setIncrement(5);
        mTrackPositionSlider.setPageIncrement(10);
        mTrackPositionSlider.setThumb(5);
        mTrackPositionSlider.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getTrackPositionCommand())));
        rowData = new RowData(230, 12);
        mTrackPositionSlider.setLayoutData(((Object) (rowData)));
        mTrackPositionSlider.setMenu(MenuFactory.getToolbarPopupMenu(((org.eclipse.swt.widgets.Control) (composite))));
        mTrackPositionSlider.setEnabled(false);
        if(Mixer.getMixer().implementsSlider()) {
            volumeSlider = new Slider(composite, SWT.HORIZONTAL);
            volumeSlider.setToolTipText(I18N.get("CONTROL_VOLUME", "Volume"));
            volumeSlider.setMinimum(0);
            volumeSlider.setMaximum(81);
            volumeSlider.setIncrement(5);
            volumeSlider.setPageIncrement(10);
            volumeSlider.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (CommandFactory.getVolumeCommand())));
            int vol = Mixer.getMixer().getVolume();
            vol = (int)((((double)vol + 0.0D) / 65535D) * 81D);
            volumeSlider.setSelection(vol);
            rowData = new RowData(150, 12);
            volumeSlider.setLayoutData(((Object) (rowData)));
            volumeSlider.setMenu(MenuFactory.getToolbarPopupMenu(((org.eclipse.swt.widgets.Control) (composite))));
        }
        Player.getPlayer().addEventListener(((dplayer.model.PlayerEventListener) (playerEventHandler)));
    }

    public void updateSlider() {
        if(isDraggingSlider())
            return;
        Player player = Player.getPlayer();
        Song song = player.getSelectedSong();
        if(player.isPlaying() && song != null) {
            double p = ((double)player.getOffsetSeconds() / (double)song.getDurationSeconds()) * 100D;
            int pp = (int)p;
            if(pp < 0) {
                mTrackPositionSlider.setEnabled(false);
                mTrackPositionSlider.setSelection(0);
            } else {
                mTrackPositionSlider.setEnabled(true);
                mTrackPositionSlider.setSelection(pp);
            }
        }
    }

    public boolean isDraggingSlider() {
        return draggingSlider;
    }

    public void setDraggingSlider(boolean draggingSlider) {
        this.draggingSlider = draggingSlider;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

}
