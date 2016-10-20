package com.music.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.music.R;

public abstract class BaseFragmentActivity extends FragmentActivity {

	public static final int SCAN_START = 0x001;
	public static final int SCAN_END = 0x002;
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(bundle);
		setContentView(R.layout.test);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.global_titlebar);
		
	}

}
