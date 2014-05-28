package com.shuangse.ui;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ControlMsg;

import android.os.Message;
import android.util.Log;

public class BackgroundCheckThread extends Thread {
  private final static String TAG = "BackgroundCheckThread";
  private ShuangSeToolsSetApplication appContext;
  private MainViewActivity mainViewActivity;

  public BackgroundCheckThread(ShuangSeToolsSetApplication appContext,
      MainViewActivity mainViewActivity) {
    this.appContext = appContext;
    this.mainViewActivity = mainViewActivity;  
  }

  @Override
  public void run() {
    try {
      String URLtoServer = appContext.getServerAddr() + "BackgroundCheckAction.do";
      HttpPost httpGet = new HttpPost(URLtoServer);
      Log.i(TAG, "BackgroundCheckThread.run(), URL: " + URLtoServer);
      HttpResponse response = appContext.getHttpClient().execute(httpGet);
      
      if (response != null && response.getStatusLine().getStatusCode() == 200) {
        String responseContent = EntityUtils.toString(response.getEntity(), HTTP.UTF_8).trim();
        Log.v(TAG, "BackgroundCheckThread.run() responseContent: " + responseContent);
        JSONObject jsonObj = new JSONObject(responseContent);
        
        ControlMsg apkObj = new ControlMsg();
        apkObj.setId(jsonObj.getInt("id"));
        apkObj.setInfoType(jsonObj.getString("infoType"));
        apkObj.setText(jsonObj.getString("text"));
        apkObj.setUrl(jsonObj.getString("url"));
        apkObj.setVersion(jsonObj.getString("version"));
        apkObj.setValidFlag(jsonObj.getInt("validFlag"));
        
        float apkVersion = Float.parseFloat(apkObj.getVersion());
        float localVersion = Float.parseFloat(appContext.getVersion());
        Log.i(TAG, "Server apkVersion:" + apkVersion + " Local apkVersion:" + localVersion);
        
        if(localVersion < apkVersion) {
          
          Message msg = new Message();
          msg.what = MainViewActivity.BACKGROUNDCHECKMSG;
          msg.obj = apkObj; 
          this.mainViewActivity.msgHandler.sendMessage(msg);
          
        }
        return;
        
      } else if(response != null && response.getStatusLine().getStatusCode()  == 204) {
        
        if ((response != null) && (response.getEntity() != null)) {
          response.getEntity().consumeContent();
        }
         Log.i(TAG, "BackgroundCheckThread.run() server returns 204 - no content");
        return;
        
      } else {
        if ((response != null) && (response.getEntity() != null)) {
          response.getEntity().consumeContent();
        }
        Log.e(TAG, "BackgroundCheckThread.run() failed since server returns is not 200.");
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.i(TAG, "BackgroundCheckThread is exiting since exception:" + e.toString());
      return;
    }
  }

}
