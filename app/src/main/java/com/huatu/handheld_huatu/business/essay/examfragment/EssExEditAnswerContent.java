package com.huatu.handheld_huatu.business.essay.examfragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.common.PhotoInfo;
import com.huatu.handheld_huatu.business.common.PictureViewActivity;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamTimerHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.camera.CropImgActivity;
import com.huatu.handheld_huatu.business.essay.cusview.ObArrayList;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.AnswerImage;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.EssayEditImgAdapter;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.ImageUpResult;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.MyCallBack;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.MyGlideEngine;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.OnRecyclerItemClickListener;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.essay.net.FileUploadObserver;
import com.huatu.handheld_huatu.business.essay.net.UploadFileRequestBody;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.BitmapUtil;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.utils.StringUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import okhttp3.MultipartBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 答案编辑页面
 */
public class EssExEditAnswerContent extends BaseFragment {

    private static final String TAG = "EssExEditAnswerContent";

    private NestedScrollView scrollView;                // 跟布局
    private ExerciseTextView tvQuestion;                // 问题TextView
    private TextView tvSeeQuestion;                     // 提示语
    private ImageView ivHideQuestion;                   // 提示箭头

    private EditText etAnswer;                          // 问题输入框

    private RelativeLayout rlImg;                       // 图片布局
    private TextView tvImg;                             // 提示文字
    private RecyclerView rvImg;                         // 图片列表

    private TextView tvProofread;                       // 校对按钮
    private TextView tvDelete;                          // 删除图片按钮

    private Bundle extraArgs;
    private boolean isSingle;                           // 是否是单题
    private int index;                                  // 当前问题是第几个
    private int correctMode;                            // 批改方式

    private SingleQuestionDetailBean singleQuestionDetailBean;      // 问题内容

    private boolean isHideQuestion = true;                  // 是否隐藏问题

    private ObArrayList<AnswerImage> originImages;          // 选择的图片

    private EssayEditImgAdapter postArticleImgAdapter;      // 图片 RecycleView Adapter
    private ItemTouchHelper itemTouchHelper;                // 拖拽相关

    private EssayExamEditAnswer parentFragment;             // 父Fragment

    private AppPermissions rxPermissions;                   // 权限请求

    @Override
    public int onSetRootViewId() {
        return R.layout.ess_ex_edit_ans_content_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_START_EXAM
                || event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_NOT_START_EXAM) {
            initEditText();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {
            setTxtSize();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExCardViewShow_clearview) {
            if (tvQuestion != null) {
                tvQuestion.clearView();
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_CLEAR_CONTENT) {    // 答题页切换答题方式，这里把EditText作答内容清除
            if (correctMode == 2) {     // 人工批改
                etAnswer.setHint("请输入答案");
            } else {                    // 智能批改
                etAnswer.setHint("请输入答案");
            }
            if (etAnswer != null) {
                etAnswer.setText("");
            }
        }
        return true;
    }

    @Override
    protected void onInitView() {
        LogUtils.d(TAG, "EssExEditAnswerContent   onInitView");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        scrollView = rootView.findViewById(R.id.scroll_view);
        tvQuestion = rootView.findViewById(R.id.tv_question);
        tvSeeQuestion = rootView.findViewById(R.id.tv_see_question);
        ivHideQuestion = rootView.findViewById(R.id.iv_hide_question);
        etAnswer = rootView.findViewById(R.id.et_answer);
        rlImg = rootView.findViewById(R.id.rl_img);
        tvImg = rootView.findViewById(R.id.tv_img);
        rvImg = rootView.findViewById(R.id.rv_img);
        tvProofread = rootView.findViewById(R.id.tv_proofread);
        tvDelete = rootView.findViewById(R.id.tv_delete);

        if (args != null) {
            requestType = args.getInt("request_type");
            isSingle = args.getBoolean("isSingle");
            index = args.getInt("index");
            if (isSingle) {
                singleQuestionDetailBean = EssayExamDataCache.getInstance().cacheSingleQuestionDetailBean;
            } else {
                singleQuestionDetailBean = EssayExamDataCache.getInstance().cachePaperQuestionDetailBean.essayQuestions.get(index);
            }
            extraArgs = args.getBundle("extra_args");
            correctMode = args.getInt("correctMode");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
        }

        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

        rxPermissions = new AppPermissions(mActivity);

        rvImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (calculateRvHeight()) {
                    rvImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

            }
        });

        tvImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int measuredHeight = tvImg.getMeasuredHeight();
                if (measuredHeight > 0) {
                    rvImg.setPadding(DisplayUtil.dp2px(10), measuredHeight + DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(50));
                    tvImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        setTxtSize();

        // 初始化人工批改选择图片的东西
        initImgLayout();

        if (correctMode == 2) {     // 人工批改
            etAnswer.setHint("请输入答案");
            etAnswer.setVisibility(View.GONE);
            rlImg.setVisibility(View.VISIBLE);
            tvProofread.setVisibility(View.VISIBLE);
        } else {                    // 智能批改
            etAnswer.setHint("请输入答案");
            etAnswer.setVisibility(View.VISIBLE);
            rlImg.setVisibility(View.GONE);
            tvProofread.setVisibility(View.GONE);
        }

        showData();
    }

    @OnClick({R.id.view_hide_question, R.id.tv_proofread})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.view_hide_question:                   // 隐藏问题
                if (!isHideQuestion) {
                    isHideQuestion = true;
                    tvSeeQuestion.setText("查看问题");
                    ivHideQuestion.setImageResource(R.mipmap.ess_edit_ans_content_more);
                    tvQuestion.setVisibility(View.GONE);
                } else {
                    isHideQuestion = false;
                    tvSeeQuestion.setText("点击收起");
                    ivHideQuestion.setImageResource(R.mipmap.ess_edit_ans_content_less);
                    tvQuestion.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_proofread:
                getParent().checkReadAnswer();
                break;
        }
    }

    @Override
    protected void onLoadData() {

    }

    // 显示内容
    private void showData() {
        if (singleQuestionDetailBean != null) {
            // 设置问题
            tvQuestion.setHtmlSource(EssayHelper.getFilterTxt(singleQuestionDetailBean.answerRequire));
            tvQuestion.openCopy();

            if (correctMode == 2) {     // 人工批改，就要把图片设置进去
                if (singleQuestionDetailBean.userMeta != null && singleQuestionDetailBean.userMeta.size() > 0) {
                    for (AnswerImage answerImage : singleQuestionDetailBean.userMeta) {
                        answerImage.upState = 4;
                    }
                    if (originImages.size() == 1) {   // 有其他的
                        originImages.addAll(0, singleQuestionDetailBean.userMeta);
                    }
                    if (StringUtils.isEmpty(singleQuestionDetailBean.content)) {
                        StringBuilder content = new StringBuilder();
                        for (int i = 0; i < originImages.size() - 1; i++) {
                            AnswerImage answerImage = originImages.get(i);
                            content.append(EssayExamImpl.filterPhotoResultString(answerImage.content));
                        }
                        singleQuestionDetailBean.content = content.toString();
                    }
                } else {
                    getParent().changeTvProofreadState();
                }
                checkComplete(false);
            }
        }

        // 设置编辑框
        initEditText();
    }

    /**
     * 人工批改，选择图片的控件初始化
     */
    private void initImgLayout() {
        // 这只是为了防止 返回材料页，再回来，图片信息消失 切换答题卡/新进来是不会有东西的
        originImages = new ObArrayList<>();

        originImages.setChangeListener(new ObArrayList.ChangeListener() {
            @Override
            public void onChanged() {
                getParent().changeTvProofreadState();
            }
        });

        // 添加按钮图片资源
        AnswerImage bean = new AnswerImage();
        bean.originPath = imageTranslateUri(R.mipmap.mine_btn_plus);
        bean.upState = 4;
        // 添加按键，超过9张时在adapter中隐藏
        originImages.add(bean);

        // 判断列数 图片 最小宽度为100dp 最大宽度为200dp
        int miniWidth = DisplayUtil.dp2px(100);
        int maxWidth = DisplayUtil.dp2px(200);
        int colums = 3;
        int rvWidth = DisplayUtil.getScreenWidth() - DisplayUtil.dp2px(20);
        if (rvWidth > DisplayUtil.dp2px(300)) {     // 手机宽度小于320dp
            for (int i = 3; i < 100; i++) {
                float width = (float) rvWidth / i;
                if (miniWidth <= width && width <= maxWidth) {
                    colums = i;
                    break;
                }
            }
        }

        postArticleImgAdapter = new EssayEditImgAdapter(mActivity, originImages);
        rvImg.setLayoutManager(new StaggeredGridLayoutManager(colums, StaggeredGridLayoutManager.VERTICAL));
        rvImg.setAdapter(postArticleImgAdapter);

        MyCallBack myCallBack = new MyCallBack(mActivity, postArticleImgAdapter, originImages);
        itemTouchHelper = new ItemTouchHelper(myCallBack);
        itemTouchHelper.attachToRecyclerView(rvImg);           // 绑定RecyclerView

        // 点击监听
        rvImg.addOnItemTouchListener(new OnRecyclerItemClickListener(rvImg) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                int adapterPosition = vh.getAdapterPosition();
                if (originImages == null || originImages.size() == 0 || adapterPosition < 0 || adapterPosition >= originImages.size()) {
                    return;
                }
                if (adapterPosition == originImages.size() - 1) {
                    selectPic();
                } else {
                    AnswerImage answerImage = originImages.get(adapterPosition);
                    if (answerImage.upState == -4) {
                        readPhoto();
                    } else {
                        ArrayList<PhotoInfo> photos = new ArrayList<>();
                        for (int i = 0; i < originImages.size() - 1; i++) {
                            AnswerImage img = originImages.get(i);
                            PhotoInfo info = new PhotoInfo();
                            info.path = StringUtils.isEmpty(img.imageUrl) ? (StringUtils.isEmpty(img.path) ? img.originPath : img.path) : img.imageUrl;
                            photos.add(info);
                        }
                        // 查看照片时间为作答时间
                        EssayExamTimerHelper.getInstance().needRecordBgTime = true;
                        PictureViewActivity.show(mActivity, photos, adapterPosition);
                    }
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                // 如果item不是最后一个，则执行拖拽
                if (vh.getLayoutPosition() != originImages.size() - 1) {
                    itemTouchHelper.startDrag(vh);
                }
            }
        });

        // 拖拽监听
        myCallBack.setDragListener(new MyCallBack.DragListener() {
            @Override
            public void deleteState(boolean delete) {
                if (delete) {
                    tvDelete.setBackgroundColor(Color.parseColor("#cc0000"));
                    tvDelete.setText("松开删除");
                } else {
                    tvDelete.setBackgroundColor(Color.parseColor("#ff4444"));
                    tvDelete.setText("拖到这里删除");
                }
            }

            @Override
            public void dragState(boolean start) {
                if (start) {
                    tvDelete.setVisibility(View.VISIBLE);
                } else {
                    tvDelete.setVisibility(View.GONE);
                }
            }

            @Override
            public void clearView() {
                // 拖拽完成后设置答案内容
                checkComplete(false);
            }

            @Override
            public void deleteView(AnswerImage remove) {
                // 删除图片，这里要删除网络上的图片
//                if (remove != null && remove.id > 0
//                        && singleQuestionDetailBean != null && singleQuestionDetailBean.answerCardId > 0) {
//                    ServiceProvider.deletePicture(compositeSubscription, remove.id, singleQuestionDetailBean.answerCardId, new NetResponse());
//                }
            }
        });
    }

    private String imageTranslateUri(int resId) {
        Resources r = getResources();
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId);
    }

    /**
     * 压缩图片，并上传识别
     */
    public void readPhoto() {
        if (originImages != null && originImages.size() > 1) {
            ArrayList<AnswerImage> dealImages = new ArrayList<>(originImages);
            dealImages.remove(dealImages.size() - 1);
            Subscription subscribe = Observable.from(dealImages)
                    .map(new Func1<AnswerImage, AnswerImage>() {
                        @Override
                        public AnswerImage call(AnswerImage answerImage) {
                            // 如果内容不为空 || 正在压缩，就一定不用压缩
                            if (!StringUtils.isEmpty(answerImage.content) || answerImage.upState != 0) {
                                return answerImage;
                            }
                            answerImage.upState = 1;
                            // 如果路径是空，就压缩
                            if (StringUtils.isEmpty(answerImage.path) || !new File(answerImage.path).exists()) {
                                // 文件名称，创建文件路径
                                String fileName = singleQuestionDetailBean.sort + "-" + System.currentTimeMillis() + ".jpeg";
                                String filePath = FileUtil.getFilePath("EssayPictures", fileName);
                                BitmapUtil.compressBitmap(answerImage.originPath, filePath, 200);
                                answerImage.path = filePath;
                                if (!new File(answerImage.path).exists()) {
                                    answerImage.upState = 0;
                                    mActivity.hideProgress();
                                } else {
                                    answerImage.upState = 2;
                                }
                            }
                            return answerImage;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<AnswerImage>() {
                        @Override
                        public void onCompleted() {
                            upDatePhoto();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            ToastUtils.showEssayToast("压缩失败，点击校验重试！");
                            mActivity.hideProgress();
                        }

                        @Override
                        public void onNext(AnswerImage answerImage) {
                        }
                    });
            compositeSubscription.add(subscribe);
        }
    }

    /**
     * 上传图片识别
     */
    private void upDatePhoto() {
        if (originImages != null && originImages.size() > 1) {
            if (!NetUtil.isConnected()) {
                ToastUtil.showToast("无网络连接，请确认网络连接后重试");
                for (int i = 0; i < originImages.size() - 1; i++) {
                    final AnswerImage bean = originImages.get(i);
                    if (!StringUtils.isEmpty(bean.content)) {               // 识别过得，就不用识别了
                        bean.upState = 4;
                    } else if (bean.upState != 0) {
                        bean.upState = -4;
                    }
                }
                checkComplete(false);
                postArticleImgAdapter.notifyDataSetChanged();
                return;
            }
            for (int i = 0; i < originImages.size() - 1; i++) {
                final AnswerImage bean = originImages.get(i);
                if (!StringUtils.isEmpty(bean.content)) {               // 识别过得，就不用识别了
                    bean.upState = 4;
                    checkComplete(false);
                } else if (bean.upState == 2 || bean.upState == -4) {   // 只有压缩成功 || 上传失败 的话，才进行上传
                    File file = new File(bean.path);

                    FileUploadObserver progressObserver = new FileUploadObserver() {
                        @Override
                        public void onProgress(int progress) {
                            if (progress - bean.progress > 5 || progress == 100) {
                                bean.progress = progress;
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        postArticleImgAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    };

                    UploadFileRequestBody uploadFileRequestBody = new UploadFileRequestBody(file, progressObserver);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), uploadFileRequestBody);
                    bean.upState = 3;
                    postArticleImgAdapter.notifyDataSetChanged();

                    ServiceProvider.updateEssayPicture(compositeSubscription, part, singleQuestionDetailBean.answerCardId, bean.sort, new NetResponse() {
                        @Override
                        public void onSuccess(BaseResponseModel model) {
                            super.onSuccess(model);
                            if (model != null && model.data != null) {
                                ImageUpResult imageUpResult = (ImageUpResult) model.data;
                                if (StringUtils.isEmpty(imageUpResult.content)) {
                                    ToastUtils.showEssayToast("第" + (bean.sort + 1) + "张图片识别不到文字，请重新选择！");
                                }
                                bean.id = imageUpResult.id;
                                bean.content = EssayExamImpl.filterPhotoResultString(imageUpResult.content);
                                bean.sort = imageUpResult.sort;
                                bean.imageUrl = imageUpResult.url;
                                bean.upState = 4;
                                checkComplete(true);
                            } else {
                                bean.upState = -4;   // 回到压缩成功装
                                ToastUtils.showEssayToast("第" + (bean.sort + 1) + "张图片识别文字失败，请重新选择！");
                            }
                            postArticleImgAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            bean.upState = -4;       // 回到压缩成功装
                            if (e instanceof ApiException) {
                                ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                            } else {
                                ToastUtils.showEssayToast("上传失败,请稍后再试");
                            }
                            postArticleImgAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

    /**
     * 检查是否都识别完成
     *
     * @param forceSet 是不是要强制把识别完的文字内容放入编辑框
     *                 图片刚识别完成是一定要setText
     *                 图片改变顺序是一定要setText
     *                 前提是一定全部识别完成isOk
     */
    private void checkComplete(boolean forceSet) {
        if (originImages.size() <= 1) {
            return;
        }
        boolean isOk = true;
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < originImages.size() - 1; i++) {
            AnswerImage answerImage = originImages.get(i);
            if (answerImage.upState != 4) {
                isOk = false;
                break;
            }
            content.append(EssayExamImpl.filterPhotoResultString(answerImage.content));
        }
        // 一定要读取完 && (强制set(识别完了) || 改变顺序了)
        // 先执行一下排序
        if ((imagesSortChanged() || forceSet) && isOk) {
            etAnswer.setText(content.toString());
        }
    }

    // 是否拖拽改变了顺序
    private boolean imagesSortChanged() {
        boolean changed = false;
        if (originImages != null && originImages.size() > 0) {
            for (int i = 0; i < originImages.size(); i++) {
                if (originImages.get(i).sort != i) {
                    originImages.get(i).sort = i;
                    changed = true;
                }
            }
        }
        return changed;
    }

    private ObjectAnimator animationBottomIn;
    private ObjectAnimator animationBottomOut;

    // 显示校对页
    public void showEdit() {
        if (correctMode == 2) {     // 人工批改
            etAnswer.setHint("请输入答案");
        } else {                    // 智能批改
            etAnswer.setHint("请输入答案");
        }
        etAnswer.setVisibility(View.VISIBLE);
        rlImg.setVisibility(View.GONE);
        if (tvProofread.getVisibility() == View.GONE) return;
        if (animationBottomOut == null) {
            animationBottomOut = ObjectAnimator.ofFloat(tvProofread, "translationY", 0, DisplayUtil.dp2px(50));
            animationBottomOut.setDuration(200);
            animationBottomOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvProofread.setVisibility(View.GONE);
                }
            });
        }
        animationBottomOut.start();
    }

    // 隐藏校对页
    public void hideEdit() {
        etAnswer.setVisibility(View.GONE);
        rlImg.setVisibility(View.VISIBLE);
        if (tvProofread.getVisibility() == View.VISIBLE) return;
        if (animationBottomIn == null) {
            animationBottomIn = ObjectAnimator.ofFloat(tvProofread, "translationY", DisplayUtil.dp2px(50), 0);
            animationBottomIn.setDuration(200);
            animationBottomIn.setStartDelay(210);
            animationBottomIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvProofread.setVisibility(View.VISIBLE);
                }
            });
        }
        animationBottomIn.start();
    }

    /**
     * 改变选择图片的个数，修改校对按钮的状态
     */
    public void changeTvProofreadState(boolean canClick) {
        if (tvProofread == null) return;
        if (canClick) {     // 可校对
            tvProofread.setClickable(true);
            tvProofread.setBackgroundResource(R.drawable.essay_edit_proofread_bg);
            tvProofread.setTextColor(Color.parseColor("#EC74A0"));
        } else {            // 不可校对
            tvProofread.setClickable(false);
            tvProofread.setBackgroundResource(R.drawable.essay_edit_proofread_no_bg);
            tvProofread.setTextColor(Color.parseColor("#9B9B9B"));
        }
    }

    // 计算图片RecyclerView的高度
    // 至少填充到屏幕底部，为了拖拽图片到底部删除操作
    private boolean calculateRvHeight() {
        int[] location = new int[2];
        rvImg.getLocationOnScreen(location);    // 获取在整个屏幕内的绝对坐标

        // 获取View可见区域的bottom
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        if (location[1] > 0 && rect.bottom > 0) {
            rvImg.setMinimumHeight(rect.bottom - location[1]);
            rvImg.requestLayout();
            return true;
        }
        return false;
    }

    private EssayExamEditAnswer getParent() {
        if (parentFragment == null) {
            Fragment fragment = getParentFragment();
            if (fragment != null) {
                parentFragment = (EssayExamEditAnswer) fragment;
            }
        }
        return parentFragment;
    }


    /**
     * 选择照片
     */
    private void selectPic() {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            CommonUtils.showToast("获取权限失败");
                            return;
                        }
                        // 人工选择图片需要记录后台时间
                        EssayExamTimerHelper.getInstance().needRecordBgTime = true;
                        Matisse.from(mActivity)
                                // 选择图片
                                .choose(MimeType.ofImage())
                                // 是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                                .showSingleMediaType(true)
                                // 这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                                .capture(true)
                                .captureStrategy(new CaptureStrategy(true, "com.huatu.handheld_huatu.fileprovider"))
                                // 有序选择图片 123456...
                                .countable(true)
                                // 最大选择数量为9
                                .maxSelectable(10 - originImages.size())
                                // 选择方向
//                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                // 界面中缩略图的质量
                                .thumbnailScale(0.8f)
                                // 黑色主题
                                .theme(R.style.Matisse_Dracula)
                                // Glide加载方式
                                .imageEngine(new MyGlideEngine())
                                // 请求码
                                .forResult(singleQuestionDetailBean.sort);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        // 相册选择照片返回
        if (requestCode == singleQuestionDetailBean.sort) {
            List<String> strings = Matisse.obtainPathResult(data);
            if (strings != null && strings.size() > 0) {
                if (strings.size() == 1) {      // 单张图片去裁剪

                    // "originPath"     原始图片路径
                    // "targetPath"     存储目标图片路径

                    String originPath = strings.get(0);
                    if (FileUtil.isFileExist(originPath)) {
                        // 去裁剪，不要resume添加时间
                        EssayExamTimerHelper.getInstance().needRecordBgTime = false;
                        goCrop(originPath);
                    } else {
                        ToastUtils.showEssayToast("图片不存在，请重新选择！");
                    }

                } else {                        // 多张图片不裁剪
                    for (String path : strings) {
                        // LogUtils.e("onActivityResult",path);
                        if (FileUtil.isFileExist(path)) {
                            AnswerImage bean = new AnswerImage();
                            bean.originPath = path;
                            originImages.add(originImages.size() - 1, bean);
                        } else {
                            ToastUtils.showEssayToast("图片不存在，请重新选择！");
                        }
                    }
                    if (originImages != null && originImages.size() > 0) {
                        for (int i = 0; i < originImages.size(); i++) {
                            originImages.get(i).sort = i;
                        }
                    }
                    postArticleImgAdapter.notifyDataSetChanged();
                    // 选择完图片就压缩上传识别
                    readPhoto();
                }
            }
        } else if (requestCode == singleQuestionDetailBean.sort + 50) {    // 裁剪成功
            // 裁剪回来，需要resume添加时间
            EssayExamTimerHelper.getInstance().needRecordBgTime = true;
            String targetPath = data.getStringExtra("targetPath");
            if (StringUtils.isEmpty(targetPath)) {
                ToastUtils.showEssayToast("裁剪失败！");
            } else if (!new File(targetPath).exists()) {
                ToastUtils.showEssayToast("裁剪失败！");
            } else {
                AnswerImage bean = new AnswerImage();
                bean.originPath = targetPath;
                bean.sort = originImages.size() - 1;
                originImages.add(bean.sort, bean);

                postArticleImgAdapter.notifyDataSetChanged();
                // 选择完图片就压缩上传识别
                readPhoto();
            }
        }
    }

    private void goCrop(final String originPath) {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            CommonUtils.showToast("获取权限失败");
                            return;
                        }
                        String fileName = singleQuestionDetailBean.sort + "-" + System.currentTimeMillis() + ".jpeg";
                        String filePath = FileUtil.getFilePath("EssayPictures", fileName);

                        Bundle bundle = new Bundle();
                        bundle.putString("originPath", originPath);
                        bundle.putString("targetPath", filePath);
                        Intent intent = new Intent(mActivity, CropImgActivity.class);
                        intent.putExtra("extra_args", bundle);
                        startActivityForResult(intent, singleQuestionDetailBean.sort + 50);
                    }
                });
    }

    private void initEditText() {
        if (etAnswer != null) {
            if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {
                if (!EssayExamTimerHelper.getInstance().isEnableExam()) {
                    etAnswer.clearFocus();
                    etAnswer.setEnabled(false);
                    etAnswer.setHint("开始答题时间未到，不能作答");
                    etAnswer.setText("");
                } else {
                    if (singleQuestionDetailBean != null) {
                        etAnswer.setText(singleQuestionDetailBean.content);
                    }
                    etAnswer.requestFocus();
                    etAnswer.setEnabled(true);
                    etAnswer.setHint("请输入答案");
                }
            } else {
                if (singleQuestionDetailBean != null) {
                    etAnswer.setText(singleQuestionDetailBean.content);
                }
                etAnswer.requestFocus();
            }
        }
        TimeUtils.delayTask(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 50);
    }

    public void showHideSoftInput(int type) {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        if (type == EssayExamMessageEvent.EssayExam_show_soft) {
            InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (manager != null) {
                if (!isSoftShowing()) {
                    etAnswer.requestFocus();
                    manager.showSoftInput(etAnswer, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    manager.hideSoftInputFromWindow(etAnswer.getWindowToken(), 0);
                }
            }
        } else if (type == EssayExamMessageEvent.EssayExam_hide_soft) {
            InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (manager != null) {
                if (isSoftShowing()) {
                    manager.hideSoftInputFromWindow(etAnswer.getWindowToken(), 0);
                }
            }
        }
    }

    private boolean isSoftShowing() {
        if (Method.isActivityFinished(mActivity)) {
            return false;
        }
        if (mActivity != null) {
            //获取当前屏幕内容的高度
            int screenHeight = mActivity.getWindow().getDecorView().getHeight();
            //获取View可见区域的bottom
            Rect rect = new Rect();
            mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            return screenHeight - rect.bottom != 0;
        }
        return false;
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (!isVisibleToUser) {
            clearView();
        }
    }

    public void clearView() {
        if (tvQuestion != null) {
            tvQuestion.clearView();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        calculateRvHeight();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (EssayExamDataCache.getInstance().correctMode == 2) {
            if (singleQuestionDetailBean != null && originImages != null && originImages.size() >= 2) {
                if (singleQuestionDetailBean.userMeta == null) {
                    singleQuestionDetailBean.userMeta = new ObArrayList<>();
                }
                singleQuestionDetailBean.userMeta.clear();
                for (int i = 0; i < originImages.size() - 1; i++) {
                    singleQuestionDetailBean.userMeta.add(originImages.get(i));
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void appendInputAnsStr(String inputAnsStr) {
        etAnswer.append(inputAnsStr);
    }

    public EditText getEditV() {
        return etAnswer;
    }

    public ObArrayList<AnswerImage> getOriginImages() {
        return originImages;
    }

    public static EssExEditAnswerContent newInstance(Bundle extra) {
        EssExEditAnswerContent fragment = new EssExEditAnswerContent();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

    /**
     * 改变字体大小
     */
    private void setTxtSize() {
        float textSize = EssayExamDataCache.getInstance().materialsTxtSize;
        if (textSize > 0) {
            tvQuestion.setTextSize(textSize);
            etAnswer.setTextSize(textSize);
        }
    }
}
