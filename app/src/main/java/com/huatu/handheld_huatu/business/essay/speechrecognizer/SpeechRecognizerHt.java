package com.huatu.handheld_huatu.business.essay.speechrecognizer;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.huatu.handheld_huatu.business.essay.cusview.EssayExamBottomView;
import com.huatu.handheld_huatu.business.essay.cusview.VolumeWaveView;
import com.huatu.handheld_huatu.business.essay.speechrecognizer.util.JsonParser;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SpeechRecognizerHt {
    private static String TAG = SpeechRecognizerHt.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private EditText mResultText;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    int ret = 0; // 函数调用返回值
    private boolean mTranslateEnable = false;

    VolumeWaveView mSoundWave;

    private Subscription timeTicker;                                        // 监听无语音输入的定时器
    private AtomicInteger atomTime = new AtomicInteger(0);            // 语音没有输入的时间s
    private EssayExamBottomView bottomView;

    private boolean isEnd;              // 当前光标是否在句末
    private int cmd = 2;                // 当前识别状态

    public void onCreate(Context cxt, VolumeWaveView soundWave) {
        mSoundWave = soundWave;
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(cxt, mInitListener);
        this.mIat.setParameter("params", null);
        this.mIat.setParameter("domain", "iat");
        this.mIat.setParameter("language", "zh_cn");
        this.mIat.setParameter("accent", "mandarin");
        this.mIat.setParameter("engine_type", "cloud");
        this.mIat.setParameter("result_type", "json");
        this.mIat.setParameter("speech_timeout", "160000");
        this.mIat.setParameter("vad_bos", "4000");
        this.mIat.setParameter("vad_eos", "1800");
        this.mIat.setParameter("asr_ptt", "1");

    }

    public void setEditText(EditText mResultText) {
        this.mResultText = mResultText;
    }

    public void onClick(int cmd) {
        this.cmd = cmd;
        if (null == mIat) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        switch (cmd) {
            // 进入参数设置页面
            // 开始听写
            // 如何判断一次听写结束：OnResult isLast=true 或者 onError
            case 1:
                start(null);
                unSubscribeTimeTicker();
                timeTicker = Observable.interval(1, TimeUnit.SECONDS)
                        .onBackpressureDrop()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Long>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Long aLong) {
                                int time = atomTime.incrementAndGet();
                                if (time == 5) {
                                    bottomView.setVoiceText("长时间无输入，即将暂停!");
                                }
                                if (time == 10) {
                                    bottomView.onStopRecognizer();
                                    mIat.cancel();
                                    unSubscribeTimeTicker();
                                }
                            }
                        });
                break;
            case 2:
                unSubscribeTimeTicker();
                mIat.stopListening();
//			showTip("停止听写");
                break;
            // 取消听写
            case 3:
                mIat.cancel();
//			showTip("取消听写");
                break;
            default:
                break;
        }
    }

    private void start(Context cxt) {
        if (mResultText != null) {
            isEnd = (mResultText.getSelectionEnd() == mResultText.length());
        }
        // 移动数据分析，收集开始听写事件
        if (cxt != null) {
            FlowerCollector.onEvent(cxt, "iat_recognize");
        }
        mIatResults.clear();
        // 设置参数
        setParam();
        // 不显示听写对话框
        mIat.stopListening();
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
//			showTip("听写失败,错误码：" + ret);
            showTip("听写失败");
        }
//        else {
//            showTip("开始");
//        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//				showTip("初始化失败，错误码：" + code);
                showTip("初始化失败");
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
//				showTip(error.getPlainDescription(true));
//                showTip("您好像没有说话哦");
            }
            unSubscribeTimeTicker();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");
            Log.d(TAG, "michael   onEndOfSpeech");
            // TODO 最后的结果
            // 移动数据分析，收集开始听写事件
//            start(null);
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, "isLast:" + isLast);
            Log.d(TAG, results.getResultString());
            if (mTranslateEnable) {
                printTransResult(results);
            } else {
                printResult(results);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//			showTip("当前正在说话，音量大小：" + volume);
//            if (mSoundWave != null) {
//                mSoundWave.pervolume = volume;
//            }

            Log.d("volume", "返回音频数据：" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG, "results:" + results);
        if (text != null && mResultText != null) {
            // 有输入，把变量置零
            bottomView.setVoiceText("正在听写中...");
            atomTime.getAndSet(0);
            int indexend = mResultText.getSelectionEnd();
            if (indexend == mResultText.length()) {                     // 如果当前光标在句末
                if (!isEnd && this.cmd == 2) {
                    return;
                }
                mResultText.append(text);
                mResultText.setSelection(mResultText.length());
            } else {                                                    // 光标在句中
                String content = mResultText.getText().toString();
                StringBuffer newContent = new StringBuffer(content);
                if (indexend < newContent.length()) {
                    newContent.insert(indexend, text);
                    mResultText.setText(newContent);
                    int sel = indexend + text.length();
                    if (sel < 0) {
                        sel = 0;
                    }
                    if (sel > mResultText.length()) {
                        sel = mResultText.length();
                    }
                    mResultText.setSelection(sel);
                }
            }
        }
    }

    private void showTip(final String str) {
        ToastUtils.showMessage(str);
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        this.mTranslateEnable = false;
        if (mTranslateEnable) {
            Log.i(TAG, "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }

        String lag = "mandarin";
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
            }
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);

            if (mTranslateEnable) {
                mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
            }
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "10000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");

        this.mIat.setParameter("params", null);
        this.mIat.setParameter("domain", "iat");
        this.mIat.setParameter("language", "zh_cn");
        this.mIat.setParameter("accent", "mandarin");
        this.mIat.setParameter("engine_type", "cloud");
        this.mIat.setParameter("result_type", "json");
        this.mIat.setParameter("speech_timeout", "60000");
        this.mIat.setParameter("vad_bos", "10000");
        this.mIat.setParameter("vad_eos", "10000");
        this.mIat.setParameter("asr_ptt", "1");
    }

    private void printTransResult(RecognizerResult results) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。");
        } else {
            mResultText.setText("原始语言:\n" + oris + "\n目标语言:\n" + trans);
        }
    }

    public void onDestroy() {
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
        unSubscribeTimeTicker();
    }

    private void unSubscribeTimeTicker() {
        atomTime.getAndSet(0);
        if (timeTicker != null && !timeTicker.isUnsubscribed()) {
            timeTicker.unsubscribe();
            timeTicker = null;
        }
    }

    protected void onResume(Context cxt) {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(cxt);
        FlowerCollector.onPageStart(TAG);
    }

    protected void onPause(Context cxt) {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(cxt);
    }

    public void setBottomView(EssayExamBottomView bottomView) {
        this.bottomView = bottomView;
    }
}
