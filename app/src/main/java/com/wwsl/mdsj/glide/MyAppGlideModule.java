package com.wwsl.mdsj.glide;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide,
                                   @NonNull Registry registry) {


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true) // 设置出现错误进行重新连接。
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS);

        if (Build.VERSION.SDK_INT < 29) {
            builder.sslSocketFactory(GlideHttpsUtil.getSSLSocketFactory());
        }
        builder.hostnameVerifier(GlideHttpsUtil.getHostnameVerifier());

        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(builder.build()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
