package com.wwsl.mdsj.activity.live;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;

import com.wwsl.mdsj.views.OnVideoTickListener;

public class LiveCutDownTimer extends CountDownTimer {

    private OnVideoTickListener listener;
    private boolean isTicking = false;
    private long total;

    public LiveCutDownTimer(long total, OnVideoTickListener listener) {
        super(total, 1000);
        this.listener = listener;
        this.total = total;
    }

    private final static String TAG = "VideoCutDownTimer";

    @SuppressLint("DefaultLocale")
    @Override
    public void onTick(long millisUntilFinished) {
        isTicking = true;
        listener.onTick(total - millisUntilFinished);
    }

    @Override
    public void onFinish() {
        isTicking = false;
        listener.onFinish();
    }

    public boolean isTicking() {
        return isTicking;
    }
}
