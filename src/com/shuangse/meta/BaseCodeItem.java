package com.shuangse.meta;

public class BaseCodeItem {
  public int[] num;
  public static int length = -1;
  
  public BaseCodeItem(int len) {
    if(len > 0) {
      num = new int[len];
      length = len;
      for(int i=0;i<length;i++) {
        num[i] = 0;
      }
    }
  }
}
