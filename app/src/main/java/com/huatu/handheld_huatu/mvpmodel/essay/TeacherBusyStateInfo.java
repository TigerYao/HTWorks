package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;

public class TeacherBusyStateInfo extends CheckCountInfo implements Serializable {

    public boolean canCorrect;          // 是否可以批改。true、可以批改，老师工作不饱和 false、不能批改，老师工作饱和（在交卷之前请求的接口使用）
    public String correctDesc;          // 如果老师工作饱和，显示文案

    public CountInfo correct;           // 批改次数
}
