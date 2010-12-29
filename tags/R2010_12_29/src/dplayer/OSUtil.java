package dplayer;

public class OSUtil {
	public static boolean isLinux(){return About.OS.startsWith("Linux");}
	public static boolean isWindows(){return About.OS.startsWith("Windows");}
}
