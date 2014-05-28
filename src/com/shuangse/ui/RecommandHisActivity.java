package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.RecommandHisRecord;
import com.shuangse.util.MagicTool;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecommandHisActivity extends ListActivity {
  private final static String TAG = "RecommandHisActivity";
  private ProgressDialog progressDialog;
  private ShuangSeToolsSetApplication appContext;
  
  private DataLoadCompletedListener loadCompletedLisneter;
  public void setLoadDataComplete(DataLoadCompletedListener dataComplete) {
      this.loadCompletedLisneter = dataComplete;
  }
  
  private  ArrayList<RecommandHisRecord> recommendHisListItems;
  private ArrayAdapter<RecommandHisRecord> adapter;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.listplaceholder);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_recommendhis);
  
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "本页显示软件智能推荐号码的历史记录，包括验证信息，一行一期的方式显示";
        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, RecommandHisActivity.this).show();
      }
    });
    
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    appContext = (ShuangSeToolsSetApplication) getApplication();
    
    //This will hold the new items
    recommendHisListItems = new ArrayList<RecommandHisRecord>();
    Log.i(TAG, recommendHisListItems.size() + "");
    adapter = new RecommendHisListViewArrayAdapter(this, R.layout.recommhislistviewitem, recommendHisListItems);
    //Set the adapter
    this.setListAdapter(adapter);
    
    TextView emptyView = (TextView) findViewById(android.R.id.empty);
    this.getListView().setEmptyView(emptyView);
    emptyView.setText("点击查看软件推荐历史选项即可查看本历史回顾。");
    
    progressDialog = new ProgressDialog(RecommandHisActivity.this);
    progressDialog.setTitle("提示");
    progressDialog.setMessage("请稍等，正在加载...");
    progressDialog.setCancelable(false);
    progressDialog.show();
    
    new LoadDataAsyncTask().execute("");
    
    setLoadDataComplete(new DataLoadCompletedListener() {
        @Override
        public void loadComplete() {
          if (recommendHisListItems != null && recommendHisListItems.size() > 0) {
            for (int i = 0; i < recommendHisListItems.size(); i++) {
              adapter.add(recommendHisListItems.get(i));
            }
          }
          adapter.notifyDataSetChanged();
          //这里执行你要的操作，当UI更新完成后会自动调用这里面的代码 
          progressDialog.dismiss();
        }});
    
    this.getListView().setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("onItemClick", "position:" + position + " id:" + id);
        RecommandHisRecord itm = adapter.getItem(position);
        Log.i("onItemClick", "item.id:" + itm.getItemId() +
            " item.red:" + itm.getRedNumbers() + 
            " item.blue:" + itm.getBlueNumbers() + 
            "hitRedCnt:" + itm.getOccurRedCnt() + 
            "hitBlueCnt:" + itm.getOccurBlueCnt());
      }
    });
    }
  
  private interface DataLoadCompletedListener {
    public void loadComplete();
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
              recommendHisListItems = appContext.statHotAndWarmSetOccursSummary();
            }
        });

        return "Success";
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
  
  static class ViewHolder {
    View itemView;
    RecommandHisRecord item;
  }
  
  class RecommendHisListViewArrayAdapter extends ArrayAdapter<RecommandHisRecord> {
    private int itemViewResourceId;
    private List<RecommandHisRecord> listItems;
    
    public RecommendHisListViewArrayAdapter(Context context, int itemViewResourceId, List<RecommandHisRecord> listItems) {
      super(context, itemViewResourceId, listItems);
      this.itemViewResourceId = itemViewResourceId;
      this.listItems = listItems;
    }
    
    public int getCount() {  
      if (!listItems.isEmpty()) {  
          return listItems.size();  
      } else {  
          return 0;
      }  
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      RecommandHisRecord item = getItem(position);
      if(item == null) return null;
             
      if (convertView == null) {
        holder = new ViewHolder();
        
        RelativeLayout itemLayout = new RelativeLayout(getContext());
        convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.itemViewResourceId, itemLayout, true);
        holder.itemView = convertView;
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }
      
      holder.item = item;
      TextView titleTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemTitleText);
      String titleTxt = String.valueOf(item.getItemId());
      titleTextView.setText(titleTxt + "期");
      
      TextView redStrTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemRedStr);
      TextView blueStrTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemBlueStr);
      TextView hitCntStrTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemHitStr);
      
     // Log.i(TAG, "red" + item.getRedNumberStr() + "hit Cnt:" + item.getOccurRedCnt());
     // Log.i(TAG, "blue" + item.getBlueNumberStr() + "hit Cnt:" + item.getOccurBlueCnt());
      
      redStrTextView.setText("荐红:" + item.getRedNumberStr() + "共" + item.getRedNumbers().size() + "码");
      blueStrTextView.setText("荐蓝:" + item.getBlueNumberStr() + "共" + item.getBlueNumbers().size() + "码");
      
      if( item.getOccurRedCnt() < 0 || item.getOccurBlueCnt() < 0) {
        //下一期
        hitCntStrTextView.setText("当期实际击中: <未开奖>");
      } else {
        hitCntStrTextView.setText("当期实际击中:" +  item.getOccurRedCnt() + "红" + item.getOccurBlueCnt() + "蓝");
      }
      
      return convertView;
    }
    
  }

}
