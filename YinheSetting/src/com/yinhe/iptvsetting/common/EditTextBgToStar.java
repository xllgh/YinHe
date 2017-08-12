package com.yinhe.iptvsetting.common;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * 修改EditText输入框密码显示样式。 <br>
 * '······' To '******'
 * 
 * @author zhbn
 * 
 */
public class EditTextBgToStar extends PasswordTransformationMethod {

	@Override
	public CharSequence getTransformation(CharSequence source, View view) {
		return new PasswordCharSequence(source);
	}

	private class PasswordCharSequence implements CharSequence {

		private CharSequence mSource;

		public PasswordCharSequence(CharSequence source) {
			mSource = source;
		}

		@Override
		public int length() {
			return mSource.length();
		}

		@Override
		public char charAt(int index) {
			return '*';
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return mSource.subSequence(start, end);
		}
	}
}
