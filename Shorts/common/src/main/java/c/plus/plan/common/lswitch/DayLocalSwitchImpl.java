package c.plus.plan.common.lswitch;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import c.plus.plan.common.constants.Constants;

import java.util.Calendar;

/**
 * Created by fanwei on 1/28/22
 */
public class DayLocalSwitchImpl extends LocalSwitchImpl {

    public DayLocalSwitchImpl(String storageKey) {
        this.storageKey = storageKey;
        this.switchUnit = SWITCH_UNIT_DAY;
    }

    /**
     * @@description 不是间隔24小时，比如2022-01-28 23:59:59到2022-01-29 00:00:01即为已超过一天
     * @param time 间隔天数
     **/
    @Override
    boolean check(long lastShowAt, long time) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        long todayEndMills = TimeUtils.date2Millis(todayEnd.getTime());
        LogUtils.dTag(Constants.COMMON_TGA, todayEndMills, System.currentTimeMillis(), todayEndMills - lastShowAt);
        return (todayEndMills - lastShowAt)/(24*60*60*1000) >= time;
    }
}
