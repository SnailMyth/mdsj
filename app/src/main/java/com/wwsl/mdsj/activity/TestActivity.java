package com.wwsl.mdsj.activity;

import android.content.Intent;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebView;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;

/**
 * Created by cxf on 2018/10/19.
 */

public class TestActivity extends AbsActivity {

    String h5 = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Document</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <img src=\"https://img.yzcdn.cn/vant/leaf.jpg\" alt=\"\" style='width: 50px; height: 50px;'>\n" +
            "</body>\n" +
            "</html>";
    private TextView textView4;
    private WebView testWeb;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void main() {
        textView4 = findViewById(R.id.textView4);
        testWeb = findViewById(R.id.testWeb);
        testWeb.loadData(h5,"text/html", "UTF-8");
    }

    public static void forward() {
        Intent intent = new Intent(AppContext.sInstance, TestActivity.class);
        AppContext.sInstance.startActivity(intent);
    }

}
