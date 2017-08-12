package com.android.settings.g3;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.settings.R;

public class TextPrefreence extends Preference {

    private TextView textView ;
    private String text;
    public TextPrefreence(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setLayoutResource(R.layout.text_preference);
    }

    @Override
    protected void onBindView(View view) {
        // TODO Auto-generated method stub
        super.onBindView(view);
        textView = (TextView) view.findViewById(R.id.third_gen_textView);
        textView.setText(text);
        textView.setSingleLine(true);

    }

    public void setText(String text){
        this.text = text;
        notifyChanged();
    }

    public String getText() {
        return text;
    }

}
