// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   ExceptionUtil.java

package dplayer.gui;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import dplayer.About;
import dplayer.gui.i18n.I18N;


public class ExceptionUtil {
	private static Logger log=Logger.getLogger(ExceptionUtil.class);

    public ExceptionUtil() {
    }

    public static void handleException(final Throwable e) {
        log.error("",e);
        final StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                MessageBox messageBox = new MessageBox(MainShell.getShell(), SWT.OK|SWT.ABORT|SWT.ICON_ERROR); //TODO put all errors into a single error reporting box.
                messageBox.setText((new StringBuilder(About.TITLE_SHORT+" - ")).append(I18N.get("ERROR_TITLE", "error")).toString());
                if(e instanceof FileNotFoundException) {
                    Controller.getController().stop(dplayer.model.Player.StopReason.OTHER_STOP_REASON);
                    messageBox.setMessage(I18N.get("ERROR_FILE_NOT_FOUND", "Unable to open file \"{0}\".", new String[] {
                        e.getMessage()
                    }));
                } else {
                    messageBox.setMessage(sw.toString());
                }
                if(messageBox.open()==SWT.ABORT){
                	log.info("Abort selected by the user, exiting");
                	MainShell.getShell().dispose();
                }
            }
        });
    }
}

//TODO do not kill MPlayer, this should avoid:
/*
java.io.IOException: Неправильный дескриптор файла/Bad file descriptor
at java.io.FileInputStream.readBytes(Native Method)
at java.io.FileInputStream.read(FileInputStream.java:199)
at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:264)
at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:306)
at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:158)
at java.io.InputStreamReader.read(InputStreamReader.java:167)
at java.io.BufferedReader.fill(BufferedReader.java:136)
at java.io.BufferedReader.readLine(BufferedReader.java:299)
at java.io.BufferedReader.readLine(BufferedReader.java:362)
at dplayer.media.MPlayerThread$StreamGobbler.run(MPlayerThread.java:25)
*/