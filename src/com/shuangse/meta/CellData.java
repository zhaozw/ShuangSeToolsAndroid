package com.shuangse.meta;

import android.graphics.drawable.Drawable;

public final class CellData {
    public static final int P_FOR_SEL_RED_SMART_COMBINE=1;
    public static final int P_FOR_SEL_RED_DANTUO_COMBINE=2;
    public static final int P_FOR_SEL_BLUE_SMART_COMBINE=3;
    public static final int P_FOR_SEL_BLUE_DANTUO_COMBINE=4;
    private int cellFor;
    private int num; // 代表的真实的红或蓝号码
    private int item_id; // 该号码/cell所在期号
    private int dispNum;// 要显示的数字，如遗漏值
    private Drawable dispImg; // 单次点击后要显示的图片
    private boolean ifAllowDoubleClickSel;//是否允许双击选择
    //如果doubleclickDispImg 为null,则不允许选胆码
    private Drawable doubleclickDispImg; // 双击后要显示的图片
    
    private int row; // 在表格中的行号
    private int col; // 在表格中的列号
    //点击状态转换 0 -> 1 - >2->0
    private int clicked; // 是否已经点击过的状态 1 = 点击一次，2=点击2次，0=未点击

    public CellData(int _num, int _item_id, int disp_num, Drawable disp_img, 
            Drawable doubleclickDispImg,boolean ifAllowDoubleClickSel,
            int _row, int _col, int purpose) {
        this.num = _num;
        this.item_id = _item_id;
        this.dispNum = disp_num;
        this.dispImg = disp_img;
        //如果doubleclickDispImg 为null,则不允许选胆码
        this.doubleclickDispImg = doubleclickDispImg;
        this.ifAllowDoubleClickSel = ifAllowDoubleClickSel;
        this.row = _row;
        this.col = _col;
        this.cellFor = purpose;
        this.clicked = 0;
    }

    public final void setNum(int _num) {
        this.num = _num;
    }

    public final int getNum() {
        return num;
    }

    public final void setItem_num(int itemId) {
        this.item_id = itemId;
    }

    public final int getItem_num() {
        return item_id;
    }

    public final void setRow(int row) {
        this.row = row;
    }

    public final int getRow() {
        return row;
    }

    public final void setCol(int col) {
        this.col = col;
    }

    public final int getCol() {
        return col;
    }

    public final String toString() {
        StringBuffer str = new StringBuffer("item_id: ");

        str.append(this.item_id);
        str.append(" num: ");
        str.append(num);
        str.append(" dispNum: ");
        str.append(dispNum);
        str.append(" row: ");
        str.append(row);
        str.append(" col: ");
        str.append(col);

        return str.toString();
    }

    public final void setDispNum(int dispNum) {
        this.dispNum = dispNum;
    }

    public final int getDispNum() {
        return dispNum;
    }

    public final void setClicked(int clickedStatus) {
        this.clicked = clickedStatus;
    }

    public final int isClicked() {
        return clicked;
    }

    public final void setDispImg(Drawable dispImg) {
        this.dispImg = dispImg;
    }

    public final Drawable getDispImg() {
        return dispImg;
    }

    public Drawable getDoubleclickDispImg() {
      return doubleclickDispImg;
    }

    public void setDoubleclickDispImg(Drawable doubleclickDispImg) {
      this.doubleclickDispImg = doubleclickDispImg;
    }

    public boolean isIfAllowDoubleClickSel() {
      return ifAllowDoubleClickSel;
    }

    public void setIfAllowDoubleClickSel(boolean ifAllowDoubleClickSel) {
      this.ifAllowDoubleClickSel = ifAllowDoubleClickSel;
    }

    public int getCellFor() {
      return cellFor;
    }

    public void setCellFor(int cellFor) {
      this.cellFor = cellFor;
    }
}
