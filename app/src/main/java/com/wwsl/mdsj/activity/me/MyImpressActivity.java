package com.wwsl.mdsj.activity.me;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.bean.ImpressBean;
import com.wwsl.mdsj.custom.MyTextView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/15.
 * 我收到的主播印象
 */

public class MyImpressActivity extends AbsActivity {

    private LinearLayout mGroup;
    private TextView mTip;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_impress;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.impress));
        mGroup = (LinearLayout) findViewById(R.id.group);
        mTip = (TextView) findViewById(R.id.tip);
        HttpUtil.getMyImpress(AppConfig.getInstance().getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        List<ImpressBean> list = JSON.parseArray(Arrays.toString(info), ImpressBean.class);
                        int line = 0;
                        int fromIndex = 0;
                        boolean hasNext = true;
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        while (hasNext) {
                            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.view_impress_line, mGroup, false);
                            int endIndex = line % 2 == 0 ? fromIndex + 3 : fromIndex + 2;
                            if (endIndex >= list.size()) {
                                endIndex = list.size();
                                hasNext = false;
                            }
                            for (int i = fromIndex; i < endIndex; i++) {
                                MyTextView item = (MyTextView) inflater.inflate(R.layout.view_impress_item, linearLayout, false);
                                item.setBean(list.get(i),true);
                                linearLayout.addView(item);
                            }
                            fromIndex = endIndex;
                            line++;
                            mGroup.addView(linearLayout);
                        }
                    } else {
                        mTip.setText(WordUtil.getString(R.string.impress_tip_3));
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.GET_MY_IMPRESS);
        super.onDestroy();
    }
}
