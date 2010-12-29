// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   MediaMixerFactory.java

package dplayer.media;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

// Referenced classes of package dplayer.media:
//            MediaMixer

public class MediaMixerFactory {

    private static final Logger logger = Logger.getLogger(MediaMixerFactory.class);
    private static final Map<Object, Object> sMediaMixerMap = new HashMap<Object, Object>();

    public MediaMixerFactory() {
    }

    public static void registerMediaMixer(String name, Class<? extends MediaMixer> mediaMixerClass) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        if(mediaMixerClass == null)
            throw new AssertionError();
        if(sMediaMixerMap.get(((Object) (name))) != null)
            throw new RuntimeException((new StringBuilder("MediaMixer with name '")).append(name).append("' already registered.").toString());
        logger.info(((Object) ((new StringBuilder("Registering MediaMixer '")).append(name).append("' using class ").append(mediaMixerClass.getCanonicalName()).append("...").toString())));
        try {
            Constructor<? extends MediaMixer> constructor = mediaMixerClass.getConstructor(new Class[0]);
            MediaMixer mx = constructor.newInstance(new Object[0]);
            sMediaMixerMap.put(((Object) (name)), ((Object) (mx)));
        }
        catch(Exception e) {
            logger.error(((Object) (e)));
            throw new RuntimeException(((Throwable) (e)));
        }
    }

    public static MediaMixer getMediaMixer(String name) {
        if((name == null || name.length() <= 0))
            throw new AssertionError();
        else
            return (MediaMixer)sMediaMixerMap.get(((Object) (name)));
    }

}
