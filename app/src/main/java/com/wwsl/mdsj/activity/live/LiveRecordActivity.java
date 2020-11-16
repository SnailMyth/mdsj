package com.wwsl.mdsj.activity.live;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.LiveRecordViewHolder;

/**
 * Created by cxf on 2018/9/30.
 */

public class LiveRecordActivity extends AbsActivity {

    public static void forward(Context context, UserBean userBean) {
        if (userBean == null) {
            return;
        }
        Intent intent = new Intent(context, LiveRecordActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.live_record));
        UserBean userBean = getIntent().getParcelableExtra(Constants.USER_BEAN);
        if (userBean == null) {
            return;
        }
        LiveRecordViewHolder liveRecordViewHolder = new LiveRecordViewHolder(mContext, (ViewGroup) findViewById(R.id.container));
        addLifeCycleListener(liveRecordViewHolder.getLifeCycleListener());
        liveRecordViewHolder.addToParent();
        liveRecordViewHolder.loadData(userBean);
    }
}
