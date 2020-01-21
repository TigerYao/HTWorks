package com.huatu.handheld_huatu.business.essay.cusview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamEditAnswer;
import com.huatu.handheld_huatu.business.essay.speechrecognizer.SpeechRecognizerHt;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EssayExamBottomView extends LinearLayout {

    Context mContext;
    private SpeechRecognizerHt mSpeechRecognizerHt = new SpeechRecognizerHt();
    private int resId = R.layout.essay_exam_bottom_edit_layout;
    private View rootView;
    EssayExamEditAnswer essayExamEditAnswer;
    TextView bottomLeftText;                    // xxx字
    TextView tvTotal;                           // 总字数
    ImageView essExInputCameraIv;               // 拍照输入按钮
    ImageView essExInputVoiceIv;                // 声音输入按钮
    ImageView essExInputSoftIv;                 // 打字输入按钮

    LinearLayout llPost;                        // 交卷布局、按钮，为了控制交卷交卷样式
    ImageView ivPost;
    TextView tvPost;

    @BindView(R.id.tip_tv)
    TextView tip_tv;                            // 正在输入的显示字
    @BindView(R.id.view_bg)
    View viewBg;                                // 点击的按钮
    @BindView(R.id.iv_voice)
    ImageView ivVoice;                          // 扩散的背景

    private boolean isOpenVoice;

    private TextWatcher mTextWatcher;
    private ObjectAnimator animator;            // 录音扩散的动画

    public EssayExamBottomView(Context context) {
        super(context);
        initView(context);
    }

    public EssayExamBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EssayExamBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this, rootView);
        mSpeechRecognizerHt.onCreate(mContext, null);
        mSpeechRecognizerHt.setBottomView(this);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (tip_tv != null) {
            tip_tv.setText("点我答题");
        }
        animator = ObjectAnimator.ofFloat(viewBg, "alpha", 1f, 0f);
        // 设置线性差值器
        animator.setInterpolator(new LinearInterpolator());
        // 设置重复
        animator.setRepeatCount(ValueAnimator.INFINITE);
        // 设置持续事件
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                viewBg.setScaleX(1.5f - values / 2);
                viewBg.setScaleY(1.5f - values / 2);
            }
        });
        animator.setDuration(1000);
    }

    public void setInitView(EssayExamEditAnswer essayExamEditAnswer, TextView bottomLeftText, TextView tvTotal, ImageView essExInputCameraIv,
                            ImageView essExInputVoiceIv, ImageView essExInputSoftIv, LinearLayout llPost, ImageView ivPost, TextView tvPost) {
        this.essayExamEditAnswer = essayExamEditAnswer;
        this.bottomLeftText = bottomLeftText;
        this.tvTotal = tvTotal;
        this.essExInputCameraIv = essExInputCameraIv;
        this.essExInputVoiceIv = essExInputVoiceIv;
        this.essExInputSoftIv = essExInputSoftIv;

        this.llPost = llPost;
        this.ivPost = ivPost;
        this.tvPost = tvPost;

    }

    public void onViewClicked(int type) {
        if (essExInputVoiceIv == null) return;
        if (essExInputSoftIv == null) return;
        switch (type) {
            case 0:
                essExInputVoiceIv.setImageResource(R.mipmap.ee_voice_n);
                essExInputSoftIv.setImageResource(R.mipmap.ee_edit_s);
                essExInputCameraIv.setImageResource(R.mipmap.ee_photo_n);
                break;
            case 1:
                essExInputVoiceIv.setImageResource(R.mipmap.ee_voice_s);
                essExInputSoftIv.setImageResource(R.mipmap.ee_edit_n);
                essExInputCameraIv.setImageResource(R.mipmap.ee_photo_n);
                tip_tv.setText("点我答题");
                break;

            case 2:
                essExInputVoiceIv.setImageResource(R.mipmap.ee_voice_n);
                essExInputSoftIv.setImageResource(R.mipmap.ee_edit_n);
                break;
            case 3:
                essExInputVoiceIv.setImageResource(R.mipmap.ee_voice_n);
                essExInputSoftIv.setImageResource(R.mipmap.ee_edit_n);
                essExInputCameraIv.setImageResource(R.mipmap.ee_photo_n);
                break;

        }
    }

    public void setNormal() {
        if (essExInputVoiceIv == null) return;
        if (essExInputSoftIv == null) return;
        if (essExInputCameraIv == null) return;
        essExInputVoiceIv.setImageResource(R.mipmap.ee_voice_n);
        essExInputSoftIv.setImageResource(R.mipmap.ee_edit_n);
        essExInputCameraIv.setImageResource(R.mipmap.ee_photo_n);
    }

    public void setEditText(EditText editText, final int commitLimitLength, final int limitLength) {
        mSpeechRecognizerHt.setEditText(editText);
        addTextWatcher(editText, commitLimitLength, limitLength);
    }

    private void addTextWatcher(final EditText editText, final int commitLimitLength, final int inputLimitLength) {
        if (bottomLeftText == null || editText == null || editText.getText() == null) return;
        String trim = editText.getText().toString();
        StringUtils.WordsCount wordsCount = StringUtils.getStringLength(trim, 0);
        int length;
        if (wordsCount != null) {
            length = wordsCount.length;
        } else {
            length = 0;
        }
        resetPost();
        bottomLeftText.setText(length + "");
        tvTotal.setText("/" + commitLimitLength + "字");
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String trim = editText.getText().toString();
                Editable editable = editText.getText();
                // 计算字符串的长度 连续的字母或数字 算一个
                StringUtils.WordsCount wordsCount = StringUtils.getStringLength(trim, commitLimitLength);
                int length;
                if (wordsCount != null) {
                    length = wordsCount.length;
                } else {
                    length = 0;
                }
                resetPost();
                bottomLeftText.setText(length + "");
                tvTotal.setText("/" + commitLimitLength + "字");
                if (length > commitLimitLength) {
                    if (editText.getVisibility() == VISIBLE) {
                        ToastUtils.showEssayToast("录入字数超出限制");
                    }
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String newStr;
                    if (wordsCount != null) {
                        newStr = wordsCount.subContent;
                    } else {
                        newStr = "";
                    }
                    editText.setText(newStr);
                    editable = editText.getText();

                    // 新字符串的长度
                    int newLen = editable.length();
                    // 旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    // 设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                    resetPost();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editText.addTextChangedListener(mTextWatcher);
    }

    @OnClick({R.id.iv_voice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_voice:
                if (isOpenVoice) {
                    onStopRecognizer();
                } else {
                    onStart();
                }
                break;
        }
    }

    public void onStart() {
        isOpenVoice = true;
        mSpeechRecognizerHt.onClick(1);
        // 扩散开始
        animator.start();
        viewBg.setVisibility(VISIBLE);
        if (tip_tv != null) {
            tip_tv.setText("正在听写中...");
            ivVoice.setImageResource(R.mipmap.essay_ans_voice_btn);
        }
    }

    public void onStopRecognizer() {
        isOpenVoice = false;
        mSpeechRecognizerHt.onClick(2);
        // 扩散结束
        animator.cancel();
        viewBg.setVisibility(GONE);
        if (tip_tv != null) {
            tip_tv.setText("点我答题");
            ivVoice.setImageResource(R.mipmap.essay_ans_voice_begin_btn);
        }
    }

    public void setVoiceText(String content) {
        tip_tv.setText(content);
    }

    /**
     * 答题按钮的样式，是否可用
     * 还得判断套题
     */
    private void resetPost() {
        if (essayExamEditAnswer.isAlreadyAns()) {
            llPost.setClickable(true);
            llPost.setBackgroundResource(R.drawable.bg_essay_control_x);
            ivPost.setImageResource(R.mipmap.ee_edit_post);
            tvPost.setTextColor(getResources().getColor(R.color.pink250));
        } else {
            llPost.setClickable(false);
            llPost.setBackgroundResource(R.drawable.bg_essay_control_x_gray);
            ivPost.setImageResource(R.mipmap.ee_edit_post_gray);
            tvPost.setTextColor(getResources().getColor(R.color.outline_last_layer_text));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSpeechRecognizerHt != null) {
            mSpeechRecognizerHt.onDestroy();
        }
    }
}
