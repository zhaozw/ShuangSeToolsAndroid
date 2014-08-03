package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.shuangse.base.ShuangSeToolsSetApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

public class ShareMyExperienceActivity extends Activity {
  private final static String TAG = "ShareMyExperienceActivity";
  private TextView exprTitle;
  private TextView exprText;
  private TextView exprAuthor;
  private TextView exprContact;
  private Button exprSaveBtn;  
  
  private ProgressDialog progressDialog;
  private ShuangSeToolsSetApplication appContext;
  private Thread mThread = null;
  
  private final static int ExprSendCompletedMsg = 10;
  private final static int ExprSendFailMsg = 20;
  
  Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<ShareMyExperienceActivity> mActivity;

    MHandler(ShareMyExperienceActivity mAct) {
      mActivity = new WeakReference<ShareMyExperienceActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      ShareMyExperienceActivity theActivity = mActivity.get();
      
      theActivity.hideProgressBox();
      
      switch (msg.what) {
      case ExprSendCompletedMsg:
          theActivity.InfoMessageBox("成功", "保存成功！其它彩友可以看到你的分享哦，谢谢亲");
          theActivity.finish();
          break;
        
      case ExprSendFailMsg:
          theActivity.InfoMessageBox("信息", "数据发送出错，请检查您的网络并稍候重试.");
          break;
        
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.sharemyexprshare);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_share_my_experience);
    appContext = (ShuangSeToolsSetApplication) getApplication();
    
    exprTitle = (TextView) findViewById(R.id.exprTitle);
    exprText = (TextView) findViewById(R.id.exprText);
        
    exprAuthor = (TextView) findViewById(R.id.exprAuthor);
    exprContact = (TextView) findViewById(R.id.exprContact);
    exprSaveBtn = (Button) findViewById(R.id.exprSaveBtn);
    exprSaveBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String exprTitleStr = exprTitle.getText().toString();
          String exprTextStr = exprText.getText().toString();
          String exprAuthorStr = exprAuthor.getText().toString();
          String exprContactStr = exprContact.getText().toString();
          
          if (TextUtils.isEmpty(exprTitleStr)) {
            InfoMessageBox("提醒", "请填写标题哦，标题要简短明了");
            return;
          }
          
          if (TextUtils.isEmpty(exprTextStr)) {
              InfoMessageBox("提醒", "请填写内容哦，内容要清晰说明你的方法");
              return;
          }
          showProgressDialog("提示", "请稍等，正在保存分享到服务器...");
          mThread = new PostExperienceThread(appContext,
                  exprTitleStr, exprTextStr, exprAuthorStr, exprContactStr);
          mThread.start();
          
        }
      });
  }
  
  
  
  @Override
  protected void onPause() {
    super.onPause();
    hideProgressBox();
  }

  @Override
  protected void onStart() {
    super.onStart();
  }
  
  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(ShareMyExperienceActivity.this);
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
    AlertDialog notifyDialog = new AlertDialog.Builder(ShareMyExperienceActivity.this)
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
  
  /**
   * 
   * Thread for post the experience to server
   * 
   */
  private class PostExperienceThread extends Thread {
      private String exprTitleStr;
      private String exprTextStr;
      private String exprAuthorStr;
      private String exprContactStr;
      private ShuangSeToolsSetApplication mContext;

      PostExperienceThread(ShuangSeToolsSetApplication appContext,
              String exprTitleStrIn,
              String exprTextStrIn, String exprAuthorStrIn, String exprContactStrIn) {
          exprTitleStr = exprTitleStrIn;
          exprTextStr = exprTextStrIn;
          exprAuthorStr = exprAuthorStrIn;
          exprContactStr = exprContactStrIn;
          mContext = appContext;
      }

      public void run() {
          try {
              HttpPost httpPost = new HttpPost(mContext.getServerAddr()
                      + "ShareMyExperienceAction.do");
              List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
              httpParams.add(new BasicNameValuePair("Action", "add"));
              httpParams.add(new BasicNameValuePair("exprTitle", exprTitleStr));
              httpParams.add(new BasicNameValuePair("exprText", exprTextStr));
              httpParams.add(new BasicNameValuePair("exprAuthor", exprAuthorStr));
              httpParams.add(new BasicNameValuePair("exprContact", exprContactStr));
              httpPost.setEntity(new UrlEncodedFormEntity(httpParams, HTTP.UTF_8));

              HttpResponse response = mContext.getHttpClient().execute(httpPost);
              if (response != null
                      && response.getStatusLine().getStatusCode() == 200) {
                  String responseContent = EntityUtils.toString(
                          response.getEntity()).trim();
                  Log.v(TAG, "responseContent: " + responseContent);
                  Message msg = msgHandler
                          .obtainMessage(ExprSendCompletedMsg);
                  msg.arg1 = 1;
                  msg.sendToTarget();
              } else {
                  if (response != null) {
                      response.getEntity().consumeContent();
                  }
                  Log.e(TAG, "Server response is not 200.");
                  Message msg = msgHandler
                          .obtainMessage(ExprSendFailMsg);
                  msg.arg1 = 0;
                  msg.sendToTarget();
              }

          } catch (Exception e) {
              e.printStackTrace();
              Message msg = msgHandler.obtainMessage(ExprSendFailMsg);
              msg.arg1 = 0;
              msg.sendToTarget();
          }
      }
  };

  
}
