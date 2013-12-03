package com.shuangse.meta;

import java.util.ArrayList;

public class RecommandHisRecord {
  private int itemId = 0;
  private int occurRedCnt = -1;
  private int occurBlueCnt = -1;
  private ArrayList<Integer> redNumbers = null;
  private ArrayList<Integer> blueNumbers = null;
  
  public RecommandHisRecord() {
     itemId = 0;
     occurRedCnt = -1;
     occurBlueCnt = -1;
     redNumbers = null;
     blueNumbers = null;
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
    if(this.redNumbers == null) return "";
    StringBuffer sb = new StringBuffer();
    for(Integer red : redNumbers) {
      if(red < 10) sb.append("0");
      sb.append(red.intValue()).append(" ");
    }
    return sb.toString();
  }
  public String getBlueNumberStr() {
    if(this.blueNumbers == null) return "";
    StringBuffer sb = new StringBuffer();
    for(Integer blue : this.blueNumbers) {
      if(blue < 10) sb.append("0");
      sb.append(blue.intValue()).append(" ");
    }
    return sb.toString();
  }
  public void setRedNumbers(ArrayList<Integer> redNumbers) {
    this.redNumbers = redNumbers;
  }
  
  public int getOccurRedCnt() {
    return occurRedCnt;
  }
  public void setOccurRedCnt(int occurRedCnt) {
    this.occurRedCnt = occurRedCnt;
  }
  public ArrayList<Integer> getBlueNumbers() {
    return blueNumbers;
  }
  public void setBlueNumbers(ArrayList<Integer> blueNumbers) {
    this.blueNumbers = blueNumbers;
  }
  public int getOccurBlueCnt() {
    return occurBlueCnt;
  }
  public void setOccurBlueCnt(int occurBlueCnt) {
    this.occurBlueCnt = occurBlueCnt;
  }
  
}
