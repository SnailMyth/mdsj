package com.wwsl.mdsj.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.custom.ProgressTextView3;

/**
 * Created by cxf on 2018/12/1.
 */

public class ProgressDialog extends Dialog {

    private ProgressTextView3 mProgress;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public ProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

    }

    public ProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mProgress = findViewById(R.id.progress);
    }

    public void setProgress(int progress) {
        if (mProgress != null) {
            mProgress.setProgress(progress);
        }
    }

}
