package com.android.settings.video;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.settings.R;

import com.hisilicon.android.HiDisplayManager;
import com.hisilicon.android.DispFmt;

public class ScreenPreference extends DialogPreference implements OnSeekBarChangeListener,OnKeyListener
{
    private static final String TAG = "ScreenPreference";
    private SeekBar bright_seek,hue_seek,contrast_seek,saturation_seek;
    private TextView bright_text,hue_text,contrast_text,saturation_text;
    private HiDisplayManager display_manager = null;

    private int init_brightness = 50;
    private int init_hue = 50;
    private int init_contrast = 50;
    private int init_saturation = 50;

    public ScreenPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDialogLayoutResource(R.xml.preference_dialog_screen);
        if(display_manager == null)
        {
            display_manager = new HiDisplayManager();
        }
    }

    protected void onBindDialogView(View view)
    {
        super.onBindDialogView(view);

        /**
         * Find TextView in layout XML.
         * */
        bright_text = (TextView)view.findViewById(R.id.brightness_text);
        hue_text = (TextView)view.findViewById(R.id.hue_text);
        contrast_text = (TextView)view.findViewById(R.id.contrast_text);
        saturation_text = (TextView)view.findViewById(R.id.saturation_text);

        /**
         * Find SeekBar in layout XML.
         * */
        bright_seek = (SeekBar)view.findViewById(R.id.brightness_seek);
        hue_seek = (SeekBar)view.findViewById(R.id.hue_seek);
        contrast_seek = (SeekBar)view.findViewById(R.id.contrast_seek);
        saturation_seek= (SeekBar)view.findViewById(R.id.saturation_seek);

        /**
         * Sets a listener to receive notifications of changes by pull SeekBar.
         * */
        bright_seek.setOnSeekBarChangeListener(this);
        hue_seek.setOnSeekBarChangeListener(this);
        contrast_seek.setOnSeekBarChangeListener(this);
        saturation_seek.setOnSeekBarChangeListener(this);

        /**
         * Sets a listener to receive notifications of changes by clik Key.
         * */
        bright_seek.setOnKeyListener(this);
        hue_seek.setOnKeyListener(this);
        contrast_seek.setOnKeyListener(this);
        saturation_seek.setOnKeyListener(this);

        /**
         * Set initial SeekBar progress and text of bright,hue,contrast,saturation.
         * */
        init_brightness = display_manager.getBrightness();
        if(init_brightness < 0 || init_brightness > 100)
        {
            init_brightness = 50;
        }

        init_hue = display_manager.getHue();
        if(init_hue < 0 || init_hue > 100)
        {
            init_hue= 50;
        }

        init_contrast = display_manager.getContrast();
        if(init_contrast < 0 || init_contrast > 100)
        {
            init_contrast = 50;
        }

        init_saturation = display_manager.getSaturation();
        if(init_saturation < 0 || init_saturation > 100)
        {
            init_saturation = 50;
        }

        bright_seek.setProgress(init_brightness);
        bright_text.setText(""+init_brightness);

        hue_seek.setProgress(init_hue);
        hue_text.setText(""+init_hue);

        contrast_seek.setProgress(init_contrast);
        contrast_text.setText(""+init_contrast);

        saturation_seek.setProgress(init_saturation);
        saturation_text.setText(""+init_saturation);
    }

    /**
     * Answer to pulling SeekBar
     * */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2)
    {
        if (progress > 100)
        {
            progress = 100;
        }
        setSeekBar(seekBar);
    }

    /**
     * Set the value and text when seekbar changed.
     * */
    private void setSeekBar(SeekBar seekBar)
    {
        try
        {
            switch (seekBar.getId())
            {
                case R.id.brightness_seek:
                    display_manager.setBrightness(seekBar.getProgress());
                    bright_text.setText(seekBar.getProgress() + "");
                    break;
                case R.id.hue_seek:
                    display_manager.setHue(seekBar.getProgress());
                    hue_text.setText(seekBar.getProgress() + "");
                    break;
                case R.id.contrast_seek:
                    display_manager.setContrast(seekBar.getProgress());
                    contrast_text.setText(seekBar.getProgress() + "");
                    break;
                case R.id.saturation_seek:
                    display_manager.setSaturation(seekBar.getProgress());
                    saturation_text.setText(seekBar.getProgress() + "");
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Set value of bright,hue,contrast,saturation.
     * */
    protected void onDialogClosed(boolean positiveResult)
    {
        if(!positiveResult)
        {
            display_manager.setBrightness(init_brightness);
            display_manager.setHue(init_hue);
            display_manager.setContrast(init_contrast);
            display_manager.setSaturation(init_saturation);
        }else{
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
