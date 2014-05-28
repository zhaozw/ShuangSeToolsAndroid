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

  // 下次开奖前截至销售时间（按每2，4，7晚8点算）
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

    // 更新标题
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
        String htmlMsg = "在本页中点击对应的栏目，仔细阅读栏目内容，尤其应认真阅读《玩法简介》中的旋转组号 比 复式组号的优势。";
        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, ShuangseRulesActivity.this).show();
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

    // 初始化当前显示的第一个view
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

    long day = interval / (24 * 3600);// 天
    long hour = interval % (24 * 3600) / 3600;// 小时
    long minute = interval % 3600 / 60;// 分钟
    long second = interval % 60;// 秒

    nextDateBuf.append(day);
    nextDateBuf.append("天");
    nextDateBuf.append(hour);
    nextDateBuf.append("小时");
    nextDateBuf.append(minute);
    nextDateBuf.append("分");
    nextDateBuf.append(second);
    nextDateBuf.append("秒");

    // 初始化第1个view的信息
    time_info.setText(nextDateBuf.toString());

    localViewPager.setOnPageChangeListener(new OnPageChangeListener() {

      @Override
      public void onPageSelected(int viewIndex) {
        // activity从n到n+1滑动，n+1被加载后掉用此方法
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
              .fromHtml("旋转组号：也叫聪明组合, 是一种由美国彩票大师盖尔.霍华德发明的投注方法, 它利用现代数学的组合覆盖计算方法搜寻最优化组合, "
                  + "用最少的投注数达到同等中奖概率下的最大覆盖, 比复式投注法的全覆盖更节省, 如中6保5, 中6保4等旋转组号模式是常"
                  + "用的组合方式. <br><br>比如选好10个红球和1个篮球号码, 全复式需购买210注号码, 而采用旋转组号只需要"
                  + "16注号码就能做到中6保5(也即: 如果选择的10个号码包含该期的6个红球, 运用聪明组合法只需购买16注号码就能做"
                  + "到保证中5个红球的奖项, 而且有机会中6个红球奖项). 其它类似的旋转组号模式有如11个数字中6保5需26注等."
                  + "<br><br>目前全球最新的“旋转组号/聪明组合模式”研究成果是由Iliya Bluskow博士在2010年"
                  + "出版的<a href=\"http://www.amazon.ca/Combinatorial-lottery-systems-wheels-guaranteed/dp/0968950205\">《Combinatorial Lottery Systems (Wheels) with Guaranteed Wins》</a>"
                  + "一书中描述."));
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
        // 从n到n+1滑动，此方法在n滑动前调用
      }

      @Override
      public void onPageScrollStateChanged(int arg0) {
        // 状态有三个0空闲，1是增在滑行中，2目标加载完毕
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
    // 如果需要异步加载数据，在此中加载
    // this.loadContent();
  }

  protected void onResume() {
    // 初始化定时器
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

  // 获取本期开奖前截至销售的时间点（按周二，四，日晚8点）
  private Date getBuyEndTime() {
    Calendar currentTime = Calendar.getInstance();
    Date currentDate = getSystemCurrentTime();
    currentTime.setTime(currentDate);

    int curHour = currentTime.get(Calendar.HOUR_OF_DAY);
    int curWeekN = currentTime.get(Calendar.DAY_OF_WEEK);
    //此时，curWeekN的值如下：
    // "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六",  
    //    1              2              3              4             5             6             7
    Log.i(TAG, "curWeekN:" + curWeekN + "curHour:"  + curHour + " currentTime:"
            + currentTime);

    // 下面调整day会自动调整month 和 year
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

    // 每周二、四、日开奖,晚上8点截至销售
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

  // 获取系统当前东八区时区的时间
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

      long day = interval / (24 * 3600);// 天
      long hour = interval % (24 * 3600) / 3600;// 小时
      long minute = interval % 3600 / 60;// 分钟
      long second = interval % 60;// 秒

      nextDateBuf.append(day);
      nextDateBuf.append("天");
      nextDateBuf.append(hour);
      nextDateBuf.append("小时");
      nextDateBuf.append(minute);
      nextDateBuf.append("分");
      nextDateBuf.append(second);
      nextDateBuf.append("秒");

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
