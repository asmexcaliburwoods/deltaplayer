// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LinuxExt.java

package dplayer.ext.linux;

import dplayer.ext.ExtException;
import dplayer.gui.i18n.I18N;
import java.io.*;
import org.apache.log4j.Logger;

public class LinuxExt {
    public static class ExecResult {

        private String mOutput;
        private String mError;
        private int mExitCode;

        public String getOutput() {
            return mOutput;
        }

        public String getError() {
            return mError;
        }

        public int getExitCode() {
            return mExitCode;
        }

        ExecResult(String output, String error, int ec) {
            mOutput = output;
            mError = error;
            mExitCode = ec;
        }
    }

    private static class StreamGobbler extends Thread {

        private InputStream mInputStream;
        private StringBuffer mBuffer;

        public void run() {
            try {
                BufferedReader br = new BufferedReader(((java.io.Reader) (new InputStreamReader(mInputStream))));
                for(String line = null; (line = br.readLine()) != null;) {
                    mBuffer.append(line);
                    mBuffer.append('\n');
                }

            }
            catch(IOException ioexception) { }
        }

        String getBuffer() {
            return mBuffer.toString();
        }

        StreamGobbler(InputStream is) {
            mInputStream = is;
            mBuffer = new StringBuffer();
        }
    }


    protected static final Logger logger = Logger.getLogger(LinuxExt.class);

    public LinuxExt() {
    }

    public static ExecResult exec(String command) throws ExtException {
        try {
            logger.debug(((Object) ((new StringBuilder("Running [")).append(command).append("]...").toString())));
            Process p = Runtime.getRuntime().exec(command);
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream());
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream());
            errorGobbler.start();
            outputGobbler.start();
            int exitCode = p.waitFor();
            logger.debug(((Object) ((new StringBuilder("  command terminated with exit code ")).append(exitCode).toString())));
            return new ExecResult(outputGobbler.getBuffer(), errorGobbler.getBuffer(), exitCode);
        }
        catch(Throwable t) {
            logger.debug(((Object) (t)));
            throw new ExtException(I18N.get("ERROR_LINUX_EXEC_FAILED", "Error while running command '{0}'.", new String[] {
                command
            }), t);
        }
    }

    public static boolean isRunning() throws ExtException {
        ExecResult er = exec("ps aux");
        return er.getExitCode() == 0 && er.getOutput().contains("dplayer.jar");
    }

}
