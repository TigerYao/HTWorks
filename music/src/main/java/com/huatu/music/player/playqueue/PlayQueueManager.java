package com.huatu.music.player.playqueue;

import com.huatu.music.bean.Music;
import com.huatu.music.common.Constants;
import com.huatu.music.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019\8\20 0020.
 */

public  class PlayQueueManager {

    /**
     * 播放模式 0：顺序播放，1：单曲循环，2：随机播放
     */
    public static final int PLAY_MODE_LOOP = 0;
    public static final int PLAY_MODE_REPEAT = 1;
    public static final int PLAY_MODE_RANDOM = 2;

    private   int playingModeId=PLAY_MODE_LOOP;

    private static final java.lang.Integer[] playingMode = null;

    /**
     * 总共多少首歌曲
     */
    private static int total;
    private static java.util.List<java.lang.Integer> orderList;
    private static java.util.List<java.lang.Integer> saveList;
    private static int randomPosition;


    private List<Music> mPlayQueue = new ArrayList<>();
    private List<Integer> mHistoryPos = new ArrayList<>();
    private int mPlayingPos = -1;
    private int mNextPlayPos = -1;

    public static final PlayQueueManager INSTANCE = new PlayQueueManager();

    public int getSize(){
        return mPlayQueue.size();
    }

    /**
     * 更新播放模式
     */
    public final void updatePlayMode(int playMode) {
        playingModeId=playMode;
    }

    /**
     * 获取播放模式id
     */
    public final int getPlayModeId() {
        return playingModeId;
    }

    /**
     * 获取播放模式
     */

    public final java.lang.String getPlayMode() {
        return null;
    }

    private final void initOrderList(int total) { }



    public Music getCurrentItem(){
        if(isOutOfPlayList()) return null;
        return mPlayQueue.get(mPlayingPos);
    }

    public boolean isOutOfPlayList(){
      return   mPlayingPos >= mPlayQueue.size() || mPlayingPos < 0;
    }
    /**
     * 获取下一首位置
     *
     * @return isAuto 是否自动下一曲
     */
    private final int getNextPosition(boolean isAuto, int total, int cuePosition) {
        //return 0;
        if (total == 1) {
            return -1;
        }
        initOrderList(total);
        if (playingModeId == PlayQueueManager.PLAY_MODE_REPEAT && isAuto) {
            return  cuePosition<0? 0:cuePosition;

        } else if (playingModeId == PlayQueueManager.PLAY_MODE_RANDOM) {
           // printOrderList(orderList[randomPosition])
           // saveList.add(orderList[randomPosition])
            return 0;//orderList[randomPosition]
        } else {
            if (cuePosition == total - 1) {
                return -1;
            } else if (cuePosition < total - 1) {
                return cuePosition + 1;
            }
        }
        return cuePosition;
    }

    public boolean isLocalPath(int playIndex){
        if (playIndex >= mPlayQueue.size() || playIndex < 0) {
            return false;
        }
        return mPlayQueue.get(playIndex).type.equals(Constants.LOCAL);
    }

    public int  skipQueuePosition(boolean isAuto,int amount){
        int curPos =-1;
        if(amount==-1){
           curPos = getPreviousPosition(mPlayQueue.size(), mPlayingPos);
        }else {
           curPos = getNextPosition(isAuto,mPlayQueue.size(), mPlayingPos);
        }
        if(curPos==-1) return curPos;
        else {
            mPlayingPos=curPos;
            return mPlayingPos;
        }
    }

    /**
     * 获取下一首位置
     *
     * @return isAuto 是否自动下一曲
     */
    private final int getPreviousPosition(int total, int cuePosition) {
        //return 0;
        if (total == 1) {
            return -1;
        }
      //  getPlayModeId()
        if (playingModeId == PlayQueueManager.PLAY_MODE_REPEAT) {
            return  cuePosition<0 ?0:cuePosition;

        } else if (playingModeId == PlayQueueManager.PLAY_MODE_RANDOM) {
              return 0;
        } else {
            if (cuePosition == 0) {
                return  - 1;
            } else if (cuePosition > 0) {
                return cuePosition - 1;
            }
        }
        return cuePosition;
    }

    /**
     * 打印当前顺序
     */
    private final void printOrderList(int cur) {
    }

    private PlayQueueManager() {
        super();
    }


    /**
     * 保存播放队列
     *
     * @param full 是否存储
     */
    public void savePlayQueue(boolean full) {
       /* if (full) {
            PlayQueueLoader.INSTANCE.updateQueue(mPlayQueue);
        }
        if (mPlayingMusic != null) {
            //保存歌曲id
            SPUtils.saveCurrentSongId(mPlayingMusic.getMid());
        }
        //保存歌曲id
        SPUtils.setPlayPosition(mPlayingPos);
        //保存歌曲进度
        SPUtils.savePosition(getCurrentPosition());
        */
        LogUtil.e("MusicPlayerService", "save 保存歌曲id=" + mPlayingPos + " 歌曲进度= " );//+ getCurrentPosition()
    }


    public void saveHistory(long seekPosition) {
        //  PlayHistoryLoader.INSTANCE.addSongToHistory(mPlayingMusic);
        savePlayQueue(false);
        mHistoryPos.add(mPlayingPos);
    }

    public void removeFromQueue(int position){
      /*  if (position == mPlayingPos) {
            mPlayQueue.remove(position);
            if (mPlayQueue.size() == 0) {
                clearQueue();
            } else {
                playMusic(position);
            }
        } else if (position > mPlayingPos) {
            mPlayQueue.remove(position);
        } else if (position < mPlayingPos) {
            mPlayQueue.remove(position);
            mPlayingPos = mPlayingPos - 1;
        }*/

    }

    public void clear(){

        playingModeId=PlayQueueManager.PLAY_MODE_LOOP;
        mPlayingPos = -1;
        mPlayQueue.clear();
        mHistoryPos.clear();
    }

    Music  mPlayingMusic;
    public boolean resetplayMusic(int position,int startPos) {
        int nextPos=-1;
        if (position >= mPlayQueue.size() || position == -1) {
            nextPos = getNextPosition(true, mPlayQueue.size(), position);
        } else {
            nextPos = position;
        }
        if (nextPos == -1)
            return false;
        if (startPos > 0) {
            if (nextPos >= mPlayQueue.size() || nextPos < 0) {
                return false;
            }

           Music mPlayingMusic = mPlayQueue.get(nextPos);
           mPlayingMusic.trackNumber = startPos;
        }
        mPlayingPos=nextPos;
        return true;
    }

    public void addAndplayMusic(Music music) {
        if (mPlayingPos == -1 || mPlayQueue.size() == 0) {
            if(mPlayQueue!=null){
                mPlayQueue.clear();
            }
            mPlayQueue.add(music);
            mPlayingPos = 0;
        } else if (mPlayingPos < mPlayQueue.size()) {
            mPlayQueue.add(mPlayingPos, music);
        } else {
            mPlayQueue.add(mPlayQueue.size(), music);
        }
    }

    public int getCurPlayPositon(){
        return mPlayingPos;
    }

    /**
     * 设置播放队列
     *
     * @param playQueue 播放队列
     */
    private void setPlayQueue(List<Music> playQueue) {
        mPlayQueue.clear();
        mHistoryPos.clear();
        mPlayQueue.addAll(playQueue);

        savePlayQueue(true);
    }

    private String mPlaylistId = Constants.PLAYLIST_QUEUE_ID;

    public String getPlayListId(){
        return mPlaylistId;
    }

    public boolean isSamePlayingPos(int id, String pid){
        return  (mPlaylistId.equals(pid) && id == getCurPlayPositon()) ;
     }

    public void resetOrAddPlayQueue(List<Music> musicList, int index, String pid){
      if(index>=0){
            if (!mPlaylistId.equals(pid) || isUnSameQueue(musicList)) {
                setPlayQueue(musicList);
                mPlaylistId = pid;
            }
            mPlayingPos = index;

        }else if(index==-1){
            mPlaylistId = pid;
            mPlayQueue.addAll(0,musicList);
        }else if(index==-2){
            mPlaylistId = pid;
            mPlayQueue.addAll(musicList);
        }
    }
    //
    public boolean isUnSameQueue(List<Music> musicList){

      return   mPlayQueue.size() == 0 || mPlayQueue.size() != musicList.size();
    }

     public List<Music> getPlayQueue(){
         if (mPlayQueue.size() > 0) {
             return mPlayQueue;
         }
         return mPlayQueue;
     }
}