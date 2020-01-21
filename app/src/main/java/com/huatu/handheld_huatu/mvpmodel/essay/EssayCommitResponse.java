package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019\8\13 0013.
 */

public class EssayCommitResponse implements Serializable {
    public List<CheckDetailBean> list;
    public String msg;
}
