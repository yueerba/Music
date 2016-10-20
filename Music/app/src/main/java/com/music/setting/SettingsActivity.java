package com.music.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.music.R;
import com.music.activity.BaseActivity;
import com.music.service.MediaNotificationManager;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class SettingsActivity extends BaseActivity {

    private CheckBox cbNotification;

    private CheckBox cbShake;
    private CheckBox cbFlick;

    private CheckBox cbUnplugHeadphone;
    private CheckBox cbPlugHeadphone;

    private CheckBox cbDoublePress;
    private CheckBox cbHookkeyControl;

    private CheckBox cbTimeLimit;
    private CheckBox cbSizeLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // 通知栏
        cbNotification = (CheckBox) findViewById(R.id.cbNotification);
        cbNotification.setChecked(!Setting.getInstance().mNotificationDisplay);
        cbNotification.setOnCheckedChangeListener(onCheckedChangeListener);
        // 摇一摇换歌
        cbShake = (CheckBox) findViewById(R.id.cbShake);
        cbShake.setChecked(Setting.getInstance().mShakeChangeSong);
        cbShake.setOnCheckedChangeListener(onCheckedChangeListener);
        // 挥一挥换歌
        cbFlick = (CheckBox) findViewById(R.id.cbFlick);
        cbFlick.setChecked(Setting.getInstance().mFlickChangeSong);
        cbFlick.setOnCheckedChangeListener(onCheckedChangeListener);
        // 双击切换歌曲
        cbDoublePress = (CheckBox) findViewById(R.id.cbDoublePress);
        cbDoublePress.setChecked(Setting.getInstance().mDoublePress);
        cbDoublePress.setOnCheckedChangeListener(onCheckedChangeListener);
        // 耳机按键控制播放
        cbHookkeyControl = (CheckBox) findViewById(R.id.cbHookkeyControl);
        cbHookkeyControl.setChecked(Setting.getInstance().mHookkeyControl);
        cbHookkeyControl.setOnCheckedChangeListener(onCheckedChangeListener);
        // 拔出耳机暂停
        cbUnplugHeadphone = (CheckBox) findViewById(R.id.cbUnplugHeadphone);
        cbUnplugHeadphone.setOnCheckedChangeListener(onCheckedChangeListener);
        cbUnplugHeadphone.setChecked(Setting.getInstance().mHeadSetUnplug);
        // 插入耳机播放
        cbPlugHeadphone = (CheckBox) findViewById(R.id.cbPlugHeadphone);
        cbPlugHeadphone.setOnCheckedChangeListener(onCheckedChangeListener);
        cbPlugHeadphone.setChecked(Setting.getInstance().mHeadSetPlug);
        // 扫描时间限制
        cbTimeLimit = (CheckBox) findViewById(R.id.cbTimeLimit);
        cbTimeLimit.setOnCheckedChangeListener(onCheckedChangeListener);
        cbTimeLimit.setChecked(Setting.getInstance().mScanTimeLimit);
        // 扫描大小限制
        cbSizeLimit = (CheckBox) findViewById(R.id.cbSizeLimit);
        cbSizeLimit.setOnCheckedChangeListener(onCheckedChangeListener);
        cbSizeLimit.setChecked(Setting.getInstance().mScanSizeLimit);
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == cbNotification) {
                Setting.getInstance().setNotificationDisplay(!isChecked);
                if (isChecked) {
                    MediaNotificationManager.getInstance(SettingsActivity.this).remove();
                } else {
                    MediaNotificationManager.getInstance(SettingsActivity.this).updateNotification();
                }
            } else if (buttonView == cbShake) {
                Setting.getInstance().setShakeChangeSong(isChecked);
            } else if (buttonView == cbFlick) {
                Setting.getInstance().setFlickChangeSong(isChecked);
            } else if (buttonView == cbHookkeyControl) {
                Setting.getInstance().setHookkeyControlg(isChecked);
            } else if (buttonView == cbDoublePress) {
                Setting.getInstance().setHookkeyDoublePress(isChecked);
            } else if (buttonView == cbUnplugHeadphone) {
                Setting.getInstance().setHeadSetUnPlug(isChecked);
            } else if (buttonView == cbPlugHeadphone) {
                Setting.getInstance().setHeadSetPlug(isChecked);
            } else if (buttonView == cbTimeLimit) {
                Setting.getInstance().setScanTimeLimit(isChecked);
            } else if (buttonView == cbSizeLimit) {
                Setting.getInstance().setScanSizeLimit(isChecked);
            }
        }
    };

}