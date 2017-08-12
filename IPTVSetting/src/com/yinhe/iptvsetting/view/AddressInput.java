package com.yinhe.iptvsetting.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LogUtil;

/**
 * 网络地址输入控件，含四个输入框。 满三位自动跳转至下个输入框。
 * 
 * @author zhbn
 * 
 */
public class AddressInput extends LinearLayout {

	private LogUtil mLogUtil = new LogUtil("AddressInput");

	private Context mContext = null;

	/* 第一位地址输入框 */
	private EditText mEditText01 = null;
	/* 第二位地址输入框 */
	private EditText mEditText02 = null;
	/* 第三位地址输入框 */
	private EditText mEditText03 = null;
	/* 第四位地址输入框 */
	private EditText mEditText04 = null;

	/* 输入结束监听器 */
	private OnInputCompletedListener mOnInputCompletedListener = null;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public AddressInput(Context context) {
		super(context);
		mContext = context;

		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(R.layout.address_input, null);

		this.addView(view);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */
	public AddressInput(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(R.layout.address_input, null);
		mEditText01 = initEditText(view, R.id.address_01);
		mEditText02 = initEditText(view, R.id.address_02);
		mEditText03 = initEditText(view, R.id.address_03);
		mEditText04 = initEditText(view, R.id.address_04);
		this.addView(view);
	}

	/**
	 * 初始化EditText。
	 * 
	 * @param view
	 * @param editTextId
	 * @return
	 */
	private EditText initEditText(View view, final int editTextId) {
		EditText editText = (EditText) view.findViewById(editTextId);
		// 添加文字变化监听器
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(FuncUtil.STR_DOT)) {
					changeFocus(editTextId, true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.contains(FuncUtil.STR_DOT)) {
					int index = str.indexOf(FuncUtil.STR_DOT);
					s.delete(index, ++index);
					return;
				}

				// 合法性check:0-255
				if (!FuncUtil.isNullOrEmpty(str)
						&& Integer.valueOf(str) > 255) {
					s.clear();

					Toast toast = Toast.makeText(
							mContext,
							str
									+ " "
									+ getResources().getString(
											R.string.net_address_illegal),
							Toast.LENGTH_SHORT);
					toast.show();

					return;
				}

				// 字数满三位时，自动跳转至下一个控件。
				if (s.length() == 3) {
					changeFocus(editTextId, true);
				}
			}
		});

		// 添加按键事件，监听【删除】键按下
		// 用于清空某一EditText时，再按【删除】键，光标移动至前一EditText。
		editText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() != KeyEvent.ACTION_DOWN
						|| keyCode != KeyEvent.KEYCODE_DEL) {
					return false;
				}

				EditText et = (EditText) v;
				if (et.length() == 0) {
					changeFocus(editTextId, false);
				}
				return false;
			}
		});

//		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if (hasFocus) {
//					EditText et = (EditText) v;
//					et.setSelection(et.getText().length());
//				}
//			}
//		});
		return editText;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		// 控制光标移动方向：
		// 光标在第一控件的首位时，按左键无效；
		// 光标在第四控件的末位时，按右键无效；
		boolean rtn = super.dispatchKeyEvent(event);
		if (event.getAction() == KeyEvent.ACTION_UP) {
			return rtn;
		}

		if (KeyEvent.KEYCODE_DPAD_LEFT != keyCode
				&& KeyEvent.KEYCODE_DPAD_RIGHT != keyCode) {
			return rtn;
		}
		if (this.mEditText01.hasFocus()
				&& KeyEvent.KEYCODE_DPAD_LEFT == keyCode
				&& this.mEditText01.getSelectionStart() == 0) {
			return true;
		}

		if (this.mEditText04.hasFocus()
				&& KeyEvent.KEYCODE_DPAD_RIGHT == keyCode
				&& this.mEditText04.getSelectionStart() == this.mEditText04
						.length()) {
			return true;
		}

		return rtn;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			return false;
		}

		if (KeyEvent.KEYCODE_DPAD_LEFT != keyCode
				&& KeyEvent.KEYCODE_DPAD_RIGHT != keyCode) {
			return false;
		}
		if (this.mEditText01.hasFocus()
				&& KeyEvent.KEYCODE_DPAD_LEFT == keyCode
				&& this.mEditText01.getSelectionStart() == 0) {
			return true;
		}

		if (this.mEditText04.hasFocus()
				&& KeyEvent.KEYCODE_DPAD_RIGHT == keyCode
				&& this.mEditText04.getSelectionStart() == this.mEditText04
						.length()) {
			return true;
		}

		return false;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
	    mLogUtil.d("onFocusChanged");
		//super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus) {
			this.editTextRequestFocus();
		}
	}

	/**
	 * 设置末位输入框输入结束事件。
	 * 
	 * @param completedListener
	 */
	public void setOnInputCompletedListener(
			OnInputCompletedListener completedListener) {
		mOnInputCompletedListener = completedListener;
	}

	/**
	 * 改变光标。
	 * 
	 * @param editTextId
	 *            输入框控件ID
	 * @param isNext
	 *            光标移动方向（true：向右；false：向左）
	 */
	private void changeFocus(int editTextId, boolean isNext) {
		switch (editTextId) {
		case R.id.address_01:
			if (isNext) {
				mEditText02.requestFocus();
			}
			break;
		case R.id.address_02:
			if (isNext) {
				mEditText03.requestFocus();
			} else {
				mEditText01.requestFocus();
			}
			break;
		case R.id.address_03:
			if (isNext) {
				mEditText04.requestFocus();
			} else {
				mEditText02.requestFocus();
			}
			break;
		case R.id.address_04:
			if (isNext) {
				if (mOnInputCompletedListener != null) {
					mOnInputCompletedListener.onInputCompleted();
				}
			} else {
				mEditText03.requestFocus();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 获取光标。
	 * 
	 * @return
	 */
	public boolean editTextRequestFocus() {
		return mEditText01.requestFocus();
	}

	/**
	 * 获取输入框中的地址。
	 * 
	 * @return String ***.***.***.***
	 */
	public String getAddress() {
		String s01 = mEditText01.getText().toString().trim();
		String s02 = mEditText02.getText().toString().trim();
		String s03 = mEditText03.getText().toString().trim();
		String s04 = mEditText04.getText().toString().trim();

		if (FuncUtil.isNullOrEmpty(s01) || FuncUtil.isNullOrEmpty(s02)
				|| FuncUtil.isNullOrEmpty(s03)
				|| FuncUtil.isNullOrEmpty(s04)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(s01);
		sb.append(FuncUtil.STR_DOT);
		sb.append(s02);
		sb.append(FuncUtil.STR_DOT);
		sb.append(s03);
		sb.append(FuncUtil.STR_DOT);
		sb.append(s04);
		return sb.toString();
	}

	/**
	 * 设置输入框中的地址。
	 * 
	 * @param String
	 *            ***.***.***.***
	 */
	public void setAddress(String address) {
	    mLogUtil.d("setAddress : address = " + address);
		if (FuncUtil.isNullOrEmpty(address)) {
			mEditText01.setText(FuncUtil.STR_EMPTY);
			mEditText02.setText(FuncUtil.STR_EMPTY);
			mEditText03.setText(FuncUtil.STR_EMPTY);
			mEditText04.setText(FuncUtil.STR_EMPTY);
			return;
		}

		String[] strs = address.split("\\.");
		if (strs.length != 4) {
		    mLogUtil.e("setAddress : address = " + address);
			return;
		}

		mEditText01.setText(strs[0]);
		mEditText02.setText(strs[1]);
		mEditText03.setText(strs[2]);
		mEditText04.setText(strs[3]);
	}

	/**
	 * 末位输入框输入结束事件。
	 * 
	 * @author zhbn
	 * 
	 */
	interface OnInputCompletedListener {
		void onInputCompleted();
	}
}
