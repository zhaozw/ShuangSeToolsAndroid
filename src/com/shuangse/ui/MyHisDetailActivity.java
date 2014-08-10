package com.shuangse.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.MyHisRecord;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.ui.SmartCombineResultActivity.ViewHolder;
import com.shuangse.util.MagicTool;

public class MyHisDetailActivity extends Activity {
  //private final String TAG = "MyHisDetailActivity";
  private ShuangSeToolsSetApplication appContext;
  private MyHisRecord itm = null;
  
  private TextView itemidTextView;
  private TextView redstrTextView;
  private TextView bluestrTextView;
  private TextView modelstrTextView;
  private TextView occurTextView;
  private TextView resultDetailTitleTextView;

  private ArrayList<ShuangseCodeItem> allCombinedCodes = null;
  private ArrayAdapter<ShuangseCodeItem> adapter;
  private ListView resultListView = null;
  private ShuangseCodeItem outItem = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.myhisdetail);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_myhisdetail);
  
//    Button returnBtn = (Button)findViewById(R.id.returnbtn);
//    returnBtn.setVisibility(View.VISIBLE);
//    Button helpBtn = (Button)findViewById(R.id.helpbtn);
//    helpBtn.setVisibility(View.VISIBLE);
//    helpBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        String htmlMsg = "本页为您提供您个人购买号码的详细中奖情况。";
//        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, MyHisDetailActivity.this).show();
//      }
//    });
//    returnBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        onBackPressed();
//      }
//    });
    
    appContext = (ShuangSeToolsSetApplication) getApplication();
 
    itemidTextView = (TextView)findViewById(R.id.itemidstr);
    redstrTextView = (TextView)findViewById(R.id.redstr);
    bluestrTextView = (TextView)findViewById(R.id.bluestr);
    modelstrTextView = (TextView)findViewById(R.id.modelstr);
    occurTextView = (TextView)findViewById(R.id.occurstr);
    resultDetailTitleTextView = (TextView)findViewById(R.id.result_detail_title);
    resultListView = (ListView) findViewById(R.id.result_list_view);
     
    Bundle bundleData = this.getIntent().getExtras();
    
    if(bundleData != null) {      
      itm = new MyHisRecord(); 
      itm.setRecordIndex( bundleData.getInt("ITEMRECORDINDEX"));
      itm.setItemId(bundleData.getInt("ITEMID"));
      itm.setSelectModel(bundleData.getInt("MODEL"));
      
      //是否已经出号
      outItem = appContext.getCodeItemByID(itm.getItemId());
      int[] selRedNumbers = null;
      int[] selTotalRedNumbers = null;
      int[] redDanNumbers = null;
      
      StringBuffer redStrDisp = new StringBuffer();
      if(itm.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
        
        itm.setRedDanNumbers(bundleData.getIntegerArrayList("RedDanArrayList"));
        itm.setRedTuoNumbers(bundleData.getIntegerArrayList("RedTuoArrayList"));
        
        ArrayList<Integer> currentSelTotalRedList = new ArrayList<Integer>();
        currentSelTotalRedList = MagicTool.join(itm.getRedDanNumbers(), itm.getRedTuoNumbers());
        
        redStrDisp.append("红胆:").append(itm.getRedDanNumberStr());
        if(outItem != null) {
          int redHitCnt = MagicTool.getHowManyNumSame(outItem.getRedNumbersList(),itm.getRedDanNumbers());
          redStrDisp.append(";[中").append(redHitCnt).append("红].");
        }
        
        redStrDisp.append("\n红拖:").append(itm.getRedTuoNumberStr());
        if(outItem != null) {
          int redHitCnt = MagicTool.getHowManyNumSame(outItem.getRedNumbersList(),itm.getRedTuoNumbers());
          redStrDisp.append(";[中").append(redHitCnt).append("红].");
        }
        
        selTotalRedNumbers = new int[currentSelTotalRedList.size()];
        int x=0;
        for(Integer itm:currentSelTotalRedList) {
          selTotalRedNumbers[x++] = itm.intValue();
        }
        redDanNumbers = new int[itm.getRedDanNumbers().size()];
        int y = 0;
        for(Integer reditm : itm.getRedDanNumbers()) {
          redDanNumbers[y++] = reditm.intValue();
        }

      } else {
        itm.setRedNumbers(bundleData.getIntegerArrayList("RedArrayList"));
        redStrDisp.append(itm.getRedNumberStr()).append("共").append(itm.getRedNumbers().size()).append("码");
        
        if(outItem != null) {
          int redHitCnt = MagicTool.getHowManyNumSame(outItem.getRedNumbersList(),itm.getRedNumbers());
          redStrDisp.append(";[中").append(redHitCnt).append("红].");
        }
        
        ArrayList<Integer> redList = itm.getRedNumbers();
        
        selRedNumbers = new int[redList.size()];
        int x=0;
        for(Integer red : redList) {
          selRedNumbers[x++] = red.intValue();
        }
      }
      
      itm.setBlueNumbers(bundleData.getIntegerArrayList("BlueArrayList"));
      itm.setGetoutHisFlag(bundleData.getInt("GetOutHisFlag"));
      
      Log.i("ITEM", itm.toString());
      Log.i("ITEM RecordIndex", ""+ itm.getRecordIndex());
      Log.i("ITEM ID", ""+itm.getItemId());
      Log.i("ITEMred", ""+itm.getRedNumbers());
      Log.i("ITEMredDan", ""+itm.getRedDanNumbers());
      Log.i("ITEMredTuo", ""+itm.getRedTuoNumbers());
      Log.i("ITEMblue", ""+itm.getBlueNumberStr());
      Log.i("ITEMmodel", ""+itm.getSelectModel());
      Log.i("ITEMflag", ""+itm.getGetoutHisFlag());
      
      itemidTextView.setText(String.valueOf(itm.getItemId()) + "期");
      modelstrTextView.setText(SmartCombineActivity.getModelStrByModelID(itm.getSelectModel()));
      
      StringBuffer blueStrDisp = new StringBuffer();
      blueStrDisp.append(itm.getBlueNumberStr()).append("共").append(itm.getBlueNumbers().size()).append("码");
      
      if(outItem != null) {
        occurTextView.setText(outItem.toLineUpString());
        if(itm.getBlueNumbers().contains(Integer.valueOf(outItem.blue))) {
          blueStrDisp.append(";[击中].");
        } else {
          blueStrDisp.append(";[未中].");
        }
      } else {
        occurTextView.setText("未开奖");
      }
      
      redstrTextView.setText(redStrDisp.toString());
      bluestrTextView.setText(blueStrDisp.toString());
      
//   读取全部保存的记录///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//      boolean getOutHisFlag = itm.getGetoutHisFlag() > 0 ? true : false;
//      ArrayList<Integer> blueList = itm.getBlueNumbers();
//      allCombinedCodes = new ArrayList<ShuangseCodeItem>();
//      //重新组号
//      for(Integer blue : blueList) {
//        int blueNum = blue.intValue();
//        ArrayList<ShuangseCodeItem> oneBlueCombinedCodes = new ArrayList<ShuangseCodeItem>();
//        
//        if(itm.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
//          appContext.combineCodes(selTotalRedNumbers, selTotalRedNumbers.length,
//              getOutHisFlag, redDanNumbers, redDanNumbers.length, 6, 
//              itm.getItemId(), blue, oneBlueCombinedCodes);
//          
//        } else {
//            int ret = appContext.combineCodes(itm.getSelectModel(), selRedNumbers,
//                                  0, getOutHisFlag, false, 
//                                  (int) itm.getItemId(), blueNum,
//                                  oneBlueCombinedCodes);
//            if(ret != 1) {
//              InfoMessageBox("错误", "内部错误，请联系作者告知详情，谢谢.");
//            }
//        }
//        
//        //success
//        allCombinedCodes.addAll(oneBlueCombinedCodes);
//      }//end for blue
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      allCombinedCodes = appContext.selectMyCombineDetailRecord(itm);
      if(allCombinedCodes == null) {
        InfoMessageBox("错误", "读取保存号码出错，请联系作者 或 客服！");
        return;
      }
      resultDetailTitleTextView.setText("共组号" + allCombinedCodes.size() + "注，详情如下：");
      
      adapter = new HisResultListViewArrayAdapter(this, R.layout.myhisresultlistviewitem, allCombinedCodes);
      TextView emptyView = (TextView) findViewById(android.R.id.empty);
      resultListView.setEmptyView(emptyView);
      //Set the adapter
      resultListView.setAdapter(adapter);
    }
  }

  class HisResultListViewArrayAdapter extends ArrayAdapter<ShuangseCodeItem> {
    private int itemViewResourceId;
    private ArrayList<ShuangseCodeItem> listItems;
    
    public HisResultListViewArrayAdapter(Context context, int itemViewResourceId, ArrayList<ShuangseCodeItem> listItems) {
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
      
      if (outItem == null) { // 还未开奖或数据未更新
        titleTextView.setText("(" + (position + 1) + ")" + titleTxt + "=未开奖");
      } else {
          //针对每一注号码验证其中奖情况
          int hitResult = MagicTool.getHitResult(outItem, item);
          String reStr = "";
          switch (hitResult) {
          case 1:
              reStr = "中6红1蓝";
              break;
          case 2:
              reStr = "中6红0蓝";
              break;
          case 3:
              reStr = "中5红1蓝";
              break;
          case 4:
              reStr = "中4红1蓝";
              break;
          case 5:
              reStr = "中5红0蓝";
              break;
          case 6:
              reStr = "中3红1蓝";
              break;
          case 7:
              reStr = "中4红0蓝";
              break;
          case 8:
              reStr = "中2红1蓝";
              break;
          case 9:
              reStr = "中1红1蓝";
            break;    
          case 10:
            reStr = "中0红1蓝";
            break; 
          case 11:
            reStr = "中3红0蓝";
            break;  
          case 12:
            reStr = "中2红0蓝";
            break;
          case 13:
            reStr = "中1红0蓝";
            break;
          case 14:
            reStr = "中0红0蓝";
            break;
          case -1:
          default:
              reStr = "未中奖";
              break;
          }
          titleTextView.setText("(" + (position + 1) + "): " + titleTxt + "=" + reStr);
      }

      return convertView;
    }
    
  }

  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(MyHisDetailActivity.this)
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
