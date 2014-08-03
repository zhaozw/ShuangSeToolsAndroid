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

public class ExperienceShareActivity extends FragmentActivity {
  private static final String TAG = "ExperienceShareActivity";
  private static final int ViewSwitchCompletedMsg = 10;

  private ViewPager exprShareViewPager;
  private ExprSharePagerAdapter exprSharePagerAdapter;
  private List<Fragment> mListViews;
  private Fragment expShareLayout1 = null;
  private Fragment expShareLayout2 = null;
  private Fragment expShareLayout3 = null;
  private Button expShareBtn1;
  private Button expShareBtn2;
  private Button expShareBtn3;

  private Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<ExperienceShareActivity> mActivity;

    MHandler(ExperienceShareActivity mAct) {
      this.mActivity = new WeakReference<ExperienceShareActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      ExperienceShareActivity theActivity = mActivity.get();
      
      switch (msg.what) {
      //左右切换返回的消息
      case ViewSwitchCompletedMsg:
        switch (msg.arg1) {
        case 0:
          theActivity.expShareBtn1.setBackgroundResource(R.drawable.rectbg);
          theActivity.expShareBtn2.setBackgroundResource(R.color.transparent);
          theActivity.expShareBtn3.setBackgroundResource(R.color.transparent);
          break;
        case 1:
          theActivity.expShareBtn2.setBackgroundResource(R.drawable.rectbg);
          theActivity.expShareBtn1.setBackgroundResource(R.color.transparent);
          theActivity.expShareBtn3.setBackgroundResource(R.color.transparent);
          break;
        case 2:
          theActivity.expShareBtn3.setBackgroundResource(R.drawable.rectbg);
          theActivity.expShareBtn2.setBackgroundResource(R.color.transparent);
          theActivity.expShareBtn1.setBackgroundResource(R.color.transparent);
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

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.exprshare);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        R.layout.custom_title);

    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_expr_share);
    
    mListViews = new ArrayList<Fragment>();
    expShareLayout1 = new SmsFragment();
    expShareLayout2 = new NetworkCommentFragment();
    expShareLayout3 = new ShareExpFragment();
    mListViews.add(expShareLayout1);
    mListViews.add(expShareLayout2);
    mListViews.add(expShareLayout3);
    
    exprSharePagerAdapter = new ExprSharePagerAdapter(getSupportFragmentManager(), mListViews);
    exprSharePagerAdapter.setFragments(mListViews);
    
    exprShareViewPager = (ViewPager) findViewById(R.id.expshareviewpager);
    exprShareViewPager.setAdapter(exprSharePagerAdapter);
    exprShareViewPager.setCurrentItem(0);
    
    expShareBtn1 = (Button) findViewById(R.id.contactbtn1);
    expShareBtn2 = (Button) findViewById(R.id.contactbtn2);
    expShareBtn3 = (Button) findViewById(R.id.contactbtn3);
    
    expShareBtn1.setBackgroundResource(R.drawable.rectbg);
    expShareBtn2.setBackgroundResource(R.color.transparent);
    expShareBtn3.setBackgroundResource(R.color.transparent);
    
    expShareBtn1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          exprShareViewPager.setCurrentItem(0);
        }
    });
    expShareBtn2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          exprShareViewPager.setCurrentItem(1);
        }
    });
    expShareBtn3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          exprShareViewPager.setCurrentItem(2);
        }
    });

    exprShareViewPager.setOnPageChangeListener(new OnPageChangeListener() {
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
          // EditText smsText = (EditText)v.findViewById(R.id.smsText);
          // 清空内容?
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

  }
  
  private class ExprSharePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentsList;
    private FragmentManager fm;
    
    public ExprSharePagerAdapter(FragmentManager fm) {
      super(fm);
      this.fm = fm;
    }
    
    public ExprSharePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
  
  protected void onPause() {
      super.onPause();
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



