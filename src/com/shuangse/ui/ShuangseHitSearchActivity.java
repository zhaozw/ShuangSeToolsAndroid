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
    //��ʽ
    private int singleSelItemId;
    private ShuangseCodeItem currentSelItem;
    private int singleSelBlue;
    private ShuangseCodeItem inputItem;
    private Button inputRedBtn;
    private TextView resultCodeView;
    private TextView result_title;
    private TextView red_title;
    
    //��ʽ
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
    
    //����
    private int dantuoSelItemId;
    private ShuangseCodeItem currentSelItemdantuo;
    private TextView resultCodeViewdantuo;
    private Button inputRedBtndantuo;
    private Button inputRed2Btndantuo;
    private Button inputBlueBtndantuo;
    private TextView result_titledantuo;
    //��ʾѡ��ĺ���
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
        // ���±���
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
            String htmlMsg = "��ҳΪ���ṩ��ѯ�������Ʊ���н���Ϣ���밴��ʾ�����Ӧ�Ĺ�����룬���ɲ�ѯ���н��Ľ��������";
            MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, ShuangseHitSearchActivity.this).show();
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
        
        //��ʼ��һҳ
        localViewPager.setCurrentItem(0);
        currentInWhichPagerFlag = 1;
        
        localViewPager.setOnPageChangeListener(new OnPageChangeListener() {
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
        
        //����ѡ�����ݳ�ʼ��
        blueValAdapter = ArrayAdapter.createFromResource(this,
                R.array.blue_value_list, android.R.layout.simple_spinner_item);
        blueValAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // ��ʼ����ǰ��ʾ�ĵ�һ��view
        initlizeFirstView();
        
        //���õڶ���View�е�����
        initlizeSecondView();
        
        //���õ�����View�е�����
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
        //��ע�����
        inputRedBtn = (Button)contentLayout1.findViewById(R.id.input_single_red);
        inputRedBtn.setOnClickListener(new View.OnClickListener() {
          
          @Override
          public void onClick(View v) {
            //��ʼ��һ���Զ����Dialog
            Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
            dialog.show();
          }
        });
        
        red_title = (TextView) contentLayout1.findViewById(R.id.red_title);
        //�������� ��
        resultCodeView = (TextView) contentLayout1.findViewById(R.id.result_code);
        //ѡ����һ��
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
        //�������������
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
        
        //�н���� - Ĭ�ϳ�ʼֵ
        result_title = (TextView) contentLayout1.findViewById(R.id.result_title);
        
        //��ѯ��ť
        Button single_search_btn = (Button)contentLayout1.findViewById(R.id.search_single_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(inputItem == null) {
                  InfoMessageBox("����","����ѡ���㹺��ĵ�ʽ������룡");
                  return;
                }
                Log.i(TAG, "itemID:" + singleSelItemId +
                        " blue:" + singleSelBlue);
                result_title.setText("");

                int hitResult = MagicTool.getHitResult(currentSelItem, inputItem);
                switch (hitResult) {
                    case 1:
                        result_title.setText("��6��1������ϲ����һ�Ƚ�������500��Ԫ����࣬��μ����ڿ�������.");
                        break;
                    case 2:
                        result_title.setText("��6��0������ϲ���ж��Ƚ�����������Ԫ����࣬��μ����ڿ�������.");
                        break;
                    case 3:
                        result_title.setText("��5��1������ϲ�������Ƚ�������3000Ԫ.");
                        break;
                    case 4:
                        result_title.setText("��4��1������ϲ�����ĵȽ�������200Ԫ.");
                        break;
                    case 5:
                        result_title.setText("��5��0������ϲ�����ĵȽ�������200Ԫ.");
                        break;
                    case 6:
                        result_title.setText("��3��1������ϲ������Ƚ�������10Ԫ.");
                        break;
                    case 7:
                        result_title.setText("��4��0������ϲ������Ƚ�������10Ԫ.");
                        break;
                    case 8:
                        result_title.setText("��2��1������ϲ�������Ƚ�������5Ԫ.");
                        break;
                    case 9:
                      result_title.setText("��1��1������ϲ�������Ƚ�������5Ԫ.");
                      break;    
                    case 10:
                      result_title.setText("��0��1������ϲ�������Ƚ�������5Ԫ.");
                      break; 
                    case 11:
                      result_title.setText("��3��0����δ�н�������0Ԫ.");
                      break;  
                    case 12:
                      result_title.setText("��2��0����δ�н�������0Ԫ.");
                      break;
                    case 13:
                      result_title.setText("��1��0����δ�н�������0Ԫ.");
                      break;
                    case 14:
                      result_title.setText("��0��0����δ�н�������0Ԫ.");
                      break;
                    case -1:
                    default:
                        result_title.setText("δ�н�������0Ԫ.");
                        break;
                }
            }
        });
    }
    
    //���õڶ���View�е�����
    private void initlizeSecondView() {
        //�������� ��
        resultCodeViewFuShi = (TextView) contentLayout2.findViewById(R.id.result_code_fushi);
        //�ں�ѡ��
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
        
        //����
        inputRedBtnFuShi = (Button)contentLayout2.findViewById(R.id.input_fushi_red);
        inputRedBtnFuShi.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //��ʼ��һ���Զ����Dialog
            Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
            dialog.show();
          }
        });
        //����
        inputBlueBtnFuShi = (Button)contentLayout2.findViewById(R.id.input_fushi_blue);
        inputBlueBtnFuShi.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
          //��ʼ��һ���Զ����Dialog
            Dialog dialog = new SelectBlueDialog(ShuangseHitSearchActivity.this, R.style.SelectBlueDialog, ShuangseHitSearchActivity.this);
            dialog.show();
          }
        });
        
        //�н����
        result_titleFuShi = (TextView) contentLayout2.findViewById(R.id.result_title_fushi);
        
        //��ѯ��ť
        Button single_search_btn = (Button)contentLayout2.findViewById(R.id.search_fushi_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fuShiSelRedNum == null) {
                  InfoMessageBox("����","��ѡ��������ĸ�ʽ����");
                  return;
                }
                
                if(fuShiSelblueNum == null) {
                  InfoMessageBox("����","��ѡ�������������");
                  return;
                }
                               
                if(fuShiSelRedNum.length < 6) {
                    InfoMessageBox("����","ѡ��ĺ���������ԣ�����6������");
                    return;
                }

                if(fuShiSelRedNum.length > 20) {
                  InfoMessageBox("����","˫ɫ�����ʽ��಻�ܳ���20������������ѡ��");
                  return;
                }
                
                //�������
                if(fuShiSelblueNum.length < 1) {
                  InfoMessageBox("����","ѡ�������������ԣ�����1������");
                  return;
                }
              
                if(fuShiSelblueNum.length > 16) {
                  InfoMessageBox("����","ѡ�������ʽ��಻�ܳ���16������������ѡ��");
                  return;
                }
                result_titleFuShi.setText("");
              
              //fuShiSelRedNum[] fuShiSelblueNum[]
              //���20���� + 16�� = 38760 �������� 620160
              SparseIntArray resultMap = new SparseIntArray ();
              long totalItemCnt = MagicTool.combine(fuShiSelRedNum, fuShiSelRedNum.length, 6, currentSelItemFuShi, fuShiSelblueNum,fuShiSelblueNum.length,resultMap);
              
              StringBuffer dispTextBuf = new StringBuffer();
              dispTextBuf.append("�ø�ʽ������").append(totalItemCnt).append("ע����,");
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
    
    //���õ�����View�е�����
    private void initlizeThirdView() {
      //�������� ��
      resultCodeViewdantuo = (TextView) contentLayout3.findViewById(R.id.result_code_dantuo);
      //�ں�ѡ��
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
      
      //���� �� �� ����
      inputRedBtndantuo = (Button)contentLayout3.findViewById(R.id.input_dantuo_red);
      inputRedBtndantuo.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        //��ʼ��һ���Զ����Dialog
          Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
          currentInWhichPagerFlag = 3;
          dialog.show();
        }
      });
      
      inputRed2Btndantuo = (Button)contentLayout3.findViewById(R.id.input_dantuo_red2);
      inputRed2Btndantuo.setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
        //��ʼ��һ���Զ����Dialog
          Dialog dialog = new SelectRedDialog(ShuangseHitSearchActivity.this, R.style.SelectRedDialog, ShuangseHitSearchActivity.this);
          currentInWhichPagerFlag = 4;
          dialog.show();
        }
      });
      //����
      inputBlueBtndantuo = (Button)contentLayout3.findViewById(R.id.input_dantuo_blue);
      inputBlueBtndantuo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //��ʼ��һ���Զ����Dialog
          Dialog dialog = new SelectBlueDialog(ShuangseHitSearchActivity.this, R.style.SelectBlueDialog, ShuangseHitSearchActivity.this);
          dialog.show();
        }
      });
      
      //�н����
      result_titledantuo = (TextView) contentLayout3.findViewById(R.id.result_title_dantuo);
      
      red_title_dantuo = (TextView) contentLayout3.findViewById(R.id.red_title_dantuo);
      red2_title_dantuo = (TextView) contentLayout3.findViewById(R.id.red2_title_dantuo);
      blue_title_dantuo = (TextView) contentLayout3.findViewById(R.id.blue_title_dantuo);
      
      //��ѯ��ť
      Button single_search_btn = (Button)contentLayout3.findViewById(R.id.search_dantuo_btn);
      single_search_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(redDanNum == null) {
                InfoMessageBox("����","��ѡ������룬��������1-5����");
                return;
              }
              if(redTuoNum == null) {
                InfoMessageBox("����","��ѡ��������룬��������1-20����");
                return;
              }
              
              if(redDanNum.length > 5 || redDanNum.length < 1) {
                  InfoMessageBox("����","������ֻ����1-5����");
                  return;
              }
              
              if(redTuoNum.length > 20 || redTuoNum.length < 1) {
                  InfoMessageBox("����","��������ֻ����1-20����");
                  return;
              }
              
              //���Ϻϲ�
              danTuoTotalRedNumbers = MagicTool.join(redDanNum, redTuoNum);
              if(danTuoTotalRedNumbers.length < 6) {
                InfoMessageBox("����","����ĵ�����������һ���������6������������ѡ��");
                return;
              }

              //reuse fuShiSelblueNum
              int[] blueNum = fuShiSelblueNum;
              if(blueNum.length < 1 || blueNum.length > 16) {
                  InfoMessageBox("����","ѡ�������������ԣ�");
                  return;
              }
            
              result_titledantuo.setText("");
              
            //redNum[] red2Num[] blueNum[]
            SparseIntArray resultMap = new SparseIntArray();
            long totalItemCnt = MagicTool.combine(danTuoTotalRedNumbers, danTuoTotalRedNumbers.length, redDanNum, redDanNum.length,
                6, currentSelItemdantuo, blueNum,blueNum.length,resultMap);
            
            StringBuffer dispTextBuf = new StringBuffer();
            dispTextBuf.append("�õ���ʽ������").append(totalItemCnt).append("ע����,");
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
          return "��"+hitCount+"ע6��1��(һ�Ƚ�), ����ÿע500��Ԫ�����;";
        case 2:
          return "��"+hitCount+"ע6��0��(���Ƚ�), ����ÿע����Ԫ�����;";
        case 3:
          return "��"+hitCount+"ע5��1��(���Ƚ�), ����ÿע3000Ԫ;";
        case 4:
          return "��"+hitCount+"ע4��1��(�ĵȽ�), ����ÿע200Ԫ;";
        case 5:
          return "��"+hitCount+"ע5��0��(�ĵȽ�), ����ÿע200Ԫ;";
        case 6:
          return "��"+hitCount+"ע3��1��(��Ƚ�), ����ÿע10Ԫ;";
        case 7:
          return "��"+hitCount+"ע4��0��(��Ƚ�), ����ÿע10Ԫ;";
        case 8:
          return "��"+hitCount+"ע2��1��(���Ƚ�), ����ÿע5Ԫ;";
        case 9:
          return "��"+hitCount+"ע1��1��(���Ƚ�), ����ÿע5Ԫ;";
        case 10:
          return "��"+hitCount+"ע0��1��(���Ƚ�), ����ÿע5Ԫ;";
        case 11:
          return "��"+hitCount+"ע3��0��(�޽�);";
        case 12:
          return "��"+hitCount+"ע2��0��(�޽�);";
        case 13:
          return "��"+hitCount+"ע1��0��(�޽�);";
        case 14:
          return "��"+hitCount+"ע0��0��(�޽�);";
        case -1:
        default:
          return "δ�н�������0Ԫ.";
      }
    }

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {
      
      if(this.currentInWhichPagerFlag == 1) {
          if(redList.size() !=6 ) {
            InfoMessageBox("����","ѡ��ĺ���������ԣ���ʽ��6������");
            return;
          }
        
         int[] redNum = new int[6];
         for(int i=0;i < redList.size(); i++) {
              redNum[i] = redList.get(i);
              if(redNum[i] <1 || redNum[i] > 33) {
                InfoMessageBox("����","ѡ��ĺ��򲻶ԣ�����Χ��1-33��");
                return;
              }
          }
          
          inputItem = new ShuangseCodeItem(singleSelItemId, redNum, singleSelBlue);
          StringBuffer sb = new StringBuffer();
          sb.append("�죺").append(inputItem.toRedString());
          red_title.setText(sb.toString());      
          result_title.setText("����·�<��ѯ��ť>����");
      } else if(this.currentInWhichPagerFlag == 2) {
          if(redList.size() < 6 ) {
            InfoMessageBox("����","ѡ��ĺ���������ԣ�����6������");
            return;
          }
          fuShiSelRedNum = new int[redList.size()]; 
          StringBuffer sb = new StringBuffer();
          sb.append("�죺");
          
          for (int i = 0; i < redList.size(); i++) {
            fuShiSelRedNum[i] = redList.get(i);
            if(fuShiSelRedNum[i] < 10) {
              sb.append("0");
            }
            sb.append(fuShiSelRedNum[i]).append(" ");
           }

          red_title_fushi.setText(sb.toString());
          result_titleFuShi.setText("����·�<��ѯ��ť>����");
      } else if(this.currentInWhichPagerFlag == 3) {//ѡ��
        if(redList.size() < 1 || redList.size() > 5) {
          InfoMessageBox("����","ѡ��ĺ�����������ԣ�ѡ��1-5�������룡");
          return;
        }
        redDanNum = new int[redList.size()]; 
        StringBuffer sb = new StringBuffer();
        sb.append("�쵨��");
        
        for (int i = 0; i < redList.size(); i++) {
          redDanNum[i] = redList.get(i);
          if(redDanNum[i] < 10) {
            sb.append("0");
          }
          sb.append(redDanNum[i]).append(" ");
         }

        red_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("����·�<��ѯ��ť>����");
      }else if(this.currentInWhichPagerFlag == 4) {//ѡ����
        if(redList.size() < 1 || redList.size() > 20) {
          InfoMessageBox("����","ѡ��ĺ�������������ԣ�ѡ��1-20���������룡");
          return;
        }
        redTuoNum = new int[redList.size()]; 
        StringBuffer sb = new StringBuffer();
        sb.append("���ϣ�");
        
        for (int i = 0; i < redList.size(); i++) {
          redTuoNum[i] = redList.get(i);
          if(redTuoNum[i] < 10) {
            sb.append("0");
          }
          sb.append(redTuoNum[i]).append(" ");
         }

        red2_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("����·�<��ѯ��ť>����");
      }
      
    }

    @Override
    public void sendSelectedBlueData(ArrayList<Integer> blueList) {
      
      if(blueList.size() < 1 ) {
        InfoMessageBox("����","ѡ�������������ԣ�����1������");
        return;
      }
    
      fuShiSelblueNum = new int[blueList.size()];
      StringBuffer sb = new StringBuffer();
      sb.append("����");
      for(int i=0;i < blueList.size(); i++) {
         fuShiSelblueNum[i] = blueList.get(i);
         if(fuShiSelblueNum[i] < 10) {
           sb.append("0");
         }
         sb.append(fuShiSelblueNum[i]).append(" ");
       }
      
      if(this.currentInWhichPagerFlag == 2) {
        blue_title_fushi.setText(sb.toString());
        result_titleFuShi.setText("����·�<��ѯ��ť>����");
      }else if(this.currentInWhichPagerFlag == 3 || this.currentInWhichPagerFlag == 4) {//ѡ�쵨
        blue_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("����·�<��ѯ��ť>����");
      }
    }

}
