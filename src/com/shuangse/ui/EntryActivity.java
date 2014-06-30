package com.shuangse.ui;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.util.MagicTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class EntryActivity extends Activity {
    private final static String TAG = "EntryActivity";
    private SharedPreferences sharedPreferences;
    private boolean ifAutoUpdateData = false;
    
    private TextView titleTextView;
    private TextView loadingTextView;
    
    private ShuangSeToolsSetApplication appContext;
    
    public void onCreate(Bundle savedInstanceState) {
        //此句必须在第一行避免 appname作为标题闪现问题
        setTheme(R.style.CustomWindowTitleStyle);
        //获取全局数据
        appContext = (ShuangSeToolsSetApplication)getApplication();
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EntryActivity.this);
        ifAutoUpdateData = sharedPreferences.getBoolean("auto_update_data", true);
        
        //读入已经选择的号码        
        ShuangSeToolsSetApplication.getCurrentSelection().setSelectedRedNumbers(
            MagicTool.parsetRedArrayListByString(
                sharedPreferences.getString(appContext.My_Selection_Red_String, "")));

        ShuangSeToolsSetApplication.getCurrentSelection().setSelectedBlueNumbers(
            MagicTool.parsetRedArrayListByString(
                sharedPreferences.getString(appContext.My_Selection_Blue_String, "")));
        
        ShuangSeToolsSetApplication.getCurrentSelection().setSelectedRedDanNumbers(
            MagicTool.parsetRedArrayListByString(
                sharedPreferences.getString(appContext.My_Selection_Red_Dan_Str, "")));
        
        ShuangSeToolsSetApplication.getCurrentSelection().setSelectedRedTuoNumbers(
            MagicTool.parsetRedArrayListByString(
                sharedPreferences.getString(appContext.My_Selection_Red_Tuo_Str, "")));
        
        ShuangSeToolsSetApplication.getCurrentSelection().setSelectedBlueNumbersForDanTuo(
            MagicTool.parsetRedArrayListByString(
                sharedPreferences.getString(appContext.My_Selection_BlueForDanTuo_String, "")));
        
        //启动自定义窗口标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        View entryView = View.inflate(this, R.layout.entryview, null);
        setContentView(entryView);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
            
        //设置窗口标题
        titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_sys_enter);
                        
        loadingTextView = (TextView) findViewById(R.id.loadingtext);
        loadingTextView.setText(R.string.entering_sys);

        new InitialLoadingTask().execute("");
        super.onCreate(savedInstanceState);
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port
        }
    }
        
    //period is Toast.LENGTH_SHORT or Toast.LENGTH_LONG
    private void PromptToastMessage(String msg, int period) {
        Toast.makeText(EntryActivity.this, msg, period).show();
    }
    
    private void ConfirmExitMessageBox(String title, String msg) {
        AlertDialog notifyDialog = new AlertDialog.Builder(
                EntryActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,    int which) {
                                EntryActivity.this.finish();
                            }
                        })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,    int which) {
                                return;
                            }
                        }).create();
        notifyDialog.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConfirmExitMessageBox("提示", "点<确定>就退出软件了哦...");
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    
    
    class InitialLoadingTask extends AsyncTask<String, String, String> {
        private ProgressDialog progDialog;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
          super.onPostExecute(result);
        }

        protected void onProgressUpdate(String... text) {
          if("DownloadDataFailed".equalsIgnoreCase(text[0])) {
                if(progDialog != null) {
                    progDialog.dismiss();
                }
                
                PromptToastMessage("数据下载失败-请稍候再试.", Toast.LENGTH_LONG);
          } else if("DownloadDataException".equalsIgnoreCase(text[0])) {
                if(progDialog != null) {
                    progDialog.dismiss();
                }
                PromptToastMessage("数据下载失败-请确保手机已经连接网络.", Toast.LENGTH_LONG);
                
            } else if("StartDownLoad".equalsIgnoreCase(text[0])) {
                
                progDialog = new ProgressDialog(EntryActivity.this);
                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);    
                progDialog.setTitle("提示");  
                progDialog.setMessage("正在更新数据，请稍等");  
                progDialog.setIndeterminate(false);    
                progDialog.setCancelable(false);  
                progDialog.show();
                
            } else if("EndDownLoad".equalsIgnoreCase(text[0])) {
                
                if(progDialog != null) {
                    progDialog.dismiss();
                }
                PromptToastMessage("数据更新完成.", Toast.LENGTH_SHORT);
                
            } else if("StartMainActivity".equalsIgnoreCase(text[0])) {
                Intent mainLauncher = new Intent().setClass(EntryActivity.this, MainViewActivity.class);
                startActivity(mainLauncher);
                EntryActivity.this.finish();
            } else {
                loadingTextView.append(text[0]);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            
            publishProgress(".");
            //创建全局变量
            appContext.createGlobalDatas();
            publishProgress(".");
            
            //去服务器下载更新历史数据插入本地数据库中
            Log.i(TAG, "Start to update item data from server.");
            int localLatestItemId = appContext.getLatestItemIDFromLocalDB();
            Log.i(TAG, "localLatestItemId:" + localLatestItemId);
            
            if(ifAutoUpdateData) {
              if(!downloadHistoryData(localLatestItemId)) {
                  Log.e(TAG, " downloadHistoryData failed.");
              }
            }
            
            //把当前本地数据库中的所有数据load进入本地cache中来
            publishProgress(".");
//            new Thread(new Runnable() {
//              @Override
//              public void run() {
                appContext.loadLocalHisDataIntoCache();
//              }
//            }).start();
            
            publishProgress(".");
            
            //统计数据
            appContext.statHistoryData();
            
            publishProgress("StartMainActivity");
            return "Success";
        }

        //下载从startItemID到最新的itemID
        private boolean downloadHistoryData(int startItemID) {
            String URLtoServer = appContext.getServerAddr() + "GetHistoryDataAction.do?StartItemID="+startItemID;
            Log.i(TAG, "downloadHistoryData(): URL: " + URLtoServer);
            HttpGet httpGet = new HttpGet(URLtoServer);
            
            try {
                publishProgress("StartDownLoad");
                HttpResponse response = appContext.getHttpClient().execute(httpGet);
                
                if(response!= null && response.getStatusLine().getStatusCode() == 200) {
                    String responseContent = EntityUtils.toString(response.getEntity()).trim();
                    Log.v(TAG, "downloadHistoryData() responseContent: " + responseContent);

                    if(responseContent.equals("NOMOREDATA")) {
                        
                        publishProgress("EndDownLoad");
                        return true;
                        
                    } else {                    
                        //start to parse data and store it into local DB
                        appContext.parseAndStoreItemsData(responseContent);
                        publishProgress("EndDownLoad");    
                        return true;
                    }
                } else {
                    if(response != null) {
                      response.getEntity().consumeContent();
                    }
                    Log.e(TAG, "download data failed since server returns is not 200.");
                    publishProgress("DownloadDataFailed");
                    return false;
                }
            } catch(Exception e) {
                Log.e(TAG, "download data failed since the exception.");
                e.printStackTrace();
                publishProgress("DownloadDataException");
                return false;
            }
        }
    }
    
}
