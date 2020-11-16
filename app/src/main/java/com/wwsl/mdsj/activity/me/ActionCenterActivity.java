package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.ActionCenterViewHolder;

public class ActionCenterActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ActionCenterActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_action_center;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.action_center));

        ActionCenterViewHolder mainHomeTicketViewHolder = new ActionCenterViewHolder(mContext, (FrameLayout) findViewById(R.id.layoutActionCenter));
        mainHomeTicketViewHolder.addToParent();
        mainHomeTicketViewHolder.loadData();
    }
}
