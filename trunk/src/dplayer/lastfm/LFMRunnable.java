// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LFMRunnable.java

package dplayer.lastfm;

import java.io.IOException;

import com.googlecode.ascrblr.api.scrobbler.AudioscrobblerService;
import com.googlecode.ascrblr.api.scrobbler.TrackInfo;
import com.googlecode.ascrblr.api.util.ServiceException;

public class LFMRunnable
    implements Runnable {

    private volatile boolean terminated;
    private LastFmQueueImpl queue;
    private AudioscrobblerService protocol;
    private boolean loggedIn;

    LFMRunnable(LastFmQueueImpl queue) {
        protocol = new AudioscrobblerService("tst", "1.0", "1.2");
        if(queue == null) {
            throw new AssertionError();
        } else {
            this.queue = queue;
            return;
        }
    }

    public void run() {
        try {
            login();
            while(!terminated && !Thread.interrupted())  {
                LastFmQueueImpl.QueueItem item = isLoggedIn() ? (LastFmQueueImpl.QueueItem)queue.getSongQueue().poll() : null;
                if(item == null)
                    synchronized(queue) {
                        ((Object) (queue)).wait();
                    }
                else
                    processSubmittedSong(item);
            }
            logout();
        }
        catch(InterruptedException interruptedexception) { }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }

    private void processSubmittedSong(LastFmQueueImpl.QueueItem item) {
        TrackInfo trackInfo = item.trackInfo;
        try {
            if(item.havePlayed)
                protocol.submit(trackInfo);
            else
                protocol.notifyNew(trackInfo);
        }
        catch(ServiceException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        loggedIn = false;
    }

    private void login() {
        String user = queue.getUserName();
        String password = queue.getPassword();
        if(user != null && password != null)
            try {
                protocol.setCredentials(user, password);
                loggedIn = true;
            }
            catch(ServiceException e) {
                e.printStackTrace();
            }
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public void wake() {
        synchronized(queue) {
            ((Object) (queue)).notify();
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}
