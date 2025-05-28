package c.plus.plan.skits;

public class DataApiInfo {
    private String method;
    private String methodDesc;
    private String paramsDesc;

    public DataApiInfo(String method, String methodDesc, String paramsDesc) {
        this.method = method;
        this.methodDesc = methodDesc;
        this.paramsDesc = paramsDesc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public String getParamsDesc() {
        return paramsDesc;
    }

    public void setParamsDesc(String paramsDesc) {
        this.paramsDesc = paramsDesc;
    }
}
