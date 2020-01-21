package com.huatu.handheld_huatu.utils;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.PayResult;
import com.huatu.handheld_huatu.event.PayMessageEvent;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Query;


public class PayUtils {

/*	     * 支付宝：32
			* 微信：33
			* 图币：1*/

	@IntDef({TUGOLD, ALI, WEIXIN})
	@Retention(RetentionPolicy.SOURCE)
	public @interface PayWay{};

	public static final int TUGOLD=1;
	public static final int ALI=32;
	public static final int WEIXIN=33;


	private static String TAG="PayUtils";
	// 商户PID
	public static final String PARTNER = "2088202452415265";//"2088411127750624";
	// 商户收款账号
//	public static final String SELLER = "htcwzx@htexam.com";
	// 网校收款支付宝账号
	public static final String HTWX_SELLER ="huatwxcw@163.com";// "htwx@huatu.com";
	//	rele    https://apitk.huatu.com/v3/AliSecurity/notify_url_app.php
//  debug	http://123.103.79.69:8022/v3/AliSecurity/notify_url_app.php
//    public static final String ZFB_RESPONSE_URL = "http://123.103.79.69:8022/v3/AliSecurity/notify_url_app.php";
	public static final String ZFB_RESPONSE_URL = "https://apitk.huatu.com/v3/AliSecurity/notify_url_app.php";
	public static final String ZFB_RESPONSE_URL_COURSE = "https://apitk.huatu.com/v3/AliSecurity/notify_url.php";
	public static final String ZFB_RESPONSE_URL_SECKILL = "http://sk.v.huatu.com/pay/alipay/notify_url.php";
	//	public static final String ZFB_RESPONSE_URL_SECKILL = "http://sk.v.huatu.com/pay/alipay/notify_url.php";
	// 商户私钥，pkcs8格式
//	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANV6eCVkHqPo4YkOHoHA0KCCfAtCs0VtWlwA7G4gX6WfsEuya4+3dQh2f8kn3vEmsJKMYhjQl6RqjDfCdwuJ66Mpnr0VdWDMQizn9KaLU7HtG5BfDzBxRoujNN3nfTyxdVPHCjS8VmFhZDL+y7NJInDt1Lgsq20DruY0GJJ1eG3VAgMBAAECgYEAxhIuvcqqXzypXif6iDtllzfabfxCJ42xxCGbPQbOl/aVdXajNCJs9SA1qmdTBH74X9DfB9UqjgPJ+8Zz/AHI3dXW6wcvtzRiVClcFXipVizgc0mEqNuOcrROZ4znc5y4zlxOBXt4xcJVT5UoMFPzetn3OWXNvkDJjvzIAB+wvjUCQQDtliIHGJOLM45reJI2I4TZbcv5QmwFgXYj7Flwe0qL1OWO/SdZ+bHVVBtkoGSxYHSyh3isMGHgyP4Vgi3xT3nfAkEA5gYEXq7JVp92mPGLZNERVgSaUYQgKTy+sP63TMFv/RUMYIqjgyHHIZ69y705mdewWik5tFxCaohgnARtO3p2ywJBAOd+EUm4uIo5gdtVb6EwmpEAWm5UOcxjiCkYcU0X1FrK5aGdKGqS0KN7f/VcEsCBqzMIrJuZyMStEmUCoqEtPyECQQDIl/a3mzV5lRaXyg0Fnky/9sOc0tw5GgAx2e9/wDEpQ3HHvx9Y+9vsNcLOKfZRcwcXmVv5LXu967BUXofjlqiBAkAAnUULjr19HM4UaAms9H9NkxIT7gRRMX+KUQdYc0IlO5gre2ZebC2CFrExKccmQfrycLz1/B8Gn9dcKwX8Y2fP";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDVenglZB6j6OGJDh6BwNCggnwL" +
			"QrNFbVpcAOxuIF+ln7BLsmuPt3UIdn/JJ97xJrCSjGIY0Jekaow3wncLieujKZ69" +
			"FXVgzEIs5/Smi1Ox7RuQXw8wcUaLozTd5308sXVTxwo0vFZhYWQy/suzSSJw7dS4" +
			"LKttA67mNBiSdXht1QIDAQAB";
	private static final int SDK_PAY_FLAG = 2;

	public static final String APP_ID = "wxd0611111b31aa452";//wechat appid

	public static final int PAY_RESULT_SUCC = 0;
	public static final int PAY_RESULT_FAIL = 1;
	public static final int PAY_RESULT_UNUSUAL = 2;

	public static int PAYTYPE_ALI=1;
	public static int PAYTYPE_WEIXIN=2;

	public static int payAliReqType=-2;
	public static int payWeiXinReqType=-1;

	public static final int PAY_ALI_ACCOUNT_CHARGE= 1;
	public static final int PAY_WEIXIN_ACCOUNT_CHARGE= 2;

	public static boolean sendPayMessage(int payType,int payMsgType) {
		if(payType==PAYTYPE_ALI) {
			EventBus.getDefault().post(new PayMessageEvent(
					payMsgType));
			return true;
		}else if(payType==PAYTYPE_WEIXIN) {
			EventBus.getDefault().post(new PayMessageEvent(
					payMsgType));
			return true;
		}
		return false;
	}

	private Handler payHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG:
					PayResult payResult = new PayResult((String) msg.obj);

					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();

					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						if (sendPayMessage(PAYTYPE_ALI,PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS)) {
							LogUtils.e("PayMessageEvent","PayMessageEvent PAY_MESSAGE_TYPE_ALI_SUCCESS");
							return;
						}

					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							if (sendPayMessage(PAYTYPE_ALI,PayMessageEvent.PAY_MESSAGE_TYPE_ALI_8000)) {
								LogUtils.e("PayMessageEvent","PayMessageEvent PAY_MESSAGE_TYPE_ALI_8000");
								return;
							}

						} else {
							if (sendPayMessage(PAYTYPE_ALI,PayMessageEvent.PAY_MESSAGE_TYPE_ALI_FAIL)) {
								LogUtils.e("PayMessageEvent","PayMessageEvent PAY_MESSAGE_TYPE_ALI_FAIL");
								return;
							}
//							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误

						}
					}
					break;
				default:
					break;
			}
		}

		;
	};

	public static boolean checkAliPayInstalled(Context context) {
		if(context!=null) {
			Uri uri = Uri.parse("alipays://platformapi/startApp");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			ComponentName componentName = intent.resolveActivity(context.getPackageManager());
			LogUtils.d(TAG, "checkAliPayInstalled "+ (componentName != null));
			return componentName != null;
		}
		return false;
	}

	/**
	 * 支付宝支付
	 */
	public  void payZFB(final Activity act,String subject, String body, String price,
						String orderNum,String notifyUrl) {
		payZFB(act,subject,body,price,orderNum,notifyUrl, PARTNER, HTWX_SELLER,true);

	}

	/**
	 * 支付宝支付
	 */
	public  void payZFB(final Activity act,String subject, String body, String price,
						String orderNum,String notifyUrl, String partnerId, String sellerId,boolean isHuaTu) {
		if(!TextUtils.isEmpty(orderNum) && !Method.isActivityFinished(act)) {
			if(!checkAliPayInstalled(act)){
				CommonUtils.showToast("没有安装支付宝");
			}
			// 订单
			String orderInfo =isHuaTu ? getOrderInfo(partnerId, sellerId, subject, body, price, orderNum,notifyUrl)
					: getEduOrderInfo(partnerId, sellerId, subject, body, price, orderNum,notifyUrl);
			Log.v("order", orderInfo);

			// 必须异步调用
			Thread payThread = new Thread(new PayAliRunnable(act, orderInfo, payHandler,isHuaTu));
			payThread.start();
		}else {
			LogUtils.e(TAG,"!TextUtils.isEmpty(orderNum) && !TextUtils.isEmpty(payNotifyUrl) && !Method.isActivityFinished(act)");
		}
	}

	public void payZFBV2(final Activity act,String signString){

		Thread payThread = new Thread(new PayAliRunnableV2(act,signString,payHandler));
		payThread.start();
	}

	private static class PayAliRunnableV2 implements Runnable {

		private WeakReference<Activity> mContext;
		private String orderInfo;
		private Handler payHandler;


		public PayAliRunnableV2(Activity act,String pInfo,Handler payH) {
			mContext = new WeakReference<Activity>(act);
			orderInfo=pInfo;
			payHandler=payH;

		}
		@Override
		public void run() {
			Activity act = (Activity) mContext.get();
			if(!Method.isActivityFinished(act) && orderInfo!=null && payHandler!=null) {

				// 构造PayTask 对象
				PayTask alipay = new PayTask(mContext.get());
				// 调用支付接口，获取支付结果
				String result = alipay.pay(orderInfo,true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				payHandler.sendMessage(msg);
			}else {
				LogUtils.e(TAG,"payZFB Method.isActivityFinished(act)  if(!Method.isActivityFinished(act) && payInfo!=null && payHandler!=null) ");
			}
		}
	};

	private static class PayAliRunnable implements Runnable {

		private WeakReference<Activity> mContext;
		private String orderInfo;
		private Handler payHandler;
		private boolean mIsHuatu;

		public PayAliRunnable(Activity act,String pInfo,Handler payH,boolean isHuaTu) {
			mContext = new WeakReference<Activity>(act);
			orderInfo=pInfo;
			payHandler=payH;
			mIsHuatu=isHuaTu;
		}
		@Override
		public void run() {
			Activity act = (Activity) mContext.get();
			if(!Method.isActivityFinished(act) && orderInfo!=null && payHandler!=null) {

				String singStr="";
				try{
					// 对订单做RSA 签名

					//@Query("flag") int flag,@Query("orderInfo") String orderInfo

					JsonObject payerReg = new JsonObject();
					payerReg.addProperty("orderInfo", orderInfo);

					Response<BaseResponseModel<String>> sign = CourseApiService.getApi().getAliPaySign(mIsHuatu ?0:1,payerReg).execute();
					if(sign.isSuccessful()){
						singStr=sign.body().data;
					}
				}catch (IOException e) {
					//onFailure(call, e);
				}
				try {
					// 仅需对sign 做URL编码
					singStr = URLEncoder.encode(singStr, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				// 完整的符合支付宝参数规范的订单信息
				final String payInfo = orderInfo + "&sign=\"" + singStr + "\"&"
						+ getSignType();
				Log.v("payInfo", payInfo);

				// 构造PayTask 对象
				PayTask alipay = new PayTask(mContext.get());
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo,true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				payHandler.sendMessage(msg);
			}else {
				LogUtils.e(TAG,"payZFB Method.isActivityFinished(act)  if(!Method.isActivityFinished(act) && payInfo!=null && payHandler!=null) ");
			}
		}
	};


	/**
	 * create the order info. 创建订单信息
	 */
	public static String getZfbOrderInfo(String subject, String notifyUrl, String body, String price,
								  String orderNum,boolean isSecKill) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PayUtils.PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + PayUtils.HTWX_SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		String noUrl = notifyUrl;
		if (TextUtils.isEmpty(noUrl)) {
			if (isSecKill) {
				noUrl = PayUtils.ZFB_RESPONSE_URL_SECKILL;
			} else {
				noUrl = PayUtils.ZFB_RESPONSE_URL_COURSE;
			}
		}
		orderInfo += "&notify_url=" + "\"" + noUrl + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}


	/**
	 * create the order info. 创建订单信息
	 */
	private static String getOrderInfo(String partner, String sellerId, String subject, String body, String price,
									   String orderNum,String notifyUrl) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + sellerId + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		String noUrl = notifyUrl;
		if(TextUtils.isEmpty(noUrl)) {
			noUrl = ZFB_RESPONSE_URL;
		}
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ noUrl
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	private static String getEduOrderInfo(String partner, String sellerId, String subject, String body, String price,
										  String orderNum,String notifyUrl) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + sellerId + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		String noUrl = notifyUrl;
		if(TextUtils.isEmpty(noUrl)) {
			noUrl = ZFB_RESPONSE_URL;
		}
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ noUrl
				+ "\"";

		// 服务接口名称， 固定值
		//orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		//orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		//orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		//orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 *
	 * @param content 待签名订单信息
	 */
/*	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}*/

/*	private String signByEdu(String content){

		return SignUtils.sign(content,"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANJQ5dZJ3uSix1ifbAvWKmoXi7kb2DMj82hEMaHFGk6dbMFw708WgqzjOvOGQPQV9Nvq423aSlCT1qO/Ow2dDbRLMHyi6e7L3e7Ab5dDL4osWxxqTd4z9ShkPmtwGCgIDSOdKF0zsdtEtrHKUZ0hmR4g7/D2hO0bYxItBCbg+kv3AgMBAAECgYAhHWnC/uigmVzIAHoTtwoAoGp4oAC/tKZrwWkLLqzAuhrYrn6Ptlym+jRbCcWKaTaftfFPZ7KR02VVbRPQRe1VL7JFcEga6Po6KZKbfNOf75T2mT0RZpwZpYjxa74yo47mnN4g+ZpyEhm1OsAGUNPQw/LloMGw2YANd9V1qdgd0QJBAP193dwfF7sI09upzsLljhCOcF4rZvJUgbnBANyKljWw7SIoy8H/AgLIeTq39LTdFkvoXLCnCekv2+wzQL3EOOkCQQDUZakbpisQzeHdFm86n6ehUmkojHth8NN6t/2TPW2oJbqXjzbAIgyF/G2XIr+mNnieL6fUqDx6PyVJc4HN01HfAkAJqH8IgQLFdIul5e1jzZ1BEjxDykGM4B1lN25R+NHKT+hpEcbZqF6qPnsn+pRPQ9EyqTLG5EZjZRhrAnToBg7pAkBYykrgkR2tv7OtHgTPqBCGoxHs7pVhwzBVO/dYhzSBN4yCcU89EL7VgEo8BT3C+UCBOIDbqJznqeAnjY71AWNXAkBlf5wKS+iM4uGjN+hn2wcT24GJFERHKPlcjrLdnT9kWw785hn+8m4ZKMJwhm1YrsnT86ty4ek63t1Wf2vJET4y");
	}*/

	/**
	 * get the sign type we use. 获取签名方式
	 */
	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}


	/*************************************************************************************************************************/

	/**
	 * 微信支付
	 */
	public void payWX(IWXAPI api, PayInfo info, Context cxt) {
		if(api==null || !isWXAppInstalledAndSupported(api)){
			Toast.makeText(cxt, "微信未安装，请选择其他付款方式", Toast.LENGTH_SHORT).show();
			return;
		}
		PayReq req = new PayReq();
		req.appId = info.appid;
		req.partnerId = info.partnerid;
		req.prepayId = info.prepay_id;
		req.nonceStr = info.noncestr;
		req.timeStamp = info.timestamp;
		req.packageValue = info.packageValue;
		req.sign = info.sign;
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		api.sendReq(req);
	}

	private boolean isWXAppInstalledAndSupported(IWXAPI api) {
		boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
		return sIsWXAppInstalledAndSupported;
	}


//	/** 支付宝支付业务：入参app_id */
//	public static final String APPID = "";
//
//	/** 支付宝账户登录授权业务：入参pid值 */
//	public static final String PID = "";
//	/** 支付宝账户登录授权业务：入参target_id值 */
//	public static final String TARGET_ID = "";
//
//	/** 商户私钥，pkcs8格式 */
//	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
//	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
//	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
//	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
//	/** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
//	public static final String RSA2_PRIVATE = "";
//
//
//	/**
//	 * 支付宝支付业务
//	 *
//	 * @param v
//	 */
//	public void payV2(Context cxt,View v) {
//		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
//			new AlertDialog.Builder(cxt).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
//					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialoginterface, int i) {
//							//
////							finish();
//						}
//					}).show();
//			return;
//		}
//
//		/**
//		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
//		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
//		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
//		 *
//		 * orderInfo的获取必须来自服务端；
//		 */
//		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
//		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//		final String orderInfo = orderParam + "&" + sign;
//
//		Runnable payRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				PayTask alipay = new PayTask(cxt);
//				Map<String, String> result = alipay.payV2(orderInfo, true);
//				Log.i("msp", result.toString());
//
//				Message msg = new Message();
//				msg.what = SDK_PAY_FLAG;
//				msg.obj = result;
//				payHandler.sendMessage(msg);
//			}
//		};
//
//		Thread payThread = new Thread(payRunnable);
//		payThread.start();
//	}


}
