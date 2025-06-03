package c.plus.plan.shorts.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import c.plus.plan.common.entity.Converters;

@Entity(tableName = "drama_prices", primaryKeys = {"countryName"})
public class DramaPrice {
    public final static String COUNTRY_OTHER = "OTHER";
    public final static String PRICE_OTHER = "OTHER";

    @SerializedName("country")
    @NonNull
    private String countryName;
    @TypeConverters({Converters.class})
    private HashMap<String, Integer> dramaLevelCoin;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public HashMap<String, Integer> getDramaLevelCoin() {
        return dramaLevelCoin;
    }

    public void setDramaLevelCoin(HashMap<String, Integer> dramaLevelCoin) {
        this.dramaLevelCoin = dramaLevelCoin;
    }

    public int getCoin(String level){
        try {
            if(dramaLevelCoin.containsKey(level)){
                return dramaLevelCoin.get(level);
            } else if(dramaLevelCoin.containsKey(PRICE_OTHER)){
                dramaLevelCoin.get(PRICE_OTHER);
            }
        } catch (Exception e){

        }
        return -1;
    }
}
