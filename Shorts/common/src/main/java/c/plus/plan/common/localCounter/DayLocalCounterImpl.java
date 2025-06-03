package c.plus.plan.common.localCounter;

import android.text.format.DateUtils;

import java.util.Calendar;

import c.plus.plan.common.utils.KVUtils;

public class DayLocalCounterImpl implements LocalCounter {
    protected String key;

    public DayLocalCounterImpl(String key) {
        this.key = key;
    }

    @Override
    public int getTimes() {
        String values =  KVUtils.decodeString(key, null);
        if(values == null) return 0;

        String[] arr = values.split("-");
        if(arr.length != 2) return 0;

        long time = Long.parseLong(arr[0]);
        int times = Integer.parseInt(arr[1]);
        if(!DateUtils.isToday(time)){
            times = 0;
        }
        return times;
    }

    @Override
    public void plus() {
        String values =  KVUtils.decodeString(key, null);
        if(values == null){
            KVUtils.encode(key, System.currentTimeMillis() + "-" + 1);
        } else {
            String[] arr = values.split("-");
            long time = Long.parseLong(arr[0]);
            if(DateUtils.isToday(time)){
                int times = Integer.parseInt(arr[1]);
                KVUtils.encode(key, System.currentTimeMillis() + "-" + (++times));
            } else {
                KVUtils.encode(key, System.currentTimeMillis() + "-" + 1);
            }
        }
    }
}
