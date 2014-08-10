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

  public class ShuangseHitSearchActivity extends FragmentActivity {
    private final static String TAG = "ShuangseHitSearchActivity";
    
    private final static int ViewSwitchCompletedMsg = 2;    
    private ViewPager localViewPager;
    private HitSearchPagerAdapter localAdapter;

    private List<Fragment> mListViews;
    private Fragment contentLayout0 = null;
    private Fragment contentLayout1 = null;
    private Fragment contentLayout2 = null;
    private Fragment contentLayout3 = null;
    
    private Button btn0;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    
    private Handler msgHandler = new MHandler(this);
    
    static class MHandler extends Handler {
      private WeakReference<ShuangseHitSearchActivity> mActivity;
      
      public MHandler(ShuangseHitSearchActivity mAct) {
        this.mActivity = new WeakReference<ShuangseHitSearchActivity>(mAct);
      }
        @Override
        public void handleMessage(Message msg) {
          ShuangseHitSearchActivity   theActivity = mActivity.get();
          
            switch (msg.what) { 
            //左右切换返回的消息
            case ViewSwitchCompletedMsg:
                switch(msg.arg1) {
                case 0:
                  theActivity.btn0.setBackgroundResource(R.drawable.rectbg);
                  theActivity.btn1.setBackgroundResource(R.color.transparent);
                  theActivity.btn2.setBackgroundResource(R.color.transparent);
                  theActivity.btn3.setBackgroundResource(R.color.transparent);
                    break;
                case 1:
                  theActivity.btn0.setBackgroundResource(R.color.transparent);
                  theActivity.btn1.setBackgroundResource(R.drawable.rectbg);
                  theActivity.btn2.setBackgroundResource(R.color.transparent);
                  theActivity.btn3.setBackgroundResource(R.color.transparent);
                    break;
                case 2:
                  theActivity.btn0.setBackgroundResource(R.color.transparent);
                  theActivity.btn1.setBackgroundResource(R.color.transparent);
                  theActivity.btn2.setBackgroundResource(R.drawable.rectbg);
                  theActivity.btn3.setBackgroundResource(R.color.transparent);
                    break;
                case 3:
                    theActivity.btn0.setBackgroundResource(R.color.transparent);
                    theActivity.btn1.setBackgroundResource(R.color.transparent);
                    theActivity.btn2.setBackgroundResource(R.color.transparent);
                    theActivity.btn3.setBackgroundResource(R.drawable.rectbg);
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
        setContentView(R.layout.hitsearch);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title);

        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_hit_search);
        
        mListViews = new ArrayList<Fragment>();
        contentLayout0 = new HisDataListFragment();
        contentLayout1 = new SingleSearchFragment();
        contentLayout2 = new FuShiSearchFragment();
        contentLayout3 = new DanTuoSearchFragment();
        mListViews.add(contentLayout0);
        mListViews.add(contentLayout1);
        mListViews.add(contentLayout2);
        mListViews.add(contentLayout3);

        localAdapter = new HitSearchPagerAdapter(getSupportFragmentManager(), mListViews);
        localAdapter.setFragments(mListViews);
        
        localViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
        localViewPager.setAdapter(localAdapter);
        localViewPager.setCurrentItem(0);
        
        btn0 = (Button) findViewById(R.id.btn0);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        
        btn0.setBackgroundResource(R.drawable.rectbg);
        btn1.setBackgroundResource(R.color.transparent);
        btn2.setBackgroundResource(R.color.transparent);
        btn3.setBackgroundResource(R.color.transparent);
        
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localViewPager.setCurrentItem(0);
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localViewPager.setCurrentItem(1);                
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localViewPager.setCurrentItem(2);                
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localViewPager.setCurrentItem(3);                
            }
        });
        
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
                 * Indicates that the pager is in an idle, settled state. The
                 * current page is fully in view and no animation is in
                 * progress.
                 */
                // public static final int SCROLL_STATE_IDLE = 0;
                /**
                 * Indicates that the pager is currently being dragged by the
                 * user.
                 */
                // public static final int SCROLL_STATE_DRAGGING = 1;
                /**
                 * Indicates that the pager is in the process of settling to a
                 * final position.
                 */
                // public static final int SCROLL_STATE_SETTLING = 2;

            }
        });
    }
    
    private class HitSearchPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentsList;
        private FragmentManager fm;
        
        public HitSearchPagerAdapter(FragmentManager fm) {
          super(fm);
          this.fm = fm;
        }
        
        public HitSearchPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
}
