package c.plus.plan.shorts.dao;

import static c.plus.plan.common.service.impl.UserServiceImpl.useCountry;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.blankj.utilcode.util.LanguageUtils;

import java.util.List;

import c.plus.plan.shorts.entity.DramaPrice;

@Dao
public abstract class DramaPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<DramaPrice> dramaPrices);

    @Query("SELECT * FROM drama_prices WHERE countryName = :country")
    public abstract DramaPrice select(String country);

    @Transaction
    public DramaPrice getCurrent(){
        DramaPrice dramaPrice = select(useCountry().toUpperCase());
        if(dramaPrice == null){
            dramaPrice = select(DramaPrice.COUNTRY_OTHER);
        }
        return dramaPrice;
    }
}
