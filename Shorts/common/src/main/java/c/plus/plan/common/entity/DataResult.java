package c.plus.plan.common.entity;

/**
 * Created by fanwei on 1/26/22
 */
public class DataResult<T> {
    public static final int RET_CD_NETWORK_FAIL = -1;
    public static final int RET_CD_SUCCESS = 0;
    private T data;
    private int retCd;
    private String errorMessage;

    public DataResult() {

    }

    public DataResult(T data, int retCd) {
        this.data = data;
        this.retCd = retCd;
    }

    public int getRetCd() {
        return retCd;
    }

    public boolean isSuccess(){
        return retCd == RET_CD_SUCCESS;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setRetCd(int retCd) {
        this.retCd = retCd;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static DataResult generateFailResult(){
        DataResult dataResult = new DataResult();
        dataResult.setRetCd(RET_CD_NETWORK_FAIL);
        return dataResult;
    }
}
