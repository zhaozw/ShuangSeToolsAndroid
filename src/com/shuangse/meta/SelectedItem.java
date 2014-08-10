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
  //��ŵ�ģʽ��
  private int selectedModelId;
  //���
  private ArrayList<Integer> selectedRedNumbers = new ArrayList<Integer>();
  //�����
  private ArrayList<Integer> selectedRedDanNumbers = new ArrayList<Integer>();

  
  public ArrayList<Integer> getSelectedRedNumbers() {
    return selectedRedNumbers;
  }

  public void setSelectedRedNumbers(ArrayList<Integer> selectedRedNumbers) {
    this.selectedRedNumbers = selectedRedNumbers;
  }
  //��ת���ʱѡ�������
  private ArrayList<Integer> selectedBlueNumbers = new ArrayList<Integer>();
  public ArrayList<Integer> getSelectedBlueNumbers() {
    return selectedBlueNumbers;
  }
  public void setSelectedBlueNumbers(ArrayList<Integer> selectedBlueNumbers) {
    this.selectedBlueNumbers = selectedBlueNumbers;
  }

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
}
