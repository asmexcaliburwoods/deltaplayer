// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   ExtException.java

package dplayer.ext;

public class ExtException extends Exception {
	private static final long serialVersionUID = -7872507755536519806L;

	public ExtException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtException(String message) {
        super(message);
    }
}
