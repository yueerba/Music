package com.music.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.music.R;
import com.music.activity.ScanActivity;
import com.music.activity.ThemeActivity;
import com.music.base.IConstants;
import com.music.service.MusicUtil;
import com.music.setting.MyCountDownTimer;
import com.music.setting.SettingsActivity;
import com.music.setting.SleepSettingsActivity;
import com.music.util.CommonUtil;
import com.music.util.SharedPreferencesUtil;

/**
 * Created by dingfeng on 2016/4/12.
 */
public class SlideFragment extends Fragment implements View.OnClickListener {

    LinearLayout layoutScan;
    LinearLayout layoutPlaymode;
    ImageView iconPlaymode;
    TextView txtPlaymode;
    LinearLayout layoutTheme;
    LinearLayout layoutSleep;
    LinearLayout layoutSetting;
    LinearLayout layoutQuit;
    TextView txtTimeLeft;

    MyCountDownTimer mCountDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        layoutScan = (LinearLayout) view.findViewById(R.id.layoutScan);
        layoutScan.setOnClickListener(this);
        layoutPlaymode = (LinearLayout) view.findViewById(R.id.layoutPlaymode);
        layoutPlaymode.setOnClickListener(this);
        iconPlaymode = (ImageView) view.findViewById(R.id.iconPlaymode);
        txtPlaymode = (TextView) view.findViewById(R.id.txtPlaymode);
        layoutTheme = (LinearLayout) view.findViewById(R.id.layoutTheme);
        layoutTheme.setOnClickListener(this);
        layoutSleep = (LinearLayout) view.findViewById(R.id.layoutSleep);
        layoutSleep.setOnClickListener(this);
        layoutSetting = (LinearLayout) view.findViewById(R.id.layoutSetting);
        layoutSetting.setOnClickListener(this);
        layoutQuit = (LinearLayout) view.findViewById(R.id.layoutQuit);
        layoutQuit.setOnClickListener(this);
        txtTimeLeft = (TextView) view.findViewById(R.id.txtTimeLeft);
        updatePlayModeState();
    }

    @Override
    public void onResume() {
        super.onResume();
        txtTimeLeft.setVisibility(View.GONE);
        mCountDownTimer = MyCountDownTimer.getInstance(0);
        mCountDownTimer.setCallBack(callBack);
    }

    @Override
    public void onStop() {
        super.onStop();
        mCountDownTimer.removeCallBack(callBack);
    }

    MyCountDownTimer.ICallBack callBack = new MyCountDownTimer.ICallBack() {
        @Override
        public void onTick(int millisUntilFinished) {
            txtTimeLeft.setVisibility(View.VISIBLE);
            txtTimeLeft.setText(CommonUtil.secondTo(millisUntilFinished, false));
        }

        @Override
        public void onFinish() {
            txtTimeLeft.setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.layoutScan:
                startActivity(new Intent(getActivity(), ScanActivity.class));
                break;
            case R.id.layoutPlaymode:
                setPlayMode();
                break;
            case R.id.layoutTheme:
                startActivity(new Intent(getActivity(), ThemeActivity.class));
                break;
            case R.id.layoutSleep:
                startActivity(new Intent(getActivity(), SleepSettingsActivity.class));
                break;
            case R.id.layoutSetting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.layoutQuit:
                quit();
                break;
            default:
                break;
        }
    }

    private void updatePlayModeState() {
        int mode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        Drawable drawable;
        switch (mode) {
            case IConstants.ORDER_PLAY:
                txtPlaymode.setText(R.string.play_mode_normal);
                drawable = getResources().getDrawable(R.drawable.ic_playmode_normal);
                iconPlaymode.setImageDrawable(drawable);
                break;
            case IConstants.LIST_LOOP_PLAY:
                txtPlaymode.setText(R.string.play_mode_repeat_all);
                drawable = getResources().getDrawable(R.drawable.ic_playmode_repeat_all);
                iconPlaymode.setImageDrawable(drawable);
                break;
            case IConstants.RANDOM_PLAY:
                txtPlaymode.setText(R.string.play_mode_shuffle);
                drawable = getResources().getDrawable(R.drawable.ic_playmode_shuffle);
                iconPlaymode.setImageDrawable(drawable);
                break;
            case IConstants.SINGLE_LOOP_PLAY:
                txtPlaymode.setText(R.string.play_mode_single_repeat);
                drawable = getResources().getDrawable(R.drawable.ic_playmode_single_repeat);
                iconPlaymode.setImageDrawable(drawable);
                break;
            default:
                break;
        }
    }

    private void setPlayMode() {
        int mode = SharedPreferencesUtil.getInstance().getShare("play_mode", 0);
        if (mode == IConstants.SINGLE_LOOP_PLAY) {
            mode = IConstants.ORDER_PLAY;
        } else {
            mode++;
        }
        SharedPreferencesUtil.getInstance().putShare("play_mode", mode);
        updatePlayModeState();
    }

    private void quit() {
        if (MusicUtil.sService != null) {
            try {
                MusicUtil.sService.exit();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        getActivity().finish();
    }
}
