package com.shuangse.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ItemPair;
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

public class SmartCombineActivity extends Activity {
  private final static String TAG = "SmartCombineActivity";
  
  public final static int SELFSELECTION1 = 1;
  public final static int SELFSELECTION2 = 2;
  public final static int RECOMMENDREDMODELHOTWARM = 3;
  public final static int RECOMMENDBLUEOP = 4;
  public final static int RECOMMEND6FOR456 = 5;
  public final static int USEMYKEEPRED = 6;
  public final static int RECOMMEND17FOR6 = 7;
  public final static int RECOMMEND11FOR56 = 8;
  public final static int SELFSELECTIONBLUE = 9;
  
  public final static int INVALID_MODEL_ID = 0;
  public final static int MIN_SEL_RED_NUMBERS = 8;
  
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
  
  public final static int M_TOTAL_COMBINE = 9999;//全复式组号
  public final static int M_DAN_TUO_COMBINE = 9998;//胆拖组号
  
  private ShuangSeToolsSetApplication appContext;
  private SharedPreferences sharedPreferences;
  private boolean ifGetOutOfHistoryitem = true;
  private boolean ifCheckRedSum = true;
  
  public static List<ItemPair> allModels;
  public static List<ItemPair> allRecommendRedModels;
  public static List<ItemPair> allRecommendBlueItems;
  static {
    allModels = new ArrayList<ItemPair>();
    initializeCombineModels(allModels);
    allRecommendRedModels = new ArrayList<ItemPair>();
    initializeRecommendRedModels(allRecommendRedModels);
    allRecommendBlueItems = new ArrayList<ItemPair>();
    initializeRecommendBlueModels(allRecommendBlueItems);
  };
  
  private static void initializeCombineModels(List<ItemPair> allCombineModels) {
    allCombineModels.clear();
    allCombineModels.add(new ItemPair(INVALID_MODEL_ID, "点击选择模式"));
    allCombineModels.add(new ItemPair(M_TOTAL_COMBINE, "中6保6(复式)组合所选红球"));
    
    allCombineModels.add(new ItemPair(M_8_S_6_G_5_4_ITEM, "8个红码 - 中6保5 - 4注"));
    allCombineModels.add(new ItemPair(M_9_S_6_G_5_7_ITEM, "9个红码 - 中6保5 - 7注"));
    allCombineModels.add(new ItemPair(M_10_S_6_G_5_14_ITEM, "10个红码 - 中6保5 - 14注"));
    allCombineModels.add(new ItemPair(M_11_S_6_G_5_22_ITEM, "11个红码 - 中6保5 - 22注"));
    allCombineModels.add(new ItemPair(M_12_S_6_G_5_38_ITEM, "12个红码 - 中6保5 - 38注"));
    //allCombineModels.add(new ItemPair(M_13_S_6_G_5_61_ITEM, "13个红码 - 中6保5 - 61注"));
    
    allCombineModels.add(new ItemPair(M_10_S_6_G_4_3_ITEM, "10个红码 - 中6保4 - 3注"));
    allCombineModels.add(new ItemPair(M_11_S_6_G_4_5_ITEM, "11个红码 - 中6保4 - 5注"));
    allCombineModels.add(new ItemPair(M_12_S_6_G_4_6_ITEM, "12个红码 - 中6保4 - 6注"));
    allCombineModels.add(new ItemPair(M_13_S_6_G_4_10_ITEM, "13个红码 - 中6保4 - 10注"));
    allCombineModels.add(new ItemPair(M_14_S_6_G_4_14_ITEM, "14个红码 - 中6保4 - 14注"));
    allCombineModels.add(new ItemPair(M_15_S_6_G_4_19_ITEM, "15个红码 - 中6保4 - 19注"));
    allCombineModels.add(new ItemPair(M_16_S_6_G_4_25_ITEM, "16个红码 - 中6保4 - 25注"));
    allCombineModels.add(new ItemPair(M_17_S_6_G_4_33_ITEM, "17个红码 - 中6保4 - 33注"));
    //allCombineModels.add(new ItemPair(M_18_S_6_G_4_42_ITEM, "18个红码 - 中6保4 - 42注"));
    
    allCombineModels.add(new ItemPair(M_9_S_5_G_4_3_ITEM, "9个红码 - 中5保4 - 3注"));
    allCombineModels.add(new ItemPair(M_10_S_5_G_4_7_ITEM, "10个红码 - 中5保4 - 7注"));
    allCombineModels.add(new ItemPair(M_11_S_5_G_4_10_ITEM, "11个红码 - 中5保4 - 10注"));
    allCombineModels.add(new ItemPair(M_12_S_5_G_4_14_ITEM, "12个红码 - 中5保4 - 14注"));
    
    allCombineModels.add(new ItemPair(M_7_S_4_G_4_5_ITEM, "7个红码 - 中4保4 - 5注"));
    allCombineModels.add(new ItemPair(M_8_S_4_G_4_7_ITEM, "8个红码 - 中4保4 - 7注"));
    allCombineModels.add(new ItemPair(M_9_S_4_G_4_12_ITEM, "9个红码 - 中4保4 - 12注"));
    allCombineModels.add(new ItemPair(M_10_S_4_G_4_20_ITEM, "10个红码 - 中4保4 - 20注"));
    //allCombineModels.add(new ItemPair(M_11_S_4_G_4_32_ITEM, "11个红码 - 中4保4 - 32注"));
    //allCombineModels.add(new ItemPair(M_12_S_4_G_4_41_ITEM, "12个红码 - 中4保4 - 41注"));
    
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
      return "胆拖组合号码";
    }
    for(ItemPair item : allModels) {
      if (item.getItemVal() == model) {
        return item.getDispText();
      }
    }
    return "";
  }
  
  private ArrayAdapter<ItemPair> allModelsAdaptor;
  private ArrayAdapter<ItemPair> recommendRedModelAdaptor;
  private ArrayAdapter<ItemPair> recommendBlueAdaptor;
  private ArrayAdapter<String> allitemIDAdaptor;
  
  //3个spinner
  private Spinner spinner_combine_model;
  private Spinner spinner_recommend_red_model;
  private Spinner recommend_blue_spinner;
  private Spinner item_id_spinner;
  private List<String> itemIDs;
  private List<ShuangseCodeItem> allHisData;
  private int currentSelItemId;
  
  //本页一共4个Spinner
  private int mSpinnerCount = 4;
  private int mSpinnerInitializedCount = 0;
  
  private int currentCombineItemId;
  private int currentSelModelId;
  //当前选中的推荐红球的模式
  private int currentSelRecommendRedModelId;
  //篮球推荐选择的几个
  private int currentSelBlueRecommendId;
  
  private TextView selRedTextView;
  private ArrayList<Integer> currentSelRedList;
  private TextView selBlueTextView;
  private ArrayList<Integer> currentSelBlueList;
  
  private Button startCombineButton;
  private Button verifyRedButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appContext = (ShuangSeToolsSetApplication)getApplication();
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.activity_smart_combine);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.title_activity_smart_combine);
    
//    Button returnBtn = (Button)findViewById(R.id.returnbtn);
//    returnBtn.setVisibility(View.VISIBLE);
//    Button helpBtn = (Button)findViewById(R.id.helpbtn);
//    helpBtn.setVisibility(View.VISIBLE);
//    helpBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        String htmlMsg = "本页操作提示：<br>\t 一、旋转矩阵组号是目前世界上基于可靠数学理论最优化组号方法，" +
//                                      " 相比其他组号方法（复式、胆拖等），它可以实现 购买/操作 同样多个 红球号码的情况下，" +
//                                      "大大节省投资资金（购买/操作红球个数越多，中奖概率越大，但投资也大），且有中奖保证；" + 
//                                      "<br>\t 二、红球可以在遗漏走势图中选择，也可以在冷热走势图中选择好，此处会自动保存；" +
//                                      "<br>\t 三、红球也可以采用软件推荐按钮生成，其中软件推荐按钮点击后，会有对应的选项，会把" +
//                                      "对应的红球显示在这里，同时也会在遗漏走势图 或 冷热走势图中 显示；" +
//                                      "<br>\t 四、例如典型的操作如下：点击 软件荐红-> 智能推荐 或 使用守号，生成红球后，再点" +
//                                      "击选择红球按钮将 智能推荐 号码 结合遗漏走势图 或 冷热走势图进行二次 选号，最后返回此处进行组号；" +
//                                      "<br>\t 五、 选择模式时请根据您选择的 红球个数 选择对应的模式，最多可支持8-27个红球 (定期更新中);";
//        
//        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, SmartCombineActivity.this).show();
//      }
//    });
//    returnBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        onBackPressed();
//      }
//    });
    
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
    item_id_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {
            if(mSpinnerInitializedCount < mSpinnerCount) {
                //this is triggered by onCreate;
                mSpinnerInitializedCount++;
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

    });
    
    selRedTextView = (TextView)findViewById(R.id.combine_selred_str);
    
    selBlueTextView = (TextView)findViewById(R.id.combine_selblue_str);    
    verifyRedButton = (Button)findViewById(R.id.combine_verify_red_btn);
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SmartCombineActivity.this);
    ifGetOutOfHistoryitem = sharedPreferences.getBoolean("getout_his_item", true);
    ifCheckRedSum = sharedPreferences.getBoolean("check_sum_combine", true);
        
    recommend_blue_spinner = (Spinner) findViewById(R.id.recommend_blue_spin);
    recommendBlueAdaptor = new ArrayAdapter<ItemPair>(this, R.layout.spinnerformat, allRecommendBlueItems);
    recommendBlueAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    recommend_blue_spinner.setAdapter(recommendBlueAdaptor);
    recommend_blue_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        
        if(mSpinnerInitializedCount < mSpinnerCount) {
          //this is triggered by onCreate;
          mSpinnerInitializedCount++;
          
        } else {//User click
          currentSelBlueRecommendId = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
          Log.i(TAG, "Selected Recommend Blue Model:" + currentSelBlueRecommendId);
          
         if(currentSelBlueRecommendId == INVALID_MODEL_ID) {
                currentSelBlueList.clear();
                selBlueTextView.setText("");
         } else if(currentSelBlueRecommendId == SELFSELECTIONBLUE) {
             showProgressDialog("提示", "请稍等...");
             Intent intent = new Intent(SmartCombineActivity.this,
                 BlueMissingDataActivity.class);
             intent.putExtra("FROM", "SmartCombineActivity");
             startActivity(intent);
         } else if(currentSelBlueRecommendId == RECOMMENDBLUEOP) {
                HashSet<Integer> blueSet = appContext.getRecommendBlueNumbers(appContext.getAllHisData().size());
                blueSet.addAll(blueSet);
                
                currentSelBlueList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers();
                currentSelBlueList.clear();
                currentSelBlueList.addAll(blueSet);
                StringBuffer blueSb = new StringBuffer();
                for(Integer item : currentSelBlueList) {
                    if(item < 10) {
                      blueSb.append("0");
                    }
                    blueSb.append(item);
                    blueSb.append(" ");
                }
                
                blueSb.append("共" + currentSelBlueList.size() + "码");
                selBlueTextView.setText(blueSb.toString());
         }
        }
        
      }
      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        
      } 
    });
    
    spinner_recommend_red_model = (Spinner) findViewById(R.id.recommend_red_model);
    recommendRedModelAdaptor = new ArrayAdapter<ItemPair>(this, R.layout.spinnerformat, allRecommendRedModels);
    recommendRedModelAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    spinner_recommend_red_model.setAdapter(recommendRedModelAdaptor);
    spinner_recommend_red_model.setOnItemSelectedListener( new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
          Log.i(TAG, "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString());
          if(mSpinnerInitializedCount < mSpinnerCount) {
            //this is triggered by onCreate;
            mSpinnerInitializedCount++;
          } else {//User click            
            currentSelRecommendRedModelId = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
            Log.i(TAG, "Selected Recommend Red Model:" + currentSelRecommendRedModelId);
            
           if(currentSelRecommendRedModelId == INVALID_MODEL_ID) {
                  currentSelRedList.clear();
                  selRedTextView.setText("");
                  verifyRedButton.setVisibility(View.INVISIBLE);
           } else if(currentSelRecommendRedModelId == SELFSELECTION1) {
               showProgressDialog("提示", "请稍等...");
               Intent intent = new Intent(appContext,
                   RedMissingDataActivity.class);
               intent.putExtra("FROM", "SmartCombineActivity");
               startActivity(intent);
           } else if(currentSelRecommendRedModelId == SELFSELECTION2) {
               showProgressDialog("提示", "请稍等...");
               Intent intent = new Intent(appContext,
                   RedMissingTrendActivity.class);
               intent.putExtra("FROM", "SmartCombineActivity");
               startActivity(intent);
           } else if(currentSelRecommendRedModelId == RECOMMENDREDMODELHOTWARM) { 
             
                  HashSet<Integer> redSet = appContext.getRecommendRedNumber(appContext.getAllHisData().size() - 1);
                  
                  currentSelRedList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
                  currentSelRedList.clear();
                  currentSelRedList.addAll(redSet);
                  StringBuffer redSb = new StringBuffer();
                  redSb.append(MagicTool.getDispArrangedStr(currentSelRedList));
                  
                  redSb.append("共" + currentSelRedList.size() + "码");
                  selRedTextView.setText(redSb.toString());
                  verifyRedButton.setVisibility(View.VISIBLE);
                  verifyRedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     // String resultText = appContext.countHistoryOutDetailsForRedset(currentSelRedList);          
                      custDialog = customInfoMsgBox("该组红球历史出号情况如下");
                      custDialog.show();
                    }
                  });
           } else if(currentSelRecommendRedModelId == RECOMMEND6FOR456) {
             HashSet<Integer> redSet = appContext.getRecommend6Cover456Codes();
             currentSelRedList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
             currentSelRedList.clear();
             currentSelRedList.addAll(redSet);
             //排序
             Collections.sort(currentSelRedList);
             
             StringBuffer redSb = new StringBuffer();
             redSb.append(MagicTool.getDispArrangedStr(currentSelRedList));
             
             redSb.append("共" + currentSelRedList.size() + "码 (1/" + appContext.getRecommed6Cover456CodesCnt() + ")");
             selRedTextView.setText(redSb.toString());
             verifyRedButton.setVisibility(View.VISIBLE);
             verifyRedButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                 custDialog = customInfoMsgBox("该组红球历史出号情况如下");
                 custDialog.show();
               }
             });
             
           } else if(currentSelRecommendRedModelId == RECOMMEND17FOR6) {
             HashSet<Integer> redSet = appContext.getRecommend17Cover6Codes();
             currentSelRedList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
             currentSelRedList.clear();
             currentSelRedList.addAll(redSet);
             //排序
             Collections.sort(currentSelRedList);
             
             StringBuffer redSb = new StringBuffer();
             redSb.append(MagicTool.getDispArrangedStr(currentSelRedList));
             
             redSb.append("共" + currentSelRedList.size() + "码 (1/" + appContext.getRecommend17Cover6CodesCnt() + ")");
             selRedTextView.setText(redSb.toString());
             verifyRedButton.setVisibility(View.VISIBLE);
             verifyRedButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                 custDialog = customInfoMsgBox("该组红球历史出号情况如下");
                 custDialog.show();
               }
             });
           } else if(currentSelRecommendRedModelId == RECOMMEND11FOR56) {
             HashSet<Integer> redSet = appContext.getRecommend11Cover56Codes();
             currentSelRedList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
             currentSelRedList.clear();
             currentSelRedList.addAll(redSet);
             //排序
             Collections.sort(currentSelRedList);
             
             StringBuffer redSb = new StringBuffer();
             redSb.append(MagicTool.getDispArrangedStr(currentSelRedList));
             
             redSb.append("共" + currentSelRedList.size() + "码 (1/" + appContext.getRecommed11Cover56CodesCnt() + ")");
             selRedTextView.setText(redSb.toString());
             verifyRedButton.setVisibility(View.VISIBLE);
             verifyRedButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                 custDialog = customInfoMsgBox("该组红球历史出号情况如下");
                 custDialog.show();
               }
             });
           } else if(currentSelRecommendRedModelId == USEMYKEEPRED) { //使用我的守号
             String myKeepRedStr = sharedPreferences.getString("My_Keep_Red_Str", "");
             if(myKeepRedStr != null && myKeepRedStr.length() > 0) {
               HashSet<Integer> redSet = MagicTool.parsetRedSetByString(myKeepRedStr);
               currentSelRedList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
               currentSelRedList.clear();
               currentSelRedList.addAll(redSet);
               //排序
               Collections.sort(currentSelRedList);
               
               StringBuffer redSb = new StringBuffer();
               redSb.append(MagicTool.getDispArrangedStr(currentSelRedList));
               
               redSb.append("共" + currentSelRedList.size() + "码");
               selRedTextView.setText(redSb.toString());
               verifyRedButton.setVisibility(View.VISIBLE);
               verifyRedButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                   custDialog = customInfoMsgBox("该组红球历史出号情况如下");
                   custDialog.show();
                 }
               });
   
               
             } else {
               InfoMessageBox("提示", "请先在 软件设置 》 里设置你的长期守的红球号码！");
             }
           
           } 
          }
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
          Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
      }
    });
    
    spinner_combine_model = (Spinner) findViewById(R.id.combine_model);
    allModelsAdaptor = new ArrayAdapter<ItemPair>(this, R.layout.spinnerformat, allModels);  
    allModelsAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    spinner_combine_model.setAdapter(allModelsAdaptor);
    
    spinner_combine_model.setOnItemSelectedListener( new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
          Log.i(TAG, "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString());
          
          if(mSpinnerInitializedCount < mSpinnerCount) {
            //this is triggered by onCreate;
            mSpinnerInitializedCount++;
          } else {//User click
            currentSelModelId = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
            ShuangSeToolsSetApplication.getCurrentSelection().setSelectedModelId(currentSelModelId);
          }
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
          Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
      }
  });
    
    startCombineButton = (Button)findViewById(R.id.combine_start_btn);
    startCombineButton.setOnClickListener(new View.OnClickListener () {
      @Override
      public void onClick(View v) {
        Log.i(TAG, "currentCombineItemId:" + currentCombineItemId);
        Log.i(TAG, "currentSelModelId:" + currentSelModelId);
        Log.i(TAG, "SelectedRedNumbers:" + currentSelRedList);
        Log.i(TAG, "SelectedBlueNumbers:" + currentSelBlueList);
        if(currentSelModelId == INVALID_MODEL_ID) {
          InfoMessageBox("提示", "请选择组号模式");
          return;
        }
        if(currentSelRedList.size() < MIN_SEL_RED_NUMBERS) {
          InfoMessageBox("提示", "请选择至少" + MIN_SEL_RED_NUMBERS + "个红球进行组号.");
          return;
        }
        if(currentSelBlueList.size() < 1) {
          InfoMessageBox("提示", "请选择至少1个篮球.");
          return;
        }
        
        //匹配检查 模式是否和选的红球数一致
        int redCnt = currentSelRedList.size();
        switch (currentSelModelId) {
        case M_TOTAL_COMBINE: //全复式
          if(redCnt < MIN_SEL_RED_NUMBERS || redCnt > 20) {
            InfoMessageBox("错误", "复式组号只能选择8 - 20个红球进行组号.");
            return;
          }
          break;
        case M_7_S_4_G_4_5_ITEM:
          if(redCnt != 7) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_8_S_4_G_4_7_ITEM:
        case M_8_S_6_G_5_4_ITEM:
          if(redCnt != 8) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_9_S_6_G_5_7_ITEM:
        case M_9_S_5_G_4_3_ITEM:
        case M_9_S_4_G_4_12_ITEM:
          if(redCnt != 9) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_10_S_4_G_4_20_ITEM:
        case M_10_S_6_G_5_14_ITEM:
        case M_10_S_5_G_4_7_ITEM:
        case M_10_S_6_G_4_3_ITEM:
          if(redCnt != 10) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_11_S_6_G_4_5_ITEM:
        //case M_11_S_4_G_4_32_ITEM:
        case M_11_S_5_G_4_10_ITEM:
        case M_11_S_6_G_5_22_ITEM:
          if(redCnt != 11) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_12_S_6_G_4_6_ITEM:
        case M_12_S_5_G_4_14_ITEM:
        //case M_12_S_4_G_4_41_ITEM:
        case M_12_S_6_G_5_38_ITEM:
          if(redCnt != 12) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        //case M_13_S_6_G_5_61_ITEM:
        case M_13_S_6_G_4_10_ITEM:
          if(redCnt != 13) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_14_S_6_G_4_14_ITEM:
          if(redCnt != 14) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_15_S_6_G_4_19_ITEM:
          if(redCnt != 15) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_16_S_6_G_4_25_ITEM:
          if(redCnt != 16) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
        case M_17_S_6_G_4_33_ITEM:
          if(redCnt != 17) {
            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
            return;
          }
          break;
//        case M_18_S_6_G_4_42_ITEM:
//          if(redCnt != 18) {
//            InfoMessageBox("错误", "组号模式与所选的红球个数不匹配.");
//            return;
//          }
//          break;
        }
        
        /*组号并显示结果, 所有数据都已经经过检查*/
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
            InfoMessageBox("提示", "所选号码太集中或分散，不符合统计中奖模式，请重新选号以避免浪费钱财.");
            return;
          }
          if( ret == -2) {
            InfoMessageBox("提示", "红球个数和所选择的组号模式不匹配，请重新选择.");
            return;
          }
          //success
          allCombinedCodes.addAll(oneBlueCombinedCodes);
        }//end for selBuleList
        
        //转入显示结果页面
        showProgressDialog("提示", "请稍等...");
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
    });
    
  }
    
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
  
  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume()");
    StringBuffer redSb = new StringBuffer();
    currentSelRedList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers();
    //Collections.sort(currentSelRedList);
    for(Integer item : currentSelRedList) {
      if(item < 10) {
        redSb.append("0");
      }
      redSb.append(item);
      redSb.append(" ");
    }
    redSb.append("共" + currentSelRedList.size() + "码");
    selRedTextView.setText(redSb.toString());
    //显示查询红球历史出号情况按钮
    if(currentSelRedList.size() > 0) {
      verifyRedButton.setVisibility(View.VISIBLE);
      verifyRedButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         // String resultText = appContext.countHistoryOutDetailsForRedset(currentSelRedList);          
          custDialog = customInfoMsgBox("该组红球历史出号情况统计如下：");
          custDialog.show();
        }
      });
    } else {
      verifyRedButton.setVisibility(View.INVISIBLE);
    }

    StringBuffer blueSb = new StringBuffer();
    currentSelBlueList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers();
    //Collections.sort(currentSelBlueList);
    for(Integer item : currentSelBlueList) {
      if(item < 10) {
        blueSb.append("0");
      }
      blueSb.append(item);
      blueSb.append(" ");
    }
    
    blueSb.append("共" + currentSelBlueList.size() + "码");
    selBlueTextView.setText(blueSb.toString());
    
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
  
  private static void initializeRecommendBlueModels(List<ItemPair> allRecommendBlueList) {
    allRecommendBlueList.clear();
    allRecommendBlueList.add(new ItemPair(INVALID_MODEL_ID, "点击选择蓝号"));
    allRecommendBlueList.add(new ItemPair(SELFSELECTIONBLUE, "蓝号走势图选蓝"));
    allRecommendBlueList.add(new ItemPair(RECOMMENDBLUEOP, "软件推荐蓝码"));
  }
  
  private static void initializeRecommendRedModels(List<ItemPair> allRecommendRedModels) {
    allRecommendRedModels.clear();
    allRecommendRedModels.add(new ItemPair(INVALID_MODEL_ID, "点击选择红号"));
    allRecommendRedModels.add(new ItemPair(SELFSELECTION1, "红号走势图选红"));
    allRecommendRedModels.add(new ItemPair(SELFSELECTION2, "冷热走势图选红"));
    allRecommendRedModels.add(new ItemPair(RECOMMENDREDMODELHOTWARM, "软件推荐红号"));
    allRecommendRedModels.add(new ItemPair(RECOMMEND6FOR456, "软件推荐6红"));
    allRecommendRedModels.add(new ItemPair(RECOMMEND11FOR56, "软件推荐11红"));
    allRecommendRedModels.add(new ItemPair(RECOMMEND17FOR6, "软件推荐17红"));
    allRecommendRedModels.add(new ItemPair(USEMYKEEPRED, "使用我的守号"));
  }

  private ProgressDialog progressDialog;
  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(SmartCombineActivity.this);
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
}
