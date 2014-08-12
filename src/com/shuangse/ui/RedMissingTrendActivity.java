package com.shuangse.ui;

import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.CellData;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.meta.SummaryData;
import com.shuangse.meta.ValueObj;

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

public class RedMissingTrendActivity extends Activity {
    private final static String TAG = "RedMissingTrendActivity";

    private SharedPreferences sharedPreferences;
    
    private ProgressDialog progDialog;
    private String fromActivity;
    
    private TableLayout dataTable;
    
    private ShuangSeToolsSetApplication appContext; 
    private final static int howManyColumns = (36 + 24 + 9);
    private int disp_his_num = 20;
    private int latestId;
    private TextView operationTextView;
    private ValueObj[] redListOrderedByMissingTimes;
    private TextView[] title_top_red = new TextView[33];
    private TextView[] title_bottom_red = new TextView[33];
    
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
        setContentView(R.layout.redhotcooltrend);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_hot_cool_trend);
        
//        Button returnBtn = (Button)findViewById(R.id.returnbtn);
//        returnBtn.setVisibility(View.VISIBLE);
//        Button helpBtn = (Button)findViewById(R.id.helpbtn);
//        helpBtn.setVisibility(View.VISIBLE);
//        helpBtn.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            String htmlMsg = "本页操作提示：<br>\t 一、红球冷热走势图和遗漏走势图基本相同，只是显示方式改变，有以下区别；" + 
//                                          "<br>\t 二、本冷热走势图将红球1-33动态的划分为三个区间（热码-温码-冷码），依次用不同背景色区分显示。" + 
//                                          "<br>\t三、从左往右看，可见红球呈现阶梯状排列，这是动态的按红球热-温-冷（遗漏值）码排列的结果，遗漏0也即上期中出号码" + 
//                                          "在最左方，遗漏1（间隔1期未出号码）依次排列等等，其中背景色为黑色的热码是指遗漏值为0，1，2的红球，背景色为浅紫色" +
//                                          "的温码是指遗漏值为3，4，5，6的红球，背景色为亮紫色的冷码指遗漏值大于等于7的所有红球；" +
//                                          "<br>\t四、最右方部分从上往下看，依次为对应的热码-温码和冷码历史出号走势；" +
//                                          "<br>\t 五、选号操作及显示期数设置同红球走势遗漏图一样；" + 
//                                          "<br>\t 六、根据右方热-温-冷码出号个数走势可以看出，热码出2-3个较多；温码出1-2个较多；冷码出0-1-2较多，因此基于此图选号时，" +
//                                          "可根据该规律选号(多选热-温码)。" +
//                                          "<br>\t 七、本冷热走势图可以和遗漏走势图结合使用，在此图中选择的号码，也会在遗漏图中显示，反之亦然，二者结合使用，可" +
//                                          "大大提高选号的覆盖概率。";
//            MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, RedMissingTrendActivity.this).show();
//          }
//        });
//        returnBtn.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            onBackPressed();
//          }
//        });
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RedMissingTrendActivity.this);
        disp_his_num = Integer.parseInt(sharedPreferences.getString("shuangse_display_red_history_cnt", "20"));
        
        fromActivity = getIntent().getExtras().getString("FROM");
        operationTextView = (TextView)findViewById(R.id.operationText);
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
          operationTextView.setText("点击空白方格选【红号】,点击两次选为【红胆号】.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
          operationTextView.setText("点击空白方格选【红号】,点击两次选为【红胆号】.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
          operationTextView.setText("点击空白方格选【红号】,点击两次选为【红胆号】.");
        }
        
        progDialog = new ProgressDialog(RedMissingTrendActivity.this);
        progDialog.setTitle("提示");
        progDialog.setMessage("请稍等，正在加载...");
        progDialog.setCancelable(false);
        progDialog.show();
        
        //33个红球按遗漏递增排好序了
        redListOrderedByMissingTimes = appContext.getRedNumberOrderedByMissingTimes();
        dataTable = (TableLayout) findViewById(R.id.hotcool_datatable);
        title_top_red[0] = (TextView)findViewById(R.id.top_red_1);
        title_top_red[1] = (TextView)findViewById(R.id.top_red_2);
        title_top_red[2] = (TextView)findViewById(R.id.top_red_3);
        title_top_red[3] = (TextView)findViewById(R.id.top_red_4);
        title_top_red[4] = (TextView)findViewById(R.id.top_red_5);
        title_top_red[5] = (TextView)findViewById(R.id.top_red_6);
        title_top_red[6] = (TextView)findViewById(R.id.top_red_7);
        title_top_red[7] = (TextView)findViewById(R.id.top_red_8);
        title_top_red[8] = (TextView)findViewById(R.id.top_red_9);
        title_top_red[9] = (TextView)findViewById(R.id.top_red_10);
        title_top_red[10] = (TextView)findViewById(R.id.top_red_11);
        title_top_red[11] = (TextView)findViewById(R.id.top_red_12);
        title_top_red[12] = (TextView)findViewById(R.id.top_red_13);
        title_top_red[13] = (TextView)findViewById(R.id.top_red_14);
        title_top_red[14] = (TextView)findViewById(R.id.top_red_15);
        title_top_red[15] = (TextView)findViewById(R.id.top_red_16);
        title_top_red[16] = (TextView)findViewById(R.id.top_red_17);
        title_top_red[17] = (TextView)findViewById(R.id.top_red_18);
        title_top_red[18] = (TextView)findViewById(R.id.top_red_19);
        title_top_red[19] = (TextView)findViewById(R.id.top_red_20);
        title_top_red[20] = (TextView)findViewById(R.id.top_red_21);
        title_top_red[21] = (TextView)findViewById(R.id.top_red_22);
        title_top_red[22] = (TextView)findViewById(R.id.top_red_23);
        title_top_red[23] = (TextView)findViewById(R.id.top_red_24);
        title_top_red[24] = (TextView)findViewById(R.id.top_red_25);
        title_top_red[25] = (TextView)findViewById(R.id.top_red_26);
        title_top_red[26] = (TextView)findViewById(R.id.top_red_27);
        title_top_red[27] = (TextView)findViewById(R.id.top_red_28);
        title_top_red[28] = (TextView)findViewById(R.id.top_red_29);
        title_top_red[29] = (TextView)findViewById(R.id.top_red_30);
        title_top_red[30] = (TextView)findViewById(R.id.top_red_31);
        title_top_red[31] = (TextView)findViewById(R.id.top_red_32);
        title_top_red[32] = (TextView)findViewById(R.id.top_red_33);
        
        title_bottom_red[0] = (TextView)findViewById(R.id.bottom_red_1);
        title_bottom_red[1] = (TextView)findViewById(R.id.bottom_red_2);
        title_bottom_red[2] = (TextView)findViewById(R.id.bottom_red_3);
        title_bottom_red[3] = (TextView)findViewById(R.id.bottom_red_4);
        title_bottom_red[4] = (TextView)findViewById(R.id.bottom_red_5);
        title_bottom_red[5] = (TextView)findViewById(R.id.bottom_red_6);
        title_bottom_red[6] = (TextView)findViewById(R.id.bottom_red_7);
        title_bottom_red[7] = (TextView)findViewById(R.id.bottom_red_8);
        title_bottom_red[8] = (TextView)findViewById(R.id.bottom_red_9);
        title_bottom_red[9] = (TextView)findViewById(R.id.bottom_red_10);
        title_bottom_red[10] = (TextView)findViewById(R.id.bottom_red_11);
        title_bottom_red[11] = (TextView)findViewById(R.id.bottom_red_12);
        title_bottom_red[12] = (TextView)findViewById(R.id.bottom_red_13);
        title_bottom_red[13] = (TextView)findViewById(R.id.bottom_red_14);
        title_bottom_red[14] = (TextView)findViewById(R.id.bottom_red_15);
        title_bottom_red[15] = (TextView)findViewById(R.id.bottom_red_16);
        title_bottom_red[16] = (TextView)findViewById(R.id.bottom_red_17);
        title_bottom_red[17] = (TextView)findViewById(R.id.bottom_red_18);
        title_bottom_red[18] = (TextView)findViewById(R.id.bottom_red_19);
        title_bottom_red[19] = (TextView)findViewById(R.id.bottom_red_20);
        title_bottom_red[20] = (TextView)findViewById(R.id.bottom_red_21);
        title_bottom_red[21] = (TextView)findViewById(R.id.bottom_red_22);
        title_bottom_red[22] = (TextView)findViewById(R.id.bottom_red_23);
        title_bottom_red[23] = (TextView)findViewById(R.id.bottom_red_24);
        title_bottom_red[24] = (TextView)findViewById(R.id.bottom_red_25);
        title_bottom_red[25] = (TextView)findViewById(R.id.bottom_red_26);
        title_bottom_red[26] = (TextView)findViewById(R.id.bottom_red_27);
        title_bottom_red[27] = (TextView)findViewById(R.id.bottom_red_28);
        title_bottom_red[28] = (TextView)findViewById(R.id.bottom_red_29);
        title_bottom_red[29] = (TextView)findViewById(R.id.bottom_red_30);
        title_bottom_red[30] = (TextView)findViewById(R.id.bottom_red_31);
        title_bottom_red[31] = (TextView)findViewById(R.id.bottom_red_32);
        title_bottom_red[32] = (TextView)findViewById(R.id.bottom_red_33);
        
        for(int col = 0; col < 33; col++) {
          title_top_red[col].setText(String.valueOf(redListOrderedByMissingTimes[col].val));
          title_bottom_red[col].setText(String.valueOf(redListOrderedByMissingTimes[col].val));
        }
        
        new LoadDataAsyncTask().execute("");
        
        setLoadDataComplete(new DataLoadCompletedListener() {
            @Override
            public void loadComplete() {
                //这里执行你要的操作，当UI更新完成后会自动调用这里面的代码 
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
    
    private int getColorByMissValue(int missVal) {
      if(missVal >= ShuangSeToolsSetApplication.HOT_MISS_START && 
          missVal <= ShuangSeToolsSetApplication.HOT_MISS_END) {
        return Color.rgb(10, 10, 10);
      } else if(missVal >= ShuangSeToolsSetApplication.WARM_MISS_START &&
                    missVal <= ShuangSeToolsSetApplication.WARM_MISS_END) {
        return Color.rgb(30,33,37);
      } else if(missVal >= ShuangSeToolsSetApplication.COOL_MISS_START) {
        return Color.rgb(66, 66, 66);
      } else {
        return Color.rgb(0, 0, 0);
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
        int missValueOfThisRed = -1;
        while(startIndex <= endIndex) {
            row = null;
            row = new TableRow(RedMissingTrendActivity.this);
            row.setBackgroundColor(Color.RED);

            int itemId = hisData.get(startIndex).id;
            
            int[] redMissTimes = appContext.getMissCntOfItemByItemIndex(startIndex);
            int hotOccursCnt = ShuangSeToolsSetApplication.getOccursCntOfHot(redMissTimes);
            int warmOccursCnt = ShuangSeToolsSetApplication.getOccursCntOfWarm(redMissTimes);
            int coolOccursCnt = ShuangSeToolsSetApplication.getOccursCntOfCool(redMissTimes);
            //每一期的遗漏值的遗漏数统计 8 个
            int[] missCntOfMissValues = appContext.getMissCntOfMissValue(startIndex);
            
            //列0
            row.addView(UIFactory.makeTextCell(Integer.toString(itemId), 
                    Color.WHITE,
                    RedMissingTrendActivity.this),
                    appContext.leftCellPara);// 期号
            //列1-68
            for (int m = 1; m < (howManyColumns - 1); m++) {
                switch (m) {//m表示第几列，列号
                    case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                    case 12:case 13:case 14:case 15:case 16: case 17: case 18: case 19: case 20: case 21:case 22:
                    case 23:case 24:case 25:case 26:case 27: case 28: case 29: case 30: case 31: case 32:case 33:
                        redNum = redListOrderedByMissingTimes[m-1].val;
                        missValueOfThisRed = redListOrderedByMissingTimes[m-1].cnt;
                        //获取基于那一期的该红球的遗漏值
                        int missCnt = appContext.getRedNumMissTimes(redNum, itemId);
                        if (missCnt == 0) {//当期出号了，显示球
                            //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(redNum));
                            row.addView(UIFactory.makeImageCellWithBackground(
                                    appContext.getPicCache().get("red"+redNum),
                                    RedMissingTrendActivity.this, 
                                    getColorByMissValue(missValueOfThisRed)),
                                    appContext.rightCellPara);
                        } else {//显示遗漏值
                            row.addView(UIFactory.makeTextCellWithBackground(
                                    Integer.toString(missCnt), Color.GRAY,
                                    RedMissingTrendActivity.this, getColorByMissValue(missValueOfThisRed)),
                                    appContext.rightCellPara);
                        }
                        break;
                    case 59: case 60: case 61: case 62: case 63: case 64: case 65: case 66:
                      //遗漏0，1，2，3，4，5，6，>=7 的遗漏值 missCntOfMissValues[0-7]
                      row.addView(UIFactory.makeTextCellWithBackground(
                          Integer.toString(missCntOfMissValues[m-59]), Color.WHITE,
                          RedMissingTrendActivity.this, Color.BLACK),
                          appContext.rightCellPara);
                      continue;
                      
                    case 34: case 42:case 50:case 58:case 67:
                        //分割符号
                        row.addView(UIFactory.makeSeperator(RedMissingTrendActivity.this), 
                                appContext.rightCellPara);
                        continue;//continue the for loop
                    case 35: case 36:case 37:case 38:case 39:case 40:case 41: //热码出0 - 6个 (35 - 41)
                        if(hotOccursCnt == ( m - 35)) {
                          //显示出几个的图片
                          //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(hotOccursCnt));
                          row.addView(UIFactory.makeImageCellWithBackground(appContext.getPicCache().get("red" + hotOccursCnt), 
                              RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.HOT_MISS_START)),
                                                  appContext.rightCellPara);
                        } else {//显示遗漏次数
                          row.addView(UIFactory.makeTextCellWithBackground(Integer.toString(appContext.getRedNumHotOccurMissTimes(startIndex, m-35)),
                              Color.GRAY,  RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.HOT_MISS_START)),
                              appContext.rightCellPara);
                        }
                        continue;
                    case 43:case 44:case 45:case 46:case 47:case 48:case 49: //温码出0-6个（43-49)
                      if(warmOccursCnt == ( m - 43)) {
                        //显示出几个的图片
                        //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(warmOccursCnt));
                        row.addView(UIFactory.makeImageCellWithBackground(appContext.getPicCache().get("red" + warmOccursCnt),
                            RedMissingTrendActivity.this, 
                            getColorByMissValue(ShuangSeToolsSetApplication.WARM_MISS_START)),
                                                appContext.rightCellPara);
                      } else {//显示遗漏次数
                        row.addView(UIFactory.makeTextCellWithBackground(Integer.toString(appContext.getRedNumWarmOccurMissTimes(startIndex, m-43)),
                            Color.GRAY, RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.WARM_MISS_START)), 
                            appContext.rightCellPara);
                      }
                      continue;
                    case 51:case 52:case 53:case 54:case 55:case 56:case 57://冷码出0-6个 （51-57)
                      if(coolOccursCnt == ( m - 51)) {
                        //显示出几个的图片
                        //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(coolOccursCnt));
                        row.addView(UIFactory.makeImageCellWithBackground(appContext.getPicCache().get("red" + coolOccursCnt), 
                            RedMissingTrendActivity.this,
                            getColorByMissValue(ShuangSeToolsSetApplication.COOL_MISS_START)),
                                                appContext.rightCellPara);
                      } else {//显示遗漏次数
                        row.addView(UIFactory.makeTextCellWithBackground(Integer.toString(appContext.getRedNumCoolOccurMissTimes(startIndex, m-51)),
                            Color.GRAY, RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.COOL_MISS_START)), 
                            appContext.rightCellPara);
                      }
                      continue;
                    default:
                        break;
                }//end switch
                
            }//end for
            
            //右边空1列 - 操作列
            row.addView(UIFactory.makeBlankCell(RedMissingTrendActivity.this), appContext.rightCellPara);
            //把该行加入
            dataTable.addView(row, viewIndex++, appContext.rowPara);
            
            startIndex++;
        }//end while
        
        //加1行空 - 1个操作行
        latestId = appContext.getLoalLatestItemIDFromCache();

        row = null;
        row = new TableRow(RedMissingTrendActivity.this);
        //第0列  期号
        row.addView(UIFactory.makeTextCell(Integer.toString(latestId + 1), 
                Color.WHITE,
                RedMissingTrendActivity.this), 
                appContext.leftCellPara);// 期号
        //第1-68列
        for (int m = 1; m < howManyColumns; m++) {
            switch (m) {//m表示列号
                case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                case 12:case 13:case 14:case 15:case 16: case 17: case 18: case 19: case 20: case 21:case 22:
                case 23:case 24:case 25:case 26:case 27: case 28: case 29: case 30: case 31: case 32:case 33:
                    redNum = redListOrderedByMissingTimes[m-1].val; break;
                case 34:case 42:case 50:case 58:case 67:
                    //分割符号
                    row.addView(UIFactory.makeSeperator(RedMissingTrendActivity.this), 
                              appContext.rightCellPara);
                    continue;//continue the for loop
                                
                case 59: case 60: case 61: case 62: case 63: case 64: case 65: case 66:
                //遗漏0，1，2，3，4，5，6，>=7 的遗漏值 missCntOfMissValues[0-7]
                case 35: case 36:case 37:case 38:case 39:case 40:case 41: //热码出0 - 6个 (35 - 41)
                case 43:case 44:case 45:case 46:case 47:case 48:case 49: //温码出0-6个（43-49)
                case 51:case 52:case 53:case 54:case 55:case 56:case 57://冷码出0-6个 （51-57)
                    row.addView(UIFactory.makeBlankCell(RedMissingTrendActivity.this), appContext.rightCellPara);
                    continue;
                case 68://操作按钮
                    row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.combinebtn),
                                   new RedOPCommonCombineBtnListener(),
                                   RedMissingTrendActivity.this),
                                   appContext.rightCellPara);
                    continue;//continue the for loop
                default:
                    break;
            }//end switch
    
            CellData blankCell = null;
            blankCell = new CellData(redNum, latestId + 1, 0, 
                  appContext.getPicCache().get("red"+redNum),
                  appContext.getPicCache().get("danRed"+redNum),
                  true, //允许双击选择
                  disp_his_num + 1, m, CellData.P_FOR_SEL_RED_COMBINE);
            if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().contains(Integer.valueOf(redNum))) {
                blankCell.setClicked(2);
            } else if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().contains(Integer.valueOf(redNum))) {
                blankCell.setClicked(1);
            }
            ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, RedMissingTrendActivity.this);
            row.addView(cellImage, appContext.rightCellPara);
        }//End for
        
        dataTable.addView(row, viewIndex++, appContext.rowPara);
        
        //跳过提示行
        viewIndex++;
        
        //增加三行(出现次数,最大遗漏，最大连出）
        SparseArray<SummaryData> redSummaryData = appContext.getRedNumSummaryData();
        SparseArray<SummaryData> hotOccursSummaryData = appContext.getRedNumHotWarmCoolOccursSummaryData(1);
        SparseArray<SummaryData> warmOccursSummaryData = appContext.getRedNumHotWarmCoolOccursSummaryData(2);
        SparseArray<SummaryData> coolOccursSummaryData = appContext.getRedNumHotWarmCoolOccursSummaryData(3);
        
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
                    RedMissingTrendActivity.this), 
                    appContext.leftCellPara);
            //1 到  最后一列
            String dispText="";
            SummaryData summaryData = null;
            int occursNum = -1;
            for (int m = 1; m <= (howManyColumns - 1); m++) {
                summaryData = null;
                dispText = "";
                switch(m) {
                case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                case 12:case 13:case 14:case 15:case 16: case 17: case 18: case 19: case 20: case 21:case 22:
                case 23:case 24:case 25:case 26:case 27: case 28: case 29: case 30: case 31: case 32:case 33:
                              redNum = redListOrderedByMissingTimes[m-1].val; 
                              summaryData = redSummaryData.get(redNum);
                              break;
                case 34:case 42:case 50:case 58:case 67:
                            //分割符号
                            row.addView(UIFactory.makeSeperator(RedMissingTrendActivity.this), 
                                    appContext.rightCellPara);
                            continue;//continue the for loop
                //遗漏0，1，2，3，4，5，6，>=7 的遗漏值 missCntOfMissValues[0-7]
                case 59: 
                  if(i==0) {
                    dispText = "0";
                  }else if(i==1) {//最大值
                    dispText = "5"; 
                  }else {dispText = "";}
                  break;
                case 60:case 61:
                  if(i==0) {
                    dispText = String.valueOf(m-59);
                  }else if(i==1) {//最大值
                    dispText = "6"; 
                  }else {dispText = "";}
                  break;
                case 62:
                  if(i==0) {
                    dispText = "3";
                  } else if(i==1) {//最大值
                    dispText = "10"; 
                  }else {dispText = "";}
                  break;
                  case 63:
                    if(i==0) {
                      dispText = "4";
                    }else if(i==1) {//最大值
                      dispText = "14"; 
                    }else {dispText = "";}
                    break;
                  case 64:
                    if(i==0) {
                      dispText = "5";
                    } else if(i==1) {//最大值
                    dispText = "18"; 
                  }else {dispText = "";}
                  break;
                  case 65: 
                    if(i==0) {
                      dispText = "6";
                    } else if(i==1) {//最大值
                      dispText = "21"; 
                    }else {dispText = "";}
                    break;
                  case 66:
                    if(i==0) {
                      dispText = ">=7";
                    }else if(i==1) {//最大值
                      dispText = "1"; 
                    } else {dispText = "";}
                    break;
                    
                case 35: case 36:case 37:case 38:case 39:case 40:case 41: //热码出0 - 6个 (35 - 41)
                          occursNum = m - 35;
                          summaryData = hotOccursSummaryData.get(occursNum);
                          break;
                case 43:case 44:case 45:case 46:case 47:case 48:case 49: //温码出0-6个（43-49)
                          occursNum = m - 43;
                          summaryData = warmOccursSummaryData.get(occursNum);
                          break;
                case 51:case 52:case 53:case 54:case 55:case 56:case 57://冷码出0-6个 （51-57)
                          occursNum = m - 51;
                          summaryData = coolOccursSummaryData.get(occursNum); 
                          break;
                case 68:
                          dispText = "";
                          //最右边空一列
                          row.addView(UIFactory.makeTextCell(dispText, Color.GRAY, RedMissingTrendActivity.this),
                                  appContext.rightCellPara);
                          continue;
                  
                default:
                        dispText = "";
                        break;
                }//end switch
                
                //下面逻辑是红球及出热-温-冷总结数据共同使用显示
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
              } 
                
              row.addView(UIFactory.makeTextCell(dispText, Color.GRAY, RedMissingTrendActivity.this),
                  appContext.rightCellPara);
                
            }//end for
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
        }
    }
    
    private OnClickListener cellClickListener = new CellOnClickListener(this);
    static class CellOnClickListener implements OnClickListener {
      private static RedMissingTrendActivity theActivity;
      public CellOnClickListener(RedMissingTrendActivity theAct) {
        theActivity = theAct;
      }
      public void onClick(View v) {
        CellData cellData = (CellData) v.getTag();
        //Log.i("makeClickableBlankCell", cellData.toString());
        if(v instanceof ImageView) {
          ImageView imageView = (ImageView)v;
          if(cellData.isClicked() == 0) {//未点击，现在点击
            if(cellData.isIfAllowDoubleClickSel() && (cellData.getCellFor() == CellData.P_FOR_SEL_RED_COMBINE)) {
                imageView.setImageDrawable(cellData.getDispImg());
                cellData.setClicked(1);
                if((cellData.getNum() > 0 ) && (cellData.getNum() < 34)) {
                  try{
                    ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().add(Integer.valueOf(cellData.getNum()));
                  }catch (Exception e) {
                    theActivity.InfoMessageBox("提示", "选择红球出错，请联系作者。");
                  }
                }
            } 
          } else if(cellData.isClicked() == 1){//点击了1次，现在又点击
                //允许双击,选择为胆码,去掉拖，加入胆
                imageView.setImageDrawable(cellData.getDoubleclickDispImg());
                cellData.setClicked(2);
                try {
                    ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().remove(Integer.valueOf(cellData.getNum()));
                    ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().add(Integer.valueOf(cellData.getNum()));
                }catch (Exception e) {
                  theActivity.InfoMessageBox("提示", "选择红球胆码时出错，请联系作者。");
                }
          } else if(cellData.isClicked() == 2){//点击了2次，现在又点击，去掉为胆码 
            imageView.setImageDrawable(null);
            cellData.setClicked(0);
            try {
                ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().remove(Integer.valueOf(cellData.getNum()));
                ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().remove(Integer.valueOf(cellData.getNum()));
            }catch (Exception e) {
              theActivity.InfoMessageBox("提示", "选择红球胆码时出错，请联系作者。");
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
    class RedOPCommonCombineBtnListener implements OnClickListener {
        public RedOPCommonCombineBtnListener() {
        }

        public void onClick(View v) {
            final CustomDefineAlertDialog dialog = new CustomDefineAlertDialog(RedMissingTrendActivity.this, "ForChooseCombineMethod");
            dialog.setTitle("请选择:");
        }
    };

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
      AlertDialog notifyDialog = new AlertDialog.Builder(RedMissingTrendActivity.this)
          .setTitle(title).setMessage(msg)
          .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }).create();
      notifyDialog.show();
    }

    private ProgressDialog progressDialog;
//    private void showProgressDialog(String title, String msg) {
//      progressDialog = new ProgressDialog(RedMissingTrendActivity.this);
//      progressDialog.setTitle(title);
//      progressDialog.setMessage(msg);
//      progressDialog.setCancelable(false);
//      progressDialog.show();
//    }

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
          Intent intent = new Intent(RedMissingTrendActivity.this,
              SmartCombineActivity.class);
          startActivity(intent);
          this.finish();
          return true;
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) { 
          Intent intent = new Intent(RedMissingTrendActivity.this,
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
    
}