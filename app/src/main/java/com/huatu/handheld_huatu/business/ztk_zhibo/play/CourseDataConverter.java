package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.text.TextUtils;

import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.music.bean.Music;
import com.huatu.music.player.PlayManager;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/10/5.
 */

public class CourseDataConverter {


    public static CourseWareInfo convertMusicToCourseware(Music music,CourseWareInfo currentInfo){
         if(currentInfo==null){
             currentInfo=new CourseWareInfo();
         }
         currentInfo.coursewareId=StringUtils.parseInt(music.mid);
         currentInfo.title=music.title;
         currentInfo.teacher=music.artist;
         currentInfo.hasTeacher=music.isCp ?1:0;
         currentInfo.id=music.artistId;
         if(music.type.equals("local")){
               currentInfo.downStatus= DownBtnLayout.FINISH;
         }else
             currentInfo.downStatus=0;

         currentInfo.videoId=music.id+"";
         currentInfo.token=music.collectId;
         currentInfo.videoType=5;
         currentInfo.classId=music.quality;
         currentInfo.parentId=music.coverSmall;
         return currentInfo;
    }

    public static   Music convertCoursewareToMusic(CourseWareInfo courseWareInfo,CourseInfoBean courseInfo){
        return convertCoursewareToMusic(courseWareInfo,courseInfo,true);
    }

    public static   Music convertCoursewareToMusic(CourseWareInfo courseWareInfo,CourseInfoBean courseInfo,boolean checkType){

        //"videoId":"16091936","token":"w_ZgK9dOGftMyA0LsBVeTLKXgwsBKRfiE0xsO4HlQ9acpc4W_VwHrDTSEzZrILF4"

            if(checkType&&(courseWareInfo.videoType!=5)) return null;

            Music tmpDto=new Music();
            tmpDto.type="online";
            //tmpDto.id=54;
            tmpDto.mid=String.valueOf(courseWareInfo.coursewareId);
            tmpDto.title=courseWareInfo.title;
            tmpDto.album=courseWareInfo.title;
            tmpDto.artist=courseWareInfo.teacher;
            if(null!=courseInfo){
                tmpDto.album=courseInfo.title;
                tmpDto.albumId=String.valueOf(courseInfo.courseId);
            }

            tmpDto.artistId=courseWareInfo.id;

            tmpDto.trackNumber=0;
            tmpDto.duration=0;
            tmpDto.isLove=false;
            tmpDto.isOnline=true;
            tmpDto.isCp=courseWareInfo.hasTeacher==1? true:false;

        //  tmpDto.uri="http://audio01.dmhmusic.com/71_53_T10047981991_128_4_1_0_sdk-cpm/0207/M00/7B/93/ChR461z87ZCAcB1eAD_XaNRR9N4986.mp3?xcode=e39838a8d114bbea4f4df19d92731d19f5f4796";
          /*  tmpDto.coverUri="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_150,h_150";
            tmpDto.coverBig="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_450,h_450";
            tmpDto.coverSmall="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_90,h_90";*/

            if(null!=courseInfo){
                tmpDto.coverUri=courseInfo.scaleimg;
                tmpDto.coverBig=courseInfo.scaleimg;
            }

            tmpDto.coverSmall=courseWareInfo.parentId;
            tmpDto.fileSize=4183912;
            tmpDto.date=0;
            tmpDto.isDl=true;
            tmpDto.quality=courseWareInfo.classId;

            if(!TextUtils.isEmpty(courseWareInfo.targetPath)){
                tmpDto.uri=courseWareInfo.targetPath;
                tmpDto.type="local";
            }
            tmpDto.id=StringUtils.parseLong(courseWareInfo.videoId);
            tmpDto.collectId=courseWareInfo.token;
            return tmpDto;
    }

    public static CourseWareInfo convertLocalLessonToCourseware(DownLoadLesson cuLession ,boolean isPlayBack){
        CourseWareInfo info = new CourseWareInfo();
        info.offSignalFilePath = cuLession.getSignalFilePath();
        info.targetPath = cuLession.getPlayPath();
        info.title = cuLession.getSubjectName();
        info.downStatus = DownBtnLayout.FINISH;
        info.bjyRoomId = cuLession.getRoomId();
        info.bjySessionId = cuLession.getSessionId();
        info.token = cuLession.getVideoToken();

        // 直播回放
        if(isPlayBack){
           info.videoType =3;
           info.liveStatus = 1;
        }
        else{
            info.videoType =cuLession.getPlayerType()==PlayerTypeEnum.BjAudio.getValue()? 5: 1;
            info.videoId=cuLession.getRoomId();
        }

        info.coursewareId = Integer.parseInt(cuLession.getSubjectID());
        return info;

    }

    //课件大纲转本地下载
    public static DownLoadCourse convertCatalogInfoListToDownCourse(
            CourseInfoBean buyDetailInfo, PurchasedCourseBean.Data catalogInfo) {

           List<CourseWareInfo> tmpList=new ArrayList<>();
           tmpList.add(catalogInfo);
           return convertCatalogInfoListToDownCourse(buyDetailInfo,tmpList);
    }

    //仅添加课程数据
    public static DownLoadCourse convertCatalogInfoListToDownCourse( CourseInfoBean buyDetailInfo) {

        List<CourseWareInfo> tmpList=new ArrayList<>();
        CourseWareInfo tmpDto=new CourseWareInfo();
        tmpDto.videoType=2;
        tmpList.add(tmpDto);
        return convertCatalogInfoListToDownCourse(buyDetailInfo,tmpList);
    }

     public static DownLoadCourse convertCatalogInfoListToDownCourse(
            CourseInfoBean buyDetailInfo, List<CourseWareInfo> cataloglists) {

         DownLoadCourse course = new DownLoadCourse();
         if (buyDetailInfo != null && cataloglists.size() > 0) {

             String userCourseID=UserInfoUtil.userId+"_"+buyDetailInfo.courseId;//因为是主键，添加用户区分
             List<DownLoadLesson> coursewares = new ArrayList<>();
             for (int i = 0; i < cataloglists.size(); i++) {
                 CourseWareInfo videoBean = cataloglists.get(i);

                 //1点播2直播3直播回放 5音频课程  	number	@mock=0  添加类型4为离线展示
                 if(videoBean.videoType==2) continue;

                 DownLoadLesson courseware = new DownLoadLesson();

                 //添加统计用，不参与到本地数据存储
                 courseware.classId=videoBean.classId;

                 courseware.setCourseID(userCourseID);
                 courseware.setSubjectID(String.valueOf(videoBean.coursewareId));
                 courseware.setSubjectName(videoBean.title);
                // courseware.setCourseNum();这个字段没有用到
                // courseware.setCourseNum(videoBean.id);
                 courseware.setLesson(videoBean.getSerialNumber());
                 courseware.setDownStatus(0);
                 courseware.setSpace(videoBean.getfileSize());

                 //.toJson(videoBean, VideoBean.LiveCourseParams.class)
                // courseware.setPlayParams(GsonUtil.toJson(videoBean, VideoBean.LiveCourseParams.class));

                 //添加REVERSE_1作为 录播有没有老师字段 REVERSE_2为老师名字
                 courseware.setReserve1(String.valueOf(videoBean.hasTeacher));
                 courseware.setReserve2(videoBean.teacher);
                 courseware.catalogId=StringUtils.parseInt(videoBean.id);
                 courseware.parentId=StringUtils.parseInt(videoBean.parentId);

                 if(videoBean.videoType == 1||videoBean.videoType==5) {//录播 或者 音频课程

                     if(videoBean.videoType == 1){
                         courseware.setPlayerType(PlayerTypeEnum.BjRecord.getValue());
                     }else {
                         courseware.setPlayerType(PlayerTypeEnum.BjAudio.getValue());
                     }

                     courseware.setDownloadID(UserInfoUtil.userId+videoBean.videoId );
                     courseware.setClarity(0);
                     courseware.setEncryptType(0);
                     courseware.setVideoToken(videoBean.token);
                     courseware.setRoomId(videoBean.videoId);
                     courseware.setSessionId(videoBean.bjySessionId);

                     if(TextUtils.isEmpty(videoBean.token)||TextUtils.isEmpty(videoBean.videoId))
                         continue;
                  }else {
                     if(videoBean.videoType ==3) {// 直播回放
                         courseware.setPlayerType(PlayerTypeEnum.BaiJia.getValue());
                         courseware.setDownloadID(UserInfoUtil.userId+String.valueOf(videoBean.bjyRoomId) + videoBean.bjySessionId);
                         courseware.setClarity(0);
                         courseware.setEncryptType(0);
                         courseware.setVideoToken(videoBean.token);
                         courseware.setRoomId(videoBean.bjyRoomId);
                         courseware.setSessionId(videoBean.bjySessionId);
                         if(TextUtils.isEmpty(videoBean.token)||TextUtils.isEmpty(videoBean.bjyRoomId))
                            continue;
                     }
                 }
                 coursewares.add(courseware);
             }
             course.setLessonLists(coursewares);
             course.setTotalNum(buyDetailInfo.coursewareHours);
             course.setCourseID(userCourseID);
             course.setCourseName(buyDetailInfo.title);
             course.setChangeStatus(0);
             //course.setCourseType(buyDetailInfo.TypeName);
             //course.setTeacher(buyDetailInfo.TeacherDesc);
             course.setImageURL(buyDetailInfo.scaleimg);
         }
         return course;

     }




    public static DownLoadCourse convertVideoInfoListToDownCourse(VideoBean courseInfo){
        VideoBean data =courseInfo;
        VideoBean.CourseDetail buyDetailInfo = null;
        List<VideoBean.LiveCourse> allList = new ArrayList<>();
        int liveCount = 0;
        if (data.live != null) {
            liveCount = data.live.size();
            for (VideoBean.LiveCourse course : data.live) {
                allList.add(course);
            }
        }
        if (data.lession != null) {
            for (VideoBean.LiveCourse course : data.lession) {
                allList.add(course);
            }
        }
        if(data.course != null) {
            buyDetailInfo = data.course;
        }
        return convertVideoInfoListToDownCourse(buyDetailInfo,liveCount,allList);
    }

    //课件转本地下载
    public static DownLoadCourse convertVideoInfoListToDownCourse(
            VideoBean.CourseDetail buyDetailInfo, int liveCount,
            List<VideoBean.LiveCourse> allList) {
        DownLoadCourse course = new DownLoadCourse();

        if (buyDetailInfo != null && allList.size() > 0) {

            String userCourseID=UserInfoUtil.userId+"_"+buyDetailInfo.NetClassId;//因为是主键，添加用户区分
            List<DownLoadLesson> coursewares = new ArrayList<>();
            for (int i = liveCount; i < allList.size(); i++) {
                VideoBean.LiveCourse videoBean = allList.get(i);

                DownLoadLesson courseware = new DownLoadLesson();
                courseware.setCourseID(userCourseID);
                courseware.setSubjectID(videoBean.rid);
                courseware.setSubjectName(videoBean.Title);
                courseware.setCourseNum(i);
                courseware.setDownStatus(0);


                //.toJson(videoBean, VideoBean.LiveCourseParams.class)
                courseware.setPlayParams(GsonUtil.toJson(videoBean, VideoBean.LiveCourseParams.class));

                //添加REVERSE_1作为 录播有没有老师字段 REVERSE_2为老师名字
           /*     values.put(DownLoadLesson.REVERSE_1, lesson.getReserve1());
                values.put(DownLoadLesson.REVERSE_2, lesson.getReserve2());*/

                courseware.setReserve1(String.valueOf(videoBean.hasTeacher));
                courseware.setReserve2(videoBean.teacher);

                if(videoBean.status == -2) {//录播
                    courseware.setPlayerType(PlayerTypeEnum.BjRecord.getValue());
                    courseware.setDownloadID(UserInfoUtil.userId+videoBean.bjyVideoId );
                    courseware.setClarity(0);
                    courseware.setEncryptType(0);
                    courseware.setVideoToken(videoBean.token);
                    courseware.setRoomId(videoBean.bjyVideoId);
                    courseware.setSessionId(videoBean.bjySessionId);
                 }else {
                    if(allList.get(i).playerType ==PlayerTypeEnum.BaiJia.getValue()) {// 1
                        courseware.setPlayerType(PlayerTypeEnum.BaiJia.getValue());
                        courseware.setDownloadID(UserInfoUtil.userId+String.valueOf(videoBean.bjyRoomId) + videoBean.bjySessionId);
                        courseware.setClarity(0);
                        courseware.setEncryptType(0);
                        courseware.setVideoToken(videoBean.token);
                        courseware.setRoomId(videoBean.bjyRoomId);
                        courseware.setSessionId(videoBean.bjySessionId);
                    }else {

                        courseware.setPlayerType(PlayerTypeEnum.Gensee.getValue());
                        courseware.setDomain(videoBean.url);
                        courseware.setLiveID(videoBean.JoinCode);
                        courseware.setPassword(videoBean.password);
                        courseware.setDownloadID(UserInfoUtil.userId+videoBean.JoinCode);
                    }
                }
                coursewares.add(courseware);
            }
            course.setLessonLists(coursewares);
            course.setTotalNum(coursewares.size());
            course.setCourseID(userCourseID);
            course.setCourseName(buyDetailInfo.title);
            course.setChangeStatus(0);
            course.setCourseType(buyDetailInfo.TypeName);
            course.setTeacher(buyDetailInfo.TeacherDesc);
            course.setImageURL(buyDetailInfo.scaleimg);
        }
        return course;
    }

    public static VideoBean.CourseDetail convertDownloadCourseToCourseDetail(DownLoadCourse course){
        if(course == null) {
            return null;
        }
        VideoBean.CourseDetail buyDetailInfo = new VideoBean.CourseDetail();
        buyDetailInfo.NetClassId = course.getRealCourseID() ;
        buyDetailInfo.SubjectName = course.getCourseName();
        buyDetailInfo.title = course.getCourseName();
        buyDetailInfo.scaleimg = course.getImagePath();
        return buyDetailInfo;
    }

    //  onlive下载数据库转回放课
    public static List<VideoBean.LiveCourse> convertDownloadCourseToLiveInfoList(
            List<DownLoadLesson> coursewareList) {
        List<VideoBean.LiveCourse> videoList = new ArrayList<>();
        if(Method.isListEmpty(coursewareList)) {
            return videoList;
        }
        for(int i = 0; i < coursewareList.size(); i++) {
            DownLoadLesson courseware = coursewareList.get(i);
            VideoBean.LiveCourse liveCourse = new VideoBean.LiveCourse();
            liveCourse.Title = courseware.getSubjectName();
            liveCourse.rid = courseware.getSubjectID();
            if(courseware.getPlayerType()==PlayerTypeEnum.BjRecord.getValue()
                    ||courseware.getPlayerType()==PlayerTypeEnum.CCPlay.getValue()){
                liveCourse.status = -2;
            }
            else {
                liveCourse.status = 2;
            }
            liveCourse.isOffFlag = true;
            liveCourse.offFilePath = courseware.getPlayPath();
            liveCourse.JoinCode = courseware.getDownloadID();
            liveCourse.playerType = courseware.getPlayerType();
            liveCourse.offSignalFilePath = courseware.getSignalFilePath();
            liveCourse.bjyRoomId = courseware.getRoomId();
            liveCourse.token = courseware.getVideoToken();
            liveCourse.bjySessionId = courseware.getSessionId();
            liveCourse.hasTeacher= StringUtils.parseInt(courseware.getReserve1());
            liveCourse.teacher=courseware.getReserve2();
           // LogUtils.e("convertDownload",i+","+courseware.getPlay_duration()+","+courseware.getDuration());
            liveCourse.process= (((float)courseware.getPlay_duration()) * 100 / courseware
                    .getDuration());
            ;
            videoList.add(liveCourse);
        }
        return videoList;
    }

    public static ArrayList<VodCoursePlayBean.LessionBean> convertVideoInfoToRecordInfo(
            List<VideoBean.LiveCourse> courseList) {
        ArrayList<VodCoursePlayBean.LessionBean> dataList = new ArrayList<>();
        if(Method.isListEmpty(courseList)) {
            return dataList;
        }
        for(int i = 0; i < courseList.size(); i++) {
            VideoBean.LiveCourse liveCourse = courseList.get(i);
            VodCoursePlayBean.LessionBean bean = new VodCoursePlayBean.LessionBean();
            bean.setJoinCode(liveCourse.JoinCode);
            bean.setTitle(liveCourse.Title);
            bean.setCc_key(liveCourse.cc_key);
            bean.setCc_uid(liveCourse.cc_uid);
            bean.setCc_vid(liveCourse.cc_vid);
            bean.setFileSize(liveCourse.fileSize);
            bean.setPassword(liveCourse.password);
            bean.setRid(liveCourse.rid);
            bean.setStartTime(liveCourse.startTime);
            bean.setStatus(liveCourse.status);
            bean.setTimeLength(liveCourse.timeLength);
            bean.setUrl(liveCourse.url);
            bean.setUsername(liveCourse.username);
            bean.setIsComment(liveCourse.isComment);
            bean.setBjyCode(liveCourse.bjyCode);
            bean.setBjyVideoId(liveCourse.bjyVideoId);
            bean.setBjyRoomId(liveCourse.bjyRoomId);
            bean.setBjySessionId(liveCourse.bjySessionId);
            bean.setPlayerType(String.valueOf(liveCourse.playerType));
            bean.setToken(liveCourse.token);
            bean.setTeacher(liveCourse.teacher);
            dataList.add(bean);
        }
        return dataList;
    }

    public static VodCoursePlayBean.CourseBean convertCourseDetailToRecordInfo(
            VideoBean.CourseDetail buyDetailInfo) {
        VodCoursePlayBean.CourseBean courseBean = new VodCoursePlayBean.CourseBean();
        if(buyDetailInfo != null) {
            courseBean.NetClassId = buyDetailInfo.NetClassId;
            courseBean.SubjectName = buyDetailInfo.SubjectName;
            courseBean.TeacherDesc = buyDetailInfo.TeacherDesc;
            courseBean.TimeLength = buyDetailInfo.TimeLength;
            courseBean.TypeName = buyDetailInfo.TypeName;
            courseBean.scaleimg = buyDetailInfo.scaleimg;
            courseBean.title = buyDetailInfo.title;
            courseBean.free = buyDetailInfo.free;
        }
        return courseBean;
    }
}
