package com.shuangse.ui;

import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.meta.CellData;
import com.shuangse.meta.SummaryData;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ZoomControls;

public class BlueMissingDataActivity extends ExtendedActivity {
    private final String TAG = "BlueMissingDataActivity";
    
    private SharedPreferences sharedPreferences;
    private String fromActivity = null;
    
    private ProgressDialog progDialog;
    
    private TableLayout dataTable;
    
    private ShuangSeToolsSetApplication appContext;
    //期号+16个蓝+4个分隔符+大小（2列）+分割符号+奇偶（2列）+分割符号+除3余数（3）+分割符号+四区间（4列）+分割符号+1个操作列
    private final int howManyColumns = 37;
    private int disp_his_num = 20;
    private int latestId;
    
    private DataLoadCompletedListener loadCompletedLisneter;
    public void setLoadDataComplete(DataLoadCompletedListener dataComplete) {
        this.loadCompletedLisneter = dataComplete;
    }
    
    private ZoomControls zoomControls;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        appContext = (ShuangSeToolsSetApplication) getApplication();        
        //更新标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
        setContentView(R.layout.bluemissingdata);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_right_bluemissing);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BlueMissingDataActivity.this);
        disp_his_num = Integer.parseInt(sharedPreferences.getString("shuangse_display_blue_history_cnt", "20"));
        
        fromActivity = getIntent().getExtras().getString("FROM");
        TextView blueOperationTitle = (TextView)findViewById(R.id.blueOperationTitle);
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
          blueOperationTitle.setText("点击空白方格选号，然后点击最右侧【开始组号】按钮组号.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
          blueOperationTitle.setText("点击空白方格选号，然后点击最右侧【开始组号】按钮组号.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
          blueOperationTitle.setText("点击空白方格选号，然后点击最右侧【开始组号】按钮组号.");
        }
        
        progDialog = new ProgressDialog(BlueMissingDataActivity.this);
        progDialog.setTitle("提示");
        progDialog.setMessage("请稍等，正在加载...");
        progDialog.setCancelable(false);
        progDialog.show();
        
        dataTable = (TableLayout) findViewById(R.id.datatable);
        
        new LoadDataAsyncTask().execute("");
        
        setLoadDataComplete(new DataLoadCompletedListener() {
            @Override
            public void loadComplete() {
                //这里执行操作，当UI更新完成后会自动调用这里面的代码 
                progDialog.dismiss();
            }});
        
      zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
      if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
        zoomControls.setVisibility(View.GONE);
      } else {
        zoomControls.setOnZoomInClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                dataTable.setScaleX((float) (dataTable.getScaleX() * 1.2));
                dataTable.setScaleY((float) (dataTable.getScaleY() * 1.2));
              } else {
                InfoMessageBox("提示", "手机系统版本太低，需要安卓3.0以上才能支持缩放！");
              }
          }
        });
    
        zoomControls.setOnZoomOutClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
              dataTable.setScaleX((float) (dataTable.getScaleX() * 0.8));
              dataTable.setScaleY((float) (dataTable.getScaleY() * 0.8));
            } else {
              InfoMessageBox("提示", "手机系统版本太低，需要安卓3.0以上才能支持缩放！");
            }
          }
        });
      }
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port
        }
    }
    
    private void LoadingMissingData() {
        List<ShuangseCodeItem> hisData = appContext.getAllHisData();
        if(hisData == null || hisData.size() < 1) {
            Log.w(TAG, "there is no history data locally.");
            return;
        }
        
        //期号+16蓝+4分割符号+大小（2列）+分割符号+奇偶（2列）+分割符号+除3余数（3）+分割符号+四区间（4列）+分割符号+2个空列
        int viewIndex = 1;
        
        TableRow row = null;
        int totalSize = hisData.size();
        Log.i(TAG, "totalSize:" + totalSize);
        
        int startIndex = (totalSize - disp_his_num);
        int endIndex = (totalSize - 1);
        while(startIndex <= endIndex) {
            row = null;
            row = new TableRow(BlueMissingDataActivity.this);
            row.setBackgroundColor(Color.RED);

            ShuangseCodeItem codeItem = hisData.get(startIndex);
            int itemId = codeItem.id;
            int blueVal = codeItem.blue;
            //0列
            row.addView(UIFactory.makeTextCell(Integer.toString(itemId), Color.WHITE, 
                    BlueMissingDataActivity.this), 
                    appContext.leftCellPara);// 期号
            //1-19列
            for (int m = 1; m <= 19; m++) {
                int blueNum = 0;
                switch(m) {
                case 5:
                case 10:
                case 15:
                    //分割符号
                    row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                            appContext.rightCellPara);
                    continue;//continue the for loop
                case 1:
                case 2:
                case 3:
                case 4:
                    blueNum = m;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    blueNum = m - 1;
                    break;
                case 11:
                case 12:
                case 13:
                case 14:
                    blueNum = m - 2;
                    break;
                case 16:
                case 17:
                case 18:
                case 19:
                    blueNum = m - 3;
                    break;
                }
                
                int missCnt = appContext.getBlueNumMissTimes(blueNum, itemId);

                if (missCnt == 0) {
                    row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue"+blueNum),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(blueNum)),
                            BlueMissingDataActivity.this),
                            appContext.rightCellPara);
                } else {
                    row.addView(UIFactory.makeTextCell(Integer.toString(missCnt),
                            Color.GRAY, 
                            BlueMissingDataActivity.this),
                            appContext.rightCellPara);
                }
            }//end for
            
            //20列 - 分割符号
            row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            //大小（2列）+ 分割符号
            int bigSmall = appContext.getBlueNumAttrValue(0, blueVal);    
            if(bigSmall == 1) {//大
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue17"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(17)),
                        BlueMissingDataActivity.this), appContext.rightCellPara);
                //获取篮球小(0)遗漏几次
                int missVal = appContext.getBlueNumAttrMissTimes(0, 0, itemId);                
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal), Color.GRAY, 
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            } else if(bigSmall == 0){//小
                //获取篮球大(1)遗漏几次
                int missVal = appContext.getBlueNumAttrMissTimes(0, 1, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue18"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(18)),
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            }
            row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            //奇偶（2列）+分割符号
            int evenOdd = appContext.getBlueNumAttrValue(1, blueVal);    
            if(evenOdd == 1) {//奇
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue19"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(19)),
                        BlueMissingDataActivity.this), appContext.rightCellPara);
                //获取偶（0）遗漏几次
                int missVal = appContext.getBlueNumAttrMissTimes(1, 0, itemId);                
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            } else if(evenOdd == 0){//偶
                //获取奇（1）遗漏几次
                int missVal = appContext.getBlueNumAttrMissTimes(1, 1, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue20"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(20)),
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            }
            row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            //除3余数（3）+分割符号
            int leftVal = appContext.getBlueNumAttrValue(2, blueVal);            
            if(leftVal == 0) {//余0
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue21"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(21)),
                        BlueMissingDataActivity.this), appContext.rightCellPara);
                int missVal = appContext.getBlueNumAttrMissTimes(2, 1, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal = appContext.getBlueNumAttrMissTimes(2, 2, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            } else if(leftVal == 1) {//余1
                int missVal = appContext.getBlueNumAttrMissTimes(2, 0, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue22"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(22)),
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal = appContext.getBlueNumAttrMissTimes(2, 2, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal), 
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            } else if(leftVal == 2) {//余2
                int missVal = appContext.getBlueNumAttrMissTimes(2, 0, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                
                missVal = appContext.getBlueNumAttrMissTimes(2, 1, itemId);    
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue23"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(23)),
                        BlueMissingDataActivity.this),
                        appContext.rightCellPara);
            }
            row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            //四区间（4列）+分割符号
            int disNum = appContext.getBlueNumAttrValue(3, blueVal);
            switch (disNum) {
            case 1: //1区间
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue22"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(22)),
                        BlueMissingDataActivity.this), appContext.rightCellPara);
                
                int missVal = appContext.getBlueNumAttrMissTimes(3, 2, itemId);                
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal = appContext.getBlueNumAttrMissTimes(3, 3, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal = appContext.getBlueNumAttrMissTimes(3, 4, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                break;
                
            case 2://2区间
                int missVal2 = appContext.getBlueNumAttrMissTimes(3, 1, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal2),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue23"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(23)),
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal2 = appContext.getBlueNumAttrMissTimes(3, 3, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal2),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal2 = appContext.getBlueNumAttrMissTimes(3, 4, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal2),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                break;
            case 3: //3区间
                int missVal3 = appContext.getBlueNumAttrMissTimes(3, 1, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal3),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal3 = appContext.getBlueNumAttrMissTimes(3, 2, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal3),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue24"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(24)),
                        BlueMissingDataActivity.this),
                        appContext.rightCellPara);
                missVal3 = appContext.getBlueNumAttrMissTimes(3, 4, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal3),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                break;
            case 4://4区间
                int missVal4 = appContext.getBlueNumAttrMissTimes(3, 1, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal4),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal4 = appContext.getBlueNumAttrMissTimes(3, 2, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal4),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                missVal4 = appContext.getBlueNumAttrMissTimes(3, 3, itemId);
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal4),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue25"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(25)),
                        BlueMissingDataActivity.this),
                        appContext.rightCellPara);
                break;
            default:
                Log.e(TAG, "incorrect dis num");
                break;
            }
            row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            //右边1列- 操作按钮
            row.addView(UIFactory.makeBlankCell(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
            
            startIndex++;
        }//end while
        
        //加1行空 - 1个操作行        
        latestId = appContext.getLoalLatestItemIDFromCache();
        row = null;
        row = new TableRow(BlueMissingDataActivity.this);
        //第一列
        row.addView(UIFactory.makeTextCell(Integer.toString(latestId + 1), 
                Color.WHITE,
                BlueMissingDataActivity.this), 
                appContext.leftCellPara);// 期号
            
        for (int m = 1; m <= (howManyColumns - 1); m++) {
            int blueNum = 0;
            Drawable blueRes = null;
            switch(m) {
            case 5:
            case 10:
            case 15:
            case 20:
            case 23:
            case 26:
            case 30:
            case 35:
                // 分割符号
                row.addView(UIFactory
                        .makeSeperator(BlueMissingDataActivity.this),
                         appContext.rightCellPara);
                continue; // continue for loop
            case 1:case 2:case 3:case 4: 
                blueNum = m;
                blueRes = appContext.getPicCache().get("blue"+blueNum);
                break;
            case 6:case 7:case 8:case 9:
                blueNum = m - 1;
                blueRes = appContext.getPicCache().get("blue"+blueNum);
                break;
            case 11:case 12:case 13:case 14:
                blueNum = m - 2;
                blueRes = appContext.getPicCache().get("blue"+blueNum);
                break;
            case 16:case 17:case 18:case 19:
                blueNum = m - 3;
                blueRes = appContext.getPicCache().get("blue"+blueNum);
                break;
            case 21:
                blueRes = appContext.getPicCache().get("blue17");
                break;
            case 22:
                blueRes = appContext.getPicCache().get("blue18");
                break;
            case 24:
                blueRes = appContext.getPicCache().get("blue19");
                break;
            case 25:
                blueRes = appContext.getPicCache().get("blue20");
                break;
            case 27: case 28: case 29:
                blueRes = appContext.getPicCache().get("blue"+(m-6));
                break;
            case 31: case 32: case 33: case 34:
                blueRes = appContext.getPicCache().get("blue"+(m-9));
                break;
            case 36:
                row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.combinebtn), new BlueOPBtnListener(),
                      BlueMissingDataActivity.this),
                      appContext.rightCellPara);
                continue;//continue the for loop
            default:
                continue;
            }//end switch
            
            CellData blankCell = null;
            
            blankCell = new CellData(blueNum, latestId + 1,
                      0, blueRes, null, false, //如果doubleclickDispImg 为null,则不允许双击选胆码
                      disp_his_num + 1, m, CellData.P_FOR_SEL_BLUE_COMBINE);
            if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().contains(Integer.valueOf(blueNum))) {
                    blankCell.setClicked(1);
            } 
            ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, BlueMissingDataActivity.this); 
            row.addView(cellImage, appContext.rightCellPara);
        }
        dataTable.addView(row, viewIndex++, appContext.rowPara);
        
        //跳过提示行
        viewIndex++;
        
        //增加三行(出现次数,最大遗漏，最大连出）
        SparseArray<SummaryData> blueSummaryData = appContext.getBlueNumSummaryData();
        SparseArray<SummaryData> bigSmallSummaryData = appContext.getBlueAttrSummaryData(0);
        SparseArray<SummaryData> evenOddSummaryData = appContext.getBlueAttrSummaryData(1);
        SparseArray<SummaryData> divide3LeftSummaryData = appContext.getBlueAttrSummaryData(2);
        SparseArray<SummaryData> blueDisSummaryData = appContext.getBlueAttrSummaryData(3);
        
        for(int i=0; i<3; i++) {
            row = null;
            row = new TableRow(this);
            String titleStr="";
            switch(i) {
                case 0: titleStr = "出现次数";break;
                case 1: titleStr = "最大遗漏"; break;
                case 2: titleStr = "最大连出"; break;
            }
            
            //0列
            row.addView(UIFactory.makeTextCell(titleStr, 
                    Color.WHITE,
                    BlueMissingDataActivity.this), 
                    appContext.leftCellPara);
            //1 到  最后一列
            String dispText;
            SummaryData summaryData;
            int blueNum;
            for (int m = 1; m <= (howManyColumns - 1); m++) {
                summaryData = null;
                switch(m) {
                case 1:case 2:case 3:case 4: 
                    blueNum = m;
                    summaryData = blueSummaryData.get(blueNum);
                    break;
                case 6:case 7:case 8:case 9:
                    blueNum = m-1;
                    summaryData = blueSummaryData.get(blueNum);
                    break;
                case 11: case 12: case 13:case 14:
                    blueNum = m-2;
                    summaryData = blueSummaryData.get(blueNum);
                    break;
                case 16: case 17: case 18: case 19:
                    blueNum = m-3;
                    summaryData = blueSummaryData.get(blueNum);
                    break;
                case 5: case 10: case 15: case 20: case 23: case 26: case 30: case 35:
                    //分隔符
                    row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                            appContext.rightCellPara);
                    continue; // continue the for loop
                case 21:
                    summaryData = bigSmallSummaryData.get(1);
                    break;
                case 22:
                    summaryData = bigSmallSummaryData.get(0);
                    break;
                case 24:
                    summaryData = evenOddSummaryData.get(1);
                    break;
                case 25:
                    summaryData = evenOddSummaryData.get(0);
                    break;
                case 27:case 28:case 29:
                    summaryData = divide3LeftSummaryData.get(m-27);
                    break;
                case 31:case 32:case 33:case 34:
                    summaryData = blueDisSummaryData.get(m-30);
                    break;
                default:
                    dispText = "";
                    break;
                }//end switch
                
                if(summaryData != null) {
                    switch(i) {
                        case 0: 
                            dispText = Integer.toString(summaryData.totalOccursCnt);
                            break;
                        case 1:
                            dispText = Integer.toString(summaryData.maxMissCnt);
                            break;
                        case 2:
                            dispText = Integer.toString(summaryData.maxContOccursCnt);
                            break;
                        default:
                            dispText = "";
                            break;
                    }
                } else {
                    dispText = "";
                }
                
                row.addView(UIFactory.makeTextCell(dispText, Color.GRAY, BlueMissingDataActivity.this),
                        appContext.rightCellPara);
            }//end for
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
        }
        
    }

    private OnClickListener cellClickListener = new CellOnClickListener(BlueMissingDataActivity.this);
    static class CellOnClickListener implements OnClickListener {
      private BlueMissingDataActivity theActivity;
      public CellOnClickListener(BlueMissingDataActivity theAct) {
        theActivity = theAct;
      }
      public void onClick(View v) {
        CellData cellData = (CellData) v.getTag();
        
        Log.i("makeClickableBlankCell", cellData.toString());
        
        if(v instanceof ImageView) {
          ImageView imageView = (ImageView)v;
          if(cellData.isClicked() == 0) { //篮球被点击选中一次
              imageView.setImageDrawable(cellData.getDispImg());
              cellData.setClicked(1);
              if((cellData.getNum() > 0 ) && (cellData.getNum() < 17)) {
                if(cellData.getCellFor() == CellData.P_FOR_SEL_BLUE_COMBINE) {
                  try {
                      ShuangSeToolsSetApplication.getCurrentSelection().
                          getSelectedBlueNumbers().add(Integer.valueOf(cellData.getNum()));
                  } catch (Exception e) {
                      theActivity.InfoMessageBox("提示", "选择蓝球出错，请联系作者0。");  
                  }
                }
              }
          } else { //篮球被点击选中2次,去掉篮球选择
              imageView.setImageDrawable(null);
              cellData.setClicked(0);
              if(cellData.getCellFor() == CellData.P_FOR_SEL_BLUE_COMBINE) {
                  if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().contains(Integer.valueOf((cellData.getNum())))) {
                    try {
                        ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().remove(Integer.valueOf((cellData.getNum())));
                    } catch (Exception e) {
                      theActivity.InfoMessageBox("提示", "去选择篮球球出错，请联系作者1。");
                    }
                  }
              }
              
          }
        }
      }
    };
    
    private interface DataLoadCompletedListener {
        public void loadComplete();
    }
    
    //操作按钮响应函数
    class BlueOPBtnListener implements OnClickListener {
          public void onClick(View v) {
//              showProgressDialog("提示", "请稍等...");
//              Log.i("BlueOPBtnListener", "Button is clicked");
//              
//              if(fromActivity != null && fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
//                  Intent intent = new Intent(BlueMissingDataActivity.this, SmartCombineActivity.class);
//                  intent.putExtra("FROM", "BlueMissingActivity");
//                  startActivity(intent);
//              } else if(fromActivity != null && fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
//                  Intent intent = new Intent(BlueMissingDataActivity.this, DantuoCombineActivity.class);
//                  intent.putExtra("FROM", "BlueMissingActivity");
//                  startActivity(intent);
//              } else if(fromActivity != null && fromActivity.equalsIgnoreCase("MainViewActivity")) {
//                  Intent intent = new Intent(BlueMissingDataActivity.this, MainViewActivity.class);
//                  intent.putExtra("FROM", "BlueMissingActivity");
//                  startActivity(intent);
//              }
              final CustomDefineAlertDialog dialog = new CustomDefineAlertDialog(BlueMissingDataActivity.this, "ForChooseCombineMethod");
              dialog.setTitle("已保存，请选择下一步：");
          }
    }
    
    class LoadDataAsyncTask extends AsyncTask<String, String, String> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();    
        }

        @Override
        protected void onPostExecute(String result) {
            if (loadCompletedLisneter != null) {
                loadCompletedLisneter.loadComplete();
            }
            super.onPostExecute(result);
        }
        
        protected void onProgressUpdate(String... text) {
            
        }
        
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingMissingData();
                }
            });

            return "Success";
        }
    }

    private ProgressDialog progressDialog;
    @Override
    public void showProgressDialog(String title, String msg) {
      progressDialog = new ProgressDialog(BlueMissingDataActivity.this);
      progressDialog.setTitle(title);
      progressDialog.setMessage(msg);
      progressDialog.setCancelable(false);
      progressDialog.show();
    }

    @Override
    public void hideProgressBox() {
      if (progressDialog != null) {
        progressDialog.dismiss();
        progressDialog = null;
      }
    }

    protected void onPause() {
      super.onPause();
      hideProgressBox();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
      // 是否触发按键为back键
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
          Intent intent = new Intent(BlueMissingDataActivity.this, SmartCombineActivity.class);
          startActivity(intent);
          this.finish();
          return true;
        }else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) { 
            Intent intent = new Intent(BlueMissingDataActivity.this,
                    DantuoCombineActivity.class);
                startActivity(intent);
                this.finish();
                return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
      } else {
        return super.onKeyDown(keyCode, event);
      }
    }

    private void InfoMessageBox(String title, String msg) {
      AlertDialog notifyDialog = new AlertDialog.Builder(BlueMissingDataActivity.this)
          .setTitle(title).setMessage(msg)
          .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }).create();
      notifyDialog.show();
    }
}
