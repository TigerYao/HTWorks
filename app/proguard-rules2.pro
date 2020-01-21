# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ZDD/Library/Android/sdk/tools/proguard/proguard-android.txt
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

#https://www.jianshu.com/p/48d6bce58e47
#https://blog.csdn.net/yuzhiqiang_1993/article/details/80676170

#
#-------------------------------------------基本不用动区域----------------------------------------------
#
#
# -----------------------------基本 -----------------------------
#

# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不进行优化，建议使用此选项，
-dontoptimize
 # 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify


# 屏蔽警告
-ignorewarnings
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*
# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
 #优化时允许访问并修改有修饰符的类和类的成员，这可以提高优化步骤的结果。
# 比如，当内联一个公共的getter方法时，这也可能需要外地公共访问。
# 虽然java二进制规范不需要这个，要不然有的虚拟机处理这些代码会有问题。当有优化和使用-repackageclasses时才适用。
#指示语：不能用这个指令处理库中的代码，因为有的类和类成员没有设计成public ,而在api中可能变成public
-allowaccessmodification
#当有优化和使用-repackageclasses时才适用。
#-repackageclasses com.test

 # 混淆时记录日志(打印混淆的详细信息)
 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
-verbose

#
# ----------------------------- 默认保留 -----------------------------
#
#----------------------------------------------------
# 保持哪些类不被混淆
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}## 保留support下的所有类及其内部类

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
#表示不混淆上面声明的类，最后这两个类我们基本也用不上，是接入Google原生的一些服务时使用的。
#----------------------------------------------------

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**


#表示不混淆任何包含native方法的类的类名以及native方法名，这个和我们刚才验证的结果是一致
-keepclasseswithmembernames class * {
    native <methods>;
}


#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#表示不混淆任何一个View中的setXxx()和getXxx()方法，
#因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#表示不混淆Parcelable实现类中的CREATOR字段，
#毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 这指定了继承Serizalizable的类的如下成员不被移除混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留R下面的资源
-keep class **.R$* {
 *;
}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}



# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#
#----------------------------- WebView(项目中没有可以忽略) -----------------------------
#
#webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.ljd.example.JSInterface {
    <methods>;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }


#------------------  下方是共性的排除项目         ----------------
# 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
# 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除

-keepclasseswithmembers class * {
    ... *JNI*(...);
}

-keepclasseswithmembernames class * {
	... *JRI*(...);
}

-keep class **JNI* {*;}


#-------------------------------------------基本不用动区域end----------------------------------------------

#application


-keep class com.huatu.handheld_huatu.UniApplication {  *;}
-keep class com.huatu.handheld_huatu.UniApplicationLike { *;}

-keep class com.huatu.handheld_huatu.utils.LogUtils { *;}
-keep class com.huatu.handheld_huatu.utils.UserInfoUtil {  *;}

-keep class com.huatu.handheld_huatu.base.NetResponse {  *;}
-keep class com.huatu.handheld_huatu.base.BaseResponseModel { *;}
-keep class com.huatu.handheld_huatu.utils.SpUtils {  *;}
-keep class com.huatu.handheld_huatu.utils.AppUtils {  *;}
-keep class com.networkbench.** { *; }
-dontwarn com.networkbench.**

#--------------------------1.实体类---------------------------------
# 如果使用了Gson之类的工具要使被它解析的JavaBean类即实体类不被混淆。（这里填写自己项目中存放bean对象的具体路径）
-keep class com.handheld_huatu.mvpmodel.**{*;}
-keep class com.handheld_huatu.business.bean.**{*;}
-keep class com.handheld_huatu.business.arena.bean.**{*;}
-keep class com.handheld_huatu.business.essay.bean.**{*;}
-keep class com.handheld_huatu.business.faceteach.bean.**{*;}
-keep class com.handheld_huatu.business.lessons.bean.**{*;}

-keep class com.handheld_huatu.business.matches.bean.**{*;}
-keep class com.handheld_huatu.business.me.bean.**{*;}
-keep class com.handheld_huatu.business.message.model.**{*;}
-keep class com.handheld_huatu.business.play.bean.**{*;}
-keep class com.handheld_huatu.business.play.event.**{*;}
-keep class com.handheld_huatu.business.ztk_vod.baijiayun.bean.**{*;}
-keep class com.handheld_huatu.business.ztk_vod.bean.**{*;}

-keep class com.huatu.handheld_huatu.business.ztk_zhibo.bean.**{*;}
-keep class com.huatu.handheld_huatu.business.ztk_zhibo.listener.**{*;}
-keep class com.huatu.handheld_huatu.business.ztk_zhibo.pay.**{*;}
-keep class com.huatu.handheld_huatu.business.ztk_zhibo.play.chat.**{*;}


-keep class com.handheld_huatu.datacache.**{*;}
-keep class com.handheld_huatu.event.**{*;}
-keep class com.handheld_huatu.helper.**{*;}
-keep class com.handheld_huatu.listener.**{*;}
-keep class com.handheld_huatu.mvpmodel.**{*;}
-keep class com.handheld_huatu.mvpview.**{*;}
-keep class com.handheld_huatu.mvppresenter.**{*;}
#-keep class com.huatu.handheld_huatu.network.api.**{*;}

-keep class com.huatu.handheld_huatu.network.**{*;}
-keep class com.huatu.handheld_huatu.tinker.**{*;}

-keep class com.huatu.handheld_huatu.wxapi.**{*;}
-keep class com.huatu.message.**{*;}




# 讯飞语音
-dontwarn com.iflytek.**
-keep class com.iflytek.** {*;}
-keep interface com.iflytek.**{*;}

#百度统计
-keep class com.baidu.bottom.** { *; }
-keep class com.baidu.kirin.** { *; }
-keep class com.baidu.mobstat.** { *; }


#router
-keep class com.huatu.handheld_huatu.router.** { *; }

#Gson
#-dontwarn class com.google.gson.**
#-keep class com.google.gson.** {*;}
#-keep class sun.misc.Unsafe {*;}
#-keep class com.google.gson.stream.** {*;}
#-keep class com.google.gson.examples.android.model.** {*;}
#-keep class com.google.** {
#    <fields>;
#    <methods>;
#}


-dontwarn sun.misc.**
-keep class sun.misc.Unsafe {*;}
-keep class com.google.gson.** {*;}
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}



#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }


#
#----------------------------- 友盟-----------------------------
#
-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.huatu.handheld_huatu.R$*{
public static final int *;
}

#这里com.xiaomi.mipushdemo.DemoMessageRreceiver改成app中定义的完整类名
-keep class com.xiaomi.mipush.sdk.DemoMessageReceiver {*;}
#可以防止一个误报的 warning 导致无法成功编译，如果编译使用的 Android 版本是 23。
-dontwarn com.xiaomi.push.**


-keep class com.meituan.android.walle.** {
    *;
}

# 神策
-dontwarn com.sensorsdata.analytics.android.sdk.**
-keep class com.sensorsdata.analytics.android.sdk.** { *; }
# 使用可视化埋点需添加
-keep class **.R$* {
    <fields>;
}

#极光
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

#//百家云混淆规则
-dontwarn com.baijiahulian.**
-dontwarn com.bjhl.**
-keep public class com.baijiahulian.**{*;}
-keep public class com.bjhl.**{*;}
-keep public class com.baijia.**{*;}
-keep public class com.baijiayun.**{*;}
# 点播SDK
-keep class tv.danmaku.ijk.**{*;}
# RxJava混淆规则 RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}


#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}



#com.tencent

-dontwarn com.tencent.mm.**

-keep class com.tencent.mm.**{*;}

-dontwarn com.tencent.tinker.**

-keep class com.tencent.tinker.**{*;}

#支付宝
-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}


# Retrofit  https://www.cnblogs.com/renhui/p/9299786.html

-dontwarn retrofit2.**

-dontwarn org.codehaus.mojo.**

-keep class retrofit2.** { *; }

#-keepattributes Signature

-keepattributes Exceptions

#-keepattributes *Annotation*

-keepattributes RuntimeVisibleAnnotations

-keepattributes RuntimeInvisibleAnnotations

-keepattributes RuntimeVisibleParameterAnnotations

-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

-keepclasseswithmembers class * {

@retrofit2.* <methods>;

}

-keepclasseswithmembers interface * {

@retrofit2.* <methods>;

}


# okhttp 3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

#https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }


# EventBus
#-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


#Matisse
-dontwarn com.bumptech.glide.**

-keep public class net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.* { *; }

#org.apache.commons.compress
-keep class org.apache.commons.compress.** { *; }
-keep interface org.apache.commons.compress.** { *; }
-dontwarn org.apache.commons.compress.**

#-keep class * extends com.gensee.view.xlistview.XListView { *; }


#reactivenetwork
-keep class com.github.pwittchen.reactivenetwork.** { *; }
-keep interface com.github.pwittchen.reactivenetwork.** { *; }
-dontwarn com.github.pwittchen.reactivenetwork.**
