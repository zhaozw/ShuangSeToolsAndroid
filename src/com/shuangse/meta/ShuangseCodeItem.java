package com.shuangse.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

public class ShuangseCodeItem implements Parcelable {
    public int[] red;
    public int blue;
    public int id;
    public static final int size = 6;
    //双色球和值中点
    public static final int MIDDLE_SUM = 102;
    
    public ShuangseCodeItem(Parcel source) {
      red = new int[6];
      source.readIntArray(red);
      blue = source.readInt();
      id = source.readInt();
    }
    
    public static synchronized ShuangseCodeItem instanceBy(int r1, int r2, int r3, int r4,
                    int r5, int r6, int _id, int b) {
      return new ShuangseCodeItem(_id, r1, r2, r3, r4, r5, r6, b); 
    }
    
    public ShuangseCodeItem(int _id, int r1, int r2, int r3, int r4,
                    int r5, int r6, int b) {
        
        red = new int[6];
        id = _id;
        
        red[0] = r1;
        red[1] = r2;
        red[2] = r3;
        red[3] = r4;
        red[4] = r5;
        red[5] = r6;
        
        Arrays.sort(red);
        blue = b;
    }
    
    public ShuangseCodeItem(int _id, int r[], int b) {
        red = new int[6];
        
        id = _id;
        Arrays.sort(red);
        System.arraycopy(r, 0, red, 0, 6);
        
        blue = b;
    }
    //覆盖equals 方法，这样可以用Vector.contains()方法
    public final boolean equals(Object obj) {
        if((((ShuangseCodeItem)obj).id == this.id) && 
           (((ShuangseCodeItem)obj).red[0] == this.red[0]) && 
           (((ShuangseCodeItem)obj).red[1] == this.red[1]) && 
           (((ShuangseCodeItem)obj).red[2] == this.red[2]) && 
           (((ShuangseCodeItem)obj).red[3] == this.red[3]) && 
           (((ShuangseCodeItem)obj).red[4] == this.red[4]) && 
           (((ShuangseCodeItem)obj).red[5] == this.red[5]) && 
           (((ShuangseCodeItem)obj).blue == this.blue) ){
            return true;
        } else {
            return false;
        }
    }

    /**Only return 01 02 11 12 22 23*/
    public final String toRedString() {
      StringBuffer strBuf = new StringBuffer();
      
      for(int i=0;i<6;i++) {
          if(red[i] < 10) strBuf.append(0);
          strBuf.append(red[i]);
          strBuf.append(" ");
      }
      
      return strBuf.toString();        
  }

    public final String toString() {
        StringBuffer strBuf = new StringBuffer();
        
        strBuf.append(id);
        strBuf.append(" ");
        for(int i=0;i<6;i++) {
            strBuf.append(red[i]);
            strBuf.append(" ");
        }
        
        strBuf.append(blue);
        
        return strBuf.toString();        
    }
    public final String toCNString() {
        StringBuffer strBuf = new StringBuffer();
        
        for(int i=0;i<6;i++) {
            strBuf.append(red[i]);
            if(i == 5) {
                strBuf.append(" + ");
            } else {
                strBuf.append(" ");
            }
        }
        
        strBuf.append(blue);
        
        return strBuf.toString();        
    }

    /**返回 01 02 11 14 15 31 + 07  格式字符串*/
    public final String toLineUpString() {
      StringBuffer strBuf = new StringBuffer();
      
      for(int i=0;i<6;i++) {
        if(red[i] < 10) {
          strBuf.append("0");
        }
        strBuf.append(red[i]);
        if(i == 5) {
            strBuf.append(" + ");
        } else {
            strBuf.append(" ");
        }
      }
      if(blue < 10) {
        strBuf.append("0");
      }
      strBuf.append(blue);
      
      return strBuf.toString();        
  }
    
   public String getRedStr(int redIndex) {
     StringBuilder sb = new StringBuilder();
     if(this.red[redIndex] < 10) {
       sb.append(0).append(this.red[redIndex]);
     } else {
       sb.append(this.red[redIndex]);
     }
     return sb.toString();
   }
   
   /**
    * 返回该注号码对应的20组3码组合
    * @return
    */   
   public String[] getThreeCodesCombineArray() {
     String[] threeCodesArray = new String[20];
     
     StringBuilder sb = new StringBuilder();
     sb.append(getRedStr(0)).append("_").append(getRedStr(1)).append("_").append(getRedStr(2));
     threeCodesArray[0] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(1)).append("_").append(getRedStr(3));
     threeCodesArray[1] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(1)).append("_").append(getRedStr(4));
     threeCodesArray[2] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(1)).append("_").append(getRedStr(5));
     threeCodesArray[3] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(2)).append("_").append(getRedStr(3));
     threeCodesArray[4] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(2)).append("_").append(getRedStr(4));
     threeCodesArray[5] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(2)).append("_").append(getRedStr(5));
     threeCodesArray[6] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(3)).append("_").append(getRedStr(4));
     threeCodesArray[7] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(3)).append("_").append(getRedStr(5));
     threeCodesArray[8] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(0)).append("_").append(getRedStr(4)).append("_").append(getRedStr(5));
     threeCodesArray[9] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(1)).append("_").append(getRedStr(2)).append("_").append(getRedStr(3));
     threeCodesArray[10] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(1)).append("_").append(getRedStr(2)).append("_").append(getRedStr(4));
     threeCodesArray[11] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(1)).append("_").append(getRedStr(2)).append("_").append(getRedStr(5));
     threeCodesArray[12] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(1)).append("_").append(getRedStr(3)).append("_").append(getRedStr(4));
     threeCodesArray[13] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(1)).append("_").append(getRedStr(3)).append("_").append(getRedStr(5));
     threeCodesArray[14] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(1)).append("_").append(getRedStr(4)).append("_").append(getRedStr(5));
     threeCodesArray[15] = sb.toString();
     
     sb.delete(0, sb.length());
     sb.append(getRedStr(2)).append("_").append(getRedStr(3)).append("_").append(getRedStr(4));
     threeCodesArray[16] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(2)).append("_").append(getRedStr(3)).append("_").append(getRedStr(5));
     threeCodesArray[17] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(2)).append("_").append(getRedStr(4)).append("_").append(getRedStr(5));
     threeCodesArray[18] = sb.toString();

     sb.delete(0, sb.length());
     sb.append(getRedStr(3)).append("_").append(getRedStr(4)).append("_").append(getRedStr(5));
     threeCodesArray[19] = sb.toString();

     return threeCodesArray;     
   }
   
    /**
     * 返回该注号码对应的15组2码组合
     * @return
     */
    public String[] getTwoCodesCombineArray() {
      String[] twoCodesArray = new String[15];
      
      StringBuilder sb = new StringBuilder();
      sb.append(getRedStr(0)).append("_").append(getRedStr(1));
      twoCodesArray[0] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(0)).append("_").append(getRedStr(2));
      twoCodesArray[1] = sb.toString();

      sb.delete(0, sb.length());
      sb.append(getRedStr(0)).append("_").append(getRedStr(3));
      twoCodesArray[2] = sb.toString();

      sb.delete(0, sb.length());
      sb.append(getRedStr(0)).append("_").append(getRedStr(4));
      twoCodesArray[3] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(0)).append("_").append(getRedStr(5));
      twoCodesArray[4] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(1)).append("_").append(getRedStr(2));
      twoCodesArray[5] = sb.toString();

      sb.delete(0, sb.length());
      sb.append(getRedStr(1)).append("_").append(getRedStr(3));
      twoCodesArray[6] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(1)).append("_").append(getRedStr(4));
      twoCodesArray[7] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(1)).append("_").append(getRedStr(5));
      twoCodesArray[8] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(2)).append("_").append(getRedStr(3));
      twoCodesArray[9] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(2)).append("_").append(getRedStr(4));
      twoCodesArray[10] = sb.toString();

      sb.delete(0, sb.length());
      sb.append(getRedStr(2)).append("_").append(getRedStr(5));
      twoCodesArray[11] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(3)).append("_").append(getRedStr(4));
      twoCodesArray[12] = sb.toString();

      sb.delete(0, sb.length());
      sb.append(getRedStr(3)).append("_").append(getRedStr(5));
      twoCodesArray[13] = sb.toString();
      
      sb.delete(0, sb.length());
      sb.append(getRedStr(4)).append("_").append(getRedStr(5));
      twoCodesArray[14] = sb.toString();
      
      return twoCodesArray;
    }
    
    //返回该注号码是否包含指定红球
    public final boolean containsRedNum(int redNum) {
        for(int i=0;i<6;i++) {
            if(red[i] == redNum) {
                return true;
            }
        }
        
        return false;
    }
    
    //获取本期边码集合
    public HashSet<Integer> getSideCodeSet() {
      HashSet<Integer> sideRedSet = new HashSet<Integer>();
      for(int i=0;i<6;i++) {
        if(((this.red[i] - 1) >=1) && ((this.red[i] - 1) <=33)) {
          sideRedSet.add(this.red[i] - 1);
        }
        if(((this.red[i] + 1) >=1) && ((this.red[i] + 1) <=33)) {
          sideRedSet.add(this.red[i] + 1);
        }
      }
      return sideRedSet;
    }

    /**江恩螺旋图
     * 31 32 33
     * 30 13 14 15 16 17
     * 29 12  3  4  5 18
     * 28 11  2  1  6 19
     * 27 10  9  8  7 20
     * 26 25 24 23 22 21
     *
     * 热线：如果一条直线上有两个号码以上，把这条线标示出来，（6行6列共12条线）
     *       通常就是下期的热线号码。
     * 本函数返回本期号码在上图中分布后对应的的热线号码集合
     * */
    @SuppressWarnings("unchecked")
    public HashSet<Integer> GetJiangEnHotLineNumSet() {
      HashSet<Integer> hotLineSet = new HashSet<Integer>();
      Set<?>[] line = new HashSet<?>[6];
      Set<?>[] column = new HashSet<?>[6];
      
      for(int m=0;m<6;m++) {
        line[m] = new HashSet<Integer>();
        column[m] = new HashSet<Integer>();
      }
     
      ((HashSet<Integer>)column[0]).add(31);
      ((HashSet<Integer>)column[0]).add(30);
      ((HashSet<Integer>)column[0]).add(29);
      ((HashSet<Integer>)column[0]).add(28);
      ((HashSet<Integer>)column[0]).add(27);
      ((HashSet<Integer>)column[0]).add(26);
      ((HashSet<Integer>)column[1]).add(32);
      ((HashSet<Integer>)column[1]).add(13);
      ((HashSet<Integer>)column[1]).add(12);
      ((HashSet<Integer>)column[1]).add(11);
      ((HashSet<Integer>)column[1]).add(10);
      ((HashSet<Integer>)column[1]).add(25);
      ((HashSet<Integer>)column[2]).add(33);
      ((HashSet<Integer>)column[2]).add(14);
      ((HashSet<Integer>)column[2]).add(3);
      ((HashSet<Integer>)column[2]).add(2);
      ((HashSet<Integer>)column[2]).add(9);
      ((HashSet<Integer>)column[2]).add(24);
      ((HashSet<Integer>)column[3]).add(15);
      ((HashSet<Integer>)column[3]).add(4);
      ((HashSet<Integer>)column[3]).add(1);
      ((HashSet<Integer>)column[3]).add(8);
      ((HashSet<Integer>)column[3]).add(23);
      ((HashSet<Integer>)column[4]).add(16);
      ((HashSet<Integer>)column[4]).add(5);
      ((HashSet<Integer>)column[4]).add(6);
      ((HashSet<Integer>)column[4]).add(7);
      ((HashSet<Integer>)column[4]).add(22);
      ((HashSet<Integer>)column[5]).add(17);
      ((HashSet<Integer>)column[5]).add(18);
      ((HashSet<Integer>)column[5]).add(19);
      ((HashSet<Integer>)column[5]).add(20);
      ((HashSet<Integer>)column[5]).add(21);

      ((HashSet<Integer>)line[0]).add(31);
      ((HashSet<Integer>)line[0]).add(32);
      ((HashSet<Integer>)line[0]).add(33);
      ((HashSet<Integer>)line[1]).add(30);
      ((HashSet<Integer>)line[1]).add(13);
      ((HashSet<Integer>)line[1]).add(14);
      ((HashSet<Integer>)line[1]).add(15);
      ((HashSet<Integer>)line[1]).add(16);
      ((HashSet<Integer>)line[1]).add(17);
      ((HashSet<Integer>)line[2]).add(29);
      ((HashSet<Integer>)line[2]).add(12);
      ((HashSet<Integer>)line[2]).add(3);
      ((HashSet<Integer>)line[2]).add(4);
      ((HashSet<Integer>)line[2]).add(5);
      ((HashSet<Integer>)line[2]).add(18);
      ((HashSet<Integer>)line[3]).add(28);
      ((HashSet<Integer>)line[3]).add(11);
      ((HashSet<Integer>)line[3]).add(2);
      ((HashSet<Integer>)line[3]).add(1);
      ((HashSet<Integer>)line[3]).add(6);
      ((HashSet<Integer>)line[3]).add(19);
      ((HashSet<Integer>)line[4]).add(27);
      ((HashSet<Integer>)line[4]).add(10);
      ((HashSet<Integer>)line[4]).add(9);
      ((HashSet<Integer>)line[4]).add(8);
      ((HashSet<Integer>)line[4]).add(7);
      ((HashSet<Integer>)line[4]).add(20);
      ((HashSet<Integer>)line[5]).add(26);
      ((HashSet<Integer>)line[5]).add(25);
      ((HashSet<Integer>)line[5]).add(24);
      ((HashSet<Integer>)line[5]).add(23);
      ((HashSet<Integer>)line[5]).add(22);
      ((HashSet<Integer>)line[5]).add(21);

      for(int i=0;i<6;i++) {

        int redLineCnt = 0;
        for(int redCnt = 0; redCnt < 6; redCnt++) {
          if(line[i].contains(this.red[redCnt])) {
            redLineCnt++;
          }
        }
        
        if(redLineCnt >=2) {//hotLine
            hotLineSet.addAll((HashSet<Integer>)line[i]);
        }

        int redColumnCnt = 0;
        for(int redCnt = 0; redCnt < 6; redCnt++) {
          if(column[i].contains(this.red[redCnt])) {
            redColumnCnt++;
          }
        }

        if(redColumnCnt >=2) {//hotColumn
            hotLineSet.addAll((HashSet<Integer>)column[i]);
        }
      }

      return hotLineSet;
    }
    
    /**返回红球集合*/
    public HashSet<Integer> getRedNumberSet() {
      HashSet<Integer> redSet = new HashSet<Integer>();
      for(int i=0; i<6; i++) {
        redSet.add(this.red[i]);
      }
      return redSet;
    }
    /**返回红球数组*/
    public ArrayList<Integer> getRedNumbersList() {
      ArrayList<Integer> redList = new ArrayList<Integer>();
      for(int i=0; i<6; i++) {
        redList.add(Integer.valueOf(this.red[i]));
      }
      return redList;
    }
    
    //返回是否包含给定的胆码组
    public final boolean containsRedNums(int[] redNum, int len) {
      Set<Integer> redSet = new HashSet<Integer>();
      Set<Integer> danMaSet = new HashSet<Integer>();
      
      for(int i=0; i<6; i++) {
        redSet.add(this.red[i]);
      }
      for(int j=0;j<len;j++) {
        danMaSet.add(redNum[j]);
      }
      
      if(redSet.containsAll(danMaSet)) {
        return true;
      } 
      
      return false;
    }
    
    /**判断该注号码和给定的号码组有几个相同 */
    public final int getHowManyRedSame(int[] redNum, int redCnt) {
      HashSet<Integer> redSet = this.getRedNumberSet();
      int metCnt = 0;
      
      for(int index = 0; index < redCnt; index++) {
        if(redSet.contains(Integer.valueOf(redNum[index]))) {
          metCnt++;
        }
      }
      return metCnt;
    }
    
    
    

    public final int getSum() {
        return (red[0]+red[1]+red[2]+red[3]+red[4]+red[5]);
    }
    
    //红球和 区间 0(<70) 1(70-79) 2(80-89) 3(90-99) 4(100-109) 5(110-119) 6 (>=120)
    public final int getSumDis() {
        int sum = getSum();
        if(sum < 70) return 0;
        else if((sum >= 70) && (sum <= 79)) return 1;
        else if((sum >= 80) && (sum <= 89)) return 2;
        else if((sum >= 90) && (sum <= 99)) return 3;
        else if((sum >= 100) && (sum <= 109)) return 4;
        else if((sum >= 110) && (sum <= 119)) return 5;
        else return 6;
    }

    public final int getACValue() {
        //计算AC值: AC值是指任何一组号码中所有两个
        //号码数字的正数差值的总数减去（R-1）的值，
        //其中R为投注号码数,双色球为6.
        //下面的计算方法的前提是红色6个球排过序的(由低到高）
        //这样才能保证是正数差值
        Set<Integer> tmpACset = new HashSet<Integer>();

        tmpACset.add(red[1] - red[0]);
        tmpACset.add(red[2] - red[0]);
        tmpACset.add(red[3] - red[0]);
        tmpACset.add(red[4] - red[0]);
        tmpACset.add(red[5] - red[0]);
        
        tmpACset.add(red[2] - red[1]);
        tmpACset.add(red[3] - red[1]);
        tmpACset.add(red[4] - red[1]);
        tmpACset.add(red[5] - red[1]);

        tmpACset.add(red[3] - red[2]);
        tmpACset.add(red[4] - red[2]);
        tmpACset.add(red[5] - red[2]);

        tmpACset.add(red[4] - red[3]);
        tmpACset.add(red[5] - red[3]);
        
        tmpACset.add(red[5] - red[4]);

        int acValue = tmpACset.size() - 5;

        return acValue;
    }

    //0 - 06 0奇6偶
    //1 - 15 1奇5偶
    //2 - 24 2奇4偶
    //3 - 33 3奇3偶
    //4 - 42 4奇2偶
    //5 - 51 5奇1偶
    //6 - 60 6奇0偶
    public final int getOddEvenModel() {
        int oddCnt = 0;
        
        for(int i=0;i<6;i++) {
            if(red[i] % 2 != 0) {
                oddCnt++;
            } 
        }
        
        return oddCnt;
    }
    
    //0 - 06 0大6小
    //1 - 15 1大5小
    //2 - 24 2大4小
    //3 - 33 3大3小
    //4 - 42 4大2小
    //5 - 51 5大1小
    //6 - 60 6大0小
    public final int getBigSmallModel() {
        int bigCnt = 0;
        
        for(int i=0;i<6;i++) {
            if(red[i] >= 17) {
                bigCnt++;
            } 
        }
        
        return bigCnt;
    }
    //判断本注是否含有某个特定的尾
    public final boolean ifhasSpecialTail(int tail) {

        for(int i=0; i<6; i++) {
            if((red[i] % 10) == tail) {
                return true;
            }
        }
        return false;
    }
    
    //判断该注号码的6个红球，在给定的尾号有几个号码
    //也就是获取给定尾号在本注中出几个号码
    public final int getTailNumberCnt(int tail) {
        int tailCnt = 0;
        
        for(int i=0; i<6; i++) {
            if((red[i] % 10) == tail) {
                tailCnt++;
            }
        }
        return tailCnt;
    }
    
    //判断该组号码在给定的区间（1： 1-11；2：12-22：3：23-33）区间出几个号码
    public final int getDisXOccursNumCnt(int dis) {
        
        int disCnt = 0;
        switch(dis) {
        case 1:
            for(int i=0;i<6;i++) {
                if((red[i] >=1) && (red[i]<=11)) {
                    disCnt++;
                }
            } //end for
            break;
        case 2:
            for(int i=0;i<6;i++) {
                if((red[i] >=12) && (red[i]<=22)) {
                    disCnt++;
                }
            } //end for
            break;
        case 3:
            for(int i=0;i<6;i++) {
                if((red[i] >=23) && (red[i]<=33)) {
                    disCnt++;
                }
            } //end for
            break;
        }
        
        return disCnt;
    }

    //本注占有几个尾区间(0-9共10个尾区间)
    public final int getTailDisCnt() {
        int _tenDisNum = 0;

        for(int o=0;o<10;o++) {
            if(ifhasSpecialTail(o)) {//含有该尾
                _tenDisNum++;
            }
        }
        return _tenDisNum;
    }

    // 2个连续的号码个数，3连号算2个连续，4连号算3个连续，以此类推
    public final int getIfHasContNum() {
        int contNum = 0; //2个连续的号码个数，3连号算2个连续，4连号算3个连续，5连算4个连续，以此类推

        for(int j=0; j<5; j++) {
             if( (red[j]+1) == red[j+1]) {
                 contNum++;
             }
        }
        
        return contNum;
    }

    public int describeContents() {
      return this.id;
    }

    public void writeToParcel(Parcel dest, int flags) {
      dest.writeIntArray(red);
      dest.writeInt(blue);
      dest.writeInt(id);
    }
    public static final Parcelable.Creator<ShuangseCodeItem> CREATOR = new Creator<ShuangseCodeItem>() {
      public ShuangseCodeItem createFromParcel(Parcel source) {
        
        return new ShuangseCodeItem(source);
      }
   
      public ShuangseCodeItem[] newArray(int size) {
        return new ShuangseCodeItem[size];
      } 
   
    };
}
