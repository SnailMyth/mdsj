package com.wwsl.mdsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.maodou.StallItemBean;
import com.wwsl.mdsj.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MdStallActivity extends BaseActivity {

    private ImageView btnMd;
    private ImageView btnDd;
    private ImageView ivLeft;
    private ImageView ivPackage;
    private ImageView icon1;
    private TextView tvTitle1;
    private TextView tvName1;
    private ConstraintLayout leftStall1;
    private ImageView icon2;
    private TextView tvTitle2;
    private TextView tvName2;
    private ConstraintLayout leftStall2;
    private ImageView icon3;
    private TextView tvTitle3;
    private TextView tvName3;
    private ConstraintLayout leftStall3;
    private ImageView icon4;
    private TextView tvTitle4;
    private TextView tvName4;
    private ConstraintLayout rightStall1;
    private ImageView icon5;
    private TextView tvTitle5;
    private TextView tvName5;
    private ConstraintLayout rightStall2;

    private List<StallItemBean> mdBeans;
    private List<StallItemBean> ddBeans;
    private List<TextView> titles;
    private List<TextView> names;
    private int type = 0;//0.毛豆 1.豆丁

    @Override
    protected int setLayoutId() {
        return R.layout.activity_md_stall;
    }

    @Override
    protected void init() {
        initView();
        initData();
        initAnim();
    }

    private void initData() {
        mdBeans = new ArrayList<>();
        ddBeans = new ArrayList<>();

        mdBeans.add(StallItemBean.builder().id("0").name("傻1哈").title("毛豆/191.00个豆丁").build());
        mdBeans.add(StallItemBean.builder().id("1").name("傻2哈").title("毛豆/192.00个豆丁").build());
        mdBeans.add(StallItemBean.builder().id("2").name("傻3哈").title("毛豆/193.00个豆丁").build());
        mdBeans.add(StallItemBean.builder().id("3").name("傻4哈").title("毛豆/194.00个豆丁").build());
        mdBeans.add(StallItemBean.builder().id("4").name("傻5哈").title("毛豆/195.00个豆丁").build());

        ddBeans.add(StallItemBean.builder().id("0").name("傻1哈").title("191.00个豆丁/毛豆").build());
        ddBeans.add(StallItemBean.builder().id("1").name("傻2哈").title("192.00个豆丁/毛豆").build());
        ddBeans.add(StallItemBean.builder().id("2").name("傻3哈").title("193.00个豆丁/毛豆").build());
        ddBeans.add(StallItemBean.builder().id("3").name("傻4哈").title("194.00个豆丁/毛豆").build());
        ddBeans.add(StallItemBean.builder().id("4").name("傻5哈").title("195.00个豆丁/毛豆").build());

    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MdStallActivity.class);
        context.startActivity(intent);
    }

    public void clickStartStall(View view) {
        StallExchangeActivity.forward(this, type == 1 ? 0 : 1);
    }

    public void clickMd(View view) {
        changeMode();
    }

    public void backClick(View view) {
        finish();
    }

    public void goPackage(View view) {

    }

    private void initView() {
        btnMd = (ImageView) findViewById(R.id.btnMd);
        btnDd = (ImageView) findViewById(R.id.btnDd);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivPackage = (ImageView) findViewById(R.id.ivPackage);
        icon1 = (ImageView) findViewById(R.id.icon1);
        tvTitle1 = (TextView) findViewById(R.id.tvTitle1);
        tvName1 = (TextView) findViewById(R.id.tvName1);
        leftStall1 = (ConstraintLayout) findViewById(R.id.leftStall1);
        icon2 = (ImageView) findViewById(R.id.icon2);
        tvTitle2 = (TextView) findViewById(R.id.tvTitle2);
        tvName2 = (TextView) findViewById(R.id.tvName2);
        leftStall2 = (ConstraintLayout) findViewById(R.id.leftStall2);
        icon3 = (ImageView) findViewById(R.id.icon3);
        tvTitle3 = (TextView) findViewById(R.id.tvTitle3);
        tvName3 = (TextView) findViewById(R.id.tvName3);
        leftStall3 = (ConstraintLayout) findViewById(R.id.leftStall3);
        icon4 = (ImageView) findViewById(R.id.icon4);
        tvTitle4 = (TextView) findViewById(R.id.tvTitle4);
        tvName4 = (TextView) findViewById(R.id.tvName4);
        rightStall1 = (ConstraintLayout) findViewById(R.id.rightStall1);
        icon5 = (ImageView) findViewById(R.id.icon5);
        tvTitle5 = (TextView) findViewById(R.id.tvTitle5);
        tvName5 = (TextView) findViewById(R.id.tvName5);
        rightStall2 = (ConstraintLayout) findViewById(R.id.rightStall2);
        titles = new ArrayList<>();
        names = new ArrayList<>();
        titles.add(tvTitle1);
        titles.add(tvTitle2);
        titles.add(tvTitle3);
        titles.add(tvTitle4);
        titles.add(tvTitle5);

        names.add(tvName1);
        names.add(tvName2);
        names.add(tvName3);
        names.add(tvName4);
        names.add(tvName5);
    }

    private TranslateAnimation shakeAnim;

    private ScaleAnimation scaleAnim;

    public void initAnim() {
        shakeAnim = new TranslateAnimation(0, 0, 0, 20);
        scaleAnim = new ScaleAnimation(0, 1, 0, 1);
        scaleAnim.setDuration(500);

        shakeAnim.setDuration(2000);           //设置动画持续时间
        shakeAnim.setRepeatCount(Animation.INFINITE);         //设置重复次数
        shakeAnim.setRepeatMode(Animation.REVERSE);    //反方向执行


        AnimationSet smallAnimationSet = new AnimationSet(false);
        smallAnimationSet.addAnimation(scaleAnim);
        smallAnimationSet.addAnimation(shakeAnim);

        leftStall1.setAnimation(smallAnimationSet);             //设置动画效果
        leftStall2.setAnimation(smallAnimationSet);             //设置动画效果
        leftStall3.setAnimation(smallAnimationSet);             //设置动画效果
        rightStall1.setAnimation(smallAnimationSet);             //设置动画效果
        rightStall2.setAnimation(smallAnimationSet);             //设置动画效果

    }


    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    private void release() {
        scaleAnim.cancel();
        shakeAnim.cancel();
        scaleAnim = null;
        shakeAnim = null;
    }

    public void goDetail(View view) {
        switch (view.getId()) {
            case R.id.leftStall1:
                goNext(0);
                break;
            case R.id.leftStall2:
                goNext(1);
                break;
            case R.id.leftStall3:
                goNext(2);
                break;
            case R.id.rightStall1:
                goNext(3);
                break;
            case R.id.rightStall2:
                goNext(4);
                break;
        }
    }

    public void goNext(int i) {
        if (i < 0 || i > 4) return;
    }

    public void changeMode() {
        type = type == 1 ? 0 : 1;
        if (type == 0) {//毛豆
            for (int i = 0; i < mdBeans.size(); i++) {
                titles.get(i).setText(mdBeans.get(i).getTitle());
                names.get(i).setText(String.format("%s的地摊", mdBeans.get(i).getName()));
                btnDd.setBackgroundResource(R.mipmap.icon_btn_md);
            }
        } else {
            for (int i = 0; i < ddBeans.size(); i++) {
                titles.get(i).setText(ddBeans.get(i).getTitle());
                names.get(i).setText(String.format("%s的地摊", ddBeans.get(i).getName()));
                btnDd.setBackgroundResource(R.mipmap.icon_btn_dd);
            }
        }
        scaleAnim.start();
    }
}
