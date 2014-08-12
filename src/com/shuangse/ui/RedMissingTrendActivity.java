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
        
        //���±���
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
//            String htmlMsg = "��ҳ������ʾ��<br>\t һ��������������ͼ����©����ͼ������ͬ��ֻ����ʾ��ʽ�ı䣬����������" + 
//                                          "<br>\t ��������������ͼ������1-33��̬�Ļ���Ϊ�������䣨����-����-���룩�������ò�ͬ����ɫ������ʾ��" + 
//                                          "<br>\t�����������ҿ����ɼ�������ֽ���״���У����Ƕ�̬�İ�������-��-�䣨��©ֵ�������еĽ������©0Ҳ�������г�����" + 
//                                          "�����󷽣���©1�����1��δ�����룩�������еȵȣ����б���ɫΪ��ɫ��������ָ��©ֵΪ0��1��2�ĺ��򣬱���ɫΪǳ��ɫ" +
//                                          "��������ָ��©ֵΪ3��4��5��6�ĺ��򣬱���ɫΪ����ɫ������ָ��©ֵ���ڵ���7�����к���" +
//                                          "<br>\t�ġ����ҷ����ִ������¿�������Ϊ��Ӧ������-�����������ʷ�������ƣ�" +
//                                          "<br>\t �塢ѡ�Ų�������ʾ��������ͬ����������©ͼһ����" + 
//                                          "<br>\t ���������ҷ���-��-������Ÿ������ƿ��Կ����������2-3���϶ࣻ�����1-2���϶ࣻ�����0-1-2�϶࣬��˻��ڴ�ͼѡ��ʱ��" +
//                                          "�ɸ��ݸù���ѡ��(��ѡ��-����)��" +
//                                          "<br>\t �ߡ�����������ͼ���Ժ���©����ͼ���ʹ�ã��ڴ�ͼ��ѡ��ĺ��룬Ҳ������©ͼ����ʾ����֮��Ȼ�����߽��ʹ�ã���" +
//                                          "������ѡ�ŵĸ��Ǹ��ʡ�";
//            MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, RedMissingTrendActivity.this).show();
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
          operationTextView.setText("����հ׷���ѡ����š�,�������ѡΪ���쵨�š�.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
          operationTextView.setText("����հ׷���ѡ����š�,�������ѡΪ���쵨�š�.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
          operationTextView.setText("����հ׷���ѡ����š�,�������ѡΪ���쵨�š�.");
        }
        
        progDialog = new ProgressDialog(RedMissingTrendActivity.this);
        progDialog.setTitle("��ʾ");
        progDialog.setMessage("���Եȣ����ڼ���...");
        progDialog.setCancelable(false);
        progDialog.show();
        
        //33��������©�����ź�����
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
                //����ִ����Ҫ�Ĳ�������UI������ɺ���Զ�����������Ĵ��� 
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
                  InfoMessageBox("��ʾ", "�ֻ�ϵͳ�汾̫�ͣ���Ҫ��׿3.0���ϲ���֧�����ţ�");
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
                InfoMessageBox("��ʾ", "�ֻ�ϵͳ�汾̫�ͣ���Ҫ��׿3.0���ϲ���֧�����ţ�");
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
            //ÿһ�ڵ���©ֵ����©��ͳ�� 8 ��
            int[] missCntOfMissValues = appContext.getMissCntOfMissValue(startIndex);
            
            //��0
            row.addView(UIFactory.makeTextCell(Integer.toString(itemId), 
                    Color.WHITE,
                    RedMissingTrendActivity.this),
                    appContext.leftCellPara);// �ں�
            //��1-68
            for (int m = 1; m < (howManyColumns - 1); m++) {
                switch (m) {//m��ʾ�ڼ��У��к�
                    case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                    case 12:case 13:case 14:case 15:case 16: case 17: case 18: case 19: case 20: case 21:case 22:
                    case 23:case 24:case 25:case 26:case 27: case 28: case 29: case 30: case 31: case 32:case 33:
                        redNum = redListOrderedByMissingTimes[m-1].val;
                        missValueOfThisRed = redListOrderedByMissingTimes[m-1].cnt;
                        //��ȡ������һ�ڵĸú������©ֵ
                        int missCnt = appContext.getRedNumMissTimes(redNum, itemId);
                        if (missCnt == 0) {//���ڳ����ˣ���ʾ��
                            //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(redNum));
                            row.addView(UIFactory.makeImageCellWithBackground(
                                    appContext.getPicCache().get("red"+redNum),
                                    RedMissingTrendActivity.this, 
                                    getColorByMissValue(missValueOfThisRed)),
                                    appContext.rightCellPara);
                        } else {//��ʾ��©ֵ
                            row.addView(UIFactory.makeTextCellWithBackground(
                                    Integer.toString(missCnt), Color.GRAY,
                                    RedMissingTrendActivity.this, getColorByMissValue(missValueOfThisRed)),
                                    appContext.rightCellPara);
                        }
                        break;
                    case 59: case 60: case 61: case 62: case 63: case 64: case 65: case 66:
                      //��©0��1��2��3��4��5��6��>=7 ����©ֵ missCntOfMissValues[0-7]
                      row.addView(UIFactory.makeTextCellWithBackground(
                          Integer.toString(missCntOfMissValues[m-59]), Color.WHITE,
                          RedMissingTrendActivity.this, Color.BLACK),
                          appContext.rightCellPara);
                      continue;
                      
                    case 34: case 42:case 50:case 58:case 67:
                        //�ָ����
                        row.addView(UIFactory.makeSeperator(RedMissingTrendActivity.this), 
                                appContext.rightCellPara);
                        continue;//continue the for loop
                    case 35: case 36:case 37:case 38:case 39:case 40:case 41: //�����0 - 6�� (35 - 41)
                        if(hotOccursCnt == ( m - 35)) {
                          //��ʾ��������ͼƬ
                          //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(hotOccursCnt));
                          row.addView(UIFactory.makeImageCellWithBackground(appContext.getPicCache().get("red" + hotOccursCnt), 
                              RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.HOT_MISS_START)),
                                                  appContext.rightCellPara);
                        } else {//��ʾ��©����
                          row.addView(UIFactory.makeTextCellWithBackground(Integer.toString(appContext.getRedNumHotOccurMissTimes(startIndex, m-35)),
                              Color.GRAY,  RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.HOT_MISS_START)),
                              appContext.rightCellPara);
                        }
                        continue;
                    case 43:case 44:case 45:case 46:case 47:case 48:case 49: //�����0-6����43-49)
                      if(warmOccursCnt == ( m - 43)) {
                        //��ʾ��������ͼƬ
                        //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(warmOccursCnt));
                        row.addView(UIFactory.makeImageCellWithBackground(appContext.getPicCache().get("red" + warmOccursCnt),
                            RedMissingTrendActivity.this, 
                            getColorByMissValue(ShuangSeToolsSetApplication.WARM_MISS_START)),
                                                appContext.rightCellPara);
                      } else {//��ʾ��©����
                        row.addView(UIFactory.makeTextCellWithBackground(Integer.toString(appContext.getRedNumWarmOccurMissTimes(startIndex, m-43)),
                            Color.GRAY, RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.WARM_MISS_START)), 
                            appContext.rightCellPara);
                      }
                      continue;
                    case 51:case 52:case 53:case 54:case 55:case 56:case 57://�����0-6�� ��51-57)
                      if(coolOccursCnt == ( m - 51)) {
                        //��ʾ��������ͼƬ
                        //Drawable redRes = getResources().getDrawable(MagicTool.getResIDbyRednum(coolOccursCnt));
                        row.addView(UIFactory.makeImageCellWithBackground(appContext.getPicCache().get("red" + coolOccursCnt), 
                            RedMissingTrendActivity.this,
                            getColorByMissValue(ShuangSeToolsSetApplication.COOL_MISS_START)),
                                                appContext.rightCellPara);
                      } else {//��ʾ��©����
                        row.addView(UIFactory.makeTextCellWithBackground(Integer.toString(appContext.getRedNumCoolOccurMissTimes(startIndex, m-51)),
                            Color.GRAY, RedMissingTrendActivity.this, getColorByMissValue(ShuangSeToolsSetApplication.COOL_MISS_START)), 
                            appContext.rightCellPara);
                      }
                      continue;
                    default:
                        break;
                }//end switch
                
            }//end for
            
            //�ұ߿�1�� - ������
            row.addView(UIFactory.makeBlankCell(RedMissingTrendActivity.this), appContext.rightCellPara);
            //�Ѹ��м���
            dataTable.addView(row, viewIndex++, appContext.rowPara);
            
            startIndex++;
        }//end while
        
        //��1�п� - 1��������
        latestId = appContext.getLoalLatestItemIDFromCache();

        row = null;
        row = new TableRow(RedMissingTrendActivity.this);
        //��0��  �ں�
        row.addView(UIFactory.makeTextCell(Integer.toString(latestId + 1), 
                Color.WHITE,
                RedMissingTrendActivity.this), 
                appContext.leftCellPara);// �ں�
        //��1-68��
        for (int m = 1; m < howManyColumns; m++) {
            switch (m) {//m��ʾ�к�
                case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                case 12:case 13:case 14:case 15:case 16: case 17: case 18: case 19: case 20: case 21:case 22:
                case 23:case 24:case 25:case 26:case 27: case 28: case 29: case 30: case 31: case 32:case 33:
                    redNum = redListOrderedByMissingTimes[m-1].val; break;
                case 34:case 42:case 50:case 58:case 67:
                    //�ָ����
                    row.addView(UIFactory.makeSeperator(RedMissingTrendActivity.this), 
                              appContext.rightCellPara);
                    continue;//continue the for loop
                                
                case 59: case 60: case 61: case 62: case 63: case 64: case 65: case 66:
                //��©0��1��2��3��4��5��6��>=7 ����©ֵ missCntOfMissValues[0-7]
                case 35: case 36:case 37:case 38:case 39:case 40:case 41: //�����0 - 6�� (35 - 41)
                case 43:case 44:case 45:case 46:case 47:case 48:case 49: //�����0-6����43-49)
                case 51:case 52:case 53:case 54:case 55:case 56:case 57://�����0-6�� ��51-57)
                    row.addView(UIFactory.makeBlankCell(RedMissingTrendActivity.this), appContext.rightCellPara);
                    continue;
                case 68://������ť
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
                  true, //����˫��ѡ��
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
        
        //������ʾ��
        viewIndex++;
        
        //��������(���ִ���,�����©�����������
        SparseArray<SummaryData> redSummaryData = appContext.getRedNumSummaryData();
        SparseArray<SummaryData> hotOccursSummaryData = appContext.getRedNumHotWarmCoolOccursSummaryData(1);
        SparseArray<SummaryData> warmOccursSummaryData = appContext.getRedNumHotWarmCoolOccursSummaryData(2);
        SparseArray<SummaryData> coolOccursSummaryData = appContext.getRedNumHotWarmCoolOccursSummaryData(3);
        
        for(int i=0; i<3; i++) {
            row = null;
            row = new TableRow(this);
            String titleStr="";
            switch(i) {
                case 0: titleStr = "���ִ���";break;
                case 1: titleStr = "�����©"; break;
                case 2: titleStr = "�������"; break;
            }
            
            //0��
            row.addView(UIFactory.makeTextCell(titleStr, 
                    Color.WHITE,
                    RedMissingTrendActivity.this), 
                    appContext.leftCellPara);
            //1 ��  ���һ��
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
                            //�ָ����
                            row.addView(UIFactory.makeSeperator(RedMissingTrendActivity.this), 
                                    appContext.rightCellPara);
                            continue;//continue the for loop
                //��©0��1��2��3��4��5��6��>=7 ����©ֵ missCntOfMissValues[0-7]
                case 59: 
                  if(i==0) {
                    dispText = "0";
                  }else if(i==1) {//���ֵ
                    dispText = "5"; 
                  }else {dispText = "";}
                  break;
                case 60:case 61:
                  if(i==0) {
                    dispText = String.valueOf(m-59);
                  }else if(i==1) {//���ֵ
                    dispText = "6"; 
                  }else {dispText = "";}
                  break;
                case 62:
                  if(i==0) {
                    dispText = "3";
                  } else if(i==1) {//���ֵ
                    dispText = "10"; 
                  }else {dispText = "";}
                  break;
                  case 63:
                    if(i==0) {
                      dispText = "4";
                    }else if(i==1) {//���ֵ
                      dispText = "14"; 
                    }else {dispText = "";}
                    break;
                  case 64:
                    if(i==0) {
                      dispText = "5";
                    } else if(i==1) {//���ֵ
                    dispText = "18"; 
                  }else {dispText = "";}
                  break;
                  case 65: 
                    if(i==0) {
                      dispText = "6";
                    } else if(i==1) {//���ֵ
                      dispText = "21"; 
                    }else {dispText = "";}
                    break;
                  case 66:
                    if(i==0) {
                      dispText = ">=7";
                    }else if(i==1) {//���ֵ
                      dispText = "1"; 
                    } else {dispText = "";}
                    break;
                    
                case 35: case 36:case 37:case 38:case 39:case 40:case 41: //�����0 - 6�� (35 - 41)
                          occursNum = m - 35;
                          summaryData = hotOccursSummaryData.get(occursNum);
                          break;
                case 43:case 44:case 45:case 46:case 47:case 48:case 49: //�����0-6����43-49)
                          occursNum = m - 43;
                          summaryData = warmOccursSummaryData.get(occursNum);
                          break;
                case 51:case 52:case 53:case 54:case 55:case 56:case 57://�����0-6�� ��51-57)
                          occursNum = m - 51;
                          summaryData = coolOccursSummaryData.get(occursNum); 
                          break;
                case 68:
                          dispText = "";
                          //���ұ߿�һ��
                          row.addView(UIFactory.makeTextCell(dispText, Color.GRAY, RedMissingTrendActivity.this),
                                  appContext.rightCellPara);
                          continue;
                  
                default:
                        dispText = "";
                        break;
                }//end switch
                
                //�����߼��Ǻ��򼰳���-��-���ܽ����ݹ�ͬʹ����ʾ
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
          if(cellData.isClicked() == 0) {//δ��������ڵ��
            if(cellData.isIfAllowDoubleClickSel() && (cellData.getCellFor() == CellData.P_FOR_SEL_RED_COMBINE)) {
                imageView.setImageDrawable(cellData.getDispImg());
                cellData.setClicked(1);
                if((cellData.getNum() > 0 ) && (cellData.getNum() < 34)) {
                  try{
                    ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().add(Integer.valueOf(cellData.getNum()));
                  }catch (Exception e) {
                    theActivity.InfoMessageBox("��ʾ", "ѡ������������ϵ���ߡ�");
                  }
                }
            } 
          } else if(cellData.isClicked() == 1){//�����1�Σ������ֵ��
                //����˫��,ѡ��Ϊ����,ȥ���ϣ����뵨
                imageView.setImageDrawable(cellData.getDoubleclickDispImg());
                cellData.setClicked(2);
                try {
                    ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().remove(Integer.valueOf(cellData.getNum()));
                    ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().add(Integer.valueOf(cellData.getNum()));
                }catch (Exception e) {
                  theActivity.InfoMessageBox("��ʾ", "ѡ�������ʱ��������ϵ���ߡ�");
                }
          } else if(cellData.isClicked() == 2){//�����2�Σ������ֵ����ȥ��Ϊ���� 
            imageView.setImageDrawable(null);
            cellData.setClicked(0);
            try {
                ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().remove(Integer.valueOf(cellData.getNum()));
                ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().remove(Integer.valueOf(cellData.getNum()));
            }catch (Exception e) {
              theActivity.InfoMessageBox("��ʾ", "ѡ�������ʱ��������ϵ���ߡ�");
            }
         } else {
            theActivity.InfoMessageBox("��ʾ", "��ת���ʱѡ���������״̬��������ϵ����2��");
        }
        }
      }
    };
    
    private interface DataLoadCompletedListener {
        public void loadComplete();
    }
    
    // ������ť��Ӧ����
    class RedOPCommonCombineBtnListener implements OnClickListener {
        public RedOPCommonCombineBtnListener() {
        }

        public void onClick(View v) {
            final CustomDefineAlertDialog dialog = new CustomDefineAlertDialog(RedMissingTrendActivity.this, "ForChooseCombineMethod");
            dialog.setTitle("��ѡ��:");
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
      // �Ƿ񴥷�����Ϊback��
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