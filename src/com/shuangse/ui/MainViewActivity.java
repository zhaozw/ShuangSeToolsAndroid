package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.AndroidPhoneInfo;
import com.shuangse.meta.ControlMsg;
import com.shuangse.util.MagicTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainViewActivity extends Activity {
  private static final String TAG = "MainViewActivity";
  private TextView titleTextView;
  private ShuangSeToolsSetApplication appContext;
  private SharedPreferences sharedPreferences;
  private boolean ifPhoneInfoRegistered = false;
  private ProgressDialog progressDialog;
  private Thread backgroundThread = null;
  private Thread checkSmsTextThread = null;
  
  private static volatile boolean ifRegOrUpdateDone = false; 
  private static volatile boolean ifSMSTextReadDone = false;
  private static volatile boolean ifBackgroundCheckDone = false;

  // 数据更新使用的消息定义
  private static final int DATAISLATESTMSG = 1;
  private static final int DATAUPDATESUCCESSMSG = 2;
  private static final int DATAUPDATEFAILMSG = 3;
  private static final int REGPHONEINFOSUCCESS = 4;
  public static final int BACKGROUNDCHECKMSG = 5;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 启动自定义窗口标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.guidegrid);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        R.layout.custom_title);

    appContext = (ShuangSeToolsSetApplication) getApplication();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainViewActivity.this);
    ifPhoneInfoRegistered = sharedPreferences.getBoolean("if_phone_info_registered", false);

    // 设置窗口标题 - 显示版本号
    titleTextView = (TextView) findViewById(R.id.title_text);
    String title = getResources().getString(R.string.custom_title_function_sel_shuangse) + 
            appContext.getVersion() + "版";
    titleTextView.setText(title);
    
    GridView gridview = (GridView) findViewById(R.id.guidegrid);
    ArrayList<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>();

    HashMap<String, Object> map1 = new HashMap<String, Object>();
    map1.put("ItemImage", R.drawable.info_reminder);
    map1.put("ItemText", "玩法说明");
    menuList.add(map1);

    HashMap<String, Object> map2 = new HashMap<String, Object>();
    map2.put("ItemImage", R.drawable.manual);
    map2.put("ItemText", "中奖经验");
    menuList.add(map2);
    
    HashMap<String, Object> map3 = new HashMap<String, Object>();
    map3.put("ItemImage", R.drawable.redmissing);
    map3.put("ItemText", "红球走势");
    menuList.add(map3);

    HashMap<String, Object> map4 = new HashMap<String, Object>();
    map4.put("ItemImage", R.drawable.bluemissing);
    map4.put("ItemText", "蓝球走势");
    menuList.add(map4);

    HashMap<String, Object> map5 = new HashMap<String, Object>();
    map5.put("ItemImage", R.drawable.hotcool);
    map5.put("ItemText", "冷热走势");
    menuList.add(map5);
    
    HashMap<String, Object> map6 = new HashMap<String, Object>();
    map6.put("ItemImage", R.drawable.dantuo);
    map6.put("ItemText", "胆拖组号");
    menuList.add(map6);    
    
    HashMap<String, Object> map7 = new HashMap<String, Object>();
    map7.put("ItemImage", R.drawable.redcombie);
    map7.put("ItemText", "旋转组号");
    menuList.add(map7);

    HashMap<String, Object> map8 = new HashMap<String, Object>();
    map8.put("ItemImage", R.drawable.recred);
    map8.put("ItemText", "软件荐号");
    menuList.add(map8);
    
    HashMap<String, Object> map9 = new HashMap<String, Object>();
    map9.put("ItemImage", R.drawable.search);
    map9.put("ItemText", "条件查询");
    menuList.add(map9);    
    
    HashMap<String, Object> map10 = new HashMap<String, Object>();
    map10.put("ItemImage", R.drawable.verify);
    map10.put("ItemText", "中奖查询");
    menuList.add(map10);

    HashMap<String, Object> map11 = new HashMap<String, Object>();
    map11.put("ItemImage", R.drawable.updatedata);
    map11.put("ItemText", "更新数据");
    menuList.add(map11);
    
    HashMap<String, Object> map12 = new HashMap<String, Object>();
    map12.put("ItemImage", R.drawable.myhis);
    map12.put("ItemText", "组号记录");
    menuList.add(map12);
    
    HashMap<String, Object> map13 = new HashMap<String, Object>();
    map13.put("ItemImage", R.drawable.setting);
    map13.put("ItemText", "软件设置");
    menuList.add(map13);
    
    HashMap<String, Object> map14 = new HashMap<String, Object>();
    map14.put("ItemImage", R.drawable.contactauthor);
    map14.put("ItemText", "联系作者");
    menuList.add(map14);

    HashMap<String, Object> map15 = new HashMap<String, Object>();
    map15.put("ItemImage", R.drawable.recommend);
    map15.put("ItemText", "推荐朋友");
    menuList.add(map15);
    
    SimpleAdapter gridMenuItem = new SimpleAdapter(MainViewActivity.this,
        menuList, // 数据源
        R.layout.gridviewitem, // xml实现
        new String[] { "ItemImage", "ItemText" }, // 对应map的Key
        new int[] { R.id.ItemImage, R.id.ItemText }); // 对应R的Id

    // 添加Item到网格中
    gridview.setAdapter(gridMenuItem);
    gridview.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
          long arg3) {
        Log.i(TAG, "click col:" + arg2 + " row:" + arg3);
        showProgressDialog("提示", "请稍等...");

        switch (arg2) {
        case 0:
          //规则说明
          Intent intent2 = new Intent(MainViewActivity.this,
              ShuangseRulesActivity.class);
          startActivity(intent2);
          break;
        case 1:
          //经验分享
          Intent intentx = new Intent(MainViewActivity.this, ExperienceListActivity.class);
          startActivity(intentx);
          break;
        case 2:
          //红球走势
          if(appContext == null || appContext.getAllHisData().size() < 1) {
            hideProgressBox();
            InfoMessageBox("警告","无历史数据信息，请先点击<数据更新>更新历史数据.");
          } else {
            Intent intent = new Intent(MainViewActivity.this,
                RedMissingDataActivity.class);
            intent.putExtra("FROM", "MainViewActivity");
            startActivity(intent);
          }
          break;
        case 3:
          //篮球走势
          if(appContext == null || appContext.getAllHisData().size() < 1) {
            hideProgressBox();
            InfoMessageBox("警告","无历史数据信息，请先点击<数据更新>更新历史数据.");
          } else {
            Intent intent3 = new Intent(MainViewActivity.this,
                BlueMissingDataActivity.class);
            intent3.putExtra("FROM", "MainViewActivity");
            startActivity(intent3);
          }
          break;
        case 4:
          //冷热走势
          if(appContext == null || appContext.getAllHisData().size() < 1) {
            hideProgressBox();
            InfoMessageBox("警告","无历史数据信息，请先点击<数据更新>更新历史数据.");
          } else {
            Intent intent = new Intent(MainViewActivity.this, RedMissingTrendActivity.class);
            intent.putExtra("FROM", "MainViewActivity");
            startActivity(intent);
          }
          break;
        case 5:
          //胆拖及复式组号
          hideProgressBox();
          Intent intent4x = new Intent(MainViewActivity.this, DantuoCombineActivity.class);
          startActivity(intent4x);
          break;
        case 6:
          //旋转组号
          hideProgressBox();
          Intent intent4 = new Intent(MainViewActivity.this, SmartCombineActivity.class);
          startActivity(intent4);
          break;
        case 7:
          //软件推荐红球
          hideProgressBox();
        //查看软件荐红历史记录
          Intent intent5x = new Intent(MainViewActivity.this,
              RecommandHisActivity.class);
          startActivity(intent5x);
          break;
        case 8:
          //条件查询
          hideProgressBox();
          Intent intent5 = new Intent(MainViewActivity.this,
              ConditionSearchActivity.class);
          startActivity(intent5);
          //InfoMessageBox("信息","火速开发中,将提供：\n1）查询某注号码是否中过奖；\n2）查询一组多个红球的历史中奖情况，例如查询某10红球组在历史上的是否中过奖等功能。\n请等待版本更新，谢谢支持.");
          break;
        case 9:
          //中奖查询
          if(appContext.getAllHisData().size() < 1) {
            hideProgressBox();
            InfoMessageBox("警告","无历史数据信息，请先点击<数据更新>更新历史数据.");
          } else {
            Intent intent6 = new Intent(MainViewActivity.this,
                ShuangseHitSearchActivity.class);
            startActivity(intent6);
          }
          break;
        case 10:
          // 数据更新
          downloadHistoryData();
          break;
        case 11:
          //我的记录
        //设置
          hideProgressBox();
          Intent myhislauncher = new Intent().setClass(MainViewActivity.this,
              MyHisActivity.class);
          startActivity(myhislauncher);
          break;
        case 12:
          //设置
          hideProgressBox();
          Intent launcher = new Intent().setClass(MainViewActivity.this,
              SettingActivity.class);
          startActivity(launcher);
          break;
        case 13:
          //联系作者
          Intent intent6 = new Intent(MainViewActivity.this,
              ContactAuthorActivity.class);
          startActivity(intent6);
          break;
        case 14:
          //推荐给朋友，导向发送短信页面
          doRecommend();
          break;
        }
      }
    });
  }
    
  /*调用系统短信接口发送短信*/
  private void doRecommend() {
    Uri smsToUri = Uri.parse("smsto:");
    Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
    intent.putExtra("sms_body", this.appContext.getSmsText());
    startActivity(intent);
  }

  Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<MainViewActivity> mActivity;

    MHandler(MainViewActivity mAct) {
      mActivity = new WeakReference<MainViewActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      MainViewActivity theActivity = mActivity.get();

      if (theActivity.progressDialog != null) {
        theActivity.progressDialog.dismiss();
      }

      switch (msg.what) {
      case DATAISLATESTMSG:
        theActivity.InfoMessageBox("信息", "已经是最新数据了.");
        break;
      case DATAUPDATESUCCESSMSG:
        theActivity.InfoMessageBox("信息", "数据更新成功.");
        break;
      case DATAUPDATEFAILMSG:
        theActivity.InfoMessageBox("错误", "数据更新失败，请稍候再试.");
        break;
      case REGPHONEINFOSUCCESS:
        SharedPreferences.Editor editor = theActivity.sharedPreferences.edit();
        editor.putBoolean("if_phone_info_registered",true);
        editor.commit();
        theActivity.ifPhoneInfoRegistered = true;
        MainViewActivity.ifRegOrUpdateDone = true;
        break;
        
      case BACKGROUNDCHECKMSG:
        ControlMsg apkObj = (ControlMsg)msg.obj;
        ifBackgroundCheckDone = true;
        if(apkObj != null) {
          StringBuffer sb = new StringBuffer();
          if(apkObj.getInfoType().equals("newapk")) {
            sb.append(apkObj.getText()).append("<br><a href='")
                .append(apkObj.getUrl()).append("'>")
                .append(apkObj.getUrl()).append("</a><br>")
                .append("版本号：").append(apkObj.getVersion()).append("<br>");
          } else {//message
            sb.append(apkObj.getText()).append("<br>");
          }
          MagicTool.customInfoMsgBox("信息", sb.toString(), theActivity).show();
        }
        break;
        
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

  // 更新数据
  private void downloadHistoryData() {
    new Thread() {
      public void run() {
        Log.i(TAG, "Start to update data from server.");
        int localLatestItemId = appContext.getLatestItemIDFromLocalDB();
        int startItemID = localLatestItemId;
        Log.i(TAG, "localLatest ItemId:" + localLatestItemId);

        String URLtoServer = appContext.getServerAddr()
            + "GetHistoryDataAction.do?StartItemID=" + startItemID;
        Log.i(TAG, "downloadHistoryData(): URL: " + URLtoServer);
        HttpGet httpGet = new HttpGet(URLtoServer);
        try {
          HttpResponse response = appContext.getHttpClient()
              .execute(httpGet);
          if (response != null
              && response.getStatusLine().getStatusCode() == 200) {
            String responseContent = EntityUtils.toString(response.getEntity())
                .trim();
            Log.v(TAG, "downloadHistoryData() responseContent: "
                + responseContent);

            if (responseContent.equals("NOMOREDATA")) {
              msgHandler.obtainMessage(DATAISLATESTMSG).sendToTarget();
              return;
            } else {
              // start to parse data and store it into local DB
              appContext.parseAndStoreItemsData(responseContent);
              /* 将本地数据库的数据重新全部读入到本地cache中 */
              appContext.loadLocalHisDataIntoCache();
              
              msgHandler.obtainMessage(DATAUPDATESUCCESSMSG).sendToTarget();
           }
          } else {
            if (response != null) {
              response.getEntity().consumeContent();
            }
            Log.e(TAG, "download data failed since server returns is not 200.");
            msgHandler.obtainMessage(DATAUPDATEFAILMSG).sendToTarget();
            return;
          }
        } catch (Exception e) {

          Log.e(TAG, "download data failed since the exception.");
          e.printStackTrace();
          msgHandler.obtainMessage(DATAUPDATEFAILMSG).sendToTarget();
          return;
        }
      }
    }.start();
  }

  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(MainViewActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        }).create();
    notifyDialog.show();
  }

  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(MainViewActivity.this);
    progressDialog.setTitle(title);
    progressDialog.setMessage(msg);
    progressDialog.setCancelable(false);
    progressDialog.show();
  }

  private void hideProgressBox() {
    if (progressDialog != null) {
      progressDialog.dismiss();
      progressDialog = null;
    }
  }
  
  protected void onPause() {
    super.onPause();
    hideProgressBox();
  }

  @Override
  protected void onDestroy() {
    if(backgroundThread != null) {
      backgroundThread.interrupt();
      backgroundThread = null;
    }
    super.onDestroy();
  }

  @Override
  protected void onStart() {
    //check once for the SMS text that for doRecommend
    if((checkSmsTextThread == null)  && (!ifSMSTextReadDone) ){
      checkSmsTextThread = new Thread()  {
        public void run() {
          try{
              String URLtoServer = appContext.getServerAddr() + "GetSmsTextAction.do";
              HttpGet httpGet = new HttpGet(URLtoServer);
              Log.i(TAG, "get SMS Text URL: " + URLtoServer);
              
              HttpResponse response = appContext.getHttpClient().execute(httpGet);
              
              if (response != null && response.getStatusLine().getStatusCode() == 200) {
                
                String responseContent = EntityUtils.toString(response.getEntity(), HTTP.UTF_8).trim();
                Log.i(TAG, "get SMS text returned: " + responseContent);
                appContext.setSmsText(responseContent);
                ifSMSTextReadDone = true;
              }else {
                if((response != null) && (response.getEntity() != null)) {
                  response.getEntity().consumeContent();
                }
                Log.e(TAG, "get SMS text failed since server returns is not 200.");
                return;
              }
            } catch (Exception e) {
              e.printStackTrace();
              Log.w(TAG, "got exception during get SMS text.");
            }
        }//end run
      };
      
      checkSmsTextThread.start();
    }
    
    if((backgroundThread == null) && (!ifBackgroundCheckDone)) {
      //only check once for the message delivered from server side
      backgroundThread = new BackgroundCheckThread(this.appContext, this);
      backgroundThread.start();
    }
    if(!ifRegOrUpdateDone) {
      new Thread() {
          public void run() {
            try {
              String URLtoServer = appContext.getServerAddr() + "RegPhoneInformationAction.do";
              if(!ifPhoneInfoRegistered) {
                URLtoServer += "?Action=Reg";
              } else {
                URLtoServer += "?Action=Update";
              }
              
              AndroidPhoneInfo phoneInfo = appContext.getSystemInformation();
              Log.i(TAG, "register phone information: " + phoneInfo.toString());
              
              HttpPost httpPost = new HttpPost(URLtoServer);
              
              //attach phone information to server
              List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.DEVICEID, phoneInfo.getDeviceId()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.MSISDNMDN, phoneInfo.getMsisdnMdn()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.NETWORKOPERATOR, phoneInfo.getNetworkOperator()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.NETWORKOPERATORNAME, phoneInfo.getNetworkOperatorName()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.NETWORKTYPE, String.valueOf(phoneInfo.getNetworkType())));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.PHONETYPE, String.valueOf(phoneInfo.getPhoneType())));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.SIMOPERATOR, phoneInfo.getSimOperator()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.SIMOPERATORNAME, phoneInfo.getSimOperatorName()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.SIMSERIALNUMBER, phoneInfo.getSimSerialNumber()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.SOFTVERSION, phoneInfo.getSoftVersion()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.SUBSCRIBERID, phoneInfo.getSubscriberId()));
              httpParams.add(new BasicNameValuePair(AndroidPhoneInfo.APKVERSION, appContext.getVersion()));
              
              httpPost.setEntity(new UrlEncodedFormEntity(httpParams, HTTP.UTF_8));
              
              Log.i(TAG, "register phone information URL: " + URLtoServer);
              
              HttpResponse response = appContext.getHttpClient().execute(httpPost);
              
              if (response != null && response.getStatusLine().getStatusCode() == 200) {
                
                String responseContent = EntityUtils.toString(response.getEntity()).trim();
                Log.i(TAG, "register phone information responseContent: " + responseContent);
                
                Message msg = new Message();
                msg.what = MainViewActivity.REGPHONEINFOSUCCESS;;
                msgHandler.sendMessage(msg);
              }else {
                if((response != null) && (response.getEntity() != null)) {
                  response.getEntity().consumeContent();
                }
                Log.e(TAG, "register phone information failed since server returns is not 200.");
                return;
              }
            
            } catch (Exception e) {
              e.printStackTrace();
            Log.w(TAG, "got exception during register phone information.");
          }
        }//end run
      }.start();
    }
    
    super.onStart();
  }

  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      // land
    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      // port
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    //menu.add(Menu.NONE, Menu.FIRST + 1, 5, "关于本软件").setIcon(R.drawable.icon_4_n);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case Menu.FIRST + 1:
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // 是否触发按键为back键
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      AlertDialog notifyDialog = new AlertDialog.Builder(MainViewActivity.this)
          .setTitle("提示")
          .setMessage("点<确定>就退出软件了哦...")
          .setPositiveButton(R.string.OK,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  
                  //退出之前保存当前的选号
                  SharedPreferences.Editor editor = sharedPreferences.edit();
                  editor.putString(appContext.My_Selection_Red_String, 
                      MagicTool.getDispArrangedStr(appContext.getCurrentSelection().getSelectedRedNumbers()));
                  editor.putString(appContext.My_Selection_Blue_String, 
                      MagicTool.getDispArrangedStr(appContext.getCurrentSelection().getSelectedBlueNumbers()));
                  editor.putString(appContext.My_Selection_Red_Dan_Str, 
                      MagicTool.getDispArrangedStr(appContext.getCurrentSelection().getSelectedRedDanNumbers()));
                  editor.putString(appContext.My_Selection_Red_Tuo_Str, 
                      MagicTool.getDispArrangedStr(appContext.getCurrentSelection().getSelectedRedTuoNumbers()));
                  editor.putString(appContext.My_Selection_BlueForDanTuo_String, 
                      MagicTool.getDispArrangedStr(appContext.getCurrentSelection().getSelectedBlueNumbersForDanTuo()));
                  editor.commit();
                  
                  MainViewActivity.this.finish();
                }
              })
          .setNegativeButton(R.string.cancle,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  return;
                }
              }).create();
      notifyDialog.show();

      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }

}
