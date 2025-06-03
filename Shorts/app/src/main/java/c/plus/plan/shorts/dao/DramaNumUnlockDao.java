package c.plus.plan.shorts.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import c.plus.plan.shorts.entity.DramaNumUnlock;

@Dao
public abstract class DramaNumUnlockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DramaNumUnlock dramaNumUnlock);

    @Query("SELECT * FROM drama_num_unlocks WHERE dramaId = :dramaId AND episodesNum = :num")
    public abstract DramaNumUnlock select(int dramaId, int num);
}
