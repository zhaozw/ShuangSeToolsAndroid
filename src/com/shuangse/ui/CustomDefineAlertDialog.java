package com.shuangse.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CustomDefineAlertDialog {
    Context context;
    android.app.AlertDialog ad;
    TextView titleView;
    ListView listView;
    private SimpleAdapter adapter;
    protected ArrayList<Map<String, Object>> mlistItems;
    
    private ArrayList<Map<String, Object>> getData() {
        mlistItems = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ItemID", "XuanZhuanCombine");
        map.put("ItemTitleText", context.getResources().getString(R.string.XuanZhuanCombine));
        mlistItems.add(map);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("ItemID", "DanTuoCombine");
        map2.put("ItemTitleText", context.getResources().getString(R.string.DanTuoCombine));
        mlistItems.add(map2);
        
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("ItemID", "ConditionsCombine");
        map3.put("ItemTitleText",context.getResources().getString(R.string.ConditionsCombine));
        mlistItems.add(map3);
        
        return mlistItems;
    }
    

    public CustomDefineAlertDialog(final Context context) {
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context,R.style.CustDefineAlterDialog).create();
        ad.show();
        ad.setCanceledOnTouchOutside(false);
        // 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
        Window window = ad.getWindow();
        window.setContentView(R.layout.custalertdialog);
        
        titleView = (TextView) window.findViewById(R.id.dialog_title);
        listView = (ListView) window.findViewById(R.id.dialog_list);
        
        mlistItems = getData();
        adapter = new SimpleAdapter(context, mlistItems,
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
                  Intent intent = new Intent(context, SmartCombineActivity.class);
                  context.startActivity(intent);
              } else if(itemID != null && itemID.equalsIgnoreCase("DanTuoCombine")) {
                  Intent intent = new Intent(context, DantuoCombineActivity.class);
                  context.startActivity(intent);
              } else if(itemID != null && itemID.equalsIgnoreCase("ConditionsCombine")) {
                  //Intent intent = new Intent(context, ConditionsCombineActivity.class);
                  //context.startActivity(intent);
                  ad.dismiss();
              } 
          }
            
        });
    }

    public void setTitle(int resId) {
        titleView.setText(resId);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }
    
    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }

}
