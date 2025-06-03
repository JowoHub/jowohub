package c.plus.plan.shorts.entity;

public class PurchaseRecord {
    private long time;
    private int coin;
    private String moneyLabel;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getMoneyLabel() {
        return moneyLabel;
    }

    public void setMoneyLabel(String moneyLabel) {
        this.moneyLabel = moneyLabel;
    }
}
