package com.yinhe.iptvsetting.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.hisilicon.android.HiDisplayManager;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.SoundAndDisplayFragmentActivity;
import com.yinhe.iptvsetting.adapter.BoundaryAdapter;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.model.Percent;
import com.yinhe.model.Rectangle;

/**
 * 显示及声音->画面边界。
 * 
 * @author zhbn
 *
 */
public class BoundaryFragment extends BaseFragment implements OnKeyListener {

	private static final String TAG = "BoundaryFragment";
	
	/*四种模式：上、下、左、右边界*/
	private static final int MODE_LEFT = 0;
	private static final int MODE_RIGHT = 1;
	private static final int MODE_TOP = 2;
	private static final int MODE_BOTTOM = 3;
	
	private HiDisplayManager display_manager = null;
    /**
     * A Rectangle for dispose.
     * */
    private Rectangle mRange;
    private Rectangle originalRange;
    
    private static final int SCALE = 2;// actual and virtual scaling
    private static final int RANGE_STEP = 2;// adjustment of step length
    
    private static int top_margin = 0;
    private static int left_margin = 0;
    private static int right_margin = 0;
    private static int bottom_margin = 0;
    
    /*内容区Adapter*/
    private BoundaryAdapter mAdapter = null;

    /*当前选中调整的模式*/
	private int mCurrentBoundaryMode = -1;;
    
	public BoundaryFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.ds_boundary, container, false);
		
		if(display_manager == null) 
        {
            display_manager = new HiDisplayManager();
        }
        mRange = new Rectangle();
        originalRange = new Rectangle();
        
		initData(view);
		
		return view;
	}
	
	private void initData(View view) {
		Rect rect = display_manager.getOutRange();
		originalRange.left = rect.left;
		originalRange.top = rect.top;
		originalRange.right = rect.right;
		originalRange.bottom = rect.bottom;
		mRange.left = rect.left;
		mRange.top = rect.top;
		mRange.right = rect.right;
		mRange.bottom = rect.bottom;
		
		Percent percent = rangeToPercent(mRange);
		left_margin = percent.leftPercent;
		top_margin = percent.topPercent;
		right_margin = percent.rightPercent;
		bottom_margin = percent.bottomPercent;
		
		int arrs[] = { percent.leftPercent, percent.rightPercent,
				percent.topPercent, percent.bottomPercent };
		
		mAdapter = new BoundaryAdapter(getActivity().getApplicationContext(), arrs);
		
		final ListView listView = (ListView) view.findViewById(R.id.lv_boundary);
		listView.setAdapter(mAdapter);
		listView.setOnKeyListener(this);
		listView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				listView.setSelector(hasFocus ? R.drawable.list_selector
						: R.drawable.list_null);
			}
		});
		listView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentBoundaryMode = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mCurrentBoundaryMode = -1;
			}
		});
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			return false;
		}
		
		switch (mCurrentBoundaryMode) {
		case MODE_LEFT:
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (left_margin < 100) {
					left_margin += RANGE_STEP;
					save(left_margin);
				}
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (left_margin > 0) {
					left_margin -= RANGE_STEP;
					save(left_margin);
				}
			}
			break;
		case MODE_RIGHT:
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (right_margin < 100) {
					right_margin += RANGE_STEP;
					save(right_margin);
				}
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (right_margin > 0) {
					right_margin -= RANGE_STEP;
					save(right_margin);
				}
			}
			break;
		case MODE_TOP:
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (top_margin < 100) {
					top_margin += RANGE_STEP;
					save(top_margin);
				}
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (top_margin > 0) {
					top_margin -= RANGE_STEP;
					save(top_margin);
				}
			}
			break;
		case MODE_BOTTOM:
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (bottom_margin < 100) {
					bottom_margin += RANGE_STEP;
					save(bottom_margin);
				}
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (bottom_margin > 0) {
					bottom_margin -= RANGE_STEP;
					save(bottom_margin);
				}
			}
			break;
		}
		return false;
	}

	public Percent rangeToPercent(Rectangle range) {
		Percent percent = new Percent();
		percent.leftPercent = range.left / SCALE;
		percent.topPercent = range.top / SCALE;
		percent.rightPercent = range.right / SCALE;
		percent.bottomPercent = range.bottom / SCALE;
		return percent;
	}
	
	/**
	 * UI、设备反映。
	 * @param value
	 */
	private void save(int value) {
		// UI更新
		mAdapter.setData(mCurrentBoundaryMode, value);
		mAdapter.notifyDataSetChanged();
		
		// 设备反映
		mRange.left = left_margin * SCALE;
		mRange.top = top_margin * SCALE;
		mRange.right = right_margin * SCALE;
		mRange.bottom = bottom_margin * SCALE;
		display_manager.setOutRange(mRange.left, mRange.top, mRange.right,
				mRange.bottom);
		display_manager.SaveParam();
	}
	
	/**
	 * 输出日志。
	 * @param msg
	 */
	private void logD(String msg) {
		Log.d(TAG, msg);
	}

}
