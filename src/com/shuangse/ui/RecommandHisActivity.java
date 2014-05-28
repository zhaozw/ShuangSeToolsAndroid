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
    
    //���±���
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
        String htmlMsg = "��ҳ��ʾ��������Ƽ��������ʷ��¼��������֤��Ϣ��һ��һ�ڵķ�ʽ��ʾ";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, RecommandHisActivity.this).show();
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
    emptyView.setText("����鿴����Ƽ���ʷѡ��ɲ鿴����ʷ�عˡ�");
    
    progressDialog = new ProgressDialog(RecommandHisActivity.this);
    progressDialog.setTitle("��ʾ");
    progressDialog.setMessage("���Եȣ����ڼ���...");
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
          //����ִ����Ҫ�Ĳ�������UI������ɺ���Զ�����������Ĵ��� 
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
      titleTextView.setText(titleTxt + "��");
      
      TextView redStrTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemRedStr);
      TextView blueStrTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemBlueStr);
      TextView hitCntStrTextView = (TextView) holder.itemView.findViewById(R.id.RecommItemHitStr);
      
     // Log.i(TAG, "red" + item.getRedNumberStr() + "hit Cnt:" + item.getOccurRedCnt());
     // Log.i(TAG, "blue" + item.getBlueNumberStr() + "hit Cnt:" + item.getOccurBlueCnt());
      
      redStrTextView.setText("����:" + item.getRedNumberStr() + "��" + item.getRedNumbers().size() + "��");
      blueStrTextView.setText("����:" + item.getBlueNumberStr() + "��" + item.getBlueNumbers().size() + "��");
      
      if( item.getOccurRedCnt() < 0 || item.getOccurBlueCnt() < 0) {
        //��һ��
        hitCntStrTextView.setText("����ʵ�ʻ���: <δ����>");
      } else {
        hitCntStrTextView.setText("����ʵ�ʻ���:" +  item.getOccurRedCnt() + "��" + item.getOccurBlueCnt() + "��");
      }
      
      return convertView;
    }
    
  }

}
