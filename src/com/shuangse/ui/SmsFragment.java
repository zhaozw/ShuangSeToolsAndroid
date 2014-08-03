package com.shuangse.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SmsFragment extends BaseFragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        isInit = true;
        return inflater.inflate(R.layout.exprshare_content1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 设置第1个View中的内容
      Button sendSmsBtn = (Button) view.findViewById(R.id.sendsms_btn);
      final EditText smsEdit = (EditText) view.findViewById(R.id.smsText);
      sendSmsBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //发短信
          String message = smsEdit.getText().toString();
          if (TextUtils.isEmpty(message)) {
            InfoMessageBox("提醒", "请填写短信内容哦");
            return;
          }
          // 发送短信
          sendSMS("13914700611", message);
          InfoMessageBox("成功", "短信发送成功！");
        }
      });

      
    }

    @Override
    protected void showData() {
        
    }

    private void InfoMessageBox(String title, String msg) {
        AlertDialog notifyDialog = new AlertDialog.Builder(this.getActivity()).setTitle(title).setMessage(msg)
            .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            }).create();
        notifyDialog.show();
   }
    
    private void sendSMS(String phoneNum, String messageText) {
        SmsManager sms = SmsManager.getDefault();

        List<String> messages = sms.divideMessage(messageText);

        for (String message : messages) {
          sms.sendTextMessage(phoneNum, null, message, PendingIntent.getBroadcast(mContext,
                  0, new Intent("com.shuangse.ui.SMS_SENT_ACTION"), 0), null);
        }
      }

}
