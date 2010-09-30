// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MenuFactory.java

package dplayer.gui;

import dplayer.Settings;
import dplayer.gui.i18n.I18N;
import dplayer.gui.icons.Icons;
import dplayer.model.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

// Referenced classes of package dplayer.gui:
//            CommandFactory, GuiUtils, Controller

class MenuFactory {
    private static Menu sToolbarPopupMenu;
    private static Menu sTrayIconPopupMenu;

    MenuFactory() {}

    static Menu getToolbarPopupMenu(Control parent) {
        if(sToolbarPopupMenu == null) {
            Menu menu = new Menu(parent);
            addMenuItem(menu, Icons.APP, I18N.get("MENU_ABOUT", "About {0}...", new String[] {
                "deltaplayer"
            }), ((SelectionListener) (CommandFactory.getAboutCommand())));
            addMenuItem(menu, ((Image) (null)), I18N.get("MENU_SETTINGS", "Settings..."), ((SelectionListener) (CommandFactory.getSettingsCommand())));
            addMenuSeparator(menu);
            addMenuItem(menu, Icons.PLAY, I18N.get("CONTROL_PLAY", "Play track from beginning"), ((SelectionListener) (CommandFactory.getPlayCommand())));
            addMenuItem(menu, Icons.PAUSE, I18N.get("CONTROL_PAUSE", "Pause / Resume (Unpause)"), ((SelectionListener) (CommandFactory.getPauseCommand())));
            addMenuItem(menu, Icons.STOP, I18N.get("CONTROL_STOP", "Stop playing"), ((SelectionListener) (CommandFactory.getStopCommand())));
            addMenuSeparator(menu);
            addMenuItem(menu, Icons.PREV, I18N.get("CONTROL_PREV", "Play previous track"), ((SelectionListener) (CommandFactory.getPrevCommand())));
            addMenuItem(menu, Icons.NEXT, I18N.get("CONTROL_NEXT", "Play next track"), ((SelectionListener) (CommandFactory.getNextCommand())));
            addMenuSeparator(menu);
            addMenuItem(menu, Icons.EXIT, I18N.get("MENU_EXIT", "Exit"), ((SelectionListener) (CommandFactory.getExitCommand())));
            sToolbarPopupMenu = menu;
        }
        return sToolbarPopupMenu;
    }

    static Menu getTrayIconPopupMenu(Control parent) {
        if(sTrayIconPopupMenu == null) {
            Menu menu = new Menu(parent);
            addMenuItem(menu, Icons.APP, I18N.get("MENU_ABOUT", "About {0}...", new String[] {
                "deltaplayer"
            }), ((SelectionListener) (CommandFactory.getAboutCommand())));
            addMenuItem(menu, ((Image) (null)), I18N.get("MENU_SETTINGS", "Settings..."), ((SelectionListener) (CommandFactory.getSettingsCommand())));
            addMenuSeparator(menu);
            addMenuItem(menu, Icons.PLAY, I18N.get("CONTROL_PLAY", "Play track from beginning"), ((SelectionListener) (CommandFactory.getPlayCommand())));
            addMenuItem(menu, Icons.PAUSE, I18N.get("CONTROL_PAUSE", "Pause / Resume (Unpause)"), ((SelectionListener) (CommandFactory.getPauseCommand())));
            addMenuItem(menu, Icons.STOP, I18N.get("CONTROL_STOP", "Stop playing"), ((SelectionListener) (CommandFactory.getStopCommand())));
            addMenuSeparator(menu);
            addMenuItem(menu, Icons.PREV, I18N.get("CONTROL_PREV", "Play previous track"), ((SelectionListener) (CommandFactory.getPrevCommand())));
            addMenuItem(menu, Icons.NEXT, I18N.get("CONTROL_NEXT", "Play next track"), ((SelectionListener) (CommandFactory.getNextCommand())));
            addMenuSeparator(menu);
            addMenuItem(menu, Icons.EXIT, I18N.get("MENU_EXIT", "Exit"), ((SelectionListener) (CommandFactory.getExitCommand())));
            sTrayIconPopupMenu = menu;
        }
        return sTrayIconPopupMenu;
    }

    static Menu getSongTablePopupMenu(Table table, Song song) {
        Menu menu = new Menu(((Control) (table)));
        addMenuItem(menu, Icons.APP, I18N.get("MENU_ABOUT", "About {0}...", new String[] {
            "deltaplayer"
        }), ((SelectionListener) (CommandFactory.getAboutCommand())));
        addMenuItem(menu, ((Image) (null)), I18N.get("MENU_SETTINGS", "Settings..."), ((SelectionListener) (CommandFactory.getSettingsCommand())));
        addMenuSeparator(menu);
        addMenuItem(menu, Icons.PLAY, I18N.get("CONTROL_PLAY", "Play track from beginning"), ((SelectionListener) (CommandFactory.getPlayCommand())));
        addMenuItem(menu, Icons.PAUSE, I18N.get("CONTROL_PAUSE", "Pause / Resume (Unpause)"), ((SelectionListener) (CommandFactory.getPauseCommand())));
        addMenuItem(menu, Icons.STOP, I18N.get("CONTROL_STOP", "Stop playing"), ((SelectionListener) (CommandFactory.getStopCommand())));
        addMenuSeparator(menu);
        addMenuItem(menu, Icons.PREV, I18N.get("CONTROL_PREV", "Play previous track"), ((SelectionListener) (CommandFactory.getPrevCommand())));
        addMenuItem(menu, Icons.NEXT, I18N.get("CONTROL_NEXT", "Play next track"), ((SelectionListener) (CommandFactory.getNextCommand())));
        addMenuSeparator(menu);
        addCheckBoxMenuItem(menu, I18N.get("MENU_SKIP", "Skip track"), SongExt.isSkip(song), ((SelectionListener) (CommandFactory.getSkipTrackCommand(song))));
        addCheckBoxMenuItem(menu, I18N.get("MENU_DISPLAY_SKIPPED", "Show skipped tracks"), Settings.getBoolean(Settings.DISPLAY_SKIPPED), ((SelectionListener) (CommandFactory.getDisplaySkippedCommand())));
        addMenuSeparator(menu);
        addMenuItem(menu, Icons.EXIT, I18N.get("MENU_EXIT", "Exit"), ((SelectionListener) (CommandFactory.getExitCommand())));
        return menu;
    }

    static Menu getLocationTreePopupMenu(Tree parent, final TreeItem item) {
        Directory d = GuiUtils.getDirectory(item);
        final boolean selectFlat = d.isDefaultSelectFlat();
        Menu menu = new Menu(((Control) (parent)));
        addMenuItem(menu, Icons.APP, I18N.get("MENU_ABOUT", "About {0}...", new String[] {
            "deltaplayer"
        }), ((SelectionListener) (CommandFactory.getAboutCommand())));
        addMenuItem(menu, ((Image) (null)), I18N.get("MENU_SETTINGS", "Settings..."), ((SelectionListener) (CommandFactory.getSettingsCommand())));
        addMenuSeparator(menu);
        addCheckBoxMenuItem(menu, I18N.get("MENU_SELECT_FLAT", "Select (including all its directories)"), selectFlat, ((SelectionListener) (new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Directory directory = GuiUtils.getDirectory(item);
                Controller.getController().selectDirectory(directory, !selectFlat, true, true);
            }
        })));
        addMenuItem(menu, Icons.REFRESH, I18N.get("MENU_REFRESH", "Refresh"), ((SelectionListener) (CommandFactory.getRefreshCommand())));
        addMenuSeparator(menu);
        addMenuItem(menu, Icons.EXIT, I18N.get("MENU_EXIT", "Exit"), ((SelectionListener) (CommandFactory.getExitCommand())));
        return menu;
    }

    private static MenuItem addCheckBoxMenuItem(Menu menu, String text, boolean checked, SelectionListener l) {
        if(menu == null) {
            throw new AssertionError();
        } else {
            MenuItem mi = new MenuItem(menu, 32);
            mi.setText(text);
            mi.setImage(((Image) (null)));
            mi.setSelection(checked);
            mi.addSelectionListener(l);
            return mi;
        }
    }

    private static MenuItem addMenuItem(Menu menu, Image icon, String text, SelectionListener command) {
        if(menu == null)
            throw new AssertionError();
        if(command == null) {
            throw new AssertionError();
        } else {
            MenuItem mi = new MenuItem(menu, 8);
            mi.setText(text);
            mi.setImage(icon);
            mi.addSelectionListener(command);
            return mi;
        }
    }

    private static void addMenuSeparator(Menu menu) {
        new MenuItem(menu, 2);
    }

}
