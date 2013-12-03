package com.shuangse.meta;

public class ControlMsg {
  private int id;
  private String infoType;//newapk or message
  private String text;
  private String url;
  private String version;
  private int validFlag;
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("id=").append(id).append("infoType=").append(infoType)
        .append("text=").append(text).append("url=").append(url)
        .append("version").append(version).append("validFlag=")
        .append(validFlag);

    return sb.toString();
  }
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public int getValidFlag() {
    return validFlag;
  }
  public void setValidFlag(int validFlag) {
    this.validFlag = validFlag;
  }
  public String getInfoType() {
    return infoType;
  }
  public void setInfoType(String infoType) {
    this.infoType = infoType;
  }
  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }

}
