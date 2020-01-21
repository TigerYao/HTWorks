package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by saiyuan on 2019/2/19.
 */

public class CreamArticleListData {
    public int click;//文章阅读数
    public int goodPost;//点赞数
    public long id;//文章ID
    public String addTime;//发布时间
    public boolean isTop;//是否置顶
    public String img;//列表缩略图
    public String title;//文章标题
    public String typeName;//所属分类名称

//    addTime	时间描述	string	@mock=$order('2017年3月14日','2017年3月14日','2017年3月13日','2017年3月13日','2017年3月13日','2017年3月10日','2017年3月10日','2017年3月9日','2017年3月9日','2017年3月8日')
//    click	点击量	number	@mock=$order(580,866,1463,910,1162,1533,1960,1162,1050,1120)
//    goodPost	点赞数	number	@mock=$order(0,0,0,0,0,0,0,0,0,0)
//    id	ID	number	@mock=$order(189615,189616,189584,189526,189527,189400,189404,189328,189331,189260)
//    img	列表缩略图, 为空无	string
//    isTop	是否置顶	boolean	@mock=$order(true,true,true,true,true,true,true,true,true,true)
//    title	标题	string
//    typeName	分类名称

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
