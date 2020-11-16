package com.wwsl.mdsj.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.frame.fire.util.LogUtils;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import cn.hutool.core.util.StrUtil;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CommonUtil {

    /**
     * 获取屏幕的高度px，不包括虚拟功能键功能
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * @param context
     * @param fileName
     * @param charset
     * @return 读取资源文件
     */
    public static String readAssert(Context context, String fileName, String charset) {
        String jsonString = "";
        String resultString = "";

        if (StrUtil.isEmpty(charset)) {
            charset = "utf-8";
        }
        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            resultString = new String(buffer, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    public static PictureParameterStyle getDefaultStyle(Context context) {
        // 相册主题
        PictureParameterStyle mPictureParameterStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        mPictureParameterStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        mPictureParameterStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        mPictureParameterStyle.isOpenCheckNumStyle = false;
        // 相册状态栏背景色
        mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#393a3e");
        // 相册列表标题栏背景色
        mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#393a3e");
        // 相册父容器背景色
        mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(context, R.color.black);
        // 相册列表标题栏右侧上拉箭头
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.picture_icon_back;
        // 标题栏字体颜色
        mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        mPictureParameterStyle.pictureCancelTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        // 选择相册目录背景样式
        mPictureParameterStyle.pictureAlbumStyle = R.drawable.picture_new_item_select_bg;
        // 相册列表勾选图片样式
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.picture_checkbox_selector;
        // 相册列表底部背景色
        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_grey);
        // 已选数量圆点背景样式
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        // 相册列表已完成色值(已完成 可点击色值)
        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_fa632d);
        // 相册列表未完成色值(请选择 不可点击色值)
        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(context, R.color.picture_color_white);
        // 预览界面底部背景色
        mPictureParameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(context, R.color.picture_color_grey);
        // 外部预览界面删除按钮样式
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalFontColor = ContextCompat.getColor(context, R.color.app_color_white);
        // 外部预览界面是否显示删除按钮
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        mPictureParameterStyle.pictureNavBarColor = Color.parseColor("#393a3e");
//        // 自定义相册右侧文本内容设置
//        mPictureParameterStyle.pictureRightDefaultText = "";
//        // 自定义相册未完成文本内容
//        mPictureParameterStyle.pictureUnCompleteText = "";
//        // 自定义相册完成文本内容
//        mPictureParameterStyle.pictureCompleteText = "";
//        // 自定义相册列表不可预览文字
//        mPictureParameterStyle.pictureUnPreviewText = "";
//        // 自定义相册列表预览文字
//        mPictureParameterStyle.picturePreviewText = "";
//
//        // 自定义相册标题字体大小
//        mPictureParameterStyle.pictureTitleTextSize = 18;
//        // 自定义相册右侧文字大小
//        mPictureParameterStyle.pictureRightTextSize = 14;
//        // 自定义相册预览文字大小
//        mPictureParameterStyle.picturePreviewTextSize = 14;
//        // 自定义相册完成文字大小
//        mPictureParameterStyle.pictureCompleteTextSize = 14;
//        // 自定义原图文字大小
//        mPictureParameterStyle.pictureOriginalTextSize = 14;

        return mPictureParameterStyle;
    }

    public static PictureCropParameterStyle getDefaultCropStyle(Context context) {

        return new PictureCropParameterStyle(
                ContextCompat.getColor(context, R.color.app_color_grey),
                ContextCompat.getColor(context, R.color.app_color_grey),
                Color.parseColor("#393a3e"),
                ContextCompat.getColor(context, R.color.app_color_white),
                true);
    }


    public static int getRandomInt(int bound) {
        return new Random().nextInt(bound);
    }

    public static View getEmptyView(String text, Context context, ViewGroup parent) {
        View emptyView = LayoutInflater.from(context).inflate(R.layout.view_no_data_text, parent, false);
        TextView textView = emptyView.findViewById(R.id.text);
        if (StrUtil.isEmpty(text)) text = "暂无数据";
        textView.setText(text);
        return emptyView;
    }

    public static void copyText(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", str);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }

    public static String getTickTimeStr() {
        String time = "";
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH", Locale.CHINA);
        time = sdf.format(ca.getTime());
        LogUtils.e(TAG, "getTickTimeStr: " + time);
        return time;
    }

    private final static String TAG = "CommonUtil";
}
