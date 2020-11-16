package com.wwsl.mdsj.activity.af;

import androidx.fragment.app.FragmentTransaction;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.fragment.WithdrawalsRecordFragment;

/**
 * 第四层UI主窗口
 */
public class FourthActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.view_framelayout;
    }

    @Override
    protected void init() {
        int index = getIntent().getIntExtra("R.id", -1);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (index) {
            case R.id.record_list:
                transaction.replace(R.id.fLayout, new WithdrawalsRecordFragment(), "提现记录");
                break;
        }
        transaction.commit();
    }
}
