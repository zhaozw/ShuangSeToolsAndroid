package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ItemPair;
import com.shuangse.meta.ShuangseCodeItem;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SmartCombineActivity extends ExtendedActivity {
  private final static String TAG = "SmartCombineActivity";
  
  public final static int INVALID_MODEL_ID = 0;
  public final static int MIN_SEL_RED_NUMBERS = 7;
  
  public final static int M_8_S_6_G_5_4_ITEM = 1;
  public final static int M_9_S_6_G_5_7_ITEM = 2;
  public final static int M_10_S_6_G_5_14_ITEM = 3;
  public final static int M_10_S_6_G_4_3_ITEM = 4;
  public final static int M_11_S_6_G_4_5_ITEM = 5;
  public final static int M_12_S_6_G_4_6_ITEM = 6;
  public final static int M_15_S_6_G_4_19_ITEM = 7;
  public final static int M_17_S_6_G_4_33_ITEM = 8;
  public final static int M_9_S_4_G_4_12_ITEM = 9;
  public final static int   M_11_S_6_G_5_22_ITEM = 10;
  public final static int M_12_S_6_G_5_38_ITEM = 11;
  //public final static int M_13_S_6_G_5_61_ITEM = 12;
  public final static int M_13_S_6_G_4_10_ITEM = 13;
  public final static int M_14_S_6_G_4_14_ITEM = 14;
  public final static int M_16_S_6_G_4_25_ITEM = 15;
  //public final static int M_18_S_6_G_4_42_ITEM = 16;
  public final static int M_9_S_5_G_4_3_ITEM = 17;
  public final static int M_10_S_5_G_4_7_ITEM = 18;
  public final static int M_11_S_5_G_4_10_ITEM = 19;
  public final static int M_12_S_5_G_4_14_ITEM = 20;
  public final static int M_7_S_4_G_4_5_ITEM = 21;
  public final static int M_8_S_4_G_4_7_ITEM = 22;
  public final static int M_10_S_4_G_4_20_ITEM = 23;
  //public final static int M_11_S_4_G_4_32_ITEM = 24;
  //public final static int M_12_S_4_G_4_41_ITEM = 25;
  
  public final static int M_TOTAL_COMBINE = 9999;//ȫ��ʽ���
  public final static int M_DAN_TUO_COMBINE = 9998;//�������
  
  private ShuangSeToolsSetApplication appContext;
  private SharedPreferences sharedPreferences;
  private boolean ifGetOutOfHistoryitem = true;
  private boolean ifCheckRedSum = true;
  
  public static List<ItemPair> allModels;
  static {
    allModels = new ArrayList<ItemPair>();
    initializeCombineModels(allModels);
  };
  
  private static void initializeCombineModels(List<ItemPair> allCombineModels) {
    allCombineModels.clear();
    allCombineModels.add(new ItemPair(INVALID_MODEL_ID, "���ѡ��ģʽ"));
    allCombineModels.add(new ItemPair(M_TOTAL_COMBINE, "��6��6(��ʽ)�����ѡ����"));
    
    allCombineModels.add(new ItemPair(M_8_S_6_G_5_4_ITEM, "8������ - ��6��5 - 4ע"));
    allCombineModels.add(new ItemPair(M_9_S_6_G_5_7_ITEM, "9������ - ��6��5 - 7ע"));
    allCombineModels.add(new ItemPair(M_10_S_6_G_5_14_ITEM, "10������ - ��6��5 - 14ע"));
    allCombineModels.add(new ItemPair(M_11_S_6_G_5_22_ITEM, "11������ - ��6��5 - 22ע"));
    allCombineModels.add(new ItemPair(M_12_S_6_G_5_38_ITEM, "12������ - ��6��5 - 38ע"));
    //allCombineModels.add(new ItemPair(M_13_S_6_G_5_61_ITEM, "13������ - ��6��5 - 61ע"));
    
    allCombineModels.add(new ItemPair(M_10_S_6_G_4_3_ITEM, "10������ - ��6��4 - 3ע"));
    allCombineModels.add(new ItemPair(M_11_S_6_G_4_5_ITEM, "11������ - ��6��4 - 5ע"));
    allCombineModels.add(new ItemPair(M_12_S_6_G_4_6_ITEM, "12������ - ��6��4 - 6ע"));
    allCombineModels.add(new ItemPair(M_13_S_6_G_4_10_ITEM, "13������ - ��6��4 - 10ע"));
    allCombineModels.add(new ItemPair(M_14_S_6_G_4_14_ITEM, "14������ - ��6��4 - 14ע"));
    allCombineModels.add(new ItemPair(M_15_S_6_G_4_19_ITEM, "15������ - ��6��4 - 19ע"));
    allCombineModels.add(new ItemPair(M_16_S_6_G_4_25_ITEM, "16������ - ��6��4 - 25ע"));
    allCombineModels.add(new ItemPair(M_17_S_6_G_4_33_ITEM, "17������ - ��6��4 - 33ע"));
    //allCombineModels.add(new ItemPair(M_18_S_6_G_4_42_ITEM, "18������ - ��6��4 - 42ע"));
    
    allCombineModels.add(new ItemPair(M_9_S_5_G_4_3_ITEM, "9������ - ��5��4 - 3ע"));
    allCombineModels.add(new ItemPair(M_10_S_5_G_4_7_ITEM, "10������ - ��5��4 - 7ע"));
    allCombineModels.add(new ItemPair(M_11_S_5_G_4_10_ITEM, "11������ - ��5��4 - 10ע"));
    allCombineModels.add(new ItemPair(M_12_S_5_G_4_14_ITEM, "12������ - ��5��4 - 14ע"));
    
    allCombineModels.add(new ItemPair(M_7_S_4_G_4_5_ITEM, "7������ - ��4��4 - 5ע"));
    allCombineModels.add(new ItemPair(M_8_S_4_G_4_7_ITEM, "8������ - ��4��4 - 7ע"));
    allCombineModels.add(new ItemPair(M_9_S_4_G_4_12_ITEM, "9������ - ��4��4 - 12ע"));
    allCombineModels.add(new ItemPair(M_10_S_4_G_4_20_ITEM, "10������ - ��4��4 - 20ע"));
    //allCombineModels.add(new ItemPair(M_11_S_4_G_4_32_ITEM, "11������ - ��4��4 - 32ע"));
    //allCombineModels.add(new ItemPair(M_12_S_4_G_4_41_ITEM, "12������ - ��4��4 - 41ע"));
    
  }
  
  public static int getPosIndexByModelID(int modelID) {
    int pos = 0;
    for(ItemPair item : allModels) {
      if (item.getItemVal() == modelID) {
        break;
      }
      pos++;
    }
    if(pos == allModels.size()) {
      return 0;
    } else {
      return pos;
    }
  }
  
  public static String getModelStrByModelID(int model) {
    if(model == SmartCombineActivity.M_DAN_TUO_COMBINE) {
      return "������Ϻ���";
    }
    for(ItemPair item : allModels) {
      if (item.getItemVal() == model) {
        return item.getDispText();
      }
    }
    return "";
  }
  
  private ArrayAdapter<ItemPair> allModelsAdaptor;
  private ArrayAdapter<String> allitemIDAdaptor;
  
  //2��spinner
  private Spinner spinner_combine_model;
  private Button select_red_model_btn;
  private Button select_blue_model_btn;
  private Spinner item_id_spinner;
  private List<String> itemIDs;
  private List<ShuangseCodeItem> allHisData;
  private int currentSelItemId;
  
  //��ҳһ��2��Spinner
  private boolean ifItemIDSpinnerInitilized = false;
  private boolean ifSelectModelSpinnerInitilized = false;
  
  private int currentCombineItemId;
  private int currentSelModelId;
  
  private TextView selRedTextView;
  private ArrayList<Integer> currentSelRedList = new ArrayList<Integer>();
  private TextView selBlueTextView;
  private ArrayList<Integer> currentSelBlueList = new ArrayList<Integer>();
  
  private Button startCombineButton;
  private Button verifyRedButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appContext = (ShuangSeToolsSetApplication)getApplication();
    //���±���
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.activity_smart_combine);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.title_activity_smart_combine);
    
    //ѡ����һ�� (��һ�ڣ�
    this.currentCombineItemId = ShuangSeToolsSetApplication.getCurrentSelection().getItemId();
    
    //�Ȱ����ںż���
    itemIDs = new ArrayList<String>();
    itemIDs.add(Integer.toString(this.currentCombineItemId));
    
    allHisData = appContext.getAllHisData();
    if (allHisData != null && allHisData.size() > 1) {
        int maxSize = (allHisData.size() - 1);
        int endSize = (maxSize - 50); // Ĭ��ֻ���Ի�����֤50��
        for (int i = maxSize; i >= endSize; i--) {
            itemIDs.add(Integer.toString(allHisData.get(i).id));
        }
        allitemIDAdaptor = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemIDs);
        allitemIDAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    item_id_spinner = (Spinner)findViewById(R.id.combine_itemid);
    item_id_spinner.setAdapter(allitemIDAdaptor);
    //Ĭ��ѡ���һ������
    item_id_spinner.setSelection(0);
    this.currentSelItemId = this.currentCombineItemId;
    item_id_spinner.setOnItemSelectedListener(new ItemIDSpinnerOnSelectedListener());
    
    selRedTextView = (TextView)findViewById(R.id.combine_selred_str);
    
    selBlueTextView = (TextView)findViewById(R.id.combine_selblue_str);    
    verifyRedButton = (Button)findViewById(R.id.combine_verify_red_btn);
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SmartCombineActivity.this);
    ifGetOutOfHistoryitem = sharedPreferences.getBoolean("getout_his_item", true);
    ifCheckRedSum = sharedPreferences.getBoolean("check_sum_combine", true);
        
    select_blue_model_btn = (Button) findViewById(R.id.recommend_blue_spin);
    select_blue_model_btn.setOnClickListener(new BlueButtonOnClickListener());
    
    select_red_model_btn = (Button) findViewById(R.id.recommend_red_model);
    select_red_model_btn.setOnClickListener(new RedButtonOnClickListener());
    
    spinner_combine_model = (Spinner) findViewById(R.id.combine_model);
    allModelsAdaptor = new ArrayAdapter<ItemPair>(this, android.R.layout.simple_spinner_item, allModels);  
    allModelsAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    spinner_combine_model.setAdapter(allModelsAdaptor);
    spinner_combine_model.setOnItemSelectedListener(new CombineModelSpinner());
    
    startCombineButton = (Button)findViewById(R.id.combine_start_btn);
    startCombineButton.setOnClickListener(new CombineButtonListener());
  }
    
  
  private class CombineButtonListener implements View.OnClickListener {
      @Override
      public void onClick(View v) {
        Log.i(TAG, "currentCombineItemId:" + currentCombineItemId);
        Log.i(TAG, "currentSelModelId:" + currentSelModelId);
        Log.i(TAG, "SelectedRedNumbers:" + currentSelRedList);
        Log.i(TAG, "SelectedBlueNumbers:" + currentSelBlueList);
        if(currentSelModelId == INVALID_MODEL_ID) {
          InfoMessageBox("��ʾ", "��ѡ�����ģʽ");
          return;
        }
        if(currentSelRedList.size() < MIN_SEL_RED_NUMBERS) {
          InfoMessageBox("��ʾ", "��ѡ������" + MIN_SEL_RED_NUMBERS + "������������.");
          return;
        }
        if(currentSelBlueList.size() < 1) {
          InfoMessageBox("��ʾ", "��ѡ������1������.");
          return;
        }
        
        //ƥ���� ģʽ�Ƿ��ѡ�ĺ�����һ��
        int redCnt = currentSelRedList.size();
        switch (currentSelModelId) {
        case M_TOTAL_COMBINE: //ȫ��ʽ
          if(redCnt < MIN_SEL_RED_NUMBERS || redCnt > 20) {
            InfoMessageBox("����", "��ʽ���ֻ��ѡ��7 - 20����Ž������.");
            return;
          }
          if(redCnt > 15) {
             InfoMessageBoxWithCancel("����", "�Դ���15����Ž���ȫ��ʽ��ţ�����ᳬ��5000ע���룬��ȷ������ֻ�����������Ҫ��");
          }
          break;
        case M_7_S_4_G_4_5_ITEM:
          if(redCnt != 7) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_8_S_4_G_4_7_ITEM:
        case M_8_S_6_G_5_4_ITEM:
          if(redCnt != 8) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_9_S_6_G_5_7_ITEM:
        case M_9_S_5_G_4_3_ITEM:
        case M_9_S_4_G_4_12_ITEM:
          if(redCnt != 9) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_10_S_4_G_4_20_ITEM:
        case M_10_S_6_G_5_14_ITEM:
        case M_10_S_5_G_4_7_ITEM:
        case M_10_S_6_G_4_3_ITEM:
          if(redCnt != 10) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_11_S_6_G_4_5_ITEM:
        //case M_11_S_4_G_4_32_ITEM:
        case M_11_S_5_G_4_10_ITEM:
        case M_11_S_6_G_5_22_ITEM:
          if(redCnt != 11) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_12_S_6_G_4_6_ITEM:
        case M_12_S_5_G_4_14_ITEM:
        //case M_12_S_4_G_4_41_ITEM:
        case M_12_S_6_G_5_38_ITEM:
          if(redCnt != 12) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        //case M_13_S_6_G_5_61_ITEM:
        case M_13_S_6_G_4_10_ITEM:
          if(redCnt != 13) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_14_S_6_G_4_14_ITEM:
          if(redCnt != 14) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_15_S_6_G_4_19_ITEM:
          if(redCnt != 15) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_16_S_6_G_4_25_ITEM:
          if(redCnt != 16) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
        case M_17_S_6_G_4_33_ITEM:
          if(redCnt != 17) {
            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
            return;
          }
          break;
//        case M_18_S_6_G_4_42_ITEM:
//          if(redCnt != 18) {
//            InfoMessageBox("����", "���ģʽ����ѡ�ĺ�Ÿ�����ƥ��.");
//            return;
//          }
//          break;
        }
        
        /* ��Ų���ʾ���, �������ݶ��Ѿ�������� */
        int[] selRedNumbers = new int[currentSelRedList.size()];
        int x=0;
        for(Integer itm:currentSelRedList) {
          selRedNumbers[x++] = itm.intValue();
        }
        
        ArrayList<ShuangseCodeItem> allCombinedCodes = new ArrayList<ShuangseCodeItem>();
        
        for(Integer itm:currentSelBlueList) {
          int blue = itm.intValue();
          ArrayList<ShuangseCodeItem> oneBlueCombinedCodes = new ArrayList<ShuangseCodeItem>();
          int ret = appContext.combineCodes(currentSelModelId, selRedNumbers,
                                0, ifGetOutOfHistoryitem, ifCheckRedSum, 
                                currentCombineItemId, blue, oneBlueCombinedCodes);
          
          if( ret == -1) {
            InfoMessageBox("��ʾ", "��ѡ����̫���л��ɢ��������ͳ���н�ģʽ��������ѡ���Ա����˷�Ǯ��.");
            return;
          }
          if( ret == -2) {
            InfoMessageBox("��ʾ", "��Ÿ�������ѡ������ģʽ��ƥ�䣬������ѡ��.");
            return;
          }
          //success
          allCombinedCodes.addAll(oneBlueCombinedCodes);
        }//end for selBuleList
        
        //ת����ʾ���ҳ��
        showProgressDialog("��ʾ", "���Ե�...");
        Intent intent =  new Intent(SmartCombineActivity.this, SmartCombineResultActivity.class);
        
        Bundle bundle = new Bundle();
        
        bundle.putInt("ItemId", currentCombineItemId);
        bundle.putInt("ModelID", currentSelModelId);
        bundle.putIntegerArrayList("SelectRed", currentSelRedList);
        bundle.putIntegerArrayList("SelectBlue", currentSelBlueList);
        if(ifGetOutOfHistoryitem) {
          bundle.putInt("IfGetOutHis", 1);
        } else {
          bundle.putInt("IfGetOutHis", 0);
        }
        
        bundle.putParcelableArrayList("ResultCodes", allCombinedCodes);
        intent.putExtras(bundle);
        startActivity(intent);
      }
  };
    
  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(SmartCombineActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        }).create();
    notifyDialog.show();
  }
  
  private void InfoMessageBoxWithCancel(String title, String msg) {
      AlertDialog notifyDialog = new AlertDialog.Builder(SmartCombineActivity.this)
          .setTitle(title).setMessage(msg)
          .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
          }).setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }).create();
      notifyDialog.show();
    }
  
  @Override
  public void refleshRedAndBlueSeleciton() {
      StringBuffer redSb = new StringBuffer();
      
      //���Ϻ�� �� �쵨 ������Cache
      currentSelRedList.clear();
      //���ŷ���ǰ��
      currentSelRedList.addAll(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers());
      if(currentSelRedList.size() > 0) {
          redSb.append("�쵨��:");
          for(Integer item : currentSelRedList) {
              if(item < 10) {
                redSb.append("0");
              }
              redSb.append(item);
              redSb.append(" ");
          }
      }
      ArrayList<Integer> tmpRedNum = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
      if(tmpRedNum.size() > 0) {
          redSb.append("���:");
          for(Integer item : tmpRedNum) {
              if(item < 10) {
                redSb.append("0");
              }
              redSb.append(item);
              redSb.append(" ");
          }
      }
      currentSelRedList.addAll(tmpRedNum);

      redSb.append("��" + currentSelRedList.size() + "��");
      selRedTextView.setText(redSb.toString());
      //��ʾ��ѯ������ʷ���������ť
      if(currentSelRedList.size() > 0) {
        verifyRedButton.setVisibility(View.VISIBLE);
        verifyRedButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
           // String resultText = appContext.countHistoryOutDetailsForRedset(currentSelRedList);          
            custDialog = customInfoMsgBox("��������ʷ�������ͳ�����£�");
            custDialog.show();
          }
        });
      } else {
        verifyRedButton.setVisibility(View.INVISIBLE);
      }

      StringBuffer blueSb = new StringBuffer();
      currentSelBlueList.clear();
      currentSelBlueList.addAll(ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers());
      //Collections.sort(currentSelBlueList);
      for(Integer item : currentSelBlueList) {
        if(item < 10) {
          blueSb.append("0");
        }
        blueSb.append(item);
        blueSb.append(" ");
      }
      
      blueSb.append("��" + currentSelBlueList.size() + "��");
      selBlueTextView.setText(blueSb.toString());
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume()");
    
    currentSelModelId =ShuangSeToolsSetApplication.getCurrentSelection().getSelectedModelId();  
    spinner_combine_model.setSelection(SmartCombineActivity.getPosIndexByModelID(currentSelModelId));
  }

  private Dialog custDialog = null;
  public Dialog customInfoMsgBox(String title) {
    AlertDialog.Builder builder = new AlertDialog.Builder(SmartCombineActivity.this);
    LayoutInflater inflater = SmartCombineActivity.this.getLayoutInflater();
    
    View dialogView = inflater.inflate(R.layout.custscrollspinnerdialog, null);
    final TextView text = (TextView) dialogView.findViewById(R.id.dispText);
    
    Spinner spinner_selection_his_cnt = (Spinner) dialogView.findViewById(R.id.spinner_selection_his_cnt);
    List<ItemPair> allHisCnt = new ArrayList<ItemPair>();
    int totalCnt = appContext.getAllHisData().size();
    
    int startCnt = totalCnt;
    while(startCnt >1) {
      allHisCnt.add(new ItemPair(startCnt, "��"+startCnt+"��"));
      startCnt -=10;
      if(startCnt <= 1) {
        allHisCnt.add(new ItemPair(1, "��1��"));
        break;
      }
    }
    
    ArrayAdapter<ItemPair> selection_his_adaptor = new ArrayAdapter<ItemPair>(this, android.R.layout.simple_spinner_item, allHisCnt);
    selection_his_adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    spinner_selection_his_cnt.setAdapter(selection_his_adaptor);
    spinner_selection_his_cnt.setOnItemSelectedListener( new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        int selLastHisCnt = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
        String textMsg = appContext.countHistoryOutDetailsForRedset(currentSelRedList, selLastHisCnt);
        text.setText(textMsg);
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });
    
    builder.setView(dialogView)
        .setTitle(title)
        .setPositiveButton(R.string.OK,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int id) {
                if(dialog != null) dialog.dismiss();
                custDialog = null;
              }
            });
    return builder.create();
  }
  
  private ProgressDialog progressDialog;
  @Override
  public void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(SmartCombineActivity.this);
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
    // �Ƿ񴥷�����Ϊback��
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      Intent intent = new Intent(SmartCombineActivity.this,
          MainViewActivity.class);
      startActivity(intent);
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
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
  
  private class ItemIDSpinnerOnSelectedListener implements OnItemSelectedListener {
      public ItemIDSpinnerOnSelectedListener() {
          super();
          ifItemIDSpinnerInitilized = false;
      }
      @Override
      public void onItemSelected(AdapterView<?> parent,
              View view, int pos, long id) {
          if(!ifItemIDSpinnerInitilized) {
              //this is triggered by onCreate;
              ifItemIDSpinnerInitilized = true;
          } else {
              Log.i(TAG, "OnItemSelectedListener : "
                      + parent.getItemAtPosition(pos).toString());
              currentSelItemId = Integer.parseInt(parent.getItemAtPosition(pos).toString());
              currentCombineItemId = currentSelItemId;
          }
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
          Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
      }
  };
  
  private class BlueButtonOnClickListener implements OnClickListener {
    public BlueButtonOnClickListener(){
        super();
    }
      
    @Override
    public void onClick(View arg0) {
        final CustomDefineAlertDialog dialog = new CustomDefineAlertDialog(SmartCombineActivity.this, "ForChooseSmartCombineBlueMethod");
        dialog.setTitle("��ѡ�����ѡ���ţ�");
    } 
  };
  
  private class RedButtonOnClickListener implements OnClickListener {
    public RedButtonOnClickListener() {
        super();
    }
    
    @Override
    public void onClick(View v) {
        final CustomDefineAlertDialog dialog = new CustomDefineAlertDialog(SmartCombineActivity.this, "ForChooseSmartCombineRedMethod");
        dialog.setTitle("��ѡ�����ѡ��ţ�");
    }
  };
  
  private class CombineModelSpinner implements OnItemSelectedListener {
      public CombineModelSpinner() {
          super();
          ifSelectModelSpinnerInitilized = false;
      }
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
          Log.i(TAG, "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString());
          
          if(!ifSelectModelSpinnerInitilized) {
            //this is triggered by onCreate;
            ifSelectModelSpinnerInitilized = true;
          } else {//User click
            currentSelModelId = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
            ShuangSeToolsSetApplication.getCurrentSelection().setSelectedModelId(currentSelModelId);
          }
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
          Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
      }
  };
  
}
