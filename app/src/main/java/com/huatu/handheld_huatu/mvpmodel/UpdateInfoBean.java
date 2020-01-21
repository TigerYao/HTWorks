package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by ljzyuhenda on 16/8/4.
 */
public class UpdateInfoBean {
    public AppVersion appVersionBean;
    public int commentStatus;       // 评论开关
    public String aboutPhone;
    public String aboutEmail;
    public String coursePhone;
    public String seckillUrl;
    public int photoAnswer;         // 拍照作答开关，0开启，1关闭
    public int photoAnswerType;     // 拍照识别第三方，0汉王，1腾讯优图
    public int voiceAnswer;         // 语音作答开关，0开启，1关闭
    public String photoAnswerMsg;   // 拍照弹框提示内容
    public int essayCorrectFree;    // 1 免费

    public int fur;                 // 是否是白名单0否1是

    public int courseVersion;      // 课程分类的版本号，判断更新

    public class AppVersion {
        public boolean update;
        public int level;           // 1 普通更新; 2 强制更新
        public String latestVersion;// 最新版本
        public String message;      // 提示语
        public String full;         // 全量地址
        public String bulk;         // 增量地址
        public String bulkMd5;      // 增量更新文件的md5

        public boolean isPatch;
        public String  patchUrl;
        public String  patchMd5;
    }
}
