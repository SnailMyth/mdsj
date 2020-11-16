package com.wwsl.mdsj.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;

public class CommonSuccessActivity extends BaseActivity {

    private TextView tvTitle;
    private TextView tvContent;
    private int type;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_common_success;
    }

    @Override
    protected void init() {
        initView();
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        type = getIntent().getIntExtra("type", -1);
        tvTitle.setText(title);
        tvContent.setText(content);
    }

    public static void forward(Context context, String title, String content, int type) {
        Intent intent = new Intent(context, CommonSuccessActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public void backClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (type == Constants.SUCCESS_PAGE_TYPE_PARTNER) {
//            UserCenterActivity.forward(this);
        } else if (type == Constants.SUCCESS_PAGE_TYPE_USER_ID_AUTH) {
            AppConfig.getInstance().getUserBean().setIsIdIdentify(1);
//            UserCenterActivity.forward(this);
        } else if (type == Constants.SUCCESS_PAGE_TYPE_VIP) {
            AppConfig.getInstance().getUserBean().setIsVip(1);
        } else if (type == Constants.SUCCESS_PAGE_TYPE_USER_AUTH) {
            AppConfig.getInstance().getUserBean().setAuth(1);
        }
        finish();
    }

    private void initView() {
        tvTitle = findViewById(R.id.title);
        tvContent = findViewById(R.id.tvContent);
    }
}
