// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Icons.java

package dplayer.gui.icons;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class Icons {

    private static final Logger logger = Logger.getLogger(Icons.class);
    private static final Map<Object, Object> sIconMap = new HashMap<Object, Object>();
    public static final Image APP = getIcon("my-app.png");
    public static final Image REFRESH = getIcon("view-refresh.png");
    public static final Image PLAY = getIcon("media-playback-start.png");
    public static final Image STOP = getIcon("media-playback-stop.png");
    public static final Image PAUSE = getIcon("media-playback-pause.png");
    public static final Image PREV = getIcon("media-skip-backward.png");
    public static final Image NEXT = getIcon("media-skip-forward.png");
    public static final Image PLAY_PLAYING = getIcon("my-playback-started.png");
    public static final Image PAUSE_PAUSED = getIcon("my-playback-paused.png");
    public static final Image SHUFFLE_ON = getIcon("my-shuffle-on.png");
    public static final Image SHUFFLE_OFF = getIcon("my-shuffle-off.png");
    public static final Image REPEAT_NONE = getIcon("my-repeat-none.png");
    public static final Image REPEAT_ONE = getIcon("my-repeat-one.png");
    public static final Image REPEAT_ALL = getIcon("my-repeat-all.png");
    public static final Image VOLUME_UP = getIcon("my-volume-up.png");
    public static final Image VOLUME_DOWN = getIcon("my-volume-down.png");
    public static final Image VOLUME_MUTE = getIcon("my-volume-muted.png");
    public static final Image EXIT = getIcon("system-log-out.png");

    public Icons() {
    }

    private static Image getIcon(String name) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        Image icon = (Image)sIconMap.get(((Object) (name)));
        if(icon == null) {
            Display display = Display.getCurrent();
            InputStream in = Icons.class.getResourceAsStream((new StringBuilder("/dplayer/gui/icons/")).append(name).toString());
            if(in != null)
                try {
                    ImageData data = new ImageData(((InputStream) (new BufferedInputStream(in))));
                    if(data.transparentPixel > 0)
                        icon = new Image(((org.eclipse.swt.graphics.Device) (display)), data, data.getTransparencyMask());
                    else
                        icon = new Image(((org.eclipse.swt.graphics.Device) (display)), data);
                    in.close();
                    sIconMap.put(((Object) (name)), ((Object) (icon)));
                }
                catch(SWTException e) {
                    logger.error(((Object) (e)));
                }
                catch(IOException e) {
                    logger.debug(((Object) (e)));
                }
            else
                logger.error(((Object) ((new StringBuilder("Unable to load icon ")).append(name).toString())));
        }
        return icon;
    }

    public static void dispose() {
        Image image;
        for(Iterator<Object> iterator = sIconMap.values().iterator(); iterator.hasNext(); image.dispose())
            image = (Image)iterator.next();

        sIconMap.clear();
    }

}
