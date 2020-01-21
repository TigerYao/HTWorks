package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.ArrayList;


public class CreateAnswerCardIdBean implements Serializable {


    /**
     * answerCardId : 160
     */

    public long answerCardId;
    public boolean flag;
    public ArrayList<QuestionAnswerCard> questionAnswerCardList;

    public static class QuestionAnswerCard implements Serializable {
        public long questionBaseId;
        public long id;
    }


}
