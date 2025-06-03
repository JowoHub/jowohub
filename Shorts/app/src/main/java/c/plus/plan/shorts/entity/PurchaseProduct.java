package c.plus.plan.shorts.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

@Entity(tableName = "purchase_products", primaryKeys = {"productId"})
public class PurchaseProduct {
    @NonNull
    private String productId;
    private int coin;
    @TypeConverters({Converters.class})
    private Benefit benefit;
    private int sort;
    private String price;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(Benefit benefit) {
        this.benefit = benefit;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public static class Benefit{
        private int coin;
        private String label;

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}

