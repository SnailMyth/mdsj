package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;

import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.utils.WordUtil;

public class UserVideoListActivity extends AbsActivity {
    private String userId;

    public static void forward(Context context, String userId) {
        Intent intent = new Intent(context, UserVideoListActivity.class);
        intent.putExtra(Constants.UID, userId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trend;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.video));
        userId = getIntent().getStringExtra(Constants.UID);


    }
}
