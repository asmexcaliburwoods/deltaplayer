// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst splitstr(100,nl) nonlb safe 
// Source File Name:   LabelProgressBar.java

package dplayer.gui.widgets;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class LabelProgressBar extends Canvas {
    private String mText;
    private int mMinimum;
    private int mMaximum;
    private int mSelection;

    public LabelProgressBar(Composite parent, int style) {
        super(parent, style);
        mMaximum = 100;
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                paint(e);
            }
        });
    }

    protected void paint(PaintEvent e) {
        GC gc = e.gc;
        Rectangle clip = gc.getClipping();
        double w = ((double)mSelection / (double)mMaximum) * (double)clip.width;
        gc.setBackground(getDisplay().getSystemColor(18));
        gc.fillRectangle(clip.x, clip.y, clip.x + (int)w, clip.height);
        if(mText != null) {
            gc.setForeground(getDisplay().getSystemColor(20));
            gc.drawString(mText, 3, 1, true);
        }
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
        int width = 0;
        int height = 0;
        String text = mText;
        if(text == null)
            text = "X";
        GC gc = new GC(((org.eclipse.swt.graphics.Drawable) (this)));
        Point extent = gc.stringExtent(text);
        gc.dispose();
        width += extent.x;
        height = Math.max(height, extent.y);
        if(wHint != -1)
            width = wHint;
        if(hHint != -1)
            height = hHint;
        return new Point(width + 2, height + 2);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        redraw();
        update();
    }

    public int getMaximum() {
        return mMaximum;
    }

    public void setMaximum(int maximum) {
        if(maximum <= mMinimum)
            throw new AssertionError();
        mMaximum = maximum;
        if(mSelection > mMaximum)
            mSelection = mMaximum;
        redraw();
        update();
    }

    public int getMinimum() {
        return mMinimum;
    }

    public void setMinimum(int minimum) {
        if(minimum >= mMaximum)
            throw new AssertionError();//TODO bad assertion?
        mMinimum = minimum;
        if(mSelection < mMinimum)
            mSelection = mMinimum;
        redraw();
        update();
    }

    public int getSelection() {
        return mSelection;
    }

    public void setSelection(int selection) {
        mSelection = selection;
        redraw();
        update();
    }

}
