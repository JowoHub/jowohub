package c.plus.plan.shorts.manager;

import android.os.Bundle;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.jowosdk.entity.Episodes;
import c.plus.plan.jowosdk.entity.UserUnlockEpisodes;
import c.plus.plan.jowosdk.sdk.Callback;
import c.plus.plan.jowosdk.sdk.JOWOSdk;
import c.plus.plan.jowosdk.sdk.Listener;
import c.plus.plan.jowosdk.sdk.Result;

public class DramaEventManager {
    private static Drama currentDrama;
    private static Episodes currentEpisodes;
    private static long position;

    public static void start(Drama drama, Episodes episodes, boolean isForu){
        if(currentDrama == null){
            currentDrama = drama;
            currentEpisodes = episodes;
        } else {
            if(!(currentDrama.getId() == drama.getId() && currentEpisodes.getEpisodesNum() == episodes.getEpisodesNum())){
                // 切换上报
                Bundle bundle = new Bundle();
                bundle.putInt("id", currentDrama.getId());
                bundle.putInt("num", currentEpisodes.getEpisodesNum());
                if(CollectionUtils.isNotEmpty(currentDrama.getBlocks())){
                    bundle.putString("type", currentDrama.getBlocks().get(0));
                }
                //TODO 第几次观看
//                bundle.putInt("series", currentEpisodes.getEpisodesNum());
                bundle.putLong("all_time", currentEpisodes.getDurationSecond()* 1000L);
                bundle.putLong("watch_time", position);
                bundle.putString("source", isForu ? "short" : "video");
                ShortsEventManager.onEvent("video_play", bundle);

                currentDrama = drama;
                currentEpisodes = episodes;
                position = 0;
            }
        }
    }

    public static void pause(Drama drama, Episodes episodes, long l){
        if(l > 0){
            currentDrama = drama;
            currentEpisodes = episodes;
            position = l;
        }
    }

    public static void finish(Drama drama, Episodes episodes, boolean isForu){
        // 切换上报
        Bundle bundle = new Bundle();
        bundle.putInt("id", currentDrama.getId());
        bundle.putInt("num", currentEpisodes.getEpisodesNum());
        if(CollectionUtils.isNotEmpty(currentDrama.getBlocks())){
            bundle.putString("type", currentDrama.getBlocks().get(0));
        }
        //TODO 第几次观看
//                bundle.putInt("series", currentEpisodes.getEpisodesNum());
        bundle.putLong("all_time", currentEpisodes.getDurationSecond()* 1000L);
        bundle.putLong("watch_time", position);
        bundle.putString("source", isForu ? "short" : "video");
        ShortsEventManager.onEvent("video_play", bundle);

        // 重置
        currentDrama = null;
        currentEpisodes = null;
        position = 0;
    }

    public static void onEvent(boolean isForu){
        if(currentDrama != null && currentDrama != null && position > 0){
            // 切换上报
            Bundle bundle = new Bundle();
            bundle.putInt("id", currentDrama.getId());
            bundle.putInt("num", currentEpisodes.getEpisodesNum());
            if(CollectionUtils.isNotEmpty(currentDrama.getBlocks())){
                bundle.putString("type", currentDrama.getBlocks().get(0));
            }
            //TODO 第几次观看
//                bundle.putInt("series", currentEpisodes.getEpisodesNum());
            bundle.putLong("all_time", currentEpisodes.getDurationSecond()* 1000L);
            bundle.putLong("watch_time", position);
            bundle.putString("source", isForu ? "short" : "video");
            ShortsEventManager.onEvent("video_play", bundle);

            currentDrama = null;
            currentEpisodes = null;
            position = 0;
        }
    }

    public static void error(int dramaId, int num, String msg) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", dramaId);
        bundle.putInt("num", num);
        bundle.putString("error", msg);
        ShortsEventManager.onEvent("play_error", bundle);
    }

    public static void unlock(String dramaId, int num) {
        JOWOSdk.fetchUserDrama(dramaId, new Callback<UserUnlockEpisodes>() {
            @Override
            public void result(Result<UserUnlockEpisodes> result) {
                if(result.getData() != null){
                    Bundle bundle = new Bundle();
                    bundle.putString("id", dramaId);
                    bundle.putInt("current_episode", num);
                    bundle.putInt("episodes_unlocked", result.getData().getUnlockEpisodesNum().size());
                    bundle.putInt("total_episodes", result.getData().getDrama().getEpisodesCount());
                    ShortsEventManager.onEvent("unlock", bundle);
                }
            }
        });
    }

    public static void controllerClick(int action) {
        Bundle bundle = new Bundle();
        if(action == Listener.OnPlayerListener.ACTION_BACK){
            bundle.putString("action", "return");
        } else if(action == Listener.OnPlayerListener.ACTION_PAUSE){
            bundle.putString("action", "pause");
        } else if(action == Listener.OnPlayerListener.ACTION_PLAY){
            bundle.putString("action", "play");
        } else if(action == Listener.OnPlayerListener.ACTION_COLLECT){
            bundle.putString("action", "click_favorite");
        } else if(action == Listener.OnPlayerListener.ACTION_COLLECT_CANCEL){
            bundle.putString("action", "unfavorite");
        } else if(action == Listener.OnPlayerListener.ACTION_EPISODES){
            bundle.putString("action", "drama_series");
        }
        ShortsEventManager.onEvent("overlay_click_behavior", bundle);
    }
}
