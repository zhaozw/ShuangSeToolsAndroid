package com.shuangse.ui;

import java.util.ArrayList;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.util.MagicTool;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SmartCombineResultActivity extends Activity {
  private final static String TAG = "SmartCombineResultActivity";
  private int itemId = -1;
  private int getoutHisFlag = 1;
  private ArrayList<Integer> selectRed = null;
  private ArrayList<Integer> selectBlue = null;
  private int selectModel = 0;

  private SharedPreferences sharedPreferences;
  private boolean ifGetOutOfHistoryitem = true;

  private ArrayList<ShuangseCodeItem> resultCodes = null;
  private ArrayAdapter<ShuangseCodeItem> adapter;
  private ListView resultListView = null;
  
  private TextView resultTitleTextView;
  private Button saveButton;
  private Button copyButton;
  ShuangSeToolsSetApplication appContext;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.result_smart_combine);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.title_activity_result_smart_combine);
    
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "本页操作提示：<br>\t 一、本页显示当前旋转组号结果，一行一注号码，按序号显示，可上下滚动查看；" + 
                                      "<br><br>\t 二、最下方《保存结果》按钮可将组号结果保存至历史记录中方便回顾和查看，每期可以保存多个记录。";
        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, SmartCombineResultActivity.this).show();
      }
    });
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    appContext = (ShuangSeToolsSetApplication)getApplication();
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SmartCombineResultActivity.this);
    ifGetOutOfHistoryitem = sharedPreferences.getBoolean("getout_his_item", true);

    resultListView = (ListView) findViewById(R.id.result_list_view);    
    resultTitleTextView = (TextView) findViewById(R.id.result_cnt_title);
    saveButton = (Button)findViewById(R.id.save_result_btn);
    copyButton = (Button)findViewById(R.id.copy_result_btn);
    
    Bundle bundle = this.getIntent().getExtras();
    if(bundle != null) {
      itemId = bundle.getInt("ItemId");
      this.getoutHisFlag = bundle.getInt("IfGetOutHis");
      this.selectModel = bundle.getInt("ModelID");
      this.selectRed  = bundle.getIntegerArrayList("SelectRed");
      this.selectBlue = bundle.getIntegerArrayList("SelectBlue");
      
      resultCodes  = bundle.getParcelableArrayList("ResultCodes");
      if(ifGetOutOfHistoryitem) {
          resultTitleTextView.setText(itemId + "期共组号" + resultCodes.size() + "注(已去除历史中过奖号码).");
      } else {
          resultTitleTextView.setText(itemId + "期共组号" + resultCodes.size() + "注.");
      }
      
      adapter = new ResultListViewArrayAdapter(this, R.layout.resultlistviewitem, resultCodes);
      
      TextView emptyView = (TextView) findViewById(android.R.id.empty);
      resultListView.setEmptyView(emptyView);
      
      copyButton.setOnClickListener(new View.OnClickListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View v) {
          int sdk = android.os.Build.VERSION.SDK_INT;
          String resultString = MagicTool.getFormatResultString(resultCodes);
          
          if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
              android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
              clipboard.setText(resultString);
          } else {
              android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
              android.content.ClipData clip = android.content.ClipData.newPlainText("text label",resultString);
              clipboard.setPrimaryClip(clip);
          }
          InfoMessageBoxWithoutReturn("信息", "复制拷贝成功，在其它需要的地方(如短信中)直接粘贴即可");
        }
      });
      
      //Set the adapter
      resultListView.setAdapter(adapter);
      resultListView.setOnItemClickListener(new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          Log.i("onItemClick", "position:" + position + " id:" + id);
          ShuangseCodeItem itm = adapter.getItem(position);
          Log.i("onItemClick", "item.id:" + itm.id + " item.title:" + itm.toLineUpString());
        }
      });
      
      saveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          appContext.insertMyCombineRecord(itemId, selectRed, selectBlue, selectModel, getoutHisFlag, resultCodes);
          //Remove current selection since we saved it
          //ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbers().clear();
          //ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedNumbers().clear();
          //ShuangSeToolsSetApplication.getCurrentSelection().setSelectedModelId(SmartCombineActivity.INVALID_MODEL_ID);
          
          InfoMessageBox("提示", "保存成功！可以在《组号记录》中查询了。");
        }
      });
      
    }
  }
      
  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "onResume()");
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // 是否触发按键为back键
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      Intent intent = new Intent(SmartCombineResultActivity.this,
          SmartCombineActivity.class);
      startActivity(intent);
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }
  
  
  static class ViewHolder {
    View itemView;
    ShuangseCodeItem item;
  }
  
  class ResultListViewArrayAdapter extends ArrayAdapter<ShuangseCodeItem> {
    private int itemViewResourceId;
    private ArrayList<ShuangseCodeItem> listItems;
    
    public ResultListViewArrayAdapter(Context context, int itemViewResourceId, ArrayList<ShuangseCodeItem> listItems) {
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
      ShuangseCodeItem item = getItem(position);
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
      TextView titleTextView = (TextView) holder.itemView.findViewById(R.id.ItemTitleText);
      String titleTxt = String.valueOf(item.toLineUpString());
      titleTextView.setText("(" + (position + 1) + "): " + titleTxt);
      
      return convertView;
    }
    
  }
  
  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(SmartCombineResultActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            SmartCombineResultActivity.this.finish();
          }
        }).create();
    notifyDialog.show();
  }
  
  private void InfoMessageBoxWithoutReturn(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(SmartCombineResultActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              return;
          }
        }).create();
    notifyDialog.show();
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
