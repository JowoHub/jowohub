package c.plus.plan.common.dao;

import c.plus.plan.common.entity.User;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Created by fanwei on 2/14/22
 */
@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE id = :uid")
    User selectByUid(long uid);

    @Query("SELECT * FROM users WHERE id = :uid")
    LiveData<User> selectByUidLV(long uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
