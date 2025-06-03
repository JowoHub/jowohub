package c.plus.plan.common.event;

public class PayEvent {
    public final static int PAY_TYPE_WX = 0; // 微信支付
    public final static int STATUS_SUCCESS = 0; // 支付成功
    public final static int STATUS_FAIL = 1; // 支付失败

    public final int payType;
    public final int status;

    public PayEvent(int payType, int status) {
        this.payType = payType;
        this.status = status;
    }
}
