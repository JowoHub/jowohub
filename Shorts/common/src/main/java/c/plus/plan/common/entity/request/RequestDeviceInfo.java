package c.plus.plan.common.entity.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fanwei on 2/14/22
 */
public class RequestDeviceInfo {
    public final static int TYPE_APPLE = 0;
    public final static int TYPE_ANDROID = 1;

    @SerializedName("brand")
    private String brand;           //品牌

    @SerializedName("deviceId")
    private String deviceId;           //品牌


    @SerializedName("channel")
    private String channel;         //渠道

    @SerializedName("pushId")
    private String pushId;           //pushId

    @SerializedName("version")
    private int version;          //版本


    @SerializedName("versionCode")
    private String versionCode;       //版本code

    @SerializedName("type")
    private int type;               //手机类型 0：Apple， 1：Android

    @SerializedName("height")
    private String height;           //手机高度

    @SerializedName("width")
    private String width;             //手机宽度

    @SerializedName("imei")
    private String imei;              //imei值

    @SerializedName("isMfrChannel")
    private boolean isMfrChannel;       //是否支持厂商通道

    @SerializedName("mac")
    private String mac;                  //mac值

    @SerializedName("model")
    private String model;                //手机的model

    @SerializedName("sdCard")
    private boolean sdCard;             //是否有sd卡

    @SerializedName("systemCode")
    private String systemCode;          //系统版本

    @SerializedName("systemName")
    private String systemName;          //系统名称

    @SerializedName("systemSdk")
    private String systemSdk;          //系统sdk版本号

    @SerializedName("product")
    private String product;              //产品名称

    @SerializedName("manufacturer")
    private String manufacturer;          // 硬件厂商

    @SerializedName("systemUserName")
    private String systemUserName;        // 系统用户名，涉及用户信息，暂时先不传


    @SerializedName("language")
    private String language;              // 系统语言

    private String unionId;



    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isMfrChannel() {
        return isMfrChannel;
    }

    public void setMfrChannel(boolean mfrChannel) {
        isMfrChannel = mfrChannel;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isSdCard() {
        return sdCard;
    }

    public void setSdCard(boolean sdCard) {
        this.sdCard = sdCard;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemSdk() {
        return systemSdk;
    }

    public void setSystemSdk(String systemSdk) {
        this.systemSdk = systemSdk;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSystemUserName() {
        return systemUserName;
    }

    public void setSystemUserName(String systemUserName) {
        this.systemUserName = systemUserName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
