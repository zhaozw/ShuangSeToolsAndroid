package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.util.MagicTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ShuangseHitSearchActivity extends Activity implements SelectRedDialogCallback,
                                                                              SelectBlueDialogCallback {
    private final static String TAG = "ShuangseHitSearchActivity";
    
    private ShuangSeToolsSetApplication appContext;
    
    private final static int ViewSwitchCompletedMsg = 2;    
    private ViewPager localViewPager;
    private PrivatePagerAdapter localAdapter;    
    private LayoutInflater mInflater;
    private List<View> mListViews;
    private View contentLayout1 = null;
    private View contentLayout2 = null;
    private View contentLayout3 = null;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    
    private List<String> itemIDs;
    private ArrayAdapter<String> allitemIDAdaptor;  
    private ArrayAdapter<CharSequence> blueValAdapter;
    private List<ShuangseCodeItem> allHisData;
    
    private int currentInWhichPagerFlag = 1; //1, 2, 3
    //单式
    private int singleSelItemId;
    private ShuangseCodeItem currentSelItem;
    private int singleSelBlue;
    private ShuangseCodeItem inputItem;
    private Button inputRedBtn;
    private TextView resultCodeView;
    private TextView result_title;
    private TextView red_title;
    
    //复式
    private int fushiSelItemId;
    private ShuangseCodeItem currentSelItemFuShi;
    private TextView resultCodeViewFuShi;
    private Button inputRedBtnFuShi;
    private Button inputBlueBtnFuShi;
    private TextView result_titleFuShi;
    private TextView red_title_fushi;
    private TextView blue_title_fushi;
    private int[] fuShiSelRedNum = null;
    private int[] fuShiSelblueNum = null;
    
    //胆拖
    private int dantuoSelItemId;
    private ShuangseCodeItem currentSelItemdantuo;
    private TextView resultCodeViewdantuo;
    private Button inputRedBtndantuo;
    private Button inputRed2Btndantuo;
    private Button inputBlueBtndantuo;
    private TextView result_titledantuo;
    //显示选择的号码
    private TextView red_title_dantuo;
    private TextView red2_title_dantuo;
    private TextView blue_title_dantuo;
    private int[] danTuoTotalRedNumbers = null;
    private int[] redDanNum = null;
    private int[] redTuoNum = null;
    
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
            case ViewSwitchCompletedMsg:
                switch(msg.arg1) {
                case 0:
                  theActivity.btn1.setBackgroundResource(R.drawable.rectbg);
                  theActivity.btn2.setBackgroundResource(R.color.transparent);
                  theActivity.btn3.setBackgroundResource(R.color.transparent);
                  theActivity.currentInWhichPagerFlag = 1;
                    break;
                case 1:
                  theActivity.btn2.setBackgroundResource(R.drawable.rectbg);
                  theActivity.btn1.setBackgroundResource(R.color.transparent);
                  theActivity.btn3.setBackgroundResource(R.color.transparent);
                  theActivity.currentInWhichPagerFlag = 2;
                    break;
                case 2:
                  theActivity.btn3.setBackgroundResource(R.drawable.rectbg);
                  theActivity.btn2.setBackgroundResource(R.color.transparent);
                  theActivity.btn1.setBackgroundResource(R.color.transparent);
                  theActivity.currentInWhichPagerFlag = 3;
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

        appContext = (ShuangSeToolsSetApplication)getApplication();    
        // 更新标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.hitsearch);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title);

        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_hit_search);
                
        Button returnBtn = (Button)findViewById(R.id.returnbtn);
        returnBtn.setVisibility(View.VISIBLE);
        Button helpBtn = (Button)findViewById(R.id.helpbtn);
        helpBtn.setVisibility(View.VISIBLE);
        helpBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String htmlMsg = "本页为您提供查询您购买彩票的中奖信息，请按提示输入对应的购买号码，即可查询您中奖的奖金情况。";
            MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, ShuangseHitSearchActivity.this).show();
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
        contentLayout1 = mInflater.inflate(R.layout.singlesearch, null);
        contentLayout2 = mInflater.inflate(R.layout.fushisearch, null);
        contentLayout3 = mInflater.inflate(R.layout.dantuosearch, null);

        mListViews.add(contentLayout1);
        mListViews.add(contentLayout2);
        mListViews.add(contentLayout3);
        
        //初始第一页
        localViewPager.setCurrentItem(0);
        currentInWhichPagerFlag = 1;
        
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
        
        itemIDs = new ArrayList<String>();        
        allHisData = appContext.getAllHisData();
        if(allHisData != null && allHisData.size() > 1) {
            int maxSize = (allHisData.size() - 1);
            for(int i = maxSize; i >=0; i--) {
                itemIDs.add(Integer.toString(allHisData.get(i).id));
            }
            allitemIDAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemIDs);  
            allitemIDAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        }
        
        //篮球选项数据初始化
        blueValAdapter = ArrayAdapter.createFromResource(this,
                R.array.blue_value_list, android.R.layout.simple_spinner_item);
        blueValAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 初始化当前显示的第一个view
        initlizeFirstView();
        
        //设置第二个View中的内容
        initlizeSecondView();
        
        //设置第三个View中的内容
        initlizeThirdView();
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
    
    private void initlizeFirstView() {
        //单注红号码
        inputRedBtn = (Button)contentLayout1.findViewById(R.id.input_single_red);
        inputRedBtn.setOnClickListener(new View.OnClickListener() {
          
          @Override
          public void onClick(View v) {
            //初始化一个自定义的Dialog
            Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
            dialog.show();
          }
        });
        
        red_title = (TextView) contentLayout1.findViewById(R.id.red_title);
        //开奖号码 域
        resultCodeView = (TextView) contentLayout1.findViewById(R.id.result_code);
        //选择哪一期
        Spinner spinner_itemid_single = (Spinner) contentLayout1.findViewById(R.id.spinner_itemid);            
        spinner_itemid_single.setAdapter(allitemIDAdaptor);
        spinner_itemid_single.setOnItemSelectedListener( new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                Log.i(TAG, "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString());
                singleSelItemId = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                currentSelItem = appContext.getCodeItemByID(singleSelItemId);
                resultCodeView.setText(currentSelItem.toCNString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
            }
            
        });
        //蓝球号码下拉框
        Spinner spinner_blue_val_single = (Spinner) contentLayout1.findViewById(R.id.spinner_blue);
        spinner_blue_val_single.setAdapter(blueValAdapter);
        spinner_blue_val_single.setOnItemSelectedListener( new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                Log.i(TAG, "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString());
                singleSelBlue = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                if(inputItem != null) {
                  inputItem.blue = singleSelBlue;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
            }
            
        });
        
        //中奖结果 - 默认初始值
        result_title = (TextView) contentLayout1.findViewById(R.id.result_title);
        
        //查询按钮
        Button single_search_btn = (Button)contentLayout1.findViewById(R.id.search_single_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(inputItem == null) {
                  InfoMessageBox("错误","请点击选择你购买的单式红球号码！");
                  return;
                }
                Log.i(TAG, "itemID:" + singleSelItemId +
                        " blue:" + singleSelBlue);
                result_title.setText("");

                int hitResult = MagicTool.getHitResult(currentSelItem, inputItem);
                switch (hitResult) {
                    case 1:
                        result_title.setText("中6红1蓝；恭喜你中一等奖；奖金500万元或更多，请参见当期开奖公告.");
                        break;
                    case 2:
                        result_title.setText("中6红0蓝；恭喜你中二等奖；奖金数万元或更多，请参见当期开奖公告.");
                        break;
                    case 3:
                        result_title.setText("中5红1蓝；恭喜你中三等奖；奖金3000元.");
                        break;
                    case 4:
                        result_title.setText("中4红1蓝；恭喜你中四等奖；奖金200元.");
                        break;
                    case 5:
                        result_title.setText("中5红0蓝；恭喜你中四等奖；奖金200元.");
                        break;
                    case 6:
                        result_title.setText("中3红1蓝；恭喜你中五等奖；奖金10元.");
                        break;
                    case 7:
                        result_title.setText("中4红0蓝；恭喜你中五等奖；奖金10元.");
                        break;
                    case 8:
                        result_title.setText("中2红1蓝；恭喜你中六等奖；奖金5元.");
                        break;
                    case 9:
                      result_title.setText("中1红1蓝；恭喜你中六等奖；奖金5元.");
                      break;    
                    case 10:
                      result_title.setText("中0红1蓝；恭喜你中六等奖；奖金5元.");
                      break; 
                    case 11:
                      result_title.setText("中3红0蓝；未中奖；奖金0元.");
                      break;  
                    case 12:
                      result_title.setText("中2红0蓝；未中奖；奖金0元.");
                      break;
                    case 13:
                      result_title.setText("中1红0蓝；未中奖；奖金0元.");
                      break;
                    case 14:
                      result_title.setText("中0红0蓝；未中奖；奖金0元.");
                      break;
                    case -1:
                    default:
                        result_title.setText("未中奖；奖金0元.");
                        break;
                }
            }
        });
    }
    
    //设置第二个View中的内容
    private void initlizeSecondView() {
        //开奖号码 域
        resultCodeViewFuShi = (TextView) contentLayout2.findViewById(R.id.result_code_fushi);
        //期号选择
        Spinner spinner_itemid_fushi = (Spinner) contentLayout2.findViewById(R.id.spinner_itemid_fushi);            
        spinner_itemid_fushi.setAdapter(allitemIDAdaptor);
        spinner_itemid_fushi.setOnItemSelectedListener( new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                Log.i(TAG, "OnItemSelectedListener_fushi : " + parent.getItemAtPosition(pos).toString());
                fushiSelItemId = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                currentSelItemFuShi = appContext.getCodeItemByID(fushiSelItemId);                
                resultCodeViewFuShi.setText(currentSelItemFuShi.toCNString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Log.i(TAG, "spinner_itemid_fushi:: onNothingSelected()");
            }            
        });
        
        red_title_fushi = (TextView)contentLayout2.findViewById(R.id.red_title_fushi);
        blue_title_fushi = (TextView)contentLayout2.findViewById(R.id.blue_title_fushi);
        
        //红球
        inputRedBtnFuShi = (Button)contentLayout2.findViewById(R.id.input_fushi_red);
        inputRedBtnFuShi.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //初始化一个自定义的Dialog
            Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
            dialog.show();
          }
        });
        //篮球
        inputBlueBtnFuShi = (Button)contentLayout2.findViewById(R.id.input_fushi_blue);
        inputBlueBtnFuShi.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
          //初始化一个自定义的Dialog
            Dialog dialog = new SelectBlueDialog(ShuangseHitSearchActivity.this, R.style.SelectBlueDialog, ShuangseHitSearchActivity.this);
            dialog.show();
          }
        });
        
        //中奖结果
        result_titleFuShi = (TextView) contentLayout2.findViewById(R.id.result_title_fushi);
        
        //查询按钮
        Button single_search_btn = (Button)contentLayout2.findViewById(R.id.search_fushi_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fuShiSelRedNum == null) {
                  InfoMessageBox("错误","请选择您购买的复式红球！");
                  return;
                }
                
                if(fuShiSelblueNum == null) {
                  InfoMessageBox("错误","请选择您购买的蓝球！");
                  return;
                }
                               
                if(fuShiSelRedNum.length < 6) {
                    InfoMessageBox("错误","选择的红球个数不对，至少6个红球！");
                    return;
                }

                if(fuShiSelRedNum.length > 20) {
                  InfoMessageBox("错误","双色球红球复式最多不能超过20个红球，请重新选择！");
                  return;
                }
                
                //多个篮球
                if(fuShiSelblueNum.length < 1) {
                  InfoMessageBox("错误","选择的蓝球个数不对，至少1个蓝球！");
                  return;
                }
              
                if(fuShiSelblueNum.length > 16) {
                  InfoMessageBox("错误","选择的蓝球复式最多不能超过16个蓝球，请重新选择！");
                  return;
                }
                result_titleFuShi.setText("");
              
              //fuShiSelRedNum[] fuShiSelblueNum[]
              //最大20个红 + 16蓝 = 38760 ×１６＝ 620160
              SparseIntArray resultMap = new SparseIntArray ();
              long totalItemCnt = MagicTool.combine(fuShiSelRedNum, fuShiSelRedNum.length, 6, currentSelItemFuShi, fuShiSelblueNum,fuShiSelblueNum.length,resultMap);
              
              StringBuffer dispTextBuf = new StringBuffer();
              dispTextBuf.append("该复式共包含").append(totalItemCnt).append("注号码,");
              for(int metIndex=0; metIndex < resultMap.size(); metIndex++) {
                int hitIndex = resultMap.keyAt(metIndex);
                int hitCount = resultMap.valueAt(metIndex);
                
                if(hitCount > 0) {
                  dispTextBuf.append(getDispText(hitIndex, hitCount)).append("\n");
                }
              }

              result_titleFuShi.setText(dispTextBuf.toString());
            }
            
        });
        
    }
    
    //设置第三个View中的内容
    private void initlizeThirdView() {
      //开奖号码 域
      resultCodeViewdantuo = (TextView) contentLayout3.findViewById(R.id.result_code_dantuo);
      //期号选择
      Spinner spinner_itemid_dantuo = (Spinner) contentLayout3.findViewById(R.id.spinner_itemid_dantuo);            
      spinner_itemid_dantuo.setAdapter(allitemIDAdaptor);
      spinner_itemid_dantuo.setOnItemSelectedListener( new OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view,
                  int pos, long id) {
              Log.i(TAG, "OnItemSelectedListener_dantuo : " + parent.getItemAtPosition(pos).toString());
              dantuoSelItemId = Integer.parseInt(parent.getItemAtPosition(pos).toString());
              currentSelItemdantuo = appContext.getCodeItemByID(dantuoSelItemId);                
              resultCodeViewdantuo.setText(currentSelItemdantuo.toCNString());
          }

          @Override
          public void onNothingSelected(AdapterView<?> arg0) {
              Log.i(TAG, "spinner_itemid_dantuo:: onNothingSelected()");
          }            
      });
      
      //红球 胆 和 托码
      inputRedBtndantuo = (Button)contentLayout3.findViewById(R.id.input_dantuo_red);
      inputRedBtndantuo.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        //初始化一个自定义的Dialog
          Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
          currentInWhichPagerFlag = 3;
          dialog.show();
        }
      });
      
      inputRed2Btndantuo = (Button)contentLayout3.findViewById(R.id.input_dantuo_red2);
      inputRed2Btndantuo.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        //初始化一个自定义的Dialog
          Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
          currentInWhichPagerFlag = 4;
          dialog.show();
        }
      });
      //篮球
      inputBlueBtndantuo = (Button)contentLayout3.findViewById(R.id.input_dantuo_blue);
      inputBlueBtndantuo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //初始化一个自定义的Dialog
          Dialog dialog = new SelectBlueDialog(ShuangseHitSearchActivity.this, R.style.SelectBlueDialog, ShuangseHitSearchActivity.this);
          dialog.show();
        }
      });
      
      //中奖结果
      result_titledantuo = (TextView) contentLayout3.findViewById(R.id.result_title_dantuo);
      
      red_title_dantuo = (TextView) contentLayout3.findViewById(R.id.red_title_dantuo);
      red2_title_dantuo = (TextView) contentLayout3.findViewById(R.id.red2_title_dantuo);
      blue_title_dantuo = (TextView) contentLayout3.findViewById(R.id.blue_title_dantuo);
      
      //查询按钮
      Button single_search_btn = (Button)contentLayout3.findViewById(R.id.search_dantuo_btn);
      single_search_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(redDanNum == null) {
                InfoMessageBox("错误","请选择红球胆码，胆码允许1-5个！");
                return;
              }
              if(redTuoNum == null) {
                InfoMessageBox("错误","请选择红球拖码，拖码允许1-20个！");
                return;
              }
              
              if(redDanNum.length > 5 || redDanNum.length < 1) {
                  InfoMessageBox("错误","红球胆码只允许1-5个！");
                  return;
              }
              
              if(redTuoNum.length > 20 || redTuoNum.length < 1) {
                  InfoMessageBox("错误","红球拖码只允许1-20个！");
                  return;
              }
              
              //胆拖合并
              danTuoTotalRedNumbers = MagicTool.join(redDanNum, redTuoNum);
              if(danTuoTotalRedNumbers.length < 6) {
                InfoMessageBox("错误","输入的胆码和拖码组合一起必须最少6个红球，请重新选择！");
                return;
              }

              //reuse fuShiSelblueNum
              int[] blueNum = fuShiSelblueNum;
              if(blueNum.length < 1 || blueNum.length > 16) {
                  InfoMessageBox("错误","选择的蓝球个数不对！");
                  return;
              }
            
              result_titledantuo.setText("");
              
            //redNum[] red2Num[] blueNum[]
            SparseIntArray resultMap = new SparseIntArray();
            long totalItemCnt = MagicTool.combine(danTuoTotalRedNumbers, danTuoTotalRedNumbers.length, redDanNum, redDanNum.length,
                6, currentSelItemdantuo, blueNum,blueNum.length,resultMap);
            
            StringBuffer dispTextBuf = new StringBuffer();
            dispTextBuf.append("该胆拖式共包含").append(totalItemCnt).append("注号码,");
            for(int metIndex=0; metIndex < resultMap.size(); metIndex++) {
              int hitIndex = resultMap.keyAt(metIndex);
              int hitCount = resultMap.valueAt(metIndex);
              
              if(hitCount > 0) {
                dispTextBuf.append(getDispText(hitIndex, hitCount)).append("\n");
              }
            }

            result_titledantuo.setText(dispTextBuf.toString());
          }
          
      });
    }
    
    private void InfoMessageBox(String title, String msg) {
        AlertDialog notifyDialog = new AlertDialog.Builder(
                    ShuangseHitSearchActivity.this)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,    int which) {
                                }
                            }).create();
        notifyDialog.show();
    }
    
    private String getDispText(int hitIndex, int hitCount) {
      switch (hitIndex) {
        case 1:
          return "中"+hitCount+"注6红1蓝(一等奖), 奖金每注500万元或更多;";
        case 2:
          return "中"+hitCount+"注6红0蓝(二等奖), 奖金每注数万元或更多;";
        case 3:
          return "中"+hitCount+"注5红1蓝(三等奖), 奖金每注3000元;";
        case 4:
          return "中"+hitCount+"注4红1蓝(四等奖), 奖金每注200元;";
        case 5:
          return "中"+hitCount+"注5红0蓝(四等奖), 奖金每注200元;";
        case 6:
          return "中"+hitCount+"注3红1蓝(五等奖), 奖金每注10元;";
        case 7:
          return "中"+hitCount+"注4红0蓝(五等奖), 奖金每注10元;";
        case 8:
          return "中"+hitCount+"注2红1蓝(六等奖), 奖金每注5元;";
        case 9:
          return "中"+hitCount+"注1红1蓝(六等奖), 奖金每注5元;";
        case 10:
          return "中"+hitCount+"注0红1蓝(六等奖), 奖金每注5元;";
        case 11:
          return "中"+hitCount+"注3红0蓝(无奖);";
        case 12:
          return "中"+hitCount+"注2红0蓝(无奖);";
        case 13:
          return "中"+hitCount+"注1红0蓝(无奖);";
        case 14:
          return "中"+hitCount+"注0红0蓝(无奖);";
        case -1:
        default:
          return "未中奖；奖金0元.";
      }
    }

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {
      
      if(this.currentInWhichPagerFlag == 1) {
          if(redList.size() !=6 ) {
            InfoMessageBox("错误","选择的红球个数不对，单式是6个红球！");
            return;
          }
        
         int[] redNum = new int[6];
         for(int i=0;i < redList.size(); i++) {
              redNum[i] = redList.get(i);
              if(redNum[i] <1 || redNum[i] > 33) {
                InfoMessageBox("错误","选择的红球不对，红球范围在1-33！");
                return;
              }
          }
          
          inputItem = new ShuangseCodeItem(singleSelItemId, redNum, singleSelBlue);
          StringBuffer sb = new StringBuffer();
          sb.append("红：").append(inputItem.toRedString());
          red_title.setText(sb.toString());      
          result_title.setText("点击下方<查询按钮>查结果");
      } else if(this.currentInWhichPagerFlag == 2) {
          if(redList.size() < 6 ) {
            InfoMessageBox("错误","选择的红球个数不对，至少6个红球！");
            return;
          }
          fuShiSelRedNum = new int[redList.size()]; 
          StringBuffer sb = new StringBuffer();
          sb.append("红：");
          
          for (int i = 0; i < redList.size(); i++) {
            fuShiSelRedNum[i] = redList.get(i);
            if(fuShiSelRedNum[i] < 10) {
              sb.append("0");
            }
            sb.append(fuShiSelRedNum[i]).append(" ");
           }

          red_title_fushi.setText(sb.toString());
          result_titleFuShi.setText("点击下方<查询按钮>查结果");
      } else if(this.currentInWhichPagerFlag == 3) {//选胆
        if(redList.size() < 1 || redList.size() > 5) {
          InfoMessageBox("错误","选择的红球胆码个数不对，选择1-5个红球胆码！");
          return;
        }
        redDanNum = new int[redList.size()]; 
        StringBuffer sb = new StringBuffer();
        sb.append("红胆：");
        
        for (int i = 0; i < redList.size(); i++) {
          redDanNum[i] = redList.get(i);
          if(redDanNum[i] < 10) {
            sb.append("0");
          }
          sb.append(redDanNum[i]).append(" ");
         }

        red_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("点击下方<查询按钮>查结果");
      }else if(this.currentInWhichPagerFlag == 4) {//选红拖
        if(redList.size() < 1 || redList.size() > 20) {
          InfoMessageBox("错误","选择的红球拖码个数不对，选择1-20个红球拖码！");
          return;
        }
        redTuoNum = new int[redList.size()]; 
        StringBuffer sb = new StringBuffer();
        sb.append("红拖：");
        
        for (int i = 0; i < redList.size(); i++) {
          redTuoNum[i] = redList.get(i);
          if(redTuoNum[i] < 10) {
            sb.append("0");
          }
          sb.append(redTuoNum[i]).append(" ");
         }

        red2_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("点击下方<查询按钮>查结果");
      }
      
    }

    @Override
    public void sendSelectedBlueData(ArrayList<Integer> blueList) {
      
      if(blueList.size() < 1 ) {
        InfoMessageBox("错误","选择的蓝球个数不对，至少1个篮球！");
        return;
      }
    
      fuShiSelblueNum = new int[blueList.size()];
      StringBuffer sb = new StringBuffer();
      sb.append("蓝：");
      for(int i=0;i < blueList.size(); i++) {
         fuShiSelblueNum[i] = blueList.get(i);
         if(fuShiSelblueNum[i] < 10) {
           sb.append("0");
         }
         sb.append(fuShiSelblueNum[i]).append(" ");
       }
      
      if(this.currentInWhichPagerFlag == 2) {
        blue_title_fushi.setText(sb.toString());
        result_titleFuShi.setText("点击下方<查询按钮>查结果");
      }else if(this.currentInWhichPagerFlag == 3 || this.currentInWhichPagerFlag == 4) {//选红胆
        blue_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("点击下方<查询按钮>查结果");
      }
    }

}
