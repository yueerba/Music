package com.music.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.music.R;
import com.music.setting.ScanTask;
import com.music.view.ScanAnimation;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class ScanActivity extends BaseActivity implements View.OnClickListener {

    ImageView back;
    Button btScan;
    Button btScanEnd;
    ScanAnimation scanAnimation;

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        setContentView(R.layout.activity_scan);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backClickListener);
        btScan = (Button) findViewById(R.id.btScan);
        btScan.setOnClickListener(this);
        btScanEnd = (Button) findViewById(R.id.btScanEnd);
        btScanEnd.setOnClickListener(this);

        scanAnimation = (ScanAnimation) findViewById(R.id.scan_animation_view);
        scanAnimation.setWillNotDraw(false);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if (view.getId() == R.id.btScan) {
            new ScanTask(ScanActivity.this, callback).execute();
        } else if (view.getId() == R.id.btScanEnd) {
            onBackPressed();
        }
    }

    ScanTask.Callback callback = new ScanTask.Callback() {
        @Override
        public void start() {
            btScan.setClickable(false);
            btScan.setText(R.string.scaning);
            scanAnimation.setSearching(true);
        }

        @Override
        public void scan() {

        }

        @Override
        public void end() {
            btScan.setVisibility(View.GONE);
            btScanEnd.setVisibility(View.VISIBLE);
            scanAnimation.setSearching(false);
        }
    };
}

