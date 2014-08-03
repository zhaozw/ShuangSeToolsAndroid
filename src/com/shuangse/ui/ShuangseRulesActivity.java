package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ShuangseRulesActivity extends FragmentActivity {
  private static final String TAG = "ShuangseRulesActivity";
  private static final int ViewSwitchCompletedMsg = 2;
  private ViewPager localViewPager;
  private PrivatePagerAdapter localAdapter;

  private List<Fragment> mListViews;
  
  private Fragment contentLayout1 = null;
  private Fragment contentLayout2 = null;
  private Fragment contentLayout3 = null;
  private Fragment contentLayout4 = null;
  
  private Button btn1;
  private Button btn2;
  private Button btn3;
  private Button btn4;
  
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
          theActivity.btn4.setBackgroundResource(R.color.transparent);
          break;
        case 1:
          theActivity.btn2.setBackgroundResource(R.drawable.rectbg);
          theActivity.btn1.setBackgroundResource(R.color.transparent);
          theActivity.btn3.setBackgroundResource(R.color.transparent);
          theActivity.btn4.setBackgroundResource(R.color.transparent);
          break;
        case 2:
          theActivity.btn3.setBackgroundResource(R.drawable.rectbg);
          theActivity.btn2.setBackgroundResource(R.color.transparent);
          theActivity.btn1.setBackgroundResource(R.color.transparent);
          theActivity.btn4.setBackgroundResource(R.color.transparent);
          break;
        case 3:
          theActivity.btn4.setBackgroundResource(R.drawable.rectbg);
          theActivity.btn3.setBackgroundResource(R.color.transparent);
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
    
    mListViews = new ArrayList<Fragment>();
    
    contentLayout1 = new RulesContent1Fragment();//mInflater.inflate(R.layout.rules_content1, null);
    contentLayout2 = new RulesContent2Fragment();//mInflater.inflate(R.layout.rules_content2, null);
    contentLayout3 = new RulesContent3Fragment();//mInflater.inflate(R.layout.rules_content3, null);
    contentLayout4 = new RulesContent4Fragment();//mInflater.inflate(R.layout.rules_content4, null);

    mListViews.add(contentLayout1);
    mListViews.add(contentLayout2);
    mListViews.add(contentLayout3);
    mListViews.add(contentLayout4);

    localAdapter = new PrivatePagerAdapter(getSupportFragmentManager(), mListViews);
    localAdapter.setFragments(mListViews);
    
    localViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
    localViewPager.setAdapter(localAdapter);

    btn1 = (Button) findViewById(R.id.btn1);
    btn2 = (Button) findViewById(R.id.btn2);
    btn3 = (Button) findViewById(R.id.btn3);
    btn4 = (Button) findViewById(R.id.btn4);
    
    btn1.setBackgroundResource(R.drawable.rectbg);
    btn2.setBackgroundResource(R.color.transparent);
    btn3.setBackgroundResource(R.color.transparent);
    btn4.setBackgroundResource(R.color.transparent);
    
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
    btn4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          localViewPager.setCurrentItem(3);
        }
      });

    localViewPager.setCurrentItem(0);
    
    
    localViewPager.setOnPageChangeListener(new OnPageChangeListener() {

      @Override
      public void onPageSelected(int viewIndex) {
        // activity从n到n+1滑动，n+1被加载后掉用此方法
        //View v = mListViews.get(viewIndex);
        Log.i(TAG, "viewIndex:" + viewIndex);
        switch (viewIndex) {
        case 0:
          Message msg0 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg0.arg1 = 0;
          msg0.sendToTarget();
          break;
        case 1:
          Message msg1 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg1.arg1 = 1;
          msg1.sendToTarget();
          break;
        case 2:
          Message msg2 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg2.arg1 = 2;
          msg2.sendToTarget();
          break;
          
        case 3:
          Message msg3 = msgHandler.obtainMessage(ViewSwitchCompletedMsg);
          msg3.arg1 = 3;
          msg3.sendToTarget();
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


  private class PrivatePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentsList;
    private FragmentManager fm;

    public PrivatePagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }
    
    public PrivatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
        this.fm = fm;
    }
    
    @Override
    public int getCount() {
      return fragmentsList.size();
    }
    
    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }
    @Override  
    public int getItemPosition(Object object) {  
        //加此方法可以使viewpager可以进行刷新  
        return PagerAdapter.POSITION_NONE;
    }
    
    public void setFragments(List<Fragment> fragments) {
        if(this.fragmentsList != null){  
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f:this.fragmentsList){  
                ft.remove(f);
            }
            ft.commit();  
            ft=null;  
            fm.executePendingTransactions();  
        }  
        this.fragmentsList = fragments;
        notifyDataSetChanged();  
    }
    
    
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
    }
  };


  protected void onDestroy() {
    super.onDestroy();
  }

}
