package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.util.MagicTool;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class DantuoCombineActivity extends Activity {
  private final static String TAG = "DantuoCombineActivity";
  
  private ShuangSeToolsSetApplication appContext;
  private SharedPreferences sharedPreferences;
  private boolean ifGetOutOfHistoryitem = true; 
  private int currentCombineItemId;

  private TextView selRedDanTextView;
  private TextView selRedTuoTextView;
  private ArrayList<Integer> currentSelRedDanList;
  private ArrayList<Integer> currentSelRedTuoList;
  private ArrayList<Integer> currentSelTotalRedList;
  private TextView selBlueTextView;
  private ArrayList<Integer> currentSelBlueList;
  
  private Button selRedButton;
  private Button selBlueButton;
  private Button startCombineButton;
  private Button verifyRedButton;
  
  public static class ItemPair {
    private String dispText;
    private int itemVal;
    
    @Override
    public String toString() {
      return dispText;
    }
    
    public ItemPair(int itemVal, String dispText) {
      this.setDispText(dispText);
      this.setItemVal(itemVal);
    }

    public String getDispText() {
      return dispText;
    }

    public void setDispText(String dispText) {
      this.dispText = dispText;
    }

    public int getItemVal() {
      return itemVal;
    }

    public void setItemVal(int itemVal) {
      this.itemVal = itemVal;
    }  
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appContext = (ShuangSeToolsSetApplication)getApplication();
    //���±���
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.dantuocombine);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_dantuo_combine);
    final TextView currentCombineItemIdTextView = (TextView) findViewById(R.id.combine_itemid);
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "��ҳ������ʾ��<br>\t һ���ں� ��ʾ������һ�ڼ���������ڣ����ɸı䣬����Զ������" + 
                                      "<br>\t ������� ��һ�� ��Ӧ��ť�������������©����ͼ����ѡ�ţ�ѡ����ɺ󣬱�ҳ����ʾ��ѡ���룬���� ��ѯ������� ����ʷ���������" + 
                                      "<br>\t������� �ڶ��� ��Ӧ��ť��������������©��ͼ����ѡ�ţ���ѡ������򣩣�" +
                                      "<br>\t�ġ���ҳҲ��ͨ����©����ͼ �� ��������ͼ�� �����������š���ť���룻" +
                                      "<br>\t�塢ѡ�ú��򵨡����룬������󣬵�����·���������š���ť������Ϻ��롣";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, DantuoCombineActivity.this).show();
      }
    });
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    selRedDanTextView = (TextView)findViewById(R.id.combine_selred_dan);
    selRedTuoTextView = (TextView)findViewById(R.id.combine_selred_tuo);
    selBlueTextView = (TextView)findViewById(R.id.combine_selblue_str);
    
    selRedButton = (Button)findViewById(R.id.combine_sel_red_btn);
    verifyRedButton = (Button)findViewById(R.id.combine_verify_red_btn);
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DantuoCombineActivity.this);
    ifGetOutOfHistoryitem = sharedPreferences.getBoolean("getout_his_item", true);
    
    //ѡ����һ��
    this.currentCombineItemId = ShuangSeToolsSetApplication.getCurrentSelection().getItemId(); 
    currentCombineItemIdTextView.setText(String.valueOf(this.currentCombineItemId));
    
    selRedButton.setOnClickListener(new View.OnClickListener () {
      @Override
      public void onClick(View v) {
        showProgressDialog("��ʾ", "���Ե�...");
        Intent intent = new Intent(DantuoCombineActivity.this,
            RedMissingDataActivity.class);
        intent.putExtra("FROM", "DantuoCombineActivity");
        startActivity(intent);
      }
    });
    
    selBlueButton = (Button)findViewById(R.id.combine_sel_blue_btn);
    selBlueButton.setOnClickListener(new View.OnClickListener () {
      @Override
      public void onClick(View v) {
        showProgressDialog("��ʾ", "���Ե�...");
        Intent intent = new Intent(DantuoCombineActivity.this,
            BlueMissingDataActivity.class);
        intent.putExtra("FROM", "DantuoCombineActivity");
        startActivity(intent);
      }
    });
    
    startCombineButton = (Button)findViewById(R.id.combine_start_btn);
    startCombineButton.setOnClickListener(new View.OnClickListener () {
      @Override
      public void onClick(View v) {
        Log.i(TAG, "currentCombineItemId:" + currentCombineItemId);
        Log.i(TAG, "SelectedBlueNumbers:" + currentSelBlueList);
        Log.i(TAG, "Dan Red:" + currentSelRedDanList);
        Log.i(TAG, "Tuo Red:" + currentSelRedTuoList);
        Log.i(TAG, "Total Red:" + currentSelTotalRedList);

        if(currentSelRedDanList.size() < 1 || currentSelRedDanList.size() > 6) {
          InfoMessageBox("����", "������������ԣ���ѡ�������Ϊ1-6��.");
          return;
        }
        
        if(currentSelRedTuoList.size() > 20) {
          InfoMessageBox("����", "��������������ԣ���ѡ������1-20��.");
          return;
        }
        
        if(currentSelTotalRedList.size() < 6 || currentSelTotalRedList.size() > 20) {
          InfoMessageBox("����", "����������ԣ����� �� ����һ������6-20��.");
          return;
        }
        
        if(currentSelBlueList.size() < 1) {
          InfoMessageBox("��ʾ", "��ѡ������1������.");
          return;
        }
                
        /* ��Ų���ʾ���, �������ݶ��Ѿ�������� */
        int[] selRedNumbers = new int[currentSelTotalRedList.size()];
        int x=0;
        for(Integer itm:currentSelTotalRedList) {
          selRedNumbers[x++] = itm.intValue();
        }
        int[] redDanNumbers = new int[currentSelRedDanList.size()];
        int y = 0;
        for(Integer itm : currentSelRedDanList) {
          redDanNumbers[y++] = itm.intValue();
        }
        
        ArrayList<ShuangseCodeItem> allCombinedCodes = new ArrayList<ShuangseCodeItem>();
        
        for(Integer itm:currentSelBlueList) {
          int blue = itm.intValue();
          ArrayList<ShuangseCodeItem> oneBlueCombinedCodes = new ArrayList<ShuangseCodeItem>();
          appContext.combineCodes(selRedNumbers, selRedNumbers.length,
                                               ifGetOutOfHistoryitem, redDanNumbers, currentSelRedDanList.size(), 6, 
                                               currentCombineItemId, blue, oneBlueCombinedCodes);
          //success
          allCombinedCodes.addAll(oneBlueCombinedCodes);
        }//end for
        
        //ת����ʾ���ҳ��
        showProgressDialog("��ʾ", "���Ե�...");
        Intent intent =  new Intent(DantuoCombineActivity.this, DantuoCombineResultActivity.class);
        
        Bundle bundle = new Bundle();
        
        bundle.putInt("ItemId", currentCombineItemId);
        bundle.putIntegerArrayList("SelectRedDan", currentSelRedDanList);
        bundle.putIntegerArrayList("SelectRedTuo", currentSelRedTuoList);
        bundle.putIntegerArrayList("SelectBlue", currentSelBlueList);
        bundle.putInt("ModelID", SmartCombineActivity.M_DAN_TUO_COMBINE);
        if(ifGetOutOfHistoryitem) {
          bundle.putInt("IfGetOutHis", 1);
        } else {
          bundle.putInt("IfGetOutHis", 0);
        }
        
        bundle.putParcelableArrayList("ResultCodes", allCombinedCodes);
        intent.putExtras(bundle);
        
        startActivity(intent);
      }
    });
    
  }
    
  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(DantuoCombineActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        }).create();
    notifyDialog.show();
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume()");
    StringBuffer redSb = new StringBuffer("�����룺");
    currentSelRedDanList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers();
    currentSelRedTuoList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers();
    currentSelTotalRedList = MagicTool.join(currentSelRedDanList, currentSelRedTuoList);
    ShuangSeToolsSetApplication.getCurrentSelection().setSelectedModelId(SmartCombineActivity.M_DAN_TUO_COMBINE);
    
    //Collections.sort(currentSelRedDanList);
    for(Integer item : currentSelRedDanList) {
      if(item < 10) {
        redSb.append("0");
      }
      redSb.append(item);
      redSb.append(" ");
    }
    redSb.append("��" + currentSelRedDanList.size() + "������");
    selRedDanTextView.setText(redSb.toString());

    redSb = new StringBuffer("�������룺");
    //Collections.sort(currentSelRedTuoList);
    for(Integer item : currentSelRedTuoList) {
      if(item < 10) {
        redSb.append("0");
      }
      redSb.append(item);
      redSb.append(" ");
    }
    redSb.append("��" + currentSelRedTuoList.size() + "������");
    selRedTuoTextView.setText(redSb.toString());

    //��ʾ��ѯ���к��򣨵���+���룩��ʷ���������ť
    if(currentSelRedDanList.size() > 0) {
      verifyRedButton.setVisibility(View.VISIBLE);
      verifyRedButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         // String resultText = appContext.countHistoryOutDetailsForRedset(currentSelRedList);          
          custDialog = customInfoMsgBox("����+�����������ʷ�������ͳ�����£�");
          custDialog.show();
        }
      });
    } else {
      verifyRedButton.setVisibility(View.INVISIBLE);
    }

    StringBuffer blueSb = new StringBuffer();
    currentSelBlueList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo();
    //Collections.sort(currentSelBlueList);
    for(Integer item : currentSelBlueList) {
      if(item < 10) {
        blueSb.append("0");
      }
      blueSb.append(item);
      blueSb.append(" ");
    }
    
    blueSb.append("��" + currentSelBlueList.size() + "������");
    selBlueTextView.setText(blueSb.toString());
  }

  private Dialog custDialog = null;
  public Dialog customInfoMsgBox(String title) {
    AlertDialog.Builder builder = new AlertDialog.Builder(DantuoCombineActivity.this);
    LayoutInflater inflater = DantuoCombineActivity.this.getLayoutInflater();
    
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
        String textMsg = appContext.countHistoryOutDetailsForRedset(currentSelTotalRedList, selLastHisCnt);
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
  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(DantuoCombineActivity.this);
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
      Intent intent = new Intent(DantuoCombineActivity.this,
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
}
