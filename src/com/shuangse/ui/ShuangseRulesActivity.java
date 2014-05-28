package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.shuangse.util.MagicTool;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ShuangseRulesActivity extends Activity {
  private static final String TAG = "ShuangseRulesActivity";
  private static final int ViewSwitchCompletedMsg = 2;
  private ViewPager localViewPager;
  private PrivatePagerAdapter localAdapter;

  // �´ο���ǰ��������ʱ�䣨��ÿ2��4��7��8���㣩
  private Date nextOpenEndTime;
  // Timer task handler to update the video timer on screen
  private ScheduledThreadPoolExecutor execUpdateDisTime = null;

  private LayoutInflater mInflater;
  private List<View> mListViews;
  private View contentLayout1 = null;
  private View contentLayout2 = null;
  private View contentLayout3 = null;
  private Button btn1;
  private Button btn2;
  private Button btn3;
  private Handler msgHandler = new MHandler(this);

  static class MHandler extends Handler {
    WeakReference<ShuangseRulesActivity> mActivity;

    MHandler(ShuangseRulesActivity mAct) {
      mActivity = new WeakReference<ShuangseRulesActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      ShuangseRulesActivity theActivity = mActivity.get();

      switch (msg.what) {
      case ViewSwitchCompletedMsg:
        switch (msg.arg1) {
        case 0:
          theActivity.btn1.setBackgroundResource(R.drawable.rectbg);
          theActivity.btn2.setBackgroundResource(R.color.transparent);
          theActivity.btn3.setBackgroundResource(R.color.transparent);
          break;
        case 1:
          theActivity.btn2.setBackgroundResource(R.drawable.rectbg);
          theActivity.btn1.setBackgroundResource(R.color.transparent);
          theActivity.btn3.setBackgroundResource(R.color.transparent);
          break;
        case 2:
          theActivity.btn3.setBackgroundResource(R.drawable.rectbg);
          theActivity.btn2.setBackgroundResource(R.color.transparent);
          theActivity.btn1.setBackgroundResource(R.color.transparent);
          break;
        default:
          break;
        }
        break;
      default:
        break;
      }

      super.handleMessage(msg);
    }
  };

  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // land
    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        // port
    }
  }
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // ���±���
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.rules);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        R.layout.custom_title);

    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_rules_reminder);
    
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "�ڱ�ҳ�е����Ӧ����Ŀ����ϸ�Ķ���Ŀ���ݣ�����Ӧ�����Ķ����淨��顷�е���ת��� �� ��ʽ��ŵ����ơ�";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, ShuangseRulesActivity.this).show();
      }
    });    
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    

    localAdapter = new PrivatePagerAdapter();
    localViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
    localViewPager.setAdapter(localAdapter);

    btn1 = (Button) findViewById(R.id.btn1);
    btn2 = (Button) findViewById(R.id.btn2);
    btn3 = (Button) findViewById(R.id.btn3);
    btn1.setBackgroundResource(R.drawable.rectbg);
    btn2.setBackgroundResource(R.color.transparent);
    btn3.setBackgroundResource(R.color.transparent);
    btn1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        localViewPager.setCurrentItem(0);
      }
    });
    btn2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        localViewPager.setCurrentItem(1);
      }
    });
    btn3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        localViewPager.setCurrentItem(2);
      }
    });

    mListViews = new ArrayList<View>();
    mInflater = getLayoutInflater();
    contentLayout1 = mInflater.inflate(R.layout.rules_content1, null);
    contentLayout2 = mInflater.inflate(R.layout.rules_content2, null);
    contentLayout3 = mInflater.inflate(R.layout.rules_content3, null);

    mListViews.add(contentLayout1);
    mListViews.add(contentLayout2);
    mListViews.add(contentLayout3);

    // ��ʼ����ǰ��ʾ�ĵ�һ��view
    localViewPager.setCurrentItem(0);
    TextView time_info = (TextView) contentLayout1.findViewById(R.id.time_info);

    nextOpenEndTime = getBuyEndTime();
    Date nowDate = getSystemCurrentTime();

    long interval = (nextOpenEndTime.getTime() - nowDate.getTime()) / 1000; // s
    Log.i(TAG, "nextDate:" + nextOpenEndTime
        + " nowDate:" + nowDate + " nextDate.getTime():"
        + nextOpenEndTime.getTime() + " nowDate.getTime():" + nowDate.getTime()
        + " distance(s):" + interval);
    StringBuffer nextDateBuf = new StringBuffer();

    long day = interval / (24 * 3600);// ��
    long hour = interval % (24 * 3600) / 3600;// Сʱ
    long minute = interval % 3600 / 60;// ����
    long second = interval % 60;// ��

    nextDateBuf.append(day);
    nextDateBuf.append("��");
    nextDateBuf.append(hour);
    nextDateBuf.append("Сʱ");
    nextDateBuf.append(minute);
    nextDateBuf.append("��");
    nextDateBuf.append(second);
    nextDateBuf.append("��");

    // ��ʼ����1��view����Ϣ
    time_info.setText(nextDateBuf.toString());

    localViewPager.setOnPageChangeListener(new OnPageChangeListener() {

      @Override
      public void onPageSelected(int viewIndex) {
        // activity��n��n+1������n+1�����غ���ô˷���
        View v = mListViews.get(viewIndex);
        Log.i(TAG, "viewIndex:" + viewIndex);
        switch (viewIndex) {
        case 0:
          Message msg0 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg0.arg1 = 0;
          msg0.sendToTarget();
          break;
        case 1:
          TextView smartText = (TextView) v.findViewById(R.id.smartText);
          smartText.setText(Html
              .fromHtml("��ת��ţ�Ҳ�д������, ��һ����������Ʊ��ʦ�Ƕ�.�����·�����Ͷע����, �������ִ���ѧ����ϸ��Ǽ��㷽����Ѱ���Ż����, "
                  + "�����ٵ�Ͷע���ﵽͬ���н������µ���󸲸�, �ȸ�ʽͶע����ȫ���Ǹ���ʡ, ����6��5, ��6��4����ת���ģʽ�ǳ�"
                  + "�õ���Ϸ�ʽ. <br><br>����ѡ��10�������1���������, ȫ��ʽ�蹺��210ע����, ��������ת���ֻ��Ҫ"
                  + "16ע�������������6��5(Ҳ��: ���ѡ���10������������ڵ�6������, ���ô�����Ϸ�ֻ�蹺��16ע���������"
                  + "����֤��5������Ľ���, �����л�����6��������). �������Ƶ���ת���ģʽ����11��������6��5��26ע��."
                  + "<br><br>Ŀǰȫ�����µġ���ת���/�������ģʽ���о��ɹ�����Iliya Bluskow��ʿ��2010��"
                  + "�����<a href=\"http://www.amazon.ca/Combinatorial-lottery-systems-wheels-guaranteed/dp/0968950205\">��Combinatorial Lottery Systems (Wheels) with Guaranteed Wins��</a>"
                  + "һ��������."));
          smartText.setMovementMethod(LinkMovementMethod.getInstance());
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
    // �����Ҫ�첽�������ݣ��ڴ��м���
    // this.loadContent();
  }

  protected void onResume() {
    // ��ʼ����ʱ��
    execUpdateDisTime = new ScheduledThreadPoolExecutor(1);
    execUpdateDisTime.scheduleAtFixedRate(new UpdateDisplayTimeTask(), 0, 1000,
        TimeUnit.MILLISECONDS);
    super.onResume();
  }

  protected void onPause() {
    if (execUpdateDisTime != null) {
      execUpdateDisTime.shutdownNow();
      execUpdateDisTime = null;
    }
    super.onPause();
  }

  // ��ȡ���ڿ���ǰ�������۵�ʱ��㣨���ܶ����ģ�����8�㣩
  private Date getBuyEndTime() {
    Calendar currentTime = Calendar.getInstance();
    Date currentDate = getSystemCurrentTime();
    currentTime.setTime(currentDate);

    int curHour = currentTime.get(Calendar.HOUR_OF_DAY);
    int curWeekN = currentTime.get(Calendar.DAY_OF_WEEK);
    //��ʱ��curWeekN��ֵ���£�
    // "������", "����һ", "���ڶ�", "������", "������", "������", "������",  
    //    1              2              3              4             5             6             7
    Log.i(TAG, "curWeekN:" + curWeekN + "curHour:"  + curHour + " currentTime:"
            + currentTime);

    // �������day���Զ�����month �� year
    switch (curWeekN) {
    case Calendar.SUNDAY:
      if(curHour >=20) {
        currentTime.add(Calendar.DAY_OF_MONTH, 2);
      }
      break;
    case Calendar.MONDAY:
      currentTime.add(Calendar.DAY_OF_MONTH, 1);
      break;
    case Calendar.TUESDAY:
      if(curHour >= 20) {
        currentTime.add(Calendar.DAY_OF_MONTH, 2);
      }
      break;
    case Calendar.WEDNESDAY:
      currentTime.add(Calendar.DAY_OF_MONTH, 1);
      break;
    case Calendar.THURSDAY:
      if(curHour >= 20) {
        currentTime.add(Calendar.DAY_OF_MONTH, 3);
      }
      break;
    case Calendar.FRIDAY:
        currentTime.add(Calendar.DAY_OF_MONTH, 2);
      break;
    case Calendar.SATURDAY:
      currentTime.add(Calendar.DAY_OF_MONTH, 1);
      break;
    default:
      break;
    }
    
    int openyear, openmonth, openday;
    openyear = currentTime.get(Calendar.YEAR);
    openmonth = currentTime.get(Calendar.MONTH) + 1;
    openday = currentTime.get(Calendar.DAY_OF_MONTH);

    // ÿ�ܶ����ġ��տ���,����8���������
    StringBuffer nextTimeBuf = new StringBuffer();
    nextTimeBuf.append(openyear);
    nextTimeBuf.append("-");
    nextTimeBuf.append(openmonth);
    nextTimeBuf.append("-");
    nextTimeBuf.append(openday);
    nextTimeBuf.append(" ");
    nextTimeBuf.append("20:00:00");
    try {
      
      Log.i(TAG,"nextTimeBuf String:"+ nextTimeBuf.toString());
      SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+0800")); //
      Date openEndTime = timeFormat.parse(nextTimeBuf.toString());
      Log.i(TAG, "openEndTime Date: " + openEndTime.toString());
      
      return openEndTime;
      
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // ��ȡϵͳ��ǰ������ʱ����ʱ��
  private Date getSystemCurrentTime() {
    Calendar nowTime = Calendar.getInstance();
    nowTime.setTimeInMillis(System.currentTimeMillis());
    return (nowTime.getTime());
  }

  private class PrivatePagerAdapter extends PagerAdapter {

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

  private class UpdateDisplayTimeTask implements Runnable {
    public void run() {
      Date nowDate = getSystemCurrentTime();
      long interval = (nextOpenEndTime.getTime() - nowDate.getTime()) / 1000; // s
      final StringBuffer nextDateBuf = new StringBuffer();

      long day = interval / (24 * 3600);// ��
      long hour = interval % (24 * 3600) / 3600;// Сʱ
      long minute = interval % 3600 / 60;// ����
      long second = interval % 60;// ��

      nextDateBuf.append(day);
      nextDateBuf.append("��");
      nextDateBuf.append(hour);
      nextDateBuf.append("Сʱ");
      nextDateBuf.append(minute);
      nextDateBuf.append("��");
      nextDateBuf.append(second);
      nextDateBuf.append("��");

      runOnUiThread(new Runnable() {
        public void run() {
          TextView time_info = (TextView) contentLayout1
              .findViewById(R.id.time_info);
          time_info.setText(nextDateBuf.toString());
        }
      });
    }
  }

  protected void onDestroy() {
    super.onDestroy();
  }

}
