package c.plus.plan.common.entity.response;

import com.google.gson.annotations.SerializedName;

import c.plus.plan.common.entity.User;

/**
 * Created by fanwei on 2/14/22
 */
public class UserTokenResponse {
    @SerializedName("accountInfo")
    private User user;

    private String token;

    private long expireAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}
