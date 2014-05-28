package com.shuangse.meta;

public class AndroidPhoneInfo {
  private String deviceId;
  public final static String DEVICEID = "deviceId";
  private String softVersion;
  public final static String SOFTVERSION = "softVersion";
  private String msisdnMdn;
  public final static String MSISDNMDN = "msisdnMdn";
  private String networkOperator;
  public final static String NETWORKOPERATOR = "networkOperator";
  private String networkOperatorName;
  public final static String NETWORKOPERATORNAME = "networkOperatorName";
  private int networkType;
  public final static String NETWORKTYPE = "networkType";
  private int phoneType;
  public final static String PHONETYPE = "phoneType";
  private String simOperator;
  public final static String SIMOPERATOR = "simOperator";
  private String simOperatorName;
  public final static String SIMOPERATORNAME = "simOperatorName";
  private String simSerialNumber;
  public final static String SIMSERIALNUMBER = "simSerialNumber";
  private String subscriberId;
  public final static String SUBSCRIBERID = "subscriberId";
  private String apkVersion;
  public final static String APKVERSION = "APKVersion";

  public String toString() {
    StringBuffer sb = new StringBuffer();
    
    sb.append("deviceId=").append(deviceId).append("\n")
        .append("softVersion=").append(softVersion).append("\n")
        .append("msisdnMdn=").append(msisdnMdn).append("\n")
        .append("networkOperator=").append(networkOperator).append("\n")
        .append("networkOperatorName=").append(networkOperatorName).append("\n")
        .append("networkType=").append(networkType).append("\n")
        .append("phoneType=").append(phoneType).append("\n")
        .append("simOperator=").append(simOperator).append("\n")
        .append("simOperatorName=").append(simOperatorName).append("\n")
        .append("simSerialNumber=").append(simSerialNumber).append("\n")
        .append("subscriberId=").append(subscriberId).append("\n")
        .append("apkVersion=").append(apkVersion).append("\n");
    
        return sb.toString();
    
  }
  
  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getSoftVersion() {
    return softVersion;
  }

  public void setSoftVersion(String softVersion) {
    this.softVersion = softVersion;
  }

  public String getMsisdnMdn() {
    return msisdnMdn;
  }

  public void setMsisdnMdn(String msisdnMdn) {
    this.msisdnMdn = msisdnMdn;
  }

  public String getNetworkOperator() {
    return networkOperator;
  }

  public void setNetworkOperator(String networkOperator) {
    this.networkOperator = networkOperator;
  }

  public String getNetworkOperatorName() {
    return networkOperatorName;
  }

  public void setNetworkOperatorName(String networkOperatorName) {
    this.networkOperatorName = networkOperatorName;
  }

  public int getNetworkType() {
    return networkType;
  }

  public void setNetworkType(int networkType) {
    this.networkType = networkType;
  }

  public int getPhoneType() {
    return phoneType;
  }

  public void setPhoneType(int phoneType) {
    this.phoneType = phoneType;
  }

  public String getSimOperator() {
    return simOperator;
  }

  public void setSimOperator(String simOperator) {
    this.simOperator = simOperator;
  }

  public String getSimOperatorName() {
    return simOperatorName;
  }

  public void setSimOperatorName(String simOperatorName) {
    this.simOperatorName = simOperatorName;
  }

  public String getSimSerialNumber() {
    return simSerialNumber;
  }

  public void setSimSerialNumber(String simSerialNumber) {
    this.simSerialNumber = simSerialNumber;
  }

  public String getSubscriberId() {
    return subscriberId;
  }

  public void setSubscriberId(String subscriberId) {
    this.subscriberId = subscriberId;
  }

  public String getApkVersion() {
    return apkVersion;
  }

  public void setApkVersion(String apkVersion) {
    this.apkVersion = apkVersion;
  }
}
