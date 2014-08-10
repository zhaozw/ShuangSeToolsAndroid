package com.shuangse.ui;

import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.CellData;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.meta.SummaryData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
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

public class RedMissingDataActivity extends Activity {
    private final static String TAG = "RedMissingDataActivity";

    private SharedPreferences sharedPreferences;
    
    private ProgressDialog progDialog;
    private String fromActivity;
    
    private TableLayout dataTable;
    
    private ShuangSeToolsSetApplication appContext; 
    private final static int howManyColumns = 38;
    private int disp_his_num = 20;
    private int latestId;
    private TextView operationTextView;
    
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
        setContentView(R.layout.redmissingdata);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_right_redmissing);
        
//        Button returnBtn = (Button)findViewById(R.id.returnbtn);
//        returnBtn.setVisibility(View.VISIBLE);
//        Button helpBtn = (Button)findViewById(R.id.helpbtn);
//        helpBtn.setVisibility(View.VISIBLE);
//        helpBtn.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            String htmlMsg = "本页操作提示：<br>\t 一、本页可上下左右拖动，拖动到右边下方有操作按钮；" + 
//                                          "<br>\t 二、从上往下看为红球近期出号历史，最左边一列表示期号，" + 
//                                          "从第二列开始往后列依次为红球1，2...到33的出号和遗漏情况：" + 
//                                          "其中用红色球状图形表示当期该红球中出，用灰色数字表示该红球遗漏多" + 
//                                          "少期未中出,也即：遗漏值.<br>\t 三、该图默认显示期数20期，您可在《软件设置->红球走势显示期数》" + 
//                                          "里设置更多的显示期数以方便您查看更多期;" + "<br>\t四、遗漏数据下方空白行方格是选择红球的操作区域，根据走势" + 
//                                          "看好某个红球后，在该方格中轻轻点击，该方格会出现一个球状号码，表示选中该红球，再次点击去掉选择；<br>" + 
//                                          "注意：在第二行 或 胆拖组号 对应行的空白方格中，点击一次表示选中，点击二次表示选中为胆码，点击三次去掉选择;" + 
//                                          "<br>\t 五、选择完后，拖动到最右侧，点击《旋转组号》或《胆拖组号》按钮即可用对应的旋转矩阵组合 或 胆拖组合所选红球;" + 
//                                          "<br>\t 六、最下方标题行上出现次数，最大遗漏和最大连续出号行分别为对应红球在所有双色球开奖历史数据中统计的历史数据，共选号参考;" + 
//                                          "<br>\t 七、选号时，可根据历史遗漏走势，多选上期中出号码的相邻码，斜连图形码，二连码等等，可根据经验选出7-20码进行组号" + 
//                                          "(先别担心选太多红无法购买，旋转矩阵能帮您大大节省资金)。";
//            MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, RedMissingDataActivity.this).show();
//          }
//        }); 
//        returnBtn.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            onBackPressed();
//          }
//        });
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RedMissingDataActivity.this);
        disp_his_num = Integer.parseInt(sharedPreferences.getString("shuangse_display_red_history_cnt", "20"));
        
        fromActivity = getIntent().getExtras().getString("FROM");
        operationTextView = (TextView)findViewById(R.id.operationText);
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
          operationTextView.setText("点击空白方格选号，然后点击最右侧【旋转组号】按钮组号.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
          operationTextView.setText("双击（点击2次）空白方格选为胆码，点击一次选为拖码)，然后点击最右侧【胆拖组号】按钮组号.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
          operationTextView.setText("点击空白方格选号(第二行点击两次选为胆码，点击一次选为拖码)，然后点击最右侧【对应行的组号按钮】组号.");
        }
        
        progDialog = new ProgressDialog(RedMissingDataActivity.this);
        progDialog.setTitle("提示");
        progDialog.setMessage("请稍等，正在加载...");
        progDialog.setCancelable(false);
        progDialog.show();
        
        dataTable = (TableLayout) findViewById(R.id.datatable);        
        new LoadDataAsyncTask().execute("");
        
        setLoadDataComplete(new DataLoadCompletedListener() {
            @Override
            public void loadComplete() {
                //这里执行你要的操作，当UI更新完成后会自动调用这里面的代码 
                //for(int i=0;i<33;i++) {
                //    Log.i(TAG, (i+1)+ " missing: " + appContext.getRedMissingTimes().getMissValueOfNum(i+1)); 
                //}
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
        
        int viewIndex = 1;
        TableRow row = null;
        int totalSize = hisData.size();
        int startIndex = (totalSize - disp_his_num);
        int endIndex = (totalSize - 1);

        int redNum = 0;
        while(startIndex <= endIndex) {
            row = null;
            row = new TableRow(RedMissingDataActivity.this);
            row.setBackgroundColor(Color.RED);

            int itemId = hisData.get(startIndex).id;
            //列0
            row.addView(UIFactory.makeTextCell(Integer.toString(itemId), 
                    Color.WHITE,
                    RedMissingDataActivity.this),
                    appContext.leftCellPara);// 期号
            //列1-33            
            for (int m = 1; m < 37; m++) {
                switch (m) {
                    case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                        redNum = m; break;
                    case 13:case 14:case 15:case 16:case 17: case 18: case 19: case 20: case 21: case 22:case 23:
                        redNum = m - 1; break;
                    case 25:case 26:case 27:case 28:case 29: case 30: case 31: case 32: case 33: case 34:case 35:
                        redNum = m - 2; break;
                    case 12:case 24:case 36:
                        //分割符号
                        row.addView(UIFactory.makeSeperator(RedMissingDataActivity.this), 
                                appContext.rightCellPara);
                        continue;//continue the for loop
                    default:
                        break;
                }//end switch
                
                int missCnt = appContext.getRedNumMissTimes(redNum, itemId);
                if (missCnt == 0) {
                    //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(redNum));
                    row.addView(UIFactory.makeImageCell(appContext.getPicCache().get("red"+redNum),
                            RedMissingDataActivity.this),
                            appContext.rightCellPara);
                } else {
                    row.addView(UIFactory.makeTextCell(
                            Integer.toString(missCnt), Color.GRAY,
                            RedMissingDataActivity.this),
                            appContext.rightCellPara);
                }
            
            }//end for
            
            //右边空1列 - 操作列
            row.addView(UIFactory.makeBlankCell(RedMissingDataActivity.this), appContext.rightCellPara);
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
            
            startIndex++;
        }
        
        //加2行空 - 2个操作行
        int operaLine = 1;
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")){
          operaLine = 2;
        }
        latestId = appContext.getLoalLatestItemIDFromCache();
        for(int lineNum = 0; lineNum < operaLine; lineNum++) {
            row = null;
            row = new TableRow(RedMissingDataActivity.this);
            //第0列
            row.addView(UIFactory.makeTextCell(Integer.toString(latestId + 1), 
                    Color.WHITE,
                    RedMissingDataActivity.this), 
                    appContext.leftCellPara);// 期号        
            //第1-33列
            for (int m = 1; m < howManyColumns; m++) {
                switch (m) {
                    case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                        redNum = m; break;
                    case 13:case 14:case 15:case 16:case 17: case 18: case 19: case 20: case 21: case 22:case 23:
                        redNum = m - 1; break;
                    case 25:case 26:case 27:case 28:case 29: case 30: case 31: case 32: case 33: case 34:case 35:
                        redNum = m - 2; break;
                    case 12:case 24:case 36:
                        //分割符号
                        row.addView(UIFactory.makeSeperator(RedMissingDataActivity.this), 
                                appContext.rightCellPara);
                        continue;//continue the for loop
                    case 37:
                        {
                          if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
                              row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.smartcombinebtn), new RedOPSmartCombineBtnListener(),
                                                      RedMissingDataActivity.this),
                                                      appContext.rightCellPara);
                          } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
                              row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.dantuocombine), new RedOPDanTuoBtnListener(),
                                                      RedMissingDataActivity.this),
                                                      appContext.rightCellPara);
                          } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
                             if(lineNum == 0) {
                               row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.smartcombinebtn), new RedOPSmartCombineBtnListener(),
                                   RedMissingDataActivity.this),
                                   appContext.rightCellPara);
                             } else if(lineNum == 1) {
                               row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.dantuocombine), new RedOPDanTuoBtnListener(),
                                   RedMissingDataActivity.this),
                                   appContext.rightCellPara);
                             }
                          }
                          continue;//continue the for loop
                        }
                    default:
                        break;
                }//end switch
                
                CellData blankCell = null;
                if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
                    blankCell = new CellData(redNum, latestId + 1, 0, 
                        //getResources().getDrawable(MagicTool.getResIDbyRednum(redNum)),
                        //getResources().getDrawable(MagicTool.getDanResIDbyRednum(redNum)),
                        appContext.getPicCache().get("red"+redNum),
                        appContext.getPicCache().get("danRed"+redNum),
                        false, //不允许双击选择
                        disp_his_num + lineNum + 1, m, CellData.P_FOR_SEL_RED_SMART_COMBINE);
                    if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().contains(Integer.valueOf(redNum))) {
                      blankCell.setClicked(1);
                    }
                    ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, RedMissingDataActivity.this);
                    row.addView(cellImage, appContext.rightCellPara);
                } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
                   blankCell = new CellData(redNum, latestId + 1, 0, 
                      //getResources().getDrawable(MagicTool.getResIDbyRednum(redNum)),
                      //getResources().getDrawable(MagicTool.getDanResIDbyRednum(redNum)),
                      appContext.getPicCache().get("red"+redNum),
                      appContext.getPicCache().get("danRed"+redNum),
                      true, //允许双击选择
                      disp_his_num + lineNum + 1, m, CellData.P_FOR_SEL_RED_DANTUO_COMBINE);
                  if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().contains(Integer.valueOf(redNum))) {
                    blankCell.setClicked(2);
                  } else if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().contains(Integer.valueOf(redNum))) {
                    blankCell.setClicked(1);
                  }
                  ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, RedMissingDataActivity.this);
                  row.addView(cellImage, appContext.rightCellPara);
                  
                }else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
                  if(lineNum == 0) {
                    blankCell = new CellData(redNum, latestId + 1, 0, 
                        //getResources().getDrawable(MagicTool.getResIDbyRednum(redNum)),
                        //getResources().getDrawable(MagicTool.getDanResIDbyRednum(redNum)),
                        appContext.getPicCache().get("red"+redNum),
                        appContext.getPicCache().get("danRed"+redNum),
                        false, //不允许双击选择
                        disp_his_num + lineNum + 1, m, CellData.P_FOR_SEL_RED_SMART_COMBINE);
                    if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().contains(Integer.valueOf(redNum))) {
                      blankCell.setClicked(1);
                    }
                    ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, RedMissingDataActivity.this);
                    row.addView(cellImage, appContext.rightCellPara);
                  } else if(lineNum == 1) {
                    blankCell = new CellData(redNum, latestId + 1, 0, 
                        //getResources().getDrawable(MagicTool.getResIDbyRednum(redNum)),
                        //getResources().getDrawable(MagicTool.getDanResIDbyRednum(redNum)),
                        appContext.getPicCache().get("red"+redNum),
                        appContext.getPicCache().get("danRed"+redNum),
                        true, //允许双击选择
                        disp_his_num + lineNum + 1, m, CellData.P_FOR_SEL_RED_DANTUO_COMBINE);
                    if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().contains(Integer.valueOf(redNum))) {
                      blankCell.setClicked(2);
                    } else if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().contains(Integer.valueOf(redNum))) {
                      blankCell.setClicked(1);
                    }
                    ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, RedMissingDataActivity.this);
                    row.addView(cellImage, appContext.rightCellPara);
                  }
                }
            }//end for 
            dataTable.addView(row, viewIndex++, appContext.rowPara);
        }
        ///跳过提示行
        viewIndex++;
        
        //增加三行(出现次数,最大遗漏，最大连出）
        SparseArray<SummaryData> redSummaryData = appContext.getRedNumSummaryData();
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
                    RedMissingDataActivity.this), 
                    appContext.leftCellPara);
            //1 到  最后一列
            String dispText;
            
            SummaryData summaryData;
            for (int m = 1; m <= (howManyColumns - 1); m++) {
                summaryData = null;
                switch(m) {
                case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                    redNum = m; summaryData = redSummaryData.get(redNum); break;
                case 13:case 14:case 15:case 16:case 17: case 18: case 19: case 20: case 21: case 22:case 23:
                    redNum = m - 1; summaryData = redSummaryData.get(redNum); break;
                case 25:case 26:case 27:case 28:case 29: case 30: case 31: case 32: case 33: case 34:case 35:
                    redNum = m - 2; summaryData = redSummaryData.get(redNum); break;
                case 12:case 24:case 36:
                    //分割符号
                    row.addView(UIFactory.makeSeperator(RedMissingDataActivity.this), 
                            appContext.rightCellPara);
                    continue;//continue the for loop
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
                
                row.addView(UIFactory.makeTextCell(dispText, Color.GRAY, RedMissingDataActivity.this),
                        appContext.rightCellPara);
            }//end for
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
        }
    }
    
    private OnClickListener cellClickListener = new CellOnClickListener(this);
    static class CellOnClickListener implements OnClickListener {
      private static RedMissingDataActivity theActivity;
      public CellOnClickListener(RedMissingDataActivity theAct) {
        theActivity = theAct;
      }
      public void onClick(View v) {
        CellData cellData = (CellData) v.getTag();
        //Log.i("makeClickableBlankCell", cellData.toString());
        if(v instanceof ImageView) {
          ImageView imageView = (ImageView)v;
          if(cellData.isClicked() == 0) {//未点击，现在点击
            if(!cellData.isIfAllowDoubleClickSel() && (cellData.getCellFor() == CellData.P_FOR_SEL_RED_SMART_COMBINE)) {
                //不允许双击选择,选择旋转组号的红球
                imageView.setImageDrawable(cellData.getDispImg());
                cellData.setClicked(1);
                if((cellData.getNum() > 0 ) && (cellData.getNum() < 34)) {
                  if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().add(Integer.valueOf(cellData.getNum()))) {
                    theActivity.InfoMessageBox("提示", "选择红球出错，请联系作者。");
                  }
                }
            } else if(cellData.isIfAllowDoubleClickSel() && (cellData.getCellFor() == CellData.P_FOR_SEL_RED_DANTUO_COMBINE)){
              //允许双击选择，选择为拖码 
              imageView.setImageDrawable(cellData.getDispImg());
              cellData.setClicked(1);
              if((cellData.getNum() > 0 ) && (cellData.getNum() < 34)) {
                if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().add(Integer.valueOf(cellData.getNum()))) {
                  theActivity.InfoMessageBox("提示", "选择红球出错，请联系作者。");
                }
              }
            }
          } else if(cellData.isClicked() == 1){//点击了1次，现在又点击
              if(!cellData.isIfAllowDoubleClickSel()) {//不允许双击选择，选择去掉
                  imageView.setImageDrawable(null);
                  cellData.setClicked(0);
                  if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().remove(Integer.valueOf(cellData.getNum()))) {
                    theActivity.InfoMessageBox("提示", "去选择红球出错，请联系作者。");
                  }
              } else {//允许双击,选择为胆码,去掉拖，加入胆
                imageView.setImageDrawable(cellData.getDoubleclickDispImg());
                cellData.setClicked(2);
                if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().remove(Integer.valueOf(cellData.getNum()))) {
                  theActivity.InfoMessageBox("提示", "去选择红球拖码时出错，请联系作者。");
                } 
                if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().add(Integer.valueOf(cellData.getNum()))) {
                  theActivity.InfoMessageBox("提示", "选择红球胆码时出错，请联系作者。");
                }
              }
          } else if(cellData.isClicked() == 2){//点击了2次，现在又点击，去掉为胆码 
            imageView.setImageDrawable(null);
            cellData.setClicked(0);
            if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().remove(Integer.valueOf(cellData.getNum()))) {
              theActivity.InfoMessageBox("提示", "去选择红球胆码出错，请联系作者。");
            }
         } else {
            theActivity.InfoMessageBox("提示", "旋转组号时选择红球胆码表格状态出错，请联系作者2。");
        }
        }
      }
    };
    
    private interface DataLoadCompletedListener {
        public void loadComplete();
    }
    
  // 操作按钮响应函数
   class RedOPSmartCombineBtnListener implements OnClickListener {
    public RedOPSmartCombineBtnListener() {
    }

    public void onClick(View v) {
      showProgressDialog("提示", "请稍等...");
      Log.i("RedOPBtnListener", "Button is clicked");

      Intent intent = new Intent(RedMissingDataActivity.this, SmartCombineActivity.class);
      intent.putExtra("FROM", "RedMissingActivity");
      startActivity(intent);
   }
  }

   // 操作按钮响应函数
   class RedOPDanTuoBtnListener implements OnClickListener {
    public RedOPDanTuoBtnListener() {
    }

    public void onClick(View v) {
      showProgressDialog("提示", "请稍等...");
      Log.i("RedOPBtnListener", "Button is clicked");

      Intent intent = new Intent(RedMissingDataActivity.this, DantuoCombineActivity.class);
      intent.putExtra("FROM", "RedMissingActivity");
      startActivity(intent);
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
    
    private void InfoMessageBox(String title, String msg) {
      AlertDialog notifyDialog = new AlertDialog.Builder(RedMissingDataActivity.this)
          .setTitle(title).setMessage(msg)
          .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }).create();
      notifyDialog.show();
    }

    private ProgressDialog progressDialog;
    private void showProgressDialog(String title, String msg) {
      progressDialog = new ProgressDialog(RedMissingDataActivity.this);
      progressDialog.setTitle(title);
      progressDialog.setMessage(msg);
      progressDialog.setCancelable(false);
      progressDialog.show();
    }

    private void hideProgressBox() {
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
          Intent intent = new Intent(RedMissingDataActivity.this,
              SmartCombineActivity.class);
          startActivity(intent);
          return true;
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) { 
          Intent intent = new Intent(RedMissingDataActivity.this,
              DantuoCombineActivity.class);
          startActivity(intent);
          return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
      } else {
        return super.onKeyDown(keyCode, event);
      }
    }
    
}