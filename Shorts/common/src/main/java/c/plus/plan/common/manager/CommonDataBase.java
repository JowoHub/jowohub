package c.plus.plan.common.manager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import c.plus.plan.common.NativeManager;
import c.plus.plan.common.base.BaseDataBase;
import c.plus.plan.common.dao.UserDao;
import c.plus.plan.common.entity.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Created by fanwei on 2/14/22
 */
@Database(entities = {User.class}, version = 3)
public abstract class CommonDataBase extends BaseDataBase {
    private static final String TAG = "CommonDataBase";
    private static volatile CommonDataBase INSTANCE;

    public abstract UserDao userDao();

    public static CommonDataBase getDatabase() {
        //  数据库加密
        // https://github.com/sqlcipher/android-database-sqlcipher#using-sqlcipher-for-android-with-room
        if (INSTANCE == null) {
            synchronized (CommonDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(Utils.getApp().getApplicationContext(),
                            CommonDataBase.class, "common_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    LogUtils.dTag(TAG, "super.onCreate(db)");
                                }
                            })
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //此处对于数据库中的所有更新都需要写下面的代码
            database.execSQL("ALTER TABLE users "
                    + " ADD COLUMN vipInfo VARCHAR(255)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //此处对于数据库中的所有更新都需要写下面的代码
            database.execSQL("ALTER TABLE users "
                    + " ADD COLUMN countView INT NOT NULL DEFAULT 0");
        }
    };

}
