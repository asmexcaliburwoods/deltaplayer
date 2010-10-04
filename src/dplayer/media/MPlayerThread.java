// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MPlayerThread.java

package dplayer.media;

import dplayer.SettingsAdapter;
import dplayer.gui.ExceptionUtil;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.media:
//            MPlayer

class MPlayerThread extends Thread {
    private class StreamGobbler extends Thread {
        private String mName;
        private InputStream mInputStream;

        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(mInputStream));
                String line;
                while((line = br.readLine()) != null)  {
                    if(logger.isDebugEnabled())
                    	logger.debug(((Object) ((new StringBuilder(String.valueOf(((Object) (mName))))).append("> ").append(line).toString())));
                    if(line.startsWith("File not found: ")){
                    	line=line.substring("File not found: ".length());
                        player.fireExceptionEvent(new FileNotFoundException(line));
                    }else
                    if(line.startsWith("Exiting... (End of file)"))
                        player.fireTrackFinished();
                }
            }
            catch(Throwable e) {
            	if(e instanceof IOException&&"Stream closed".equals(e.getMessage())){
            		logger.info("Stream closed");
            	}
            	else
            		ExceptionUtil.handleException(e);
            }
        }


        StreamGobbler(String name, InputStream is) {
            super();
            if(name == null)
                throw new AssertionError();
            if(is == null) {
                throw new AssertionError();
            } else {
                mName = name;
                mInputStream = is;
            }
        }
    }


    private static final Logger logger = Logger.getLogger(MPlayerThread.class);
    private long mOffset;
    private MPlayer player;
    private volatile Process mProc;
    private volatile boolean threadStartupDone;
    private volatile boolean terminated;

    MPlayerThread(MPlayer player, long offset) {
        this.player = player;
        mOffset = offset;
    }

    boolean isTerminated() {
        return terminated;
    }

    void terminate() {
        new Thread() {
            public void run() {
                try {
                    while(!threadStartupDone) 
                        Thread.sleep(10L);
                    if(mProc != null) {
                        MPlayerThread.logger.debug(((Object) ((new StringBuilder("Killing ")).append(((Object) (mProc))).toString())));
                        mProc.destroy();
                    }
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void run() {
        Throwable tr = null;
    	try{
            List<Object> cmdArray = ((List<Object>) (new ArrayList<Object>(20)));
            cmdArray.add(((Object) (SettingsAdapter.getInstance().getMPlayerCommand())));
            if(MPlayer.isChangingProcessPrioritySupported()) {
                cmdArray.add("-priority");
                cmdArray.add(((Object) (SettingsAdapter.getInstance().getMPlayerProcessPriority().name())));
            }
            cmdArray.add("-ss");
            cmdArray.add(((Object) (Long.toString(mOffset))));
            cmdArray.add(((Object) (player.mFile.getPath())));
            if(logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                String s;
                for(Iterator<Object> iterator = cmdArray.iterator(); iterator.hasNext(); sb.append(s).append(' '))
                    s = (String)iterator.next();

                logger.debug(((Object) ((new StringBuilder("Executing [")).append(((Object) (sb))).append("]").toString())));
            }
            mProc = Runtime.getRuntime().exec((String[])cmdArray.toArray(((Object []) (new String[cmdArray.size()]))));
            logger.debug(((Object) ((new StringBuilder("  as process ")).append(((Object) (mProc))).toString())));
            threadStartupDone = true;
            StreamGobbler errorGobbler = new StreamGobbler("stderr", mProc.getErrorStream());
            errorGobbler.start();
            StreamGobbler outputGobbler = new StreamGobbler("stdout", mProc.getInputStream());
            outputGobbler.start();
            int exitCode = mProc.waitFor();
            logger.debug(((Object) ((new StringBuilder()).append(((Object) (mProc))).append(" finished with exit code ").append(exitCode).toString())));
        }
        catch(Exception e) {
            logger.error("", ((Throwable) (e)));
            tr = ((Throwable) (e));
    	}finally{
        threadStartupDone = true;
        terminated = true;
    	}
        if(tr != null)
            player.fireExceptionEvent(tr);
    }





}
