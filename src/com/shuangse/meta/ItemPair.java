package com.shuangse.meta;

public class ItemPair {
    private String dispText;
    private int itemVal;
    
    @Override
    public String toString() {
      return dispText;
    }
    
    public ItemPair(int itemVal, String dispText) {
      this.setDispText(dispText);
      this.setItemVal(itemVal);
    }

    public String getDispText() {
      return dispText;
    }

    public void setDispText(String dispText) {
      this.dispText = dispText;
    }

    public int getItemVal() {
      return itemVal;
    }

    public void setItemVal(int itemVal) {
      this.itemVal = itemVal;
    }
  };
  