package com.android.settings.video;

/**
 * Rectangle discribe a rectangle aera which can be seen.
 * */
public class Rectangle
{
    public int left;
    public int top;
    public int width;
    public int height;

    public Rectangle(int left,int top,int width,int height)
    {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
    public Rectangle()
    {
        this.left = 0;
        this.top = 0;
        this.width = 0;
        this.height = 0;
    }
    public boolean equals(Object obj)
    {
        Rectangle r = (Rectangle) obj;
        if (r != null)
        {
            return left == r.left && top == r.top && width == r.width && height == r.height;
        }
        return false;
    }
}
