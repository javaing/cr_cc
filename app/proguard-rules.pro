# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:


# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-dontwarn kotlin.**
#ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

-keep class android.support.**{*;}

-keep class com.google.android.material.** { *; }

-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

############retrofit
# exclusions for Okio
-dontwarn okio.**

# exclusions for Retrofit
-dontnote retrofit2.Platform
-keepattributes Signature
-keepattributes Exceptions

# exclusions for RxJava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-dontwarn sun.misc.Unsafe
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
###############glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-date@value=GlideModule


#######################permission
-dontwarn com.yanzhenjie.permission.**


####################vLayout
-keepattributes InnerClasses

#####################bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }

#################友盟 uApp

#-libraryjars /libs/IMKFSDK-2.8.0.jar
-keep class com.umeng.** {*;}
-keep class org.android.**{*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.aliee.quei.mo.R$*{
    public static final int *;
}
    ###############umeng push
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*

#######################umeng share
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontnote com.umeng.**
-dontwarn com.tencent.**
-dontnote com.tencent.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-keep class com.sina.weibo.**{*;}
-keep class com.tencent.**{*;}

################################
-keep class com.aliee.quei.mo.data.**{*;}
-keepclasseswithmembernames class com.hkzy.data.bean.**{*;}
-keep class com.hkzy.database.bean.**{*;}

-dontwarn com.tmall.**
-keep class com.tamll.**{*;}
-dontwarn dagger.android.**

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-dontnote org.androidannotaions.api.**
-dontwarn org.androidannotaions.api.**
-dontnote org.springframework.**
-dontwarn org.springframework.**
-keep class com.aliee.quei.mo.di.**{*;}
-keep class com.aliee.quei.mo.base.request.**{*;}
-keep class com.aliee.quei.mo.utils.**
-keep class * extends io.realm.RealmObject{*;}

######################小米推送 ，华为推送，魅族推送
-keep class com.xiaomi.**{*;}
-keep class org.android.agoo.**{*;}
-keep class org.apache.thrift.**{*;}

-keep class com.huawei.**{*;}
-keep class com.meizu.**{*;}
-keep class umeng.message.**{*;}

-dontwarn android.arch.**

-dontwarn com.moor.imkf.**
-dontwarn com.m7.**
-keep class com.moor.imkf.** { *; }
-keep class com.m7.** {*;}


-keep class com.just.agentweb.** {
    *;
}

# Addidional for x5.sdk classes for apps

-keep class com.tencent.smtt.export.external.**{
    *;
}




-keepattributes InnerClasses

-keep public enum com.tencent.smtt.sdk.WebSettings$** {
    *;
}

-keep public enum com.tencent.smtt.sdk.QbSdk$** {
    *;
}


-keepattributes Signature

# 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
	public protected *;
}

# 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
	public protected *;
}



-keep public class com.tencent.smtt.sdk.Tbs* {
	public <fields>;
	public <methods>;
}

-keep class com.tencent.smtt.** {
	*;
}
# end


-dontwarn com.just.agentweb.**

-dontwarn com.alipay.**

-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ut.device.** { *;}

-dontwarn com.flyco.**
# Addidional for x5.sdk classes for apps

-keep class com.tencent.smtt.export.external.**{
    *;
}


-keepattributes InnerClasses

-keep public enum com.tencent.smtt.sdk.WebSettings$** {
    *;
}

-keep public enum com.tencent.smtt.sdk.QbSdk$** {
    *;
}

# 1. extension interfaces should be apparent
-keep public class com.tencent.smtt.export.external.extension.interfaces.* {
	public protected *;
}

# 2. interfaces should be apparent
-keep public class com.tencent.smtt.export.external.interfaces.* {
	public protected *;
}


-keep public class com.tencent.smtt.sdk.Tbs* {
	public <fields>;
	public <methods>;
}


-keep class com.tencent.smtt.** {
	*;
}
# end


#arouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
# If you use the byType method to obtain Service, add the following rules to protect the interface:
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# If single-type injection is used, that is, no interface is defined to implement IProvider, the following rules need to be added to protect the implementation
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider


-keep public class * extends android.app.Fragment

#Sophix
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-dontwarn com.alibaba.sdk.android.utils.**
#防止inline
-dontoptimize

-keepclassmembers class com.aliee.quei.mo.application.ReaderApplication {
    public <init>();
}


#JPUSH
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-assumenosideeffects class android.util.Log {
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** e(...);
}

-keep class com.dueeeke.videoplayer.** { *; }
-dontwarn com.dueeeke.videoplayer.**

# IjkPlayer
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

#自定义混淆
-obfuscationdictionary dic1.txt
-classobfuscationdictionary dic1.txt
-packageobfuscationdictionary dic1.txt