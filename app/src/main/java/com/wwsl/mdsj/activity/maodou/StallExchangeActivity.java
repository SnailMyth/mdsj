package com.wwsl.mdsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;

public class StallExchangeActivity extends BaseActivity {

    private int type = 0;//0.毛豆,1.豆丁
    private int step = 0;
    private TextView txTitle;
    private TextView tvDialogWord;
    private AppCompatEditText etPrice;
    private TextView tvPer;
    private ConstraintLayout step1Layout;
    private AppCompatEditText etNum;
    private ConstraintLayout step2Layout;
    private ImageView ivImg;
    private TextView tvFinishNum;
    private TextView tvBottom;
    private ConstraintLayout step3Layout;
    private ConstraintLayout inputLayout;
    private ImageView ivLeft;
    private ConstraintLayout rootView;


    private String price = "";
    private String num = "";

    @Override
    protected int setLayoutId() {
        return R.layout.activity_stall_exchange;
    }

    @Override
    protected void init() {
        type = getIntent().getIntExtra("type", 0);
        findView();
        initView();
    }

    private void initView() {
        String title = "";
        String dialogWord = "";
        String per = "";
        int imgRes = 0;
        if (type == 0) {//兑换毛豆
            title = "豆丁/毛豆兑换";
            dialogWord = "客官想要\t以多少豆丁换一个毛豆呢？";
            imgRes = R.mipmap.icon_img_md;
            per = "豆丁/毛豆";
        } else {//兑换豆丁
            title = "毛豆/豆丁兑换";
            dialogWord = "客官想要\t以一个毛豆换多少豆丁呢？";
            imgRes = R.mipmap.icon_img_dd;
            per = "毛豆/豆丁";
        }

        tvDialogWord.setText(dialogWord);
        txTitle.setText(title);
        ivImg.setBackgroundResource(imgRes);
        tvPer.setText(per);
    }

    public static void forward(Context context, int type) {
        Intent intent = new Intent(context, StallExchangeActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    private void findView() {
        txTitle = findViewById(R.id.txTitle);
        tvDialogWord = findViewById(R.id.tvDialogWord);
        etPrice = findViewById(R.id.etSalePrice);
        tvPer = findViewById(R.id.tvPer);
        step1Layout = findViewById(R.id.step1Layout);
        etNum = findViewById(R.id.etNum);
        step2Layout = findViewById(R.id.step2Layout);
        ivImg = findViewById(R.id.ivImg);
        tvFinishNum = findViewById(R.id.tvFinishNum);
        tvBottom = findViewById(R.id.tvBottom);
        step3Layout = findViewById(R.id.step3Layout);
        inputLayout = findViewById(R.id.inputLayout);
        ivLeft = findViewById(R.id.ivLeft);
        rootView = findViewById(R.id.rootView);
    }

    public void backClick(View view) {
        finish();
    }


    public void nextStep(View view) {
        if (step == 0) {
            step = 1;
            price = etPrice.getText().toString().trim();
            step1Layout.setVisibility(View.GONE);
            step2Layout.setVisibility(View.VISIBLE);
            step3Layout.setVisibility(View.GONE);
        } else if (step == 1) {
            //发起兑换
            num = etNum.getText().toString().trim();
            doExchange();
        } else if (step == 2) {
            step = 3;
            tvBottom.setBackgroundResource(R.mipmap.icon_btn_finish);
            step1Layout.setVisibility(View.GONE);
            step2Layout.setVisibility(View.GONE);
            step3Layout.setVisibility(View.VISIBLE);
        } else if (step == 3) {
            finish();
        }
    }

    private void doExchange() {
        step = 2;
        tvFinishNum.setText(String.format("+%s", num));
        nextStep(null);
    }
}
