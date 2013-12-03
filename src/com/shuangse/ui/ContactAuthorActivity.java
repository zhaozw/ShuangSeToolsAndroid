package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.apache.http.protocol.HTTP;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.util.MagicTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ContactAuthorActivity extends Activity {
  private static final String TAG = "ContactAuthorActivity";

  private ShuangSeToolsSetApplication appContext;
  private Thread sendMailThread;
  private ProgressDialog progDialog;

  private static final int ViewSwitchCompletedMsg = 1;
  private static final int MailSendCompletedMsg = 2;

  private ViewPager contactViewPager;
  private ContactPagerAdapter contactPagerAdapter;
  private LayoutInflater mInflater;
  private List<View> mListViews;
  private View contactLayout1 = null;
  private View contactLayout2 = null;
  private View contactLayout3 = null;
  private Button contactBtn1;
  private Button contactBtn2;
  private Button contactBtn3;

  private Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<ContactAuthorActivity> mActivity;

    MHandler(ContactAuthorActivity mAct) {
      this.mActivity = new WeakReference<ContactAuthorActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      ContactAuthorActivity theActivity = mActivity.get();

      switch (msg.what) {
      case ViewSwitchCompletedMsg:
        switch (msg.arg1) {
        case 0:
          theActivity.contactBtn1.setBackgroundResource(R.drawable.rectbg);
          theActivity.contactBtn2.setBackgroundResource(R.color.transparent);
          theActivity.contactBtn3.setBackgroundResource(R.color.transparent);
          break;
        case 1:
          theActivity.contactBtn2.setBackgroundResource(R.drawable.rectbg);
          theActivity.contactBtn1.setBackgroundResource(R.color.transparent);
          theActivity.contactBtn3.setBackgroundResource(R.color.transparent);
          break;
        case 2:
          theActivity.contactBtn3.setBackgroundResource(R.drawable.rectbg);
          theActivity.contactBtn2.setBackgroundResource(R.color.transparent);
          theActivity.contactBtn1.setBackgroundResource(R.color.transparent);
          break;
        default:
          break;
        }
        break;
      case MailSendCompletedMsg:
        if (theActivity.progDialog != null) {
          theActivity.progDialog.dismiss();
        }
        switch (msg.arg1) {
        case 1:
          theActivity.InfoMessageBox("�ɹ�", "����ɹ���лл���Ľ���");
          break;
        case 0:
          theActivity.InfoMessageBox("ʧ��", "����ʧ�ܣ�������������Ƿ�������");
          break;
        }
        break;
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ���±���
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.contactauth);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        R.layout.custom_title);

    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_contact_author);

    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "��ҳΪ���ṩ��ϵ������ߵ�;����������" +
                "ͨ���绰�����Ͷ��Ż��ṩ���罨��ķ�ʽ�� ���� �� �ͻ�������ϵ��" +
                "���ǽ��������ܵĴ��� �� ������ʵ�����롣���ĳɹ��������ǵĳɹ���";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, ContactAuthorActivity.this).show();
      }
    });
    
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    
    // ��ȡȫ������
    appContext = (ShuangSeToolsSetApplication) getApplication();

    contactPagerAdapter = new ContactPagerAdapter();
    contactViewPager = (ViewPager) findViewById(R.id.contactviewpager);
    contactViewPager.setAdapter(contactPagerAdapter);

    contactBtn1 = (Button) findViewById(R.id.contactbtn1);
    contactBtn2 = (Button) findViewById(R.id.contactbtn2);
    contactBtn3 = (Button) findViewById(R.id.contactbtn3);
    contactBtn1.setBackgroundResource(R.drawable.rectbg);
    contactBtn2.setBackgroundResource(R.color.transparent);
    contactBtn3.setBackgroundResource(R.color.transparent);
    contactBtn1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        contactViewPager.setCurrentItem(0);
      }
    });
    contactBtn2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        contactViewPager.setCurrentItem(1);
      }
    });
    contactBtn3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        contactViewPager.setCurrentItem(2);
      }
    });

    mListViews = new ArrayList<View>();
    mInflater = getLayoutInflater();
    contactLayout1 = mInflater.inflate(R.layout.contact_content1, null);
    contactLayout2 = mInflater.inflate(R.layout.contact_content2, null);
    contactLayout3 = mInflater.inflate(R.layout.contact_content3, null);

    mListViews.add(contactLayout1);
    mListViews.add(contactLayout2);
    mListViews.add(contactLayout3);

    // ��ʼ����ǰ��ʾ�ĵ�һ��view
    contactViewPager.setCurrentItem(0);
    Button dialBtn = (Button) contactLayout1.findViewById(R.id.dial_btn);
    dialBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // ��绰
        Intent telIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
            + "13914700611"));
        startActivity(telIntent);
      }
    });
    // ���õڶ���View�е�����
    Button sendSmsBtn = (Button) contactLayout2.findViewById(R.id.sendsms_btn);
    sendSmsBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // ������
        EditText smsEdit = (EditText) contactLayout2.findViewById(R.id.smsText);
        String message = smsEdit.getText().toString();
        if (TextUtils.isEmpty(message)) {
          InfoMessageBox("����", "����д��������Ŷ");
          return;
        }
        // ���Ͷ���
        sendSMS("13914700611", message);
        InfoMessageBox("�ɹ�", "���ŷ��ͳɹ���");
      }
    });

    // ������View�е�����
    Button sendMailBtn = (Button) contactLayout3
        .findViewById(R.id.sendmail_btn);
    sendMailBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // ���ʼ�
        EditText mailEdit = (EditText) contactLayout3
            .findViewById(R.id.mailText);
        EditText contactbackEdit = (EditText) contactLayout3
            .findViewById(R.id.contactback);
        String contactBack = contactbackEdit.getText().toString();
        if (contactBack == null) {
          contactBack = "";
        }
        String message = mailEdit.getText().toString();
        //�滻���Ƿ��ַ�
        message = MagicTool.replaceInvalidChars(message);
        
        if (TextUtils.isEmpty(message)) {
          InfoMessageBox("����", "����д��������Ŷ");
          return;
        }
        progDialog = new ProgressDialog(ContactAuthorActivity.this);
        progDialog.setTitle("��ʾ");
        progDialog.setMessage("���Եȣ����ڱ������Ľ���...");
        progDialog.setCancelable(false);
        progDialog.show();
        sendMailThread = new PostSuggestionThread(contactBack, message);
        sendMailThread.start();
      }
    });

    contactViewPager.setOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageSelected(int viewIndex) {
        // activity��n��n+1������n+1�����غ���ô˷���
        //View v = mListViews.get(viewIndex);
        Log.i(TAG, "viewIndex:" + viewIndex);
        switch (viewIndex) {
        case 0:
          Message msg0 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg0.arg1 = 0;
          msg0.sendToTarget();
          break;
        case 1:
          // EditText smsText = (EditText)v.findViewById(R.id.smsText);
          // �������?
          // smsText.setText("");

          Message msg1 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg1.arg1 = 1;
          msg1.sendToTarget();
          break;
        case 2:
          Message msg2 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg2.arg1 = 2;
          msg2.sendToTarget();
          break;
        default:
          break;
        }
      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2) {
        // ��n��n+1�������˷�����n����ǰ����
      }

      @Override
      public void onPageScrollStateChanged(int arg0) {
        // ״̬������0���У�1�����ڻ����У�2Ŀ��������
        /**
         * Indicates that the pager is in an idle, settled state. The current
         * page is fully in view and no animation is in progress.
         */
        // public static final int SCROLL_STATE_IDLE = 0;
        /**
         * Indicates that the pager is currently being dragged by the user.
         */
        // public static final int SCROLL_STATE_DRAGGING = 1;
        /**
         * Indicates that the pager is in the process of settling to a final
         * position.
         */
        // public static final int SCROLL_STATE_SETTLING = 2;

      }
    });

  }

  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(
        ContactAuthorActivity.this).setTitle(title).setMessage(msg)
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
      sms.sendTextMessage(phoneNum, null, message, PendingIntent.getBroadcast(
          ContactAuthorActivity.this, 0, new Intent(
              "com.shuangse.ui.SMS_SENT_ACTION"), 0), null);
      Log.i(TAG, "1 message sent, msg:" + message);
    }
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
        HttpPost httpPost = new HttpPost(appContext.getServerAddr()
            + "SuggestionAction.do");
        List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        httpParams.add(new BasicNameValuePair("Action", "add"));
        httpParams.add(new BasicNameValuePair("ContactBack", contact));
        httpParams.add(new BasicNameValuePair("Suggestion", suggestion));
        httpPost.setEntity(new UrlEncodedFormEntity(httpParams, HTTP.UTF_8));

        HttpResponse response = appContext.getHttpClient().execute(httpPost);
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
          String responseContent = EntityUtils.toString(response.getEntity()).trim();
          Log.v(TAG, "responseContent: " + responseContent);
          Message msg = msgHandler.obtainMessage(MailSendCompletedMsg);
          msg.arg1 = 1;
          msg.sendToTarget();
        } else {
          if(response != null) {
            response.getEntity().consumeContent();
          }
          Log.e(TAG, "Server response is not 200.");
          Message msg = msgHandler.obtainMessage(MailSendCompletedMsg);
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

  private class ContactPagerAdapter extends PagerAdapter {

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
      ((ViewPager) arg0).removeView(mListViews.get(arg1));
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public int getCount() {
      return mListViews.size();
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
      ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
      return mListViews.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
      return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
      return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }
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
