package com.wwsl.mdsj.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TicketListItemLayout extends RelativeLayout {

    public TicketListItemLayout(Context context) {
        super(context);
    }

    public TicketListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TicketListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 1 / 2), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
