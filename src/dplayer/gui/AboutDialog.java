// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   AboutDialog.java

package dplayer.gui;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import dplayer.About;
import dplayer.LogAppender;
import dplayer.OSUtil;
import dplayer.ext.win.WinExt;
import dplayer.gui.i18n.I18N;
import dplayer.gui.icons.Icons;
import dplayer.gui.widgets.LabelProgressBar;

// Referenced classes of package dplayer.gui:
//            ExceptionUtil

public class AboutDialog {
    protected class CloseCommand extends MouseAdapter {

        public void mouseUp(MouseEvent e) {
            mShell.dispose();
        }

        protected CloseCommand() {
            super();
        }
    }


    protected Shell mShell;
    protected LabelProgressBar mProgress;
    private static AboutDialog sInstance;

    static void show(Display display) {
        if(display == null)
            throw new AssertionError();
        if(sInstance != null) {
            throw new AssertionError();
        } else {
            sInstance = new AboutDialog(display, false);
            sInstance.runModal();
            sInstance = null;
            return;
        }
    }

    static void showSplash(Display display) {
        if(display == null)
            throw new AssertionError();
        if(sInstance != null) {
            throw new AssertionError();
        } else {
            sInstance = new AboutDialog(display, true);
            return;
        }
    }

    static void hideSplash() {
        if(sInstance == null) {
            throw new AssertionError();
        } else {
            sInstance.mShell.setVisible(false);
            sInstance = null;
            return;
        }
    }

    static void setProgress(int p, String text) {
        if(sInstance == null)
            throw new AssertionError();
        if(sInstance.mProgress != null) {
            sInstance.mProgress.setSelection(p);
            sInstance.mProgress.setText(text);
        }
    }

    private AboutDialog(Display display, boolean splash) {
        mShell = new Shell(display, 0x10008);
        mShell.setText(splash ? getSplashTitle() : I18N.get("ABOUT_TITLE", "{0} - About", new String[] {
            About.TITLE_SHORT
        }));
        mShell.setImage(Icons.APP);
        mShell.setLayout(((org.eclipse.swt.widgets.Layout) (new GridLayout())));
        CloseCommand closeCommand = new CloseCommand();
        mShell.addMouseListener(((org.eclipse.swt.events.MouseListener) (closeCommand)));
        mShell.setCapture(true);
        Composite top = new Composite(((Composite) (mShell)), 0);
        RowLayout topLayout = new RowLayout();
        topLayout.marginHeight = 6;
        topLayout.marginWidth = 6;
        topLayout.spacing = 12;
        top.setLayout(((org.eclipse.swt.widgets.Layout) (topLayout)));
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = 4;
        top.setLayoutData(((Object) (gd)));
        top.addMouseListener(((org.eclipse.swt.events.MouseListener) (closeCommand)));
        Label topImageLabel = new Label(top, 0);
        topImageLabel.setImage(Icons.APP);
        topImageLabel.addMouseListener(((org.eclipse.swt.events.MouseListener) (closeCommand)));
        Link topTextLabel = new Link(top, 0);
        topTextLabel.setText(About.TITLE+"\n\n" +
        		About.COPYRIGHT_LINE1+"\n"+About.COPYRIGHT_LINE2+"\n"+
				"<a>"+About.HOMEPAGE+"</a>"
				);
        topTextLabel.addListener(13, new Listener() {
            public void handleEvent(Event event) {
                try {
                    if(OSUtil.isWindows()) {
                        WinExt winExt = WinExt.getWinExt();
                        winExt.launchDefaultBrowser(About.HOMEPAGE);
                    }
//                    else if OSUtil.isLinux(){
//                    	//TODO
//                    }else {
//                    	//TODO
//                    }
                }
                catch(Exception e) {
                    ExceptionUtil.handleException(((Throwable) (e)));
                }
            }
        }
);
        if(splash) {
            mProgress = new LabelProgressBar(((Composite) (mShell)), 0);
            gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalAlignment = 4;
            mProgress.setLayoutData(((Object) (gd)));
        } else {
            TabFolder tabFolder = new TabFolder(((Composite) (mShell)), 0);
            gd = new GridData();
            gd.widthHint = 500;
            gd.heightHint = 300;
            tabFolder.setLayoutData(((Object) (gd)));
            tabFolder.setFocus();
            TabItem aboutTab = new TabItem(tabFolder, 0);
            aboutTab.setText("About");
            Text aboutText = new Text(((Composite) (tabFolder)), 586);
            aboutText.setText(About.ABOUT);
            aboutText.addMouseListener(((org.eclipse.swt.events.MouseListener) (closeCommand)));
            aboutTab.setControl(((org.eclipse.swt.widgets.Control) (aboutText)));
            TabItem licenseTab = new TabItem(tabFolder, 0);
            licenseTab.setText("License");
            Text licenseText = new Text(((Composite) (tabFolder)), 586);
            licenseText.setText(About.LICENSE);
            licenseText.addMouseListener(((org.eclipse.swt.events.MouseListener) (closeCommand)));
            licenseTab.setControl(((org.eclipse.swt.widgets.Control) (licenseText)));
            TabItem todoTab = new TabItem(tabFolder, 0);
            todoTab.setText("TODO");
            Text todoText = new Text(((Composite) (tabFolder)), 586);
            todoText.setText(About.TODO);
            todoText.addMouseListener(((org.eclipse.swt.events.MouseListener) (closeCommand)));
            todoTab.setControl(((org.eclipse.swt.widgets.Control) (todoText)));
            if(LogAppender.getLogAppender() != null) {
                TabItem logTab = new TabItem(tabFolder, 0);
                logTab.setText("Log");
                Text logText = new Text(((Composite) (tabFolder)), 778);
                logText.append(LogAppender.getLogAppender().getText());
                logText.setTopIndex(logText.getText().length());
                logTab.setControl(((org.eclipse.swt.widgets.Control) (logText)));
            }
        }
        mShell.pack();
        Monitor ma[] = Display.getDefault().getMonitors();
        if(ma != null) {
            Point size = mShell.getSize();
            mShell.setLocation((ma[0].getClientArea().width - size.x) / 2, (ma[0].getClientArea().height - size.y) / 2);
        }
        mShell.open();
    }

    public static String getSplashTitle() {
        return "deltaplayer";
    }

    private void runModal() {
        Display display = mShell.getDisplay();
        while(!mShell.isDisposed()) 
            if(!display.readAndDispatch())
                display.sleep();
    }

}
