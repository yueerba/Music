package com.music.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.music.AppInfo;
import com.music.R;
import com.music.activity.BaseActivity;
import com.music.service.MusicUtil;
import com.music.util.CommonUtil;
import com.music.util.SharedPreferencesUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class SleepSettingsActivity extends BaseActivity {

    private final static String SLEEP_MODE = "sleep_mode";

    TextView txtPrompt;
    ListView mListView;

    MyAdapter myAdapter;
    String[] sleepTime;
    int[] sleepTimeValue;
    Timer mTimer;
    MyCountDownTimer mCountDownTimer;

    int currentSleepTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_settings);
        txtPrompt = (TextView) findViewById(R.id.txtPrompt);
        mListView = (ListView) findViewById(R.id.listview);

        currentSleepTime = SharedPreferencesUtil.getInstance().getShare(SLEEP_MODE, -1);
        sleepTime = getResources().getStringArray(R.array.sleep_time);
        sleepTimeValue = getResources().getIntArray(R.array.sleep_time_value);
        myAdapter = new MyAdapter(this, sleepTime, sleepTimeValue);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(onItemClickListener);

        mCountDownTimer = MyCountDownTimer.getInstance(0);
        mCountDownTimer.setCallBack(callBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownTimer.removeCallBack(callBack);
    }

    MyCountDownTimer.ICallBack callBack = new MyCountDownTimer.ICallBack() {
        @Override
        public void onTick(int millisUntilFinished) {
            String str = getResources().getString(R.string.sleep_settings_prompt2, CommonUtil.secondTo(millisUntilFinished, true));
            txtPrompt.setText(str);
        }

        @Override
        public void onFinish() {
            txtPrompt.setText(R.string.sleep_settings_prompt);
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentSleepTime = sleepTimeValue[position];
            if (currentSleepTime == 0) {// customize
                showCustomSleepDialog();
            } else {
                SharedPreferencesUtil.getInstance().putShare(SLEEP_MODE, currentSleepTime);
                myAdapter.notifyDataSetChanged();
                if (mTimer != null) {
                    mTimer.cancel();
                }
                mTimer = new Timer();
                if (sleepTimeValue[position] == -1) {
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                        mCountDownTimer.onFinish();
                    }
                    Snackbar.make(view, R.string.cancel_timer, Snackbar.LENGTH_SHORT).show();
                } else {
                    mTimer.schedule(new timertask(), currentSleepTime * 1000 * 60);
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    mCountDownTimer.removeInstance();
                    mCountDownTimer = MyCountDownTimer.getInstance(currentSleepTime * 1000 * 60);
                    mCountDownTimer.setCallBack(callBack);
                    mCountDownTimer.start();
                    String str = getResources().getString(R.string.sleep_settings_toast, currentSleepTime);
                    Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void showCustomSleepDialog() {
        View view = View.inflate(this, R.layout.dialog_sleep_time, null);
        final Dialog dialog = new Dialog(this, R.style.sleep_time_dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        lp.width = (int) (AppInfo.screenWidth * 0.7);
        window.setAttributes(lp);

        dialog.show();

        final Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        final Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        final EditText et_time = (EditText) view.findViewById(R.id.et_time);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.bt_cancel) {
                    dialog.dismiss();
                } else if (view.getId() == R.id.bt_ok) {
                    String time = et_time.getText().toString();
                    if (TextUtils.isEmpty(time) || Integer.parseInt(time) == 0) {
                        Snackbar.make(txtPrompt, "输入无效", Snackbar.LENGTH_SHORT).show();
                    } else {
                        setSleepTime(time);
                        SharedPreferencesUtil.getInstance().putShare(SLEEP_MODE, 0);
                        myAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        Snackbar.make(txtPrompt, "亲，将在" + time + "分钟后退出", Snackbar.LENGTH_SHORT).show();
                    }

                }
            }
        };

        bt_cancel.setOnClickListener(listener);
        bt_ok.setOnClickListener(listener);
    }

    private void setSleepTime(String times) {
        int time = Integer.parseInt(times);
        long longtime = time * 60 * 1000;

        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new timertask(), longtime);
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer.removeInstance();
        mCountDownTimer = MyCountDownTimer.getInstance(longtime);
        mCountDownTimer.setCallBack(callBack);
        mCountDownTimer.start();
    }

    class MyAdapter extends BaseAdapter {

        Context mContext;
        String[] sleep;
        int[] sleepValue;

        public MyAdapter(Context context, String[] sleep, int[] sleepValue) {
            mContext = context;
            this.sleep = sleep;
            this.sleepValue = sleepValue;
        }

        @Override
        public int getCount() {
            return sleep.length;
        }

        @Override
        public Object getItem(int position) {
            return sleep[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sleeptime, null);
                holder = new ViewHolder();
                holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
                holder.checkState = (ImageView) convertView.findViewById(R.id.checkState);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtTime.setText(sleep[position]);
            if (currentSleepTime == sleepValue[position]) {
                holder.checkState.setSelected(true);
            } else {
                holder.checkState.setSelected(false);
            }

            return convertView;
        }
    }

    class ViewHolder {
        TextView txtTime;
        ImageView checkState;
    }


    class timertask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.d("dingfeng", "excute tiemer task");
            try {
                if (MusicUtil.sService != null && MusicUtil.sService.isPlaying()) {
                    MusicUtil.sService.pause();
//                    MusicUtil.sService.exit();
//					System.exit(0);
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


}
