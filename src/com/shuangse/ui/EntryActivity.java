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
        //�˾�����ڵ�һ�б��� appname��Ϊ������������
        setTheme(R.style.CustomWindowTitleStyle);
        //��ȡȫ������
        appContext = (ShuangSeToolsSetApplication)getApplication();
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EntryActivity.this);
        ifAutoUpdateData = sharedPreferences.getBoolean("auto_update_data", true);
        
        //�����Ѿ�ѡ��ĺ���        
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
        
        //�����Զ��崰�ڱ���
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        View entryView = View.inflate(this, R.layout.entryview, null);
        setContentView(entryView);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
            
        //���ô��ڱ���
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
        // �Ƿ񴥷�����Ϊback��
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ConfirmExitMessageBox("��ʾ", "��<ȷ��>���˳������Ŷ...");
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
                
                PromptToastMessage("��������ʧ��-���Ժ�����.", Toast.LENGTH_LONG);
          } else if("DownloadDataException".equalsIgnoreCase(text[0])) {
                if(progDialog != null) {
                    progDialog.dismiss();
                }
                PromptToastMessage("��������ʧ��-��ȷ���ֻ��Ѿ���������.", Toast.LENGTH_LONG);
                
            } else if("StartDownLoad".equalsIgnoreCase(text[0])) {
                
                progDialog = new ProgressDialog(EntryActivity.this);
                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);    
                progDialog.setTitle("��ʾ");  
                progDialog.setMessage("���ڸ������ݣ����Ե�");  
                progDialog.setIndeterminate(false);    
                progDialog.setCancelable(false);  
                progDialog.show();
                
            } else if("EndDownLoad".equalsIgnoreCase(text[0])) {
                
                if(progDialog != null) {
                    progDialog.dismiss();
                }
                PromptToastMessage("���ݸ������.", Toast.LENGTH_SHORT);
                
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
            //����ȫ�ֱ���
            appContext.createGlobalDatas();
            publishProgress(".");
            
            //ȥ���������ظ�����ʷ���ݲ��뱾�����ݿ���
            Log.i(TAG, "Start to update item data from server.");
            int localLatestItemId = appContext.getLatestItemIDFromLocalDB();
            Log.i(TAG, "localLatestItemId:" + localLatestItemId);
            
            if(ifAutoUpdateData) {
              if(!downloadHistoryData(localLatestItemId)) {
                  Log.e(TAG, " downloadHistoryData failed.");
              }
            }
            
            //�ѵ�ǰ�������ݿ��е���������load���뱾��cache����
            publishProgress(".");
//            new Thread(new Runnable() {
//              @Override
//              public void run() {
                appContext.loadLocalHisDataIntoCache();
//              }
//            }).start();
            
            publishProgress(".");
            
            //ͳ������
            appContext.statHistoryData();
            
            publishProgress("StartMainActivity");
            return "Success";
        }

        //���ش�startItemID�����µ�itemID
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
