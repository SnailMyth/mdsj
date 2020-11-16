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

import java.util.List;


public class OneChooseWheel implements MyOnWheelChangedListener, View.OnClickListener {

    private Button cancelButton;
    private Button confirmButton;
    private TextView txTitle;
    private String title;
    private MyWheelView firstWheel;
    private Activity context;
    private View parentView;
    private PopupWindow popupWindow = null;
    private WindowManager.LayoutParams layoutParams = null;
    private LayoutInflater layoutInflater = null;


    private List<String> firstData = null;

    private OnDataChoseListener listener = null;

    public OneChooseWheel(Activity context, String title) {
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
        parentView = layoutInflater.inflate(R.layout.dialog_choose_one_layout, null);
        txTitle = parentView.findViewById(R.id.title);
        txTitle.setText(title);
        firstWheel = parentView.findViewById(R.id.level_0);
        cancelButton = parentView.findViewById(R.id.cancel_button);
        confirmButton = parentView.findViewById(R.id.confirm_button);

        firstWheel.setVisibleItems(5);

        firstWheel.addChangingListener(this);
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
        firstWheel.setViewAdapter(new SimpleWheelAdapter(context, firstData));
    }

    @Override
    public void onChanged(MyWheelView wheel, int oldValue, int newValue) {

    }

    public void show(View v) {
        layoutParams.alpha = 0.6f;
        context.getWindow().setAttributes(layoutParams);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    public void setData(List<String> data) {
        this.firstData = data;
        bindData();
    }

    public void confirm() {
        if (listener != null) {
            int firstIndex = firstWheel.getCurrentItem();

            String firstStr = null, secStr = null;
            firstStr = firstData.get(firstIndex);
            listener.onAddressChange(firstStr);
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
        void onAddressChange(String first);
    }
}
