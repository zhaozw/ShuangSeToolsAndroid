package com.shuangse.meta;

import java.util.ArrayList;

import com.shuangse.ui.SmartCombineActivity;

public class MyHisRecord {
  private int recordIndex;
  private int itemId;
  private int getoutHisFlag;
  private int selectModel;
  private ArrayList<Integer> redNumbers;//复式和旋转组号时选的红球
  private ArrayList<Integer> redDanNumbers;//胆拖组号的胆码
  private ArrayList<Integer> redTuoNumbers;//胆拖组号的拖码
  private ArrayList<Integer> blueNumbers;

  @Override
  public boolean equals(Object obj) {
    if(obj == this){
      return true;
    }
    if(obj == null || obj.getClass() != this.getClass()) {
      return false;
    }

    MyHisRecord guest = (MyHisRecord) obj;
    if(selectModel == SmartCombineActivity.M_DAN_TUO_COMBINE) {
      
      return ((itemId == guest.itemId) &&
          (recordIndex == guest.recordIndex) && 
          (getoutHisFlag == guest.getoutHisFlag) &&
          (selectModel  == guest.selectModel) && 
          (redDanNumbers.equals(guest.redDanNumbers)) &&
          (redTuoNumbers.equals(guest.redTuoNumbers)) &&
          (blueNumbers.equals(guest.blueNumbers)));
      
    } else {
      
        return ((itemId == guest.itemId) &&
                 (recordIndex == guest.recordIndex) &&
                  (getoutHisFlag == guest.getoutHisFlag) &&
                  (selectModel  == guest.selectModel) && 
                  (redNumbers.equals(guest.redNumbers)) && 
                  (blueNumbers.equals(guest.blueNumbers)));
        
    }
  }

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public ArrayList<Integer> getRedNumbers() {
    return redNumbers;
  }

  public String getRedNumberStr() {
    StringBuffer sb = new StringBuffer();
    for (Integer red : redNumbers) {
      if (red < 10)
        sb.append("0");
      sb.append(red.intValue()).append(" ");
    }
    return sb.toString();
  }

  public String getRedDanNumberStr() {
    if(this.redDanNumbers == null) return "";
    StringBuffer sb = new StringBuffer();
    for (Integer red : this.redDanNumbers) {
      if (red < 10)
        sb.append("0");
      sb.append(red.intValue()).append(" ");
    }
    return sb.toString();
  }
  
  public String getRedTuoNumberStr() {
    if(this.redTuoNumbers == null) return "";
    StringBuffer sb = new StringBuffer();
    for (Integer red : this.redTuoNumbers) {
      if (red < 10)
        sb.append("0");
      sb.append(red.intValue()).append(" ");
    }
    return sb.toString();
  }
  
  public void setRedNumbers(ArrayList<Integer> redNumbers) {
    this.redNumbers = redNumbers;
  }

  public ArrayList<Integer> getBlueNumbers() {
    return blueNumbers;
  }

  public String getBlueNumberStr() {
    StringBuffer sb = new StringBuffer();
    for (Integer blue : blueNumbers) {
      if (blue < 10)
        sb.append("0");
      sb.append(blue.intValue()).append(" ");
    }
    return sb.toString();
  }

  public void setBlueNumbers(ArrayList<Integer> blueNumbers) {
    this.blueNumbers = blueNumbers;
  }

  public int getGetoutHisFlag() {
    return getoutHisFlag;
  }

  public void setGetoutHisFlag(int getoutHisFlag) {
    this.getoutHisFlag = getoutHisFlag;
  }

  public int getSelectModel() {
    return selectModel;
  }

  public void setSelectModel(int selectModel) {
    this.selectModel = selectModel;
  }

  public ArrayList<Integer> getRedDanNumbers() {
    return redDanNumbers;
  }

  public void setRedDanNumbers(ArrayList<Integer> redDanNumbers) {
    this.redDanNumbers = redDanNumbers;
  }

  public ArrayList<Integer> getRedTuoNumbers() {
    return redTuoNumbers;
  }

  public void setRedTuoNumbers(ArrayList<Integer> redTuoNumbers) {
    this.redTuoNumbers = redTuoNumbers;
  }

  public int getRecordIndex() {
    return recordIndex;
  }

  public void setRecordIndex(int recordIndex) {
    this.recordIndex = recordIndex;
  }

}
