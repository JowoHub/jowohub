package c.plus.plan.shorts.entity;

public class UnlockDramaRecord {
    private long time;
    private int dramaEpisodeNum;
    private String dramaName;
    private int coin;
    private int dramaId;
    private String dramaCover;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDramaEpisodeNum() {
        return dramaEpisodeNum;
    }

    public void setDramaEpisodeNum(int dramaEpisodeNum) {
        this.dramaEpisodeNum = dramaEpisodeNum;
    }

    public String getDramaName() {
        return dramaName;
    }

    public void setDramaName(String dramaName) {
        this.dramaName = dramaName;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getDramaId() {
        return dramaId;
    }

    public void setDramaId(int dramaId) {
        this.dramaId = dramaId;
    }

    public String getDramaCover() {
        return dramaCover;
    }

    public void setDramaCover(String dramaCover) {
        this.dramaCover = dramaCover;
    }
}
