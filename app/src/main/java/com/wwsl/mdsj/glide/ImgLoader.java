package com.wwsl.mdsj.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.R;

import java.io.File;

import jp.wasabeef.glide.transformations.BitmapTransformation;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader implements XPopupImageLoader {
    private static RequestManager sManager;

    static {
        sManager = GlideApp.with(AppContext.sInstance);
    }

    /**
     * 显示用户头像
     */
    public static void displayAvatar(String url, ImageView imageView) {
        display(url, imageView, R.mipmap.icon_avatar_placeholder, R.mipmap.icon_avatar_placeholder, true, null, null, null);
    }

    /**
     * 显示用户头像
     */
    public static void displayAvatar(File file, ImageView imageView) {
        display(file, imageView, R.mipmap.icon_avatar_placeholder, R.mipmap.icon_avatar_placeholder, true, null, null, null);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(String videoPath, ImageView imageView) {
        display(Uri.fromFile(new File(videoPath)), imageView);
    }

    /**
     * 显示模糊的毛玻璃图片，radius取值1-25,值越大图片越模糊
     */
    public static void displayBlur(String url, ImageView imageView) {
        display(url, imageView, 0, 0, false, new BlurTransformation(10, 2), null, null);
    }


    public static void display(String url, int error, ImageView imageView) {
        display(url, imageView, 0, error, false, null, null, null);
    }

    public static void display(String url, int error, int placeholderRes, ImageView imageView) {
        display(url, imageView, placeholderRes, error, false, null, null, null);
    }

    public static void display(String url, ImageView imageView) {
        display(url, imageView, 0, 0, false, null, null, null);
    }

    public static void display(File file, ImageView imageView) {
        display(file, imageView, 0, 0, false, null, null, null);
    }

    public static void display(int res, ImageView imageView) {
        display(res, imageView, 0, 0, false, null, null, null);
    }

    public static void display(Uri uri, ImageView imageView) {
        display(uri, imageView, 0, R.mipmap.icon_default_image_mini, false, null, null, null);
    }

    public static void display(String url, ImageView imageView, int placeholderRes, int errorRes, boolean circleCrop, BitmapTransformation bitmapTransformation, RequestListener<Drawable> requestListener, SimpleTarget simpleTarget) {
        RequestOptions requestOptions = new RequestOptions();
        if (placeholderRes != 0) {
            requestOptions = requestOptions.placeholder(placeholderRes);
        }
        if (errorRes != 0) {
            requestOptions = requestOptions.error(errorRes);
        }
        if (circleCrop) {
            requestOptions = requestOptions.circleCrop();
        }
        if (bitmapTransformation != null) {
            requestOptions = requestOptions.transform(bitmapTransformation);
        }
        if (requestListener != null) {
            if (simpleTarget != null) {
                sManager.load(url).apply(requestOptions).listener(requestListener).into(simpleTarget);
            } else {
                sManager.load(url).apply(requestOptions).listener(requestListener).into(imageView);
            }
        } else {
            if (simpleTarget != null) {
                sManager.load(url).apply(requestOptions).into(simpleTarget);
            } else {
                sManager.load(url).apply(requestOptions).into(imageView);
            }
        }
    }

    public static void display(File file, ImageView imageView, int placeholderRes, int errorRes, boolean circleCrop, BitmapTransformation bitmapTransformation, RequestListener<Drawable> requestListener, SimpleTarget simpleTarget) {
        RequestOptions requestOptions = new RequestOptions();
        if (placeholderRes != 0) {
            requestOptions = requestOptions.placeholder(placeholderRes);
        }
        if (errorRes != 0) {
            requestOptions = requestOptions.error(errorRes);
        }
        if (circleCrop) {
            requestOptions = requestOptions.circleCrop();
        }
        if (bitmapTransformation != null) {
            requestOptions = requestOptions.transform(bitmapTransformation);
        }
        if (requestListener != null) {
            if (simpleTarget != null) {
                sManager.load(file).apply(requestOptions).listener(requestListener).into(simpleTarget);
            } else {
                sManager.load(file).apply(requestOptions).listener(requestListener).into(imageView);
            }
        } else {
            if (simpleTarget != null) {
                sManager.load(file).apply(requestOptions).into(simpleTarget);
            } else {
                sManager.load(file).apply(requestOptions).into(imageView);
            }
        }
    }

    public static void display(int res, ImageView imageView, int placeholderRes, int errorRes, boolean circleCrop, BitmapTransformation bitmapTransformation, RequestListener<Drawable> requestListener, SimpleTarget simpleTarget) {
        RequestOptions requestOptions = new RequestOptions();
        if (placeholderRes != 0) {
            requestOptions = requestOptions.placeholder(placeholderRes);
        }
        if (errorRes != 0) {
            requestOptions = requestOptions.error(errorRes);
        }
        if (circleCrop) {
            requestOptions = requestOptions.circleCrop();
        }
        if (bitmapTransformation != null) {
            requestOptions = requestOptions.transform(bitmapTransformation);
        }
        if (requestListener != null) {
            if (simpleTarget != null) {
                sManager.load(res).apply(requestOptions).listener(requestListener).into(simpleTarget);
            } else {
                sManager.load(res).apply(requestOptions).listener(requestListener).into(imageView);
            }
        } else {
            if (simpleTarget != null) {
                sManager.load(res).apply(requestOptions).into(simpleTarget);
            } else {
                sManager.load(res).apply(requestOptions).into(imageView);
            }
        }
    }

    public static void display(Uri uri, ImageView imageView, int placeholderRes, int errorRes, boolean circleCrop, BitmapTransformation bitmapTransformation, RequestListener<Drawable> requestListener, SimpleTarget simpleTarget) {
        RequestOptions requestOptions = new RequestOptions();
        if (placeholderRes != 0) {
            requestOptions = requestOptions.placeholder(placeholderRes);
        }
        if (errorRes != 0) {
            requestOptions = requestOptions.error(errorRes);
        }
        if (circleCrop) {
            requestOptions = requestOptions.circleCrop();
        }
        if (bitmapTransformation != null) {
            requestOptions = requestOptions.transform(bitmapTransformation);
        }
        if (requestListener != null) {
            if (simpleTarget != null) {
                sManager.load(uri).apply(requestOptions).listener(requestListener).into(simpleTarget);
            } else {
                sManager.load(uri).apply(requestOptions).listener(requestListener).into(imageView);
            }

        } else {
            if (simpleTarget != null) {
                sManager.load(uri).apply(requestOptions).into(simpleTarget);
            } else {
                sManager.load(uri).apply(requestOptions).into(imageView);
            }
        }
    }

    @Override
    public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
        Glide.with(imageView).load(uri).apply(new RequestOptions().placeholder(R.mipmap.icon_default_image_mini).override(Target.SIZE_ORIGINAL)).into(imageView);
    }

    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
