package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ItemPair;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.util.MagicTool;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class DantuoCombineActivity extends ExtendedActivity {
  private final static String TAG = "DantuoCombineActivity";
  
  private ShuangSeToolsSetApplication appContext;
  private SharedPreferences sharedPreferences;
  private boolean ifGetOutOfHistoryitem = true; 
  private int currentCombineItemId;
  private Spinner item_id_spinner;
  private int currentSelItemId;
  private List<String> itemIDs;
  private List<ShuangseCodeItem> allHisData;
  private boolean ifItemIDSpinnerInitilized = false;
  private ArrayAdapter<String> allitemIDAdaptor;

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
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appContext = (ShuangSeToolsSetApplication)getApplication();
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.dantuocombine);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_dantuo_combine);
    
//    Button returnBtn = (Button)findViewById(R.id.returnbtn);
//    returnBtn.setVisibility(View.VISIBLE);
//    Button helpBtn = (Button)findViewById(R.id.helpbtn);
//    helpBtn.setVisibility(View.VISIBLE);
//    helpBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        String htmlMsg = "本页操作提示：<br>\t 一、期号 显示的是下一期即将购买的期，不可改变，软件自动算出；" + 
//                                      "<br>\t 二、点击 第一步 对应按钮，即进入红球遗漏走势图进行选号，选号完成后，本页会显示所选号码，并可 查询该组号吗 的历史出号情况；" + 
//                                      "<br>\t三、点击 第二步 对应按钮，即进入篮球遗漏势图进行选号（可选多个篮球）；" +
//                                      "<br>\t四、本页也可通过遗漏走势图 或 冷热走势图中 点击《胆拖组号》按钮进入；" +
//                                      "<br>\t五、选好红球胆、拖码，和篮球后，点击最下方《胆拖组号》按钮即可组合号码。";
//        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, DantuoCombineActivity.this).show();
//      }
//    });
//    returnBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        onBackPressed();
//      }
//    });
    
    selRedDanTextView = (TextView)findViewById(R.id.combine_selred_dan);
    selRedTuoTextView = (TextView)findViewById(R.id.combine_selred_tuo);
    selBlueTextView = (TextView)findViewById(R.id.combine_selblue_str);
    
    selRedButton = (Button)findViewById(R.id.combine_sel_red_btn);
    verifyRedButton = (Button)findViewById(R.id.combine_verify_red_btn);
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DantuoCombineActivity.this);
    ifGetOutOfHistoryitem = sharedPreferences.getBoolean("getout_his_item", true);
    
  //选择哪一期 (下一期）
    this.currentCombineItemId = ShuangSeToolsSetApplication.getCurrentSelection().getItemId();
    
    //先把下期号加入
    itemIDs = new ArrayList<String>();
    itemIDs.add(Integer.toString(this.currentCombineItemId));
    
    allHisData = appContext.getAllHisData();
    if (allHisData != null && allHisData.size() > 1) {
        int maxSize = (allHisData.size() - 1);
        int endSize = (maxSize - 50); // 默认只可以回退验证50期
        for (int i = maxSize; i >= endSize; i--) {
            itemIDs.add(Integer.toString(allHisData.get(i).id));
        }
        allitemIDAdaptor = new ArrayAdapter<String>(this,
                R.layout.spinnerformat, itemIDs);
        allitemIDAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    item_id_spinner = (Spinner)findViewById(R.id.combine_itemid);
    item_id_spinner.setAdapter(allitemIDAdaptor);
    //默认选择第一个下期
    item_id_spinner.setSelection(0);
    this.currentSelItemId = this.currentCombineItemId;
    item_id_spinner.setOnItemSelectedListener(new ItemIDSpinnerOnSelectedListener());
    
    selRedButton.setOnClickListener(new View.OnClickListener () {
      @Override
      public void onClick(View v) {
        showProgressDialog("提示", "请稍等...");
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
        showProgressDialog("提示", "请稍等...");
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
          InfoMessageBox("错误", "红号胆码个数不对，请选择红号胆码为1-6个.");
          return;
        }
        
        if(currentSelRedTuoList.size() > 20) {
          InfoMessageBox("错误", "红号拖码个数不对，请选择托码1-20个.");
          return;
        }
        
        if(currentSelTotalRedList.size() < 6 || currentSelTotalRedList.size() > 20) {
          InfoMessageBox("错误", "红号个数不对，胆码 加 拖码一共允许6-20个.");
          return;
        }
        
        if(currentSelBlueList.size() < 1) {
          InfoMessageBox("提示", "请选择至少1个篮号.");
          return;
        }
                
        /* 组号并显示结果, 所有数据都已经经过检查 */
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
        
        //转入显示结果页面
        showProgressDialog("提示", "请稍等...");
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
  public void refleshRedAndBlueSeleciton() {
      StringBuffer redSb = new StringBuffer("红号胆：");
      currentSelRedDanList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers();
      currentSelRedTuoList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
      currentSelTotalRedList = MagicTool.join(currentSelRedDanList, currentSelRedTuoList);
      ShuangSeToolsSetApplication.getCurrentSelection().setSelectedModelId(SmartCombineActivity.M_DAN_TUO_COMBINE);
      
      for(Integer item : currentSelRedDanList) {
        if(item < 10) {
          redSb.append("0");
        }
        redSb.append(item);
        redSb.append(" ");
      }
      redSb.append("共" + currentSelRedDanList.size() + "个胆码");
      selRedDanTextView.setText(redSb.toString());

      redSb = new StringBuffer("红号托码：");
      for(Integer item : currentSelRedTuoList) {
        if(item < 10) {
          redSb.append("0");
        }
        redSb.append(item);
        redSb.append(" ");
      }
      redSb.append("共" + currentSelRedTuoList.size() + "个托码");
      selRedTuoTextView.setText(redSb.toString());

      //显示查询所有红球（胆码+托码）历史出号情况按钮
      if(currentSelRedDanList.size() > 0) {
        verifyRedButton.setVisibility(View.VISIBLE);
        verifyRedButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
           // String resultText = appContext.countHistoryOutDetailsForRedset(currentSelRedList);          
            custDialog = customInfoMsgBox("胆码+托码红号组历史出号情况统计如下：");
            custDialog.show();
          }
        });
      } else {
        verifyRedButton.setVisibility(View.INVISIBLE);
      }

      StringBuffer blueSb = new StringBuffer();
      currentSelBlueList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers();
      
      for(Integer item : currentSelBlueList) {
        if(item < 10) {
          blueSb.append("0");
        }
        blueSb.append(item);
        blueSb.append(" ");
      }
      
      blueSb.append("共" + currentSelBlueList.size() + "个篮号");
      selBlueTextView.setText(blueSb.toString());
  }
  
  
  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume()");
    this.refleshRedAndBlueSeleciton();
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
      allHisCnt.add(new ItemPair(startCnt, "近"+startCnt+"期"));
      startCnt -=10;
      if(startCnt <= 1) {
        allHisCnt.add(new ItemPair(1, "近1期"));
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
  @Override
  public void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(DantuoCombineActivity.this);
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
}
