package com.netease.hearttouch.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.ReuseActivityHelper;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.netease.hearttouch.router.intercept.HTInterceptorEntry;
import com.netease.hearttouch.router.intercept.IRouterInterceptor;
import com.netease.hearttouch.router.method.HTMethodRouterEntry;
import com.tencent.tinker.loader.shareutil.ShareReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cjx on 2019\10\29 0029.
 * 针对fragment的跳转
 *
 */

public class HTPageRouterCall extends HTRouterCall {
    private int index = 0;
    private List<IRouterInterceptor> mInterceptors = new ArrayList();

    public void start() {
        if(this.index <= 0) {
            this.proceed();
        }
    }

    public static void call(Context context, String url) {
        newBuilderV2(url).context(context).build().start();
    }

    public static void call(Fragment fragment, String url) {
        newBuilderV2(url).fragment(fragment).build().start();
    }

    public static void call(Context context, String url, int requestCode) {
        newBuilderV2(url).context(context).forResult(true).requestCode(requestCode).build().start();
    }

    public static void call(Fragment fragment, String url, int requestCode) {
        newBuilderV2(url).fragment(fragment).forResult(true).requestCode(requestCode).build().start();
    }


    public static HTPageRouterCall.Builder newBuilderV2(String url) {
        return new HTPageRouterCall.Builder(url);
    }
    public void proceed() {
        IRouterInterceptor interceptor = this.index < this.mInterceptors.size()?(IRouterInterceptor)this.mInterceptors.get(this.index):null;
        ++this.index;
        if(interceptor != null) {
            interceptor.intercept(this);
        } else {
             realProceed();
        }

    }

    private void realProceed() {
        HTUrlEntry entry = HTRouterManager.findRouterEntryByUrl(this.params.url);
        if(entry == null && this.params.downgradeUrls != null) {
            Iterator var2 = this.params.downgradeUrls.iterator();

            while(var2.hasNext()) {
                String url = (String)var2.next();
                entry = HTRouterManager.findRouterEntryByUrl(url);
                if(entry != null) {
                    break;
                }
            }
        }

        if(entry != null) {
            this.doProceedPageRouter();
        } else {
            HTUrlEntry entry2 = HTRouterManager.findMethodRouterEntryByUrl(this.params.url);
            if(entry2 != null) {
                this.doProceedMethodRouter();
            }
        }
    }

    private void doProceedPageRouter() {
        if(this.params.forResult) {
            if(this.params.context != null) {
                HTRouterManagerV2.startActivityForResult((Activity)this.params.context, this.params.url, this.params.sourceIntent, this.params.isFinish, this.params.requestCode, this.params.entryAnim, this.params.exitAnim);
            } else {
                HTRouterManagerV2.startActivityForResult(this.params.fragment, this.params.url, this.params.sourceIntent, this.params.isFinish, this.params.requestCode, this.params.entryAnim, this.params.exitAnim);
            }
        } else if(this.params.context != null) {
            HTRouterManagerV2.startActivity(this.params.context, this.params.url, this.params.sourceIntent, this.params.isFinish, this.params.entryAnim, this.params.exitAnim);
        }
    }

    private void doProceedMethodRouter() {
        try {
            HTRouterManagerV2.callMethod(this.params.getContext(), this.params.url);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }


    public static class Builder  {
        private HTPageRouterCall call = new HTPageRouterCall();

        public Builder(String url) {
            this.call.params.url = url;
        }

        public HTPageRouterCall.Builder context(Context context) {
            this.call.params.context = context;
            return this;
        }

        public HTPageRouterCall.Builder fragment(Fragment fragment) {
            this.call.params.fragment = fragment;
            return this;
        }

        public HTPageRouterCall.Builder url(String url) {
            this.call.params.url = url;
            return this;
        }

        public HTPageRouterCall.Builder downgradeUrls(List<String> urls) {
            if(urls != null) {
                this.call.params.downgradeUrls = new LinkedList();
                this.call.params.downgradeUrls.addAll(urls);
            }

            return this;
        }

        public HTPageRouterCall.Builder downgradeUrls(String... urls) {
            if(urls != null) {
                this.call.params.downgradeUrls = new LinkedList();
                String[] var2 = urls;
                int var3 = urls.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String url = var2[var4];
                    this.call.params.downgradeUrls.add(url);
                }
            }

            return this;
        }

        public HTPageRouterCall.Builder sourceIntent(Intent sourceIntent) {
            this.call.params.sourceIntent = sourceIntent;
            return this;
        }

        public HTPageRouterCall.Builder isFinish(boolean isFinish) {
            this.call.params.isFinish = isFinish;
            return this;
        }

        public HTPageRouterCall.Builder entryAnim(int entryAnim) {
            this.call.params.entryAnim = entryAnim;
            return this;
        }

        public HTPageRouterCall.Builder exitAnim(int exitAnim) {
            this.call.params.exitAnim = exitAnim;
            return this;
        }

        public HTPageRouterCall.Builder forResult(boolean forResult) {
            this.call.params.forResult = forResult;
            return this;
        }

        public HTPageRouterCall.Builder requestCode(int requestCode) {
            this.call.params.requestCode = requestCode;
            return this;
        }

        public HTPageRouterCall.Builder interceptors(IRouterInterceptor... interceptor) {
            Collections.addAll(this.call.mInterceptors, interceptor);
            return this;
        }

        public HTPageRouterCall build() {
            Iterator var1 = HTRouterCall.ANNO_INTERCEPTORS.iterator();

            while(var1.hasNext()) {
                HTInterceptorEntry entry = (HTInterceptorEntry)var1.next();
                if(entry.matches(this.call.params.url)) {
                    this.call.mInterceptors.add(entry.getInterceptor());
                }
            }

            this.call.mInterceptors.addAll(HTRouterCall.sGlobalInterceptors);
            if(this.call.params.requestCode != 0) {
                this.call.params.forResult = true;
            }

            return this.call;
        }
    }


    public static class HTRouterManagerV2 {
        private static Class mWebActivityClass;
        private static String mWebExtraKey = "HTUrl";
        /**
         * 通过URL启动一个页面
         *
         * @param activity     当前需要进行跳转的activity
         * @param url          跳转的目标URL
         * @param sourceIntent 传递进来一个intent，用户数据及启动模式等扩展
         * @param isFinish     跳转后是否需要关闭当前页面
         * @param entryAnim    自定义的入场动画
         * @param exitAnim     自定义的出场动画
         */
    /*package*/
        static void startActivity(Activity activity,
                                  String url,
                                  Intent sourceIntent,
                                  boolean isFinish,
                                  int entryAnim,
                                  int exitAnim) {
            Intent intent = null;
            HTRouterEntry entry = HTRouterManager.findRouterEntryByUrl(url);
            if (entry != null) {
                intent = processIntent(activity, sourceIntent, url, entry);
                activity.startActivity(intent);
                if (exitAnim == 0 && entryAnim == 0) {
                    exitAnim = getAnimIdMethod(activity.getClass(), true);
                    entryAnim = getAnimIdMethod(entry.getActivity(), false);
                }
                if (isFinish) {
                    activity.finish();
                }
                if (entryAnim != 0 || exitAnim != 0) {
                    activity.overridePendingTransition(entryAnim, exitAnim);
                }
            } else if (mWebActivityClass != null) {
                intent = processIntentclass(activity, sourceIntent, url, mWebActivityClass);
                intent.putExtra(mWebExtraKey, HTRouterManager.getHttpSchemaHostAndPath(url));
                activity.startActivity(intent);
                if (exitAnim == 0 && entryAnim == 0) {
                    exitAnim = getAnimIdMethod(activity.getClass(), true);
                    entryAnim = getAnimIdMethod(mWebActivityClass, false);
                }
                if (isFinish) {
                    activity.finish();
                }
                if (entryAnim != 0 || exitAnim != 0) {
                    activity.overridePendingTransition(entryAnim, exitAnim);
                }
            }
        }

        /**
         * 通过URL启动一个页面
         *
         * @param activity     当前需要进行跳转的activity
         * @param url          跳转的目标URL
         * @param sourceIntent 传递进来一个intent，用户数据及启动模式等扩展
         * @param isFinish     跳转后是否需要关闭当前页面
         */
    /*package*/
        static void startActivity(Activity activity, String url, Intent sourceIntent, boolean isFinish) {
            startActivity(activity, url, sourceIntent, isFinish, 0, 0);
        }

        /**
         * 通过URL启动一个页面
         *
         * @param context      当前需要进行跳转的 context
         *                     如果 context 是 Activity，则使用 startActivity(Activity activity, String url, Intent sourceIntent, boolean isFinish)进行跳转
         *                     否则这个函数进行处理，但参数 isFinish 不会生效，另外启动页面的场景切换动画会使用主题默认动画
         * @param url          跳转的目标URL
         * @param sourceIntent 传递进来一个intent，用户数据及启动模式等扩展
         * @param isFinish     跳转后是否需要关闭当前页面
         * @param entryAnim    自定义的入场动画
         * @param exitAnim     自定义的出场动画
         */
    /*package*/
        static void startActivity(Context context,
                                  String url,
                                  Intent sourceIntent, boolean isFinish, int entryAnim, int exitAnim) {
            if (context instanceof Activity) {
                startActivity((Activity) context, url, sourceIntent,
                        isFinish, entryAnim, exitAnim);
                return;
            }
            Intent intent = null;
            HTRouterEntry entry = HTRouterManager.findRouterEntryByUrl(url);
            if (entry != null) {
                intent = processIntent(context, sourceIntent, url, entry);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (mWebActivityClass != null) {
                intent = processIntentclass(context, sourceIntent, url, mWebActivityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(mWebExtraKey, HTRouterManager.getHttpSchemaHostAndPath(url));
                context.startActivity(intent);
            }
        }

        /**
         * 通过URL启动一个页面,同时可以获得result回调
         *
         * @param activity     当前需要进行跳转的activity
         * @param url          跳转的目标URL
         * @param sourceIntent 传递进来一个intent，用户数据及启动模式等扩展
         * @param isFinish     跳转后是否需要关闭当前页面
         * @param entryAnim    自定义的入场动画
         * @param exitAnim     自定义的出场动画
         */
    /*package*/
        static void startActivityForResult(Activity activity,
                                           String url,
                                           Intent sourceIntent,
                                           boolean isFinish,
                                           int requestCode,
                                           int entryAnim,
                                           int exitAnim) {
            Intent intent = null;
            HTRouterEntry entry = HTRouterManager.findRouterEntryByUrl(url);
            if (entry != null) {
                intent = processIntent(activity, sourceIntent, url, entry);
                activity.startActivityForResult(intent, requestCode);
                if (exitAnim == 0 && entryAnim == 0) {
                    exitAnim = getAnimIdMethod(activity.getClass(), true);
                    entryAnim = getAnimIdMethod(entry.getActivity(), false);
                }
                if (isFinish) {
                    activity.finish();
                }
                if (entryAnim != 0 || exitAnim != 0) {
                    activity.overridePendingTransition(entryAnim, exitAnim);
                }
            } else if (mWebActivityClass != null) {
                intent = processIntentclass(activity, sourceIntent, url, mWebActivityClass);
                intent.putExtra(mWebExtraKey, HTRouterManager.getHttpSchemaHostAndPath(url));
                activity.startActivityForResult(intent, requestCode);
                if (exitAnim == 0 && entryAnim == 0) {
                    exitAnim = getAnimIdMethod(activity.getClass(), true);
                    entryAnim = getAnimIdMethod(mWebActivityClass, false);
                }
                if (isFinish) {
                    activity.finish();
                }
                if (entryAnim != 0 || exitAnim != 0) {
                    activity.overridePendingTransition(entryAnim, exitAnim);
                }
            }
        }

        /**
         * 通过URL启动一个页面,同时可以获得result回调，回调在fragment中
         *
         * @param fragment     当前需要进行跳转的fragment
         * @param url          跳转的目标URL
         * @param sourceIntent 传递进来一个intent，用户数据及启动模式等扩展
         * @param isFinish     跳转后是否需要关闭当前页面
         * @param entryAnim    自定义的入场动画
         * @param exitAnim     自定义的出场动画
         */
    /*package*/
        static void startActivityForResult(Fragment fragment,
                                           String url,
                                           Intent sourceIntent,
                                           boolean isFinish,
                                           int requestCode,
                                           int entryAnim,
                                           int exitAnim) {
            if (fragment.getActivity() == null) {
                return;
            }

            Intent intent = null;
            HTRouterEntry entry = HTRouterManager.findRouterEntryByUrl(url);
            if (entry != null) {
                intent = processIntent(fragment.getActivity(), sourceIntent, url, entry);
                fragment.startActivityForResult(intent, requestCode);
                if (exitAnim == 0 && entryAnim == 0) {
                    exitAnim = getAnimIdMethod(fragment.getActivity().getClass(), true);
                    entryAnim = getAnimIdMethod(entry.getActivity(), false);
                }
                if (isFinish) {
                    fragment.getActivity().finish();
                }
                if (entryAnim != 0 || exitAnim != 0) {
                    fragment.getActivity().overridePendingTransition(entryAnim, exitAnim);
                }
            } else if (mWebActivityClass != null) {
                intent = processIntentclass(fragment.getActivity(), sourceIntent, url, mWebActivityClass);
                intent.putExtra(mWebExtraKey, HTRouterManager.getHttpSchemaHostAndPath(url));
                fragment.startActivityForResult(intent, requestCode);
                if (exitAnim == 0 && entryAnim == 0) {
                    exitAnim = getAnimIdMethod(fragment.getActivity().getClass(), true);
                    entryAnim = getAnimIdMethod(mWebActivityClass, false);
                }
                if (isFinish) {
                    fragment.getActivity().finish();
                }
                if (entryAnim != 0 || exitAnim != 0) {
                    fragment.getActivity().overridePendingTransition(entryAnim, exitAnim);
                }
            }
        }

        private static int getAnimIdMethod(Class activity, boolean isExitAnim) {
            HTRouter anno = (HTRouter)activity.getClass().getAnnotation(HTRouter.class);
            return anno != null?(isExitAnim?anno.exitAnim():anno.entryAnim()):0;
        }

        private static Intent processIntentclass(Context context, Intent sourceIntent, String url, Class activityClass) {
            Intent intent = null;
            if(sourceIntent != null) {
                intent = (Intent)sourceIntent.clone();
                intent.setClass(context, activityClass);
            } else {
                intent = new Intent(context, activityClass);
            }

            intent.putExtra("HTROUTER_TARGET_URL_KEY", Uri.parse(url));
            HashMap<String, String> paramsMap = HTRouterManager.getParams(url);
            if(paramsMap != null) {
                intent.putExtra("ht_url_params_map", paramsMap);
            }

            return intent;
        }

        static  Field mClassNameField;
        private static String getClassName(HTRouterEntry entry) {
            try {
                if (null == mClassNameField) {
                    mClassNameField = ShareReflectUtil.findField(entry, "activityClassName");
                }
                return mClassNameField.get(entry).toString();

            } catch (Exception e) {

            }
            return null;
        }

        private static Intent processIntent(Context context, Intent sourceIntent, String url, HTRouterEntry entry) {
            Intent intent = null;
            long startTime=System.currentTimeMillis();
            if(entry.getUrl().endsWith("{fragment2}")){

                Bundle args=new Bundle();
                if(null!=sourceIntent){
                    String uriPath=sourceIntent.getDataString();
                    if(!TextUtils.isEmpty(uriPath)){
                       args.putString(ArgConstant.URI_PATH,uriPath);
                    }
                }
                String currentClassName=getClassName(entry);
                if(TextUtils.isEmpty(currentClassName)){
                    currentClassName= entry.getActivity().getName();
                }
                intent= BaseFrgContainerActivity.createIntent(context,currentClassName,args);
            }
            else if(entry.getUrl().endsWith("{fragment}")){
                Bundle args=new Bundle();
                if(null!=sourceIntent){
                    String uriPath=sourceIntent.getDataString();
                    if(!TextUtils.isEmpty(uriPath)){
                        args.putString(ArgConstant.URI_PATH,uriPath);
                    }
                }
                String currentClassName=getClassName(entry);
                if(TextUtils.isEmpty(currentClassName)){
                    currentClassName= entry.getActivity().getName();
                }
                intent = ReuseActivityHelper.builder(context).setFragmentParameter(FragmentParameter.create(currentClassName,args)).build();
            }else {

                String currentClassName=getClassName(entry);
                if(!TextUtils.isEmpty(currentClassName)){
                    Log.e("processIntent",currentClassName);
                    if(sourceIntent != null) {
                        intent = (Intent)sourceIntent.clone();
                        intent.setClassName(context,currentClassName);
                    } else {
                        intent = new Intent();
                        intent.setComponent(new ComponentName(context, currentClassName));
                    }
                }else {
                    if(sourceIntent != null) {
                        intent = (Intent)sourceIntent.clone();
                        intent.setClass(context, entry.getActivity());

                    } else {
                        intent = new Intent(context,  entry.getActivity());
                    }
                }
                intent.putExtra("HTROUTER_TARGET_URL_KEY", Uri.parse(url));
                HashMap<String, String> paramsMap = HTRouterManager.getParams(url);
                if(paramsMap != null) {
                    intent.putExtra("ht_url_params_map", paramsMap);
                }
            }
            Log.e("processIntent",(System.currentTimeMillis()-startTime)+"ms");
            return intent;
        }

        //
        static Object callMethod(@Nullable Context context, String url) throws ParamConvertor.ParamConvertException, NullPointerException {
            HTMethodRouterEntry entry = HTRouterManager.findMethodRouterEntryByUrl(url);
            Method method = null;
            if(entry != null && entry.isValid()) {
                method = entry.getMethod();
            }

            if(method == null) {
                HTLogUtil.d("entry is invalid. url = " + url);
                throw new NullPointerException("\"entry is invalid. url = \" + url");
            } else {
                List paramObjs = new ArrayList();
                Iterator var7 = entry.paramTypes.iterator();

                while(var7.hasNext()) {
                    Class clazz = (Class)var7.next();
                    if(Context.class.isAssignableFrom(clazz)) {
                        paramObjs.add(context);
                    } else {
                        String value = url;
                        paramObjs.add(value);
                    }
                }

                if ((method.getModifiers() & Modifier.STATIC) != 0) {
                    return RefInvoker.invoke((Object)null, method, paramObjs.toArray());
                } else {
                    Object instance = RefInvoker.invokeStaticMethod(entry.className, "getInstance", (Class[])null, (Object[])null);
                    if(instance != null) {
                        return RefInvoker.invoke(instance, method, paramObjs.toArray());
                    } else {
                        return null;
                    }
                }
            }
        }


    }
}
