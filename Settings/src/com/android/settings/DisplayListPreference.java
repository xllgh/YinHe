package com.android.settings;

import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import com.android.settings.R;
import android.util.Log;
import android.os.SystemProperties;
import com.hisilicon.android.HiDisplayManager;

public class DisplayListPreference extends ListPreference{
    private String TAG = "DisplayListPreference";
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private int mClickedDialogEntryIndex;
    private HiDisplayManager display_manager;
    public DisplayListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        //mEntries = getEntries();
        //mEntryValues = getEntryValues();
        display_manager = new HiDisplayManager();
        if(display_manager.getDisplayDeviceType() <= 1) //tv case: 0 is av, 1 is hdmi
        {
            mEntries = context.getResources()
                        .getStringArray(R.array.set_video_tv_list);
            mEntryValues = context.getResources()
                        .getStringArray(R.array.set_video_tv_list_value);
            String mProduc_name = SystemProperties.get("ro.product.device");
            if(!mProduc_name.substring(0, 5).equals("Hi379"))
            {
                String[] tem_enum = new String[8];
                String[] tem_valuse = new String[8];
                int length = mEntryValues.length;
                for (int i = 2; i < length; i++)
                {
                    tem_enum[i-2] = mEntries[i].toString();
                    tem_valuse[i-2] = mEntryValues[i].toString();
                }
                mEntries = tem_enum;
                mEntryValues = tem_valuse;
            }
            setEntries(mEntries);
            setEntryValues(mEntryValues);
        }
        else
        {
            mEntries = context.getResources()
                        .getStringArray(R.array.set_video_pc_list);
            mEntryValues = context.getResources()
                        .getStringArray(R.array.set_video_pc_list_value);
            setEntries(mEntries);
            setEntryValues(mEntryValues);
        }
    }

    public DisplayListPreference(Context context) {
        this(context, null);
    }
    private int getValueIndex() {
        return findIndexOfValue(String.valueOf(display_manager.getFmt()));
    }
    @Override
    protected void onPrepareDialogBuilder(final Builder builder) {
        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }
        mClickedDialogEntryIndex = getValueIndex();
        builder.setSingleChoiceItems(mEntries, mClickedDialogEntryIndex,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mClickedDialogEntryIndex = which;

                        /*
                         * Clicking on an item simulates the positive button
                         * click, and dismisses the dialog.
                         */
                        DisplayListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    }
        });
        /*
         * The typical interaction for list-based dialogs is to have
         * click-on-an-item dismiss the dialog instead of the user having to
         * press 'Ok'.
         */
        builder.setPositiveButton(null, null);
    }
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }
}
