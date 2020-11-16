package com.wwsl.mdsj.activity.me;

import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

/**
 * Created by cxf on 2018/9/29.
 * 设置昵称
 */

public class EditNameActivity extends AbsActivity implements View.OnClickListener {
    private EditText mEditText;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_name;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile_update_nickname));
        mEditText = findViewById(R.id.edit);
        mEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(8)
        });
        findViewById(R.id.ivClear).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        String content = getIntent().getStringExtra(Constants.NICK_NAME);
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > 8) {
                content = content.substring(0, 8);
            }
            mEditText.setText(content);
            mEditText.setSelection(content.length());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClear:
                mEditText.setText("");
                break;
            case R.id.btn_save:
                if (!canClick()) {
                    return;
                }
                final String content = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.edit_profile_name_empty);
                    return;
                }

                if (content.length()>10){
                    ToastUtil.show("昵称过长~");
                    return;
                }
                HttpUtil.updateFields("{\"user_nicename\":\"" + content + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                ToastUtil.show(obj.getString("msg"));
                                UserBean u = AppConfig.getInstance().getUserBean();
                                if (u != null) {
                                    u.setUsername(content);
                                }
                                Intent intent = getIntent();
                                intent.putExtra(Constants.NICK_NAME, content);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
                break;
        }
    }


    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.UPDATE_FIELDS);
        super.onDestroy();
    }
}
