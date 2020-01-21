package com.huatu.handheld_huatu.datacache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamAreaActivity;
import com.huatu.handheld_huatu.business.arena.activity.DownloadArenaPaperActivity;
import com.huatu.handheld_huatu.business.arena.activity.EvaluateReportActivityNew;
import com.huatu.handheld_huatu.business.arena.activity.SimulationExamActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.matchsmall.activity.SmallMatchActivity;
import com.huatu.handheld_huatu.business.me.CollectionActivity;
import com.huatu.handheld_huatu.business.me.ErrorActivity;
import com.huatu.handheld_huatu.business.me.RecordActivity;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.Category;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by michael on 17/7/20.
 * 修改记录：
 * 2019-4-22：军队文职；事业单位里：职测ABC和非联考四个小类；添加模考大赛分类
 * <p>
 * 公务员（行测）：     智能刷题、模考大赛、小模考、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 公务员（申论）：     套题、文章写作、模考大赛、批改记录、收藏、下载
 * <p>
 * 事业单位（公基）：		模考大赛、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 事业单位（联考A类）：	智能刷题、模考大赛、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 事业单位（联考B类）：	智能刷题、模考大赛、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 事业单位（联考C类）：	智能刷题、模考大赛、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 事业单位（非联考）： 	智能刷题、模考大赛、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * <p>
 * 教师招聘：				        模考大赛、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 教师资格证（小学：综素、教知）：	模考大赛、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 教师资格证（中学：综素、教知）：	模考大赛、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * <p>
 * 招警考试：			智能刷题、评估报告、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 军队文职：			智能刷题、模考大赛、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 国家电网：			智能刷题、精准估分、真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 * 金融                  真题演练、错题重练、精准估分、答题记录、收藏夹、模考大赛、专项模考、试卷下载、每日特训
 * <p>
 * 无论小类或大类默认新增：	真题演练、错题重练、专项模考、答题记录、收藏夹、试卷下载、每日特训
 */
public class SignUpTypeDataCache {

    private static volatile SignUpTypeDataCache instance = null;

    public static SignUpTypeDataCache getInstance() {
        synchronized (SignUpTypeDataCache.class) {
            if (instance == null) {
                instance = new SignUpTypeDataCache();
            }
        }
        return instance;
    }

    public long dTime = 0;                                          // 服务器时间 - 本地时间

    private List<HomeIconBean> homeIconsList;                       // 首页的一横排按钮

    private int mCurCategory = -1;                                  // 当前的报考类型 大科目
    private int curSubject = -1;                                    // 小科目

    public List<Category> dataList;                                 // 所有的科目类型 大科目和小科目
    public Category curCategory;                                    // 当前选择的大科目

     private List<CourseCategoryBean> courseCategoryList;            // 这是视频相关的东西吧
   // private List<CourseCategoryBean> vodcourseCategoryList;

    private SignUpTypeDataCache() {
        initDataList();         // 获取本地存储的科目信息
        reSetData();            // 初始化数据
    }

    // 读取本地的科目类型缓存
    private void initDataList() {
        if (dataList == null) {
            String subjectList = SpUtils.getHtSubjectList();
            List<Category> varlist = (List<Category>) GsonUtil.string2JsonObject(subjectList, new TypeToken<List<Category>>() {
            }.getType());
            if (varlist != null && varlist.size() > 0) {
                dataList = varlist;
            }
        }
    }

    public void reSetData() {
        initCategorySubject();                          // 初始化本地科目信息
        setTypeBeanList();                              // 初始化本地图标信息
        refreshCurSignUpTypeBean();                     // 取出当前大科目
        UniApplicationContext.updatePushAgentTag();     // 友盟推送初始化吧
    }

    // 初始化科目信息
    private void initCategorySubject() {
        mCurCategory = SpUtils.getUserCatgory();
        curSubject = SpUtils.getUserSubject();

        //数据出现不一致，重置
        if (mCurCategory <= 0 || curSubject <= 0) {
            mCurCategory = Type.SignUpType.CIVIL_SERVANT;
            curSubject = 1;

            SpUtils.setUserCatgory(mCurCategory);
            SpUtils.setUserSubject(curSubject);
        }
    }

    private void refreshCurSignUpTypeBean() {
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                Category category = dataList.get(i);
                if (category != null) {
                    if (category.id == mCurCategory) {
                        curCategory = category;
                        break;
                    }
                }
            }
        }
    }

    // 根据大科目id获取第一个子科目的id
    public Integer fromCategory2Subject(int cat) {
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                Category category = dataList.get(i);
                if (category != null) {
                    if (category.id == cat && category.childrens != null && category.childrens.size() > 0) {
                        return category.childrens.get(0).id;
                    }
                }
            }
        }
        return null;
    }

    // 是否大于3个子科目
    public boolean isHasChildrenMore() {
        if (curCategory != null && curCategory.childrens != null && curCategory.childrens.size() > 3) {
            return true;
        }
        return false;
    }

    private void setTypeBeanList() {
        if (homeIconsList == null) {
            homeIconsList = new ArrayList<>();
        }
        homeIconsList.clear();
        if (getCurCategory() == Type.SignUpType.CIVIL_SERVANT) {                         // 公务员
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, "模考大赛", R.mipmap.homef_pt_simulation_contest_new);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, "智能刷题", R.mipmap.homef_pt_ai_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, "小模考", R.mipmap.homef_pt_small_match);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.START_EVALUATE_REPORT, "评估报告", R.mipmap.homef_pt_eve_report);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
        } else if (getCurCategory() == Type.SignUpType.PUBLIC_INSTITUTION) {             // 事业单位
            if (getCurSubject() == Type.PB_ExamType.PUBLIC_BASE) {  // 公基
                addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, "模考大赛", R.mipmap.homef_pt_simulation_contest_new);
            } else {                                                // 其他
                addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, "智能刷题", R.mipmap.homef_pt_ai_exercise);
                addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, "模考大赛", R.mipmap.homef_pt_simulation_contest_new);
            }
            addSignUpExamPaperTypeBean(ArenaConstant.START_EVALUATE_REPORT, "评估报告", R.mipmap.homef_pt_eve_report);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real_fp);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        } else if (getCurCategory() == Type.SignUpType.TEACHER_T
                || getCurCategory() == Type.SignUpType.TEACHER_PRIMARY
                || getCurCategory() == Type.SignUpType.TEACHER_MIDDLE) {                 // 教师题库、教师小学、教师中学
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, "模考大赛", R.mipmap.homef_pt_simulation_contest_new);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        } else if (getCurCategory() == Type.SignUpType.PUBLIC_SECURITY) {                // 招警考试
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, "智能刷题", R.mipmap.homef_pt_ai_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.START_EVALUATE_REPORT, "评估报告", R.mipmap.homef_pt_eve_report);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real_fp);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        } else if (getCurCategory() == Type.SignUpType.MILITARY_CIVILIAN) {              // 军转文职
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, "智能刷题", R.mipmap.homef_pt_ai_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, "模考大赛", R.mipmap.homef_pt_simulation_contest_new);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real_fp);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        } else if (getCurCategory() == Type.SignUpType.STATE_GRID) {                     // 国家电网
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, "智能刷题", R.mipmap.homef_pt_ai_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real_fp);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        } else if (getCurCategory() == Type.SignUpType.FINANCIAL_T) {                    // 金融 真题演练、错题重练、精准估分、答题记录、收藏夹、模考大赛、专项模考、试卷下载、每日特训
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real_fp);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, "精准估分", R.mipmap.homef_pt_exam_esti_score);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, "模考大赛", R.mipmap.homef_pt_simulation_contest_new);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        } else {
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, "真题演练", R.mipmap.homef_pt_exam_real_fp);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, "错题重练", R.mipmap.homef_pt_error_paper_exercise);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, "专项模考", R.mipmap.homef_pt_exam_simulation);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_RECORD, "答题记录", R.mipmap.homef_pt_exam_recorder);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_FAVORITE_COLLECT, "收藏夹", R.mipmap.homef_pt_exam_collection);
            addSignUpExamPaperTypeBean(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, "下载", R.mipmap.homef_pt_exam_download);
            addSignUpExamPaperTypeBean(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, "每日特训", R.mipmap.homef_pt_special_daily);
        }
    }

    private void addSignUpExamPaperTypeBean(int requestType, String name, int icon) {
        HomeIconBean iconBean = new HomeIconBean();
        iconBean.requestType = requestType;
        iconBean.name = name;
        iconBean.icon = icon;
        homeIconsList.add(iconBean);
    }

    //@LoginTrace(type = 0)
    public void startActivity(final Context cxt, int paperType, CompositeSubscription compositeSubscription) {
        if (!(cxt instanceof Activity)) return;
        switch (paperType) {
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN:                         // 真题演练（传入小分类getCurSubject()）
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "真题演练");
                ArenaExamAreaActivity.newInstance(cxt, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST:                     // 模考大赛（传入小分类getCurSubject()）
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "模考大赛");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                MatchCacheData.getInstance().matchPageFrom = "点击模考大赛";
                Intent intent = new Intent(cxt, SimulationContestActivityNew.class);
                intent.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
                cxt.startActivity(intent);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "智能刷题");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                Bundle bundle = new Bundle();
                ArenaDataCache.getInstance().isShowedAiTips = false;
                ArenaExamActivityNew.show((Activity) cxt, ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, bundle);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH:
                StudyCourseStatistic.clickStatistic("题库", "行测", "小模考");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                SmallMatchActivity.show(cxt, ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, new Bundle());
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "精准估分");
                SimulationExamActivity.newIntent(cxt, ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, null);
                break;
            case ArenaConstant.START_EVALUATE_REPORT:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "评估报告");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                Intent intentEva = new Intent(cxt, EvaluateReportActivityNew.class);
                cxt.startActivity(intentEva);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "错题重练");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                ErrorActivity.newInstance(cxt);
                break;
            case ArenaConstant.START_PAGER_RECORD:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "答题记录");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                RecordActivity.newInstance(cxt, 0);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "每日特训");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_GET_DAILYINFO));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "专项模考");
                SimulationExamActivity.newIntent(cxt, ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, null);
                break;
            case ArenaConstant.START_PAGER_FAVORITE_COLLECT:
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "收藏夹");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                CollectionActivity.newInstance(cxt);
                break;
            case ArenaConstant.START_PAGER_DOWNLOAD_PAPER:                                  // 题库试卷下载（getCurSubject()）
                StudyCourseStatistic.clickStatistic("题库", "页面第三模块", "试卷下载");
                if (!CommonUtils.checkLogin(cxt)) {
                    return;
                }
                Intent intentDown = new Intent(cxt, DownloadArenaPaperActivity.class);
                intentDown.putExtra("curSubject", getCurSubject());
                cxt.startActivity(intentDown);
                break;
        }
    }

    // 根据网络返回的首页icon图标，附上本地的类型和图片资源
    public Point getRequestType(String type) {
        switch (type) {
            case "ZTYL":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, R.mipmap.homef_pt_exam_real_fp);
            case "MKDS":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, R.mipmap.homef_pt_simulation_contest_new);
            case "ZNST":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, R.mipmap.homef_pt_ai_exercise);
            case "XMK":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, R.mipmap.homef_pt_small_match);
            case "JZGF":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN, R.mipmap.homef_pt_exam_esti_score);
            case "PGBG":
                return new Point(ArenaConstant.START_EVALUATE_REPORT, R.mipmap.homef_pt_eve_report);
            case "CTCL":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, R.mipmap.homef_pt_error_paper_exercise);
            case "DTJL":
                return new Point(ArenaConstant.START_PAGER_RECORD, R.mipmap.homef_pt_exam_recorder);
            case "MRTX":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, R.mipmap.homef_pt_special_daily);
            case "ZXMK":
                return new Point(ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN, R.mipmap.homef_pt_exam_simulation);
            case "SC":
                return new Point(ArenaConstant.START_PAGER_FAVORITE_COLLECT, R.mipmap.homef_pt_exam_collection);
            case "XZ":
                return new Point(ArenaConstant.START_PAGER_DOWNLOAD_PAPER, R.mipmap.homef_pt_exam_download);
        }
        return new Point();
    }

    public int getCurCategory() {
        return mCurCategory;
    }

    //subject  科目（eg行测 申论）
    public void setCurSubject(int subject) {
        this.curSubject = subject;
    }

    public int getCurSubject() {
        return curSubject;
    }

    public int getSignUpType() {
        return mCurCategory;
    }

    public List<HomeIconBean> getHomeIconsList() {
        return homeIconsList;
    }

    public String getCategoryTitle() {
        String title = "";
        if (curCategory != null) {
            title = curCategory.name;
        }
        return title;
    }

    public String getSubjectTitle() {
        String title = "";
        if (curCategory != null) {
            if (curCategory.childrens != null && curCategory.childrens.size() >
                    0) {
                for (int i = 0; i < curCategory.childrens.size(); i++) {
                    Category.Subject cvar = curCategory.childrens.get(i);
                    if (cvar != null) {
                        if (cvar.id == curSubject) {
                            title = cvar.name;
                            break;
                        }
                    }
                }
            } else {
                title = curCategory.name;
            }
        }
        return title;
    }

    public String getSubjectTitle(int pos) {
        String title = "";
        if (curCategory != null) {
            if (curCategory.childrens != null && curCategory.childrens.size() > pos) {
                Category.Subject cvar = curCategory.childrens.get(pos);
                if (cvar != null) {
                    title = cvar.name;
                }
            }
        }
        return title;
    }

    public void getCategoryListNet(int type, CompositeSubscription compositeSubscription, final Object event) {
        if (type == 0) {
            if (dataList != null) {
                return;
            }
        }
        ServiceProvider.getSignUpDataList(compositeSubscription, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                setCategoryDataList(model.data);
                writeSpRecordSubjects(model.data);
                reSetData();
                if (event != null) {
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onError(final Throwable e) {
            }
        });
    }

    private void writeSpRecordSubjects(List<Category> dataList) {
        String var = GsonUtil.toJsonStr(dataList);
        if (var != null) {
            SpUtils.setHtSubjectList(var);
        }
    }

    private void setCategoryDataList(List<Category> dataList) {
        this.dataList = dataList;
    }


    // --------这是视频相关的东西吧
    public void setCourseCategoryList(List<CourseCategoryBean> dataList) {
        courseCategoryList = dataList;
    }

    public List<CourseCategoryBean> getCourseCategoryList() {
        List<CourseCategoryBean> dataList = new ArrayList<>();
        if (courseCategoryList != null) {
            for (CourseCategoryBean bean : courseCategoryList) {
                dataList.add(bean.clone());
            }
        }
        return dataList;
    }

    /*public void setVodCourseCategoryList(List<CourseCategoryBean> dataList) {
       // vodcourseCategoryList = dataList;
    }

    public List<CourseCategoryBean> getVodCourseCategoryList() {
        List<CourseCategoryBean> dataList = new ArrayList<>();
        if (vodcourseCategoryList != null) {
            for (CourseCategoryBean bean : vodcourseCategoryList) {
                dataList.add(bean.clone());
            }
        }
        return dataList;
    }*/
}
