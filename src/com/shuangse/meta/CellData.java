package com.shuangse.meta;

import android.graphics.drawable.Drawable;

public final class CellData {
    public static final int P_FOR_SEL_RED_SMART_COMBINE=1;
    public static final int P_FOR_SEL_RED_DANTUO_COMBINE=2;
    public static final int P_FOR_SEL_BLUE_SMART_COMBINE=3;
    public static final int P_FOR_SEL_BLUE_DANTUO_COMBINE=4;
    private int cellFor;
    private int num; // �������ʵ�ĺ��������
    private int item_id; // �ú���/cell�����ں�
    private int dispNum;// Ҫ��ʾ�����֣�����©ֵ
    private Drawable dispImg; // ���ε����Ҫ��ʾ��ͼƬ
    private boolean ifAllowDoubleClickSel;//�Ƿ�����˫��ѡ��
    //���doubleclickDispImg Ϊnull,������ѡ����
    private Drawable doubleclickDispImg; // ˫����Ҫ��ʾ��ͼƬ
    
    private int row; // �ڱ���е��к�
    private int col; // �ڱ���е��к�
    //���״̬ת�� 0 -> 1 - >2->0
    private int clicked; // �Ƿ��Ѿ��������״̬ 1 = ���һ�Σ�2=���2�Σ�0=δ���

    public CellData(int _num, int _item_id, int disp_num, Drawable disp_img, 
            Drawable doubleclickDispImg,boolean ifAllowDoubleClickSel,
            int _row, int _col, int purpose) {
        this.num = _num;
        this.item_id = _item_id;
        this.dispNum = disp_num;
        this.dispImg = disp_img;
        //���doubleclickDispImg Ϊnull,������ѡ����
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
