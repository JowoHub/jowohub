package c.plus.plan.common.lswitch;

import c.plus.plan.common.utils.KVUtils;

/**
 * Created by fanwei on 1/28/22
 */
public abstract class LocalSwitchImpl implements LocalSwitch {
    public static final int SWITCH_UNIT_MINUTE = 0;
    public static final int SWITCH_UNIT_HOUR = 1;
    public static final int SWITCH_UNIT_DAY = 2;

    protected String storageKey;
    protected int switchUnit;

    /**
     * @@description 判断指定时间内是否能显示
     * @param time 已经间隔时间
     **/
    @Override
    public boolean canShow(long time) {
        long lastShowAt = KVUtils.decodeLong(storageKey,0);
        return check(lastShowAt, time);
    }

    abstract boolean check(long lastShowAt, long time);

    /*
     * 将显示时间记录到本地
     */
    @Override
    public void show() {
        KVUtils.encode(storageKey, System.currentTimeMillis());
    }
}
