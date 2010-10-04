/*
Deltaplayer.
© Copyright 2007-2008 Tobias Winterhalter
© Copyright 2008-2010 Evgeniy Philippov
*/

package dplayer;

import java.io.*;
import java.util.Properties;

import dplayer.gui.ExceptionUtil;

public class About {

	private static final Properties relProp=new Properties();
	static{
		InputStream is=About.class.getResourceAsStream("/dplayer/release.properties");
		try{
			relProp.load(is);
			String ver=relProp.getProperty("app.version");
			VERSION=ver==null?"N/A":ver;
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			if(is!=null)try{is.close();}catch(IOException e){e.printStackTrace();}
		}
	}
    public static final String TITLE_SHORT = "deltaplayer";
    public static final String VERSION;
    public static final String TITLE = "deltaplayer v."+VERSION+" - a music player";
    public static final String COPYRIGHT_LINE1 = "© Copyright 2007-2008 Tobias Winterhalter";
    public static final String COPYRIGHT_LINE2 = "© Copyright 2008-2010 Evgeniy Philippov";
    public static final String HOMEPAGE = "http://code.google.com/p/deltaplayer/";
    public static final String ABOUT = loadText("/dplayer/ABOUT");
    public static final String LICENSE = loadText("/dplayer/LICENSE");
    public static final String LICENSE_SHORT = loadText("/dplayer/LICENSE_SHORT");
    public static final String TODO = loadText("/dplayer/TODO");
    public static final String OS = System.getProperty("os.name");

    public About() {
    }

    private static String loadText(String name) {
    	if(name==null)throw new AssertionError();
        StringBuilder result;
        BufferedReader in;
        result = new StringBuilder();
        in = null;
        try {
            in = new BufferedReader(((java.io.Reader) (new InputStreamReader(About.class.getResourceAsStream(name)))),64*1024);
            String line;
            while((line = in.readLine()) != null)  {
                result.append(line).append("\n");
            }
        }catch(Throwable e){
        	throw new RuntimeException("loadText("+name+")",e);
        }finally{
            if(in != null)
                try {
                    in.close();
                }catch(IOException ioexception){
                	ExceptionUtil.handleException(ioexception);
                }
        }
        return result.toString();
    }
}
