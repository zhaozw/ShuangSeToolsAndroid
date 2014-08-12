package com.shuangse.ui;

import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ExtShuangseCodeItem;
import com.shuangse.util.MagicTool;

public class DispItemOpenDataDetailsActivity extends Activity {
  private final static String TAG = "DispItemOpenDataDetailsActivity";
  private TextView itemIDTextView;
  private TextView itemDateTextView;
  private ImageButton red1Btn;
  private ImageButton red2Btn;
  private ImageButton red3Btn;
  private ImageButton red4Btn;
  private ImageButton red5Btn;
  private ImageButton red6Btn;
  private ImageButton blueBtn;
  private TextView itemFirstPrizeCntTextView;
  private TextView itemFirstPrizeValueTextView;
  private TextView itemFirstSecondCntTextView;
  private TextView itemFirstSecondValueTextView;
  private TextView itemLeftValueTextView;
  
  private ProgressDialog progressDialog;
  private ShuangSeToolsSetApplication application;

  private final static int QUERYSUCCESSMSG = 10;
  private final static int QUERYERRMSG = 20;
  private Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<DispItemOpenDataDetailsActivity> mActivity;

    MHandler(DispItemOpenDataDetailsActivity mAct) {
      this.mActivity = new WeakReference<DispItemOpenDataDetailsActivity>(mAct);
    }

        @Override
        public void handleMessage(Message msg) {

            DispItemOpenDataDetailsActivity theActivity = mActivity.get();
            theActivity.hideProgressBox();
            switch (msg.what) {
            // 列表下载数据返回的消息
            case QUERYSUCCESSMSG:
                ExtShuangseCodeItem codeItem = (ExtShuangseCodeItem) msg.obj;
                theActivity.UpdateDetailsInformation(codeItem);
                break;
            case QUERYERRMSG:
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };
  
  private void UpdateDetailsInformation(ExtShuangseCodeItem codeItem) {
    itemFirstPrizeCntTextView.setText(String.valueOf(codeItem.firstPrizeCnt));
    itemFirstPrizeValueTextView.setText(String.valueOf(codeItem.firstPrizeValue));
    itemFirstSecondCntTextView.setText(String.valueOf(codeItem.secondPrizeCnt));
    itemFirstSecondValueTextView.setText(String.valueOf(codeItem.secondPrizeValue));
    itemLeftValueTextView.setText("奖池金额：" + String.valueOf(codeItem.poolTotal) + "元");
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.item_data_details);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
 
    application = (ShuangSeToolsSetApplication)this.getApplication();
    this.showProgressDialog("信息", "请稍等，全力加载中...");
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.his_data_detail_title);
    
    itemIDTextView = (TextView)findViewById(R.id.ItemID);
    itemDateTextView = (TextView)findViewById(R.id.ItemDate);
    red1Btn = (ImageButton)findViewById(R.id.blankrbtn1);
    red2Btn = (ImageButton)findViewById(R.id.blankrbtn2);
    red3Btn = (ImageButton)findViewById(R.id.blankrbtn3);
    red4Btn = (ImageButton)findViewById(R.id.blankrbtn4);
    red5Btn = (ImageButton)findViewById(R.id.blankrbtn5);
    red6Btn = (ImageButton)findViewById(R.id.blankrbtn6);
    blueBtn = (ImageButton)findViewById(R.id.blankbluebtn);
    
    itemFirstPrizeCntTextView = (TextView)findViewById(R.id.itemFirstPrizeCnt);
    itemFirstPrizeValueTextView = (TextView)findViewById(R.id.itemFirstPrizeValue);
    itemFirstSecondCntTextView = (TextView)findViewById(R.id.itemSecondPrizeCnt);
    itemFirstSecondValueTextView = (TextView)findViewById(R.id.itemSecondPrizeValue);
    itemLeftValueTextView = (TextView)findViewById(R.id.item_left_value);
    
    Bundle bundle = this.getIntent().getExtras();
    int dispItemId = bundle.getInt("ItemId",0);
    Log.i("ItemId", "Item ID:" + dispItemId);
    int red1 = bundle.getInt("RED1",0);
    int red2 = bundle.getInt("RED2",0);
    int red3 = bundle.getInt("RED3",0);
    int red4 = bundle.getInt("RED4",0);
    int red5 = bundle.getInt("RED5",0);
    int red6 = bundle.getInt("RED6",0);
    int blue = bundle.getInt("BLUE",0);
    String openDateStr = bundle.getString("OPENDATE","");
    
    itemIDTextView.setText("第" + String.valueOf(dispItemId) + "期");
    itemDateTextView.setText("开奖日期:" + openDateStr);
    red1Btn.setImageResource(MagicTool.getResIDbyRednum(red1));
    red2Btn.setImageResource(MagicTool.getResIDbyRednum(red2));
    red3Btn.setImageResource(MagicTool.getResIDbyRednum(red3));
    red4Btn.setImageResource(MagicTool.getResIDbyRednum(red4));
    red5Btn.setImageResource(MagicTool.getResIDbyRednum(red5));
    red6Btn.setImageResource(MagicTool.getResIDbyRednum(red6));
    blueBtn.setImageResource(MagicTool.getResIDbyBluenum(blue));
    
    Thread t = new DownloadItemDataThread(dispItemId, application);
    t.start();
  }
  
  
  
  @Override
  protected void onPause() {
    super.onPause();
    hideProgressBox();
  }

  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(DispItemOpenDataDetailsActivity.this);
    progressDialog.setTitle(title);
    progressDialog.setMessage(msg);
    progressDialog.setCancelable(true);
    progressDialog.show();
  }

  private void hideProgressBox() {
    if (progressDialog != null) {
      progressDialog.dismiss();
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
  
  class DownloadItemDataThread extends Thread {
      private int itemid;
      private ShuangSeToolsSetApplication mContext;
      public DownloadItemDataThread(int itemID, ShuangSeToolsSetApplication mContext) {
          this.itemid = itemID;
          this.mContext = mContext;
      }
      @Override
      public void run() {
        try{
          String reqURL = mContext.getServerAddr() + "GetHistoryDataAction.do?action=GetCodeItemInDetail&ItemId=" + itemid;
          Log.v(TAG, reqURL);
          HttpGet httpGet = new HttpGet(reqURL);
          
          HttpResponse response = mContext.getHttpClient().execute(httpGet);
          if (response != null && response.getStatusLine().getStatusCode() == 200) {
            String responseContent = EntityUtils.toString(response.getEntity(),  HTTP.UTF_8).trim();
            Log.v(TAG, "responseContent: " + responseContent);
            
            JSONObject item = new JSONObject(responseContent);

            int id = item.getInt("id"); // 获取对象对应的值
            int red1 = item.getInt("red1");
            int red2 = item.getInt("red2");
            int red3 = item.getInt("red3");
            int red4 = item.getInt("red4");
            int red5 = item.getInt("red5");
            int red6 = item.getInt("red6");
            int blue = item.getInt("blue");
            String openDate = item.getString("openDate");
            int totalSale = item.getInt("totalSale");
            int poolTotal = item.getInt("poolTotal");
            int firstPrizeCnt = item.getInt("firstPrizeCnt");
            int firstPrizeValue = item.getInt("firstPrizeValue");
            int secondPrizeCnt = item.getInt("secondPrizeCnt");
            int secondPrizeValue = item.getInt("secondPrizeValue");
            
            ExtShuangseCodeItem codeItem = new ExtShuangseCodeItem(id,
                    red1,red2,red3,red4,red5,red6,blue,openDate);
            
            codeItem.totalSale = totalSale;
            codeItem.poolTotal = poolTotal;
            codeItem.firstPrizeCnt = firstPrizeCnt;
            codeItem.firstPrizeValue = firstPrizeValue;
            codeItem.secondPrizeCnt = secondPrizeCnt;
            codeItem.secondPrizeValue = secondPrizeValue;
            
            Message msg = new Message();
            msg.what = QUERYSUCCESSMSG;
            msg.obj = codeItem;
            msgHandler.sendMessage(msg);
            
          } else {//Various Error
            Log.e(TAG, "Server returns error.");
            if(response != null && response.getEntity() != null) {
              response.getEntity().consumeContent();
            }
            Message msg = new Message();
            msg.what = QUERYERRMSG;
            msgHandler.sendMessage(msg);
          }
        }catch (Exception e) {
          Log.e(TAG, "Query Server gets exception.");
          e.printStackTrace();
          Message msg = new Message();
          msg.what = QUERYERRMSG;
          msgHandler.sendMessage(msg);
        }
      }
  };
}
