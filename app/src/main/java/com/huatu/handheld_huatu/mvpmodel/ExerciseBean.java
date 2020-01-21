package com.huatu.handheld_huatu.mvpmodel;

import java.util.List;

/**
 * Created by ljzyuhenda on 16/7/23.
 */

/**
 * currentPage	int	搜索当前页数
 * total	long	搜索到的总记录数
 * id	int	试题id
 * fragment	string	高亮字段
 * from	string	试题来源
 * type	int	试题类型
 */
public class ExerciseBean {
    public String message;
    public String code;
    public ExerciseData data;

    public class ExerciseData {
        public String totalElements;
        public String totalPages;
        public List<ExerciseInfoBean> content;
    }

    public class ExerciseInfoBean {
        public int id;
        public String fragment;
        public String from;
        public String type;
        public String teachType;
    }
    /**
     {
     "data":{
     "content":[
     {
     "id":38018,
     "fragment":"某市西区治安联防队行使该区公安分局委托的治安管理权。某日联防队员李某抓获有盗窃嫌疑的王某，因王某拒不说出自己真实姓名，李某用<b>木棍</b>将其殴打致伤。王某向法院请求国家赔偿，应由（ ）承担国家赔偿义务。",
     "from":"2007年福建省公务员考试《行测》真题（春季）",
     "type":99
     },
     {
     "id":21917795,
     "fragment":null,
     "from":"2017年河北省事业单位职测真题",
     "type":99
     }
     ],
     "totalPages":1,
     "totalElements":13
     },
     "code":1000000
     }
     */
}
