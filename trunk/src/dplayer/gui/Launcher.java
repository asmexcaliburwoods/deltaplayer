// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Launcher.java

package dplayer.gui;

import dplayer.*;
import dplayer.ext.ExtException;
import dplayer.ext.linux.LinuxExt;
import dplayer.ext.win.WinExt;
import dplayer.gui.i18n.I18N;
import dplayer.model.cache.CacheManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.*;

// Referenced classes of package dplayer.gui:
//            AboutDialog, MainShell, Controller

public class Launcher {
	//static{org.apache.log4j.PropertyConfigurator.configure("dplayer/log4j.properties");}
    private static final Logger logger = Logger.getLogger(Launcher.class);
    private static volatile boolean closed;

    public Launcher() {
    }

    private static List<File> getFilesFromArgs(String args[]) {
        List<File> list = ((List<File>) (new ArrayList<File>()));
        String as[];
        int j = (as = args).length;
        for(int i = 0; i < j; i++) {
            String arg = as[i];
            File f = new File(arg);
            if(f.exists())
                list.add(((f)));
        }

        return list;
    }

    private static boolean isRunning() throws ExtException {
        if(OSUtil.isLinux())
            return LinuxExt.isRunning();
        try {
            if(OSUtil.isWindows())
                return WinExt.isRunning();
        }
        catch(ExtException extexception) { }
        return false;
    }

    public static void main(String args[]){
        try {
            Settings.init();
            if(isRunning()) {
                logger.info("dplayer is running already!");
                saveOk();
                System.exit(1);
            }
            Display display = new Display();
            AboutDialog.showSplash(display);
            AboutDialog.setProgress(10, I18N.get("INIT_LOADING", "Loading..."));
            MainShell shell = new MainShell(display);
            if(CacheManager.isEnabled()) {
                AboutDialog.setProgress(30, I18N.get("INIT_CACHE", "Initializing cache..."));
                CacheManager.init();
            }
            AboutDialog.setProgress(60, I18N.get("INIT_PLAYER", "Initializing player..."));
            Controller.getController().init(getFilesFromArgs(args));
            AboutDialog.setProgress(100, I18N.get("INIT_OPENING", "Opening..."));
            shell.open();
            MainShell.getShell().addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    Launcher.close();
                }
            });
            display.addListener(21, new Listener() {
                public void handleEvent(Event event) {
                    Launcher.close();
                }
            });
            AboutDialog.hideSplash();
            while(!MainShell.getShell().isDisposed()) 
                if(!display.readAndDispatch())
                    display.sleep();
            display.dispose();
        }catch(Throwable e) {
            ExceptionUtil.handleException(e);
        }finally{
        	close();
        }
    }

    private static void close() {
        if(closed)return;
    	synchronized(Launcher.class) {
            if(closed)return;
    		try{
	            closed = true;
	            Shell shell;
	            Controller.getController().setApplicationExiting(true);
	            shell = MainShell.getShell();
	            Settings.setBoolean(Settings.WINDOW_MAXIMIZED, shell.getMaximized());
	            Settings.setBoolean(Settings.WINDOW_MINIMIZED, shell.getMinimized());
	            Controller.getController().deinit();
	            saveOk();
	        }catch(Throwable e) {
	            ExceptionUtil.handleException(e);
	        }
	    }
    }

    private static void saveOk() throws FileNotFoundException {
        try {
            PrintStream ps = new PrintStream(((java.io.OutputStream) (new FileOutputStream("ok.log"))));//TODO move to logs system. 
            ps.println("ok");
            ps.close();
        }
        catch(Exception e) {
            ExceptionUtil.handleException(e);
        }
    }
}
