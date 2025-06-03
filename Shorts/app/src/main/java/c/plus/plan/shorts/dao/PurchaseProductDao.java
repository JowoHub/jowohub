package c.plus.plan.shorts.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import c.plus.plan.shorts.entity.PurchaseProduct;

@Dao
public abstract class PurchaseProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAll(List<PurchaseProduct> purchaseProducts);

    @Query("Delete FROM purchase_products")
    public abstract void delete();

    @Query("SELECT * FROM purchase_products ORDER BY sort")
    public abstract List<PurchaseProduct> selectAll();

    @Transaction
    public void update(List<PurchaseProduct> purchaseProducts){
        delete();
        int index = 0;
        for (PurchaseProduct bl : purchaseProducts) {
            bl.setSort(index);
            index ++;
        }
        insertAll(purchaseProducts);
    }
}
