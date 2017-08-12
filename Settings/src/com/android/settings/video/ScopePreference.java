package com.android.settings.video;

import android.content.Context;
import android.content.DialogInterface;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.graphics.Rect;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.settings.R;

import com.hisilicon.android.HiDisplayManager;
import com.hisilicon.android.DispFmt;

public class ScopePreference extends DialogPreference implements OnSeekBarChangeListener, OnKeyListener
{
    private static final String TAG = "ScopePreference";

    /**
     * Format and its value
     * It must be defined same as hi_unf_display.h
     * */
    public final static int ENC_FMT_1080P_60 = 0;         /*1080p60hz*/
    public final static int ENC_FMT_1080P_50 = 1;         /*1080p50hz*/
    public final static int ENC_FMT_1080P_30 = 2;         /*1080p30hz*/
    public final static int ENC_FMT_1080P_25 = 3;         /*1080p25hz*/
    public final static int ENC_FMT_1080P_24 = 4;         /*1080p24hz*/
    public final static int ENC_FMT_1080i_60 = 5;         /*1080i60hz*/
    public final static int ENC_FMT_1080i_50 = 6;         /*1080i60hz*/
    public final static int ENC_FMT_720P_60 = 7;          /*720p60hz*/
    public final static int ENC_FMT_720P_50 = 8;          /*720p50hz*/
    public final static int ENC_FMT_576P_50 = 9;          /*576p50hz*/
    public final static int ENC_FMT_480P_60 = 10;         /*480p60hz*/
    public final static int ENC_FMT_PAL = 11;             /*BDGHIPAL*/
    public final static int ENC_FMT_PAL_N = 12;           /*(N)PAL*/
    public final static int ENC_FMT_PAL_Nc = 13;          /*(Nc)PAL*/
    public final static int ENC_FMT_NTSC = 14;            /*(M)NTSC*/
    public final static int ENC_FMT_NTSC_J = 15;          /*NTSC-J*/
    public final static int ENC_FMT_NTSC_PAL_M = 16;      /*(M)PAL*/
    public final static int ENC_FMT_SECAM_SIN = 17;
    public final static int ENC_FMT_SECAM_COS = 18;
    public final static int ENC_FMT_861D_640X480_60 = 19;
    public final static int ENC_FMT_VESA_800X600_60 = 20;
    public final static int ENC_FMT_VESA_1024X768_60 = 21;
    public final static int ENC_FMT_VESA_1280X720_60 = 22;
    public final static int ENC_FMT_VESA_1280X800_60 = 23;
    public final static int ENC_FMT_VESA_1280X1024_60 = 24;
    public final static int ENC_FMT_VESA_1366X768_60 = 25;
    public final static int ENC_FMT_VESA_1440X900_60 = 26;
    public final static int ENC_FMT_VESA_1440X900_60_RB = 27;
    public final static int ENC_FMT_VESA_1600X1200_60 = 28;
    public final static int ENC_FMT_VESA_1920X1080_60 = 29;
    public final static int ENC_FMT_VESA_1920X1200_60 = 30;
    public final static int ENC_FMT_VESA_2048X1152_60 = 31;
    public final static int ENC_FMT_VESA_CUSTOMER_DEFINE =32;

    /**
     * Standard left,top,width,height of 1080P.
     * */
    public final static int TOP_1080P = 0;
    public final static int LEFT_1080P = 0;
    public final static int WIDTH_1080P = 1920;
    public final static int HEIGHT_1080P = 1080;

    /**
     * Standard left,top,width,height of 720P.
     * */
    public final static int TOP_720P = 0;
    public final static int LEFT_720P = 0;
    public final static int WIDTH_720P = 1280;
    public final static int HEIGHT_720P = 720;

    /**
     * Standard left,top,width,height of 576P.
     * */
    public final static int TOP_576P = 0;
    public final static int LEFT_576P = 0;
    public final static int WIDTH_576P = 720;
    public final static int HEIGHT_576P = 576;

    /**
     * Standard left,top,width,height of 480P.
     * */
    public final static int TOP_480P = 0;
    public final static int LEFT_480P = 0;
    public final static int WIDTH_480P = 720;
    public final static int HEIGHT_480P = 480;

    /**
     * Standard left,top,width,height of PAL.
     * */
    public final static int TOP_PAL = 0;
    public final static int LEFT_PAL = 0;
    public final static int WIDTH_PAL = 720;
    public final static int HEIGHT_PAL = 576;

    /**
     * Standard left,top,width,height of NTSC.
     * */
    public final static int TOP_NTSC = 0;
    public final static int LEFT_NTSC = 0;
    public final static int WIDTH_NTSC = 720;
    public final static int HEIGHT_NTSC = 480;



    /**
     * Display settings java interface.
     * */
    private HiDisplayManager display_manager = null;

    /**
     * A Rectangle for dispose.
     * */
    private Rectangle mRange;
    private Rectangle originalRange;

    /**
     * 4 TextView for 4 SeekBar,can be used in whole Class.
     * */
    private TextView vertical_text;
    private TextView horizontal_text;
    private TextView width_text;
    private TextView height_text;

    public final static int SEEKBAR_STEP_SIZE = 4;

    public ScopePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDialogLayoutResource(R.xml.preference_dialog_outrange);
        if(display_manager == null)
        {
            display_manager = new HiDisplayManager();
        }
        mRange = new Rectangle();
        originalRange = new Rectangle();

    }

    protected void onBindDialogView(View view)
    {
        SeekBar vertical_seek;
        SeekBar horizontal_seek;
        SeekBar width_seek;
        SeekBar height_seek;

        /**
         * Find TextView in layout XML.
         * */
        vertical_text = (TextView) view.findViewById(R.id.left_text);
        horizontal_text = (TextView) view.findViewById(R.id.top_text);
        width_text = (TextView) view.findViewById(R.id.width_text);
        height_text = (TextView) view.findViewById(R.id.height_text);

        /**
         * Find SeekBar in layout XML.
         * */
        vertical_seek = (SeekBar) view.findViewById(R.id.left_seek);
        horizontal_seek = (SeekBar) view.findViewById(R.id.top_seek);
        width_seek = (SeekBar) view.findViewById(R.id.width_seek);
        height_seek = (SeekBar) view.findViewById(R.id.height_seek);

        vertical_seek.setKeyProgressIncrement(SEEKBAR_STEP_SIZE);
        horizontal_seek.setKeyProgressIncrement(SEEKBAR_STEP_SIZE);
        width_seek.setKeyProgressIncrement(SEEKBAR_STEP_SIZE);
        height_seek.setKeyProgressIncrement(SEEKBAR_STEP_SIZE);

        /**
         * Sets a listener to receive notifications of changes by pull SeekBar.
         * */
        vertical_seek.setOnSeekBarChangeListener(this);
        horizontal_seek.setOnSeekBarChangeListener(this);
        width_seek.setOnSeekBarChangeListener(this);
        height_seek.setOnSeekBarChangeListener(this);

        /**
         * Sets a listener to receive notifications of changes by clik Key.
         * */
        vertical_seek.setOnKeyListener(this);
        horizontal_seek.setOnKeyListener(this);
        width_seek.setOnKeyListener(this);
        height_seek.setOnKeyListener(this);

        /**
         * Get current system display range and set SeekBar's progress and text
         * Store original range value.
         * */
        Rect rect = display_manager.getOutRange();
        originalRange.left = rect.left;
        originalRange.top = rect.top;
        originalRange.width = rect.right;
        originalRange.height = rect.bottom;
        mRange.left = rect.left;
        mRange.top = rect.top;
        mRange.width = rect.right;
        mRange.height = rect.bottom;

        Percent percent = rangeToPercent(mRange);

        vertical_seek.setProgress(percent.leftPercent);
        horizontal_seek.setProgress(percent.topPercent);
        width_seek.setProgress(percent.widthPercent);
        height_seek.setProgress(percent.heightPercent);

        vertical_text.setText(""+percent.leftPercent);
        horizontal_text.setText(""+percent.topPercent);
        width_text.setText(""+percent.widthPercent);
        height_text.setText(""+percent.heightPercent);
    }

    /**
     * getMaxRangeByFmt return standard left,top,right,bottom by format "fmt".
     * */
    public Rectangle getMaxRangeByFmt(int fmt)
    {
        Rectangle rectangle = new Rectangle();
        try
        {
            switch (fmt)
            {
                case ENC_FMT_1080P_60:
                case ENC_FMT_1080P_50:
                case ENC_FMT_1080i_60:
                case ENC_FMT_1080i_50:
                    rectangle.left = LEFT_1080P;
                    rectangle.top = TOP_1080P;
                    rectangle.width = WIDTH_1080P;
                    rectangle.height = HEIGHT_1080P;
                    break;
                case ENC_FMT_720P_60:
                case ENC_FMT_720P_50:
                    rectangle.left = LEFT_720P;
                    rectangle.top = TOP_720P;
                    rectangle.width = WIDTH_720P;
                    rectangle.height = HEIGHT_720P;
                    break;
                case ENC_FMT_576P_50:
                    rectangle.left = LEFT_576P;
                    rectangle.top = TOP_576P;
                    rectangle.width = WIDTH_576P;
                    rectangle.height = HEIGHT_576P;
                    break;
                case ENC_FMT_480P_60:
                    rectangle.left = LEFT_480P;
                    rectangle.top = TOP_480P;
                    rectangle.width = WIDTH_480P;
                    rectangle.height = HEIGHT_480P;
                    break;
                case ENC_FMT_PAL:
                    rectangle.left = LEFT_PAL;
                    rectangle.top = TOP_PAL;
                    rectangle.width = WIDTH_PAL;
                    rectangle.height = HEIGHT_PAL;
                    break;
                case ENC_FMT_NTSC:
                    rectangle.left = LEFT_NTSC;
                    rectangle.top = TOP_NTSC;
                    rectangle.width = WIDTH_NTSC;
                    rectangle.height = HEIGHT_NTSC;
                    break;
                default:
                    rectangle.left = LEFT_1080P;
                    rectangle.top = TOP_1080P;
                    rectangle.width = WIDTH_1080P;
                    rectangle.height = HEIGHT_1080P;
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rectangle;
    }

    /**
     * rangeToPercent convert curent display range to percent.
     * */
    public Percent rangeToPercent(Rectangle range)
    {
        Percent percent = new Percent();

        percent.leftPercent = range.left;
        percent.topPercent = range.top;
        percent.widthPercent = range.width;
        percent.heightPercent = range.height;
        //Log.d(TAG, "rangeToPercent, range.left = "+range.left +" range.top = "+range.top +" range.width = "+range.width+" range.height = "+range.height);
        //Log.d(TAG, "rangeToPercent, percent.leftPercent = "+percent.leftPercent +" percent.topPercent = "+percent.topPercent +" percent.widthPercent = "+percent.widthPercent+" percent.heightPercent = "+percent.heightPercent);
        return percent;
    }

    /**
     * percentToRange convert SeekBar progress to display range
     * process single SeekBar each turn.
     * */
    public int percentToRange(SeekBar seekBar)
    {
        int fmt = display_manager.getFmt();
        Rectangle maxRange = getMaxRangeByFmt(fmt);
        int range = 0;
        //Log.d(TAG, "percentToRange, seekBar.getId() = "+seekBar.getId() +" seekBar.getProgress() = "+seekBar.getProgress());

        switch (seekBar.getId())
        {
            case R.id.left_seek:
                range = seekBar.getProgress();
                break;
            case R.id.top_seek:
                range = seekBar.getProgress();
                break;
            case R.id.width_seek:
                range = seekBar.getProgress();
                break;
            case R.id.height_seek:
                range = seekBar.getProgress();
                break;
            default:
                break;
        }

        Log.d(TAG, "percentToRange, range = "+range);
        return range;
    }

    /**
     * setSeekBar process SeekBar Object,do setOutrange and setText with SeekBar progress.
     * */
    private void setSeekBar(SeekBar seekBar)
    {
        Percent rangePercent = new Percent();
        Rectangle currentRange = new Rectangle();
        Rectangle maxRange = new Rectangle();
        Rect rect = new Rect();
        maxRange = getMaxRangeByFmt(display_manager.getFmt());

        switch (seekBar.getId())
        {
            /**
             * This case is to process vertical SeekBar
             * Other SeekBar is the same way.
             * */
            case R.id.left_seek:
                mRange.left = percentToRange(seekBar);
                rect = display_manager.getOutRange();
                //control vertical range reasonable.
                /*if(mRange.left + rect.right > maxRange.width)
                {
                    mRange.left = maxRange.width - rect.right;
                }*/
                display_manager.setOutRange(mRange.left,mRange.top,mRange.width,mRange.height);
                rangePercent = rangeToPercent(mRange);
                seekBar.setProgress(rangePercent.leftPercent);
                vertical_text.setText("" + rangePercent.leftPercent);
                //get real range and setText with real percent
                /*rect = display_manager.getOutRange();
                currentRange.left = rect.left;
                currentRange.top = rect.top;
                currentRange.width = rect.right;
                currentRange.height = rect.bottom;
                //if current range is not what we set,modify.
                if(!currentRange.equals(mRange))
                {
                    rangePercent = rangeToPercent(currentRange);
                    seekBar.setProgress(rangePercent.leftPercent);
                    vertical_text.setText("" + rangePercent.leftPercent);
                    mRange.left = currentRange.left;
                }*/
                break;

            case R.id.top_seek:
                mRange.top = percentToRange(seekBar);
                rect = display_manager.getOutRange();
                /*if(mRange.top + rect.bottom > maxRange.height)
                {
                    mRange.top = maxRange.height - rect.bottom;
                }*/
                display_manager.setOutRange(mRange.left,mRange.top,mRange.width,mRange.height);
                rangePercent = rangeToPercent(mRange);
                seekBar.setProgress(rangePercent.topPercent);
                horizontal_text.setText("" + rangePercent.topPercent);
                /*rect = display_manager.getOutRange();
                currentRange.left = rect.left;
                currentRange.top = rect.top;
                currentRange.width = rect.right;
                currentRange.height = rect.bottom;
                if(!currentRange.equals(mRange))
                {
                    rangePercent = rangeToPercent(currentRange);
                    seekBar.setProgress(rangePercent.topPercent);
                    horizontal_text.setText("" + rangePercent.topPercent);
                    mRange.top = currentRange.top;
                }*/
                break;

            case R.id.width_seek:
                mRange.width = percentToRange(seekBar);
                rect = display_manager.getOutRange();
                //control width range reasonable.
                /*if(rect.left + mRange.width > maxRange.width)
                {
                    mRange.width = maxRange.width - rect.left;
                }*/
                display_manager.setOutRange(mRange.left,mRange.top,mRange.width,mRange.height);
                rangePercent = rangeToPercent(mRange);
                seekBar.setProgress(rangePercent.widthPercent);
                width_text.setText("" + rangePercent.widthPercent);
                /*rect = display_manager.getOutRange();
                currentRange.left = rect.left;
                currentRange.top = rect.top;
                currentRange.width = rect.right;
                currentRange.height = rect.bottom;
                if(!currentRange.equals(mRange))
                {
                    rangePercent = rangeToPercent(currentRange);
                    seekBar.setProgress(rangePercent.widthPercent);
                    width_text.setText("" + rangePercent.widthPercent);
                    mRange.width = currentRange.width;
                }*/
                break;

            case R.id.height_seek:
                mRange.height = percentToRange(seekBar);
                rect = display_manager.getOutRange();
                // control height range reasonable.
                /*if(rect.top + mRange.height > maxRange.height)
                {
                    mRange.height = maxRange.height - rect.top;
                }*/
                display_manager.setOutRange(mRange.left,mRange.top,mRange.width,mRange.height);
                rangePercent = rangeToPercent(mRange);
                seekBar.setProgress(rangePercent.heightPercent);
                height_text.setText("" + rangePercent.heightPercent);
                /*rect = display_manager.getOutRange();
                currentRange.left = rect.left;
                currentRange.top = rect.top;
                currentRange.width = rect.right;
                currentRange.height = rect.bottom;
                if(!currentRange.equals(mRange))
                {
                    rangePercent = rangeToPercent(currentRange);
                    seekBar.setProgress(rangePercent.heightPercent);
                    height_text.setText("" + rangePercent.heightPercent);
                    mRange.height = currentRange.height;
                }*/
                break;
            default:
                break;
        }
    }

    /**
     * Answer to pulling SeekBar
     * */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if(fromUser)
        {
            setSeekBar(seekBar);
        }
    }

    /**
     * Restore original range value.
     * */
    protected void onDialogClosed(boolean positiveResult)
    {
        if(!positiveResult)
        {
            display_manager.setOutRange(originalRange.left,originalRange.top,originalRange.width,originalRange.height);
        } else {
            display_manager.SaveParam();
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        