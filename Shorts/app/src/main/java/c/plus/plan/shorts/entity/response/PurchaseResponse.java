package c.plus.plan.shorts.entity.response;

import android.text.TextUtils;

public class PurchaseResponse {
    public final static String STATUS_SUCCESS = "SUCCESSFUL";
    private String orderId;
    private String token;
    private String status;
    private String message;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess(){
        return TextUtils.equals(status, STATUS_SUCCESS);
    }
}
