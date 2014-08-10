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
        
        //���±���
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
//            String htmlMsg = "��ҳ������ʾ��<br>\t һ����ҳ�����������϶����϶����ұ��·��в�����ť��" + 
//                                          "<br>\t �����������¿�Ϊ������ڳ�����ʷ�������һ�б�ʾ�ںţ�" + 
//                                          "�ӵڶ��п�ʼ����������Ϊ����1��2...��33�ĳ��ź���©�����" + 
//                                          "�����ú�ɫ��״ͼ�α�ʾ���ڸú����г����û�ɫ���ֱ�ʾ�ú�����©��" + 
//                                          "����δ�г�,Ҳ������©ֵ.<br>\t ������ͼĬ����ʾ����20�ڣ������ڡ��������->����������ʾ������" + 
//                                          "�����ø������ʾ�����Է������鿴������;" + "<br>\t�ġ���©�����·��հ��з�����ѡ�����Ĳ������򣬸�������" + 
//                                          "����ĳ��������ڸ÷��������������÷�������һ����״���룬��ʾѡ�иú����ٴε��ȥ��ѡ��<br>" + 
//                                          "ע�⣺�ڵڶ��� �� ������� ��Ӧ�еĿհ׷����У����һ�α�ʾѡ�У�������α�ʾѡ��Ϊ���룬�������ȥ��ѡ��;" + 
//                                          "<br>\t �塢ѡ������϶������Ҳ࣬�������ת��š��򡶵�����š���ť�����ö�Ӧ����ת������� �� ���������ѡ����;" + 
//                                          "<br>\t �������·��������ϳ��ִ����������©��������������зֱ�Ϊ��Ӧ����������˫ɫ�򿪽���ʷ������ͳ�Ƶ���ʷ���ݣ���ѡ�Ųο�;" + 
//                                          "<br>\t �ߡ�ѡ��ʱ���ɸ�����ʷ��©���ƣ���ѡ�����г�����������룬б��ͼ���룬������ȵȣ��ɸ��ݾ���ѡ��7-20��������" + 
//                                          "(�ȱ���ѡ̫����޷�������ת�����ܰ�������ʡ�ʽ�)��";
//            MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, RedMissingDataActivity.this).show();
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
          operationTextView.setText("����հ׷���ѡ�ţ�Ȼ�������Ҳࡾ��ת��š���ť���.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
          operationTextView.setText("˫�������2�Σ��հ׷���ѡΪ���룬���һ��ѡΪ����)��Ȼ�������Ҳࡾ������š���ť���.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
          operationTextView.setText("����հ׷���ѡ��(�ڶ��е������ѡΪ���룬���һ��ѡΪ����)��Ȼ�������Ҳࡾ��Ӧ�е���Ű�ť�����.");
        }
        
        progDialog = new ProgressDialog(RedMissingDataActivity.this);
        progDialog.setTitle("��ʾ");
        progDialog.setMessage("���Եȣ����ڼ���...");
        progDialog.setCancelable(false);
        progDialog.show();
        
        dataTable = (TableLayout) findViewById(R.id.datatable);        
        new LoadDataAsyncTask().execute("");
        
        setLoadDataComplete(new DataLoadCompletedListener() {
            @Override
            public void loadComplete() {
                //����ִ����Ҫ�Ĳ�������UI������ɺ���Զ�����������Ĵ��� 
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
            //��0
            row.addView(UIFactory.makeTextCell(Integer.toString(itemId), 
                    Color.WHITE,
                    RedMissingDataActivity.this),
                    appContext.leftCellPara);// �ں�
            //��1-33            
            for (int m = 1; m < 37; m++) {
                switch (m) {
                    case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                        redNum = m; break;
                    case 13:case 14:case 15:case 16:case 17: case 18: case 19: case 20: case 21: case 22:case 23:
                        redNum = m - 1; break;
                    case 25:case 26:case 27:case 28:case 29: case 30: case 31: case 32: case 33: case 34:case 35:
                        redNum = m - 2; break;
                    case 12:case 24:case 36:
                        //�ָ����
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
            
            //�ұ߿�1�� - ������
            row.addView(UIFactory.makeBlankCell(RedMissingDataActivity.this), appContext.rightCellPara);
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
            
            startIndex++;
        }
        
        //��2�п� - 2��������
        int operaLine = 1;
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")){
          operaLine = 2;
        }
        latestId = appContext.getLoalLatestItemIDFromCache();
        for(int lineNum = 0; lineNum < operaLine; lineNum++) {
            row = null;
            row = new TableRow(RedMissingDataActivity.this);
            //��0��
            row.addView(UIFactory.makeTextCell(Integer.toString(latestId + 1), 
                    Color.WHITE,
                    RedMissingDataActivity.this), 
                    appContext.leftCellPara);// �ں�        
            //��1-33��
            for (int m = 1; m < howManyColumns; m++) {
                switch (m) {
                    case 1:case 2:case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: 
                        redNum = m; break;
                    case 13:case 14:case 15:case 16:case 17: case 18: case 19: case 20: case 21: case 22:case 23:
                        redNum = m - 1; break;
                    case 25:case 26:case 27:case 28:case 29: case 30: case 31: case 32: case 33: case 34:case 35:
                        redNum = m - 2; break;
                    case 12:case 24:case 36:
                        //�ָ����
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
                        false, //������˫��ѡ��
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
                      true, //����˫��ѡ��
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
                        false, //������˫��ѡ��
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
                        true, //����˫��ѡ��
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
        ///������ʾ��
        viewIndex++;
        
        //��������(���ִ���,�����©�����������
        SparseArray<SummaryData> redSummaryData = appContext.getRedNumSummaryData();
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
                    RedMissingDataActivity.this), 
                    appContext.leftCellPara);
            //1 ��  ���һ��
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
                    //�ָ����
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
          if(cellData.isClicked() == 0) {//δ��������ڵ��
            if(!cellData.isIfAllowDoubleClickSel() && (cellData.getCellFor() == CellData.P_FOR_SEL_RED_SMART_COMBINE)) {
                //������˫��ѡ��,ѡ����ת��ŵĺ���
                imageView.setImageDrawable(cellData.getDispImg());
                cellData.setClicked(1);
                if((cellData.getNum() > 0 ) && (cellData.getNum() < 34)) {
                  if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().add(Integer.valueOf(cellData.getNum()))) {
                    theActivity.InfoMessageBox("��ʾ", "ѡ������������ϵ���ߡ�");
                  }
                }
            } else if(cellData.isIfAllowDoubleClickSel() && (cellData.getCellFor() == CellData.P_FOR_SEL_RED_DANTUO_COMBINE)){
              //����˫��ѡ��ѡ��Ϊ���� 
              imageView.setImageDrawable(cellData.getDispImg());
              cellData.setClicked(1);
              if((cellData.getNum() > 0 ) && (cellData.getNum() < 34)) {
                if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().add(Integer.valueOf(cellData.getNum()))) {
                  theActivity.InfoMessageBox("��ʾ", "ѡ������������ϵ���ߡ�");
                }
              }
            }
          } else if(cellData.isClicked() == 1){//�����1�Σ������ֵ��
              if(!cellData.isIfAllowDoubleClickSel()) {//������˫��ѡ��ѡ��ȥ��
                  imageView.setImageDrawable(null);
                  cellData.setClicked(0);
                  if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().remove(Integer.valueOf(cellData.getNum()))) {
                    theActivity.InfoMessageBox("��ʾ", "ȥѡ������������ϵ���ߡ�");
                  }
              } else {//����˫��,ѡ��Ϊ����,ȥ���ϣ����뵨
                imageView.setImageDrawable(cellData.getDoubleclickDispImg());
                cellData.setClicked(2);
                if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().remove(Integer.valueOf(cellData.getNum()))) {
                  theActivity.InfoMessageBox("��ʾ", "ȥѡ���������ʱ��������ϵ���ߡ�");
                } 
                if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().add(Integer.valueOf(cellData.getNum()))) {
                  theActivity.InfoMessageBox("��ʾ", "ѡ�������ʱ��������ϵ���ߡ�");
                }
              }
          } else if(cellData.isClicked() == 2){//�����2�Σ������ֵ����ȥ��Ϊ���� 
            imageView.setImageDrawable(null);
            cellData.setClicked(0);
            if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().remove(Integer.valueOf(cellData.getNum()))) {
              theActivity.InfoMessageBox("��ʾ", "ȥѡ��������������ϵ���ߡ�");
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
   class RedOPSmartCombineBtnListener implements OnClickListener {
    public RedOPSmartCombineBtnListener() {
    }

    public void onClick(View v) {
      showProgressDialog("��ʾ", "���Ե�...");
      Log.i("RedOPBtnListener", "Button is clicked");

      Intent intent = new Intent(RedMissingDataActivity.this, SmartCombineActivity.class);
      intent.putExtra("FROM", "RedMissingActivity");
      startActivity(intent);
   }
  }

   // ������ť��Ӧ����
   class RedOPDanTuoBtnListener implements OnClickListener {
    public RedOPDanTuoBtnListener() {
    }

    public void onClick(View v) {
      showProgressDialog("��ʾ", "���Ե�...");
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
      // �Ƿ񴥷�����Ϊback��
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