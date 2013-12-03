package com.shuangse.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.shuangse.db.DataBaseHelper;
import com.shuangse.meta.AndroidPhoneInfo;
import com.shuangse.meta.BaseCodeItem;
import com.shuangse.meta.MyHisRecord;
import com.shuangse.meta.RecommandHisRecord;
import com.shuangse.meta.SelectedItem;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.meta.SummaryData;
import com.shuangse.meta.ValueObj;

import com.shuangse.ui.R;
import com.shuangse.ui.SmartCombineActivity;
import com.shuangse.util.MagicTool;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.TableRow;

public class ShuangSeToolsSetApplication extends Application {
  private final static String TAG = "ShuangSeToolsSetApplication";
  public final static int HOT_MISS_START = 0;
  public final static int HOT_MISS_END = 2;
  public final static int WARM_MISS_START = 3;
  public final static int WARM_MISS_END = 6;
  public final static int COOL_MISS_START = 7;
  
  public final static String My_Selection_Red_String = "My_Selection_Red_String";
  public final static String My_Selection_Red_Dan_Str = "My_Selection_Red_Dan_Str";
  public final static String My_Selection_Red_Tuo_Str = "My_Selection_Red_Tuo_Str";
  public final static String My_Selection_Blue_String = "My_Selection_Blue_String";
  public final static String My_Selection_BlueForDanTuo_String = "My_Selection_BlueForDanTuo_String";
  
  //return the versionName in the AndroidManfienst.xml file
  public String getVersion() {
    try {
      PackageManager manager = this.getPackageManager();
      PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
      String version = info.versionName;
      return version;
    } catch (Exception e) {
      e.printStackTrace();
      return getResources().getString(R.string.default_version_number);
    }
  }
  
  private static volatile String smsText = "[双色球工具箱]是安卓(Android)平台上最好的双色球彩票工具，它采用遗漏走势图帮您选号，运用数学界最新的组合覆盖策略帮您实现中大奖的梦想，不妨一试，下载地址:http://23.21.160.245/a.apk";
  public String getSmsText() {
    return smsText;
  }
  public void setSmsText(String text) {
    smsText = text;
  }
  
  private volatile static ArrayList<ShuangseCodeItem> allHisData;
  public ArrayList<ShuangseCodeItem> getAllHisData() {
    return allHisData;
  }

  private SQLiteDatabase sqliteDB;
  private String DBPath = "/data/data/com.shuangse.ui/databases/";
  private String DBName = "historydata.db";
  private String neverOutDB = "neveroutdb.db";
  private SQLiteDatabase neverOutSqliteDB; //从未出号吗数据库
  //DB localsave is another DB that stores the local history data etc
  //see DataBaseHelper.java
  private DataBaseHelper dbHelper = null;
 
  //private static final String serverAddress = "http://23.21.160.245:8080/ShuangSeToolsSimpleServer/";
  private static final String serverAddress = "http://54.225.127.77:8080/ShuangSeToolsSimpleServer/";
  //private static final String serverAddress = "http://192.168.1.101:8080/ShuangSeToolsSimpleServer/";

  // 30s time out
  public HttpClient getHttpClient() {
        // 初始化一个全局的HttpClient
        BasicHttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        return new DefaultHttpClient(httpParams);
  }
  
  public String getServerAddr() {
    return serverAddress;
  }
  
  /**
   * 判断数据库是否存在 
   * @return false or true
   */
  public boolean checkDataBase() {
    Log.i(TAG, "checkDataBase entered");
    try {
      sqliteDB = SQLiteDatabase.openDatabase((DBPath + DBName), null, SQLiteDatabase.OPEN_READWRITE);
      Log.i(TAG, "checkDataBase open database successfully.database:" + (DBPath + DBName));
    } catch (SQLiteException e) {
      Log.i(TAG, "checkDataBase get exception:" + e.toString());
    }
    Log.i(TAG, "checkDataBase returned");
    return sqliteDB != null ? true : false;
  }
  
  /** 判断未出号码数据库是否存在 */
  public boolean checkNeverOutDB() {
    try {
      neverOutSqliteDB = SQLiteDatabase.openDatabase((DBPath + neverOutDB), null, SQLiteDatabase.OPEN_READWRITE);
      Log.i(TAG, "checkNeverOutDB open database successfully.database:" + (DBPath + neverOutDB));
    } catch (SQLiteException e) {
      Log.i(TAG, "checkNeverOutDB get exception:" + e.toString());
    }
    Log.i(TAG, "checkNeverOutDB returned");
    return neverOutSqliteDB != null ? true : false;    
  }
  
  /**
   * 复制数据库到手机指定文件夹下
   * 
   * @throws IOException
   */
  public void copyDataBase(String databaseName) {
    String databaseFilenames = (DBPath + databaseName);
    Log.i(TAG, "copyDataBase() database file name:" + databaseFilenames);
    File dir = new File(DBPath);
    if (!dir.exists()) {// 判断文件夹是否存在，不存在就新建一个
      dir.mkdir();
    }
    
    FileOutputStream os = null;
    try {
      os = new FileOutputStream(databaseFilenames); //得到数据库文件的写入流
    } catch (FileNotFoundException e) {
      Log.w(TAG, "can not find the database file: " + e.toString());
    }
    
    try {
      InputStream is = getResources().getAssets().open(databaseName);
      byte[] buffer = new byte[8192];
      int count = 0;
      
      while ((count = is.read(buffer)) > 0) {
        os.write(buffer, 0, count);
        os.flush();
      }
      
      is.close();
      os.close();
      
    } catch (IOException e) {
      Log.w(TAG, "open database failed." + e.toString());
    }
    
    Log.i(TAG, "copyDataBase() database file successfully.");
  }

  @Override
  public void onCreate() {
    super.onCreate();

    // 判断数据库是否存在
    if (!checkDataBase()) {
      // DB不存在就把raw里的数据库写入手机
        copyDataBase(DBName);
    }
    
    if(!checkNeverOutDB()) {
      //把从未出号的数据库写入手机
      copyDataBase(neverOutDB);
    }
    
    //打开历史数据库
    if (sqliteDB == null) {
      Log.i(TAG, "onCreate is going to open database.");
      // open the database
      try {
        sqliteDB = SQLiteDatabase.openDatabase((DBPath + DBName), null, SQLiteDatabase.OPEN_READWRITE);
      } catch (SQLiteException e) {
        throw new Error("open database failed, " + e.toString());
      }
      
      Log.i(TAG, "database is opened.");
    }
    
    //打开未出数据库
    if(neverOutSqliteDB == null) {
      Log.i(TAG, "onCreate is going to open neverOutSqliteDB.");
      // open the neverOutSqliteDB
      try {
        neverOutSqliteDB = SQLiteDatabase.openDatabase((DBPath + neverOutDB), null, SQLiteDatabase.OPEN_READWRITE);
      } catch (SQLiteException e) {
        throw new Error("open neverOutSqliteDB failed, " + e.toString());
      }
      
      Log.i(TAG, "neverOutSqliteDB is opened.");
    }
    this.dbHelper = new DataBaseHelper(this);
  }

  /* 将本地数据库的数据全部读入到本地cache中 */
  public void loadLocalHisDataIntoCache() {
    String querySQL = "select * from hisitems order by itemid desc";

    Cursor cursor = sqliteDB.rawQuery(querySQL, null);
    int cnt = cursor.getCount();

    //clear it if it is not null to ensure the reload works fine 
    if(allHisData != null) {
      synchronized(allHisData) {
        allHisData.clear(); 
      }
    }
    
    if (cnt > 0) {
        allHisData = new ArrayList<ShuangseCodeItem>(cnt + 1);
        
      synchronized (allHisData) {
        while (cursor.moveToNext()) {
          allHisData.add(new ShuangseCodeItem(cursor.getInt(cursor
              .getColumnIndex("itemid")), cursor.getInt(cursor
              .getColumnIndex("red1")), cursor.getInt(cursor
              .getColumnIndex("red2")), cursor.getInt(cursor
              .getColumnIndex("red3")), cursor.getInt(cursor
              .getColumnIndex("red4")), cursor.getInt(cursor
              .getColumnIndex("red5")), cursor.getInt(cursor
              .getColumnIndex("red6")), cursor.getInt(cursor
              .getColumnIndex("blue"))));
        }
      }

      this.sortLoadedHisData();

    } else {
      Log.w(TAG, "No history data was loaded.");
    }

    Log.w(TAG, "History data was loaded. cnt:" + cnt);

    cursor.close();
    //设置选号为下一期选号的号码
    currentSelection.setItemId(getLoalLatestItemIDFromCache() + 1);
    
    //后台删除数据库中6码出4，5，6个的记录
    Thread tx = new Thread()  {
      public void run() {
        int cnt = allHisData.size();
        //这些6码(共556组)中4，5，6的是基于2013086期计算出来的
        //这些6码在2013086期及以前的号码中从未中过4，5，6码
        int startIndex = getIndexByItemIDInLocalCache(2013086);
        Log.w(TAG, "Cnt:"+cnt + "startIndex:" + startIndex);
        
        //针对新开出的红球码，看看这些6码组是否在近期的出号中中出4，5，6，如果中出，则删除
        for (int i = startIndex; i < cnt; i++) {
            ShuangseCodeItem tmpCodeItem = (ShuangseCodeItem) allHisData.get(i);
            String query2SQL = "select * from neverout456ofsix";
            Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {});
            while (cursor2.moveToNext()) {
                 //该6码在DB中的位置关键字
                 int locVal = cursor2.getInt(0);
                 int[] redNum = new int[6];
                 for(int loc = 0; loc < 6; loc++) {
                     redNum[loc] = cursor2.getInt(loc + 1);
                 }
                 if(tmpCodeItem.getHowManyRedSame(redNum, redNum.length) >= 4) {
                     //删掉该注，因为该组已经出现4，5，6个号码了
                     try {
                       neverOutSqliteDB.delete("neverout456ofsix", "loc = ?", new String[] {String.valueOf(locVal)});
                       Log.i(TAG, redNum.toString() + " is deleted for 6 codes for 4,5,6 out.");
                       
                     }catch(Throwable e) {
                       e.printStackTrace();
                     }
                 }//end if
                 redNum = null;
            }//end while
            
            String query3SQL = "select * from neverout6of17";
            Cursor cursor3 = neverOutSqliteDB.rawQuery(query3SQL, new String[] {});
            while (cursor3.moveToNext()) {
                 //该17码在DB中的位置关键字
                 int locVal = cursor3.getInt(0);
                 int[] redNum = new int[17];
                 for(int loc = 0; loc < 17; loc++) {
                     redNum[loc] = cursor3.getInt(loc + 1);
                 }
                 if(tmpCodeItem.getHowManyRedSame(redNum, redNum.length) >= 6) {
                     //删掉该注，因为该组已经出现6个号码了
                     try {
                       neverOutSqliteDB.delete("neverout6of17", "loc = ?", new String[] {String.valueOf(locVal)});
                       Log.i(TAG, redNum.toString() + " is deleted for17 codes for 6 out.");
                     }catch(Throwable e) {
                       e.printStackTrace();
                     }
                 }//end if
                 redNum = null;
            }//end while
            
            String query4SQL = "select * from neverout56of11";
            Cursor cursor4 = neverOutSqliteDB.rawQuery(query4SQL, new String[] {});
            while (cursor4.moveToNext()) {
                 //该11码在DB中的位置关键字
                 int locVal = cursor4.getInt(0);
                 int[] redNum = new int[11];
                 for(int loc = 0; loc < 11; loc++) {
                     redNum[loc] = cursor4.getInt(loc + 1);
                 }
                 if(tmpCodeItem.getHowManyRedSame(redNum, redNum.length) >= 5) {
                     //删掉该注，因为该组已经出现5, 6个号码了
                     try {
                       neverOutSqliteDB.delete("neverout56of11", "loc = ?", new String[] {String.valueOf(locVal)});
                       Log.i(TAG, redNum.toString() + " is deleted for11 codes for 5,6 out.");
                     }catch(Throwable e) {
                       e.printStackTrace();
                     }
                 }//end if
                 redNum = null;
            }//end while

        }//end for
        
      }//end run
    };
    tx.start();
    
  }

  // 在本地内存中找出给定ItemId的其index
  public final int getIndexByItemIDInLocalCache(int itemID) {
    int size = allHisData.size();
    for (int i = 0; i < size; i++) {
      if (allHisData.get(i).id == itemID) {
        return i;
      }
    }
    return -1;
  }
  
  /**返回基于最近一期期号的红球集合（按遗漏0，1，2，3，4，5，.............排序过的）*/
  public ValueObj[] getRedNumberOrderedByMissingTimes() {
    ValueObj[] redListOrderedByMissingTimes = new ValueObj[33];
   
    int itemID = this.getLoalLatestItemIDFromCache();
    for(int red=1; red<=33; red++) {
      redListOrderedByMissingTimes[red - 1] = new ValueObj();
      redListOrderedByMissingTimes[red - 1].val = red; //红球号码
      redListOrderedByMissingTimes[red - 1].cnt = this.getRedNumMissTimes(red, itemID);//遗漏次数
    }
    
    //排序 - 按遗漏次数排序
    Arrays.sort(redListOrderedByMissingTimes, new Comparator<ValueObj>() {
      @Override
      public int compare(ValueObj lhs, ValueObj rhs) {
        int diff = lhs.cnt - rhs.cnt;
        if(diff > 0) {
          return 1;
        } else if(diff < 0) {
          return -1;
        } else {
          return 0;
        }
      }
    });
    
    for(int red=1; red<=33; red++) {
      Log.d(TAG, "red:" + redListOrderedByMissingTimes[red - 1].val + " miss:" + redListOrderedByMissingTimes[red - 1].cnt);
    }
    
    return redListOrderedByMissingTimes;
  }
  
  public int getRecommend17Cover6CodesCnt() {
    String querySQL = "select count(*) from neverout6of17";
    Cursor cursor = neverOutSqliteDB.rawQuery(querySQL, new String[] {});
    int totalCnt = 0;
    if (cursor.moveToNext()) {
      totalCnt = cursor.getInt(0);
    }
    cursor.close();
    return totalCnt;
  }
  
  /** 返回 从未出过6码的17码组 一个*/
  public HashSet<Integer> getRecommend17Cover6Codes() {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    int totalCnt = getRecommend17Cover6CodesCnt();
    
    if(totalCnt > 0) {
        Random rand = new Random();
        int randLoc = rand.nextInt(totalCnt + 1);
        boolean hasCodesThere = false;
        
        while(!hasCodesThere) {
          String query2SQL = "select * from neverout6of17 where loc = ?";
          Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {String.valueOf(randLoc)});  
          if (cursor2.moveToNext()) {
            for(int loc = 1; loc < 18; loc++) {
              redSet.add(cursor2.getInt(loc));
            }
            hasCodesThere = true;
          } else {
            hasCodesThere = false;
          }
          cursor2.close();
        }
    } //end while
    
    return redSet;
  }
  
  /**返回 从未出过4，5，6码的6码组的个数*/
  public int getRecommed6Cover456CodesCnt() {
    String querySQL = "select count(*) from neverout456ofsix";
    Cursor cursor = neverOutSqliteDB.rawQuery(querySQL, new String[] {});
    int totalCnt = 0;
    if (cursor.moveToNext()) {
      totalCnt = cursor.getInt(0);
    }
    cursor.close();
    return totalCnt;
  }
  /** 返回 从未出过4，5，6码的6码组 一个*/
  public HashSet<Integer> getRecommend6Cover456Codes() {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    int totalCnt = getRecommed6Cover456CodesCnt();
    
    if(totalCnt > 0) {
        Random rand = new Random();
        int randLoc = rand.nextInt(totalCnt + 1);
        boolean hasCodesThere = false;
        
        while(!hasCodesThere) {
          String query2SQL = "select red1,red2,red3,red4,red5,red6 from neverout456ofsix where loc = ?";
          Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {String.valueOf(randLoc)});  
          if (cursor2.moveToNext()) {
            for(int loc = 0; loc < 6; loc++) {
              redSet.add(cursor2.getInt(loc));
            }
            hasCodesThere = true;
          } else {
            hasCodesThere = false;
          }
          cursor2.close();
        }
    }//end while
    
   
    return redSet;
  }
  
  /**返回 从未出过5，6码的11码组的个数*/
  public int getRecommed11Cover56CodesCnt() {
    String querySQL = "select count(*) from neverout56of11";
    Cursor cursor = neverOutSqliteDB.rawQuery(querySQL, new String[] {});
    int totalCnt = 0;
    if (cursor.moveToNext()) {
      totalCnt = cursor.getInt(0);
    }
    cursor.close();
    return totalCnt;
  }
  /** 返回 从未出过5，6码的6码组 一个*/
  public HashSet<Integer> getRecommend11Cover56Codes() {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    int totalCnt = getRecommed11Cover56CodesCnt();
    
    if(totalCnt > 0) {
        Random rand = new Random();
        int randLoc = rand.nextInt(totalCnt + 1);
        boolean hasCodesThere = false;
        
        while(!hasCodesThere) {
          String query2SQL = "select red1,red2,red3,red4,red5,red6,red7,red8,red9,red10,red11 from neverout56of11 where loc = ?";
          Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {String.valueOf(randLoc)});  
          if (cursor2.moveToNext()) {
            for(int loc = 0; loc < 11; loc++) {
              redSet.add(cursor2.getInt(loc));
            }
            hasCodesThere = true;
          } else {
            hasCodesThere = false;
          }
          cursor2.close();
        }
    }//end while
    
   
    return redSet;
  }

  
  /**返回基于最近一期期号的热码（遗漏0-2）
   */
  public HashSet<Integer> getHotRedNumbers() {
    HashSet<Integer> redSet = new HashSet<Integer>();
    int itemID = this.getLoalLatestItemIDFromCache();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimes(red, itemID); 
      if( missValue >= HOT_MISS_START && missValue <= HOT_MISS_END) {
          redSet.add(red);
      }
    }
    return redSet;
  }

  /**返回某期(itemID期号)的热码（遗漏0-3）为预测下一期(itemID + 1)期参考
   */
  public HashSet<Integer> getHotRedNumbers(int itemID) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimes(red, itemID); 
      if( missValue >= HOT_MISS_START && missValue <= HOT_MISS_END) {
          redSet.add(red);
      }
    }
    return redSet;
  }
   
  /**返回某期号码（以该期Item在整个List中的itemindex为参数输入）的6个红球出号中，上期
   * 热码出号个数*/
  public int getHotRedNumbersCnt(int itemIndex) {
    int[] redMissValue = this.getMissCntOfItemByItemIndex(itemIndex);
    if(redMissValue == null) return 0;
    int hotCnt = 0;
    for(int i=0; i< redMissValue.length; i++) {
      if(redMissValue[i] >= HOT_MISS_START && redMissValue[i] <= HOT_MISS_END) {
        hotCnt++;
      }
    }
    return hotCnt;
  }

  /**返回某期号码（以该期Item在整个List中的itemindex为参数输入）的6个红球出号中，上期
   * 温码出号个数*/
  public int getWarmRedNumbersCnt(int itemIndex) {
    int[] redMissValue = this.getMissCntOfItemByItemIndex(itemIndex);
    if(redMissValue == null) return 0;
    int warmCnt = 0;
 
    for(int i=0; i< redMissValue.length; i++) {
      if(redMissValue[i] >= WARM_MISS_START && redMissValue[i] <= WARM_MISS_END) {
        warmCnt++;
      }
    }
    return warmCnt;
  }
  
  /**返回某期号码（以该期Item在整个List中的itemindex为参数输入）的6个红球出号中，上期
   * 温码出号个数*/
  public int getCoolRedNumbersCnt(int itemIndex) {
    int[] redMissValue = this.getMissCntOfItemByItemIndex(itemIndex);
    if(redMissValue == null) return 0;
    int coolCnt = 0;
    
    for(int i=0; i< redMissValue.length; i++) {
      if(redMissValue[i] >= COOL_MISS_START) {
        coolCnt++;
      }
    }
    return coolCnt;
  }
  
  /**返回给定遗漏值数组中热号的个数*/
  public static int getOccursCntOfHot(int[] missValue) {
    int valCnt = 0;
    for(int i=0; i<missValue.length; i++) {
      if(missValue[i] >= ShuangSeToolsSetApplication.HOT_MISS_START && 
          missValue[i] <= ShuangSeToolsSetApplication.HOT_MISS_END) {
        valCnt++;
      }
    }
    return valCnt;
  }
  /**返回给定遗漏值数组中温号的个数*/
  public static int getOccursCntOfWarm(int[] missValue) {
    int valCnt = 0;
    for(int i=0; i<missValue.length; i++) {
      if(missValue[i] >= ShuangSeToolsSetApplication.WARM_MISS_START && 
          missValue[i] <= ShuangSeToolsSetApplication.WARM_MISS_END) {
        valCnt++;
      }
    }
    return valCnt;
  }
    
  /**返回给定遗漏值数组中冷号的个数*/
  public static int getOccursCntOfCool(int[] missValue) {
    int valCnt = 0;
    for(int i=0; i<missValue.length; i++) {
      if(missValue[i] >= ShuangSeToolsSetApplication.COOL_MISS_START) {
        valCnt++;
      }
    }
    return valCnt;
  }
  
  /**返回某期(itemID期号)的温码（遗漏4-7）为预测下一期(itemID + 1)期参考
   */
  public HashSet<Integer> getWarmRedNumbers(int itemID) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimes(red, itemID); 
      if( missValue >= WARM_MISS_START && missValue <= WARM_MISS_START) {
          redSet.add(red);
      }
    }
    return redSet;
  }

  /**返回某期(itemID期号)的冷码（遗漏>=8）为预测下一期(itemID + 1)期参考
   */
  public HashSet<Integer> getCoolRedNumbers(int itemID) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimes(red, itemID); 
      if( missValue >= COOL_MISS_START) {
          redSet.add(red);
      }
    }
    return redSet;
  }  
  
  /**给定一个ItemIndex，获取其遗漏值（0，1，2，3，4，5，6，>=7）的遗漏数*/
  public int[] getMissCntOfMissValue(int itemIndex) {
    int[] missCntOfMissValue = new int[8];
    
    //获取从第2003002期截至到给定期的所有期的 遗漏模式（6个遗漏值）的列表
    ArrayList<int[]> missValueList = new ArrayList<int[]>();
    for(int index = 1; index <= itemIndex; index++) {
      int[] missVal = this.getMissCntOfItemByItemIndex(index);
      missValueList.add(missVal);
    }
    //获取每个遗漏值的遗漏期数, 遗漏值7表示遗漏大于等于7的
    for(int miss = 0; miss <= 7; miss++) {
      missCntOfMissValue[miss] = getMissCntOfMissValue(missValueList, miss);
    }
    
    return missCntOfMissValue;
  }
  
  /*获取一个给定遗漏值在给定的遗漏值表中的遗漏期数， missValue=7表示遗漏大于等于7的*/
  private int getMissCntOfMissValue(ArrayList<int[]> missValueList, int missValue) {
    int missCnt = 0;
    int maxIndex = (missValueList.size() - 1);
    
    if(missValue < 7) {
      for(int loc = maxIndex; loc >=0; loc--) {
        int[] redMissVal = missValueList.get(loc);
        if(MagicTool.ifArrayContainsValue(redMissVal, missValue)){
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else { //MissValue >= 7
      for(int loc = maxIndex; loc >=0; loc--) {
        int[] redMissVal = missValueList.get(loc);
        if(MagicTool.ifArrayContainsValueBiggerThanVal(redMissVal, 7)){
          return missCnt;
        } else {
          missCnt++;
        }
      }
    }
    
    return missCnt;
  }
  
  /**返回根据 冷热 号码 推荐的红球号码集合*/
  @SuppressLint("UseSparseArrays")
  public HashSet<Integer> getRecommendRedNumber(int index) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    //missValue (0, 1, 2, |  3, 4, 5, 6, | >=7)
    HashMap<Integer, HashSet<Integer>> missValRed = new HashMap<Integer, HashSet<Integer>>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimesByIndex(red, index);
      if(missValue >= 7) missValue = 7;
      
      HashSet<Integer> redNumSet = missValRed.get(Integer.valueOf(missValue));
      if(redNumSet == null) {
        HashSet<Integer> tmpRedNumSet = new HashSet<Integer>();
        tmpRedNumSet.add(red);
        missValRed.put(Integer.valueOf(missValue), tmpRedNumSet);
      } else {
        redNumSet.add(Integer.valueOf(red));
      }
    }
    //只加入热码和温码
    for(int redMissValue=0; redMissValue<7; redMissValue++) {
      HashSet<Integer> redNumSet = missValRed.get(Integer.valueOf(redMissValue));
      if(redNumSet == null || redNumSet.size() < 2) continue; //独号多可抛弃
      redSet.addAll(redNumSet);//其他都加入
    }
    
    return redSet;
  }
  
  /**返回某期(以它在数组中的位置locIndex)的热码（遗漏0-2）为预测下一期参考
   */
  public HashSet<Integer> getHotRedNumbersByIndex(int locIndex) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimesByIndex(red, locIndex); 
      if( missValue >= HOT_MISS_START && missValue <= HOT_MISS_END) {
          redSet.add(red);
      }
    }
    return redSet;
  }
  
  /**返回某期(以它在数组中的位置locIndex)的温码（遗漏4-6）为预测下一期参考
   */
  public HashSet<Integer> getWarmRedNumbersByIndex(int locIndex) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimesByIndex(red, locIndex); 
      if( missValue >= WARM_MISS_START && missValue <= WARM_MISS_END) {
          redSet.add(red);
      }
    }
    return redSet;
  }
  
  /**返回某期(以它在数组中的位置locIndex)的冷码（遗漏>=7）为预测下一期参考
   */
  public HashSet<Integer> getCoolRedNumbersByIndex(int locIndex) {
    HashSet<Integer> redSet = new HashSet<Integer>();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimesByIndex(red, locIndex); 
      if( missValue >= COOL_MISS_START) {
          redSet.add(red);
      }
    }
    return redSet;
  }
    
  /**返回基于最近一期期号的温码（遗漏4-6）
   */
  public HashSet<Integer> getWarmRedNumbers() {
    HashSet<Integer> redSet = new HashSet<Integer>();
    int itemID = this.getLoalLatestItemIDFromCache();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimes(red, itemID); 
      if( missValue >= WARM_MISS_START && missValue <= WARM_MISS_END) {
          redSet.add(red);
      }
    }
    return redSet;
  }

  /**返回基于最近一期期号的冷码（遗漏>=7，9.....）
   */
  public HashSet<Integer> getCoolRedNumbers() {
    HashSet<Integer> redSet = new HashSet<Integer>();
    int itemID = this.getLoalLatestItemIDFromCache();
    
    for(int red=1; red<=33; red++) {
      int missValue = this.getRedNumMissTimes(red, itemID); 
      if( missValue >= COOL_MISS_START) {
          redSet.add(red);
      }
    }
    return redSet;
  }

  
  /**返回基于最近一期期号，给定遗漏次数的红球集合*/
  public HashSet<Integer> getRedNumberOfMissingTimes(int missCnt) {
    int itemID = this.getLoalLatestItemIDFromCache();
    return this.getRedNumberOfMissingTimes(missCnt, itemID);
  }
  
 /**返回给定期号中，给定遗漏次数的红球集合*/
 public HashSet<Integer> getRedNumberOfMissingTimes(int missCnt, int itemID) {
   HashSet<Integer> redSet = new HashSet<Integer>();
   for(int red=1; red<=33; red++) {
     if(this.getRedNumMissTimes(red, itemID) == missCnt) {
         redSet.add(red);
     }
   }
   return redSet;
 }
 
 /**返回给定locIndex中，给定遗漏次数的红球集合*/
 public HashSet<Integer> getRedNumberOfMissingTimesByIndex(int missCnt, int locIndex) {
   HashSet<Integer> redSet = new HashSet<Integer>();
   for(int red=1; red<=33; red++) {
     if(this.getRedNumMissTimesByIndex(red, locIndex) == missCnt) {
         redSet.add(red);
     }
   }
   return redSet;
 }
 
 /**获取1-33红球在给定Index上的遗漏数据*/
 public ValueObj[] getAllRedNumMissTimesByIndex(int redNum, int locIndex) {
   ValueObj[] redMiss = new ValueObj[33];
   for(int i=1; i<=33; i++) {
     redMiss[i-1].val = i;
     redMiss[i-1].cnt = this.getRedNumMissTimesByIndex(i, locIndex);
   }
   return redMiss;
 }

 /**获取1-33红球在给定itemID上的遗漏数据*/
 public ValueObj[] getAllRedNumMissTimes(int redNum, int itemId) {
   ValueObj[] redMiss = new ValueObj[33];
   for(int i=1; i<=33; i++) {
     redMiss[i-1].val = i;
     redMiss[i-1].cnt = this.getRedNumMissTimes(i, itemId);
   }
   return redMiss;
 }

 /**获取给定红球1-33在给定index期中的遗漏次数*/
 private final int getRedNumMissTimesByIndex(int redNum, int locIndex) {
   int missCnt = 0;

   if (locIndex != -1) {
     // 该期itemID在本地内存中
     for (int j = locIndex; j >= 0; j--) {
       if (allHisData.get(j).containsRedNum(redNum)) {
         // 当前红号码出现了
         return missCnt;
       } else {
         missCnt++;
       }
     }
   } else {
     return -1;
   }

   return missCnt;
 }
 
  // 获取给定红号码1－33在给定期数中的遗漏次数
  public final int getRedNumMissTimes(int redNum, int itemID) {
    // 找出该号码目前的遗漏期数

    // 先在本地内存中找出其index
    int startIndex = getIndexByItemIDInLocalCache(itemID);
    int missCnt = 0;

    if (startIndex != -1) {
      // 该期itemID在本地内存中
      for (int j = startIndex; j >= 0; j--) {
        if (allHisData.get(j).containsRedNum(redNum)) {
          // 当前红号码出现了
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else {
      return -1;
    }

    return missCnt;
  }
  
  /**获取给定期号ItemId期中，出现的6个红球在上一期(ItemId-1)中的遗漏次数(6个)*/
  public int[] getMissCntOfItemByItemID(int itemID) {
    if(itemID <= 2003001) return null;
    int loc = this.getIndexByItemIDInLocalCache(itemID);
    if(loc <= 0) return null;
    ShuangseCodeItem item = this.getCodeItemByIndex(loc);
    if(item == null) return null;
    
    int[] missCnt = new int[6];
    for(int i=0;i<6;i++) {
      //获取该期6个号码在上一期中的遗漏次数
      missCnt[i] = this.getRedNumMissTimesByIndex(item.red[i], loc - 1);
    }
    return missCnt;
  }
  
  /**获取给定期(以给定的Index在数组定定位)中，出现的6个红球在上一期(itemIndex-1)中的遗漏次数(6个)
   * @param itemIndex: >=1
   * */
  public int[] getMissCntOfItemByItemIndex(int itemIndex) {
    if(itemIndex <= 0) return null;
    ShuangseCodeItem item = this.getCodeItemByIndex(itemIndex);
    if(item == null) return null;
    
    int[] missCnt = new int[6];
    for(int i=0;i<6;i++) {
      missCnt[i] = this.getRedNumMissTimesByIndex(item.red[i], itemIndex - 1);
    }
    return missCnt;
  } 
  
  /**统计全部历史中，上期热号+温号红球集合在下一期中出的个数
   * HotAndWarmOut: 1 OccursCnt:2
      HotAndWarmOut: 2 OccursCnt:33
      HotAndWarmOut: 3 OccursCnt:198
      HotAndWarmOut: 4 OccursCnt:487
      HotAndWarmOut: 5 OccursCnt:543
      HotAndWarmOut: 6 OccursCnt:248
   * */
  public SparseIntArray statHotAndWarmSetOccursData() {
    int totalCnt = allHisData.size();
    SparseIntArray missCntNumber = new SparseIntArray();
    for(int i=1; i<totalCnt; i++) {
      int[] redMissCnt = this.getMissCntOfItemByItemIndex(i);
      int hotAndWarmCnt = 0;
      for(int y=0;y<6;y++) {
        if(redMissCnt[y] >= HOT_MISS_START && redMissCnt[y] <= WARM_MISS_END) {
          hotAndWarmCnt++;
        }
      }
      
//      Log.e(TAG, "Index:" + i + " miss value:" 
//                              + redMissCnt[0] + " "
//                              + redMissCnt[1] + " "
//                              + redMissCnt[2] + " "
//                              + redMissCnt[3] + " "
//                              + redMissCnt[4] + " "
//                              + redMissCnt[5] + " "
//                              + "Hot and Warm out count: " + hotAndWarmCnt);
      
      if(missCntNumber.indexOfKey(hotAndWarmCnt) < 0) {
        missCntNumber.put(hotAndWarmCnt, 0);
      } else {
        missCntNumber.put(hotAndWarmCnt, (missCntNumber.get(hotAndWarmCnt) + 1));
      }
    }
    
    for(int cnt = 0; cnt < missCntNumber.size(); cnt++) {
        int key = missCntNumber.keyAt(cnt);
        int val = missCntNumber.get(key);
        Log.e(TAG, "HotAndWarmOut: " + key + " OccursCnt:" + val);
    }
    
    return missCntNumber;
  }

  /**统计全部历史中，上期江恩热线集合在下一期中出的个数
 
   * iangEnHotLineOut: 0 OccursCnt:49 期
    JiangEnHotLineOut: 1 OccursCnt:193期
    JiangEnHotLineOut: 2 OccursCnt:381期
    JiangEnHotLineOut: 3 OccursCnt:452期
    JiangEnHotLineOut: 4 OccursCnt:298期
    JiangEnHotLineOut: 5 OccursCnt:107期
    JiangEnHotLineOut: 6 OccursCnt:20期
   * */
  public SparseIntArray statJiangEnSetOccursData() {
    int totalCnt = allHisData.size();
    SparseIntArray jiangEnHotLineCntNumber = new SparseIntArray();
    for(int i=1; i<totalCnt; i++) {
      ShuangseCodeItem lastItem = this.getCodeItemByIndex(i-1);
      ShuangseCodeItem codeItem = this.getCodeItemByIndex(i);
       HashSet<Integer> lastJiangEnSet = lastItem.GetJiangEnHotLineNumSet();
      
       int outCnt = 0;
       for(int y=0; y<6; y++) {
         if(lastJiangEnSet.contains(Integer.valueOf(codeItem.red[y]))) {
           outCnt++;
         }
       }
       Log.e(TAG, "Index:" + i + "Occurs from last JiangEnHotLineSet: " + outCnt);
      
      if(jiangEnHotLineCntNumber.indexOfKey(outCnt) < 0) {
        jiangEnHotLineCntNumber.put(outCnt, 0);
      } else {
        jiangEnHotLineCntNumber.put(outCnt, (jiangEnHotLineCntNumber.get(outCnt) + 1));
      }
    }
    
    for(int cnt = 0; cnt < jiangEnHotLineCntNumber.size(); cnt++) {
        int key =  jiangEnHotLineCntNumber.keyAt(cnt);
        int val = jiangEnHotLineCntNumber.get(key);
        Log.e(TAG, "JiangEnHotLineOut: " + key + " OccursCnt:" + val);
    }
    return jiangEnHotLineCntNumber;
  }
  
  /**
   * 软件推荐号码历史 - 
   * 统计历史上推荐的红球（每期的热+温码集合） 和篮球 在下一期中出号的情况
   * 
   * */
  public ArrayList<RecommandHisRecord> statHotAndWarmSetOccursSummary() {
    ArrayList<RecommandHisRecord> resultList = new ArrayList<RecommandHisRecord>();
    int totalCnt = allHisData.size();
    //只计算最近100期 - 包括当前期
    for(int i=totalCnt; i>=(totalCnt - 100); i--) {
      //基于上期的热+温码
      
      HashSet<Integer> redRecommendNumbers = this.getRecommendRedNumber(i - 1);
      HashSet<Integer> blueRecommendNumbers = this.getRecommendBlueNumbers(i);
      
      //当期中出号码
      ShuangseCodeItem item = this.getCodeItemByIndex(i);
      
      int hotAndWarmCnt = 0;
      int recommendBlueCnt = 0;
      int itemID = -1;
      if(item == null) {
        //下一期推荐
        hotAndWarmCnt = -1;//-1 表示未开奖
        recommendBlueCnt = -1;
        itemID = getCurrentSelection().getItemId(); //下一期的ID
      } else {
        //历史期推荐
        itemID = item.id;
        
        for(int y=0; y<6; y++) {
          if(redRecommendNumbers.contains(Integer.valueOf(item.red[y]))) {
            hotAndWarmCnt++;
          }
        }
        
        for(Integer blue : blueRecommendNumbers) {
          if(blue.intValue() == item.blue) {
            recommendBlueCnt++;
          }
        }
        
      }

      //Log.e(TAG, "itemId:" + item.id + " HotAndWarm numbers:" + hotRedNumbers + " out count: " + hotAndWarmCnt);
      RecommandHisRecord recommendRecord = new RecommandHisRecord();
      recommendRecord.setItemId(itemID);
      ArrayList<Integer> redNumbers = new ArrayList<Integer>();
      ArrayList<Integer> blueNumbers = new ArrayList<Integer>();
      
      redNumbers.addAll(redRecommendNumbers);
      recommendRecord.setRedNumbers(redNumbers);
      recommendRecord.setOccurRedCnt(hotAndWarmCnt);
      
      blueNumbers.addAll(blueRecommendNumbers);
      //篮球出号情况
      recommendRecord.setBlueNumbers(blueNumbers);
      recommendRecord.setOccurBlueCnt(recommendBlueCnt);
      
      resultList.add(recommendRecord);
    }
    return resultList;
  }

  // 获取红球的遗漏数据
  public final SparseArray<SummaryData> getRedNumSummaryData() {
    //HashMap<Integer, SummaryData> summaryData = null;
    SparseArray<SummaryData> summaryData = null;

    int size = 33;
    int valStart = 1;
    int valEnd = 33;

    //summaryData = new HashMap<Integer, SummaryData>(size);
    summaryData = new SparseArray<SummaryData>(size);
    for (int val = valStart; val <= valEnd; val++) {
      summaryData.put(val, new SummaryData());
    }

    int total = allHisData.size();
    for (int i = 0; i < total; i++) {
      ShuangseCodeItem codeItem = allHisData.get(i);

      // 共有6个红球同时出现，其他的都未出现
      int[] attVal = new int[6];
      SummaryData[] sumData = new SummaryData[6];
      for (int redIndex = 0; redIndex < 6; redIndex++) {
        attVal[redIndex] = codeItem.red[redIndex];
        sumData[redIndex] = summaryData.get(attVal[redIndex]);

        sumData[redIndex].totalOccursCnt++;
        sumData[redIndex].curContOccursCnt++;
        if (sumData[redIndex].maxContOccursCnt < sumData[redIndex].curContOccursCnt) {
          sumData[redIndex].maxContOccursCnt = sumData[redIndex].curContOccursCnt;
        }
        sumData[redIndex].curMissCnt = 0;
      }

      for (int val = valStart; val <= valEnd; val++) {
        if ((val != attVal[0]) && (val != attVal[1]) && (val != attVal[2])
            && (val != attVal[3]) && (val != attVal[4]) && (val != attVal[5])) {
          SummaryData otherData = summaryData.get(val);
          otherData.curMissCnt++;
          if (otherData.maxMissCnt < otherData.curMissCnt) {
            otherData.maxMissCnt = otherData.curMissCnt;
          }
          otherData.curContOccursCnt = 0;
        }
      }
    }

    return summaryData;
  }

  /**获取红球热 - 温 - 冷 码出号个数的遗漏统计数据
   * flag 1 - hot
   * flag 2 - warm
   * flag 3 - cool*/
  public final SparseArray<SummaryData> getRedNumHotWarmCoolOccursSummaryData(int flag) {
    SparseArray<SummaryData> summaryData = null;

    int size = 7;//属性个数 出0-6个，共7个属性值
    int valStart = 0;//出0个
    int valEnd = 6;//出6个

    summaryData = new SparseArray<SummaryData>(size);
    for (int val = valStart; val <= valEnd; val++) {
      summaryData.put(val, new SummaryData());
    }

    int total = allHisData.size();
    for (int i = 0; i < total; i++) {
        
        int attVal  =  -1;
        if(flag == 1) {
          attVal = this.getHotRedNumbersCnt(i);//当期出几个
        } else if(flag == 2) {
          attVal = this.getWarmRedNumbersCnt(i);//当期出几个
        } else if(flag == 3) {
          attVal = this.getCoolRedNumbersCnt(i);//当期出几个
        }
          
        SummaryData sumData = summaryData.get(attVal);
        if (sumData != null) {
          sumData.totalOccursCnt++;
          sumData.curContOccursCnt++;
          if (sumData.maxContOccursCnt <= sumData.curContOccursCnt) {
            sumData.maxContOccursCnt = sumData.curContOccursCnt;
          }
          sumData.curMissCnt = 0;
        } else {
          Log.e(TAG, "hot-warm-cool occurs cnt num: " + attVal + " is not included.");
        }
        //针对其他的值做处理
        for (int val = valStart; val <= valEnd; val++) {
          if(val != attVal) {
            SummaryData otherData = summaryData.get(val);
            otherData.curMissCnt++;
            if (otherData.maxMissCnt <= otherData.curMissCnt) {
              otherData.maxMissCnt = otherData.curMissCnt;
            }
            otherData.curContOccursCnt = 0;
          }
        }//end for
    }//end for

    return summaryData;
  }

  
  // 获取篮球的遗漏数据
  public final SparseArray<SummaryData> getBlueNumSummaryData() {
    //HashMap<Integer, SummaryData> summaryData = null;
    SparseArray<SummaryData> summaryData = null;
    
    int size = 16;
    int valStart = 1;
    int valEnd = 16;

    //summaryData = new HashMap<Integer, SummaryData>(size);
    summaryData = new SparseArray<SummaryData>(size);
    
    for (int val = valStart; val <= valEnd; val++) {
      summaryData.put(val, new SummaryData());
    }

    int total = allHisData.size();
    for (int i = 0; i < total; i++) {
      ShuangseCodeItem codeItem = allHisData.get(i);

      int attVal = codeItem.blue;
      SummaryData sumData = summaryData.get(attVal);
      if (sumData != null) {
        sumData.totalOccursCnt++;
        sumData.curContOccursCnt++;
        if (sumData.maxContOccursCnt < sumData.curContOccursCnt) {
          sumData.maxContOccursCnt = sumData.curContOccursCnt;
        }
        sumData.curMissCnt = 0;
      } else {
        Log.e(TAG, "blue num: " + attVal + " is not included.");
      }
      for (int val = valStart; val <= valEnd; val++) {
        if (val != attVal) {
          SummaryData otherData = summaryData.get(val);
          otherData.curMissCnt++;
          if (otherData.maxMissCnt < otherData.curMissCnt) {
            otherData.maxMissCnt = otherData.curMissCnt;
          }
          otherData.curContOccursCnt = 0;
        }
      }
    }

    return summaryData;
  }

  // 获取篮球属性的遗漏数据
  public final SparseArray<SummaryData> getBlueAttrSummaryData(int attIndex) {
    //HashMap<Integer, SummaryData> summaryData = null;
    SparseArray<SummaryData> summaryData = new SparseArray<SummaryData>();

    int size = 0;
    int valStart = 0;
    int valEnd = 0;

    switch (attIndex) {
    case 0:
    case 1:
      size = 2;
      valStart = 0;
      valEnd = 1;
      break;
    case 2:
      size = 3;
      valStart = 0;
      valEnd = 2;
      break;
    case 3:
      size = 4;
      valStart = 1;
      valEnd = 4;
      break;
    default:
      return null;
    }

    //summaryData = new HashMap<Integer, SummaryData>(size);
    summaryData = new SparseArray<SummaryData>(size);
    
    for (int val = valStart; val <= valEnd; val++) {
      summaryData.put(val, new SummaryData());
    }

    int total = allHisData.size();
    for (int i = 0; i < total; i++) {
      ShuangseCodeItem codeItem = allHisData.get(i);

      int attVal = getBlueNumAttrValue(attIndex, codeItem.blue);
      SummaryData sumData = summaryData.get(attVal);
      if (sumData != null) {
        sumData.totalOccursCnt++;
        sumData.curContOccursCnt++;
        if (sumData.maxContOccursCnt < sumData.curContOccursCnt) {
          sumData.maxContOccursCnt = sumData.curContOccursCnt;
        }
        sumData.curMissCnt = 0;
      } else {
        Log.e(TAG, "attIndex:" + attIndex + " value " + attVal
            + " is not included.");
      }
      for (int val = valStart; val <= valEnd; val++) {
        if (val != attVal) {
          SummaryData otherData = summaryData.get(val);
          otherData.curMissCnt++;
          if (otherData.maxMissCnt < otherData.curMissCnt) {
            otherData.maxMissCnt = otherData.curMissCnt;
          }
          otherData.curContOccursCnt = 0;
        }
      }
    }

    return summaryData;
  }

  // 获取给定蓝号blueNum的指定属性的值
  public final int getBlueNumAttrValue(int attIndex, int blueNum) {

    switch (attIndex) {
    case 0: // 篮球大小属性
      if (blueNum >= 9)
        return 1; // 大
      else
        return 0; // 小
    case 1: // 篮球奇偶属性
      if (blueNum % 2 != 0)
        return 1;// 奇
      else
        return 0; // 偶
    case 2: // 篮球除3余数属性
      return (blueNum % 3); // return 0, 1, 2
    case 3: // 篮球区间分布(1,2,3,4)
      switch (blueNum) {
      case 1:
      case 2:
      case 3:
      case 4:
        return 1;
      case 5:
      case 6:
      case 7:
      case 8:
        return 2;
      case 9:
      case 10:
      case 11:
      case 12:
        return 3;
      case 13:
      case 14:
      case 15:
      case 16:
        return 4;
      default:
        return -1;
      }
    default:
      return -1;
    }
  }

  /** 获取篮球属性attIndex的值为attVal的，在当前期遗漏几期 */
  public final int getBlueNumAttrMissTimes(int attIndex, int attVal, int itemID) {
    // 找出蓝号码的属性在目前给定期号中的遗漏期数
    int startIndex = getIndexByItemIDInLocalCache(itemID);
    int missCnt = 0;

    if (startIndex != -1) {
      // 该期itemID在本地内存中
      for (int j = startIndex; j >= 0; j--) {
        if (getBlueNumAttrValue(attIndex, allHisData.get(j).blue) == attVal) {
          // 当前蓝号码的属性出现了出现了
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else {
      return -1;
    }

    return missCnt;
  }
  
  /** 获取红球热码出号为hotValue(0，1，2 ，3，4，5，6)的在当前期遗漏几期
   * itemIndex-- 当期的Index
   * hotValue-- 当期6个红球的遗漏值数组中热号出号个数
   * */
  public final int getRedNumHotOccurMissTimes(int itemIndex, int hotValue) {
    // 找出红球的属性在目前给定期号中的遗漏期数
    int startIndex = itemIndex;
    int missCnt = 0;
    
    if (startIndex != -1) {
      // 该期itemID在本地内存中
      for (int j = startIndex; j >= 0; j--) {
        if (this.getHotRedNumbersCnt(j) == hotValue) {
          // 当前红球遗漏属性的值出现了
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else {
      return -1;
    }

    return missCnt;
  }
  
  /** 获取红球温码出号为warmValue(0，1，2 ，3，4，5，6)的在当前期遗漏几期
   * itemIndex-- 当期的Index
   * hotValue-- 当期6个红球的遗漏值数组中温号出号个数
   * */
  public final int getRedNumWarmOccurMissTimes(int itemIndex, int warmValue) {
    // 找出红球的属性在目前给定期号中的遗漏期数
    int startIndex = itemIndex;
    int missCnt = 0;
    
    if (startIndex != -1) {
      // 该期itemID在本地内存中
      for (int j = startIndex; j >= 0; j--) {
        if (this.getWarmRedNumbersCnt(j) == warmValue) {
          // 当前红球遗漏属性的值出现了
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else {
      return -1;
    }

    return missCnt;
  }  
  
  /** 获取红球冷码出号为coolValue(0，1，2 ，3，4，5，6)的在当前期遗漏几期
   * itemIndex-- 当期的Index
   * hotValue-- 当期6个红球的遗漏值数组中冷号出号个数
   * */
  public final int getRedNumCoolOccurMissTimes(int itemIndex, int coolValue) {
    // 找出红球的属性在目前给定期号中的遗漏期数
    int startIndex = itemIndex;
    int missCnt = 0;
    
    if (startIndex != -1) {
      // 该期itemID在本地内存中
      for (int j = startIndex; j >= 0; j--) {
        if (this.getCoolRedNumbersCnt(j) == coolValue) {
          // 当前红球遗漏属性的值出现了
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else {
      return -1;
    }

    return missCnt;
  }  
  
  public final int getBlueNumMissTimes(int blueNum, int itemID) {
    // 找出该号码目前的遗漏期数
    // 先在本地内存中找出其index
    int startIndex = getIndexByItemIDInLocalCache(itemID);
    int missCnt = 0;

    if (startIndex != -1) {
      // 该期itemID在本地内存中
      for (int j = startIndex; j >= 0; j--) {
        if (allHisData.get(j).blue == blueNum) {
          // 当前蓝号码出现了
          return missCnt;
        } else {
          missCnt++;
        }
      }
    } else {
      return -1;
    }

    return missCnt;
  }

  public ShuangseCodeItem getCodeItemByID(int codeId) {
    // 先在本地内存中找出其index
    int localIndex = getIndexByItemIDInLocalCache(codeId);

    if (localIndex != -1) {
      return ((ShuangseCodeItem) allHisData.get(localIndex));
    } else {
      return null;
    }
  }

  public ShuangseCodeItem getCodeItemByIndex(int index) {

    if (index >= 0 && index < allHisData.size()) {
      return allHisData.get(index);
    } else {
      return null;
    }
  }

  public int getItemIDByCodes(int r1, int r2, int r3, int r4, int r5, int r6,
      int blue) {
    int cnt = allHisData.size();
    for (int i = 0; i < cnt; i++) {
      ShuangseCodeItem tmpCodeItem = (ShuangseCodeItem) allHisData.get(i);
      if ((tmpCodeItem.blue == blue) && (tmpCodeItem.red[0] == r1)
          && (tmpCodeItem.red[1] == r2) && (tmpCodeItem.red[2] == r3)
          && (tmpCodeItem.red[3] == r4) && (tmpCodeItem.red[4] == r5)
          && (tmpCodeItem.red[5] == r6)) {
        return tmpCodeItem.id;
      }
    }
    return -1;
  }

  //在本地cache中查找给定的6个红球对应的期号
  public int getItemIDByCodes(int r1, int r2, int r3, int r4, int r5, int r6) {
    int cnt = allHisData.size();
    for (int i = 0; i < cnt; i++) {
      ShuangseCodeItem tmpCodeItem = (ShuangseCodeItem) allHisData.get(i);
      if ((tmpCodeItem.red[0] == r1) && (tmpCodeItem.red[1] == r2)
          && (tmpCodeItem.red[2] == r3) && (tmpCodeItem.red[3] == r4)
          && (tmpCodeItem.red[4] == r5) && (tmpCodeItem.red[5] == r6)) {
        return tmpCodeItem.id;
      }
    }
    return -1;
  }
  //在本地数据库中查找是否给定的红球是数据库中的号码
  public boolean checkIfRedNumbersinLocalDB(int r1,int r2,int r3, int r4, int r5, int r6) {
    String querySQL = "select count(*) from hisitems where red1=? and red2=? and red3=? and red4=? and red5=? and red6=?";
    Cursor cursor = sqliteDB.rawQuery(querySQL, new String[] { String.valueOf(r1), String.valueOf(r2), String.valueOf(r3), 
        String.valueOf(r4),String.valueOf(r5),String.valueOf(r6)});
    
    int total = 0;
    if (cursor.moveToNext()) {
      total = cursor.getInt(0);
    }
    cursor.close();

    if (total > 0) {
      return true;
    } else {
      return false;
    }
  }
  //判断某组号码是否是历史中奖号码
  //return  true - 是历史中奖号码
  //            false - 不是
  boolean ifItemIsHistoryItem(int r1, int r2, int r3, int r4, int r5, int r6) {
    if(getItemIDByCodes(r1, r2, r3, r4, r5, r6) != -1) {
      return true;
    } else {
      return false;
    }
  }
  //判断某组号码是否是历史中奖号码
  //return  true - 是历史中奖号码
  //            false - 不是
  boolean ifItemIsHistoryItem(int[] r) {
    if(getItemIDByCodes(r[0], r[1], r[2], r[3], r[4], r[5]) != -1) {
      return true;
    } else {
      return false;
    }
  }
  // 检查指定的item是否在本地数据库中
  // return true -- 如果在本地库中存在
  // return false -- 不存在
  public boolean checkIfItemInLocalDB(ShuangseCodeItem item) {
    String querySQL = "select count(*) from hisitems where itemid = ?";
    Cursor cursor = sqliteDB.rawQuery(querySQL,
        new String[] { String.valueOf(item.id) });

    int total = 0;
    if (cursor.moveToNext()) {
      total = cursor.getInt(0);
    }
    cursor.close();

    if (total > 0) {
      return true;
    } else {
      return false;
    }
  }

  public int getTotalLocalDBHisDataCnt() {
    String querySQL = "select count(*) from hisitems";
    Cursor cursor = sqliteDB.rawQuery(querySQL, null);

    int total = 0;
    if (cursor.moveToNext()) {
      total = cursor.getInt(0);
    }

    cursor.close();

    return total;
  }

  // 从本地cache中获取最新的itemID
  public int getLoalLatestItemIDFromCache() {
    if (allHisData != null) {
      return (allHisData.get(allHisData.size() - 1).id);
    }
    return 2003001;
  }

  // 获取本地数据库中最新的ItemID
  public int getLatestItemIDFromLocalDB() {
    if (getTotalLocalDBHisDataCnt() < 1) {
      return 2003001;
    } else {
      String querySQL = "select max(itemid) from hisitems";
      Cursor cursor = sqliteDB.rawQuery(querySQL, null);

      int maxItemID = 2003001;
      if (cursor.moveToNext()) {
        maxItemID = cursor.getInt(0);
      }
      cursor.close();

      return maxItemID;
    }
  }

  @Override
  public void onTerminate() {
    Log.i(TAG, "onTerminate()");
    super.onTerminate();
  }

  /****************************************************************
   * 根据以下格式数据获取item 
   * //<?xml version="1.0" encoding="UTF-8"?> 
   * //<HISDATA>
   * //<ITEM> 
   * //<ID>2012012</ID> 
   * //<RED>1</RED> 
   * //<RED>2</RED> 
   * //<RED>3</RED>
   * //<RED>4</RED> 
   * //<RED>5</RED> 
   * //<RED>6</RED> 
   * //<BLUE>1</BLUE> 
   * //</ITEM> 
   * //</HISDATA>
   * 
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   *****************************************************************/
  public void parseAndStoreItemsData(String responseContent)
      throws ParserConfigurationException, SAXException, IOException {
    // transfer this String to XML document format
    StringReader sr = new StringReader(responseContent);
    InputSource is = new InputSource(sr);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document respXMLDoc = builder.parse(is);

    Element root = respXMLDoc.getDocumentElement();
    NodeList items = root.getChildNodes();
    int howManyItems = items.getLength();

    Log.i(TAG, "items.size:" + howManyItems);
    /**
     * each item has following structure <ITEM> <ID>2003001</ID> <RED>10</RED>
     * <RED>11</RED> <RED>12</RED> <RED>13</RED> <RED>26</RED> <RED>28</RED>
     * <BLUE>11</BLUE> </ITEM>
     */
    if (howManyItems > 0) {
      for (int i = 0; i < howManyItems; i++) {
        Node itemNode = items.item(i);
        NodeList itemContent = itemNode.getChildNodes();

        Node idNode = itemContent.item(0);
        String idStr = idNode.getTextContent();
        String[] redStr = new String[6];

        for (int redIndex = 0; redIndex < 6; redIndex++) {
          Node redNode = itemContent.item(redIndex + 1);
          redStr[redIndex] = redNode.getTextContent();
        }
        Node blueNode = itemContent.item(7);
        String blueStr = blueNode.getTextContent();

        ShuangseCodeItem tmpItem = new ShuangseCodeItem(
            Integer.parseInt(idStr), Integer.parseInt(redStr[0]),
            Integer.parseInt(redStr[1]), Integer.parseInt(redStr[2]),
            Integer.parseInt(redStr[3]), Integer.parseInt(redStr[4]),
            Integer.parseInt(redStr[5]), Integer.parseInt(blueStr));

        if (!storeItemData(tmpItem)) {
          Log.e(TAG, "failed to store the item: " + tmpItem.toString() + " into local DB.");
          // continue insert others.
        }

      }// end for

    } else {
      Log.w(TAG, "there is no data in this string.");
    }
    
  }

  /**
   * 将给定的item保持到本地手机的SQLite数据库中
   * 
   * @param hisDataList
   */
  private boolean storeItemData(ShuangseCodeItem codeItem) {
    ContentValues newEntry = new ContentValues();

    newEntry.put("itemid", codeItem.id);
    newEntry.put("red1", codeItem.red[0]);
    newEntry.put("red2", codeItem.red[1]);
    newEntry.put("red3", codeItem.red[2]);
    newEntry.put("red4", codeItem.red[3]);
    newEntry.put("red5", codeItem.red[4]);
    newEntry.put("red6", codeItem.red[5]);
    newEntry.put("blue", codeItem.blue);

    try {

      sqliteDB.insertOrThrow("hisitems", null, newEntry);

    } catch (SQLException e) {

      e.printStackTrace();

      return false;

    }
    // Log.i(TAG, codeItem.toString() +
    // " is inserted into local DB successfully.");
    return true;
  }

  private void sortLoadedHisData() {
    synchronized (allHisData) {
      Collections.sort(allHisData, new Comparator<ShuangseCodeItem>() {
        @Override
        public int compare(ShuangseCodeItem o1, ShuangseCodeItem o2) {
          int itemID1 = ((ShuangseCodeItem) o1).id;
          int itemID2 = ((ShuangseCodeItem) o2).id;
          return (itemID1 - itemID2);
        }
      });
    }
  }

  // 创建全局变量
  public static TableRow.LayoutParams rowPara;
  public static TableRow.LayoutParams headLeftCellPara;
  public static TableRow.LayoutParams headRightCellPara;
  public static TableRow.LayoutParams leftCellPara;
  public static TableRow.LayoutParams rightCellPara;
  
  @SuppressWarnings("deprecation")
  public void createGlobalDatas() {
    rowPara = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.FILL_PARENT);
    rowPara.setMargins(0, 0, 0, 0); // left,top,right,bottom

    headLeftCellPara = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
    headLeftCellPara.setMargins(1, 1, 1, 1); // left,top,right,bottom

    headRightCellPara = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
    headRightCellPara.setMargins(0, 1, 1, 1); // left,top,right,bottom

    leftCellPara = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
    leftCellPara.setMargins(1, 0, 1, 1); // left,top,right,bottom

    rightCellPara = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
    rightCellPara.setMargins(0, 0, 1, 1); // left,top,right,bottom
  }

  
  public AndroidPhoneInfo getSystemInformation() {
    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    AndroidPhoneInfo info = new AndroidPhoneInfo();
    
    /*    
     * 唯一的设备ID：    
     * GSM手机的 IMEI 和 CDMA手机的 MEID.     
     * Return null if device ID is not available.    
     */      
    info.setDeviceId(tm.getDeviceId());//String       
           
    /*    
     * 设备的软件版本号：    
     * 例如：the IMEI/SV(software version) for GSM phones.    
     * Return null if the software version is not available.     
     */      
    info.setSoftVersion(tm.getDeviceSoftwareVersion());//String       
           
    /*    
     * 手机号：    
     * GSM手机的 MSISDN.    
     * Return null if it is unavailable.     
     */      
    info.setMsisdnMdn(tm.getLine1Number());//String       
           
    /*    
     * MCC+MNC(mobile country code + mobile network code)    
     * 注意：仅当用户已在网络注册时有效。    
     *    在CDMA网络中结果也许不可靠。    
     */
    info.setNetworkOperator(tm.getNetworkOperator());//String       
           
    /*    
     * 按照字母次序的current registered operator(当前已注册的用户)的名字    
     * 注意：仅当用户已在网络注册时有效。    
     *    在CDMA网络中结果也许不可靠。    
     */      
    info.setNetworkOperatorName(tm.getNetworkOperatorName());//String       
           
    /*    
     * 当前使用的网络类型：    
     * 例如： NETWORK_TYPE_UNKNOWN  网络类型未知  0    
       NETWORK_TYPE_GPRS     GPRS网络  1    
       NETWORK_TYPE_EDGE     EDGE网络  2    
       NETWORK_TYPE_UMTS     UMTS网络  3    
       NETWORK_TYPE_HSDPA    HSDPA网络  8     
       NETWORK_TYPE_HSUPA    HSUPA网络  9    
       NETWORK_TYPE_HSPA     HSPA网络  10    
       NETWORK_TYPE_CDMA     CDMA网络,IS95A 或 IS95B.  4    
       NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5    
       NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6    
       NETWORK_TYPE_1xRTT    1xRTT网络  7    
     */      
    info.setNetworkType(tm.getNetworkType());//int
    
    /*    
     * 手机类型：    
     * 例如： PHONE_TYPE_NONE  无信号    
       PHONE_TYPE_GSM   GSM信号    
       PHONE_TYPE_CDMA  CDMA信号    
     */
    info.setPhoneType(tm.getPhoneType());//int       
    /*    
     * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.    
     * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字.    
     * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).    
     */      
    info.setSimOperator(tm.getSimOperator());//String       
           
    /*    
     * 服务商名称：    
     * 例如：中国移动、联通    
     * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).    
     */      
    info.setSimOperatorName(tm.getSimOperatorName());//String
           
    /*    
     * SIM卡的序列号：    
     * 需要权限：READ_PHONE_STATE    
     */      
    info.setSimSerialNumber(tm.getSimSerialNumber());//String
    
    /*    
     * 唯一的用户ID：    
     * 例如：IMSI(国际移动用户识别码) for a GSM phone.    
     * 需要权限：READ_PHONE_STATE    
     */      
    info.setSubscriberId(tm.getSubscriberId());//String
    
    return info;
  }
  
  public static SelectedItem getCurrentSelection() {
    return currentSelection;
  }
  public static void setCurrentSelection(SelectedItem currentSelection) {
    ShuangSeToolsSetApplication.currentSelection = currentSelection;
  }
  //当前选择的号码
  private static volatile SelectedItem currentSelection = new SelectedItem();
  
  /**旋转组号码
   * 
   * @param modelNum -- 组号模式
   * @param currentSelRedNums -- 要组号的红球号码
   * @param sureR - 红球胆码
   * @param ifGetOutHistoryCode -- 是否删除历史号码
   * @param ifCheckSum -- 是否对选择号码的和值进行检查
   * @param nextID -- 期号
   * @param blue - 篮球
   * @param allCombinedCodes -- 结果列表
   * @return -1 -- 选定的红球的和值不符合平衡标准，不建议组号
   *                 -2  -- 给定的模式号错误，组合条件错等
   *                  1  -- 成功组号， 结果放入allCombinedCodes list中
   */
  public int combineCodes(int modelNum, int[] currentSelRedNums, 
      int sureR, boolean ifGetOutHistoryCode, boolean ifCheckSum, int nextID, int blue,
      ArrayList<ShuangseCodeItem> allCombinedCodes) {
    if(allCombinedCodes == null || currentSelRedNums == null || 
        currentSelRedNums.length < SmartCombineActivity.MIN_SEL_RED_NUMBERS) {
      return -2;
    }
    allCombinedCodes.clear();
    if(modelNum == SmartCombineActivity.M_TOTAL_COMBINE) {
      
      return (int) MagicTool.combine(currentSelRedNums, currentSelRedNums.length, 6, nextID, blue, sureR, allCombinedCodes);
      
    } else if (modelNum == SmartCombineActivity.M_8_S_6_G_5_4_ITEM) { // 8红中6保5 - 4注
      int A, B, C, D, E, F, G, H;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[4];

      int sum = A + B + C + D + E + F + G + H;
      int codeNum = 8;
      if (ifCheckSum && (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, C, E, F, G, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, B, D, E, F, H, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(A, C, D, E, G, H, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(B, C, D, F, G, H, nextID, blue);
      for (int i = 0; i < 4; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }

    } else if (modelNum == SmartCombineActivity.M_9_S_6_G_5_7_ITEM) { // 9红中6保5 - 7注
      int A, B, C, D, E, F, G, H, I;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[7];

      int sum = A + B + C + D + E + F + G + H + I;
      int codeNum = 9;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, C, D, F, H, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, B, C, E, G, I, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(A, B, D, F, G, H, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(A, C, D, E, G, I, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem
          .instanceBy(A, C, E, F, G, I, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem
          .instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem
          .instanceBy(B, D, E, F, H, I, nextID, blue);

      for (int i = 0; i < 7; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }

    } else if (modelNum == SmartCombineActivity.M_10_S_6_G_5_14_ITEM) { // 10红中6保5 - 14注
      int A, B, C, D, E, F, G, H, I, J;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[14];
      int sum = A + B + C + D + E + F + G + H + I + J;
      int codeNum = 10;
      if (ifCheckSum && (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, C, E, F, I, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, B, D, E, G, I, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(A, B, D, E, G, J, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(A, B, F, H, I, J, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem
          .instanceBy(A, C, D, E, H, J, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem
          .instanceBy(A, C, D, F, G, J, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem
          .instanceBy(A, C, E, G, H, I, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem
          .instanceBy(A, C, G, H, I, J, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem
          .instanceBy(A, D, E, F, H, I, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem
          .instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(B, C, D, H, I, J, nextID, blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(B, C, E, F, I, J, nextID, blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(B, E, F, G, H, J, nextID, blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(D, E, F, G, I, J,nextID, blue);

      for (int i = 0; i < 14; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    }else if (modelNum == SmartCombineActivity.M_10_S_6_G_4_3_ITEM) {// 10个数字-中6保4-3注
      int A, B, C, D, E, F, G, H, I, J;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[3];
      int sum = A + B + C + D + E + F + G + H + I + J;
      int codeNum = 10;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, D, E, F, J, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(B, C, D, G, H, I, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(C, E, F, G, H, I, nextID, blue);

      for (int i = 0; i < 3; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    } else if (modelNum == SmartCombineActivity.M_11_S_6_G_4_5_ITEM) {// 11红-中6保4-5注
      int A, B, C, D, E, F, G, H, I, J, K;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
      }
      int sum = A + B + C + D + E + F + G + H + I + J + K;
      int codeNum = 11;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[5];
      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, C, D, F, J, K, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, E, F, G, H, J, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(B, C, D, F, I, K, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(B, F, G, H, I, J, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem
          .instanceBy(C, D, G, H, J, K, nextID, blue);

      for (int i = 0; i < 5; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }

    } else if (modelNum == SmartCombineActivity.M_12_S_6_G_4_6_ITEM) {// 12个数字-中6保4-6注
      int A, B, C, D, E, F, G, H, I, J, K, L;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[6];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L;
      int codeNum = 12;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, F, G, I, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, D, E, F, H,  I, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, E, I, J, K, L, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(B, C, G, J, K, L, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(D, F, H, J, K, L, nextID, blue);

      for (int i = 0; i < 6; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }

    } else if (modelNum == SmartCombineActivity.M_15_S_6_G_4_19_ITEM) {// 15红中6保4 - 19注
      int A, B, C, D, E, F, G, H, I, J, K, L, M, N, O;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
        M = currentSelRedNums[11];
        N = currentSelRedNums[12];
        O = currentSelRedNums[13];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
        M = currentSelRedNums[12];
        N = currentSelRedNums[13];
        O = currentSelRedNums[14];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[19];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M + N + O;
      int codeNum = 15;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, C, D, E, N, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, B, G, H, M, O, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(A, B, I, J, K, L, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(A, C, D, E, K, M, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem
          .instanceBy(A, C, F, H, I, L, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem
          .instanceBy(A, D, E, G, I, L, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem
          .instanceBy(A, F, G, J, K, N, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem
          .instanceBy(A, F, L, M, N, O, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem
          .instanceBy(B, C, E, G, L, N, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem
          .instanceBy(B, C, I, K, M, N, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(B, D, F, G, H, K, nextID,
          blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(B, D, F, J, L, M, nextID,
          blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(B, E, F, I, K, O, nextID,
          blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(C, D, G, I, J, O, nextID,
          blue);
      pBaseCode[14] = ShuangseCodeItem.instanceBy(C, E, F, H, J, O, nextID,
          blue);
      pBaseCode[15] = ShuangseCodeItem.instanceBy(C, E, G, K, L, M, nextID,
          blue);
      pBaseCode[16] = ShuangseCodeItem.instanceBy(D, H, K, L, N, O, nextID,
          blue);
      pBaseCode[17] = ShuangseCodeItem.instanceBy(E, H, I, J, M, N, nextID,
          blue);
      pBaseCode[18] = ShuangseCodeItem.instanceBy(F, G, H, M, N, O, nextID,
          blue);

      for (int i = 0; i < 19; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    } else if (modelNum == SmartCombineActivity.M_17_S_6_G_4_33_ITEM) {//17红中6保4 - 33注
      int A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
        M = currentSelRedNums[11];
        N = currentSelRedNums[12];
        O = currentSelRedNums[13];
        P = currentSelRedNums[14];
        Q = currentSelRedNums[15];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
        M = currentSelRedNums[12];
        N = currentSelRedNums[13];
        O = currentSelRedNums[14];
        P = currentSelRedNums[15];
        Q = currentSelRedNums[16];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[33];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M + N + O + P
          + Q;
      int codeNum = 17;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ){
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, C, G, P, Q, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, B, D, E, I, P, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(A, B, D, L, O, Q, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(A, B, H, J, N, P, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem
          .instanceBy(A, C, E, H, K, M, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem
          .instanceBy(A, C, F, H, I, O, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem
          .instanceBy(A, C, I, J, K, O, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem
          .instanceBy(A, D, F, H, K, O, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem
          .instanceBy(A, D, G, K, L, N, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem
          .instanceBy(A, E, F, L, N, Q, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(A, E, G, M, O, P, nextID,
          blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(A, F, G, I, J, M, nextID,
          blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(B, C, D, F, M, N, nextID,
          blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(B, C, F, I, K, L, nextID,
          blue);
      pBaseCode[14] = ShuangseCodeItem.instanceBy(B, C, I, L, M, P, nextID,
          blue);
      pBaseCode[15] = ShuangseCodeItem.instanceBy(B, D, G, H, K, O, nextID,
          blue);
      pBaseCode[16] = ShuangseCodeItem.instanceBy(B, E, F, G, H, L, nextID,
          blue);
      pBaseCode[17] = ShuangseCodeItem.instanceBy(B, E, F, J, O, Q, nextID,
          blue);
      pBaseCode[18] = ShuangseCodeItem.instanceBy(B, E, G, J, K, L, nextID,
          blue);
      pBaseCode[19] = ShuangseCodeItem.instanceBy(B, I, K, M, N, Q, nextID,
          blue);
      pBaseCode[20] = ShuangseCodeItem.instanceBy(C, D, E, G, I, Q, nextID,
          blue);
      pBaseCode[21] = ShuangseCodeItem.instanceBy(C, D, F, J, L, P, nextID,
          blue);
      pBaseCode[22] = ShuangseCodeItem.instanceBy(C, E, K, N, O, P, nextID,
          blue);
      pBaseCode[23] = ShuangseCodeItem.instanceBy(C, G, H, J, N, Q, nextID,
          blue);
      pBaseCode[24] = ShuangseCodeItem.instanceBy(C, G, L, M, O, Q, nextID,
          blue);
      pBaseCode[25] = ShuangseCodeItem.instanceBy(D, E, H, I, J, N, nextID,
          blue);
      pBaseCode[26] = ShuangseCodeItem.instanceBy(D, E, I, L, M, O, nextID,
          blue);
      pBaseCode[27] = ShuangseCodeItem.instanceBy(D, F, H, M, P, Q, nextID,
          blue);
      pBaseCode[28] = ShuangseCodeItem.instanceBy(D, J, K, M, P, Q, nextID,
          blue);
      pBaseCode[29] = ShuangseCodeItem.instanceBy(F, G, I, N, O, P, nextID,
          blue);
      pBaseCode[30] = ShuangseCodeItem.instanceBy(F, G, J, K, M, P, nextID,
          blue);
      pBaseCode[31] = ShuangseCodeItem.instanceBy(H, I, K, L, P, Q, nextID,
          blue);
      pBaseCode[32] = ShuangseCodeItem.instanceBy(H, J, L, M, N, O, nextID,
          blue);

      for (int i = 0; i < 33; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } else if (modelNum == SmartCombineActivity.M_9_S_4_G_4_12_ITEM) {// 9红中4保4 - 12注
      int A, B, C, D, E, F, G, H, I;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[12];
      int sum = A + B + C + D + E + F + G + H + I;
      int codeNum = 9;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem
          .instanceBy(A, B, C, D, E, F, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem
          .instanceBy(A, B, C, D, H, I, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem
          .instanceBy(A, B, C, E, G, I, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem
          .instanceBy(A, B, D, E, G, H, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem
          .instanceBy(A, B, F, G, H, I, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem
          .instanceBy(A, C, D, F, G, I, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem
          .instanceBy(A, C, E, F, G, H, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem
          .instanceBy(A, D, E, F, H, I, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem
          .instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem
          .instanceBy(B, C, E, F, H, I, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(B, D, E, F, G, I, nextID,
          blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(C, D, E, G, H, I, nextID,
          blue);

      for (int i = 0; i < 12; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    }  else if (modelNum == SmartCombineActivity.M_11_S_6_G_5_22_ITEM) {//11码中6保5共22注
      int A, B, C, D, E, F, G, H, I, J, K;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[22];
      int sum = A + B + C + D + E + F + G + H + I + J + K;
      int codeNum = 11;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
        return -1;
      }

    pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, E, F, H, nextID, blue); 
    pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, G, I, K, nextID, blue);
    pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, D, E, G, I, nextID, blue);
    pBaseCode[3] = ShuangseCodeItem.instanceBy(A, B, D, F, J, K, nextID, blue);
    pBaseCode[4] = ShuangseCodeItem.instanceBy(A, B, D, H, I, J, nextID, blue);
    pBaseCode[5] = ShuangseCodeItem.instanceBy(A, B, E, F, G, J, nextID, blue);
    pBaseCode[6] = ShuangseCodeItem.instanceBy(A, C, D, E, J, K, nextID, blue);
    pBaseCode[7] = ShuangseCodeItem.instanceBy(A, C, D, F, G, H, nextID, blue);
    pBaseCode[8] = ShuangseCodeItem.instanceBy(A, C, D, H, I, K, nextID, blue);
    pBaseCode[9] = ShuangseCodeItem.instanceBy(A, C, F, G, I, J, nextID, blue);
    pBaseCode[10] = ShuangseCodeItem.instanceBy(A, E, F, H, I, K, nextID, blue);
    pBaseCode[11] = ShuangseCodeItem.instanceBy(A, E, G, H, J, K, nextID, blue);
    pBaseCode[12] = ShuangseCodeItem.instanceBy(B, C, D, E, F, I, nextID, blue);
    pBaseCode[13] = ShuangseCodeItem.instanceBy(B, C, D, G, H, J, nextID, blue);
    pBaseCode[14] = ShuangseCodeItem.instanceBy(B, C, E, I, J, K, nextID, blue);
    pBaseCode[15] = ShuangseCodeItem.instanceBy(B, C, F, H, J, K, nextID, blue);
    pBaseCode[16] = ShuangseCodeItem.instanceBy(B, D, E, G, H, K, nextID, blue);
    pBaseCode[17] = ShuangseCodeItem.instanceBy(B, F, G, H, I, K, nextID, blue);
    pBaseCode[18] = ShuangseCodeItem.instanceBy(C, D, E, F, G, K, nextID, blue);
    pBaseCode[19] = ShuangseCodeItem.instanceBy(C, E, G, H, I, J, nextID, blue);
    pBaseCode[20] = ShuangseCodeItem.instanceBy(D, E, F, H, I, J, nextID, blue);
    pBaseCode[21] = ShuangseCodeItem.instanceBy(D, F, G, I, J, K, nextID, blue);
    
      for (int i = 0; i < 22; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }

    } else if (modelNum == SmartCombineActivity.M_12_S_6_G_5_38_ITEM) {//12码中6保5共38注
      int A, B, C, D, E, F, G, H, I, J, K, L;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[38];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L;
      int codeNum = 12;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
     
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, D, I, L, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, D, J, K, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, C, E, G, J, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, B, C, H, I, K, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, B, D, E, F, H, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(A, B, D, G, K, L, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(A, B, E, G, J, K, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(A, B, F, H, J, L, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(A, B, F, I, J, L, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(A, C, D, F, J, K, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(A, C, E, F, G, I, nextID, blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(A, C, E, H, K, L, nextID, blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(A, C, F, J, K, L, nextID, blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(A, C, G, H, J, L, nextID, blue);
      pBaseCode[14] = ShuangseCodeItem.instanceBy(A, D, E, F, G, L, nextID, blue);
      pBaseCode[15] = ShuangseCodeItem.instanceBy(A, D, E, H, I, J, nextID, blue);
      pBaseCode[16] = ShuangseCodeItem.instanceBy(A, D, G, H, I, J, nextID, blue);
      pBaseCode[17] = ShuangseCodeItem.instanceBy(A, E, I, J, K, L, nextID, blue);
      pBaseCode[18] = ShuangseCodeItem.instanceBy(A, F, G, H, I, K, nextID, blue);
      pBaseCode[19] = ShuangseCodeItem.instanceBy(B, C, D, E, J, L, nextID, blue);
      pBaseCode[20] = ShuangseCodeItem.instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[21] = ShuangseCodeItem.instanceBy(B, C, E, F, K, L, nextID, blue);
      pBaseCode[22] = ShuangseCodeItem.instanceBy(B, C, F, G, K, L, nextID, blue);
      pBaseCode[23] = ShuangseCodeItem.instanceBy(B, C, H, I, J, K, nextID, blue);
      pBaseCode[24] = ShuangseCodeItem.instanceBy(B, D, E, F, I, K, nextID, blue);
      pBaseCode[25] = ShuangseCodeItem.instanceBy(B, D, E, G, H, I, nextID, blue);
      pBaseCode[26] = ShuangseCodeItem.instanceBy(B, D, F, G, I, J, nextID, blue);
      pBaseCode[27] = ShuangseCodeItem.instanceBy(B, D, H, J, K, L, nextID, blue);
      pBaseCode[28] = ShuangseCodeItem.instanceBy(B, E, G, H, I, L, nextID, blue);
      pBaseCode[29] = ShuangseCodeItem.instanceBy(C, D, E, G, H, K, nextID, blue);
      pBaseCode[30] = ShuangseCodeItem.instanceBy(C, D, E, G, I, K, nextID, blue);
      pBaseCode[31] = ShuangseCodeItem.instanceBy(C, D, F, H, I, L, nextID, blue);
      pBaseCode[32] = ShuangseCodeItem.instanceBy(C, E, F, H, I, J, nextID, blue);
      pBaseCode[33] = ShuangseCodeItem.instanceBy(C, G, I, J, K, L, nextID, blue);
      pBaseCode[34] = ShuangseCodeItem.instanceBy(D, E, F, G, J, L, nextID, blue);
      pBaseCode[35] = ShuangseCodeItem.instanceBy(D, F, H, I, K, L, nextID, blue);
      pBaseCode[36] = ShuangseCodeItem.instanceBy(E, F, G, H, I, L, nextID, blue);
      pBaseCode[37] = ShuangseCodeItem.instanceBy(E, F, G, H, J, K, nextID, blue);
      
      for (int i = 0; i < 38; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    
    } 
//    else if (modelNum == SmartCombineActivity.M_13_S_6_G_5_61_ITEM) {//13码中6保5共61注
//      int A, B, C, D, E, F, G, H, I, J, K, L, M;
//      if (sureR > 0) {
//        A = sureR;
//        B = currentSelRedNums[0];
//        C = currentSelRedNums[1];
//        D = currentSelRedNums[2];
//        E = currentSelRedNums[3];
//        F = currentSelRedNums[4];
//        G = currentSelRedNums[5];
//        H = currentSelRedNums[6];
//        I = currentSelRedNums[7];
//        J = currentSelRedNums[8];
//        K = currentSelRedNums[9];
//        L = currentSelRedNums[10];
//        M = currentSelRedNums[11];
//      } else {
//        A = currentSelRedNums[0];
//        B = currentSelRedNums[1];
//        C = currentSelRedNums[2];
//        D = currentSelRedNums[3];
//        E = currentSelRedNums[4];
//        F = currentSelRedNums[5];
//        G = currentSelRedNums[6];
//        H = currentSelRedNums[7];
//        I = currentSelRedNums[8];
//        J = currentSelRedNums[9];
//        K = currentSelRedNums[10];
//        L = currentSelRedNums[11];
//        M = currentSelRedNums[12];
//      }
//
//      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[61];
//      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M;
//      int codeNum = 13;
//      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
//        return -1;
//      }
//      
//      for (int i = 0; i < 61; i++) {
//        allCombinedCodes.add(pBaseCode[i]);
//      }
//    
//    } 
    else if (modelNum == SmartCombineActivity.M_13_S_6_G_4_10_ITEM) {//13码中6保4共10注
      int A, B, C, D, E, F, G, H, I, J, K, L, M;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
        M = currentSelRedNums[11];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
        M = currentSelRedNums[12];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[10];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M;
      int codeNum = 13;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
    
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, D, H, J, M, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, E, F, H, L, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, C, E, I, J, K, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, D, F, G, L, M, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(B, C, F, H, I, K, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(B, C, H, I, K, L, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(B, F, G, H, J, L, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(C, D, E, G, I, K, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(C, E, G, I, K, M, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(D, E, F, J, L, M, nextID, blue);
      
      for (int i = 0; i < 10; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } else if (modelNum == SmartCombineActivity.M_14_S_6_G_4_14_ITEM) {//14码中6保4共14注
      int A, B, C, D, E, F, G, H, I, J, K, L, M, N;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
        M = currentSelRedNums[11];
        N = currentSelRedNums[12];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
        M = currentSelRedNums[12];
        N = currentSelRedNums[13];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[14];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M + N;
      int codeNum = 14;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, F, I, L, N, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, G, J, L, M, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, C, D, G, K, L, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, C, F, H, J, M, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, D, E, H, I, J, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(A, E, G, H, K, N, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(B, C, E, I, K, M, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(B, D, J, K, M, N, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(B, E, F, H, K, L, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(C, D, E, J, L, N, nextID, blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(C, H, I, L, M, N, nextID, blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(D, E, F, G, I, M, nextID, blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(F, G, I, J, K, N, nextID, blue);

      for (int i = 0; i < 14; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } else if (modelNum == SmartCombineActivity.M_16_S_6_G_4_25_ITEM) {//16码中6保4共25注
      int A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
        M = currentSelRedNums[11];
        N = currentSelRedNums[12];
        O = currentSelRedNums[13];
        P = currentSelRedNums[14];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
        M = currentSelRedNums[12];
        N = currentSelRedNums[13];
        O = currentSelRedNums[14];
        P = currentSelRedNums[15];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[25];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M + N + O + P;
      int codeNum = 16;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
    
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, F, I, K, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, D, E, G, M, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, J, N, O, P, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, C, E, K, L, M, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, C, H, J, K, N, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(A, D, F, G, H, I, nextID, blue); 
      pBaseCode[6] = ShuangseCodeItem.instanceBy(A, D, G, L, O, P, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(A, E, H, M, O, P, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(A, F, I, J, L, N, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(B, C, E, H, K, M, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(B, C, K, L, O, P, nextID, blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(B, D, G, H, O, P, nextID, blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(B, D, G, J, L, N, nextID, blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(B, E, F, I, L, M, nextID, blue);
      pBaseCode[14] = ShuangseCodeItem.instanceBy(B, F, H, I, J, N, nextID, blue);
      pBaseCode[15] = ShuangseCodeItem.instanceBy(C, D, E, F, N, O, nextID, blue);
      pBaseCode[16] = ShuangseCodeItem.instanceBy(C, D, G, H, K, L, nextID, blue);
      pBaseCode[17] = ShuangseCodeItem.instanceBy(C, E, G, I, J, P, nextID, blue);
      pBaseCode[18] = ShuangseCodeItem.instanceBy(C, G, I, J, M, O, nextID, blue);
      pBaseCode[19] = ShuangseCodeItem.instanceBy(D, F, J, K, M, P, nextID, blue);
      pBaseCode[20] = ShuangseCodeItem.instanceBy(D, I, K, M, N, P, nextID, blue);
      pBaseCode[21] = ShuangseCodeItem.instanceBy(E, G, I, J, K, O, nextID, blue);
      pBaseCode[22] = ShuangseCodeItem.instanceBy(E, H, J, L, M, N, nextID, blue);
      pBaseCode[23] = ShuangseCodeItem.instanceBy(F, G, K, M, N, P, nextID, blue);
      pBaseCode[24] = ShuangseCodeItem.instanceBy(F, H, I, L, O, P, nextID, blue);
      
      for (int i = 0; i < 25; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    }
//    else if (modelNum == SmartCombineActivity.M_18_S_6_G_4_42_ITEM) {//18码中6保5共42注
//      int A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R;
//      if (sureR > 0) {
//        A = sureR;
//        B = currentSelRedNums[0];
//        C = currentSelRedNums[1];
//        D = currentSelRedNums[2];
//        E = currentSelRedNums[3];
//        F = currentSelRedNums[4];
//        G = currentSelRedNums[5];
//        H = currentSelRedNums[6];
//        I = currentSelRedNums[7];
//        J = currentSelRedNums[8];
//        K = currentSelRedNums[9];
//        L = currentSelRedNums[10];
//        M = currentSelRedNums[11];
//        N = currentSelRedNums[12];
//        O = currentSelRedNums[13];
//        P = currentSelRedNums[14];
//        Q = currentSelRedNums[15];
//        R = currentSelRedNums[16];
//      } else {
//        A = currentSelRedNums[0];
//        B = currentSelRedNums[1];
//        C = currentSelRedNums[2];
//        D = currentSelRedNums[3];
//        E = currentSelRedNums[4];
//        F = currentSelRedNums[5];
//        G = currentSelRedNums[6];
//        H = currentSelRedNums[7];
//        I = currentSelRedNums[8];
//        J = currentSelRedNums[9];
//        K = currentSelRedNums[10];
//        L = currentSelRedNums[11];
//        M = currentSelRedNums[12];
//        N = currentSelRedNums[13];
//        O = currentSelRedNums[14];
//        P = currentSelRedNums[15];
//        Q = currentSelRedNums[16];
//        R = currentSelRedNums[17];
//      }
//
//      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[42];
//      int sum = A + B + C + D + E + F + G + H + I + J + K + L + M + N + O + P + Q + R;
//      int codeNum = 18;
//      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
//        return -1;
//      }    
//      
//      for (int i = 0; i < 42; i++) {
//        allCombinedCodes.add(pBaseCode[i]);
//      }
//    } 
    else if (modelNum == SmartCombineActivity.M_9_S_5_G_4_3_ITEM) {//9码中5保4共3注
      int A, B, C, D, E, F, G, H, I;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[3];
      int sum = A + B + C + D + E + F + G + H + I;
      int codeNum = 9;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
        return -1;
      }

      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, E, G, I, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, D, E, F, H, I, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(B, C, D, F, G, H, nextID, blue);

      for (int i = 0; i < 3; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } else if (modelNum == SmartCombineActivity.M_10_S_5_G_4_7_ITEM) {//10码中5保4共7注
      int A, B, C, D, E, F, G, H, I, J;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[7];
      int sum = A + B + C + D + E + F + G + H + I + J;
      int codeNum = 10;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
        return -1;
      }
    
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, E, H, I, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, G, H, I, nextID, blue); 
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, D, E, F, G, J, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(B, C, D, F, H, I, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(B, C, D, H, I, J, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(B, C, E, G, H, I, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(B, C, F, H, I, J, nextID, blue);
      
      for (int i = 0; i < 7; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } else if (modelNum == SmartCombineActivity.M_11_S_5_G_4_10_ITEM) {//11码中5保4共10注
      
      int A, B, C, D, E, F, G, H, I, J, K;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[10];
      int sum = A + B + C + D + E + F + G + H + I + J + K;
      int codeNum = 11;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
        return -1;
      }
      
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, D, E, G, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, E, G, H, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, E, F, I, K, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, C, D, E, F, G, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, C, E, F, G, H, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(A, C, E, G, I, J, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(A, D, E, H, J, K, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(B, C, F, G, J, K, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(B, D, F, H, I, J, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(C, D, G, H, I, K, nextID, blue);
      
      for (int i = 0; i < 10; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    } else if (modelNum == SmartCombineActivity.M_12_S_5_G_4_14_ITEM) {//12码中5保4共14注
      int A, B, C, D, E, F, G, H, I, J, K, L;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
        K = currentSelRedNums[9];
        L = currentSelRedNums[10];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
        K = currentSelRedNums[10];
        L = currentSelRedNums[11];
      }

      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[14];
      int sum = A + B + C + D + E + F + G + H + I + J + K + L;
      int codeNum = 12;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
      
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, D, E, F, J, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, E, G, H, L, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, H, I, J, K, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, C, D, I, J, L, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, C, E, H, J, K, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(A, C, F, G, H, I, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(A, D, F, G, K, L, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(B, C, D, E, G, I, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(B, C, D, F, H, K, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(B, C, E, F, I, L, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(B, C, G, J, K, L, nextID, blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(D, E, H, I, K, L, nextID, blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(D, F, G, H, J, L, nextID, blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(E, F, G, I, J, K, nextID, blue);
      
      for (int i = 0; i < 14; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    
    } else if (modelNum == SmartCombineActivity.M_7_S_4_G_4_5_ITEM) {//7码中4保4共5注
      int A, B, C, D, E, F, G;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[5];

      int sum = A + B + C + D + E + F + G;
      int codeNum = 7;
      if (ifCheckSum && (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
    
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, D, E, F, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, D, E, G, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, C, D, F, G, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, B, D, E, F, G, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, C, D, E, F, G, nextID, blue);
      
      for (int i = 0; i < 5; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } else if (modelNum == SmartCombineActivity.M_8_S_4_G_4_7_ITEM) {//8码中4保4共7注
      int A, B, C, D, E, F, G, H;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[7];

      int sum = A + B + C + D + E + F + G + H;
      int codeNum = 8;
      if (ifCheckSum && (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
        return -1;
      }
      
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, D, E, F, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, E, G, H, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, D, F, G, H, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, C, D, E, F, G, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, C, D, E, F, H, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(B, C, D, F, G, H, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(B, D, E, F, G, H, nextID, blue);
      
      for (int i = 0; i < 7; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
    
    } else if (modelNum == SmartCombineActivity.M_10_S_4_G_4_20_ITEM) {//10码中4保4共20注
      int A, B, C, D, E, F, G, H, I, J;
      if (sureR > 0) {
        A = sureR;
        B = currentSelRedNums[0];
        C = currentSelRedNums[1];
        D = currentSelRedNums[2];
        E = currentSelRedNums[3];
        F = currentSelRedNums[4];
        G = currentSelRedNums[5];
        H = currentSelRedNums[6];
        I = currentSelRedNums[7];
        J = currentSelRedNums[8];
      } else {
        A = currentSelRedNums[0];
        B = currentSelRedNums[1];
        C = currentSelRedNums[2];
        D = currentSelRedNums[3];
        E = currentSelRedNums[4];
        F = currentSelRedNums[5];
        G = currentSelRedNums[6];
        H = currentSelRedNums[7];
        I = currentSelRedNums[8];
        J = currentSelRedNums[9];
      }
      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[20];
      int sum = A + B + C + D + E + F + G + H + I + J;
      int codeNum = 10;
      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
        return -1;
      }
      
      pBaseCode[0] = ShuangseCodeItem.instanceBy(A, B, C, D, H, I, nextID, blue);
      pBaseCode[1] = ShuangseCodeItem.instanceBy(A, B, C, E, F, G, nextID, blue);
      pBaseCode[2] = ShuangseCodeItem.instanceBy(A, B, C, E, I, J, nextID, blue);
      pBaseCode[3] = ShuangseCodeItem.instanceBy(A, B, D, E, H, J, nextID, blue);
      pBaseCode[4] = ShuangseCodeItem.instanceBy(A, B, D, F, G, H, nextID, blue);
      pBaseCode[5] = ShuangseCodeItem.instanceBy(A, B, F, G, I, J, nextID, blue);
      pBaseCode[6] = ShuangseCodeItem.instanceBy(A, C, D, E, F, J, nextID, blue);
      pBaseCode[7] = ShuangseCodeItem.instanceBy(A, C, D, E, G, H, nextID, blue);
      pBaseCode[8] = ShuangseCodeItem.instanceBy(A, C, E, F, H, I, nextID, blue);
      pBaseCode[9] = ShuangseCodeItem.instanceBy(A, C, G, H, I, J, nextID, blue);
      pBaseCode[10] = ShuangseCodeItem.instanceBy(A, D, E, G, I, J, nextID, blue);
      pBaseCode[11] = ShuangseCodeItem.instanceBy(A, D, F, H, I, J, nextID, blue);
      pBaseCode[12] = ShuangseCodeItem.instanceBy(B, C, D, E, G, I, nextID, blue);
      pBaseCode[13] = ShuangseCodeItem.instanceBy(B, C, D, F, I, J, nextID, blue);
      pBaseCode[14] = ShuangseCodeItem.instanceBy(B, C, E, G, H, J, nextID, blue);
      pBaseCode[15] = ShuangseCodeItem.instanceBy(B, C, F, G, H, I, nextID, blue);
      pBaseCode[16] = ShuangseCodeItem.instanceBy(B, D, E, F, G, J, nextID, blue);
      pBaseCode[17] = ShuangseCodeItem.instanceBy(B, E, F, H, I, J, nextID, blue);
      pBaseCode[18] = ShuangseCodeItem.instanceBy(C, D, F, G, H, J, nextID, blue);
      pBaseCode[19] = ShuangseCodeItem.instanceBy(D, E, F, G, H, I, nextID, blue);
      
      for (int i = 0; i < 20; i++) {
        allCombinedCodes.add(pBaseCode[i]);
      }
      
    } 
//    else if (modelNum == SmartCombineActivity.M_11_S_4_G_4_32_ITEM) {//11码中4保4共32注
//      int A, B, C, D, E, F, G, H, I, J, K;
//      if (sureR > 0) {
//        A = sureR;
//        B = currentSelRedNums[0];
//        C = currentSelRedNums[1];
//        D = currentSelRedNums[2];
//        E = currentSelRedNums[3];
//        F = currentSelRedNums[4];
//        G = currentSelRedNums[5];
//        H = currentSelRedNums[6];
//        I = currentSelRedNums[7];
//        J = currentSelRedNums[8];
//        K = currentSelRedNums[9];
//      } else {
//        A = currentSelRedNums[0];
//        B = currentSelRedNums[1];
//        C = currentSelRedNums[2];
//        D = currentSelRedNums[3];
//        E = currentSelRedNums[4];
//        F = currentSelRedNums[5];
//        G = currentSelRedNums[6];
//        H = currentSelRedNums[7];
//        I = currentSelRedNums[8];
//        J = currentSelRedNums[9];
//        K = currentSelRedNums[10];
//      }
//      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[32];
//      int sum = A + B + C + D + E + F + G + H + I + J + K;
//      int codeNum = 11;
//      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum))) ) {
//        return -1;
//      }
//    
//      for (int i = 0; i < 32; i++) {
//        allCombinedCodes.add(pBaseCode[i]);
//      }
//    } else if (modelNum == SmartCombineActivity.M_12_S_4_G_4_41_ITEM) {//12码中4保4共41注
//      int A, B, C, D, E, F, G, H, I, J, K, L;
//      if (sureR > 0) {
//        A = sureR;
//        B = currentSelRedNums[0];
//        C = currentSelRedNums[1];
//        D = currentSelRedNums[2];
//        E = currentSelRedNums[3];
//        F = currentSelRedNums[4];
//        G = currentSelRedNums[5];
//        H = currentSelRedNums[6];
//        I = currentSelRedNums[7];
//        J = currentSelRedNums[8];
//        K = currentSelRedNums[9];
//        L = currentSelRedNums[10];
//      } else {
//        A = currentSelRedNums[0];
//        B = currentSelRedNums[1];
//        C = currentSelRedNums[2];
//        D = currentSelRedNums[3];
//        E = currentSelRedNums[4];
//        F = currentSelRedNums[5];
//        G = currentSelRedNums[6];
//        H = currentSelRedNums[7];
//        I = currentSelRedNums[8];
//        J = currentSelRedNums[9];
//        K = currentSelRedNums[10];
//        L = currentSelRedNums[11];
//      }
//
//      ShuangseCodeItem[] pBaseCode = new ShuangseCodeItem[41];
//      int sum = A + B + C + D + E + F + G + H + I + J + K + L;
//      int codeNum = 12;
//      if(ifCheckSum &&  (!((sum >= 13 * codeNum) && (sum <= 21 * codeNum)))) {
//        return -1;
//      }
//    
//      for (int i = 0; i < 41; i++) {
//        allCombinedCodes.add(pBaseCode[i]);
//      }
//    } 
    else {
      return -2;
    }

    // 胆码判断
    if (sureR > 0) {
      for (Iterator<ShuangseCodeItem> it = allCombinedCodes.iterator(); it.hasNext();) {
        ShuangseCodeItem item = it.next();
        if (!item.containsRedNum(sureR)) {// 删除不含有胆码的号码
          it.remove();
        }
      }
    }

    // 删除历史号码
    if (ifGetOutHistoryCode) {
      for (Iterator<ShuangseCodeItem> it = allCombinedCodes.iterator(); it.hasNext();) {
        ShuangseCodeItem item = it.next();
        if (ifItemIsHistoryItem(item.red)) {// 删除历史号码
          it.remove();
        }
      }
    }

    // 组号码完成，结果放入allCombinedCodes中
    return 1;
  }
  
  /** insert a combine record into localsave DB
   * 保存时只保存选择的红，蓝 (以空格间隔) 和 组号模式
   * 在历史查询时根据此重新组号并检查中奖结果 
   * */
public  void insertMyCombineRecord(int itemid, List<Integer> selectRed, 
    List<Integer> selectBlue, int model, int getoutHisFlag, ArrayList<ShuangseCodeItem> resultCodes) {
  
    StringBuffer rsb = new StringBuffer();
    StringBuffer bsb = new StringBuffer();
    for(Integer r : selectRed) {
      rsb.append(r.toString()).append(" ");
    }
    for(Integer b : selectBlue) {
      bsb.append(b.toString()).append(" ");
    }
    
    this.insertMyCombineHisRecord(rsb, bsb, itemid, model, getoutHisFlag, resultCodes);
  }

/**
 * 胆拖组号时保存胆码和拖码"01 02 03 #06 11 12 13 21 22 23"格式
 * 胆码和拖码之间用#分开 
 * insert a combine record into local save DB
 * 保存时只保存选择的红，蓝 (以空格间隔) 和 组号模式
 * 在历史查询时根据此重新组号并检查中奖结果 
 * */
public  void insertMyDanTuoCombineRecord(int itemid, List<Integer> selectDanRed, List<Integer> selectTuoRed, 
    List<Integer> selectBlue, int model, int getoutHisFlag, ArrayList<ShuangseCodeItem> resultCodes) {
  
  StringBuffer rsb = new StringBuffer();
  StringBuffer bsb = new StringBuffer();
  for(Integer r : selectDanRed) {
    rsb.append(r.toString()).append(" ");
  }
  rsb.append("#");
  for(Integer r : selectTuoRed) {
    rsb.append(r.toString()).append(" ");
  }
  
  for(Integer b : selectBlue) {
    bsb.append(b.toString()).append(" ");
  } 
  
  this.insertMyCombineHisRecord(rsb, bsb, itemid, model, getoutHisFlag, resultCodes);
}

/*Perform the real insert transaction*/
private void insertMyCombineHisRecord(StringBuffer rsb, StringBuffer bsb, int itemid, 
    int model, int getoutHisFlag, ArrayList<ShuangseCodeItem> resultCodes) {
  
  SQLiteDatabase db = this.dbHelper.getWritableDatabase();
  db.beginTransaction();
  try {
    //Get the old record Index if it have
    String sql = "select recordIndex from mycombinehis where itemId=? and selectRed=? and selectBlue=? and model=? and getoutHisFlag=? limit 1";
    Cursor cursor = null;
    try {
      cursor = db.rawQuery(sql, new String[] {String.valueOf(itemid), rsb.toString(), bsb.toString(), String.valueOf(model), String.valueOf(getoutHisFlag)});
    }catch(Throwable e) {
      Log.w(TAG, e.toString());
      //continue
    }
    
    int oldRecordIndex = -1;
    
    if(cursor != null && cursor.moveToNext()) {
      oldRecordIndex = cursor.getInt(cursor.getColumnIndex("recordIndex"));
      cursor.close();
    }
    if(oldRecordIndex != -1) {
      //Delete same record first
      db.delete("mycombinehis", "recordIndex=?", new String[] {String.valueOf(oldRecordIndex)});
      db.delete("combineitems", "recordIndex=?", new String[] {String.valueOf(oldRecordIndex)});
    }
    
    //插入新值
    ContentValues newEntry = new ContentValues();
    //插入表mycombinehis
    newEntry.put("itemid", itemid);
    newEntry.put("selectRed", rsb.toString());
    newEntry.put("selectBlue", bsb.toString());
    newEntry.put("model", model);
    newEntry.put("getoutHisFlag", getoutHisFlag);
    db.insertOrThrow("mycombinehis", null, newEntry);
    
    //获取新的RecordIndex
    String sql2 = "select recordIndex from mycombinehis where itemId=? and selectRed=? and selectBlue=? and model=? and getoutHisFlag=? limit 1";
    Cursor cursor2 = null;
    try {
      cursor2 = db.rawQuery(sql2, new String[] {String.valueOf(itemid), rsb.toString(), bsb.toString(), String.valueOf(model), String.valueOf(getoutHisFlag)});
    }catch(Throwable e) {
      Log.w(TAG, e.toString());
      //continue to fall through
    }
    
    int recordIndex = -1;
    
    if(cursor2 != null && cursor2.moveToNext()) {
      recordIndex = cursor2.getInt(cursor2.getColumnIndex("recordIndex"));
      cursor2.close();
    }
    
    if(recordIndex != -1) {
        //插入表combineitems
      for(ShuangseCodeItem itm : resultCodes) {
        ContentValues newEntryItem = new ContentValues();
        
        newEntryItem.put("red1", itm.red[0]);
        newEntryItem.put("red2", itm.red[1]);
        newEntryItem.put("red3", itm.red[2]);
        newEntryItem.put("red4", itm.red[3]);
        newEntryItem.put("red5", itm.red[4]);
        newEntryItem.put("red6", itm.red[5]);
        newEntryItem.put("blue", itm.blue);
        newEntryItem.put("recordIndex", recordIndex);
        try {
          db.insertOrThrow("combineitems", null, newEntryItem);
        }catch(Throwable e) {Log.w(TAG, e.toString());};
      }
    }
    
    db.setTransactionSuccessful();
  } catch (Exception e)  {
      e.printStackTrace();
      Log.e(TAG, "Exception when insert:" + e.toString());
  } finally {
    db.endTransaction();
  }
  Log.d(TAG, "insert successfully");
}

/**删除一条组号的历史记录*/
public  void deleteOneRecord(MyHisRecord currentSelItem) {
  SQLiteDatabase db = this.dbHelper.getWritableDatabase();
  
  StringBuffer rsb = new StringBuffer();
  StringBuffer bsb = new StringBuffer();
  if(currentSelItem.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
    for(Integer r : currentSelItem.getRedDanNumbers()) {
      rsb.append(r.toString()).append(" ");
    }
    rsb.append("#");
    for(Integer r : currentSelItem.getRedTuoNumbers()) {
      rsb.append(r.toString()).append(" ");
    }
  } else {
    for(Integer r : currentSelItem.getRedNumbers()) {
      rsb.append(r.toString()).append(" ");
    }
  }
  
  for(Integer b : currentSelItem.getBlueNumbers()) {
    bsb.append(b.toString()).append(" ");
  }
  
  db.beginTransaction();
  try {
    //Delete same record first
    db.delete("mycombinehis", "itemId=? and selectRed=? and selectBlue=? and model=? and getoutHisFlag=?",
        new String[] {String.valueOf(currentSelItem.getItemId()), rsb.toString(), bsb.toString(), 
        String.valueOf(currentSelItem.getSelectModel()), String.valueOf(currentSelItem.getGetoutHisFlag())});
    
    db.setTransactionSuccessful();
  } catch (Exception e)  {
      e.printStackTrace();
      Log.e(TAG, "Exception when delete record:" + e.toString());
  } finally {
    db.endTransaction();
  }
  Log.d(TAG, "delete successfully");
}

/**根据ItemIndex删除一条组号的历史记录*/
public  void deleteOneRecordByIndex(int itemIndex) {
  SQLiteDatabase db = this.dbHelper.getWritableDatabase();
  
  db.beginTransaction();
  try {
    //Delete same record first
    db.delete("mycombinehis", "recordIndex=?", new String[] {String.valueOf(itemIndex)});
    db.setTransactionSuccessful();
  } catch (Exception e)  {
      e.printStackTrace();
      Log.e(TAG, "Exception when  deleteOneRecordByIndex:" + e.toString());
  } finally {
    db.endTransaction();
  }
  Log.d(TAG, "delete successfully");
}


/** 选出某一个组号记录的Index, 根据记录的内容 */
public  int selectMyCombineRecordIndexByContent(int itemid, List<Integer> selectDanRed, List<Integer> selectTuoRed, 
    List<Integer> selectBlue, int model, int getoutHisFlag) {
  SQLiteDatabase db = this.dbHelper.getReadableDatabase();
  StringBuffer rsb = new StringBuffer();
  StringBuffer bsb = new StringBuffer();
  for(Integer r : selectDanRed) {
    rsb.append(r.toString()).append(" ");
  }
  rsb.append("#");
  for(Integer r : selectTuoRed) {
    rsb.append(r.toString()).append(" ");
  }
  
  for(Integer b : selectBlue) {
    bsb.append(b.toString()).append(" ");
  }
  
  String sql = "select recordIndex from mycombinehis where itemId=? and selectRed=? and selectBlue=? and model=? and getoutHisFlag=? limit 1";
  Cursor cursor = db.rawQuery(sql, new String[] {String.valueOf(itemid), rsb.toString(), bsb.toString(), String.valueOf(model), String.valueOf(getoutHisFlag)});
  int recordIndex = -1;
  
  if(cursor != null && cursor.moveToNext()) {
    recordIndex = cursor.getInt(cursor.getColumnIndex("recordIndex"));
    cursor.close();
  }
  return recordIndex;
}


/**选出所有的组号历史记录*/
public  ArrayList<MyHisRecord> selectAllMyCombineRecord() {
  ArrayList<MyHisRecord> myHis = new ArrayList<MyHisRecord>();
  
  SQLiteDatabase db = this.dbHelper.getReadableDatabase();
  String sql = "select * from mycombinehis where itemId !=0 order by itemId desc";

  Cursor cursor = db.rawQuery(sql, null);
  
  while(cursor != null && cursor.moveToNext()) {
    MyHisRecord item = new MyHisRecord();
    int recordIndexIndex = cursor.getColumnIndex("recordIndex");
    if(recordIndexIndex == -1) {
        item.setRecordIndex(-1);
    } else {
        item.setRecordIndex(cursor.getInt(recordIndexIndex));
    }
    item.setItemId(cursor.getInt(cursor.getColumnIndex("itemId")));
    item.setSelectModel(cursor.getInt(cursor.getColumnIndex("model")));
    
    String redStr = cursor.getString(cursor.getColumnIndex("selectRed"));
    
    if(item.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
        //抽出胆部分和拖部分    
        String[] allRedNumStrs = redStr.split("#");
        String redDanNumberStr = allRedNumStrs[0];
        String[] redDanNumberStrs = redDanNumberStr.split(" ");
        String redTuoNumberStr = allRedNumStrs[1];
        String[] redTuoNumberStrs = redTuoNumberStr.split(" ");
        
        ArrayList<Integer> redDanNumbers = new ArrayList<Integer>();
        ArrayList<Integer> redTuoNumbers = new ArrayList<Integer>();
        
        for(String redstr : redDanNumberStrs) {
          redDanNumbers.add(Integer.parseInt(redstr));
        }
        item.setRedDanNumbers(redDanNumbers);
        for(String redstr : redTuoNumberStrs) {
          redTuoNumbers.add(Integer.parseInt(redstr));
        }
        item.setRedTuoNumbers(redTuoNumbers);
        
    } else {
          
          String[] redNumberStrs = redStr.split(" ");
          
          ArrayList<Integer> redNumbers = new ArrayList<Integer>();
          for(String redstr : redNumberStrs) {
            redNumbers.add(Integer.parseInt(redstr));
          }
          item.setRedNumbers(redNumbers);
          
    }
    
    String blueStr = cursor.getString(cursor.getColumnIndex("selectBlue"));
    String[] blueNumberStrs = blueStr.split(" ");
    ArrayList<Integer> blueNumbers = new ArrayList<Integer>();
    for(String bluestr : blueNumberStrs) {
      blueNumbers.add(Integer.parseInt(bluestr));
    }
    
    item.setBlueNumbers(blueNumbers);
    item.setGetoutHisFlag(cursor.getInt(cursor.getColumnIndex("getoutHisFlag")));
    
    myHis.add(item);
  }
  cursor.close();
  
  return myHis;
}

public String countHistoryOutDetailsForRedset(ArrayList<Integer> selectedRedNumbers) {
  StringBuffer sb = new StringBuffer();
  int cnt = allHisData.size();
  //统计所有历史
  sb.append(this.countHistoryOutDetailsForRedset(selectedRedNumbers, cnt));
  sb.append("++++++++++++++++\n");
  sb.append(this.countHistoryOutDetailsForRedset(selectedRedNumbers, 1000));
  sb.append("++++++++++++++++\n");
  sb.append(this.countHistoryOutDetailsForRedset(selectedRedNumbers, 500));
  sb.append("++++++++++++++++\n");
  sb.append(this.countHistoryOutDetailsForRedset(selectedRedNumbers, 300));
  sb.append("++++++++++++++++\n");
  sb.append(this.countHistoryOutDetailsForRedset(selectedRedNumbers, 100));
  sb.append("++++++++++++++++\n");
  sb.append(this.countHistoryOutDetailsForRedset(selectedRedNumbers, 50));
  sb.append("++++++++++++++++\n");
  return sb.toString();
}

/*统计一组红球在近lastCnt期出号情况*/
public String countHistoryOutDetailsForRedset(ArrayList<Integer> selectedRedNumbers, int lastCnt) {
  StringBuffer sb = new StringBuffer();
  int cnt = allHisData.size();
  if(cnt == lastCnt) {
          sb.append("该组").append(selectedRedNumbers.size()).append("码红球在历史共")
                                 .append(getAllHisData().size()).append("期的出号情况如下：\n");
  } else {
    sb.append("该组").append(selectedRedNumbers.size()).append("码红球在最近")
    .append(lastCnt).append("期的出号情况如下：\n");    
  }
  
  int[] redOutCountCounter = new int[7];
  for(int y=0;y<7;y++) redOutCountCounter[y] = 0;
  HashSet<Integer> tmpSet = new HashSet<Integer>();
  int startPos = cnt - lastCnt;
  int endPos = (cnt - 1);
  for (int i = startPos; i <= endPos; i++) {
    ShuangseCodeItem tmpCodeItem = (ShuangseCodeItem) allHisData.get(i);
    HashSet<Integer> redSet = tmpCodeItem.getRedNumberSet();
    tmpSet.clear();
    tmpSet.addAll(selectedRedNumbers);
    tmpSet.retainAll(redSet);
    
    redOutCountCounter[tmpSet.size()]++;
  }
  for(int y=6;y>=0;y--) {
    sb.append("\t\t出现").append(y).append("码的有").append(redOutCountCounter[y]).append("期;\n");
  }
  return sb.toString();
}

//统计2码同出情况
//返回排过序的 01_02  --> 12
//                      11_12   ->  14 ......数组
public List<Map.Entry<String, Integer>> countTwoRedNumberOccurs() {
  Map<String, Integer> twoCodeMap = new HashMap<String, Integer>();
  
  for (ShuangseCodeItem tmpCodeItem : allHisData) {
    String[] itemTwoCodesArray = tmpCodeItem.getTwoCodesCombineArray();

    for(String twoCodesItem : itemTwoCodesArray) {
      if(twoCodeMap.containsKey(twoCodesItem)) {
        Integer val = twoCodeMap.get(twoCodesItem);
        val++;
        twoCodeMap.put(twoCodesItem, val);
      } else {
        twoCodeMap.put(twoCodesItem, 1);
      }
    }//end for
  }//end for
  int[] a = new int[33];
  for(int aIndx=0; aIndx < 33; aIndx++) {
    a[aIndx] = (aIndx+1);
  }
  
  Vector<BaseCodeItem> allCodesResult = new Vector<BaseCodeItem>();  
  //增加上遗漏未出的组合
  MagicTool.combine(a, 33, 2, allCodesResult);
  for(BaseCodeItem itm : allCodesResult) {
    StringBuilder itemKey = new StringBuilder();
    if(itm.num[0] < 10) {
      itemKey.append(0);
    }
    itemKey.append(itm.num[0]);
    itemKey.append("_");
    if(itm.num[1] < 10) {
      itemKey.append(0);
    }
    itemKey.append(itm.num[1]);
    
    if(!twoCodeMap.containsKey(itemKey.toString())) {
      twoCodeMap.put(itemKey.toString(), 0);
    }
  }
    //排序
    List<Map.Entry<String, Integer>> valueList = new Vector<Map.Entry<String, Integer>>( twoCodeMap.entrySet());
    Collections.sort(valueList, new Comparator<Map.Entry<String, Integer>>(){  
      public int compare(Map.Entry<String, Integer> entry, Map.Entry<String, Integer> entry1)  
      {
        return entry.getValue().compareTo(entry1.getValue());  
      }
    });
  
    return valueList;
}

//统计三码同出情况
public List<Map.Entry<String, Integer>> countThreeRedNumberOccurs() {
  Map<String, Integer> threeCodeMap = new HashMap<String, Integer>();
  
  for (ShuangseCodeItem tmpCodeItem : allHisData) {
    String[] itemTwoCodesArray = tmpCodeItem.getThreeCodesCombineArray();

    for(String threeCodesItem : itemTwoCodesArray) {
      if(threeCodeMap.containsKey(threeCodesItem)) {
        Integer val = threeCodeMap.get(threeCodesItem);
        val++;
        threeCodeMap.put(threeCodesItem, val);
      } else {
        threeCodeMap.put(threeCodesItem, 1);
      }
    }//end for
  }//end for 

  int[] a = new int[33];
  for(int aIndx=0; aIndx < 33; aIndx++) {
    a[aIndx] = (aIndx+1);
  }
  Vector<BaseCodeItem> allCodesResult = new Vector<BaseCodeItem>();  
  MagicTool.combine(a, 33, 3, allCodesResult);
  for(BaseCodeItem itm : allCodesResult) {
    StringBuilder itemKey = new StringBuilder();
    if(itm.num[0] < 10) {
      itemKey.append(0);
    }
    itemKey.append(itm.num[0]);
    itemKey.append("_");
    if(itm.num[1] < 10) {
      itemKey.append(0);
    }
    itemKey.append(itm.num[1]);
    itemKey.append("_");
    if(itm.num[2] < 10) {
      itemKey.append(0);
    }
    itemKey.append(itm.num[2]);
    
    if(!threeCodeMap.containsKey(itemKey.toString())) {
      threeCodeMap.put(itemKey.toString(), 0);
    }
  }
  
    //排序
    List<Map.Entry<String, Integer>> valueList = new Vector<Map.Entry<String, Integer>>( threeCodeMap.entrySet());
    Collections.sort(valueList, new Comparator<Map.Entry<String, Integer>>(){  
      public int compare(Map.Entry<String, Integer> entry, Map.Entry<String, Integer> entry1)  
      {
        return entry.getValue().compareTo(entry1.getValue());  
      }
    });
    
    return valueList;
}

/**
 * 统计红球1-33 在近 size期出号次数
 * @param size
 * @return
 */
public ValueObj[] countRedNumberOccuresCount(int size) {
  ValueObj[] redOccur = new ValueObj[33];
  for(int i=0;i<33;i++) {
    redOccur[i] = new ValueObj();
    redOccur[i].val = (i+1);
  }
  int totalSize = allHisData.size();
  int beginIndex = (totalSize - size);
  for (int loc = (totalSize - 1); loc >= beginIndex; loc--) {
    ShuangseCodeItem tmpCodeItem = allHisData.get(loc);
    redOccur[tmpCodeItem.red[0] - 1].cnt++;
    redOccur[tmpCodeItem.red[1] - 1].cnt++;
    redOccur[tmpCodeItem.red[2] - 1].cnt++;
    redOccur[tmpCodeItem.red[3] - 1].cnt++;
    redOccur[tmpCodeItem.red[4] - 1].cnt++;
    redOccur[tmpCodeItem.red[5] - 1].cnt++;
  }
  
  return redOccur;
}

/**Hook to do various stat 在EntryActivity 的InitialLoadingTask.doInbackground()中调用，只做Debug用*/
public void statHistoryData() {
   //statHotAndWarmSetOccursData();
  //statJiangEnSetOccursData();
}

/**胆拖组号*/
public int combineCodes(int[] a, int n,
    boolean ifGetOutOfHistoryitem, int[] b, int bLen, int m, 
    int currentCombineItemId, int blue,
    ArrayList<ShuangseCodeItem> oneBlueCombinedCodes) {
  
  oneBlueCombinedCodes.clear();
  
  int cnt = 0;
  int id = currentCombineItemId;
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
          //如果不包含胆码，跳过该注号码
          if(baseCode.containsRedNums(b, bLen)) {
            oneBlueCombinedCodes.add(baseCode);
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
 * Get Recommend Blue for ItemIndex
 * @param itemIndex
 * @return
 */
public HashSet<Integer> getRecommendBlueNumbers(int itemIndex) {
    HashSet<Integer> blueSet = new HashSet<Integer>();
    
    int curLatestIndex = allHisData.size();
    ShuangseCodeItem lastItem = null;
    
    if(itemIndex >= curLatestIndex) {
       //预测下一期未出现的篮球
       lastItem = this.getCodeItemByIndex(curLatestIndex - 1);
    } else {
      //求历史统计
       lastItem = this.getCodeItemByIndex(itemIndex - 1);
    }
    
    //上期红球第二个加减1
    blueSet.add(lastItem.red[1] + 1);
    blueSet.add(lastItem.red[1] - 1);
    // 8 - 上期蓝号 的绝对值
    int targetVal = (8 - lastItem.blue);
    if(targetVal < 0) { 
      targetVal = targetVal * (-1);
      blueSet.add(targetVal);
    } else if(targetVal == 0) {
      blueSet.add(11);blueSet.add(15);
      blueSet.add(5);blueSet.add(2);
    } else {
      blueSet.add(targetVal);
    }
    
    //同尾蓝码
    if(lastItem.blue == 1) {
      blueSet.add(11);
    } else if(lastItem.blue == 2) {
      blueSet.add(12);
    } else if(lastItem.blue == 3) {
      blueSet.add(13);
    }else if(lastItem.blue == 4) {
      blueSet.add(14);
    }else if(lastItem.blue == 5) {
      blueSet.add(15);
    }else if(lastItem.blue == 6) {
      blueSet.add(16);
    }else if(lastItem.blue == 11) {
      blueSet.add(1);
    }else if(lastItem.blue == 12) {
      blueSet.add(2);
    }else if(lastItem.blue == 13) {
      blueSet.add(3);
    }else if(lastItem.blue == 14) {
      blueSet.add(4);
    }else if(lastItem.blue == 15) {
      blueSet.add(5);
    }else if(lastItem.blue == 16) {
      blueSet.add(6);
    }
    
    return blueSet;
}

public String getAllRecommed6Cover456Codes() {
    String query2SQL = "select loc, red1,red2,red3,red4,red5,red6 from neverout456ofsix";
    Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {});
    StringBuffer sb = new StringBuffer();
    while (cursor2.moveToNext()) {
      int locVal = cursor2.getInt(0);
      if(locVal < 10) {
        sb.append("00").append(locVal).append("=");
      }else if(locVal < 100) {
        sb.append("0").append(locVal).append("=");
      } else {
        sb.append(locVal).append("=");
      }
      
      for(int loc = 0; loc < 6; loc++) {
        int red = cursor2.getInt(loc + 1);
        if(red < 10) {
            sb.append("0").append(red).append(" ");
        } else {
          sb.append(red).append(" ");
        }
      }
      sb.append("\r\n");
    }
    cursor2.close();
    return sb.toString();
}

public String getAllRecommed11Cover56Codes() {
  String query2SQL = "select loc, red1,red2,red3,red4,red5,red6,red7,red8,red9,red10,red11 from neverout56of11";
  Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {});
  StringBuffer sb = new StringBuffer();
  while (cursor2.moveToNext()) {
    int locVal = cursor2.getInt(0);
    if(locVal < 10) {
      sb.append("0").append(locVal).append("=");
    } else {
      sb.append(locVal).append("=");
    }
    
    for(int loc = 0; loc < 11; loc++) {
      int red = cursor2.getInt(loc + 1);
      if(red < 10) {
          sb.append("0").append(red).append(" ");
      } else {
        sb.append(red).append(" ");
      }
    }
    sb.append("\r\n");
  }
  cursor2.close();
  return sb.toString();
}


public String getAllRecommed17Cover6Codes() {
  String query2SQL = "select * from neverout6of17";
  Cursor cursor2 = neverOutSqliteDB.rawQuery(query2SQL, new String[] {});
  StringBuffer sb = new StringBuffer();
  
  while (cursor2.moveToNext()) {
    int locVal = cursor2.getInt(0);
    if(locVal < 10) {
      sb.append("0").append(locVal).append("=");
    } else {
      sb.append(locVal).append("=");
    }
    
    for(int loc = 0; loc < 17; loc++) {
      int red = cursor2.getInt(loc + 1);
      if(red < 10) {
          sb.append("0").append(red).append(" ");
      } else {
        sb.append(red).append(" ");
      }
    }
    sb.append("\r\n");
  }
  cursor2.close();
  return sb.toString();
}

/**选择出本地保存的所有 组号详细记录 */
public ArrayList<ShuangseCodeItem> selectMyCombineDetailRecord(MyHisRecord itm) {
  ArrayList<ShuangseCodeItem> myHis = new ArrayList<ShuangseCodeItem>();
  
  SQLiteDatabase db = this.dbHelper.getReadableDatabase();
  String sql = "select * from combineitems where recordIndex =?";
  Cursor cursor = null;
  try {
    cursor = db.rawQuery(sql, new String[]{String.valueOf(itm.getRecordIndex())});
  }catch (Throwable e) {
    e.printStackTrace();
    return myHis;
  }
  
  while(cursor != null && cursor.moveToNext()) {
    int r1 = cursor.getInt(cursor.getColumnIndex("red1"));
    int r2 = cursor.getInt(cursor.getColumnIndex("red2"));
    int r3 = cursor.getInt(cursor.getColumnIndex("red3"));
    int r4 = cursor.getInt(cursor.getColumnIndex("red4"));
    int r5 = cursor.getInt(cursor.getColumnIndex("red5"));
    int r6 = cursor.getInt(cursor.getColumnIndex("red6"));
    int blue = cursor.getInt(cursor.getColumnIndex("blue"));
    
    ShuangseCodeItem item = new ShuangseCodeItem(itm.getItemId(), r1,r2,r3, r4, r5, r6, blue);
    
    myHis.add(item);
  }
  
  if(cursor != null) {
    cursor.close();
  }
  
  return myHis;
}

}
