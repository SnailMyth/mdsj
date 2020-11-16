# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/macpro/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the groupLast number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the groupLast number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

##中文混淆
#-classobfuscationdictionary ./proguard-keys.txt
#-packageobfuscationdictionary ./proguard-keys.txt
#-obfuscationdictionary ./proguard-keys.txt

-keep class com.simple.spiderman.** { *; }
-keepnames class com.simple.spiderman.** { *; }
-keep public class * extends android.app.Activity
-keep class * implements Android.os.Parcelable {
    public static final Android.os.Parcelable$Creator *;
}

# androidx
-keep public class * extends androidx.annotation.** { *; }
-keep public class * extends androidx.core.content.FileProvider

#UMeng
-keep class com.umeng.** {*;}
-keep class com.uc.** {*;}


#JMessage
-dontoptimize
-dontpreverify
-keepattributes  EnclosingMethod,Signature
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn cn.jmessage.**
-keep class cn.jmessage.**{ *; }

-keepclassmembers class ** {
    public void onEvent*(**);
}

#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
-keep class com.google.protobuf.** {*;}

-include webank-cloud-normal-rules.pro
-include webank-cloud-face-rules.pro

#=======================众简广告===============================
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}