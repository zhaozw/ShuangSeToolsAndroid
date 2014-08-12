package com.shuangse.meta;

public class ExtShuangseCodeItem extends ShuangseCodeItem {
    public String openDate; //¿ª½±ÈÕÆÚ
    public int totalSale;
    public int poolTotal;
    public int firstPrizeCnt;
    public int firstPrizeValue;
    public int secondPrizeCnt;
    public int secondPrizeValue;
    
    public ExtShuangseCodeItem(int _id, int r1, int r2, int r3, int r4,
            int r5, int r6, int b, String openDate) {
        super (_id, r1, r2, r3, r4, r5, r6, b);
        this.setOpenDate(openDate);
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }
    
    public String toLineString() {
        StringBuffer sb = new StringBuffer();
        sb.append(id).append(" ");
        for(int i=0;i<6;i++) {
            sb.append(red[i]).append(" ");
        }
        sb.append(blue).append(" ");
        sb.append(openDate);
        return sb.toString();
    }

}
