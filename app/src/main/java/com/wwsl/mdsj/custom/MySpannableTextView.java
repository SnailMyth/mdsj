package com.wwsl.mdsj.custom;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class MySpannableTextView extends TextView {

    private LinkTouchMovementMethod mLinkTouchMovementMethod;

    public MySpannableTextView(Context context) {
        super(context);
    }

    public MySpannableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpannableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        return mLinkTouchMovementMethod != null ? mLinkTouchMovementMethod.isPressedSpan() : result;
    }

    public void setLinkTouchMovementMethod(LinkTouchMovementMethod linkTouchMovementMethod) {
        mLinkTouchMovementMethod = linkTouchMovementMethod;
    }
}