package com.music.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.music.R;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class RhythmFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_rhythm, container, false);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.cd_rotate);
        LinearInterpolator lir = new LinearInterpolator();
        anim.setInterpolator(lir);
        view.findViewById(R.id.cd).startAnimation(anim);

        return view;
    }

}
