package com.yinhe.model;

/**
 * Percent discribe a struct contains 4 percent:
 * leftPercent,topPercent,widthPercent,heightPercent;
 * For example,leftPercent is the percent which can be seen in Vertical position Bar.
 * */
public class Percent
{
    public int leftPercent;
    public int topPercent;
    public int rightPercent;
    public int bottomPercent;

    public Percent(int leftPercent,int topPercent,int widthPercent,int heightPercent)
    {
        this.leftPercent = leftPercent;
        this.topPercent = topPercent;
        this.rightPercent = widthPercent;
        this.bottomPercent = heightPercent;
    }

    public Percent()
    {
        this.leftPercent = 0;
        this.topPercent = 0;
        this.rightPercent = 0;
        this.bottomPercent = 0;
    }
}
