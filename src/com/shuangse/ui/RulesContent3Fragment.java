package com.shuangse.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;

public class RulesContent3Fragment extends ListFragment {
    private final static String TAG = "RulesContent3Fragment";
    protected Activity activity;
    private static ShuangSeToolsSetApplication mContext;
    protected boolean isInit; // 是否可以开始加载数据
    private ListView expListView;
    private SimpleAdapter adapter;
    protected ArrayList<Map<String, Object>> mlistItems;
            
    private ArrayList<Map<String, Object>> getData() {
        mlistItems = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ItemID", "xuanzhuanZuHao");
        map.put("ItemTitleText", mContext.getResources().getString(R.string.xuanzhuan_zuhao_title));
        mlistItems.add(map);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("ItemID", "conditionsZuHao");
        map2.put("ItemTitleText", mContext.getResources().getString(R.string.conditions_zuhao_title));
        mlistItems.add(map2);
        
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("ItemID", "dantuoZuHao");
        map3.put("ItemTitleText",mContext.getResources().getString(R.string.dantuo_zuhao_title));
        mlistItems.add(map3);
        
        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("ItemID", "fushiZuHao");
        map4.put("ItemTitleText",mContext.getResources().getString(R.string.fushi_zuhao_title));
        mlistItems.add(map4);
        
        return mlistItems;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        activity = this.getActivity();
        mContext = (ShuangSeToolsSetApplication)this.getActivity().getApplication();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        isInit = true;
        return inflater.inflate(R.layout.listplaceholder, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
      expListView = getListView();
      mlistItems = getData();
      adapter = new SimpleAdapter(mContext, mlistItems,
              R.layout.listviewitem, new String[] {
                      "ItemID","ItemTitleText"
              }, new int[] {
                      R.id.ItemID, R.id.ItemTitleText
              });
      setListAdapter(adapter);
      adapter.notifyDataSetChanged();
      expListView.setOnItemClickListener(new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> view, View arg1, int arg2,
                long arg3) {
            TextView ItemIDTextView = (TextView)arg1.findViewById(R.id.ItemID);
            String itemID = ItemIDTextView.getText().toString();
            Log.d(TAG, itemID);
            Intent intent = new Intent(RulesContent3Fragment.this.getActivity(), CommonDispActivity.class);
            intent.putExtra("ItemId", itemID);
            startActivity(intent);
        }
          
      });

    }
    
}
