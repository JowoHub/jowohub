package c.plus.plan.common.localCounter;

import c.plus.plan.common.utils.KVUtils;

public class LocalCountImpl implements LocalCounter {
    protected String key;

    public LocalCountImpl(String key) {
        this.key = key;
    }

    @Override
    public int getTimes() {
        return KVUtils.decodeInt(key,0);
    }

    @Override
    public void plus() {
        int times = KVUtils.decodeInt(key,0);
        KVUtils.encode(key, ++times);
    }
}
