package com.wwsl.mdsj.utils;

import com.wwsl.mdsj.AppConfig;

import java.io.File;
import java.util.Random;

public class PictureEditUtil {
    private Random mRandom;
    private static PictureEditUtil sInstance;

    private PictureEditUtil() {
        mRandom = new Random();
    }

    public static PictureEditUtil getInstance() {
        if (sInstance == null) {
            synchronized (PictureEditUtil.class) {
                if (sInstance == null) {
                    sInstance = new PictureEditUtil();
                }
            }
        }
        return sInstance;
    }

    /**
     * 设置拍照输出路径
     */
    public String generatePictureOutputPath() {
        String outputDir = AppConfig.CAMERA_IMAGE_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String pictureName = DateFormatUtil.getVideoCurTimeString() + mRandom.nextInt(9999);
        return outputDir + "android_" + AppConfig.getInstance().getUid() + "_" + pictureName + ".jpg";
    }
}
