package com.huatu.handheld_huatu.network;

import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.me.bean.ExchangeVoucherBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.CourseBuyDetailBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.GetEvaluateBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HighEndBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.MianShouInfoBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.ShaixuanBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDefenBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDetailListBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherJieshaoBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherLishiBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherListBeans;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherPingjiaBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.setMianShouBean;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseDiss;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.utils.SpUtils;

import rx.Observable;

/**
 * @author zhaodongdong
 */
public class DataController {
    private static DataController mController;
    private HttpService mService;

    public static DataController getInstance() {
        if (mController == null) {
            synchronized (DataController.class) {
                if (mController == null) {
                    mController = new DataController();
                }
            }
        }
        return mController;
    }

    public DataController() {
        mService = RetrofitManager.getInstance().getService();
    }

    /**
     * 课程分享
     */
    public Observable<BaseResponseModel<ShareInfo>> sendClass(long id) {
        return mService.sendClass(id);
    }

    public Observable<BaseResponseModel<ShareInfo>> shareAthleticDaily() {
        return mService.shareAthleticDaily();
    }

    public Observable<BaseResponseModel<ShareInfo>> shareAthleticHome() {
        return mService.shareAthleticHome();
    }

    public Observable<BaseListResponseModel<AdvertiseConfig>> getSplashConfig(int catgory) {
        return mService.getSplashConfig(catgory);
    }

    public Observable<BaseListResponseModel<AdvertiseConfig>> getAdvertise() {
        return mService.getAdvertise(SpUtils.getUserCatgory() == -1 ? Type.SignUpType.CIVIL_SERVANT : SpUtils.getUserCatgory());
    }

    @Deprecated
    public Observable<HandoutBean> getHandoutData(int coutseId) {
        return mService.getHandoutInfo(coutseId);
    }

    /**
     * 我的直播隐藏课程
     */
    public Observable<CourseDiss> courseDiss(String courseIds, String orderIds) {
        return mService.courseDiss(courseIds, orderIds);
    }

    /**
     * 恢复隐藏课程
     */
    public Observable<CourseDiss> recoverCourseDiss(String courseIds, String orderIds) {
        return mService.recoverCourseDiss(courseIds, orderIds);
    }

    /**
     * 课程详情
     */
    public Observable<CourseBuyDetailBean> getCourseDetail(long courseId) {
        return mService.getCourseDetail(courseId);
    }


    /**
     * 教师列表接口
     */
    public Observable<BaseListResponseModel<TeacherListBeans>> getTeacherList(int courseId) {
        return mService.getTeacherList(courseId);
    }

    /**
     * 录播播放页面接口
     */
    public Observable<VodCoursePlayBean> getVodCoursePlay(int courseId) {
        return mService.getVodCoursePlay(courseId);
    }

    /**
     * 课程大纲免费试听
     */

    public Observable<VodCoursePlayBean> getCourseSyllabus(String courseId, int isTrial) {
        return mService.getCourseSyllabus(courseId, isTrial);
    }

    /**
     * 教师介绍接口
     */
    public Observable<TeacherJieshaoBean> getTeacherJieshao(int teacherId) {
        return mService.getTeacherJieshao(teacherId);
    }

    /**
     * 老师综合得分
     */
    public Observable<TeacherDefenBean> getTeacherDefen(int teacherId) {
        return mService.getTeacherDefen(teacherId);
    }

    /**
     * 老师历史课程
     */
    public Observable<TeacherLishiBean> getTeacherLishi(int teacherId, int page) {
        return mService.getTeacherLishi(teacherId, page);
    }

    /**
     * 老师在售课程列表
     */
    public Observable<TeacherDetailListBean> getTeacherDetailList(int teacherId, int page) {
        return mService.getTeacherDetailList(teacherId, page);
    }

    /**
     * 评价列表接口
     */
    public Observable<TeacherPingjiaBean> getTeacherPingjia(int classid, int lessionid, int page) {
        return mService.getTeacherPingjia(classid, lessionid, page);
    }

    /**
     * 获取自己对某个课程的评价
     */
    public Observable<GetEvaluateBean> getEvaluateContent(int classid, int lessionid) {
        return mService.getEvaluateContent(classid, lessionid);
    }

    /**
     * 提交评价
     */
    public Observable<ExchangeVoucherBean> commitEvaluate(int classid, int lessionid, String courseRemark, int coursescore, int type) {
        return mService.commitEvaluate(classid, lessionid, courseRemark, coursescore, type);
    }

    /**
     * 录播列表筛选条件
     */
    public Observable<ShaixuanBean> getVodCourseShaixuan() {
        return mService.getVodCourseShaixuan();
    }

    /**
     * 保存高端面授学员信息
     */
    public Observable<setMianShouBean> setHighend(HighEndBean info) {
        return mService.setHighend(info);
    }

    /**
     * 获取高端面授学员信息
     */
    public Observable<MianShouInfoBean> getHighend() {
        return mService.getHighend();
    }

    /**
     * 高端面授赠送课程
     */
    public Observable<BaseResponseModel<String>> getReceiveHighend() {
        return mService.getReceiveHighend();
    }
}
