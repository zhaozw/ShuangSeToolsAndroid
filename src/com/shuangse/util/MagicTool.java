package com.shuangse.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shuangse.meta.BaseCodeItem;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.ui.R;

public class MagicTool {

  public static Dialog customInfoMsgBox(String title, String htmlMsg, Activity activity) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    LayoutInflater inflater = activity.getLayoutInflater();
    
    View dialogView = inflater.inflate(R.layout.custscrolldialog, null);
    TextView text = (TextView) dialogView.findViewById(R.id.dispText);
    
    text.setMovementMethod(LinkMovementMethod.getInstance());
    text.setText(Html.fromHtml(htmlMsg));
    builder.setView(dialogView)
        .setTitle(title)
        .setPositiveButton(R.string.OK,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int id) {
              }
            });
    return builder.create();
  }

  
  /**返回一个ArralyList<Integer>类型数组的字符串 以空格分隔 */
  public String getStringOfArrayList(ArrayList<Integer> redList) {
    StringBuffer sb = new StringBuffer();
    for(int itm : redList) {
      if(itm < 10) {
        sb.append("0").append(itm).append(" ");
      } else {
        sb.append(itm).append(" ");
      }
    }
    return sb.toString();
  }
  
  public static int getResIDbyBluenum(int blueNum) {
   switch(blueNum) {
   case 1:
     return R.drawable.blue1;
   case 2:
     return R.drawable.blue2;
   case 3:
     return R.drawable.blue3;
   case 4:
     return R.drawable.blue4;
   case 5:
     return R.drawable.blue5;
   case 6:
     return R.drawable.blue6;
   case 7:
     return R.drawable.blue7;
   case 8:
     return R.drawable.blue8;
   case 9:
     return R.drawable.blue9;
   case 10:
     return R.drawable.blue10;
   case 11:
     return R.drawable.blue11;
   case 12:
     return R.drawable.blue12;
   case 13:
     return R.drawable.blue13;
   case 14:
     return R.drawable.blue14;
   case 15:
     return R.drawable.blue15;
   case 16:
     return R.drawable.blue16;
   case 17:
     return R.drawable.blue_big;
   case 18:
     return R.drawable.blue_small;
   case 19:
     return R.drawable.blue_odd;
   case 20:
     return R.drawable.blue_even;
   case 21:
     return R.drawable.blue_num_0;
   case 22:
     return R.drawable.blue_num_1;
   case 23:
     return R.drawable.blue_num_2;
   case 24:
     return R.drawable.blue_num_3;
   case 25:
     return R.drawable.blue_num_4;
   default:
       return -1;
   }
  }
  
  public static int getResIDbyRednum(int redNum) {
    switch(redNum) {
    case 0:
    return R.drawable.red0;
    case 1:
      return R.drawable.red1;
    case 2:
      return R.drawable.red2;
    case 3:
      return R.drawable.red3;
    case 4:
      return R.drawable.red4;
    case 5:
      return R.drawable.red5;
    case 6:
      return R.drawable.red6;
    case 7:
      return R.drawable.red7;
    case 8:
      return R.drawable.red8;
    case 9:
      return R.drawable.red9;
    case 10:
      return R.drawable.red10;
    case 11:
      return R.drawable.red11;
    case 12:
      return R.drawable.red12;
    case 13:
      return R.drawable.red13;
    case 14:
      return R.drawable.red14;
    case 15:
      return R.drawable.red15;
    case 16:
      return R.drawable.red16;
    case 17:
      return R.drawable.red17;
    case 18:
      return R.drawable.red18;
    case 19:
      return R.drawable.red19;
    case 20:
      return R.drawable.red20;
    case 21:
      return R.drawable.red21;
    case 22:
      return R.drawable.red22;
    case 23:
      return R.drawable.red23;
    case 24:
      return R.drawable.red24;
    case 25:
      return R.drawable.red25;
    case 26:
      return R.drawable.red26;
    case 27:
      return R.drawable.red27;
    case 28:
      return R.drawable.red28;
    case 29:
      return R.drawable.red29;
    case 30:
      return R.drawable.red30;
    case 31:
      return R.drawable.red31;
    case 32:
      return R.drawable.red32;
    case 33:
      return R.drawable.red33;
    default:
      return -1;
    }
  }

  public static int getDanResIDbyRednum(int redNum) {
    switch(redNum) {
    case 1:
      return R.drawable.hlred1;
    case 2:
      return R.drawable.hlred2;
    case 3:
      return R.drawable.hlred3;
    case 4:
      return R.drawable.hlred4;
    case 5:
      return R.drawable.hlred5;
    case 6:
      return R.drawable.hlred6;
    case 7:
      return R.drawable.hlred7;
    case 8:
      return R.drawable.hlred8;
    case 9:
      return R.drawable.hlred9;
    case 10:
      return R.drawable.hlred10;
    case 11:
      return R.drawable.hlred11;
    case 12:
      return R.drawable.hlred12;
    case 13:
      return R.drawable.hlred13;
    case 14:
      return R.drawable.hlred14;
    case 15:
      return R.drawable.hlred15;
    case 16:
      return R.drawable.hlred16;
    case 17:
      return R.drawable.hlred17;
    case 18:
      return R.drawable.hlred18;
    case 19:
      return R.drawable.hlred19;
    case 20:
      return R.drawable.hlred20;
    case 21:
      return R.drawable.hlred21;
    case 22:
      return R.drawable.hlred22;
    case 23:
      return R.drawable.hlred23;
    case 24:
      return R.drawable.hlred24;
    case 25:
      return R.drawable.hlred25;
    case 26:
      return R.drawable.hlred26;
    case 27:
      return R.drawable.hlred27;
    case 28:
      return R.drawable.hlred28;
    case 29:
      return R.drawable.hlred29;
    case 30:
      return R.drawable.hlred30;
    case 31:
      return R.drawable.hlred31;
    case 32:
      return R.drawable.hlred32;
    case 33:
      return R.drawable.hlred33;
    default:
      return -1;
    }
  }

  
  /**
   * 判别手机是否为正确手机号码； 号码段分配如下：
   * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
   * 联通：130、131、132、152、155、156、185、186 
   * 电信：133、153、180、189
   */
  public static boolean isMobileNum(String mobiles) {
      Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0,5-9]))\\d{8}$");
      Matcher m = p.matcher(mobiles);
      return m.matches();
  }

  // 返回给定的codeItem在给定的curItem那一期中的中奖情况
  // curItem是出的号码
  // codeItem是买的号码
  // 返回 -1： 期号不同，不是同一期号码
  // 1: 一等奖 6 + 1
  // 2: 二等奖 6 + 0
  // 3: 三等奖 5 + 1
  // 4： 四等奖 4 + 1
  // 5: 四等奖 5 + 0
  // 6: 五等奖 3 + 1
  // 7： 五等奖 4 + 0
  // 8： 六等奖 2 + 1
  // 9：六等奖 1+1
  //10: 六等奖 0 +1
  //11：没中奖3+0
  //12：没中奖2+0
  //13：没中奖1+0
  //14：没中奖0+0
  public static int getHitResult(ShuangseCodeItem curItem, ShuangseCodeItem codeItem) {
    if (curItem.id != codeItem.id)
      return -1;
    int redHit = 0;
    boolean blueHit = (curItem.blue == codeItem.blue ? true : false);

    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 6; j++) {
        if (curItem.red[i] == codeItem.red[j]) {
          redHit++;
        }
      }
    }
    if (blueHit && redHit == 6)
      return 1;
    else if (!blueHit && redHit == 6)
      return 2;
    else if (blueHit && redHit == 5)
      return 3;
    else if (blueHit && redHit == 4)
      return 4;
    else if (!blueHit && redHit == 5)
      return 5;
    else if (blueHit && redHit == 3)
      return 6;
    else if (!blueHit && redHit == 4)
      return 7;
    else if (blueHit && redHit == 2) 
      return 8;
    else if (blueHit && redHit == 1)
      return 9;
    else if (blueHit && redHit == 0)
      return 10;
    else if (!blueHit && redHit == 3)
      return 11;
    else if (!blueHit && redHit == 2)
      return 12;
    else if (!blueHit && redHit == 1)
      return 13;
    else 
      return 14;
  }
  
  /**
   * 给定含有n个数字的整数数组a，求在n个数字中选m个数字的全组合，返回组合及个数
   * @param a - 整数数组
   * @param n - n 数组中组合数字的基个数如33
   * @param m - m 组合几个
   * @param id - 期号
   * @param blue - 蓝号
   * @return - 返回组合个数
   */
  public static long combine(int a[], int n, int m, Vector<BaseCodeItem> allCodesResult) {
      allCodesResult.clear();
      long cnt = 0;
      
      int k = m;
      boolean flag = true;// 标志找到一个有效组合
      int i = 0;

      //m should smaller or equal n
      m = m > n ? n : m;

      int[] order = new int[m+1];
      for(i=0; i<=m; i++) {
          order[i] = i-1; // 注意这里order[0]=-1用来作为循环判断标识
      }

      while(order[0] == -1) {

          if(flag) {
              //保存符合要求的组合
            BaseCodeItem baseCode = new BaseCodeItem(m);
            for(int numIndex = 0;numIndex < m; numIndex++) {
              baseCode.num[numIndex] = a[order[numIndex + 1]];
            }
            allCodesResult.add(baseCode);
            cnt++;
            flag = false;
          }

          order[k]++;                // 在当前位置选择新的数字
          if(order[k] == n)          // 当前位置已无数字可选，回溯
          {
              order[k--] = 0;
              continue;
          }

          if(k < m)                  // 更新当前位置的下一位置的数字
          {
              order[++k] = order[k-1];
              continue;
          }

          if(k == m) {
              flag = true;
          }
      }

      order = null;

      return cnt;
  }

   /**
     * 给定含有n个数字的整数数组a，求在n个数字中选m个数字的全组合，返回组合及个数
     * @param a - 整数数组
     * @param n - n 数组中组合数字的基个数如33
     * @param m - m 组合几个
     * @param id - 期号
     * @param blue - 蓝号
     * @param sureR - 是否有胆码 0 无胆码
     * @return - 返回组合个数
     */
    public static long combine(int a[], int n, int m, int id, int blue,int sureR,
                              ArrayList<ShuangseCodeItem> allCodesResult) {
        allCodesResult.clear();
        long cnt = 0;
        
        int k = m;
        boolean flag = true;// 标志找到一个有效组合
        int i = 0;

        //m should smaller or equal n
        m = m > n ? n : m;

        int[] order = new int[m+1];
        for(i=0; i<=m; i++) {
            order[i] = i-1; // 注意这里order[0]=-1用来作为循环判断标识
        }

        while(order[0] == -1) {

            if(flag) {
                //保存符合要求的组合
                ShuangseCodeItem baseCode = new ShuangseCodeItem(id, 
                                  a[order[1]], a[order[2]],
                                  a[order[3]], a[order[4]],
                                  a[order[5]], a[order[6]], blue);
                if(sureR >= 1 && sureR <= 33) {
                  if(baseCode.containsRedNum(sureR)) {
                    allCodesResult.add(baseCode);
                    cnt++;
                  }
                } else {
                  allCodesResult.add(baseCode);
                  cnt++;
                }
                
                flag = false;
            }

            order[k]++;                // 在当前位置选择新的数字
            if(order[k] == n)          // 当前位置已无数字可选，回溯
            {
                order[k--] = 0;
                continue;
            }

            if(k < m)                  // 更新当前位置的下一位置的数字
            {
                order[++k] = order[k-1];
                continue;
            }

            if(k == m) {
                flag = true;
            }
        }

        order = null;

        return cnt;
    }

    /**
     * 给定含有n个数字的整数数组a，求在n个数字中选m个数字的全组合，返回组合及个数
     * @param a - 整数数组
     * @param n - n 数组中组合数字的基个数如33
     * @param m - m 组合几个
     * @param id - 期号
     * @param blue - 蓝号
     * @return - 返回组合个数
     */
    public static long combine(int a[], int n, int m, int id, int blue,
                              Vector<ShuangseCodeItem> allCodesResult) {
        allCodesResult.clear();
        long cnt = 0;
        
        int k = m;
        boolean flag = true;// 标志找到一个有效组合
        int i = 0;

        //m should smaller or equal n
        m = m > n ? n : m;

        int[] order = new int[m+1];
        for(i=0; i<=m; i++) {
            order[i] = i-1; // 注意这里order[0]=-1用来作为循环判断标识
        }

        while(order[0] == -1) {

            if(flag) {
                //保存符合要求的组合
                ShuangseCodeItem baseCode = new ShuangseCodeItem(id, 
                                  a[order[1]], a[order[2]],
                                  a[order[3]], a[order[4]],
                                  a[order[5]], a[order[6]], blue);
                allCodesResult.add(baseCode);
                cnt++;
                flag = false;
            }

            order[k]++;                // 在当前位置选择新的数字
            if(order[k] == n)          // 当前位置已无数字可选，回溯
            {
                order[k--] = 0;
                continue;
            }

            if(k < m)                  // 更新当前位置的下一位置的数字
            {
                order[++k] = order[k-1];
                continue;
            }

            if(k == m) {
                flag = true;
            }
        }

        order = null;

        return cnt;
    }
    
  /**
   * 给定n个整数数组a - 红球 和
   * blueLen个blue篮球，求其和item相比有多少注中奖
   * @param a
   * @param n
   * @param m
   * @param id
   * @param blue
   * @param blueLen
   */
  public static long  combine(int[] a, int n, int m,
      ShuangseCodeItem item, int[] blue, int blueLen,  SparseIntArray resultMap) {
    
    resultMap.clear();
    
    long cnt = 0;
    int id = item.id;
    int k = m;
    boolean flag = true;// 标志找到一个有效组合
    int i = 0;

    //m should smaller or equal n
    m = m > n ? n : m;

    int[] order = new int[m+1];
    for(i=0; i<=m; i++) {
        order[i] = i-1; // 注意这里order[0]=-1用来作为循环判断标识
    }

    while(order[0] == -1) {
        if(flag) {
            //保存符合要求的组合
          for(int bIndex=0;bIndex < blueLen; bIndex++) {
            ShuangseCodeItem baseCode = new ShuangseCodeItem(id, 
                              a[order[1]], a[order[2]],
                              a[order[3]], a[order[4]],
                              a[order[5]], a[order[6]], blue[bIndex]);
            int metIndex = MagicTool.getHitResult(item, baseCode);
            if(resultMap.get(metIndex) != 0) {
              int val = resultMap.get(metIndex);
              val += 1;
              //set new value back
              resultMap.put(metIndex, val);
            } else {
              resultMap.put(metIndex, 1);
            }
            
            cnt++;
          }   
          
          flag = false;
        }

        order[k]++;                // 在当前位置选择新的数字
        if(order[k] == n)          // 当前位置已无数字可选，回溯
        {
            order[k--] = 0;
            continue;
        }

        if(k < m)                  // 更新当前位置的下一位置的数字
        {
            order[++k] = order[k-1];
            continue;
        }

        if(k == m) {
            flag = true;
        }
    }

    order = null;

    return cnt;
  }
  
  /**胆拖组号，并验证是否符合中奖*/
  public static long  combine(int[] a, int n, int[] b, int bLen, int m,
      ShuangseCodeItem item, int[] blue, int blueLen,  SparseIntArray resultMap) {
    
    resultMap.clear();
    
    long cnt = 0;
    int id = item.id;
    int k = m;
    boolean flag = true;// 标志找到一个有效组合
    int i = 0;

    //m should smaller or equal n
    m = m > n ? n : m;

    int[] order = new int[m+1];
    for(i=0; i<=m; i++) {
        order[i] = i-1; // 注意这里order[0]=-1用来作为循环判断标识
    }

    while(order[0] == -1) {
        if(flag) {
            //保存符合要求的组合
          for(int bIndex=0;bIndex < blueLen; bIndex++) {
            ShuangseCodeItem baseCode = new ShuangseCodeItem(id, 
                              a[order[1]], a[order[2]],
                              a[order[3]], a[order[4]],
                              a[order[5]], a[order[6]], blue[bIndex]);
            //如果不包含胆码，跳过该注号码
            if(!baseCode.containsRedNums(b, bLen)) {
              continue;
            }
            
            int metIndex = MagicTool.getHitResult(item, baseCode);
            if(resultMap.get(metIndex) != 0) {
              int val = resultMap.get(metIndex);
              val += 1;
              //set new value back
              resultMap.put(metIndex, val);
            } else {
              resultMap.put(metIndex, 1);
            }
            
            cnt++;
          }
          
          flag = false;
        }

        order[k]++;                // 在当前位置选择新的数字
        if(order[k] == n)          // 当前位置已无数字可选，回溯
        {
            order[k--] = 0;
            continue;
        }

        if(k < m)                  // 更新当前位置的下一位置的数字
        {
            order[++k] = order[k-1];
            continue;
        }

        if(k == m) {
            flag = true;
        }
    }

    order = null;

    return cnt;
  }
  
  //连接2个整数数组,除掉重复的数据
  static public int[] join(int[] a1, int[] a2)  {
    Set<Integer> reSet = new HashSet<Integer>();
    for(int i=0;i<a1.length;i++) {
      reSet.add(a1[i]);
    }
    for(int i=0;i<a2.length;i++) {
      reSet.add(a2[i]);
    }
    
    int[] result = new int[reSet.size()];
    int index = 0;
    for(Integer val : reSet) {
      result[index] = val;
      index++;
    }
    return result;
  }

  //连接2个整数数组,除掉重复的数据
  static public ArrayList<Integer> join(ArrayList<Integer> a1, ArrayList<Integer> a2) {
    Set<Integer> reSet = new HashSet<Integer>();
    for(int i=0;i<a1.size();i++) {
      reSet.add(a1.get(i));
    }
    for(int i=0;i<a2.size();i++) {
      reSet.add(a2.get(i));
    }
    
    ArrayList<Integer> result = new ArrayList<Integer>();
    result.addAll(reSet);
    
    return result;
  }

  public static final char[] invalidchars = {'\0','\1','\2','\3','\4','\5','\6','\7',
                                                                          '\10','\11','\12','\13','\14','\15','\16',
                                                                          '\17','\20','\21','\22','\23','\24','\25',
                                                                          '\26','\27','\30','\31','\32','\33','\34',
                                                                          '\35','\36','\37','\177'};
  public static String replaceInvalidChars(String message){
    for (char invalidchar : invalidchars){
      message = message.replace(invalidchar, '?');
    }
    return message;
  }
  
  /**返回2个数组有几个数字相同*/
  public static int getHowManyNumSame(ArrayList<Integer> aList, ArrayList<Integer> bList) {
    int metCnt = 0;
    for(Integer a : aList) {
      if(bList.contains(a)) {
        metCnt++;
      }
    }
    return metCnt;
  }
  
  /**返回 01 02 11 格式字符串*/
  public static String getDispArrangedStr(ArrayList<Integer> valList) {
    StringBuffer sb = new StringBuffer();
    
    for(Integer a : valList) {
      if(a < 10) sb.append("0");
      sb.append(a).append(" ");
    }
    
    return sb.toString();
  }

    /** 根据空格分隔的字符串获取整数集合 */
    public static HashSet<Integer> parsetRedSetByString(String myKeepRedStr) {
      HashSet<Integer> reSet = new HashSet<Integer>();
      if(myKeepRedStr == null || myKeepRedStr.length() < 1) {
        return reSet;
      }
      
      String[] redStrings = myKeepRedStr.split(" ");
      for(int i = 0; i < redStrings.length; i++) {
        reSet.add(Integer.valueOf(redStrings[i]));
      }
      return reSet;
    }
    
    /** 根据空格分隔的字符串获取整数集合 */
    public static ArrayList<Integer> parsetRedArrayListByString(String myKeepRedStr) {
      ArrayList<Integer> reSet = new ArrayList<Integer>();
      if(myKeepRedStr == null || myKeepRedStr.length() < 1) {
        return reSet;
      }
      
      String[] redStrings = myKeepRedStr.split(" ");
      for(int i = 0; i < redStrings.length; i++) {
        reSet.add(Integer.valueOf(redStrings[i]));
      }
      return reSet;
    }


    public static String getFormatResultString(ArrayList<ShuangseCodeItem> resultCodes) {
      StringBuffer sb = new StringBuffer();
      for(ShuangseCodeItem item : resultCodes) {
        sb.append(item.toLineUpString()).append("\r\n");
      }
      return sb.toString();
    }
    
    public static boolean ifArrayContainsValue(int[] array, int val) {
      for(int len = 0; len < array.length; len++) {
        if(array[len] == val) {
          return true;
        }
      }
      return false;
    }
    
    public static boolean ifArrayContainsValueBiggerThanVal(int[] array, int val) {
      for(int len = 0; len < array.length; len++) {
        if(array[len] >= val) {
          return true;
        }
      }
      return false;
    }
    
  } 
