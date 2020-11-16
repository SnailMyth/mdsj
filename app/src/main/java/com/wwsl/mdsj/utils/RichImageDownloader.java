package com.wwsl.mdsj.utils;

import com.lzy.okgo.OkGo;
import com.zzhoujay.richtext.callback.BitmapStream;
import com.zzhoujay.richtext.ig.ImageDownloader;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by zhou on 2017/9/11.
 * 使用OkHttp实现的图片下载器
 */

public class RichImageDownloader implements ImageDownloader {

    @Override
    public BitmapStream download(String source) throws IOException {
        return new BitmapStreamWrapper(source);
    }

    private static class BitmapStreamWrapper implements BitmapStream {

        private final String url;
        private Response response;
        private InputStream inputStream;

        private BitmapStreamWrapper(String url) {
            this.url = url;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            response = OkGo.get(url).execute();
            assert response.body() != null;
            inputStream = response.body().byteStream();
            return inputStream;
        }

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
            }
            if (response != null) {
                response.close();
            }
        }
    }
}
