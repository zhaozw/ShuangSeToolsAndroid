package com.shuangse.ui;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.UserCounterObj;
import com.shuangse.util.MagicTool;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends PreferenceActivity implements SelectRedDialogCallback {
  private final static String TAG = "SettingActivity";
  private Preference keepRedPreference;
  private Preference userConterPreference;
  private SharedPreferences sharedPreferences;
  private String myKeepRedStr;
  ShuangSeToolsSetApplication appContext;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // �˾�����ڵ�һ�б��� appname��Ϊ������������
    setTheme(R.style.CustomWindowTitleStyle);
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.settings);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        R.layout.custom_title);

    appContext = (ShuangSeToolsSetApplication)getApplication();
    
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "��ҳ�ṩ��������ù��ܵ����ã�����ϸ�Ķ�ÿ�������˵����ȷ�������ٸ��ġ�";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, SettingActivity.this).show();
      }
    }); 
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
    myKeepRedStr = sharedPreferences.getString("My_Keep_Red_Str", "");
    
    keepRedPreference = (Preference) findPreference("mykeepred");
    keepRedPreference.setTitle("�ҵ��غ� - ��������غź���");
    keepRedPreference.setSummary("�غ���:" + myKeepRedStr);
    
    userConterPreference = (Preference)findPreference("usercounter");
    userConterPreference.setSummary("");

    new QueryUserCounterThread().start();
    
    // ���±���
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_settings);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {  
          if(preference == keepRedPreference) {
              Log.i(TAG, "keepRedPreference clicked.");
              Dialog dialog = new SelectRedDialog(SettingActivity.this, R.style.SelectRedDialog, SettingActivity.this);
              dialog.show();
          } else if(preference == userConterPreference) {
            new QueryUserCounterThread().start();
          }
          return super.onPreferenceTreeClick(preferenceScreen, preference);  
  }  

  
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      // land
    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      // port
    }
  }

  protected void onPause() {
    super.onPause();
  }
  
  @Override
  public void sendSelectedRedData(ArrayList<Integer> redList) {
    if(redList.size() > 0) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Collections.sort(redList);
        
        myKeepRedStr = MagicTool.getDispArrangedStr(redList);
        editor.putString("My_Keep_Red_Str", myKeepRedStr);
        //�ύ����
        editor.commit();
        
        keepRedPreference.setSummary("�غ���:" + myKeepRedStr);
    }
    
  }
  
  private class QueryUserCounterThread extends Thread {
    
    public QueryUserCounterThread() {
    }
    
    public void run() {
      String URLtoServer = appContext.getServerAddr() + "GetUserCounterAction.do";
      HttpGet httpGet = new HttpGet(URLtoServer);
      try {
        HttpResponse response = appContext.getHttpClient().execute(httpGet);
        
        if(response!= null && response.getStatusLine().getStatusCode() == 200) {
          String responseContent = EntityUtils.toString(response.getEntity(), HTTP.UTF_8).trim();
          Log.v(TAG, "QueryUserCounterThread.run() responseContent: " + responseContent);
          
          JSONObject jsonObj = new JSONObject(responseContent);
          final UserCounterObj userCounterObj = new UserCounterObj();
          userCounterObj.setTotalNumber(jsonObj.getInt("totalNumber"));
          userCounterObj.setTodayAccess(jsonObj.getInt("todayAccess"));
          userCounterObj.setTodayReg(jsonObj.getInt("todayReg"));
          
          runOnUiThread(new Runnable() {
            public void run() {
              userConterPreference.setSummary(userCounterObj.toString());
            }
          });
          
        } else {
          runOnUiThread(new Runnable() {
            public void run() {
              userConterPreference.setSummary(R.string.querying_usercounter_fail);
            }
          });
          
          Log.e(TAG, "Query querying_usercounter_fail failed since server returns is not 200.");              
        }
      } catch(Exception e) {        
        runOnUiThread(new Runnable() {
          public void run() {
            userConterPreference.setSummary(R.string.querying_usercounter_fail);
          }
        });
        
        Log.e(TAG, "Query querying_usercounter_fail failed since the exception.");
        e.printStackTrace();
      }
    }
  }
  
}
