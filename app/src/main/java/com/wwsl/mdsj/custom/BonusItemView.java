package com.wwsl.mdsj.custom;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.wwsl.mdsj.R;

/**
 * Created by cxf on 2018/10/1.
 */

public class BonusItemView extends RatioImageView {
    public BonusItemView(Context context) {
        super(context);
    }

    public BonusItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BonusItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChecked(boolean checked) {
        if (checked) {
            setImageResource(R.mipmap.icon_bonus_1);
        } else {
            setImageDrawable(null);
        }
    }
}
