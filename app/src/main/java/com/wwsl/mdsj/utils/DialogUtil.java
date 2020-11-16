package com.wwsl.mdsj.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bumptech.glide.Glide;
import com.contrarywind.view.WheelView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.PopupAnimator;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.bean.LocalOccupationBean;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.bean.VideoCommentBean;
import com.wwsl.mdsj.dialog.AtFriendDialog;
import com.wwsl.mdsj.dialog.AuthPayDialog;
import com.wwsl.mdsj.dialog.HometownSetDialog;
import com.wwsl.mdsj.dialog.ProgressDialog;
import com.wwsl.mdsj.dialog.RedPacketDialog;
import com.wwsl.mdsj.dialog.SimpleCenterDialog;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.wwsl.mdsj.views.dialog.OneChooseWheel;
import com.wwsl.mdsj.views.dialog.TwoChooseWheel;
import com.wwsl.mdsj.views.dialog.address.ChooseAddressWheel;
import com.wwsl.mdsj.views.dialog.address.OnAddressChangeListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;


/**
 * Created by cxf on 2017/8/8.
 */

public class DialogUtil {
    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_NUMBER = 1;
    public static final int INPUT_TYPE_NUMBER_PASSWORD = 2;
    public static final int INPUT_TYPE_TEXT_PASSWORD = 3;

    //第三方登录的时候用显示的dialog
    public static Dialog loginAuthDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_login_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 用于网络请求等耗时操作的LoadingDialog
     */
    public static Dialog loadingDialog(Context context, String text, boolean cancelable) {
        Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        if (!TextUtils.isEmpty(text)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.text);
            if (titleView != null) {
                titleView.setText(text);
            }
        }
        return dialog;
    }

    /**
     * 用于网络请求等耗时操作的ProgressDialog
     */
    public static ProgressDialog progressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_loading_2);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog loadingDialog(Context context, boolean cancelable) {
        return loadingDialog(context, "", cancelable);
    }

    public static void showSimpleTipDialog(Context context, String content) {
        showSimpleTipDialog(context, null, content);
    }

    public static void showSimpleTipDialog(Context context, String title, String content) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_simple_tip);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(title)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            titleView.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            TextView contentTextView = (TextView) dialog.findViewById(R.id.content);
            contentTextView.setText(content);
        }
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSimpleDialog(Context context, String content, SimpleCallback callback) {
        showSimpleDialog(context, content, true, callback);
    }

    public static void showSimpleDialog(Context context, String content, boolean cancelable, SimpleCallback callback) {
        showSimpleDialog(context, null, content, cancelable, callback);
    }

    public static void showSimpleDialog(Context context, String title, String content, boolean cancelable, SimpleCallback callback) {
        new Builder(context)
                .setTitle(title)
                .setContent(content)
                .setCancelable(cancelable)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, String content, String hint, int inputType, int length, int cancelTextColor, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setContent(content)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setCancelTextColor(cancelTextColor)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, String hint, int inputType, int length, int cancelTextColor, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setCancelTextColor(cancelTextColor)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, int length, int cancelTextColor, SimpleCallback callback) {
        showSimpleInputDialog(context, title, null, inputType, length, cancelTextColor, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, String hint, int inputType, int length, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, int length, SimpleCallback callback) {
        showSimpleInputDialog(context, title, null, inputType, length, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, SimpleCallback callback) {
        showSimpleInputDialog(context, title, inputType, 0, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, SimpleCallback callback) {
        showSimpleInputDialog(context, title, INPUT_TYPE_TEXT, callback);
    }

    public static void showSimpleInputDialogWithoutCancel(Context context, String title, String hint, int inputType, int length, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(false)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setClickCallback(callback)
                .hideCancel(true)
                .build()
                .show();
    }

    public static void showAdvertiseDialog(Context context, final SimpleConfirmCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_ads);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        assert window != null;
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        dialog.findViewById(R.id.ad_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onConfirmClick();
            }
        });

        dialog.show();
    }

    public static void showRecommendDialog(Context context, final IntCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_recommend);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        assert window != null;
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        dialog.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onConfirmClick(1);
            }
        });

        dialog.findViewById(R.id.ad_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showSysTipsDialog(Context context, final SimpleCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_sys_tips);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        assert window != null;
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        ((TextView) dialog.findViewById(R.id.txContent)).setText(AppConfig.getInstance().getConfig().getSysTips());
        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onConfirmClick(dialog, "");
            }
        });

        dialog.show();
    }

    public static void showContentTipsDialog(Context context, String title, String content, final SimpleCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_content_tips);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        assert window != null;
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        if (StrUtil.isEmpty(title)) {
            title = "提示";
        }
        ((TextView) dialog.findViewById(R.id.txContent)).setText(content);
        ((TextView) dialog.findViewById(R.id.txTitle)).setText(title);
        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onConfirmClick(dialog, "");
            }
        });

        dialog.show();
    }

    public static void showStringArrayDialog(Context context, Integer[] array, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        for (int i = 0, length = array.length; i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(54)));
            textView.setTextColor(0xffffffff);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setText(array[i]);
            textView.setBackgroundColor(Color.parseColor("#1E222D"));
            textView.setTag(array[i]);
            textView.setOnClickListener(itemListener);
            container.addView(textView);
//            if (i != length - 1) {
//                View v = new View(context);
//                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(1)));
//                v.setBackgroundColor(0xffffffff);
//                container.addView(v);
//            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showStringArrayDialog(Context context, SparseArray<String> array, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        for (int i = 0, length = array.size(); i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(54)));
            textView.setTextColor(0xff323232);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setText(array.valueAt(i));
            textView.setTag(array.keyAt(i));
            textView.setOnClickListener(itemListener);
            container.addView(textView);
            if (i != length - 1) {
                View v = new View(context);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(1)));
                v.setBackgroundColor(0xfff5f5f5);
                container.addView(v);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSexPickerDialog(Context context, int sex, final IntCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.BottomDialogStyle);
        dialog.setContentView(R.layout.dialog_sex_picker);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        final WheelView wheelView = dialog.findViewById(R.id.wheelview);
        wheelView.setCyclic(false);
        wheelView.setTextColorCenter(Color.WHITE);
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("保密");
        mOptionsItems.add("男");
        mOptionsItems.add("女");
        wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
        if (sex == 1) {
            wheelView.setCurrentItem(0);
        } else {
            wheelView.setCurrentItem(1);
        }

        dialog.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onConfirmClick(wheelView.getCurrentItem());
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static class Builder {

        private Context mContext;
        private String mTitle;
        private String mContent;
        private String mConfrimString;
        private String mCancelString;
        private boolean mCancelable;
        private boolean mBackgroundDimEnabled;//显示区域以外是否使用黑色半透明背景
        private boolean mInput;//是否是输入框的
        private String mHint;
        private int mInputType;
        private int mLength;
        private SimpleCallback mClickCallback;
        private int mCancelTextColor;
        private boolean mHideCancel;//隐藏取消按钮

        public Builder(Context context) {
            mContext = context;
        }

        public Builder hideCancel(boolean hideCancel) {
            mHideCancel = hideCancel;
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setContent(String content) {
            mContent = content;
            return this;
        }

        public Builder setConfrimString(String confrimString) {
            mConfrimString = confrimString;
            return this;
        }

        public Builder setCancelString(String cancelString) {
            mCancelString = cancelString;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setCancelTextColor(int cancelTextColor) {
            mCancelTextColor = cancelTextColor;
            return this;
        }

        public Builder setBackgroundDimEnabled(boolean backgroundDimEnabled) {
            mBackgroundDimEnabled = backgroundDimEnabled;
            return this;
        }

        public Builder setInput(boolean input) {
            mInput = input;
            return this;
        }

        public Builder setHint(String hint) {
            mHint = hint;
            return this;
        }

        public Builder setInputType(int inputType) {
            mInputType = inputType;
            return this;
        }

        public Builder setLength(int length) {
            mLength = length;
            return this;
        }

        public Builder setClickCallback(SimpleCallback clickCallback) {
            mClickCallback = clickCallback;
            return this;
        }

        public Dialog build() {
            final Dialog dialog = new Dialog(mContext, mBackgroundDimEnabled ? R.style.dialog : R.style.dialog2);
            dialog.setContentView(mInput ? R.layout.dialog_input : R.layout.dialog_simple);
            dialog.setCancelable(mCancelable);
            dialog.setCanceledOnTouchOutside(mCancelable);
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            if (!TextUtils.isEmpty(mTitle)) {
                titleView.setText(mTitle);
            }
            final TextView content = (TextView) dialog.findViewById(R.id.content);
            if (!TextUtils.isEmpty(mHint)) {
                content.setHint(mHint);
            }
            if (!TextUtils.isEmpty(mContent)) {
                content.setText(mContent);
                CharSequence text = content.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }
            if (mInputType == INPUT_TYPE_NUMBER) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (mInputType == INPUT_TYPE_NUMBER_PASSWORD) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            } else if (mInputType == INPUT_TYPE_TEXT_PASSWORD) {
                content.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            if (mLength > 0 && content instanceof EditText) {
                content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLength)});
            }
            TextView btnConfirm = (TextView) dialog.findViewById(R.id.btn_confirm);
            if (!TextUtils.isEmpty(mConfrimString)) {
                btnConfirm.setText(mConfrimString);
            }
            TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
            if (mCancelTextColor != 0) {
                btnCancel.setTextColor(mCancelTextColor);
            }
            if (!TextUtils.isEmpty(mCancelString)) {
                btnCancel.setText(mCancelString);
            }
            if (mHideCancel) {
                btnCancel.setVisibility(View.GONE);
                dialog.findViewById(R.id.viewDialogCancelDivider).setVisibility(View.GONE);
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_confirm) {
                        if (mClickCallback != null) {
                            if (mInput) {
                                mClickCallback.onConfirmClick(dialog, content.getText().toString());
                            } else {
                                dialog.dismiss();
                                mClickCallback.onConfirmClick(dialog, "");
                            }
                        } else {
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                        if (mClickCallback instanceof SimpleCallback2) {
                            ((SimpleCallback2) mClickCallback).onCancelClick();
                        }
                    }
                }
            };
            btnConfirm.setOnClickListener(listener);
            btnCancel.setOnClickListener(listener);
            return dialog;
        }

    }

    public interface DataPickerCallback {
        void onConfirmClick(String date);
    }

    public interface StringArrayDialogCallback {
        void onItemClick(String text, int tag);
    }

    public interface SimpleCallback {
        void onConfirmClick(Dialog dialog, String content);
    }

    public interface SimpleConfirmCallback {
        void onConfirmClick();
    }

    public interface SimpleCallback2 extends SimpleCallback {
        void onCancelClick();
    }

    public interface IntCallback {
        void onConfirmClick(int sex);
    }


//    public static ChooseAddressWheel getCityDetailDialog(Activity context, OnAddressChangeListener listener) {
//        ChooseAddressWheel chooseAddressWheel = new ChooseAddressWheel(context);
//        chooseAddressWheel.setOnAddressChangeListener(listener);
//
//        String addressTex = CommonUtil.readAssert(context, "address.json", null);
//        addressTex = StringUtil.replaceBank(addressTex);
//        AddressModel model = JsonUtil.parseJson(addressTex, AddressModel.class);
//        if (model != null) {
//            AddressDetailsEntity data = model.Result;
//            if (data == null) return null;
//            if (data.ProvinceItems != null && data.ProvinceItems.Province != null) {
//                chooseAddressWheel.setProvince(data.ProvinceItems.Province);
//                chooseAddressWheel.defaultValue(data.Province, data.City, data.Area);
//            }
//        }
//        return chooseAddressWheel;
//    }

    public static ChooseAddressWheel getCityDetailDialog(Activity context, List<PartnerCityBean> cityBeans, OnAddressChangeListener listener) {
        ChooseAddressWheel chooseAddressWheel = new ChooseAddressWheel(context);
        chooseAddressWheel.setOnAddressChangeListener(listener);
        chooseAddressWheel.setProvince(cityBeans);
        return chooseAddressWheel;
    }

    public static TwoChooseWheel getProvinceDialog(Activity activity, TwoChooseWheel.OnDataChoseListener listener) {
        TwoChooseWheel chooseWheel = new TwoChooseWheel(activity, "请选择省市");
        chooseWheel.setListener(listener);
        String occupationTex = CommonUtil.readAssert(activity, "address1.json", null);
        occupationTex = StringUtil.replaceBank(occupationTex);
        LocalOccupationBean bean = JSON.parseObject(occupationTex, LocalOccupationBean.class);
        if (null != bean) {
            chooseWheel.setData(bean.parseData());
        }
        return chooseWheel;
    }

    public static TwoChooseWheel getProvinceCityDialog(Activity activity, String title, Map<String, List<String>> map, TwoChooseWheel.OnDataChoseListener listener) {
        TwoChooseWheel chooseWheel = new TwoChooseWheel(activity, title);
        chooseWheel.setListener(listener);
        chooseWheel.setData(map);
        return chooseWheel;
    }

    public static OneChooseWheel getSingleCityDialog(Activity activity, String title, List<String> data, OneChooseWheel.OnDataChoseListener listener) {
        OneChooseWheel chooseWheel = new OneChooseWheel(activity, title);
        chooseWheel.setListener(listener);
        chooseWheel.setData(data);
        return chooseWheel;
    }

    public static DialogAnimator getRotateAnimator() {
        return new DialogAnimator();
    }

    /**
     * 图片,文字 确认按钮 dialog
     */
    public static SimpleCenterDialog getSimpleCenterDialog(Context context, int imgRes, int imgWith, int imgHeight, String content) {
        return (SimpleCenterDialog) new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new SimpleCenterDialog(context, imgRes, imgWith, imgHeight, content));
    }

    /**
     * 图片,文字 确认按钮 dialog
     */
    public static SimpleCenterDialog getSimpleCenterDialog(Context context, String imgRes, int imgWith, int imgHeight, String content) {
        return (SimpleCenterDialog) new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new SimpleCenterDialog(context, imgRes, imgWith, imgHeight, content));
    }

    /**
     * 图片,文字 确认按钮 dialog
     */
    public static SimpleCenterDialog getSimpleCenterDialog(Context context, String imgRes, int imgWith, int imgHeight,
                                                           int marginTop, int marginBottom, int marginStart, int marginEnd, String content) {
        return (SimpleCenterDialog) new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new SimpleCenterDialog(context, imgRes, imgWith, imgHeight, marginTop, marginBottom, marginStart, marginEnd, content, null));
    }

    /**
     * 认证支付弹窗
     */
    public static void showAuthPayDialog(Context context, String pirce, OnDialogCallBackListener listener) {
        new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new AuthPayDialog(context, pirce, listener))
                .show();
    }

    /**
     * 图片,文字 确认按钮 dialog
     */
    public static void showRedPacketDialog(@NonNull Context context, boolean isSelfSend, String packetId, String price, String status, OnDialogCallBackListener listener) {
        new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new RedPacketDialog(context, isSelfSend, packetId, price, status, listener))
                .show();
    }

    /**
     * 图片,文字 确认按钮 dialog
     */
    public static AtFriendDialog getAtFriendDialog(Context context, String uid, OnDialogCallBackListener listener) {
        return (AtFriendDialog) new XPopup.Builder(context)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new AtFriendDialog(context, uid, listener));
    }


    /**
     * 图片,文字 确认按钮 dialog
     */
    public static HometownSetDialog getHometownSetDialog(Activity activity, OnDialogCallBackListener listener) {
        return (HometownSetDialog) new XPopup.Builder(activity)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogAnimator())
                .asCustom(new HometownSetDialog(activity, activity, listener));
    }

    /**
     * 图片,文字 确认按钮 dialog
     */
    public static void showInputInviteDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_input_invate);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            EditText etCode = dialog.findViewById(R.id.etInvitedCode);
            String content = etCode.getText().toString().trim();
            HttpUtil.setInvitation(content, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        AppConfig.getInstance().getUserBean().setIsHaveCode("1");
                        ToastUtil.show("绑定成功");
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        });

        dialog.show();
    }

    public static class DialogAnimator extends PopupAnimator {
        @Override
        public void initAnimator() {
            targetView.setScaleX(1);
            targetView.setScaleY(1);
            targetView.setTranslationY(targetView.getHeight());
            targetView.setAlpha(0);
            targetView.setRotation(0);
        }

        @Override
        public void animateShow() {
            targetView.animate().alpha(1).translationY(0).setInterpolator(new FastOutSlowInInterpolator()).setDuration(500).start();
        }

        @Override
        public void animateDismiss() {
            targetView.animate().alpha(0).translationY(targetView.getHeight()).setInterpolator(new FastOutSlowInInterpolator()).setDuration(500).start();
        }
    }

    /**
     * 删除评论
     */
    public static void delComment(Context context, VideoCommentBean commentBean, VideoBean videoBean, OnDialogCallBackListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_comment_del);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        LinearLayout vDel = dialog.findViewById(R.id.click_del);
        try {
            String uid = AppConfig.getInstance().getUid();//登录用户的id
            String id1 = videoBean.getUserBean().getId();//视频所属用户id
            String id = commentBean.getUserBean().getId();//评论所属用户id
            if (uid.equals(id1)) {
                //是自己的短视频
                vDel.setVisibility(View.VISIBLE);
            } else {
                //是自己的评论
                vDel.setVisibility(uid.equals(id) ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.tvCopy).setOnClickListener(v -> {
            try {
                CommonUtil.copyText(context, commentBean.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        vDel.setOnClickListener(v -> {
            String video_id;
            String comment_id;
            try {
                video_id = videoBean.getId();
                comment_id = commentBean.getId();
            } catch (Exception e) {
                dialog.dismiss();
                return;
            }
            HttpUtil.deleteComment(video_id, comment_id, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    ToastUtil.show(msg);
                    if (code == 0) {
                        //0删除成功 不是0删除失败
                        listener.onDialogViewClick(null, code);
                    }
                    dialog.dismiss();
                }
            });
        });

        //宽度不能自适应处理
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }

    /**
     * 首页推广图片
     */
    public static void showPromoteHome(Activity activity, JSONObject object) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_main_promote);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        ImageView img_ad = dialog.findViewById(R.id.img_ad);
//        Glide.with(activity).load(object.optString("image")).error(R.mipmap.img_load_fail).into(img_ad);
        Glide.with(activity).load(object.optString("image")).into(img_ad);
        dialog.findViewById(R.id.ivClose).setOnClickListener(v -> dialog.dismiss());
        img_ad.setOnClickListener(v -> {
            dialog.dismiss();
            WebViewActivity.forward3(activity, object.optString("url"), 2);
        });

        //宽度不能自适应处理
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 家族推广图片
     */
    public static void showPromoteAtFamily(Activity activity, JSONObject object) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_family_promote);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        ImageView img_ad = dialog.findViewById(R.id.img_ad);
//        Glide.with(activity).load(object.optString("image")).error(R.mipmap.img_load_fail).into(img_ad);
        Glide.with(activity).load(object.optString("image")).into(img_ad);
        dialog.findViewById(R.id.ivClose).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.receive).setOnClickListener(v -> {
            dialog.dismiss();
            String url = object.optString("url");
            AppConfig.getInstance().setTgUrl(url);
            WebViewActivity.forward3(activity, url, 99);
        });

        //宽度不能自适应处理
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

}
