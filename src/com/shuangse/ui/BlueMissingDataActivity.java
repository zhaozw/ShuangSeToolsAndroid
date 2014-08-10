package com.shuangse.ui;

import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.meta.CellData;
import com.shuangse.meta.SummaryData;

import android.app.Activity;
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

public class BlueMissingDataActivity extends Activity {
    private final String TAG = "BlueMissingDataActivity";
    
    private SharedPreferences sharedPreferences;
    private String fromActivity = null;
    
    private ProgressDialog progDialog;
    
    private TableLayout dataTable;
    
    private ShuangSeToolsSetApplication appContext;
    //�ں�+16����+4���ָ���+��С��2�У�+�ָ����+��ż��2�У�+�ָ����+��3������3��+�ָ����+�����䣨4�У�+�ָ����+1��������
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
        //���±���
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
        setContentView(R.layout.bluemissingdata);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_right_bluemissing);
        
//        Button returnBtn = (Button)findViewById(R.id.returnbtn);
//        returnBtn.setVisibility(View.VISIBLE);
//        Button helpBtn = (Button)findViewById(R.id.helpbtn);
//        helpBtn.setVisibility(View.VISIBLE);
//        helpBtn.setOnClickListener(new View.OnClickListener() {
//          public void onClick(View v) {
//            String htmlMsg = "��ҳ������ʾ��<br>\t һ����ҳ�����������϶����϶����ұ��·��в�����ť��" + 
//                                          "<br>\t �����������¿�Ϊ������ڳ�����ʷ�������һ�б�ʾ�ںţ�" + 
//                                          "�ӵڶ��п�ʼ����������Ϊ����1��2...��16�ĳ��ź���©�����" + 
//                                          "��������ɫ��״ͼ�α�ʾ���ڸ������г����û�ɫ���ֱ�ʾ��������©��" + 
//                                          "����δ�г�,Ҳ��������©ֵ��16�������û�ɫ���߷ָ�Ϊ�ĸ����䣬���ҷ���-С��" +
//                                          "��ż����3���� �� ����ֲ� �ֱ�ͳ����ʾ�˶�Ӧ�������������ƣ��ɸ��ݴ�4������" + 
//                                          "����Ԥ������.��������(��)��ʾ������9-16�г�������(ż)��ʾ������(2,4,6,8,10,12,14,16)�г���" +
//                                          "����(��3��1)��ʾ������4,7,10,13,16�г�������(2����)��ʾ��5��6��7��8�г����������ƣ�" + 
//                                          "<br>\t ������ͼĬ����ʾ����20�ڣ������ڡ��������->����������ʾ������" + 
//                                          "�����ø������ʾ�����Է������鿴������;" + "<br>\t�ġ���©�����·��հ��з�����ѡ������Ĳ������򣬸�������" + 
//                                          "����ĳ��������ڸ÷��������������÷�������һ����״���룬��ʾѡ�и������ٴε��ȥ��ѡ�񣨿�ѡ�������;" +
//                                          "<br>\t �塢ѡ������϶������Ҳ࣬�������ת��š��򡶵�����š���ť���ɺ���ѡ����һ�����;" + 
//                                          "<br>\t �������·��������ϳ��ִ����������©��������������зֱ�Ϊ��Ӧ����������˫ɫ�򿪽���ʷ������ͳ�Ƶ���ʷ���ݣ���ѡ�Ųο�;";
//            MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, BlueMissingDataActivity.this).show();
//          }
//        }); 
//        returnBtn.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            onBackPressed();
//          }
//        });
                
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BlueMissingDataActivity.this);
        disp_his_num = Integer.parseInt(sharedPreferences.getString("shuangse_display_blue_history_cnt", "20"));
        
        fromActivity = getIntent().getExtras().getString("FROM");
        TextView blueOperationTitle = (TextView)findViewById(R.id.blueOperationTitle);
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
          blueOperationTitle.setText("����հ׷���ѡ�ţ�Ȼ�������Ҳࡾ��ת��š���ť���.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
          blueOperationTitle.setText("����հ׷���ѡ�ţ�Ȼ�������Ҳࡾ������š���ť���.");
        } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
          blueOperationTitle.setText("����հ׷���ѡ�ţ�Ȼ�������Ҳࡾ��Ӧ�е���Ű�ť�����.");
        }
        
        progDialog = new ProgressDialog(BlueMissingDataActivity.this);
        progDialog.setTitle("��ʾ");
        progDialog.setMessage("���Եȣ����ڼ���...");
        progDialog.setCancelable(false);
        progDialog.show();
        
        dataTable = (TableLayout) findViewById(R.id.datatable);
        
        new LoadDataAsyncTask().execute("");
        
        setLoadDataComplete(new DataLoadCompletedListener() {
            @Override
            public void loadComplete() {
                //����ִ�в�������UI������ɺ���Զ�����������Ĵ��� 
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
        
        //�ں�+16��+4�ָ����+��С��2�У�+�ָ����+��ż��2�У�+�ָ����+��3������3��+�ָ����+�����䣨4�У�+�ָ����+2������
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
            //0��
            row.addView(UIFactory.makeTextCell(Integer.toString(itemId), Color.WHITE, 
                    BlueMissingDataActivity.this), 
                    appContext.leftCellPara);// �ں�
            //1-19��
            for (int m = 1; m <= 19; m++) {
                int blueNum = 0;
                switch(m) {
                case 5:
                case 10:
                case 15:
                    //�ָ����
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
            
            //20�� - �ָ����
            row.addView(UIFactory.makeSeperator(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            //��С��2�У�+ �ָ����
            int bigSmall = appContext.getBlueNumAttrValue(0, blueVal);    
            if(bigSmall == 1) {//��
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue17"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(17)),
                        BlueMissingDataActivity.this), appContext.rightCellPara);
                //��ȡ����С(0)��©����
                int missVal = appContext.getBlueNumAttrMissTimes(0, 0, itemId);                
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal), Color.GRAY, 
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            } else if(bigSmall == 0){//С
                //��ȡ�����(1)��©����
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
            
            //��ż��2�У�+�ָ����
            int evenOdd = appContext.getBlueNumAttrValue(1, blueVal);    
            if(evenOdd == 1) {//��
                row.addView(UIFactory.makeImageCell(
                        appContext.getPicCache().get("blue19"),
                        //getResources().getDrawable(MagicTool.getResIDbyBluenum(19)),
                        BlueMissingDataActivity.this), appContext.rightCellPara);
                //��ȡż��0����©����
                int missVal = appContext.getBlueNumAttrMissTimes(1, 0, itemId);                
                row.addView(UIFactory.makeTextCell(Integer.toString(missVal),
                        Color.GRAY,
                        BlueMissingDataActivity.this), 
                        appContext.rightCellPara);
            } else if(evenOdd == 0){//ż
                //��ȡ�棨1����©����
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
            //��3������3��+�ָ����
            int leftVal = appContext.getBlueNumAttrValue(2, blueVal);            
            if(leftVal == 0) {//��0
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
            } else if(leftVal == 1) {//��1
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
            } else if(leftVal == 2) {//��2
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
            
            //�����䣨4�У�+�ָ����
            int disNum = appContext.getBlueNumAttrValue(3, blueVal);
            switch (disNum) {
            case 1: //1����
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
                
            case 2://2����
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
            case 3: //3����
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
            case 4://4����
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
            
            //�ұ�1��- ������ť
            row.addView(UIFactory.makeBlankCell(BlueMissingDataActivity.this), 
                    appContext.rightCellPara);
            
            dataTable.addView(row, viewIndex++, appContext.rowPara);
            
            startIndex++;
        }//end while
        
        //��2�п� - 2��������
        int operaLine = 1;
        if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")){
          operaLine = 2;
        }
        
        latestId = appContext.getLoalLatestItemIDFromCache();
        for(int cnt = 0; cnt < operaLine; cnt++) {
            row = null;
            row = new TableRow(BlueMissingDataActivity.this);
            //��һ��
            row.addView(UIFactory.makeTextCell(Integer.toString(latestId + 1), 
                    Color.WHITE,
                    BlueMissingDataActivity.this), 
                    appContext.leftCellPara);// �ں�
            
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
                    // �ָ����
                    row.addView(UIFactory
                            .makeSeperator(BlueMissingDataActivity.this),
                            appContext.rightCellPara);
                    continue; // continue for loop
                case 1:case 2:case 3:case 4: 
                    blueNum = m;
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(blueNum));
                    blueRes = appContext.getPicCache().get("blue"+blueNum);
                    break;
                case 6:case 7:case 8:case 9:
                    blueNum = m - 1;
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(blueNum));
                    blueRes = appContext.getPicCache().get("blue"+blueNum);
                    break;
                case 11:case 12:case 13:case 14:
                    blueNum = m - 2;
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(blueNum));
                    blueRes = appContext.getPicCache().get("blue"+blueNum);
                    break;
                case 16:case 17:case 18:case 19:
                    blueNum = m - 3;            
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(blueNum));
                    blueRes = appContext.getPicCache().get("blue"+blueNum);
                    break;
                case 21:
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(17));
                    blueRes = appContext.getPicCache().get("blue17");
                    break;
                case 22:
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(18));
                    blueRes = appContext.getPicCache().get("blue18");
                    break;
                case 24:
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(19));
                    blueRes = appContext.getPicCache().get("blue19");
                    break;
                case 25:
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(20));
                    blueRes = appContext.getPicCache().get("blue20");
                    break;
                case 27:
                case 28:
                case 29:
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(m-6));
                    blueRes = appContext.getPicCache().get("blue"+(m-6));
                    break;
                case 31:
                case 32:
                case 33:
                case 34:
                    //blueRes = getResources().getDrawable(MagicTool.getResIDbyBluenum(m-9));
                    blueRes = appContext.getPicCache().get("blue"+(m-9));
                    break;
                case 36:
                {
                  if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
                      row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.smartcombinebtn), new BlueOPBtnListener(),
                          BlueMissingDataActivity.this),
                          appContext.rightCellPara);
                  } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
                      row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.dantuocombine), new BlueOPBtnListenerForDanTuo(),
                          BlueMissingDataActivity.this),
                          appContext.rightCellPara);
                  } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
                    if(cnt == 0) {
                      row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.smartcombinebtn), new BlueOPBtnListener(),
                          BlueMissingDataActivity.this),
                          appContext.rightCellPara);
                    } else if(cnt == 1) {
                      row.addView(UIFactory.makeButton(getResources().getDrawable(R.drawable.dantuocombine), new BlueOPBtnListenerForDanTuo(),
                          BlueMissingDataActivity.this),
                          appContext.rightCellPara);
                    }
                  }
                  continue;//continue the for loop
                }   
                default:
                    continue;
                }
                
                CellData blankCell = null;
                if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("SmartCombineActivity")) {
                      blankCell = new CellData(blueNum, latestId + 1,
                              0, blueRes, null, false, //���doubleclickDispImg Ϊnull,������ѡ����
                              disp_his_num + 1, m, CellData.P_FOR_SEL_BLUE_SMART_COMBINE);
                      if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().contains(Integer.valueOf(blueNum))) {
                        blankCell.setClicked(1);
                      } 
                      ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, BlueMissingDataActivity.this); 
                      row.addView(cellImage, appContext.rightCellPara);
                } else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("DantuoCombineActivity")) {
                      blankCell = new CellData(blueNum, latestId + 1,
                          0, blueRes, null, false, //���doubleclickDispImg Ϊnull,������ѡ����
                          disp_his_num + 1, m, CellData.P_FOR_SEL_BLUE_DANTUO_COMBINE);
                      if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo().contains(Integer.valueOf(blueNum))) {
                        blankCell.setClicked(1);
                      } 
                      ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, BlueMissingDataActivity.this); 
                      row.addView(cellImage, appContext.rightCellPara);
                }else if(this.fromActivity != null && this.fromActivity.equalsIgnoreCase("MainViewActivity")) {
                  if(cnt == 0) {
                      blankCell = new CellData(blueNum, latestId + 1,
                          0, blueRes, null, false, //���doubleclickDispImg Ϊnull,������ѡ����
                          disp_his_num + 1, m, CellData.P_FOR_SEL_BLUE_SMART_COMBINE);
                      if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().contains(Integer.valueOf(blueNum))) {
                        blankCell.setClicked(1);
                      } 
                      ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, BlueMissingDataActivity.this); 
                      row.addView(cellImage, appContext.rightCellPara);
                  } else if(cnt == 1) {
                    blankCell = new CellData(blueNum, latestId + 1,
                        0, blueRes, null, false, //���doubleclickDispImg Ϊnull,������ѡ����
                        disp_his_num + 1, m, CellData.P_FOR_SEL_BLUE_DANTUO_COMBINE);
                    if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo().contains(Integer.valueOf(blueNum))) {
                      blankCell.setClicked(1);
                    } 
                    ImageView cellImage = UIFactory.makeClickableBlankCell("", blankCell, cellClickListener, BlueMissingDataActivity.this); 
                    row.addView(cellImage, appContext.rightCellPara);
                  }
                }
                
            }//end for
            dataTable.addView(row, viewIndex++, appContext.rowPara);
        }
        
        //������ʾ��
        viewIndex++;
        
        //��������(���ִ���,�����©�����������
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
                case 0: titleStr = "���ִ���";break;
                case 1: titleStr = "�����©"; break;
                case 2: titleStr = "�������"; break;
            }
            
            //0��
            row.addView(UIFactory.makeTextCell(titleStr, 
                    Color.WHITE,
                    BlueMissingDataActivity.this), 
                    appContext.leftCellPara);
            //1 ��  ���һ��
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
                    //�ָ���
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
          if(cellData.isClicked() == 0) {
              imageView.setImageDrawable(cellData.getDispImg());
              cellData.setClicked(1);
              if((cellData.getNum() > 0 ) && (cellData.getNum() < 17)) {
                if(cellData.getCellFor() == CellData.P_FOR_SEL_BLUE_SMART_COMBINE) {
                  if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().add(Integer.valueOf(cellData.getNum()))) {
                    theActivity.InfoMessageBox("��ʾ", "ѡ�������������ϵ���ߡ�");
                  }
                } else if(cellData.getCellFor() == CellData.P_FOR_SEL_BLUE_DANTUO_COMBINE) {
                  if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo().add(Integer.valueOf(cellData.getNum()))) {
                    theActivity.InfoMessageBox("��ʾ", "ѡ���������������ϵ���ߡ�");
                  }
                } else {
                  theActivity.InfoMessageBox("��ʾ", "ѡ��������ʱ״̬��������ϵ���ߡ�");
                }
              }
          } else {
              imageView.setImageDrawable(null);
              cellData.setClicked(0);
              if(cellData.getCellFor() == CellData.P_FOR_SEL_BLUE_SMART_COMBINE) {
                  if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().contains(Integer.valueOf((cellData.getNum())))) {
                    if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().remove(Integer.valueOf((cellData.getNum())))) {
                      theActivity.InfoMessageBox("��ʾ", "ȥѡ���������������ϵ���ߡ�");
                    }
                  }
              } else if(cellData.getCellFor() == CellData.P_FOR_SEL_BLUE_DANTUO_COMBINE) {
                  if(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo().contains(Integer.valueOf((cellData.getNum())))) {
                    if(!ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo().remove(Integer.valueOf((cellData.getNum())))) {
                      theActivity.InfoMessageBox("��ʾ", "ȥѡ���������������ϵ���ߡ�");
                    }
                  }
              } else {
                theActivity.InfoMessageBox("��ʾ", "ȥѡ��������ʱ״̬��������ϵ���ߡ�");
              }
              
          }
        }
      }
    };
    
    private interface DataLoadCompletedListener {
        public void loadComplete();
    }
    
    //������ť��Ӧ����
    class BlueOPBtnListener implements OnClickListener {
          public void onClick(View v) {
              showProgressDialog("��ʾ", "���Ե�...");
              Log.i("BlueOPBtnListener", "Button is clicked");
              
              Intent intent = new Intent(BlueMissingDataActivity.this, SmartCombineActivity.class);
              intent.putExtra("FROM", "BlueMissingActivity");
              startActivity(intent);
          }
    }
        
    //������ť��Ӧ����
    class BlueOPBtnListenerForDanTuo implements OnClickListener {
          public void onClick(View v) {
              showProgressDialog("��ʾ", "���Ե�...");
              Log.i("BlueOPBtnListenerforDanTuo", "Button is clicked");
              
              Intent intent = new Intent(BlueMissingDataActivity.this, DantuoCombineActivity.class);
              intent.putExtra("FROM", "BlueMissingActivity");
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

    private ProgressDialog progressDialog;
    private void showProgressDialog(String title, String msg) {
      progressDialog = new ProgressDialog(BlueMissingDataActivity.this);
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
          Intent intent = new Intent(BlueMissingDataActivity.this, SmartCombineActivity.class);
          startActivity(intent);
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
