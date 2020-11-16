package com.wwsl.mdsj.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.wwsl.mdsj.interfaces.KeyBoardHeightChangeListener;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2018/10/27.
 * 获取键盘高度的工具类
 */

public class KeyBoardHeightUtil extends PopupWindow implements ViewTreeObserver.OnGlobalLayoutListener {

    private final static String TAG = "KeyBoardHeightUtil";
    private View mParentView;
    private View mContentView;
    private Rect mRect;
    private int mScreenHeight;
    private int mScreenStatusHeight;
    private int mLastHeight;
    private KeyBoardHeightChangeListener mKeyBoardChangeListener;
    private boolean mSoftInputShowed;

    public KeyBoardHeightUtil(Context context, View parentView, KeyBoardHeightChangeListener listener) {
        super(context);
        mParentView = parentView;
        mContentView = new View(context);
        setContentView(mContentView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setWidth(0);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable());
        mRect = new Rect();
        ScreenDimenUtil util = ScreenDimenUtil.getInstance();
        mScreenHeight = util.getScreenHeight();
        mScreenStatusHeight = util.getStatusBarHeight();
        L.e(TAG, "---屏幕高度--->" + mScreenHeight);
        L.e(TAG, "---状态栏高度--->" + mScreenStatusHeight);
        mKeyBoardChangeListener = new WeakReference<>(listener).get();
    }


    @Override
    public void onGlobalLayout() {
        if (mContentView != null && mRect != null) {
            mContentView.getWindowVisibleDisplayFrame(mRect);
            int visibleHeight = mRect.height() + mScreenStatusHeight;
            if (visibleHeight > mScreenHeight) {
                mScreenHeight = visibleHeight;
            }
            if (mLastHeight != visibleHeight) {
                mLastHeight = visibleHeight;
                if (mKeyBoardChangeListener != null) {
                    int keyboardHeight = mScreenHeight - visibleHeight;
                    if (keyboardHeight < 0) {
                        keyboardHeight = 0;
                    }
                    L.e(TAG, "-------可视区高度----->" + visibleHeight);
                    L.e(TAG, "-------键盘高度----->" + keyboardHeight);
                    mSoftInputShowed = keyboardHeight > 0;
                    mKeyBoardChangeListener.onKeyBoardHeightChanged(visibleHeight, keyboardHeight);
                }
            }
        }
    }


    /**
     * 添加布局变化的监听器
     */
    public void start() {
        if (mContentView != null) {
            if (!isShowing() && mParentView.getWindowToken() != null) {
                showAtLocation(mParentView, Gravity.NO_GRAVITY, 0, 0);
            }
            mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            L.e(TAG, "-------添加键盘监听--->");
        }
    }


    /**
     * 移除布局变化的监听器
     */
    public void release() {
        if (mContentView != null) {
            mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        dismiss();
        mParentView = null;
        mRect = null;
        mKeyBoardChangeListener = null;
        L.e(TAG, "-------移除键盘监听--->");
    }


    public boolean isSoftInputShowed() {
        return mSoftInputShowed;
    }
}
