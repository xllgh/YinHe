
package com.yinhe.iptvsetting;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.adapter.FragmentTabAdapter;
import com.yinhe.iptvsetting.fragment.BoundaryFragment;
import com.yinhe.iptvsetting.fragment.FontFragment;
import com.yinhe.iptvsetting.fragment.HDMIFragment;
import com.yinhe.iptvsetting.fragment.ScreenDisplayFragment;
import com.yinhe.iptvsetting.fragment.VideoFragment;
import com.yinhe.iptvsetting.view.BaseFragment;

/**
 * 声音及显示。
 * 
 * @author zhbn
 */
public class SoundAndDisplayFragmentActivity extends FragmentActivity implements
        OnFocusChangeListener {

    public static final int RESULT_VALUE = 1001;
    public static final String SHOW_FONT = "SHOW_FONT";

    private TextView tv_video, tv_boundary, tv_screendisplay, tv_hont, tv_hdmi;
    // public static TextView tv_hont = null;
    private List<TextView> tv_title;

    private List<BaseFragment> fragments = null;
    private FragmentTabAdapter fragmentTabAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.voiceanddisplay);
        initData();
    }

    private void initData() {
        tv_title = new ArrayList<TextView>();
        tv_video = (TextView) super.findViewById(R.id.title_video);
        tv_video.setOnFocusChangeListener(this);
        tv_title.add(tv_video);
        tv_boundary = (TextView) super.findViewById(R.id.title_boundary);
        tv_boundary.setOnFocusChangeListener(this);
        tv_title.add(tv_boundary);
        tv_screendisplay = (TextView) super
                .findViewById(R.id.title_screendisplay);
        tv_screendisplay.setOnFocusChangeListener(this);
        tv_title.add(tv_screendisplay);
        tv_hont = (TextView) super.findViewById(R.id.title_hont);
//        tv_hont.setVisibility(View.GONE);
        tv_hont.setOnFocusChangeListener(this);
        tv_title.add(tv_hont);
        tv_hdmi = (TextView) super.findViewById(R.id.title_hdmi);
        tv_hdmi.setOnFocusChangeListener(this);
        tv_title.add(tv_hdmi);
        fragments = new ArrayList<BaseFragment>();
        fragments.add(new VideoFragment());
        fragments.add(new BoundaryFragment());
        fragments.add(new ScreenDisplayFragment());
        fragments.add(new FontFragment());
        fragments.add(new HDMIFragment());

        LinearLayout layout = (LinearLayout) super
                .findViewById(R.id.linear_title);
        fragmentTabAdapter = new FragmentTabAdapter(this, fragments,
                R.id.mFrameLayout, layout);

        Intent intent = getIntent();
        boolean showFont = intent.getBooleanExtra(SHOW_FONT, false);
        if (showFont) {
            tv_hont.requestFocus();
        } else {
            tv_video.requestFocus();
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        TextView tv = (TextView) v;
        tv.setTextColor(Color.WHITE);
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.epg_btn_f);
            tv.setTextColor(getResources().getColor(R.color.focused_color));
        } else {
            v.setBackgroundResource(R.drawable.btn_title);
        }

        for (int i = 0; i < this.tv_title.size(); i++) {
            if (v.getId() != this.tv_title.get(i).getId()) {
                this.tv_title.get(i).setBackgroundResource(Color.TRANSPARENT);
            }
        }
        this.fragmentTabAdapter.showFocuseFragment(v);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                int ff = fragmentTabAdapter.getCorrentFragmentNumber();
                tv_title.get(ff).requestFocus();
                finish();
                return false;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
