package com.android.settings.video;

/**
 * Percent discribe a struct contains 4 percent:
 * leftPercent,topPercent,widthPercent,heightPercent;
 * For example,leftPercent is the percent which can be seen in Vertical position Bar.
 * */
public class Percent
{
    public int leftPercent;
    public int topPercent;
    public int widthPercent;
    public int heightPercent;

    public Percent(int leftPercent,int topPercent,int widthPercent,int heightPercent)
    {
        this.leftPercent = leftPercent;
        this.topPercent = topPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
    }

    public Percent()
    {
        this.leftPercent = 0;
        this.topPercent = 0;
        this.widthPercent = 0;
        this.heightPercent = 0;
    }
}
