package com.android.settings.video;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.graphics.Rect;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.hisilicon.android.HiDisplayManager;
import com.android.settings.R;

public class ScopeRangePreference extends Preference {
    private int screenWidth;
    private int screenHeight;
    private int frameMaxWidth;
    private int frameMinWidth;
    private int frameMaxHeight;
    private int frameMinHeight;
    private static int top_margin = 0;
    private static int left_margin = 0;
    private static int right_margin = 0;
    private static int bottom_margin = 0;
    private boolean left_top_focus = true;
    private Dialog mDialog;
    private static final String TAG = "ScopeRangePreference";
    private static final int SCALE = 2;// actual and virtual scaling
    private static final int RANGE_STEP = 2;// adjustment of step length
    private static final float MIN_SCREEN_SCALE = 0.8f;
    private Rectangle mRange;
    private Rectangle originalRange;
    private HiDisplayManager display_manager = null;
    private static final boolean DEBUG = false;

    public ScopeRangePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (display_manager == null) {
            display_manager = new HiDisplayManager();
        }
        mRange = new Rectangle();
        originalRange = new Rectangle();
    }

    @Override
    protected void onBindView(View arg0) {
        // TODO Auto-generated method stub
        super.onBindView(arg0);
        WindowManager wManager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        frameMaxWidth = screenWidth / SCALE;
        frameMinWidth = (int) (screenWidth * MIN_SCREEN_SCALE) / SCALE;
        frameMaxHeight = screenHeight / SCALE;
        frameMinHeight = (int) (screenHeight * MIN_SCREEN_SCALE) / SCALE;
        if(DEBUG)
            Log.d(TAG, "  frameMaxWidth =" + frameMaxWidth + " frameMinWidth="
                    + frameMinWidth + " frameMaxHeight=" + frameMaxHeight
                    + " frameMinHeight=" + frameMinHeight);
    }

    public Percent rangeToPercent(Rectangle range) {
        Percent percent = new Percent();
        percent.leftPercent = range.left / SCALE;
        percent.topPercent = range.top / SCALE;
        percent.widthPercent = range.width / SCALE;
        percent.heightPercent = range.height / SCALE;
        return percent;
    }

    @Override
    protected void onClick() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        showDialog();
    }


    public void showDialog() {
        left_top_focus = true;

        Rect rect = display_manager.getOutRange();
        originalRange.left = rect.left;
        originalRange.top = rect.top;
        originalRange.width = rect.right;
        originalRange.height = rect.bottom;
        mRange.left = rect.left;
        mRange.top = rect.top;
        mRange.width = rect.right;
        mRange.height = rect.bottom;
        if (DEBUG) {
            Log.d(TAG, "originalRange.left =" + originalRange.left
                    + ", originalRange.top=" + originalRange.top
                    + ", originalRange.width=" + originalRange.width
                    + ", originalRange.height=" + originalRange.height);
        }
        Percent percent = rangeToPercent(mRange);
        left_margin = percent.leftPercent;
        top_margin = percent.topPercent;
        right_margin = percent.widthPercent;
        bottom_margin = percent.heightPercent;
        if (DEBUG) {
            Log.d(TAG, "percent.leftPercent =" + percent.leftPercent
                    + ", percent.topPercent=" + percent.topPercent
                    + ", percent.widthPercent=" + percent.widthPercent
                    + ", percent.heightPercent=" + percent.heightPercent);
        }

        Context context = getContext();
        final Dialog dialog = new Dialog(context, R.style.MyDialog);
        if (isPortrait()) {
            dialog.setContentView(R.layout.dialog_screen_portrait);
        } else {
            dialog.setContentView(R.layout.dialog_screen);
        }
        final FrameLayout frameLayout = (FrameLayout) dialog
                .findViewById(R.id.frame_image);
        final Button button_top_left = (Button) dialog
                .findViewById(R.id.top_left);
        final Button button_right_button = (Button) dialog
                .findViewById(R.id.right_button);
        final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout
                .getLayoutParams();
        button_top_left.setBackgroundResource(R.drawable.ic_btn_selected);
        button_right_button.setBackgroundResource(R.drawable.ic_btn_default);

        layoutParams.leftMargin = left_margin;
        layoutParams.rightMargin = right_margin;
        layoutParams.topMargin = top_margin;
        layoutParams.bottomMargin = bottom_margin;
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                 if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                top_margin = layoutParams.topMargin;
                left_margin = layoutParams.leftMargin;
                right_margin = layoutParams.rightMargin;
                bottom_margin = layoutParams.bottomMargin;
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (left_top_focus) {
                        if (left_margin > 0) {
                            left_margin = left_margin - RANGE_STEP;
                            if (left_margin < 0) {
                                left_margin = 0;
                            }
                            layoutParams.leftMargin = left_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    } else {
                        if (right_margin < 100) {
                            right_margin = right_margin + RANGE_STEP;
                            if (right_margin > 100) {
                                right_margin = 100;
                            }
                            layoutParams.rightMargin = right_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (left_top_focus) {
                        if (left_margin < 100) {
                            left_margin = left_margin + RANGE_STEP;
                            if (left_margin > 100) {
                                left_margin = 100;
                            }
                            layoutParams.leftMargin = left_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    } else {
                        if (right_margin > 0) {
                            right_margin = right_margin - RANGE_STEP;
                            if (right_margin < 0) {
                                right_margin = 0;
                            }
                            layoutParams.rightMargin = right_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (left_top_focus) {
                        if (top_margin > 0) {
                            top_margin = top_margin - RANGE_STEP;
                            if (top_margin < 0) {
                                top_margin = 0;
                            }
                            layoutParams.topMargin = top_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    } else {
                        if (bottom_margin < 100) {
                            bottom_margin = bottom_margin + RANGE_STEP;
                            if (bottom_margin > 100) {
                                bottom_margin = 100;
                            }
                            layoutParams.bottomMargin = bottom_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (left_top_focus) {
                        if (top_margin < 100) {
                            top_margin = top_margin + RANGE_STEP;
                            if (top_margin > 100) {
                                top_margin = 100;
                            }
                            layoutParams.topMargin = top_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    } else {
                        if (bottom_margin > 0) {
                            bottom_margin = bottom_margin - RANGE_STEP;
                            if (bottom_margin < 0) {
                                bottom_margin = 0;
                            }
                            layoutParams.bottomMargin = bottom_margin;
                            frameLayout.setLayoutParams(layoutParams);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (left_top_focus) {
                        left_top_focus = false;
                        button_right_button
                                .setBackgroundResource(R.drawable.ic_btn_selected);
                        button_top_left
                                .setBackgroundResource(R.drawable.ic_btn_default);
                    } else {
                        left_top_focus = true;
                        button_right_button
                                .setBackgroundResource(R.drawable.ic_btn_default);
                        button_top_left
                                .setBackgroundResource(R.drawable.ic_btn_selected);
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    display_manager.SaveParam();
                    dialog.dismiss();
                    break;
                default:
                    break;
                }

                Rect rect = display_manager.getOutRange();
                mRange.left = left_margin * SCALE;
                mRange.top = top_margin * SCALE;
                mRange.width = right_margin * SCALE;
                mRange.height = bottom_margin * SCALE;
                display_manager.setOutRange(mRange.left, mRange.top,
                        mRange.width, mRange.height);
                return false;
            }
        });
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = frameMaxWidth;
        params.height = frameMaxHeight;
        dialog.getWindow().setAttributes(params);
        dialog.show();

    }

    private boolean isPortrait() {
        String screenOrientation = SystemProperties.get("persist.sys.screenorientation");
        if (screenOrientation != null) {
            if ("portrait".equals(screenOrientation)) {
                return true;
            }
        }
        return false;
    }

}
