package com.shuangse.meta;

public class UserCounterObj {
  private int totalNumber;
  private int todayReg;
  private int todayAccess;

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("�û�����").append(this.totalNumber);
    sb.append("����ע�᣺").append(this.todayReg);
    sb.append("���շ��ʣ�").append(this.todayAccess);
    return sb.toString();
  }
  public int getTotalNumber() {
    return totalNumber;
  }

  public void setTotalNumber(int totalNumber) {
    this.totalNumber = totalNumber;
  }

  public int getTodayReg() {
    return todayReg;
  }

  public void setTodayReg(int todayReg) {
    this.todayReg = todayReg;
  }

  public int getTodayAccess() {
    return todayAccess;
  }

  public void setTodayAccess(int todayAccess) {
    this.todayAccess = todayAccess;
  }

}
