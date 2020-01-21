package com.huatu.handheld_huatu.helper;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/** https://blog.csdn.net/BunnyCoffer/article/details/88028125
 *https://www.cnblogs.com/lzh-Linux/p/8243434.html
 * https://blog.csdn.net/weixin_34026276/article/details/87997008
 * @author crazyZhangxl on 2019/1/28.
 * Describe: 切面
 */
@Aspect
public class LoginAspect {

  /*  *
     * 对含有某个方法的特定注解打上切点
     */
    @Pointcut("execution(@com.huatu.handheld_huatu.helper.LoginTrace * *(..))")
    public void pointCutLogin(){

    }

   /* *
     * 处理 特定的打上切点的方法
     * @param proceedingJoinPoint
     * @throws Throwable
     */
    @Around("pointCutLogin()")
    public void aroundLogin(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

         LogUtils.e("aroundLogin","SpUtils.getLoginState()+");

         if (SpUtils.getLoginState()){
            proceedingJoinPoint.proceed();
        }else {
            final ProceedingJoinPoint action=proceedingJoinPoint;
             Runnable tmpRunnable=new Runnable() {
                 @Override
                 public void run() {
                     try{
                       action.proceed();
                     }catch (Throwable e){ }
                  }
             };
             GlobalRouterInterceptor.mOnLoginResultListener=new GlobalRouterInterceptor.SimpleLoginListener(tmpRunnable);

             MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
             LoginTrace annotation = signature.getMethod().getAnnotation(LoginTrace.class);
             int type = annotation.type();
             if (proceedingJoinPoint.getThis() instanceof Context){
                // 获得注解参数
                  dealWithType(type,(Context) proceedingJoinPoint.getThis());
             }else {
                 dealWithType(type, UniApplicationContext.getContext());
             }
        }
    }

    /**
     * 在这里处理是弹出dialog呢还是跳转界面呢 等等
     * @param type
     * @param context
     */
    private void dealWithType(int type,Context context){
        switch (type){
            case 0:
                Intent intent = new Intent(Constant.APP_LOGIN_ACTION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ArgConstant.QUICK_LOGIN,true);
                context.startActivity(intent);
                break;
                default:
                    Toast.makeText( context,"请先进行登陆!", Toast.LENGTH_SHORT).show();
                    break;
        }
    }
}
