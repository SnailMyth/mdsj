package com.wwsl.mdsj.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.wwsl.mdsj.R;

public class CodeCutDownTimer extends CountDownTimer {

    private TextView mTextView;
    private String normalColor = "#ffff5a60";
    private String tickColor = "#D9D9D9";

    public CodeCutDownTimer(long total, TextView mTextView) {
        super(total, 1000);
        this.mTextView = mTextView;
    }

    public void setColor(String normalColor, String tickColor) {
        this.normalColor = normalColor;
        this.tickColor = tickColor;
    }

    public CodeCutDownTimer(TextView mTextView) {
        super(60000, 1000);
        this.mTextView = mTextView;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); // 设置不可点击
        mTextView.setText(String.format("%d秒", millisUntilFinished / 1000)); // 设置倒计时时间
        mTextView.setTextColor(Color.parseColor(tickColor));
    }

    @Override
    public void onFinish() {
        mTextView.setText("重发验证码");
        mTextView.setClickable(true);// 重新获得点击
        mTextView.setTextColor(Color.parseColor(normalColor));
    }
}
