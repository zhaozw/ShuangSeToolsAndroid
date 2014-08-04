package com.shuangse.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class CommonDispActivity extends Activity {
  private TextView dispItemTitle;
  private TextView dispItemBody;
  private ProgressDialog progressDialog;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.common_disp_activity);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
 
    this.showProgressDialog("信息", "请稍等，全力加载中...");
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.detail_description_common);
    
    dispItemTitle = (TextView)findViewById(R.id.dispItemTitle);
    dispItemBody = (TextView)findViewById(R.id.dispItemBody);
    
    Bundle bundle = this.getIntent().getExtras();
    String dispItemId = bundle.getString("ItemId","");
    Log.i("ItemId", "Item ID:" + dispItemId);
    
    if(dispItemId != null && dispItemId.equals("redTrend")) {
        titleTextView.setText(R.string.detail_description_select_num);
        dispItemTitle.setText(R.string.redTrend);
        dispItemBody.setText(R.string.red_trend_details_text);
    } else if(dispItemId != null && dispItemId.equals("blueTrend")) {
        titleTextView.setText(R.string.detail_description_select_num);
        dispItemTitle.setText(R.string.blueTrend);
        dispItemBody.setText(R.string.blue_trend_details_text);
    } else if(dispItemId != null && dispItemId.equals("condTrend")) {
        titleTextView.setText(R.string.detail_description_select_num);
        dispItemTitle.setText(R.string.condTrend);
        dispItemBody.setText(R.string.cond_trend_details_text);
    } else if(dispItemId != null && dispItemId.equals("xuanzhuanZuHao")) {
        titleTextView.setText(R.string.detail_description_zu_hao);
        dispItemTitle.setText(R.string.xuanzhuan_zuhao_title);
        dispItemBody.setText(R.string.xuanzhuan_zuhao_details_text);
    }  else if(dispItemId != null && dispItemId.equals("conditionsZuHao")) {
        titleTextView.setText(R.string.detail_description_zu_hao);
        dispItemTitle.setText(R.string.conditions_zuhao_title);
        dispItemBody.setText(R.string.conditions_zuhao_details_text);
    }  else if(dispItemId != null && dispItemId.equals("dantuoZuHao")) {
        titleTextView.setText(R.string.detail_description_zu_hao);
        dispItemTitle.setText(R.string.dantuo_zuhao_title);
        dispItemBody.setText(R.string.dantuo_zuhao_details_text);
    }  else if(dispItemId != null && dispItemId.equals("fushiZuHao")) {
        titleTextView.setText(R.string.detail_description_zu_hao);
        dispItemTitle.setText(R.string.fushi_zuhao_title);
        dispItemBody.setText(R.string.fushi_zuhao_details_text);
    } else if(dispItemId != null && dispItemId.equals("softwareIntro")) {
        titleTextView.setText(R.string.software_intro_title);
        dispItemTitle.setText(R.string.software_intro);
        dispItemBody.setText(R.string.software_intro_text);
    }  else if(dispItemId != null && dispItemId.equals("shuangseIntro")) {
        titleTextView.setText(R.string.software_intro_title);
        dispItemTitle.setText(R.string.shuangse_intro);
        dispItemBody.setText(R.string.shuangse_intro_text);
    }  else if(dispItemId != null && dispItemId.equals("xuanzhuanIntro")) {
        titleTextView.setText(R.string.software_intro_title);
        dispItemTitle.setText(R.string.xuanzhuan_intro);
        dispItemBody.setText(R.string.xuanzhuan_intro_text);
    }  else if(dispItemId != null && dispItemId.equals("dantuoIntro")) {
        titleTextView.setText(R.string.software_intro_title);
        dispItemTitle.setText(R.string.dantuo_intro);
        dispItemBody.setText(R.string.dantuo_intro_text);
    }  else if(dispItemId != null && dispItemId.equals("condIntro")) {
        titleTextView.setText(R.string.software_intro_title);
        dispItemTitle.setText(R.string.cond_intro);
        dispItemBody.setText(R.string.cond_intro_text);
    }  
    else {
        InfoMessageBox("错误","显示内容出错.");
    }
    hideProgressBox();
  }
  
  
  
  @Override
  protected void onPause() {
    super.onPause();
    hideProgressBox();
  }

  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(CommonDispActivity.this);
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
    AlertDialog notifyDialog = new AlertDialog.Builder(CommonDispActivity.this)
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
