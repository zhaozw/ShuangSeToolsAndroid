package com.shuangse.ui;

import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ExperienceItem;
import com.shuangse.util.MagicTool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;

public class ExperienceActivity extends Activity {
  private final static String TAG = "ExperienceActivity";
  private TextView expTextView;
  private TextView expTitleView;
  private ProgressDialog progressDialog;
  private int expItemId = -1;
  private ExperienceItem expRecord = null;
  private ShuangSeToolsSetApplication appContext;
  
  private Thread mThread = null;
  
  private final static int DATALOADEDMSG = 1;
  private final static int DATALOADERRMSG = 2;
  
  Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<ExperienceActivity> mActivity;

    MHandler(ExperienceActivity mAct) {
      mActivity = new WeakReference<ExperienceActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      ExperienceActivity theActivity = mActivity.get();
      
      theActivity.hideProgressBox();
      
      switch (msg.what) {
      case DATALOADEDMSG:
        theActivity.updateTextViewText(null);
        break;
        
      case DATALOADERRMSG:
        theActivity.InfoMessageBox("信息", "下载数据出错，请检查您的网络并稍候重试.");
        theActivity.updateTextViewText("获取数据出错，请检查您的网络并稍候重试.");
        break;
        
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };
  
  private void updateTextViewText(String text) {
    if(expTextView != null) {
      if(text != null) {
        expTextView.setText(Html.fromHtml(text));
      } else {
        expTitleView.setText(Html.fromHtml(this.expRecord.getTitle()));
        expTextView.setText(Html.fromHtml(this.expRecord.getHtmlText()));
      }
      expTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.activity_experience);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_experience_detail);
  
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "本页列出文章的详细内容，可上下滚动阅读";
        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, ExperienceActivity.this).show();
      }
    }); 
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    appContext = (ShuangSeToolsSetApplication) getApplication();
    
    expTextView = (TextView)findViewById(R.id.experiencestr);
    expTitleView = (TextView)findViewById(R.id.experienceTitle);
    Bundle bundle = this.getIntent().getExtras();
    this.expItemId = bundle.getInt("ItemId");
    Log.i("ItemId", "Experience Item ID:" + expItemId);
    
    this.showProgressDialog("信息", "请稍等，全力加载中...");
  }
  
  
  
  @Override
  protected void onPause() {
    super.onPause();
    hideProgressBox();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if(mThread == null || !mThread.isAlive()) {
      mThread = new Thread() {
        public void run() {
          try{
            String URLtoServer = appContext.getServerAddr() + "GetExperienceDetailAction.do?itemId=" + expItemId;
            HttpGet httpGet = new HttpGet(URLtoServer);
            Log.i(TAG, "get Experience details URL: " + URLtoServer);
            HttpResponse response = appContext.getHttpClient().execute(httpGet);
            
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
              String responseContent = EntityUtils.toString(response.getEntity(), HTTP.UTF_8).trim();
              Log.i(TAG, "get experience details returned: " + responseContent);
              JSONObject item = new JSONObject(responseContent);
              int id = item.getInt("id"); // 获取对象对应的值
              String title = item.getString("title");
              String textHtml = item.getString("htmlText");
              
              expRecord = new ExperienceItem();
              expRecord.setId(id);
              expRecord.setTitle(title);
              expRecord.setHtmlText(textHtml);
              
              Message msg = new Message();
              msg.what = ExperienceActivity.DATALOADEDMSG;
              msgHandler.sendMessage(msg);
              
            } else {
              if((response != null) && (response.getEntity() != null)) {
                response.getEntity().consumeContent();
              }
              Log.e(TAG, "get experience detail failed since server returns is not 200.");
              
              Message msg = new Message();
              msg.what = ExperienceActivity.DATALOADERRMSG;
              msgHandler.sendMessage(msg);
            }
          } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "got exception during get experience details.");
            Message msg = new Message();
            msg.what = ExperienceActivity.DATALOADERRMSG;
            msgHandler.sendMessage(msg);
          }
        }
      };
      
      mThread.start();
    }
  }



  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(ExperienceActivity.this);
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
  
  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(ExperienceActivity.this)
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
