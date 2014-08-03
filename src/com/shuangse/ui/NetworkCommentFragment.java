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

import com.shuangse.util.MagicTool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NetworkCommentFragment extends BaseFragment {
    private static final String TAG = "NetworkCommentFragment";
    private static final int MailSendCompletedMsg = 20;
    private ProgressDialog progDialog;

    private Handler msgHandler = new MHandler(this);
    static class MHandler extends Handler {
      WeakReference<NetworkCommentFragment> mActivity;

      MHandler(NetworkCommentFragment mAct) {
        this.mActivity = new WeakReference<NetworkCommentFragment>(mAct);
      }

      @Override
      public void handleMessage(Message msg) {
          NetworkCommentFragment theActivity = mActivity.get();
        
        switch (msg.what) {
        case MailSendCompletedMsg:
          if (theActivity.progDialog != null) {
            theActivity.progDialog.dismiss();
          }
          switch (msg.arg1) {
          case 1:
            theActivity.InfoMessageBox("成功", "保存成功，谢谢您的建议");
            break;
          case 0:
            theActivity.InfoMessageBox("失败", "保存失败，请检查你的网络是否正常！");
            break;
          }
          break;
        default:
          break;
        }
        super.handleMessage(msg);
      }
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        isInit = true;
        return inflater.inflate(R.layout.exprshare_content2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 第2个View中的内容
        Button sendMailBtn = (Button) view.findViewById(R.id.sendmail_btn);
        final EditText mailEdit = (EditText) view.findViewById(R.id.mailText);
        final EditText contactbackEdit = (EditText) view
                .findViewById(R.id.contactback);

        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发邮件
                String contactBack = contactbackEdit.getText().toString();
                if (contactBack == null) {
                    contactBack = "";
                }
                String message = mailEdit.getText().toString();
                // 替换掉非法字符
                message = MagicTool.replaceInvalidChars(message);

                if (TextUtils.isEmpty(message)) {
                    InfoMessageBox("提醒", "请填写建议内容哦");
                    return;
                }
                progDialog = new ProgressDialog(
                        NetworkCommentFragment.this.getActivity());
                progDialog.setTitle("提示");
                progDialog.setMessage("请稍等，正在保存您的建议...");
                progDialog.setCancelable(false);
                progDialog.show();
                Thread sendMailThread = new PostSuggestionThread(contactBack, message);
                sendMailThread.start();
            }
        });
    }

    @Override
    protected void showData() {

    }

    private void InfoMessageBox(String title, String msg) {
        AlertDialog notifyDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).create();
        notifyDialog.show();
    }

    /**
     * 
     * Thread for post the suggestion to server
     * 
     */
    private class PostSuggestionThread extends Thread {
        private String contact;
        private String suggestion;

        PostSuggestionThread(String contactBack, String message) {
            contact = contactBack;
            suggestion = message;
        }

        public void run() {
            try {
                HttpPost httpPost = new HttpPost(mContext.getServerAddr()
                        + "SuggestionAction.do");
                List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
                httpParams.add(new BasicNameValuePair("Action", "add"));
                httpParams.add(new BasicNameValuePair("ContactBack", contact));
                httpParams
                        .add(new BasicNameValuePair("Suggestion", suggestion));
                httpPost.setEntity(new UrlEncodedFormEntity(httpParams,
                        HTTP.UTF_8));

                HttpResponse response = mContext.getHttpClient().execute(
                        httpPost);
                if (response != null
                        && response.getStatusLine().getStatusCode() == 200) {
                    String responseContent = EntityUtils.toString(
                            response.getEntity()).trim();
                    Log.v(TAG, "responseContent: " + responseContent);
                    Message msg = msgHandler
                            .obtainMessage(MailSendCompletedMsg);
                    msg.arg1 = 1;
                    msg.sendToTarget();
                } else {
                    if (response != null) {
                        response.getEntity().consumeContent();
                    }
                    Log.e(TAG, "Server response is not 200.");
                    Message msg = msgHandler
                            .obtainMessage(MailSendCompletedMsg);
                    msg.arg1 = 0;
                    msg.sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Message msg = msgHandler.obtainMessage(MailSendCompletedMsg);
                msg.arg1 = 0;
                msg.sendToTarget();
            }
        }
    };

}
