package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.HelpCenterViewHolder;

public class HelpCenterActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, HelpCenterActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activitity_help_center;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.help_center));
        HelpCenterViewHolder mainListViewHolder = new HelpCenterViewHolder(mContext, (FrameLayout) findViewById(R.id.layoutHelpCenter));
//        mainListViewHolder.hideTop();
        mainListViewHolder.addToParent();
        mainListViewHolder.loadData();
    }
}
