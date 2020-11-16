package com.wwsl.mdsj.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.maodou.NetMwTaskBean;
import com.wwsl.mdsj.utils.StringUtil;

public class MwDetailDialog extends BasePopupView {


    private AppCompatTextView name;
    private AppCompatTextView needNum;
    private AppCompatTextView resNum;
    private AppCompatTextView activeAddNum;
    private AppCompatTextView sameTimeNum;
    private AppCompatTextView taskExpired;
    private AppCompatTextView activeExpired;
    private NetMwTaskBean bean;

    public MwDetailDialog(@NonNull Context context, NetMwTaskBean netMwTaskBean) {
        super(context);
        this.bean = netMwTaskBean;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_mw_detail;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        name = findViewById(R.id.name);
        needNum = findViewById(R.id.needNum);
        resNum = findViewById(R.id.resNum);
        activeAddNum = findViewById(R.id.activeAddNum);
        sameTimeNum = findViewById(R.id.sameTimeNum);
        taskExpired = findViewById(R.id.taskExpired);
        activeExpired = findViewById(R.id.activeExpired);

        findViewById(R.id.ivClose).setOnClickListener(v -> {
            dismiss();
        });

        update();
    }

    public void update() {
        if (null == bean) return;
        name.setText(bean.getTitle());
        needNum.setText(String.format("%s颗", StringUtil.checkNullNumberStr(bean.getPrice())));
        resNum.setText(String.format("%s颗", StringUtil.checkNullNumberStr(bean.getIncome())));
        sameTimeNum.setText(String.format("%s个", StringUtil.checkNullNumberStr(bean.getLimit())));
        taskExpired.setText(String.format("%s天", StringUtil.checkNullNumberStr(bean.getTaskExpired())));
        activeExpired.setText(String.format("%s天", StringUtil.checkNullNumberStr(bean.getActiveExpired())));
        activeAddNum.setText(StringUtil.checkNullNumberStr(bean.getGivehuo()));
    }
}
