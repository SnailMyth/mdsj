package com.wwsl.mdsj.views.dialog;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.views.dialog.address.MyOnWheelChangedListener;
import com.wwsl.mdsj.views.dialog.address.MyWheelView;
import com.wwsl.mdsj.views.dialog.address.SimpleWheelAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TwoChooseWheel implements MyOnWheelChangedListener, View.OnClickListener {


    private Button cancelButton;
    private Button confirmButton;
    private TextView txTitle;
    private String title;
    private MyWheelView firstWheel;
    private MyWheelView secWheel;
    private Activity context;
    private View parentView;
    private PopupWindow popupWindow = null;
    private WindowManager.LayoutParams layoutParams = null;
    private LayoutInflater layoutInflater = null;


    private Map<String, List<String>> firstData = null;
    private List<String> firstList = null;

    private OnDataChoseListener listener = null;

    public TwoChooseWheel(Activity context, String title) {
        this.context = context;
        this.title = title;
        init();
    }

    private void init() {
        layoutParams = context.getWindow().getAttributes();
        layoutInflater = context.getLayoutInflater();
        initView();
        initPopupWindow();
    }

    private void initView() {
        parentView = layoutInflater.inflate(R.layout.dialog_choose_two_layout, null);
        txTitle = parentView.findViewById(R.id.title);
        txTitle.setText(title);
        firstWheel = parentView.findViewById(R.id.level_0);
        secWheel = parentView.findViewById(R.id.level_1);
        cancelButton = parentView.findViewById(R.id.cancel_button);
        confirmButton = parentView.findViewById(R.id.confirm_button);

        firstWheel.setVisibleItems(5);
        secWheel.setVisibleItems(1);

        firstWheel.addChangingListener(this);
        secWheel.addChangingListener(this);
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

    }

    private void initPopupWindow() {
        popupWindow = new PopupWindow(parentView, WindowManager.LayoutParams.MATCH_PARENT, (int) (CommonUtil.getScreenHeight(context) * (2.0 / 5)));
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setAnimationStyle(R.style.anim_push_bottom);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                layoutParams.alpha = 1.0f;
                context.getWindow().setAttributes(layoutParams);
                popupWindow.dismiss();
            }
        });
    }

    private void bindData() {
        firstList = new ArrayList<>(firstData.keySet());
        firstWheel.setViewAdapter(new SimpleWheelAdapter(context, firstList));
        updateLevel0();
    }

    @Override
    public void onChanged(MyWheelView wheel, int oldValue, int newValue) {
        if (wheel == firstWheel) {
            updateLevel0();//省份改变后城市和地区联动
        }
    }

    private void updateLevel0() {
        int index = firstWheel.getCurrentItem();
        List<String> secData = firstData.get(firstList.get(index));
        if (secData != null && secData.size() > 0) {
            secWheel.setViewAdapter(new SimpleWheelAdapter(context, secData));
            secWheel.setCurrentItem(0);
        }
    }


    public void show(View v) {
        layoutParams.alpha = 0.6f;
        context.getWindow().setAttributes(layoutParams);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    public void setData(Map<String, List<String>> data) {
        this.firstData = data;
        bindData();
    }

    public void confirm() {
        if (listener != null) {
            int firstIndex = firstWheel.getCurrentItem();
            int secondIndex = secWheel.getCurrentItem();

            String firstStr = null, secStr = null;
            firstStr = firstList.get(firstIndex);
            secStr = Objects.requireNonNull(firstData.get(firstStr)).get(secondIndex);

            listener.onAddressChange(firstStr, secStr);
        }
        cancel();
    }

    public void cancel() {
        popupWindow.dismiss();
    }

    public void setListener(OnDataChoseListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel_button) {
            cancel();
        }
        if (i == R.id.confirm_button) {
            confirm();
        }
    }

    public interface OnDataChoseListener {
        void onAddressChange(String first, String sec);
    }
}
