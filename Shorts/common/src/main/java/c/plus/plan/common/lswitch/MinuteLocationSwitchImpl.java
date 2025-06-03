package c.plus.plan.common.lswitch;

/**
 * Created by fanwei on 1/28/22
 */
public class MinuteLocationSwitchImpl extends LocalSwitchImpl {
    public MinuteLocationSwitchImpl(String storageKey) {
        this.storageKey = storageKey;
        this.switchUnit = SWITCH_UNIT_MINUTE;
    }

    @Override
    boolean check(long lastShowAt, long time) {
        return (System.currentTimeMillis() - lastShowAt)/(60*60*1000) >= time;
    }
}
