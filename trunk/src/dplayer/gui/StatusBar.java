// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   StatusBar.java

package dplayer.gui;

import dplayer.gui.i18n.I18N;
import dplayer.model.*;
import java.util.List;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

// Referenced classes of package dplayer.gui:
//            GuiUtils, ExceptionUtil

class StatusBar {
    protected class PlayerEventHandler extends PlayerEventAdapter {
        private Song currentSong;

        public void playlistPositionChangedEvent(Song song) {
            currentSong = song;
            update2(song);
        }

        public void songDetailsChangedEvent(Song song) {
            if(song == currentSong)
                update2(song);
        }

        private void update2(final Song song) {
            if(song == null) {
                throw new AssertionError();
            } else {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        StringBuffer sb = new StringBuffer();
                        long dur = song.getDurationSeconds();
                        if(dur < 0L)
                            sb.append(song.toString());
                        else
                            sb.append(GuiUtils.formatTimeSeconds(dur)).append(' ').append(song.toString()).append(", ").append(song.getBitRate()).append("kb/s, ").append(song.getSampleRate()).append("Hz");
                        mCenterLabel.setText(sb.toString());
                        mComposite.layout(new Control[] {
                            mCenterLabel
                        });
                    }
                });
                return;
            }
        }

        public void songListChangedEvent(Directory directory, List<Song> songList) {
            update1();
        }

        public void directoryPopulatedEvent(dplayer.model.Directory.PopulateRequest pr, Directory directory) {
            update1();
        }

        private void update1() {
            final StringBuffer sb = new StringBuffer();
            sb.append(I18N.get("STATUS_TRACKS", "{0} track(s)", new String[] {
                Integer.toString(Player.getPlayer().getSongListSize())
            }));
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    try {
                        mLeftLabel.setText(sb.toString());
                        mComposite.layout(new Control[] {
                            mLeftLabel, mSpace, mCenterLabel
                        });
                    }
                    catch(Exception e) {
                        ExceptionUtil.handleException(((Throwable) (e)));
                    }
                }
            });
        }



        protected PlayerEventHandler() {}
    }


    protected Label mLeftLabel;
    protected Label mCenterLabel;
    private Composite mComposite;
    private Label mSpace;

    StatusBar(Shell shell) {
        mComposite = new Composite(((Composite) (shell)), 0);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = 4;
        mComposite.setLayoutData(((Object) (gd)));
        GridLayout layout = new GridLayout(3, false);
        layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = 0;
        layout.horizontalSpacing = 4;
        layout.verticalSpacing = 0;
        layout.marginHeight = layout.marginWidth = 0;
        mComposite.setLayout(((org.eclipse.swt.widgets.Layout) (layout)));
        mLeftLabel = new Label(mComposite, 16384);
        mLeftLabel.setText("");
        gd = new GridData();
        gd.heightHint = 16;
        mLeftLabel.setLayoutData(((Object) (gd)));
        mSpace = new Label(mComposite, 0x1000000);
        mSpace.setText("|");
        mSpace.setForeground(Display.getCurrent().getSystemColor(18));
        gd = new GridData(3, 16);
        mSpace.setLayoutData(((Object) (gd)));
        mCenterLabel = new Label(mComposite, 16384);
        mCenterLabel.setText("");
        gd = new GridData();
        gd.heightHint = 16;
        mCenterLabel.setLayoutData(((Object) (gd)));
        Player.getPlayer().addEventListener(((dplayer.model.PlayerEventListener) (new PlayerEventHandler())));
    }


}
