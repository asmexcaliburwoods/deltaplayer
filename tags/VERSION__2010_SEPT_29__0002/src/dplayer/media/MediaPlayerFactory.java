// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MediaPlayerFactory.java

package dplayer.media;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.media:
//            MediaPlayer

public class MediaPlayerFactory {
    private static final Logger logger = Logger.getLogger(MediaPlayerFactory.class);
    private static final Map<Object, Object> sMediaPlayerMap = new HashMap<Object, Object>();

    public MediaPlayerFactory() {
    }

    public static void registerMediaPlayer(String name, Class<? extends MediaPlayer> mediaPlayerClass) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        if(mediaPlayerClass == null)
            throw new AssertionError();
        if(sMediaPlayerMap.get(((Object) (name))) != null)
            throw new RuntimeException((new StringBuilder("MediaPlayer with name '")).append(name).append("' already registered.").toString());
        logger.info(((Object) ((new StringBuilder("Registering MediaPlayer '")).append(name).append("' using class ").append(mediaPlayerClass.getCanonicalName()).append("...").toString())));
        try {
            Constructor<? extends MediaPlayer> constructor = mediaPlayerClass.getConstructor(new Class[0]);
            MediaPlayer mp = constructor.newInstance();
            sMediaPlayerMap.put(((Object) (name)), ((Object) (mp)));
        }
        catch(Exception e) {
            logger.error(((Object) (e)));
            throw new RuntimeException(((Throwable) (e)));
        }
    }

    public static MediaPlayer getMediaPlayer(String name) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        else
            return (MediaPlayer)sMediaPlayerMap.get(((Object) (name)));
    }

}
