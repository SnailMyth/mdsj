package com.wwsl.mdsj.views;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;




/**
 * @author :
 * @date : 2020/6/17 16:35
 * @description : 跑马灯 textview
 */
public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused(){
        return true;
    }

}
