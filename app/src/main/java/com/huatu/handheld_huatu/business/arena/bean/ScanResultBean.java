package com.huatu.handheld_huatu.business.arena.bean;

/**
 * 扫二维码结果
 */
public class ScanResultBean {

    private long paperId;           // paperId，试卷Id，真题演练等试卷
    private long answerId;          // answerId，这是错题导出试卷扫描出来的答题卡Id

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }
}
