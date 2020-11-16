package com.wwsl.mdsj.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;


/**
 * @author :
 * @date : 2020/6/17 15:39
 * @description : 用于加载icon font的TextView
 */
public class IconFontTextView extends AppCompatTextView {
    /** 所有IconFontTextView公用typeface */
    private static Typeface typeface;

    public IconFontTextView(Context context) {
        super(context);
    }

    public IconFontTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    {
        typeface = Typeface.createFromAsset(getContext().getAssets(), "iconfont.ttf");
        setTypeface(typeface);
    }

}
