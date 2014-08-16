package com.shuangse.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.util.MagicTool;

public class CustomDefineAlertDialog {
    private ExtendedActivity activity;
    private ShuangSeToolsSetApplication appContext;
    private android.app.AlertDialog ad;
    private TextView titleView;
    private ListView listView;
    private String forWhat = null;
    private SimpleAdapter adapter;
    protected ArrayList<Map<String, Object>> mlistItems;
    
    private ArrayList<Map<String, Object>> getData() {
        if(this.forWhat != null && this.forWhat.equals("ForChooseCombineMethod")) {
            mlistItems = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemID", "XuanZhuanCombine");
            map.put("ItemTitleText", activity.getResources().getString(R.string.XuanZhuanCombine));
            mlistItems.add(map);
    
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("ItemID", "DanTuoCombine");
            map2.put("ItemTitleText", activity.getResources().getString(R.string.DanTuoCombine));
            mlistItems.add(map2);
            
            Map<String, Object> map3 = new HashMap<String, Object>();
            map3.put("ItemID", "ConditionsCombine");
            map3.put("ItemTitleText",activity.getResources().getString(R.string.ConditionsCombine));
            mlistItems.add(map3);
            
            return mlistItems;
        } else if(this.forWhat != null && this.forWhat.equals("ForChooseSmartCombineBlueMethod")) {
            
            mlistItems = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemID", "SELFSELECTIONBLUE");
            map.put("ItemTitleText", activity.getResources().getString(R.string.SELFSELECTIONBLUE));
            mlistItems.add(map);
    
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("ItemID", "RECOMMENDBLUEOP");
            map2.put("ItemTitleText", activity.getResources().getString(R.string.RECOMMENDBLUEOP));
            mlistItems.add(map2);
            
            return mlistItems;
        } else if(this.forWhat != null && this.forWhat.equals("ForChooseSmartCombineRedMethod")) {
            
            mlistItems = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemID", "SELFSELECTIONRED1");
            map.put("ItemTitleText", activity.getResources().getString(R.string.SELFSELECTIONRED1));
            mlistItems.add(map);
    
            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put("ItemID", "SELFSELECTIONRED2");
            map2.put("ItemTitleText", activity.getResources().getString(R.string.SELFSELECTIONRED2));
            mlistItems.add(map2);
            
            Map<String, Object> map3 = new HashMap<String, Object>();
            map3.put("ItemID", "RECOMMENDREDMODELHOTWARM");
            map3.put("ItemTitleText", activity.getResources().getString(R.string.RECOMMENDREDMODELHOTWARM));
            mlistItems.add(map3);
            
            Map<String, Object> map4 = new HashMap<String, Object>();
            map4.put("ItemID", "RECOMMEND6FOR456");
            map4.put("ItemTitleText", activity.getResources().getString(R.string.RECOMMEND6FOR456));
            mlistItems.add(map4);
            
            Map<String, Object> map5 = new HashMap<String, Object>();
            map5.put("ItemID", "RECOMMEND17FOR6");
            map5.put("ItemTitleText", activity.getResources().getString(R.string.RECOMMEND17FOR6));
            mlistItems.add(map5);
            
            Map<String, Object> map6 = new HashMap<String, Object>();
            map6.put("ItemID", "RECOMMEND11FOR56");
            map6.put("ItemTitleText", activity.getResources().getString(R.string.RECOMMEND11FOR56));
            mlistItems.add(map6);
            
            Map<String, Object> map7 = new HashMap<String, Object>();
            map7.put("ItemID", "USEMYKEEPRED");
            map7.put("ItemTitleText", activity.getResources().getString(R.string.USEMYKEEPRED));
            mlistItems.add(map7);
            
            return mlistItems;
        } 

        
        return mlistItems;
    }
    

    public CustomDefineAlertDialog(final ExtendedActivity activity, String forWhat) {
        this.activity = activity;
        this.appContext = (ShuangSeToolsSetApplication)activity.getApplicationContext();
        this.forWhat = forWhat;
        
        ad = new android.app.AlertDialog.Builder(activity,R.style.CustDefineAlterDialog).create();
        ad.show();
        ad.setCanceledOnTouchOutside(false);
        // 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
        Window window = ad.getWindow();
        window.setContentView(R.layout.custalertdialog);
        
        titleView = (TextView) window.findViewById(R.id.dialog_title);
        listView = (ListView) window.findViewById(R.id.dialog_list);
        
        mlistItems = getData();
        adapter = new SimpleAdapter(activity, mlistItems,
                R.layout.listviewitem_custdialog, new String[] {
                        "ItemID","ItemTitleText"
                }, new int[] {
                        R.id.ItemID, R.id.ItemTitleText
                });
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> view, View arg1, int arg2,
                  long arg3) {
              TextView ItemIDTextView = (TextView)arg1.findViewById(R.id.ItemID);
              String itemID = ItemIDTextView.getText().toString();
              if(itemID != null && itemID.equalsIgnoreCase("XuanZhuanCombine")) {
                  activity.showProgressDialog("提示", "请稍等...");
                  
                  Intent intent = new Intent(activity, SmartCombineActivity.class);
                  ad.dismiss();
                  activity.startActivity(intent);
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("DanTuoCombine")) {
                  activity.showProgressDialog("提示", "请稍等...");
                  Intent intent = new Intent(activity, DantuoCombineActivity.class);
                  
                  ad.dismiss();
                  activity.startActivity(intent);
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("ConditionsCombine")) {
                  activity.showProgressDialog("提示", "请稍等...");
                  Intent intent = new Intent(activity, ConditionsCombineActivity.class);
                  
                  ad.dismiss();
                  activity.startActivity(intent);
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("SELFSELECTIONBLUE")) {
                  activity.showProgressDialog("提示", "请稍等...");
                  
                  Intent intent = new Intent(activity, BlueMissingDataActivity.class);
                  intent.putExtra("FROM", "SmartCombineActivity");
                  ad.dismiss();
                  activity.startActivity(intent);
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("RECOMMENDBLUEOP")) {
                  
                  HashSet<Integer> blueSet = appContext.getRecommendBlueNumbers(appContext.getAllHisData().size());
                  
                  ArrayList<Integer> currentSelBlueList = ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers();
                  currentSelBlueList.clear();
                  currentSelBlueList.addAll(blueSet);
                 
                  activity.refleshRedAndBlueSeleciton();
                  
                  ad.dismiss();
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("SELFSELECTIONRED1")) {
                  activity.showProgressDialog("提示","请稍等...");
                  
                  Intent intent = new Intent(appContext,
                          RedMissingDataActivity.class);
                  intent.putExtra("FROM", "SmartCombineActivity");
                  ad.dismiss();
                  
                  activity.startActivity(intent);
              } else if(itemID != null && itemID.equalsIgnoreCase("SELFSELECTIONRED2")) {
                  activity.showProgressDialog("提示","请稍等...");
                  
                  Intent intent = new Intent(appContext, RedMissingTrendActivity.class);
                  intent.putExtra("FROM", "SmartCombineActivity");
                  ad.dismiss();
                  
                  activity.startActivity(intent);
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("RECOMMENDREDMODELHOTWARM")) {
                  HashSet<Integer> redSet = appContext.getRecommendRedNumber(appContext.getAllHisData().size() - 1);
                  
                  ShuangSeToolsSetApplication.getCurrentSelection().
                  getSelectedRedNumbers().clear();
                  ShuangSeToolsSetApplication.getCurrentSelection().
                  getSelectedRedNumbers().addAll(redSet);
                  
                  activity.refleshRedAndBlueSeleciton();
                  ad.dismiss();
              } else if(itemID != null && itemID.equalsIgnoreCase("RECOMMEND6FOR456")) {
                  HashSet<Integer> redSet = appContext.getRecommend6Cover456Codes();
                   
                  ShuangSeToolsSetApplication.getCurrentSelection().
                          getSelectedRedNumbers().clear();
                   
                  ShuangSeToolsSetApplication.getCurrentSelection().
                          getSelectedRedNumbers().addAll(redSet);
                  //排序
                  //Collections.sort(currentSelRedList);
                  
                  activity.refleshRedAndBlueSeleciton();
                  ad.dismiss();
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("RECOMMEND17FOR6")) {
                  HashSet<Integer> redSet = appContext.getRecommend17Cover6Codes();
                  
                  ShuangSeToolsSetApplication.getCurrentSelection().
                  getSelectedRedNumbers().clear();
                  ShuangSeToolsSetApplication.getCurrentSelection().
                  getSelectedRedNumbers().addAll(redSet);
                  
                  activity.refleshRedAndBlueSeleciton();
                  ad.dismiss();
                  
              } else if(itemID != null && itemID.equalsIgnoreCase("RECOMMEND11FOR56")) {
                  HashSet<Integer> redSet = appContext.getRecommend11Cover56Codes();
                  
                  ShuangSeToolsSetApplication.getCurrentSelection().
                  getSelectedRedNumbers().clear();
                  ShuangSeToolsSetApplication.getCurrentSelection().
                  getSelectedRedNumbers().addAll(redSet);
                  
                  activity.refleshRedAndBlueSeleciton();
                  ad.dismiss();
              } else if(itemID != null && itemID.equalsIgnoreCase("USEMYKEEPRED")) {
                  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                  String myKeepRedStr = sharedPreferences.getString("My_Keep_Red_Str", "");
                  if(myKeepRedStr != null && myKeepRedStr.length() > 0) {
                    HashSet<Integer> redSet = MagicTool.parsetRedSetByString(myKeepRedStr);
                    
                    ShuangSeToolsSetApplication.getCurrentSelection().
                    getSelectedRedNumbers().clear();
                    ShuangSeToolsSetApplication.getCurrentSelection().
                    getSelectedRedNumbers().addAll(redSet);
                    
                    activity.refleshRedAndBlueSeleciton();
                    
                  } else {
                    InfoMessageBox("提示", "请先在 软件设置 》 里设置你的长期守的红号号码！");
                  }
                  ad.dismiss();
              }
          }
            
        });
    }

    private void InfoMessageBox(String title, String msg) {
        AlertDialog notifyDialog = new AlertDialog.Builder(activity)
            .setTitle(title).setMessage(msg)
            .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            }).create();
        notifyDialog.show();
   }
    

    public void setTitle(int resId) {
        titleView.setText(resId);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

}
