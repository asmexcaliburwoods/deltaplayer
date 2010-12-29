// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LogAppender.java

package dplayer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LogAppender extends AppenderSkeleton {
	{redirectStdStreams();};

    private StringBuffer mLogBuffer;
    private static LogAppender sInstance;
    private static final String LINE_SEP = System.getProperty("line.separator");
    private PrintStream oldOut;

    public static LogAppender getLogAppender() {
        return sInstance;
    }

    public LogAppender() {
        mLogBuffer = new StringBuffer();
        sInstance = this;
    }

    public String getText() {
        return mLogBuffer.toString();
    }

    protected void append(LoggingEvent event) {
        if(layout == null)
            return;
        StringBuffer buf = new StringBuffer();
        buf.append(layout.format(event));
        if(layout.ignoresThrowable()) {
            String s[] = event.getThrowableStrRep();
            if(s != null) {
                String as[];
                int j = (as = s).length;
                for(int i = 0; i < j; i++) {
                    String value = as[i];
                    buf.append(LINE_SEP).append(value);
                }

                buf.append(LINE_SEP);
            }
        }
        String s = buf.toString();
        if(mLogBuffer.length() > 0x10000)
            mLogBuffer.delete(0, mLogBuffer.length() - 32768);
        mLogBuffer.append(s);
        oldOut.print(s);
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return true;
    }

    private void redirectStdStreams() {
        String enc0 = System.getProperty("file.encoding");
        final String enc = enc0 != null && enc0.length() != 0 ? enc0 : "ASCII";
        oldOut = System.out;
        PrintStream out = new PrintStream(((OutputStream) (new OutputStream() {
            public void write(int b) throws IOException {
                write(new byte[] {
                    (byte)b
                });
            }

            public void write(byte b[], int off, int len) throws IOException {
                mLogBuffer.append(new String(b, off, len, enc));
                oldOut.write(b, off, len);
            }
        })), true);
        System.setErr(out);
        System.setOut(out);
    }
}
