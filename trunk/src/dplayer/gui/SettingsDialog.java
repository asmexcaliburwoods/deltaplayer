// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   SettingsDialog.java

package dplayer.gui;

import java.io.File;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import dplayer.About;
import dplayer.SettingsAdapter;
import dplayer.gui.i18n.I18N;
import dplayer.gui.icons.Icons;
import dplayer.media.MPlayer;

// Referenced classes of package dplayer.gui:
//            ExceptionUtil

class SettingsDialog {
    private class BrowseForCommandAdapter {
        private Text textField;
        private String commandName;

        public String getCommand() {
            return textField.getText();
        }



        private BrowseForCommandAdapter(final Composite parent, String textFieldValue, final String commandName) {
            this.commandName = commandName;
            Composite g = new Composite(parent, 0);
            g.setLayout(((org.eclipse.swt.widgets.Layout) (new GridLayout(1, false))));
            Label label = new Label(g, 16384);
            label.setText(I18N.get("SETTINGS_EXECUTABLE_FOR", "Executable for {0}:", new String[] {
                commandName
            }));
            Composite c = new Composite(g, 0);
            c.setLayout(((org.eclipse.swt.widgets.Layout) (new RowLayout())));
            textField = new Text(c, 18436);
            textField.setText(textFieldValue);
            label.addMouseListener(((org.eclipse.swt.events.MouseListener) (new MouseAdapter() {

                public void mouseUp(MouseEvent e) {
                    textField.setFocus();
                }
            })));
            textField.addFocusListener(((org.eclipse.swt.events.FocusListener) (new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    textField.selectAll();
                }
            })));
            Button browseButton = new Button(c, 16384);
            browseButton.setText(I18N.get("SETTINGS_BROWSE", "Browse..."));
            browseButton.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    try {
                        FileDialog fd = new FileDialog(parent.getShell(), 4096);
                        File f = new File(getCommand());
                        fd.setFileName(f.getName());
                        fd.setFilterPath(f.getAbsolutePath());
                        fd.setText(I18N.get("SETTINGS_BROWSE_FILEDIALOG_TITLE", "Select an executable for {0}", new String[] {
                            commandName
                        }));
                        String result = fd.open();
                        if(result != null)
                            textField.setText(result);
                    }
                    catch(Exception e1) {
                        ExceptionUtil.handleException(((Throwable) (e1)));
                    }
                }
            })));
        }

        BrowseForCommandAdapter(Composite composite, String s, String s1, BrowseForCommandAdapter browseforcommandadapter) {
            this(composite, s, s1);
        }
    }

    protected class CloseCommand extends MouseAdapter {
        public void mouseUp(MouseEvent e) {
            dispose();
        }

        protected CloseCommand() {}
    }

    protected Shell mShell;
    private static SettingsDialog sInstance;
    private Button displayCoverCheckbox;
    private Button cacheEnabledCheckbox;
    private BrowseForCommandAdapter mplayerCommand;
    private BrowseForCommandAdapter amixerCommand;
    private Combo mplayerPriority;

    static void show(Display display) {
        if(display == null)
            throw new AssertionError();
        if(sInstance != null) {
            throw new AssertionError();
        } else {
            sInstance = new SettingsDialog(display);
            sInstance.runModal();
            sInstance = null;
            return;
        }
    }

    private SettingsDialog(Display display) {
        mShell = new Shell(display, 0x10060);
        mShell.setText(I18N.get("SETTINGS_DIALOG_TITLE", "Settings - {0}", new String[] {
            "deltaplayer"
        }));
        mShell.setImage(Icons.APP);
        mShell.setLayout(((org.eclipse.swt.widgets.Layout) (new GridLayout(1, false))));
        TabFolder tabFolder = new TabFolder(((Composite) (mShell)), 0);
        GridData gd = new GridData();
        gd.widthHint = 500;
        gd.heightHint = 300;
        tabFolder.setLayoutData(((Object) (gd)));
        tabFolder.setFocus();
        TabItem mainTab = new TabItem(tabFolder, 0);
        mainTab.setText(I18N.get("SETTINGS_MAIN_TAB", "Main"));
        Composite mainPane = new Composite(((Composite) (tabFolder)), 0);
        GridLayout layout = new GridLayout(1, false);
        mainPane.setLayout(((org.eclipse.swt.widgets.Layout) (layout)));
        displayCoverCheckbox = new Button(mainPane, 16416);
        displayCoverCheckbox.setText(I18N.get("SETTINGS_DISPLAY_ALBUM_COVERS", "Display album covers"));
        displayCoverCheckbox.setSelection(SettingsAdapter.getInstance().isDisplayCovers());
        cacheEnabledCheckbox = new Button(mainPane, 16416);
        cacheEnabledCheckbox.setText(I18N.get("SETTINGS_ENABLE_SONG_CACHE", "Enable song cache"));
        cacheEnabledCheckbox.setSelection(SettingsAdapter.getInstance().isCacheEnabled());
        mplayerCommand = new BrowseForCommandAdapter(mainPane, SettingsAdapter.getInstance().getMPlayerCommand(), "mplayer", ((BrowseForCommandAdapter) (null)));
        if(MPlayer.isChangingProcessPrioritySupported()) {
            Label label = new Label(mainPane, 16384);
            label.setText(I18N.get("SETTINGS_MPLAYER_PROCESS_PRIORITY", "MPlayer Process Priority:"));
            mplayerPriority = new Combo(mainPane, 12);
            for(int i = 0; i < MPlayer.priorities.length; i++) {
                dplayer.media.MPlayer.ProcessPriority priority = MPlayer.priorities[i];
                mplayerPriority.add(priority.getDisplayName());
            }

            mplayerPriority.select(SettingsAdapter.getInstance().getMPlayerProcessPriority().ordinal());
        }
        if(!About.OS.startsWith("Windows"))
            amixerCommand = new BrowseForCommandAdapter(mainPane, SettingsAdapter.getInstance().getAmixerCommand(), "amixer", ((BrowseForCommandAdapter) (null)));
        mainTab.setControl(((org.eclipse.swt.widgets.Control) (mainPane)));
        Composite buttonsPane = new Composite(((Composite) (mShell)), 0);
        buttonsPane.setLayout(((org.eclipse.swt.widgets.Layout) (new GridLayout(2, false))));
        Button okBtn = new Button(buttonsPane, 0x1000000);
        okBtn.setLayoutData(((Object) (new GridData(0x20000))));
        okBtn.setText(I18N.get("OK", "OK"));
        okBtn.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(apply())
                    dispose();
            }
        })));
        Button cancelBtn = new Button(buttonsPane, 0x1000000);
        cancelBtn.setText(I18N.get("CANCEL", "Cancel"));
        cancelBtn.setLayoutData(((Object) (new GridData(0x20000))));
        cancelBtn.addSelectionListener(((org.eclipse.swt.events.SelectionListener) (new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                dispose();
            }
        })));
        mShell.pack();
        Monitor ma[] = Display.getDefault().getMonitors();
        if(ma != null) {
            Point size = mShell.getSize();
            mShell.setLocation((ma[0].getClientArea().width - size.x) / 2, (ma[0].getClientArea().height - size.y) / 2);
        }
        mShell.open();
    }

    private boolean apply() {
        SettingsAdapter sa = SettingsAdapter.getInstance();
        sa.setDisplayCovers(displayCoverCheckbox.getSelection());
        sa.setCacheEnabled(cacheEnabledCheckbox.getSelection());
        String c = mplayerCommand.getCommand();
        if("".equals(((Object) (c.trim())))) {
            invalidCommand(mplayerCommand);
            return false;
        }
        sa.setMPlayerCommand(c);
        if(mplayerPriority != null)
            sa.setMPlayerProcessPriority(MPlayer.priorities[mplayerPriority.getSelectionIndex()]);
        if(amixerCommand != null) {
            c = amixerCommand.getCommand();
            if("".equals(((Object) (c.trim())))) {
                invalidCommand(amixerCommand);
                return false;
            }
            sa.setAmixerCommand(c);
        }
        sa.commit();
        return true;
    }

    private void invalidCommand(BrowseForCommandAdapter command) {
        MessageBox mb = new MessageBox(getShell(), 33);
        mb.setMessage(I18N.get("SETTINGS_PLEASE_INPUT_COMMAND_FOR", "Please input command for {0}.", new String[] {
            command.commandName
        }));
        mb.setText(I18N.get("ERROR", "Error"));
        mb.open();
        command.textField.setFocus();
    }

    private Shell getShell() {
        return mShell;
    }

    private void runModal() {
        Display display = mShell.getDisplay();
        while(!mShell.isDisposed()) 
            if(!display.readAndDispatch())
                display.sleep();
    }

    private void dispose() {
        mShell.dispose();
    }



}
