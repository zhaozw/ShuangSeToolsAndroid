package com.shuangse.meta;

import java.util.ArrayList;

import com.shuangse.ui.SmartCombineActivity;

public class SelectedItem {
  
  public SelectedItem() {
    this.itemId = 0;
    this.selectedBlueNumbers.clear();
    this.selectedRedNumbers.clear();
    this.selectedModelId = SmartCombineActivity.INVALID_MODEL_ID;
  }
  
  private int itemId;
  //组号的模式号
  private int selectedModelId;
  //旋转矩阵选号时的红号
  private ArrayList<Integer> selectedRedNumbers = new ArrayList<Integer>();
  //胆拖组号时选择的红号
  private ArrayList<Integer> selectedRedDanNumbers = new ArrayList<Integer>();
  private ArrayList<Integer> selectedRedTuoNumbers = new ArrayList<Integer>();
  
  public ArrayList<Integer> getSelectedRedNumbers() {
    return selectedRedNumbers;
  }

  public void setSelectedRedNumbers(ArrayList<Integer> selectedRedNumbers) {
    this.selectedRedNumbers = selectedRedNumbers;
  }
  //旋转组号时选择的篮球
  private ArrayList<Integer> selectedBlueNumbers = new ArrayList<Integer>();
  public ArrayList<Integer> getSelectedBlueNumbers() {
    return selectedBlueNumbers;
  }
  public void setSelectedBlueNumbers(ArrayList<Integer> selectedBlueNumbers) {
    this.selectedBlueNumbers = selectedBlueNumbers;
  }
  //胆拖组号时选择的篮球
  private ArrayList<Integer> selectedBlueNumbersForDanTuo = new ArrayList<Integer>();
  

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getSelectedModelId() {
    return selectedModelId;
  }

  public void setSelectedModelId(int selectedModelId) {
    this.selectedModelId = selectedModelId;
  }

  public ArrayList<Integer> getSelectedRedDanNumbers() {
    return selectedRedDanNumbers;
  }

  public void setSelectedRedDanNumbers(ArrayList<Integer> selectedRedDanNumbers) {
    this.selectedRedDanNumbers = selectedRedDanNumbers;
  }

  public ArrayList<Integer> getSelectedRedTuoNumbers() {
    return selectedRedTuoNumbers;
  }

  public void setSelectedRedTuoNumbers(ArrayList<Integer> selectedRedTuoNumbers) {
    this.selectedRedTuoNumbers = selectedRedTuoNumbers;
  }

  public ArrayList<Integer> getSelectedBlueNumbersForDanTuo() {
    return selectedBlueNumbersForDanTuo;
  }

  public void setSelectedBlueNumbersForDanTuo(
      ArrayList<Integer> selectedBlueNumbersForDanTuo) {
    this.selectedBlueNumbersForDanTuo = selectedBlueNumbersForDanTuo;
  }
  
}
