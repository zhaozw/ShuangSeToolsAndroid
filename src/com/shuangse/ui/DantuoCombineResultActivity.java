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

public class DantuoCombineResultActivity extends Activity {
  private final static String TAG = "DantuoCombineResultActivity";
  private int itemId = -1;
  private int getoutHisFlag = 1;
  private ArrayList<Integer> selectRedDan = null;
  private ArrayList<Integer> selectRedTuo = null;
  private ArrayList<Integer> selectBlue = null;
  private int selectModel = 0;

  private SharedPreferences sharedPreferences;
  private boolean ifGetOutOfHistoryitem = true;

  private ArrayList<ShuangseCodeItem> resultCodes = null;
  private ArrayAdapter<ShuangseCodeItem> adapter;
  private ListView resultListView = null;
  private TextView redDanStr;
  private TextView redTuoStr;
  private TextView blueStr;
  
  private TextView resultTitleTextView;
  private Button saveButton;
  //private Button buyButton;
  ShuangSeToolsSetApplication appContext;
  private Button copyButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //���±���
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.result_dantuo_combine);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.title_activity_result_dantuo_combine);
    
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "��ҳ������ʾ��<br>\t һ����ҳ��ʾ��ǰ������Ž����һ��һע���룬�������ʾ�������¹����鿴��" + 
                                      "<br><br>\t �������·�������������ť�ɽ���Ž����������ʷ��¼�з���ع˺Ͳ鿴��ÿ�ڿ��Ա�������¼��";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, DantuoCombineResultActivity.this).show();
      }
    });
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    
    appContext = (ShuangSeToolsSetApplication)getApplication();
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DantuoCombineResultActivity.this);
    ifGetOutOfHistoryitem = sharedPreferences.getBoolean("getout_his_item", true);

    resultListView = (ListView) findViewById(R.id.result_list_view);    
    resultTitleTextView = (TextView) findViewById(R.id.result_cnt_title);
    saveButton = (Button)findViewById(R.id.save_result_btn);
    copyButton = (Button)findViewById(R.id.copy_result_btn);
    
    redDanStr = (TextView)findViewById(R.id.dan_red_str);
    redTuoStr =  (TextView)findViewById(R.id.tuo_red_str);
    blueStr =  (TextView)findViewById(R.id.blue_str);
    //buyButton = (Button)findViewById(R.id.buy_result_btn);
    
    Bundle bundle = this.getIntent().getExtras();
    if(bundle != null) {
      itemId = bundle.getInt("ItemId");
      this.getoutHisFlag = bundle.getInt("IfGetOutHis");
      this.selectModel = bundle.getInt("ModelID");
      this.selectRedDan  = bundle.getIntegerArrayList("SelectRedDan");
      this.selectRedTuo = bundle.getIntegerArrayList("SelectRedTuo");
      this.selectBlue = bundle.getIntegerArrayList("SelectBlue");
      resultCodes  = bundle.getParcelableArrayList("ResultCodes");
      
      if(ifGetOutOfHistoryitem) {
          resultTitleTextView.setText(itemId + "�ڹ����" + resultCodes.size() + "ע(��ȥ����ʷ�й�������).");
      } else {
          resultTitleTextView.setText(itemId + "�ڹ����" + resultCodes.size() + "ע.");
      }
      
      redDanStr.setText("�쵨�룺" + MagicTool.getDispArrangedStr(this.selectRedDan));
      redTuoStr.setText("�����룺" + MagicTool.getDispArrangedStr(this.selectRedTuo));
      blueStr.setText("����" + MagicTool.getDispArrangedStr(this.selectBlue));
      
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
          
          InfoMessageBoxWithoutReturn("��Ϣ", "���ƿ����ɹ�����������Ҫ�ĵط�(�������)ֱ��ճ������");
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
          
          appContext.insertMyDanTuoCombineRecord(itemId, selectRedDan, selectRedTuo, selectBlue, selectModel, getoutHisFlag, resultCodes);
          //Remove current selection since we saved it
          //ShuangSeToolsSetApplication.getCurrentSelection().getSelectedBlueNumbersForDanTuo().clear();
          //ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedDanNumbers().clear();
          //ShuangSeToolsSetApplication.getCurrentSelection().getSelectedRedTuoNumbers().clear();
          //ShuangSeToolsSetApplication.getCurrentSelection().setSelectedModelId(SmartCombineActivity.INVALID_MODEL_ID);
          
          InfoMessageBox("��ʾ", "����ɹ��������ڡ���ż�¼���в�ѯ�ˡ�");
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
    // �Ƿ񴥷�����Ϊback��
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      Intent intent = new Intent(DantuoCombineResultActivity.this,
         DantuoCombineActivity.class);
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
    AlertDialog notifyDialog = new AlertDialog.Builder(DantuoCombineResultActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            DantuoCombineResultActivity.this.finish();
          }
        }).create();
    notifyDialog.show();
  }
  
  private void InfoMessageBoxWithoutReturn(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(DantuoCombineResultActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
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
