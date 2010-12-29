// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   TrayIcon.java

package dplayer.gui;

import dplayer.gui.icons.Icons;
import dplayer.model.*;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.*;

// Referenced classes of package dplayer.gui:
//            MenuFactory, Controller, GuiUtils

class TrayIcon {
    protected class PlayerEventHandler extends PlayerEventAdapter {
        protected boolean mRunTimer;

        public void stateChangedEvent(dplayer.model.Player.State state) {
            if(state == null)
                throw new AssertionError();
            switch(state) {
            default:
                break;
            case PLAYING:
                mRunTimer = true;
                final Display display = Display.getDefault();
                final Runnable timer = new Runnable() {
                    public void run() {
                        if(!mShell.isDisposed()) {
                            Player player = Player.getPlayer();
                            if(player.isPlaying() && player.getSelectedSong() != null) {
                                StringBuffer sb = (new StringBuffer()).append(GuiUtils.formatTimeSeconds(player.getOffsetSeconds())).append(' ').append(player.getSelectedSong().getFilename());
                                mTrayItem.setToolTipText(sb.toString());
                            }
                            if(!display.isDisposed() && mRunTimer)
                                display.timerExec(990, ((Runnable) (this)));
                        }
                    }
                };
                display.syncExec(new Runnable() {
                    public void run() {
                        display.timerExec(990, timer);
                    }
                });
                break;

            case PAUSED:
                mRunTimer = false;
                break;

            case STOPPED:
                mRunTimer = false;
                if(!Controller.isApplicationExiting())
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            mTrayItem.setToolTipText("deltaplayer 0.1 - a media player");
                        }
                    });
                break;
            }
        }

        protected PlayerEventHandler() {
        }
    }

    protected class ShowMainShellCommand
        implements Listener {
        public void handleEvent(Event e) {
            mShell.setVisible(true);
            mShell.setMinimized(false);
            mShell.setActive();
        }

        protected ShowMainShellCommand() {
        }
    }

    protected class ShowPopupMenuCommand
        implements Listener {
        public void handleEvent(Event e) {
            mPopupMenu.setVisible(true);
        }

        protected ShowPopupMenuCommand() {
        }
    }

    private static final Logger logger = Logger.getLogger(TrayIcon.class);
    protected Shell mShell;
    protected TrayItem mTrayItem;
    protected Menu mPopupMenu;

    TrayIcon(Shell parent) {
        if(parent == null) {
            throw new AssertionError();
        } else {
            mShell = parent;
            return;
        }
    }

    void show() {
        if(mTrayItem == null) {
            Tray tray = Display.getDefault().getSystemTray();
            if(tray == null) {
                logger.warn("System tray is not available.");
                return;
            }
            mTrayItem = new TrayItem(tray, 0);
            mTrayItem.setToolTipText("deltaplayer 0.1 - a media player");
            mTrayItem.setImage(Icons.APP);
            ShowMainShellCommand smsc = new ShowMainShellCommand();
            mTrayItem.addListener(13, ((Listener) (smsc)));
            mTrayItem.addListener(14, ((Listener) (smsc)));
            mPopupMenu = MenuFactory.getTrayIconPopupMenu(((org.eclipse.swt.widgets.Control) (mShell)));
            mTrayItem.addListener(35, ((Listener) (new ShowPopupMenuCommand())));
            Player.getPlayer().addEventListener(((dplayer.model.PlayerEventListener) (new PlayerEventHandler())));
        }
        mTrayItem.setVisible(true);
    }

    void hide() {
        if(mTrayItem != null)
            mTrayItem.setVisible(false);
    }

}
