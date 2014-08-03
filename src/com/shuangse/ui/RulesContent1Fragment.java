package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.shuangse.base.ShuangSeToolsSetApplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RulesContent1Fragment extends ListFragment {
    private final static String TAG = "RulesContent1Fragment";
    protected boolean isInit; // 是否可以开始加载数据
    // 下次开奖前截至销售时间（按每2，4，7晚8点算）
    private Date nextOpenEndTime;
    private ScheduledThreadPoolExecutor execUpdateDisTime = null;
    private ShuangSeToolsSetApplication appContext;

    private ListView expListView;
    private SimpleAdapter adapter;
    protected ArrayList<Map<String, Object>> mlistItems;
    
    
    private TextView time_info;
    
    private final static int UpdateTimeDisplayMessage = 10;
    private Handler msgHandler = new MHandler(this);
    static class MHandler extends Handler {
      WeakReference<RulesContent1Fragment> mActivity;

      MHandler(RulesContent1Fragment mAct) {
        mActivity = new WeakReference<RulesContent1Fragment>(mAct);
      }

      @Override
      public void handleMessage(Message msg) {
        RulesContent1Fragment theActivity = mActivity.get();

        switch (msg.what) {
        case UpdateTimeDisplayMessage:
          switch (msg.arg1) {
          case 1:
              Bundle bundle = msg.getData();
              if(bundle != null){
                  String nextDateStr = bundle.getString("NextDateStr", "Error");
                  theActivity.updateTimeInfo(nextDateStr);
              }
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
    
    private void updateTimeInfo(String nextTimeInfoStr) {
        time_info.setText(nextTimeInfoStr);
    }
    
    private ArrayList<Map<String, Object>> getData() {
        mlistItems = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ItemID", "softwareIntro");
        map.put("ItemTitleText", appContext.getResources().getString(R.string.software_intro));
        mlistItems.add(map);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("ItemID", "shuangseIntro");
        map2.put("ItemTitleText", appContext.getResources().getString(R.string.shuangse_intro));
        mlistItems.add(map2);
        
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("ItemID", "xuanzhuanIntro");
        map3.put("ItemTitleText",appContext.getResources().getString(R.string.xuanzhuan_intro));
        mlistItems.add(map3);

        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("ItemID", "dantuoIntro");
        map4.put("ItemTitleText",appContext.getResources().getString(R.string.dantuo_intro));
        mlistItems.add(map4);
        
        Map<String, Object> map5 = new HashMap<String, Object>();
        map5.put("ItemID", "condIntro");
        map5.put("ItemTitleText",appContext.getResources().getString(R.string.cond_intro));
        mlistItems.add(map5);
        
        return mlistItems;
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        isInit = true;
        // 获取全局数据
        appContext = (ShuangSeToolsSetApplication)getActivity().getApplication();
        return inflater.inflate(R.layout.rules_content1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
      // 设置第1个View中的内容
      TextView time_info_title = (TextView) view.findViewById(R.id.time_info_title);
      time_info = (TextView) view.findViewById(R.id.time_info);

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
        time_info_title.setText("距" + appContext.getLoalLatestItemIDFromCache()+ "期投注截止");
        
        // Listview
        expListView = getListView();
        mlistItems = getData();
        adapter = new SimpleAdapter(appContext, mlistItems,
                R.layout.listviewitem, new String[] {
                        "ItemID","ItemTitleText"
                }, new int[] {
                        R.id.ItemID, R.id.ItemTitleText
                });
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        expListView.setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> view, View arg1, int arg2,
                  long arg3) {
              TextView ItemIDTextView = (TextView)arg1.findViewById(R.id.ItemID);
              String itemID = ItemIDTextView.getText().toString();
              Log.d(TAG, itemID);
              Intent intent = new Intent(RulesContent1Fragment.this.getActivity(), CommonDispActivity.class);
              intent.putExtra("ItemId", itemID);
              startActivity(intent);
          }
            
        });

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

    public void onResume() {
        // 初始化定时器
        execUpdateDisTime = new ScheduledThreadPoolExecutor(1);
        execUpdateDisTime.scheduleAtFixedRate(new UpdateDisplayTimeTask(), 0, 1000,
            TimeUnit.MILLISECONDS);
        super.onResume();
      }

    public void onPause() {
        if (execUpdateDisTime != null) {
          execUpdateDisTime.shutdownNow();
          execUpdateDisTime = null;
        }
        super.onPause();
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

          Message msg1 = msgHandler.obtainMessage(UpdateTimeDisplayMessage);
          msg1.arg1 = 1;
          Bundle bundle = new Bundle();
          bundle.putString("NextDateStr", nextDateBuf.toString());
          msg1.setData(bundle);
          msg1.sendToTarget();
        }
      };

}
