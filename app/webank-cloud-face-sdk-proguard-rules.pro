######################云刷脸混淆规则 faceverify-BEGIN###########################
#不混淆内部类
-keepattributes InnerClasses

-keep public class com.webank.facelight.tools.WbCloudFaceVerifySdk{
    public <methods>;
    public static final *;
}
-keep public class com.webank.facelight.tools.WbCloudFaceVerifySdk$*{
    *;
}
-keep public class com.webank.record.**{
    public <methods>;
    public static final *;
}
-keep public class com.webank.facelight.ui.FaceVerifyStatus{

}
-keep public class com.webank.facelight.ui.FaceVerifyStatus$Mode{
    *;
}
-keep public class com.webank.facelight.tools.IdentifyCardValidate{
    public <methods>;
}

#================数据上报混淆规则 start===========================
#实体类
-keep class com.webank.facelight.wbanalytics.EventSender{
    *;
}
-keep class com.webank.facelight.wbanalytics.EventSender$*{
    *;
}
-keep class com.webank.facelight.wbanalytics.WBAEvent{
    *;
}

#---------对外接口---------
-keep class com.webank.facelight.wbanalytics.WBAnalyticsService{
    public <methods>;
}
-keepparameternames

#其他配置类
-keep class com.webank.facelight.wbanalytics.WBAnalyticsConfig{
    public <methods>;
}
#异常类型
-keep class com.webank.facelight.wbanalytics.WBASDKException

#================数据上报混淆规则 end===========================

-keep public class com.tencent.youtulivecheck.**{
    *;
}
-keep public class com.webank.facelight.contants.**{
    *;
}
-keep public class com.webank.facelight.listerners.**{
    *;
}
-keep public class com.webank.facelight.Request.*$*{
    *;
}
-keep public class com.webank.facelight.Request.*{
    *;
}
-keep public class com.webank.facelight.config.FaceVerifyConfig {
    public <methods>;
}

-keep public class com.webank.mbank.securecheck.CheckCallback{
    *;
}

-keep public class com.webank.mbank.securecheck.EmulatorCheck{
    public <methods>;
}

-keep class com.tencent.youtuface.**{
    *;
}

-keep class com.tencent.youtulivecheck.**{
    *;
}

-keep class com.tencent.youtufacetrack.**{
    *;
}

-keep class com.tencent.youtufacelive.model.**{
    *;
}

-keep class com.tencent.youtufacelive.tools.FileUtils{
    public <methods>;
}

-keep class com.tencent.youtufacelive.tools.YTUtils{
    public <methods>;
}

-keep class com.tencent.youtufacelive.tools.YTFaceLiveLogger{
    public <methods>;
}

-keep class com.tencent.youtufacelive.tools.YTFaceLiveLogger$IFaceLiveLogger{
     *;
}

-keep class com.tencent.youtufacelive.IYTMaskStateListener{
    *;
}

-keep class com.tencent.youtufacelive.YTPreviewHandlerThread{
    public static *;
    public <methods>;
}

-keep class com.tencent.youtufacelive.YTPreviewHandlerThread$IUploadListener{
     *;
}

-keep class com.tencent.youtufacelive.YTPreviewHandlerThread$ISetCameraParameterListener{
     *;
}

-keep class com.tencent.youtufacelive.YTPreviewMask{
    public <methods>;
}

-keep class com.tencent.youtufacelive.YTPreviewMask$TickCallback{
    *;
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}

#-----权限库混淆---------
-keeppackagenames com.webank.mbank.permission_request

#######################云刷脸混淆规则 faceverify-END#############################


