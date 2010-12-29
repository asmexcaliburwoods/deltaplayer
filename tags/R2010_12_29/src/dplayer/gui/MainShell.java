// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MainShell.java

package dplayer.gui;

import dplayer.About;
import dplayer.Settings;
import dplayer.ext.ExtException;
import dplayer.ext.win.WinExt;
import dplayer.gui.i18n.I18N;
import dplayer.gui.icons.Icons;
import dplayer.model.*;
import java.io.FileNotFoundException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

// Referenced classes of package dplayer.gui:
//            ToolBar, MainArea, StatusBar, TrayIcon, 
//            Cursors, Controller, CommandFactory

class MainShell {
    protected class MixerEventHandler
        implements MixerEventListener {
        public void exceptionEvent(final Exception e) {
            e.printStackTrace();
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    MessageBox messageBox = new MessageBox(mShell, 33);
                    messageBox.setText((new StringBuilder("deltaplayer - ")).append(I18N.get("ERROR_TITLE", "error")).toString());
                    if(e instanceof FileNotFoundException)
                        messageBox.setMessage(I18N.get("ERROR_FILE_NOT_FOUND", "Unable to open file {0}.", new String[] {
                            e.getMessage()
                        }));
                    else
                        messageBox.setMessage(e.getMessage() == null ? "" : e.getMessage());
                    messageBox.open();
                }
            });
        }

        protected MixerEventHandler() {
        }
    }

    protected class PlayerEventHandler extends PlayerEventAdapter {

        protected boolean mRunTimer;

        public void exceptionEvent(final Throwable e) {
        	ExceptionUtil.handleException(e);
        }

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
                            Controller.getController().checkPlayedTime();
                            Player player = Player.getPlayer();
                            if(player.isPlaying() && player.getSelectedSong() != null) {
                                StringBuilder sb = (new StringBuilder()).append(player.getSelectedSong().getFilename());
                                String title = sb.toString();
                                if(!title.equals(((Object) (mShell.getText()))))
                                    mShell.setText(title);
                            }
                            if(!display.isDisposed() && mRunTimer)
                                display.timerExec(1000, ((Runnable) (this)));
                        }
                    }
                };
                display.syncExec(new Runnable() {
                    public void run() {
                        display.timerExec(0, timer);
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
                            mShell.setText(About.TITLE);
                        }
                    });
                break;
            }
        }

        protected PlayerEventHandler() {}
    }

    protected class ResizeCommand
        implements ControlListener {
        public void controlMoved(ControlEvent e) {
            Shell s = (Shell)e.widget;
            updateSettings(s.getBounds());
        }

        public void controlResized(ControlEvent e) {
            Shell s = (Shell)e.widget;
            updateSettings(s.getBounds());
        }

        private void updateSettings(Rectangle rect) {
            Settings.setInt(Settings.WINDOW_X, rect.x);
            Settings.setInt(Settings.WINDOW_Y, rect.y);
            Settings.setInt(Settings.WINDOW_W, rect.width);
            Settings.setInt(Settings.WINDOW_H, rect.height);
        }

        protected ResizeCommand() {}
    }

    protected class ShellHandler extends ShellAdapter {
        public void shellIconified(ShellEvent e) {
            mShell.setVisible(false);
        }

        public void shellClosed(ShellEvent e) {
            CommandFactory.getExitCommand().exec();
        }

        protected ShellHandler() {
        }
    }

    private static MainShell sInstance;
    protected Shell mShell;
    protected StatusBar mStatusBar;
    private ToolBar toolBar;

    public ToolBar getToolBar() {
        return toolBar;
    }

    MainShell(Display display) throws ExtException {
        if(display == null)
            throw new AssertionError();
        mShell = new Shell(display);
        if(About.OS.startsWith("Windows"))
            WinExt.init(mShell);
        mShell.setText(About.TITLE);
        mShell.setImage(Icons.APP);
        mShell.setLayout(((org.eclipse.swt.widgets.Layout) (new GridLayout())));
        mShell.addControlListener(((ControlListener) (new ResizeCommand())));
        mShell.addShellListener(((org.eclipse.swt.events.ShellListener) (new ShellHandler())));
        Monitor ma[] = display.getMonitors();
        int x = Settings.getInt(Settings.WINDOW_X);
        int w = Settings.getInt(Settings.WINDOW_W);
        if(x == 0 && ma != null)
            x = (ma[0].getClientArea().width - w) / 2;
        int y = Settings.getInt(Settings.WINDOW_Y);
        int h = Settings.getInt(Settings.WINDOW_H);
        if(y == 0 && ma != null)
            y = (ma[0].getClientArea().height - h) / 2;
        mShell.setBounds(x, y, w, h);
        if(Settings.getBoolean(Settings.WINDOW_MAXIMIZED))
            mShell.setMaximized(true);
        if(Settings.getBoolean(Settings.WINDOW_MINIMIZED))
            mShell.setMinimized(true);
        toolBar = new ToolBar(mShell);
        new MainArea(mShell);
        mStatusBar = new StatusBar(mShell);
        TrayIcon trayIcon = new TrayIcon(mShell);
        trayIcon.show();
        Player.getPlayer().addEventListener(((dplayer.model.PlayerEventListener) (new PlayerEventHandler())));
        Mixer.getMixer().addEventListener(((MixerEventListener) (new MixerEventHandler())));
        sInstance = this;
    }

    void open() {
        mShell.open();
    }

    boolean isDisposed() {
        return mShell.isDisposed();
    }

    static void setWaitCursor() {
        if(sInstance == null) {
            throw new AssertionError();
        } else {
            sInstance.mShell.setCursor(Cursors.WAIT);
            return;
        }
    }

    static void setNormalCursor() {
        if(sInstance == null) {
            throw new AssertionError();
        } else {
            sInstance.mShell.setCursor(((org.eclipse.swt.graphics.Cursor) (null)));
            return;
        }
    }

    static Shell getShell() {
        if(sInstance == null)
            throw new AssertionError();
        if(sInstance.mShell == null)
            throw new AssertionError();
        else
            return sInstance.mShell;
    }

    public static MainShell getInstance() {
        return sInstance;
    }

}
