package c.plus.plan.shorts.manager;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.blankj.utilcode.util.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import c.plus.plan.shorts.dao.DramaNumUnlockDao;
import c.plus.plan.shorts.dao.DramaPriceDao;
import c.plus.plan.shorts.dao.PurchaseProductDao;
import c.plus.plan.shorts.entity.DramaNumUnlock;
import c.plus.plan.shorts.entity.DramaPrice;
import c.plus.plan.shorts.entity.PurchaseProduct;

@Database(entities = {
        DramaNumUnlock.class, DramaPrice.class, PurchaseProduct.class
}, version = 8)
public abstract class ShortsDataBase extends RoomDatabase {
    public static final ExecutorService dbWriteExecutor =
            Executors.newFixedThreadPool(2);
    private static volatile ShortsDataBase INSTANCE;

    public abstract DramaNumUnlockDao dramaNumUnlockDao();
    public abstract DramaPriceDao dramaPriceDao();
    public abstract PurchaseProductDao purchaseProductDao();

    public static ShortsDataBase get() {
        //  数据库加密
        // https://github.com/sqlcipher/android-database-sqlcipher#using-sqlcipher-for-android-with-room
        if (INSTANCE == null) {
            synchronized (ShortsDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(Utils.getApp(),
                                    ShortsDataBase.class, "shorts.db")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
//                                    LogUtils.d(TAG, "super.onCreate(db)");
                                }
                            })
//                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
