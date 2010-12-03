// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MainArea.java

package dplayer.gui;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import dplayer.OSUtil;
import dplayer.Settings;
import dplayer.SettingsAdapter;
import dplayer.gui.i18n.I18N;
import dplayer.gui.icons.Icons;
import dplayer.model.Directory;
import dplayer.model.Location;
import dplayer.model.Player;
import dplayer.model.PlayerEventAdapter;
import dplayer.model.Song;
import dplayer.model.SongExt;

// Referenced classes of package dplayer.gui:
//            Controller, GuiUtils, MainShell, ExceptionUtil, 
//            MenuFactory, CommandFactory

public class MainArea {
    protected class CollapseDirectoryCommand
        implements Listener {

        public void handleEvent(Event event) {
            TreeItem item = (TreeItem)event.item;
            Directory d = (Directory)item.getData("DIRECTORY");
            MainArea.logger.debug(((Object) ((new StringBuilder("Collapse: ")).append(((Object) (d))).toString())));
        }

        protected CollapseDirectoryCommand() {
        }
    }

    protected class ExpandDirectoryCommand
        implements Listener {

        public void handleEvent(Event event) {
            expand((TreeItem)event.item);
        }

        protected ExpandDirectoryCommand() {
        }
    }

    protected class MouseOverCommand implements MouseMoveListener {
        private TableItem mMouseOverItem;
        public void mouseMove(MouseEvent event) {
            Settings.setInt(Settings.TABLE_COLUMN1_W, mTable.getColumn(0).getWidth());
            Settings.setInt(Settings.TABLE_COLUMN2_W, mTable.getColumn(1).getWidth());
            TableItem item = mTable.getItem(new Point(event.x, event.y));
            if(item != null) {
                if(item != mMouseOverItem)
                    setToolTip((Song)item.getData("SONG"));
            } else {
                cancelToolTip();
            }
            mMouseOverItem = item;
        }

        private void setToolTip(Song song) {
            StringBuilder sb = new StringBuilder();
            if(song.getTitle() != null && song.getTitle().length() > 0) {
                sb.append(song.getTitle()).append('\n');
                if(song.getArtist() != null && song.getArtist().length() > 0)
                    sb.append(song.getArtist()).append('\n');
                boolean nl = false;
                if(song.getAlbum() != null && song.getAlbum().length() > 0) {
                    sb.append(song.getAlbum());
                    nl = true;
                }
                if(song.getYear() != null && song.getYear().length() > 0) {
                    sb.append(", ").append(song.getYear());
                    nl = true;
                }
                if(nl)
                    sb.append('\n');
            }
//            if(CacheManager.isEnabled()) {
//                DateFormat df = SimpleDateFormat.getDateInstance(3);
//                Date date = SongExt.getInsertDate(song);
//                sb.append(I18N.get("STATS_INSERTED", "Inserted: {0}", new String[] {
//                    date != null ? df.format(date) : ""
//                })).append('\n');
//                Date lastPlayed = SongExt.getLastPlayed(song);
//                sb.append(I18N.get("STATS_LAST_PLAYED", "Last played: {0}", lastPlayed == null ? (new String[] {
//                    I18N.get("STATS_NEVER_PLAYED", "never")
//                }) : (new String[] {
//                    df.format(lastPlayed)
//                }))).append('\n');
//                sb.append(I18N.get("STATS_PLAY_COUNTER", "Play counter: {0} times", new String[] {
//                    Integer.toString(SongExt.getPlayCounter(song))
//                })).append('\n');
//            }
            if(sb.length() > 0)
                mTable.setToolTipText(sb.substring(0, sb.length() - 1));
            else
                cancelToolTip();
        }

        private void cancelToolTip() {
            mTable.setToolTipText(((String) (null)));
        }

        protected MouseOverCommand() {}
    }

    protected class PaintCoverCommand
        implements PaintListener {
        public void paintControl(PaintEvent event) {
            if(mCoverImage != null)
                synchronized(this) {
                    event.gc.drawImage(mCoverImage, 0, 0);
                }
        }

        protected PaintCoverCommand() {}
    }

    protected class PlayerEventHandler extends PlayerEventAdapter {
        public void locationAddedEvent(Location location) {
            if(location == null) {
                throw new AssertionError();
            } else {
                TreeItem node = new TreeItem(mTree, 0);
                node.setText(location.getName());
                node.setData("DIRECTORY", ((Object) (location.getDirectory())));
                node.setItemCount(1);
                mTree.showItem(node);
                return;
            }
        }

        public void locationRemovedEvent(Location location) {
            if(location == null)
                throw new AssertionError();
            TreeItem node = findTreeItem(location.getDirectory());
            if(node != null)
                node.dispose();
        }

        public void directoryChangedEvent(Directory directory) {
            if(directory == null)
                throw new AssertionError();
            TreeItem item = findTreeItem(directory);
            if(item != null)
                mTree.setSelection(item);
            if(SettingsAdapter.getInstance().isDisplayCovers())
                displayCover(directory);
        }

        public void directoryPopulatedEvent(final dplayer.model.Directory.PopulateRequest populateRequest, final Directory directory) {
            if(directory == null)throw new AssertionError();
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    try {
                        TreeItem item = findTreeItem(directory);
                        if(item != null)
                            addDirectory(item, directory);
                        if(directory == populateRequest.rootPopulateCall)
                            populateRequest.finished();
                    }
                    catch(Exception e) {
                        ExceptionUtil.handleException(((Throwable) (e)));
                    }
                }
            });
        }

        public void playlistCleared(final dplayer.model.Directory.PopulateRequest pr) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if(pr.aborted)
                        return;
                    try {
                        mTable.removeAll();
                    }
                    catch(Exception e) {
                        ExceptionUtil.handleException(((Throwable) (e)));
                    }
                    return;
                }
            });
        }

        public void songListChangedEvent(Directory directory, List<Song> songList) {
            if(songList == null)
                throw new AssertionError();
            mSelectedDirectory = directory;
            mSongList = songList;
            refreshTable();
            if(songList.size() > 0)
                mTable.select(0);
        }

        public void playlistItemAppended(final dplayer.model.Directory.PopulateRequest pr, final Song s) {
            if(s == null)throw new AssertionError();
            if(getSelectedDirectory() == pr.rootPopulateCall)
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        if(pr.aborted)
                            return;
                        try {
                            mSongList.add(s);
                            addSongToTable(s, pr.selectFirstSong);
                            File f = s.getFile();
                            long lastScanned = SongExt.getLastScanned(s);
                            if(lastScanned == f.lastModified()) {
                                MainArea.logger.debug(((Object) ((new StringBuilder("  Adding ")).append(f.getPath()).append(" using cached information.").toString())));
                            } else {
                                MainArea.logger.debug(((Object) ((new StringBuilder("  ")).append(f.getPath()).append(" is cached but newer - scanning again.").toString())));
                                Directory.addToFetchInfoQueue(s);
                            }
                        }
                        catch(Exception e) {
                            ExceptionUtil.handleException(((Throwable) (e)));
                        }
                        return;
                    }
                });
        }

        public void playlistPositionChangedEvent(final Song song) {
            if(song == null)throw new AssertionError();
            
            {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        try {
                            TableItem item = findTableItem(song);
                            if(item != null) {
                                int index = mTable.indexOf(item);
                                mTable.select(index);
                                Rectangle rect = mTable.getClientArea();
                                int itemHeight = mTable.getItemHeight();
                                int headerHeight = mTable.getHeaderHeight();
                                int visibleCount = (((rect.height - headerHeight) + itemHeight) - 1) / itemHeight;
                                if(index - mTable.getTopIndex() >= visibleCount || index < mTable.getTopIndex()) {
                                    int topIndex = index - 3;
                                    mTable.setTopIndex(Math.max(topIndex, 0));
                                }
                            }
                        }
                        catch(Exception e) {
                            ExceptionUtil.handleException(((Throwable) (e)));
                        }
                    }
                });
                return;
            }
        }

        public void songDetailsChangedEvent(final Song song) {
            if(song == null)throw new AssertionError();
            
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Controller.getController().updateToolbarSlider();
                    TableItem item = findTableItem(song);
                    if(item != null)
                        updateTableItem(item, song);
                }
            });
        }

        public void skipListChangedEvent(Song song) {
            if(song == null) {
                throw new AssertionError();
            } else {
                refreshTable();
            }
        }

        protected PlayerEventHandler() {}
    }


    protected static final Logger logger = Logger.getLogger(MainArea.class);
    protected Tree mTree;
    protected Table mTable;
    protected Image mCoverImage;
    protected java.util.List<Song> mSongList;
    private static MainArea sInstance;
    private Directory mSelectedDirectory;

    MainArea(Shell shell) {
        if(shell == null) {
            throw new AssertionError();
        } else {
        	instance=this;
            final SashForm sash = new SashForm(((org.eclipse.swt.widgets.Composite) (shell)), 0x10900);
            GridData gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.grabExcessVerticalSpace = true;
            gd.horizontalAlignment = 4;
            gd.verticalAlignment = 4;
            sash.setLayoutData(((Object) (gd)));
            sash.setLayout(((org.eclipse.swt.widgets.Layout) (new FillLayout())));
            mTree = new Tree(((org.eclipse.swt.widgets.Composite) (sash)), 4);
            mTree.addMouseListener(((org.eclipse.swt.events.MouseListener) (new MouseAdapter() {
                public void mouseUp(MouseEvent event) {
                    Point loc = new Point(event.x, event.y);
                    TreeItem item = mTree.getItem(loc);
                    if(item != null){
                        final Directory d = GuiUtils.getDirectory(item);
                        if(event.button == 1) {
                            if(d != null) {
                                mSelectedDirectory = d;
                                Controller.getController().selectDirectoryConditional(d, true, true);
                            }
                        } else {
                            if(d != null) {
                                final boolean selectFlat = d.isDefaultSelectFlat();
                                Menu menu = new Menu(mTree);
                                MenuFactory.addMenuItem(menu, Icons.APP, I18N.get("MENU_ABOUT", "About {0}...", new String[] {
                                    "deltaplayer"
                                }), ((SelectionListener) (CommandFactory.getAboutCommand())));
                                MenuFactory.addMenuItem(menu, ((Image) (null)), I18N.get("MENU_SETTINGS", "Settings..."), ((SelectionListener) (CommandFactory.getSettingsCommand())));
                                MenuFactory.addMenuSeparator(menu);
                                MenuFactory.addCheckBoxMenuItem(menu, I18N.get("MENU_SELECT_FLAT", "Select (including all its directories)"), selectFlat, ((SelectionListener) (new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent e) {
                                    	try{
                                    		Controller.getController().selectDirectory(d, !selectFlat, true, true);
                                    	}catch(Throwable tr){
                                    		ExceptionUtil.handleException(tr);
                                    	}
                                    }
                                })));
                                MenuFactory.addMenuItem(menu, Icons.REFRESH, I18N.get("MENU_REFRESH", "Refresh"), ((SelectionListener) (CommandFactory.getRefreshCommand())));
                                MenuFactory.addMenuSeparator(menu);
                                MenuFactory.addMenuItem(menu, Icons.EXIT, I18N.get("MENU_EXIT", "Exit"), ((SelectionListener) (CommandFactory.getExitCommand())));
                                Menu m = menu;
                                if(OSUtil.isWindows()){
                                	RECT rect = new RECT();
                                	OS.GetWindowRect(mTree.handle, rect);
                                	m.setLocation(rect.left + loc.x, rect.top + loc.y);
                                }
                                m.setVisible(true);
                            }
                        }
                    }
                }
            })));
            mTree.addListener(17, ((Listener) (new ExpandDirectoryCommand())));
            mTree.addListener(18, ((Listener) (new CollapseDirectoryCommand())));
            mTree.addControlListener(((org.eclipse.swt.events.ControlListener) (new ControlAdapter() {
                public void controlResized(ControlEvent event) {
                    Settings.setInt(Settings.TREE_W, sash.getWeights()[0] / 10);
                }
            })));
            mTable = new Table(((org.eclipse.swt.widgets.Composite) (sash)), 0x10010004);
            mTable.setHeaderVisible(true);
            mTable.setLinesVisible(false);
            mTable.addMouseMoveListener(((MouseMoveListener) (new MouseOverCommand())));
            mTable.addMouseListener(((org.eclipse.swt.events.MouseListener) (new MouseAdapter() {
                public void mouseUp(MouseEvent event) {
                	try{
	                    Point loc = new Point(event.x, event.y);
	                    TableItem item = mTable.getItem(loc);
	                    if(item != null) {
	                        Song s = GuiUtils.getSong(item);
	                        if(event.button == 1) {
	                            Controller.getController().selectSong(s);
	                        } else {
	                            Menu m = MenuFactory.getSongTablePopupMenu(mTable, s);
	                            if(OSUtil.isWindows()){
	                            	RECT rect = new RECT();
	                            	OS.GetWindowRect(mTable.handle, rect);
	                            	m.setLocation(rect.left + loc.x, rect.top + loc.y);
	                            }
	                            m.setVisible(true);
	                        }
	                    }
                	}catch(Throwable tr){
                		ExceptionUtil.handleException(tr);
                	}
                }

                public void mouseDoubleClick(MouseEvent event) {
                	try{
                		TableItem item = mTable.getItem(new Point(event.x, event.y));
                		if(item != null)
                			CommandFactory.getTreeSelectCommand().widgetSelected(((org.eclipse.swt.events.SelectionEvent) (null)));
                	}catch(Throwable tr){
                		ExceptionUtil.handleException(tr);
                	}
                }
            })));
            mTable.addPaintListener(((PaintListener) (new PaintCoverCommand())));
            TableColumn col1 = new TableColumn(mTable, 16384);
            col1.setText(I18N.get("TABLE_COLUMN1", "Filename"));
            col1.setWidth(Settings.getInt(Settings.TABLE_COLUMN1_W));
            TableColumn col2 = new TableColumn(mTable, 16384);
            col2.setText(I18N.get("TABLE_COLUMN2", "Length (m:s)"));
            col2.setWidth(Settings.getInt(Settings.TABLE_COLUMN2_W));
            int treew = Settings.getInt(Settings.TREE_W);
            sash.setWeights(new int[] {
                treew, 100 - treew
            });
            Player.getPlayer().addEventListener(((dplayer.model.PlayerEventListener) (new PlayerEventHandler())));
            sInstance = this;
            return;
        }
    }

    private TreeItem findTreeItem(TreeItem items[], Directory directory) {
        if(items == null)
            throw new AssertionError();
        if(directory == null)
            throw new AssertionError();
        TreeItem atreeitem[];
        int j = (atreeitem = items).length;
        for(int i = 0; i < j; i++) {
            TreeItem item = atreeitem[i];
            Directory d = (Directory)item.getData("DIRECTORY");
            if(d != null && d == directory)
                return item;
            TreeItem rti = findTreeItem(item.getItems(), directory);
            if(rti != null)
                return rti;
        }

        return null;
    }

    protected TreeItem findTreeItem(Directory directory) {
        if(directory == null)
            throw new AssertionError();
        else
            return findTreeItem(mTree.getItems(), directory);
    }

    protected TableItem findTableItem(Song song) {
        if(song == null)
            throw new AssertionError();
        TableItem atableitem[];
        int j = (atableitem = mTable.getItems()).length;
        for(int i = 0; i < j; i++) {
            TableItem item = atableitem[i];
            Song s = (Song)item.getData("SONG");
            if(s != null && s == song)
                return item;
        }

        return null;
    }

    void refreshTable() {
        int selected = mTable.getSelectionIndex();
        boolean displaySkipped = Settings.getBoolean(Settings.DISPLAY_SKIPPED);
        mTable.removeAll();
        Song s;
        for(Iterator<Song> iterator = mSongList.iterator(); iterator.hasNext(); addSongToTable(displaySkipped, s, false))
            s = iterator.next();
        mTable.setSelection(selected);
    }

    private void addSongToTable(Song s, boolean selectFirstSong) {
        addSongToTable(Settings.getBoolean(Settings.DISPLAY_SKIPPED), s, selectFirstSong);
    }

    private void addSongToTable(boolean displaySkipped, Song s, boolean selectFirstSong) {
        if(SongExt.isSkip(s) && !displaySkipped)
            return;
        Table table = sInstance.mTable;
        TableItem ti = new TableItem(table, 0);
        updateTableItem(ti, s);
        ti.setData("SONG", ((Object) (s)));
        if(SongExt.isSkip(s))
            ti.setForeground(ti.getDisplay().getSystemColor(18));
        if(selectFirstSong && table.getSelectionIndex() < 0)
            Controller.getController().selectSong(s);
    }
    
    private String baseFolderCanonicalPath="";
    private final String FILE_SEPARATOR=System.getProperty("file.separator");
    public synchronized void setBaseFolder(File f){
    	String p=getCanonicalPath(f);
    	if(!p.endsWith(FILE_SEPARATOR))p=p+FILE_SEPARATOR;
    	baseFolderCanonicalPath=p;
    }
    
    private static String getCanonicalPath(File f){
    	try{
    		return f.getCanonicalPath(); 
    	}catch(IOException e){
    		return f.getAbsolutePath();
    	}
    }

    private void updateTableItem(TableItem ti, Song s) {
        long duration = s.getDurationSeconds();
        String text="";
        File sf=s.getFile();
        String baseFolderCanonicalPath=this.baseFolderCanonicalPath;
        if(baseFolderCanonicalPath==null)throw new AssertionError();
        text=getCanonicalPath(sf);
        if(text.startsWith(baseFolderCanonicalPath))text=text.substring(baseFolderCanonicalPath.length());
        String durationString = duration >= 0L ? GuiUtils.formatTimeSeconds(duration) : "";
        ti.setText(new String[] {
            text, durationString
        });
    }
    
    private static MainArea instance;
    
    public static MainArea getInstance(){
    	return instance;
    }

    protected void addDirectory(TreeItem node, Directory directory) {
        if(node == null)
            throw new AssertionError();
        if(directory == null)
            throw new AssertionError();
        node.removeAll();
        TreeItem item;
        for(Iterator<Directory> iterator = directory.getDirectoryList().iterator(); iterator.hasNext(); logger.debug(((Object) ((new StringBuilder("  Adding ")).append(((Object) (item))).append(" to ").append(((Object) (node))).toString())))) {
            Directory d = iterator.next();
            item = new TreeItem(node, 0);
            item.setText(d.getName());
            item.setData("DIRECTORY", ((Object) (d)));
            item.setItemCount(1);
        }

    }

    protected synchronized void displayCover(Directory directory) {
        if(directory == null)
            throw new AssertionError();
        logger.debug("Trying to find cover...");
        mCoverImage = null;
        File imageFiles[] = directory.getImageFiles();
        if(imageFiles.length == 0) {
            logger.debug(" No images in this directory");
            return;
        }
        File imageFile = null;
        File afile[];
        int j = (afile = imageFiles).length;
        for(int i = 0; i < j; i++) {
            File f = afile[i];
            String na[] = f.getName().split("[.]");
            if(!na[0].equalsIgnoreCase("cover"))
                continue;
            imageFile = f;
            break;
        }

        if(imageFile == null)
            imageFile = imageFiles[0];
        logger.debug(((Object) ((new StringBuilder(" Using ")).append(imageFile.getName()).append(" as cover :-)").toString())));
        try {
            ImageData imageData = new ImageData(imageFile.getPath());
            imageData.alpha = 64;
            mCoverImage = new Image(((org.eclipse.swt.graphics.Device) (Display.getDefault())), imageData);
        }
        catch(SWTException e) {
            logger.debug(((Object) (e)));
        }
    }

    public Directory getSelectedDirectory() {
        return mSelectedDirectory;
    }

    private void expand(TreeItem item) {
        Directory d = (Directory)item.getData("DIRECTORY");
        MainShell.setWaitCursor();
        Controller.getController().expand(d);
        MainShell.setNormalCursor();
    }





}
