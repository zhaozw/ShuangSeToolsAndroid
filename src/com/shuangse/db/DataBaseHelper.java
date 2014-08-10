package com.shuangse.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
  private final static String TAG = "DatabaseHelper";
  private final static String dbName="localsave.db";
  //dbVersion = 1
  public final static int FIRSTVERSION = 1;
  public final static int SECONDVERSION = 2;
  
  private static int currentDBVersion = 1;
  private ArrayList<CombineRecord> dbRecords;

  public DataBaseHelper(Context context) { 
    super(context, dbName, null, currentDBVersion);
    Log.d(TAG, "DataBaseHelper current DB version" + currentDBVersion);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    if(currentDBVersion == FIRSTVERSION) {
        String createtableSQL = "CREATE TABLE mycombinehis (" + 
                "recordIndex INTEGER PRIMARY KEY AUTOINCREMENT," + 
                "itemId INTEGER," +
                "selectRed varchar(100)," +
                "selectBlue varchar(50)," + 
                "model INTEGER," + 
                "getoutHisFlag INTEGER)";
          
          String createTableSql2 = "CREATE TABLE combineitems (" + 
                "red1 INTEGER," +
                "red2 INTEGER," + 
                "red3 INTEGER," +
                "red4 INTEGER," +
                "red5 INTEGER," +
                "red6 INTEGER," +
                "blue INTEGER," +
                "recordIndex INTEGER)";
          
          db.execSQL(createtableSQL);
          Log.i(TAG, "onCreate(): SQL 1:" + createtableSQL);
          db.execSQL(createTableSql2);
          Log.i(TAG, "onCreate(): SQL 2:" + createTableSql2);
    } else if(currentDBVersion == SECONDVERSION) {
        //DB 
    }
    
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.i(TAG, " onUpgrade newVersion:" + newVersion + " oldVersion:" + oldVersion);
    if(oldVersion == FIRSTVERSION && newVersion == SECONDVERSION) {
      Log.i(TAG, " start to perform database upgrade");
      
      Cursor cursor = db.rawQuery("select * from mycombinehis", new String[] {});
      dbRecords = new ArrayList<CombineRecord>();
      
      while(cursor != null && cursor.moveToNext()) {
            CombineRecord combineRecord = new CombineRecord();
            
            combineRecord.setItemID(cursor.getInt(cursor.getColumnIndex("itemId")));
            combineRecord.setSelectRed(cursor.getString(cursor.getColumnIndex("selectRed")));
            combineRecord.setSelectBlue(cursor.getString(cursor.getColumnIndex("selectBlue")));
            combineRecord.setModel(cursor.getInt(cursor.getColumnIndex("model")));
            combineRecord.setGetoutHisFlag(cursor.getInt(cursor.getColumnIndex("getoutHisFlag")));
            
            dbRecords.add(combineRecord);
      }
      db.execSQL("DROP TABLE IF EXISTS mycombinehis");
      onCreate(db);
      
      if(dbRecords != null && (dbRecords.size() > 0)) {
        for (CombineRecord item : dbRecords) {
          ContentValues newEntryItem = new ContentValues();
          newEntryItem.put("itemId", item.getItemID());
          newEntryItem.put("selectRed", item.getSelectRed());
          newEntryItem.put("selectBlue", item.getSelectBlue());
          newEntryItem.put("model", item.getModel());
          newEntryItem.put("getoutHisFlag", item.getGetoutHisFlag());
          try {
            db.insertOrThrow("mycombinehis", null, newEntryItem);
          }catch(Throwable e) {};
        }
      }
      
      Log.i(TAG, " database upgrade completed successfully.");
    }
  }
  
  private static class CombineRecord {
    private int itemID;
    private String selectRed;
    private String selectBlue;
    private int model;
    private int getoutHisFlag;
    
    public int getItemID() {
      return itemID;
    }
    public void setItemID(int itemID) {
      this.itemID = itemID;
    }
    public String getSelectRed() {
      return selectRed;
    }
    public void setSelectRed(String selectRed) {
      this.selectRed = selectRed;
    }
    public String getSelectBlue() {
      return selectBlue;
    }
    public void setSelectBlue(String selectBlue) {
      this.selectBlue = selectBlue;
    }
    public int getModel() {
      return model;
    }
    public void setModel(int model) {
      this.model = model;
    }
    public int getGetoutHisFlag() {
      return getoutHisFlag;
    }
    public void setGetoutHisFlag(int getoutHisFlag) {
      this.getoutHisFlag = getoutHisFlag;
    }
  }

}
