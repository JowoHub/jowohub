package c.plus.plan.shorts.entity.request;

public class RequestUnlockDrama {
    private int dramaId;
    private int dramaEpisodeNum;
    private String dramaLevel;
    private String dramaName;
    private String dramaCover;

    public int getDramaId() {
        return dramaId;
    }

    public void setDramaId(int dramaId) {
        this.dramaId = dramaId;
    }

    public int getDramaEpisodeNum() {
        return dramaEpisodeNum;
    }

    public void setDramaEpisodeNum(int dramaEpisodeNum) {
        this.dramaEpisodeNum = dramaEpisodeNum;
    }

    public String getDramaLevel() {
        return dramaLevel;
    }

    public void setDramaLevel(String dramaLevel) {
        this.dramaLevel = dramaLevel;
    }

    public String getDramaName() {
        return dramaName;
    }

    public void setDramaName(String dramaName) {
        this.dramaName = dramaName;
    }

    public String getDramaCover() {
        return dramaCover;
    }

    public void setDramaCover(String dramaCover) {
        this.dramaCover = dramaCover;
    }
}
