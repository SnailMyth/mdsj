package com.wwsl.mdsj.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiniu.android.utils.StringUtils;
import com.wwsl.mdsj.R;

/**
 * Created by cxf on 2018/9/21.
 */

public class TabButton extends LinearLayout {

    private Context mContext;
    private float mScale;
    private int mSelectedIcon;
    private int mUnSelectedIcon;
    private String mTip;
    private int mIconSize;
    private int mTextSize;
    private int mTextColorSelect;
    private int mTextColorUnSelect;
    private boolean mChecked;
    private ImageView mImg;
    private TextView mText;
    private boolean isHaveLayout;

    public TabButton(Context context) {
        this(context, null);
    }

    public TabButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabButton);
        mSelectedIcon = ta.getResourceId(R.styleable.TabButton_tbn_selected_icon, 0);
        mUnSelectedIcon = ta.getResourceId(R.styleable.TabButton_tbn_unselected_icon, 0);
        mTip = ta.getString(R.styleable.TabButton_tbn_tip);
        mIconSize = (int) ta.getDimension(R.styleable.TabButton_tbn_icon_size, 0);
        mTextSize = (int) ta.getDimension(R.styleable.TabButton_tbn_text_size, 0);
        mTextColorSelect = ta.getColor(R.styleable.TabButton_tbn_text_color_select, 0xff000000);
        mTextColorUnSelect = ta.getColor(R.styleable.TabButton_tbn_text_color_un_select, 0xff000000);
        mChecked = ta.getBoolean(R.styleable.TabButton_tbn_checked, false);
        isHaveLayout = ta.getBoolean(R.styleable.TabButton_tbn_have_layout, true);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        mImg = new ImageView(mContext);
        LayoutParams params1 = new LinearLayout.LayoutParams(mIconSize, mIconSize);
        params1.setMargins(0, dp2px(5), 0, 0);
        mImg.setLayoutParams(params1);
        mText = new TextView(mContext);
        if (mChecked) {
            mImg.setImageResource(mSelectedIcon);
            mText.setTextColor(mTextColorSelect);
        } else {
            mImg.setImageResource(mUnSelectedIcon);
            mText.setTextColor(mTextColorUnSelect);
        }
        LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins(0, dp2px(1), 0, 0);
        mText.setLayoutParams(params2);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mText.setText(mTip);
        addView(mImg);
        addView(mText);
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        if (checked) {
            mText.setTextColor(mTextColorSelect);
            if (StringUtils.isNullOrEmpty(selectUrl)) {
                mImg.setImageResource(mSelectedIcon);
            } else {
                Glide.with(mContext)
                        .load(selectUrl)
                        .into(mImg);
            }

        } else {
            if (StringUtils.isNullOrEmpty(unselectedUrl)) {
                mImg.setImageResource(mUnSelectedIcon);
            } else {
                Glide.with(mContext)
                        .load(unselectedUrl)
                        .into(mImg);
            }

            mText.setTextColor(mTextColorUnSelect);
        }
    }

    public void setUnselectedUrl(String unselectedUrl) {
        this.unselectedUrl = unselectedUrl;
    }

    public void setSelectUrl(String selectUrl) {
        this.selectUrl = selectUrl;
    }

    private String unselectedUrl;
    private String selectUrl;

    public void setText(String str) {
        mText.setText(str);
    }

    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }

    public boolean isHaveLayout() {
        return isHaveLayout;
    }
}
