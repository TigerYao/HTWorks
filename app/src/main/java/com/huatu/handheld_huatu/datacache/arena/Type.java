package com.huatu.handheld_huatu.datacache.arena;

/**
 * Created by michael on 17/8/30.
 */

public class Type {

    // sign up for an examination  报名参加考试
    public interface SignUpType {
        int CIVIL_SERVANT = 1;                  // 公务员
        int PUBLIC_INSTITUTION = 3;             // 事业单位
        int TEACHER_T = 200100045;              // 教师
        int TEACHER_PRIMARY = 200100048;        // 教室资格证（小学）
        int TEACHER_MIDDLE = 200100053;         // 教室资格证（中学）
        int PUBLIC_SECURITY = 200100047;        // 公安招警
        int SELECTION_T = 41;                   // 遴选
        int MILITARY_REVOLUTION = 42;           // 军转
        int STATE_GRID = 43;                    // 国家电网
        int MEDICAL_TREATMENT_T = 200100000;    // 医疗
        int FINANCIAL_T = 200100002;            // 金融
        int GRADUATE = 129;                     // 研究生
        int ACCOUNTANT = 200100058;             // 财会
        int BUILDING = 20010005;                // 建筑
        int MILITARY_CIVILIAN = 200100060;      // 军队文职
        int OTHER_T = 200100046;                // 其他

    }

    // CIVIL_SERVANT 公务员 行测 申论
    public interface CS_ExamType {
        int ADMINISTRATIVE_APTITUDE_TEST = 1;       // 行测
        int ESSAY_TESTS_FOR_CIVIL_SERVANTS = 14;    // 申论
    }

    // PUBLIC_INSTITUTION 事业单位 职测 公基 综合应用
    public interface PB_ExamType {
        int PUBLIC_BASE = 2;                        // 公基
        int JOB_TEST = 3;                           // 职测
        int NTEGRATED_APPLICATION = 24;             // 综合应用
    }

    // 公安招警
    public interface PBS_ExamType {
        int PUBLIC_SECURITY_POLICE = 100100175;     // 公安招警 公检法
    }

    // 教师招聘（小学）
    public interface TEACHER_Primary_ExamType {
        int TEACHER_COLLIGATE = 200100049;          // 综合素质
        int TEACHER_EDUCATION = 200100051;          // 教知
    }

    // 教师招聘（中学）
    public interface TEACHER_Middle_ExamType {
        int TEACHER_COLLIGATE = 200100050;          // 综合素质
        int TEACHER_EDUCATION = 200100052;          // 教知
    }

    public static String getCategory(int catgory) {
        switch (catgory) {
            case SignUpType.CIVIL_SERVANT:
                return "公务员";
            case SignUpType.PUBLIC_INSTITUTION:
                return "事业单位";
            case SignUpType.TEACHER_T:
                return "教师招聘";
            case SignUpType.PUBLIC_SECURITY:
                return "招警考试";
            case SignUpType.SELECTION_T:
                return "公遴选";
            case SignUpType.MILITARY_REVOLUTION:
                return "军转";
            case SignUpType.STATE_GRID:
                return "国家电网";
            case SignUpType.MEDICAL_TREATMENT_T:
                return "医疗";
            case SignUpType.FINANCIAL_T:
                return "金融";
            case SignUpType.GRADUATE:
                return "考研";
            default:
                return "其他";
        }
    }

    public static String getSubject(int subject) {
        switch (subject) {
            case CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST:
                return "行测";
            case CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS:
                return "申论";
            case PB_ExamType.PUBLIC_BASE:
                return "公基";
            case PB_ExamType.JOB_TEST:
                return "职测";
            case PB_ExamType.NTEGRATED_APPLICATION:
                return "综合应用";
            case PBS_ExamType.PUBLIC_SECURITY_POLICE:
                return "公安专业科目";
            case TEACHER_Primary_ExamType.TEACHER_COLLIGATE:
            case TEACHER_Middle_ExamType.TEACHER_COLLIGATE:
                return "综合素质";
            case TEACHER_Primary_ExamType.TEACHER_EDUCATION:
            case TEACHER_Middle_ExamType.TEACHER_EDUCATION:
                return "教知";
            default:
                return "其他";
        }
    }
}
