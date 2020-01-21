package com.huatu.handheld_huatu.business.essay.bhelper;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 存储试卷下载数据的的单例，存储到Sp中，从Sp中读取
 */
public class DlEssayDataCache {

    private static final String TAG = "DlEssayDataCache";

    public ArrayList<DownloadEssayBean> dl_ingList = new ArrayList<>();
    public ArrayList<DownloadEssayBean> dl_failList = new ArrayList<>();
    public ArrayList<DownloadEssayBean> dl_successList = new ArrayList<>();

    public ArrayList<DownloadEssayBean> multi_dl_failList = new ArrayList<>();
    public ArrayList<DownloadEssayBean> multi_dl_ingList = new ArrayList<>();
    public ArrayList<DownloadEssayBean> multi_dl_successList = new ArrayList<>();

    public ArrayList<DownloadEssayBean> argue_dl_failList = new ArrayList<>();
    public ArrayList<DownloadEssayBean> argue_dl_ingList = new ArrayList<>();
    public ArrayList<DownloadEssayBean> argue_dl_successList = new ArrayList<>();

    public HashMap<String, String> downloadFilePath = new HashMap<>();          // key:试卷信息 value:文件路径

    private List<DownloadEssayBean> noExitList = new ArrayList<>();

    private DownloadEssayBean mDownloadingEssayBean;
    public int downType = -1;

    public static volatile DlEssayDataCache instance = null;

    public static DlEssayDataCache getInstance() {
        synchronized (DlEssayDataCache.class) {
            if (instance == null) {
                instance = new DlEssayDataCache();
            }
        }
        return instance;
    }

    private DlEssayDataCache() {
        readFromFileCahce();
    }

    public void readFromFileCahce() {
        readSingleFromFile();
        readMultiFromFile();
        readArgueFromFile();
    }

    public void readArgueFromFile() {
        String strIng = SpUtils.getYiLunWenEssayDownloadIngList();
        if (!TextUtils.isEmpty(strIng)) {
            ArrayList<DownloadEssayBean> dl_ingL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strIng,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_ingL != null && dl_ingL.size() > 0) {
                argue_dl_ingList.clear();
                argue_dl_ingList.addAll(dl_ingL);
                updateCacheStatus(argue_dl_ingList);
            }
        }

        String strFail = SpUtils.getArgueEssaydownloadFailList();
        if (!TextUtils.isEmpty(strFail)) {
            ArrayList<DownloadEssayBean> dl_FailL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strFail,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_FailL != null && dl_FailL.size() > 0) {
                argue_dl_failList.clear();
                argue_dl_failList.addAll(dl_FailL);
            }
        }

        String strSuccess = SpUtils.getArgueEssaydownloadSuccessList();
        if (!TextUtils.isEmpty(strSuccess)) {
            ArrayList<DownloadEssayBean> dl_SuccessL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strSuccess,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_SuccessL != null && dl_SuccessL.size() > 0) {
                argue_dl_successList.clear();
                argue_dl_successList.addAll(dl_SuccessL);
                for (DownloadEssayBean var : argue_dl_successList) {
                    if (!FileUtil.isFileExist(var.filepath)) {
                        noExitList.add(var);
                    }
                }
                if (noExitList != null && noExitList.size() > 0) {
                    argue_dl_successList.removeAll(noExitList);
                    noExitList.clear();
                }
            }
        }
    }

    public void readMultiFromFile() {
        String strIng = SpUtils.getTaoTiEssayDownloadIngList();
        if (!TextUtils.isEmpty(strIng)) {
            ArrayList<DownloadEssayBean> dl_ingL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strIng,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_ingL != null && dl_ingL.size() > 0) {
                multi_dl_ingList.clear();
                multi_dl_ingList.addAll(dl_ingL);
                updateCacheStatus(multi_dl_ingList);
            }
        }

        String strFail = SpUtils.getMultiEssaydownloadFailList();
        if (!TextUtils.isEmpty(strFail)) {
            ArrayList<DownloadEssayBean> dl_FailL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strFail,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_FailL != null && dl_FailL.size() > 0) {
                multi_dl_failList.clear();
                multi_dl_failList.addAll(dl_FailL);
            }
        }

        String strSuccess = SpUtils.getMultiEssaydownloadSuccessList();
        if (!TextUtils.isEmpty(strSuccess)) {
            ArrayList<DownloadEssayBean> dl_SuccessL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strSuccess,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_SuccessL != null && dl_SuccessL.size() > 0) {
                multi_dl_successList.clear();
                multi_dl_successList.addAll(dl_SuccessL);
                for (DownloadEssayBean var : multi_dl_successList) {
                    if (!FileUtil.isFileExist(var.filepath)) {
                        noExitList.add(var);
                    }
                }
                if (noExitList != null && noExitList.size() > 0) {
                    multi_dl_successList.removeAll(noExitList);
                    noExitList.clear();
                }
            }
        }
    }

    public void readSingleFromFile() {
        String strIng = SpUtils.getBiaoZhunDaAnEssayDownloadIngList();
        if (!TextUtils.isEmpty(strIng)) {
            ArrayList<DownloadEssayBean> dl_ingL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strIng,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_ingL != null && dl_ingL.size() > 0) {
                dl_ingList.clear();
                dl_ingList.addAll(dl_ingL);
                updateCacheStatus(dl_ingList);
            }
        }

        String strFail = SpUtils.getSingleEssaydownloadFailList();
        if (!TextUtils.isEmpty(strFail)) {
            ArrayList<DownloadEssayBean> dl_FailL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strFail,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_FailL != null && dl_FailL.size() > 0) {
                dl_failList.clear();
                dl_failList.addAll(dl_FailL);
            }
        }

        String strSuccess = SpUtils.getSingleEssaydownloadSuccessList();
        if (!TextUtils.isEmpty(strSuccess)) {
            ArrayList<DownloadEssayBean> dl_SuccessL = (ArrayList<DownloadEssayBean>) GsonUtil.string2JsonObject(strSuccess,
                    new TypeToken<ArrayList<DownloadEssayBean>>() {
                    }.getType());
            if (dl_SuccessL != null && dl_SuccessL.size() > 0) {
                dl_successList.clear();
                dl_successList.addAll(dl_SuccessL);
                for (DownloadEssayBean var : dl_successList) {
                    if (!FileUtil.isFileExist(var.filepath)) {
                        noExitList.add(var);
                    }
                }
                if (noExitList != null && noExitList.size() > 0) {
                    dl_successList.removeAll(noExitList);
                    noExitList.clear();
                }
            }
        }
    }

    public void writeToFileCache() {
        writeSingleToFile();
        writeMultiToFile();
        writeArgueToFile();
    }

    public void writeSingleToFile() {
        LogUtils.d("DlEssayDataCache", "writeToFileCahce start");

        if (dl_ingList.size() > 0) {
            String var = GsonUtil.toJsonStr(dl_ingList);
            if (var != null && var.length() > 0) {
                SpUtils.setBiaoZhunDaAnEssayDownloadIngList(var);
            }
        } else {
            SpUtils.setBiaoZhunDaAnEssayDownloadIngList("");
        }

        if (dl_failList.size() > 0) {
            String var = GsonUtil.toJsonStr(dl_failList);
            if (var != null && var.length() > 0) {
                SpUtils.setSingleEssaydownloadFailList(var);
            }
        } else {
            SpUtils.setSingleEssaydownloadFailList("");
        }

        if (dl_successList.size() > 0) {
            String var = GsonUtil.toJsonStr(dl_successList);
            if (var != null && var.length() > 0) {
                SpUtils.setSingleEssayDownloadSuccessList(var);
            }
        } else {
            SpUtils.setSingleEssayDownloadSuccessList("");
        }
        LogUtils.d("DlEssayDataCache", "writeToFileCahce end");
    }

    public void writeArgueToFile() {
        LogUtils.d("DlEssayDataCache", "writeToFileCahce start");

        if (argue_dl_ingList.size() > 0) {
            String var = GsonUtil.toJsonStr(argue_dl_ingList);
            if (var != null && var.length() > 0) {
                SpUtils.setYiLunWenEssayDownloadIngList(var);
            }
        } else {
            SpUtils.setYiLunWenEssayDownloadIngList("");
        }

        if (argue_dl_failList.size() > 0) {
            String var = GsonUtil.toJsonStr(argue_dl_failList);
            if (var != null && var.length() > 0) {
                SpUtils.setArgueEssaydownloadFailList(var);
            }
        } else {
            SpUtils.setArgueEssaydownloadFailList("");
        }

        if (argue_dl_successList.size() > 0) {
            String var = GsonUtil.toJsonStr(argue_dl_successList);
            if (var != null && var.length() > 0) {
                SpUtils.setArgueEssayDownloadSuccessList(var);
            }
        } else {
            SpUtils.setArgueEssayDownloadSuccessList("");
        }
        LogUtils.d("DlEssayDataCache", "writeToFileCahce end");
    }

    public void writeMultiToFile() {
        LogUtils.d("DlEssayDataCache", "writeToFileCahce start");

        if (multi_dl_ingList.size() > 0) {
            String var = GsonUtil.toJsonStr(multi_dl_ingList);
            if (var != null && var.length() > 0) {
                SpUtils.setTaoTiEssayDownloadIngList(var);
            }
        } else {
            SpUtils.setTaoTiEssayDownloadIngList("");
        }

        if (multi_dl_failList.size() > 0) {
            String var = GsonUtil.toJsonStr(multi_dl_failList);
            if (var != null && var.length() > 0) {
                SpUtils.setMultiEssaydownloadFailList(var);
            }
        } else {
            SpUtils.setMultiEssaydownloadFailList("");
        }

        if (multi_dl_successList.size() > 0) {
            String var = GsonUtil.toJsonStr(multi_dl_successList);
            if (var != null && var.length() > 0) {
                SpUtils.setMultiEssayDownloadSuccessList(var);
            }
        } else {
            SpUtils.setMultiEssayDownloadSuccessList("");
        }
        LogUtils.d("DlEssayDataCache", "writeToFileCahce end");
    }

    public void writeDownloadFilePathToFile() {
        LogUtils.d("DlEssayDataCache", "writeToFileCahce start");

        if (downloadFilePath.size() > 0) {
            String var = GsonUtil.toJsonStr(downloadFilePath);
            if (var != null && var.length() > 0) {
                SpUtils.setDownloadFilePath(var);
            }
        } else {
            SpUtils.setDownloadFilePath("");
        }
    }

    public void readDownloadFilePathFromFile() {
        String strIng = SpUtils.getDownloadFilePath();
        if (!TextUtils.isEmpty(strIng)) {
            HashMap<String, String> dl_ingL = (HashMap<String, String>) GsonUtil.string2JsonObject(strIng,
                    new TypeToken<HashMap<String, String>>() {
                    }.getType());
            if (dl_ingL != null && dl_ingL.size() > 0) {
                downloadFilePath.clear();
                downloadFilePath.putAll(dl_ingL);
            }
        }
    }

    public void updateCacheStatus(List<DownloadEssayBean> dl_ingList) {
        if (dl_ingList.size() > 0) {
            DownloadEssayBean mvar = dl_ingList.get(0);
            mvar.setStatus(DownloadEssayBean.DOWNLOAD_WAIT);
        }
    }

    //ssss ****************************************************///
    public boolean isContains(DownloadEssayBean var) {
        ArrayList<DownloadEssayBean> dl_List = new ArrayList<>();
        if (var.downtype == 0) {
            if (dl_ingList.size() > 0) {
                dl_List.addAll(dl_ingList);
            }
            if (dl_failList.size() > 0) {
                dl_List.addAll(dl_failList);
            }
            if (dl_List.contains(var)) {
                return true;
            }
            if (dl_successList.size() > 0) {
                if (dl_successList.contains(var)) {
                    if (var.filepath != null && FileUtil.isFileExist(var.filepath)) {
                        LogUtils.d("DlEssayDataCache", "new File(var.filepath).exists()");
                        return true;
                    } else {
                        dl_successList.remove(var);
                    }
                }
            }
        } else if (var.downtype == 1) {
            if (multi_dl_ingList.size() > 0) {
                dl_List.addAll(multi_dl_ingList);
            }
            if (multi_dl_failList.size() > 0) {
                dl_List.addAll(multi_dl_failList);
            }
            if (dl_List.contains(var)) {
                return true;
            }
            if (multi_dl_successList.size() > 0) {
                if (multi_dl_successList.contains(var)) {
                    if (var.filepath != null && FileUtil.isFileExist(var.filepath)) {
                        LogUtils.d("DlEssayDataCache", "new File(var.filepath).exists()");
                        return true;
                    } else {
                        multi_dl_successList.remove(var);
                    }
                }
            }
        } else if (var.downtype == 2) {
            if (argue_dl_ingList.size() > 0) {
                dl_List.addAll(argue_dl_ingList);
            }
            if (argue_dl_failList.size() > 0) {
                dl_List.addAll(argue_dl_failList);
            }
            if (dl_List.contains(var)) {
                return true;
            }
            if (argue_dl_successList.size() > 0) {
                if (argue_dl_successList.contains(var)) {
                    if (var.filepath != null && FileUtil.isFileExist(var.filepath)) {
                        LogUtils.d("DlEssayDataCache", "new File(var.filepath).exists()");
                        return true;
                    } else {
                        argue_dl_successList.remove(var);
                    }
                }
            }
        }

        return false;
    }

    //ssss ****************************************************///
    private void addDownloadingBean(DownloadEssayBean var) {
        if (var.downtype == 0) {
            if (dl_ingList != null && !dl_ingList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_WAIT);
                dl_ingList.add(var);
            }
        } else if (var.downtype == 1) {
            if (multi_dl_ingList != null && !multi_dl_ingList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_WAIT);
                multi_dl_ingList.add(var);
            }
        } else {
            if (argue_dl_ingList != null && !argue_dl_ingList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_WAIT);
                argue_dl_ingList.add(var);
            }
        }
    }

    private void removeDownloadingBean(DownloadEssayBean var) {
        if (var.downtype == 0) {
            if (dl_ingList.size() > 0 && dl_ingList.contains(var)) {
                dl_ingList.remove(var);
            }
        } else if (var.downtype == 1) {
            if (multi_dl_ingList.size() > 0 && multi_dl_ingList.contains(var)) {
                multi_dl_ingList.remove(var);
            }
        } else {
            if (argue_dl_ingList.size() > 0 && argue_dl_ingList.contains(var)) {
                argue_dl_ingList.remove(var);
            }
        }
        LogUtils.d("DlEssayDataCache", "dl_ingList remove:" + dl_ingList.size());
    }

    private void addFailBean(DownloadEssayBean var) {
        if (var.downtype == 0) {
            if (dl_failList != null && !dl_failList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_FAIL);
                dl_failList.add(var);
            }
        } else if (var.downtype == 1) {
            if (multi_dl_failList != null && !multi_dl_failList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_FAIL);
                multi_dl_failList.add(var);
            }
        } else if (var.downtype == 2) {
            if (argue_dl_failList != null && !argue_dl_failList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_FAIL);
                argue_dl_failList.add(var);
            }
        }
        LogUtils.d("DlEssayDataCache", "dl_failList :" + dl_failList.size());
    }

    private void removeFailBean(DownloadEssayBean var) {
        if (var.downtype == 0) {
            if (dl_failList.size() > 0 && dl_failList.contains(var)) {
                dl_failList.remove(var);
            }
        } else if (var.downtype == 1) {
            if (multi_dl_failList.size() > 0 && multi_dl_failList.contains(var)) {
                multi_dl_failList.remove(var);
            }

        } else if (var.downtype == 2) {
            if (argue_dl_failList.size() > 0 && argue_dl_failList.contains(var)) {
                argue_dl_failList.remove(var);
            }
        }
        LogUtils.d("DlEssayDataCache", "dl_failList :remove" + dl_failList.size());
    }

    private void addSuccessBean(DownloadEssayBean var) {
        if (var.downtype == 0) {
            if (dl_successList != null && !dl_successList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_SUCCESS);
                dl_successList.add(0, var);
            }
        } else if (var.downtype == 1) {
            if (multi_dl_successList != null && !multi_dl_successList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_SUCCESS);
                multi_dl_successList.add(0, var);
            }
        } else if (var.downtype == 2) {
            if (argue_dl_successList != null && !argue_dl_failList.contains(var)) {
                var.setStatus(DownloadEssayBean.DOWNLOAD_SUCCESS);
                argue_dl_successList.add(0, var);
            }
        }

        LogUtils.d("DlEssayDataCache", "dl_successList :" + dl_successList.size());
    }

    public void removeSuccessBean(DownloadEssayBean var) {
        if (var.downtype == 0) {
            if (dl_successList.size() > 0 && dl_successList.contains(var)) {
                dl_successList.remove(var);
            }
        } else if (var.downtype == 1) {
            if (multi_dl_successList.size() > 0 && multi_dl_successList.contains(var)) {
                multi_dl_successList.remove(var);
            }
        } else if (var.downtype == 2) {
            if (argue_dl_successList.size() > 0 && argue_dl_successList.contains(var)) {
                argue_dl_successList.remove(var);
            }
        }
        LogUtils.d("DlEssayDataCache", "dl_successList :remove " + dl_successList.size());
    }

    //ssss ****************************************************///


    public void fromNetToWait(DownloadEssayBean var) {
        addDownloadingBean(var);
    }

    public void fromWaitToIng(DownloadEssayBean var) {
        var.setStatus(DownloadEssayBean.DOWNLOAD_ING);
    }

    public void fromIngToSuccess(DownloadEssayBean var) {
        removeDownloadingBean(var);
        addSuccessBean(var);
    }

    public void fromIngToFail(DownloadEssayBean var) {
        removeDownloadingBean(var);
        addFailBean(var);
    }

    public void fromFailToWait(DownloadEssayBean var) {
        removeFailBean(var);
        addDownloadingBean(var);
    }

    //ssss ****************************************************///
    private boolean startDowningEssay() {
        if (dl_ingList.size() > 0) {
            mDownloadingEssayBean = dl_ingList.get(0);
            return true;
        } else {
            LogUtils.d("DlEssayDataCache", "task is complete");
        }
        if (multi_dl_ingList.size() > 0) {
            mDownloadingEssayBean = multi_dl_ingList.get(0);
            return true;
        }
        if (argue_dl_ingList.size() > 0) {
            mDownloadingEssayBean = argue_dl_ingList.get(0);
            return true;
        }
        return false;
    }

    public boolean canDownload() {
        if (startDowningEssay() && mDownloadingEssayBean.getStatus() == DownloadEssayBean.DOWNLOAD_WAIT) {
            fromWaitToIng(mDownloadingEssayBean);
            LogUtils.d("DownLoadEssayHelper", "canDownload  true");
            return true;
        }
        LogUtils.d("DownLoadEssayHelper", "canDownload  false");
        return false;
    }

    public DownloadEssayBean getmDownloadingEssayBean() {
        return mDownloadingEssayBean;
    }

    public void isDowningStatus(int staus, int progress) {
        if (staus == DownloadEssayBean.DOWNLOAD_ING) {
            LogUtils.d("DownLoadEssayHelper", "progress:" + progress);
            mDownloadingEssayBean.downProgress = progress;
        } else if (staus == DownloadEssayBean.DOWNLOAD_SUCCESS) {
            fromIngToSuccess(mDownloadingEssayBean);
        } else if (staus == DownloadEssayBean.DOWNLOAD_FAIL) {
            fromIngToFail(mDownloadingEssayBean);
        }
    }

    public void clearData() {
        dl_ingList.clear();
        dl_failList.clear();
        dl_successList.clear();
        noExitList.clear();
    }

    public void reSetData() {
        readFromFileCahce();
    }


    //**************************************************************//

    /**
     * @param type                          0 存储下载完成的filePath 1 返回已下载的试卷filePath
     * @param isSingle                      是否是单题
     * @param isStartToCheckDetail          是否是批改报告
     * @param answerId                      答题卡Id
     * @param cacheSingleQuestionDetailBean 单题
     * @param cachePaperQuestionDetailBean  套题
     * @param filePath                      下载的pdf路径
     * @return
     */
    public String fileCachePath(int type, boolean isSingle, boolean isStartToCheckDetail, long answerId, SingleQuestionDetailBean cacheSingleQuestionDetailBean, PaperQuestionDetailBean cachePaperQuestionDetailBean, String filePath) {
        // 集中类型题的key值
        // 单题        isSingle + "" + isStartToCheckDetail + "" + cacheSingleQuestionDetailBean.questionBaseId
        // 单题报告     isSingle + "" + isStartToCheckDetail + "" + answerId
        // 套题        isSingle + "" + isStartToCheckDetail + "" + cachePaperQuestionDetailBean.essayPaper.paperId
        // 套题报告    isSingle + "" + isStartToCheckDetail + "" + answerId
        if (isSingle) { // 单题
            if (!isStartToCheckDetail) {    // 不是批改
                if (cacheSingleQuestionDetailBean != null) {
                    if (downloadFilePath != null) {
                        if (type == 0) {
                            downloadFilePath.put(isSingle + "" + isStartToCheckDetail + "" + cacheSingleQuestionDetailBean.questionBaseId, filePath);
                        } else {
                            return downloadFilePath.get(isSingle + "" + isStartToCheckDetail + "" + cacheSingleQuestionDetailBean.questionBaseId);
                        }
                    }
                } else {
                    LogUtils.e(TAG, "if(cacheSingleQuestionDetailBean!=null && cacheSingleAreaListBean!=null) ");
                }
            } else {    // 批改下载
                //单题批改下载//传questionAnswerId 第3个
                if (cacheSingleQuestionDetailBean != null) {
                    if (downloadFilePath != null) {
                        if (answerId <= 0) {
                            answerId = cacheSingleQuestionDetailBean.answerCardId;
                        }
                        if (type == 0) {
                            downloadFilePath.put(isSingle + "" + isStartToCheckDetail + "" + answerId, filePath);
                        } else {
                            return downloadFilePath.get(isSingle + "" + isStartToCheckDetail + "" + answerId);
                        }
                    }
                }
            }
        } else {
            if (!isStartToCheckDetail) {
                if (cachePaperQuestionDetailBean != null) {
                    if (cachePaperQuestionDetailBean.essayPaper != null) {
                        if (downloadFilePath != null) {
                            if (type == 0) {
                                downloadFilePath.put(isSingle + "" + isStartToCheckDetail + "" + cachePaperQuestionDetailBean.essayPaper.paperId, filePath);
                            } else {
                                return downloadFilePath.get(isSingle + "" + isStartToCheckDetail + "" + cachePaperQuestionDetailBean.essayPaper.paperId);
                            }
                        }
                    } else {
                        LogUtils.e(TAG, "cachePaperQuestionDetailBean !essayPaper= null ");
                    }
                } else {
                    LogUtils.e(TAG, "cachePaperQuestionDetailBean != null ");
                }
            } else {
                if (cachePaperQuestionDetailBean != null) {
                    if (cachePaperQuestionDetailBean.essayPaper != null) {
                        if (downloadFilePath != null) {
                            if (answerId <= 0) {
                                answerId = cachePaperQuestionDetailBean.essayPaper.answerCardId;
                            }
                            if (type == 0) {
                                downloadFilePath.put(isSingle + "" + isStartToCheckDetail + "" + answerId, filePath);
                            } else {
                                return downloadFilePath.get(isSingle + "" + isStartToCheckDetail + "" + answerId);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // 删除下载后删除保存的标记已下载的地址
    public void deletFilePath(boolean isSingle, boolean isStartToCheckDetail, long downLoadId) {
        downloadFilePath.remove(isSingle + "" + isStartToCheckDetail + "" + downLoadId);
        writeDownloadFilePathToFile();
    }
}
