// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   Settings.java

package dplayer;

import java.io.*;
import java.util.*;
import org.apache.log4j.PropertyConfigurator;

public class Settings {
    static enum OsType {GENERIC,LINUX,WIN}

    protected static class Property {
        private String mKey;
        private String mDescription;
        private String mDefaultValues[];

        String getKey() {
            return mKey;
        }

        String getDescription() {
            return mDescription;
        }

        String getDefaultValue() {
            String result = null;
            String osLow = System.getProperty("os.name").toLowerCase();
            if(osLow.startsWith("linux"))
                result = mDefaultValues[OsType.LINUX.ordinal()];
            else
            if(osLow.startsWith("win"))
                result = mDefaultValues[OsType.WIN.ordinal()];
            if(result == null)
                result = mDefaultValues[OsType.GENERIC.ordinal()];
            if(result == null)
                throw new AssertionError();
            else
                return result;
        }

        private static String[] allOS(String val){
        	String[] res=new String[OsType.values().length];
        	for(int i=0;i<res.length;i++)res[i]=val;
        	return res;
        }
        
        public Property(String key, String value) {
            this(key, ((String) (null)), allOS(value));
        }

        public Property(String key, String values[]) {
            this(key, ((String) (null)), values);
        }

        public Property(String key, String description, String value) {
            this(key, description, allOS(value));
        }

        public Property(String key, String description, String values[]) {
            if((key == null || key.length() <= 0))
                throw new AssertionError();
            if((values == null || values.length != OsType.values().length))
                throw new AssertionError("key: \""+key+"\"");
            if(values[OsType.GENERIC.ordinal()] == null) {
                throw new AssertionError();
            } else {
                mKey = key;
                mDescription = description;
                mDefaultValues = new String[OsType.values().length];
                mDefaultValues[OsType.GENERIC.ordinal()] = values[OsType.GENERIC.ordinal()];
                mDefaultValues[OsType.LINUX.ordinal()] = values[OsType.LINUX.ordinal()];
                mDefaultValues[OsType.WIN.ordinal()] = values[OsType.WIN.ordinal()];
                return;
            }
        }
    }

    private static class Section
        implements Comparable<Section> {

        protected String mDescription;
        protected int mWeight;
        protected Property mProperties[];

        public int compareTo(Section o) {
            return mWeight >= o.mWeight ? mWeight <= o.mWeight ? 0 : -1 : 1;
        }

        protected Section(String description, int weight, Property properties[]) {
            mDescription = description;
            mWeight = weight;
            mProperties = properties;
        }
    }


//    private static final String ENCODING = "UTF-8";
//    private static final String FILENAME = "dplayer.properties";
    private static final Settings sInstance = new Settings();
    public static final Property WINDOW_X;
    public static final Property WINDOW_Y;
    public static final Property WINDOW_W;
    public static final Property WINDOW_H;
    public static final Property WINDOW_MAXIMIZED;
    public static final Property WINDOW_MINIMIZED;
    public static final Property TABLE_COLUMN1_W;
    public static final Property TABLE_COLUMN2_W;
    public static final Property TREE_W;
    public static final Property VOLUME;
    public static final Property ROOTS;
    public static final Property PLAYER;
    public static final Property MIXER;
    public static final Property DISPLAY_SKIPPED;
    public static final Property DISPLAY_COVER;
    public static final Property LOG_ROOT_LOGGER;
    public static final Property LOG_DPLAYER_APPENDER;
    public static final Property LOG_DPLAYER_LAYOUT;
    public static final Property LOG_DPLAYER_PATTERN;
    public static final Property CACHE_ENABLED;
    public static final Property CACHE_SONGS_FILE;
    public static final Property HISTORY_LAST_FLAT_DIR;
    public static final Property HISTORY_LAST_FILE;
    public static final Property HISTORY_LAST_OFFSET_SECONDS;
    public static final Property HISTORY_LAST_REPEAT;
    public static final Property HISTORY_LAST_SHUFFLE;
    public static final Property LAST_FM_ENABLED;
    public static final Property LAST_FM_USER_NAME;
    public static final Property LAST_FM_PASSWORD;
    private final Properties mProperties;
    private static List<Section> sSectionList;

    protected Settings() {
        FileInputStream stream;
        mProperties = new Properties();
        stream = null;
        try {
            File f = new File("dplayer.properties");
            if(f.exists()){
            	stream = new FileInputStream(f);
            	BufferedReader reader = new BufferedReader(((java.io.Reader) (new InputStreamReader(((java.io.InputStream) (new BufferedInputStream(((java.io.InputStream) (stream)), 0x10000))), "UTF-8"))));
            	do {
            		String line = reader.readLine();
            		if(line == null)
            			break;
            		readPropertyLine(mProperties, line);
            	} while(true);
            	PropertyConfigurator.configure(mProperties);
            }
        }catch(IOException ioexception) {
            throw new RuntimeException(ioexception);
        }finally{
            if(stream != null)
                try {
                    stream.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private static void readPropertyLine(Properties prop, String line) {
        line = line.trim();
        if(line.length() == 0)
            return;
        if(line.startsWith("#"))
            return;
        int p = line.indexOf('=');
        String key = line;
        String value = "";
        if(p > -1) {
            key = line.substring(0, p).trim();
            value = line.substring(p + 1).trim();
        }
        prop.setProperty(key, value);
    }

    public static void init() {
    }

    public static void save() {
        BufferedWriter out;
        BufferedReader in;
        out = null;
        in = null;
        try {
            File outFile = new File("~dplayer.properties");
            if(outFile.exists() && !outFile.delete())
                throw new IOException((new StringBuilder("Unable to delete temporary properties file ")).append(outFile.getPath()).append(".").toString());
            out = new BufferedWriter(((java.io.Writer) (new OutputStreamWriter(((java.io.OutputStream) (new FileOutputStream(outFile))), "UTF-8"))), 0x10000);
            List<Object> inKeys = null;
            File inFile = new File("dplayer.properties");
            if(inFile.exists()) {
                in = new BufferedReader(((java.io.Reader) (new InputStreamReader(((java.io.InputStream) (new FileInputStream(inFile)))))));
                inKeys = ((List<Object>) (new ArrayList<Object>()));
                Properties p = new Properties();
                String line;
                while((line = in.readLine()) != null)  {
                    if(line.trim().length() == 0 || line.startsWith("#")) {
                        out.write(line);
                        out.newLine();
                    }
                    p.clear();
                    readPropertyLine(p, line);
                    for(Iterator<Object> iterator = p.keySet().iterator(); iterator.hasNext();) {
                    	String key = (String)iterator.next();
                        if(sInstance.mProperties.get(key) != null) {
                            inKeys.add(key);
                            out.write((new StringBuilder()).append(key).append(" = ").append(sInstance.mProperties.get(key)).toString());
                            out.newLine();
                        }
                    }

                }
                in.close();
            }
            saveSections(out, inKeys);
            out.close();
            if(inFile.exists() && !inFile.delete())
                throw new IOException((new StringBuilder("Unable to delete old properties file ")).append(inFile.getPath()).append(".").toString());
            if(!outFile.renameTo(inFile))
                throw new IOException((new StringBuilder("Unable to rename temporary properties file to ")).append(inFile.getPath()).append(".").toString());
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }finally{
	        try {
	            if(out != null)
	                out.close();
	        }catch(IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            if(in != null)
	                in.close();
	        }catch(IOException e) {
	            e.printStackTrace();
	        }
        }
    }

    public static String getFilename() {
        return "dplayer.properties";
    }

    public static String getString(Property property) {
        if(property == null)
            throw new AssertionError();
        String value = sInstance.mProperties.getProperty(property.getKey());
        String returnValue = value == null ? property.getDefaultValue() : value;
        if(returnValue == null)
            throw new AssertionError();
        else
            return returnValue;
    }

    public static void setString(Property property, String value) {
        if(property == null)
            throw new AssertionError();
        if(value == null)
            sInstance.mProperties.remove(((Object) (property.getKey())));
        else
            sInstance.mProperties.setProperty(property.getKey(), value);
    }

    public static int getInt(Property property) {
        return Integer.parseInt(getString(property));
    }

    public static long getLong(Property property) {
        return Long.parseLong(getString(property));
    }

    public static void setInt(Property property, int value) {
        setString(property, Integer.toString(value));
    }

    public static void setLong(Property property, long value) {
        setString(property, String.valueOf(value));
    }

    public static float getFloat(Property property) {
        return Float.parseFloat(getString(property));
    }

    public static void setFloat(Property property, float value) {
        setString(property, Float.toString(value));
    }

    public static boolean getBoolean(Property property) {
        return Boolean.parseBoolean(getString(property));
    }

    public static void setBoolean(Property property, boolean value) {
        setString(property, Boolean.toString(value));
    }

    protected static void addSection(String description, int weight, Property properties[]) {
        if(description == null)
            throw new AssertionError();
        if(properties == null)
            throw new AssertionError();
        if(sSectionList == null)
            sSectionList = new ArrayList<Section>(10);
        sSectionList.add(new Section(description, weight, properties));
        Collections.sort(sSectionList);
    }

    private static void saveSections(BufferedWriter out, List<Object> existingKeys) throws IOException {
        for(Iterator<Section> iterator = sSectionList.iterator(); iterator.hasNext();) {
            Section s = iterator.next();
            if(existingKeys == null) {
                out.write("#");
                out.newLine();
                out.write((new StringBuilder("# ")).append(s.mDescription).toString());
                out.newLine();
                out.newLine();
            }
            Property aproperty[];
            int j = (aproperty = s.mProperties).length;
            for(int i = 0; i < j; i++) {
                Property p = aproperty[i];
                if(existingKeys == null || !existingKeys.contains(((Object) (p.getKey())))) {
                    if(p.getDescription() != null) {
                        out.write((new StringBuilder("# ")).append(p.getDescription()).toString());
                        out.newLine();
                    }
                    out.write((new StringBuilder(String.valueOf(((Object) (p.getKey()))))).append(" = ").append(getString(p)).toString());
                    out.newLine();
                    if(p.getDescription() != null)
                        out.newLine();
                }
            }

            if(existingKeys == null) {
                out.newLine();
                out.newLine();
            }
        }

    }

    static  {
        WINDOW_X = new Property("window.x", "0");
        WINDOW_Y = new Property("window.y", "0");
        WINDOW_W = new Property("window.w", "640");
        WINDOW_H = new Property("window.h", "480");
        WINDOW_MAXIMIZED = new Property("window.maximized", "false");
        WINDOW_MINIMIZED = new Property("window.minimized", "false");
        TABLE_COLUMN1_W = new Property("table.column1.w", "200");
        TABLE_COLUMN2_W = new Property("table.column2.w", "80");
        TREE_W = new Property("tree.w", "30");
        VOLUME = new Property("volume", "40");
        ROOTS = new Property("roots", "List of roots to be searched for music files.", "");
        PLAYER = new Property("player", "Media player to use for playback.", "mplayer");
        MIXER = new Property("mixer", "Media mixer to use for volume control.", new String[] {
            "none", "amixer", "WinExtMixer"
        });
        DISPLAY_SKIPPED = new Property("display.skipped", "Display (or hide) tracks that are marked to be skipped by player.", "false");
        DISPLAY_COVER = new Property("display.cover", "Try to display cover as background of track table.", "true");
        LOG_ROOT_LOGGER = new Property("log4j.rootLogger", "INFO, DPLAYERLOG");
        LOG_DPLAYER_APPENDER = new Property("log4j.appender.DPLAYERLOG", "dplayer.LogAppender");
        LOG_DPLAYER_LAYOUT = new Property("log4j.appender.DPLAYERLOG.layout", "org.apache.log4j.PatternLayout");
        LOG_DPLAYER_PATTERN = new Property("log4j.appender.DPLAYERLOG.layout.ConversionPattern", "%-5p [%t] %c - %m%n");
        CACHE_ENABLED = new Property("cache.enabled", "Enable caching of music file properties (e.g. tags, 'skip' flag, ...).", "true");
        CACHE_SONGS_FILE = new Property("cache.songs.file", "File for music file cache.", "songs.db");
        HISTORY_LAST_FLAT_DIR = new Property("history.last.flat.dir", "");
        HISTORY_LAST_FILE = new Property("history.last.file", "");
        HISTORY_LAST_OFFSET_SECONDS = new Property("history.last.offset.seconds", "0");
        HISTORY_LAST_REPEAT = new Property("history.last.repeat", "NONE, ONE, ALL", "NONE");
        HISTORY_LAST_SHUFFLE = new Property("history.last.shuffle", "false");
        LAST_FM_ENABLED = new Property("last.fm.enabled", "Submit to last.fm or not: true/false.", "false");
        LAST_FM_USER_NAME = new Property("last.fm.user.name", "", "");
        LAST_FM_PASSWORD = new Property("last.fm.user.password", "", "");
        addSection("Manual settings.", 100, new Property[] {
            ROOTS, PLAYER, MIXER, DISPLAY_SKIPPED, DISPLAY_COVER
        });
        addSection("Last.fm settings.", 99, new Property[] {
            LAST_FM_ENABLED, LAST_FM_USER_NAME, LAST_FM_PASSWORD
        });
        addSection("Cache settings.", 90, new Property[] {
            CACHE_ENABLED, CACHE_SONGS_FILE
        });
        addSection("Automatic settings - will be overwritten when application is closed.", 30, new Property[] {
            VOLUME, TREE_W, TABLE_COLUMN1_W, TABLE_COLUMN2_W, WINDOW_X, WINDOW_Y, WINDOW_W, WINDOW_H, WINDOW_MAXIMIZED, WINDOW_MINIMIZED, 
            HISTORY_LAST_FLAT_DIR, HISTORY_LAST_FILE, HISTORY_LAST_OFFSET_SECONDS, HISTORY_LAST_REPEAT, HISTORY_LAST_SHUFFLE
        });
        addSection("Log4j settings.", 0, new Property[] {
            LOG_ROOT_LOGGER, LOG_DPLAYER_APPENDER, LOG_DPLAYER_LAYOUT, LOG_DPLAYER_PATTERN
        });
    }
}
