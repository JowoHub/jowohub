package c.plus.plan.common.entity.request;

import com.google.gson.annotations.SerializedName;

public class RequestPushToken {
    @SerializedName("pushId")
    private String pushId;

    @SerializedName("enable")
    private boolean enable;

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
