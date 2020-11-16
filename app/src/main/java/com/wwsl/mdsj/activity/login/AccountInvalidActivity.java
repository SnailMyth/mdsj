package com.wwsl.mdsj.activity.login;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.common.ActivityManager;
import com.wwsl.mdsj.event.LoginInvalidEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/10/9.
 * 登录失效的时候以dialog形式弹出的activity
 */

public class AccountInvalidActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(String tip) {
        Intent intent = new Intent(AppContext.sInstance, AccountInvalidActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.TIP, tip);
        AppContext.sInstance.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_invalid;
    }

    @Override
    protected void main() {
        TextView textView = findViewById(R.id.content);
        String tip = getIntent().getStringExtra(Constants.TIP);
        textView.setText(tip);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // TODO: 2020/8/22 设备锁
        int activityIndex = ActivityManager.getInstance().getActivityIndex(LoginActivity.class.getSimpleName());

        if (v.getId() == R.id.btn_cancel) {
            if (activityIndex != 1) {
                EventBus.getDefault().post(new LoginInvalidEvent());
                AppConfig.getInstance().clearLoginInfo();
                MobclickAgent.onProfileSignOff();
                LoginActivity.forward();
            }

        } else if (v.getId() == R.id.btn_confirm) {
            if (activityIndex != 1) {
                EventBus.getDefault().post(new LoginInvalidEvent());
                AppConfig.getInstance().clearLoginInfo();
                MobclickAgent.onProfileSignOff();
                LoginActivity.forward();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
