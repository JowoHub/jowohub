package c.plus.plan.shorts.entity;

import androidx.room.Entity;

import c.plus.plan.shorts.remoteconfig.AdConfig;

@Entity(tableName = "drama_num_unlocks", primaryKeys = {"dramaId", "episodesNum"})
public class DramaNumUnlock {
    public final static int UNLOCK_AD_COUNT = 2;
    private int dramaId;
    private int episodesNum;
    private int adCount;

    public int getDramaId() {
        return dramaId;
    }

    public void setDramaId(int dramaId) {
        this.dramaId = dramaId;
    }

    public int getEpisodesNum() {
        return episodesNum;
    }

    public void setEpisodesNum(int episodesNum) {
        this.episodesNum = episodesNum;
    }

    public int getAdCount() {
        return adCount;
    }

    public void setAdCount(int adCount) {
        this.adCount = adCount;
    }

    public static int getUnlockAdCount(){
        return AdConfig.get().getUnlockEpisodes().getAdCount();
    }
}
