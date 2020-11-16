package com.wwsl.mdsj.views;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wwsl.mdsj.R;


/**
 * @author :
 * @date : 2020/6/17 17:37
 * @description : BottomSheetDialog
 */
public class BaseBottomSheetDialog extends BottomSheetDialogFragment {
    private FrameLayout bottomSheet;
    public BottomSheetBehavior<FrameLayout> behavior;

    @Override
    public void onStart() {
        super.onStart();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        assert dialog != null;
        dialog.setCanceledOnTouchOutside(true);
        bottomSheet = dialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getHeight();
            bottomSheet.setLayoutParams(layoutParams);
            behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getHeight());
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        setCancelable(true);
    }

    protected int getHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

}
