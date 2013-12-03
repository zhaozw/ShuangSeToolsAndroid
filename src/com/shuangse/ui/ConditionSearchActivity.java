package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ValueObj;
import com.shuangse.ui.SmartCombineActivity.ItemPair;
import com.shuangse.util.MagicTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ConditionSearchActivity extends Activity {
  private final String TAG = "ConditionSearchActivity";
  private ShuangSeToolsSetApplication appContext;
  private ArrayAdapter<ItemPair> allConditionsAdaptor;
  private LinearLayout bodyContainer;
  private TextView resultTextView;
  private TextView selConditionTitleTextView;
  private Button search_condition_btn;
  private List<Map.Entry<String, Integer>> valueList = null;
  private ValueObj[] redOccurs = null;
  
  private View selectRedView;
  private ImageButton[] redButton = new ImageButton[33];
  private Dialog custDialog = null;
  
  private static final int DISPSECTIONCNT = 2;
  private ArrayList<Integer> selectedRedNumbers = new ArrayList<Integer>();
  
  public final static int INVALID_CONDITION_ID = 0;
  public final static int CONDITION_ID_COND1 = 1;
  public final static int CONDITION_ID_COND2 = 2;
  public final static int CONDITION_ID_COND3 = 3;
  public final static int CONDITION_ID_COND4 = 4;
  public final static int CONDITION_ID_COND5 = 5;
  public final static int CONDITION_ID_COND6 = 6;
  public final static int CONDITION_ID_COND7 = 7;
  public final static int CONDITION_ID_COND8 = 8;
  public final static int CONDITION_ID_COND9 = 9;
  public final static int CONDITION_ID_COND10 = 10;
  
  private int currentSelConditionTypeId = INVALID_CONDITION_ID;
  
  public static List<ItemPair> allSelectionConditions;
  static {
    allSelectionConditions = new ArrayList<ItemPair>();
    initializeAllSelectionConditionTypes(allSelectionConditions);
  };
  
  private ProgressDialog progressDialog = null;
  private void showProgressDialog(String title, String msg) {
    progressDialog = new ProgressDialog(ConditionSearchActivity.this);
    progressDialog.setTitle(title);
    progressDialog.setMessage(msg);
    progressDialog.setCancelable(false);
    progressDialog.show();
  }

  private void hideProgressBox() {
    if (progressDialog != null) {
      progressDialog.dismiss();
      progressDialog = null;
    }
  }

  public static final int DISPPROGRESSDIALOG = 1;
  public static final int HIDEPROGRESSDIALOG = 2;
  public static final int DISPRESULTSTR = 3;
  public static final int SETBTNTEXT = 4;
  public static final int SETTITLETEXT = 5;
  
  //��ʾ2��ͬ�����ͳ��ʱ�İ�ť
  public static final int DISPALLTWOCODESSTAT = 1;
  //��ʾ3��ͬ�����ͳ��ʱ�İ�ť
  public static final int DISPALLTHREECODESSTAT = 2;
  //��ʾ��ť����Ϊ������1-33����
  public static final int DISPORDERBYOCCCNT = 3;
  
  Handler msgHandler = new MHandler(this);
  static class MHandler extends Handler {
    WeakReference<ConditionSearchActivity> mActivity;

    MHandler(ConditionSearchActivity mAct) {
      mActivity = new WeakReference<ConditionSearchActivity>(mAct);
    }

    @Override
    public void handleMessage(Message msg) {
      final ConditionSearchActivity theActivity = mActivity.get();
      
      theActivity.hideProgressBox();
      
      switch (msg.what) {
      case SETBTNTEXT:
        final int btnType = msg.arg1;
        theActivity.search_condition_btn.setText(msg.getData().getString("ButtonText"));
        theActivity.search_condition_btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            ButtonState btnState = (ButtonState) theActivity.search_condition_btn.getTag();
            if(btnState == null) {
              btnState = new ButtonState();
            }
            
            String dispStr = "";
            switch(btnType) {
            case ConditionSearchActivity.DISPALLTWOCODESSTAT:
              dispStr = theActivity.dispCompayCodesResult(theActivity.valueList, btnState.ifClicked);
              break;
            case ConditionSearchActivity.DISPALLTHREECODESSTAT:
              dispStr = theActivity.dispCompayCodesResult(theActivity.valueList, btnState.ifClicked);
              break;
            case ConditionSearchActivity.DISPORDERBYOCCCNT:
              dispStr = theActivity.formatRedOccursDispStr(theActivity.redOccurs, btnState.ifClicked);
              break;
            default:
              break;
            }//end switch
            theActivity.resultTextView.setText(dispStr);
            btnState.ifClicked = (!btnState.ifClicked);
            theActivity.search_condition_btn.setTag(btnState);
            if(btnType == ConditionSearchActivity.DISPALLTHREECODESSTAT || 
                btnType == ConditionSearchActivity.DISPALLTWOCODESSTAT) {
                    if(btnState.ifClicked) {
                        theActivity.search_condition_btn.setText("��ʾ����ͳ������");
                    } else {
                      theActivity.search_condition_btn.setText("��ʾȫ��ͳ������");
                    }
            } else if(btnType == ConditionSearchActivity.DISPORDERBYOCCCNT) {
                    if(btnState.ifClicked) {
                      theActivity.search_condition_btn.setText("���������1-33����");
                    } else {
                      theActivity.search_condition_btn.setText("�����ִ�����������");
                    }
            }
          }
        });
        break;
        
      case SETTITLETEXT:
        theActivity.selConditionTitleTextView.setText(msg.getData().getString("TitleText"));
        break;
      case DISPPROGRESSDIALOG:
        theActivity.showProgressDialog("��ʾ","���Եȣ����ڼ���......");
        break;
      case DISPRESULTSTR:
        theActivity.resultTextView.setText(msg.getData().getString("ResultStr"));
        theActivity.hideProgressBox();
        break;
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //���±���
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.conditionsearch);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_conditionsearch);
  
    Button returnBtn = (Button)findViewById(R.id.returnbtn);
    returnBtn.setVisibility(View.VISIBLE);
    Button helpBtn = (Button)findViewById(R.id.helpbtn);
    helpBtn.setVisibility(View.VISIBLE);
    helpBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String htmlMsg = "��ҳΪ���ṩѡ�ų��õļ������ߣ���������ѡ��ĺ������Ч�ԣ��������Ч�Էǳ���Ҫ��" +
            "������ѡ��ĵ�ע��������ʷ�Ͽ������ģ����㲻֪������ô�������˷����Ͷ�ʣ��ٱ��磬��ѡ���һ�����" +
            "�������8��10�����򣬶����������ʷ�ϻ�����Ѿ�������6�죬��ô�ٴο���6��Ŀ����нϵͣ�����������ѡ��" +
            "ʧ�󣬻���������н��ĸ��ʣ�" +" <br>\t һ��������ѡ���Ӧ���������ͣ�Ȼ�������·��ġ���ѯ����ť���ɣ�ע�⣺" +
            "����ͳ�ƺ�������ͬ��������Ҫ����Գ�ʱ����㣬�����ĵȴ�һ�¡�";
        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, ConditionSearchActivity.this).show();
      }
    });
    returnBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });    
    
    appContext = (ShuangSeToolsSetApplication) getApplication();
    bodyContainer = (LinearLayout)findViewById(R.id.bodyContainer);
    resultTextView = (TextView)findViewById(R.id.result_text);
    search_condition_btn = (Button)findViewById(R.id.search_condition_btn);

    LineBreakLayout redLine = new LineBreakLayout(ConditionSearchActivity.this);
    selectRedView =  ((LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.selectred, redLine, false);
    selConditionTitleTextView = (TextView)findViewById(R.id.selCondResultTitle);
    selConditionTitleTextView.setText("��ѡ���ѯ������Ȼ����<��ѯ>��ť��ѯ.");
    findAndSetButtonViewAction();
    
    Spinner spinner_condition_type = (Spinner) findViewById(R.id.spinner_cond_type);
    allConditionsAdaptor = new ArrayAdapter<ItemPair>(this, android.R.layout.simple_spinner_item, allSelectionConditions);
    allConditionsAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    spinner_condition_type.setAdapter(allConditionsAdaptor);
    selectedRedNumbers.clear();
    spinner_condition_type.setOnItemSelectedListener( new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
          Log.i(TAG, "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString());
          
          currentSelConditionTypeId = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
          Log.i(TAG, "Selected Condition Type :" + currentSelConditionTypeId);

          bodyContainer.removeAllViews();
          resultTextView.setText("");
          search_condition_btn.setVisibility(View.VISIBLE);
          selConditionTitleTextView.setText("");
          if(currentSelConditionTypeId == INVALID_CONDITION_ID) {
            search_condition_btn.setText("��ѯ");
            selConditionTitleTextView.setText("��ѡ���ѯ������Ȼ����<��ѯ>��ť.");
            search_condition_btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                InfoMessageBox("��ʾ","����ѡ��һ����ѯ����");
              }
            });
          } else if(currentSelConditionTypeId == CONDITION_ID_COND1) {
            selConditionTitleTextView.setText("ѡ����(1-33��)����, ��<��ѯ>��ѯ���������ʷ�������:");
            search_condition_btn.setText("��ѯ");
            //inflate and attach the view
            bodyContainer.addView(selectRedView);
            
            search_condition_btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                //String statText = appContext.countHistoryOutDetailsForRedset(selectedRedNumbers);
//              resultTextView.setText(statText);
                custDialog = customInfoMsgBox("��������ʷ����ͳ�ƽ��");
               
                custDialog.show();
              }
            });
          } else if(currentSelConditionTypeId == CONDITION_ID_COND2) {
            Message msg = new Message();
            msg.what = ConditionSearchActivity.DISPPROGRESSDIALOG;
            msgHandler.sendMessage(msg);

            Thread t2 = new Thread() {
              public void run() {
                valueList = appContext.countTwoRedNumberOccurs();
                String resultStr = dispCompayCodesResult(valueList, true);

                Message msgTitle = new Message();
                msgTitle.what = ConditionSearchActivity.SETTITLETEXT;
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("TitleText","������ʷ����" + appContext.getAllHisData().size()+"���У�528��2�����ͬ��������£�");
                msgTitle.setData(bundleTitle);
                msgHandler.sendMessage(msgTitle);

                Message msgBtn = new Message();
                msgBtn.arg1 = ConditionSearchActivity.DISPALLTWOCODESSTAT;
                msgBtn.what = ConditionSearchActivity.SETBTNTEXT;
                Bundle bundleBtn = new Bundle();
                bundleBtn.putString("ButtonText", "��ʾȫ��ͳ������");
                msgBtn.setData(bundleBtn);
                msgHandler.sendMessage(msgBtn);

                Message msg2 = new Message();
                msg2.what = ConditionSearchActivity.DISPRESULTSTR;
                Bundle bundle = new Bundle();
                bundle.putString("ResultStr", resultStr);
                msg2.setData(bundle);
                msgHandler.sendMessage(msg2);
              }
            };
            
            t2.start();
            
          } else if(currentSelConditionTypeId == CONDITION_ID_COND3) {

            Message msg = new Message();
            msg.what = ConditionSearchActivity.DISPPROGRESSDIALOG;
            msgHandler.sendMessage(msg);

            Thread t1 = new Thread () {
              public void run() {
                valueList = appContext.countThreeRedNumberOccurs();
                String reStr = dispCompayCodesResult(valueList, true);
                
                Message msgTitle = new Message();
                msgTitle.what = ConditionSearchActivity.SETTITLETEXT;
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("TitleText", "������ʷ����"+  appContext.getAllHisData().size()+"���У�5456��3�����ͬ��������£�");
                msgTitle.setData(bundleTitle);
                msgHandler.sendMessage(msgTitle);

                Message msgBtn = new Message();
                msgBtn.what = ConditionSearchActivity.SETBTNTEXT;
                msgBtn.arg1 = ConditionSearchActivity.DISPALLTHREECODESSTAT;
                Bundle bundleBtn = new Bundle();
                bundleBtn.putString("ButtonText", "��ʾȫ��ͳ������");
                msgBtn.setData(bundleBtn);
                msgHandler.sendMessage(msgBtn);
                
                Message msg2 = new Message();
                msg2.what = ConditionSearchActivity.DISPRESULTSTR;
                Bundle bundle = new Bundle();
                bundle.putString("ResultStr", reStr);
                msg2.setData(bundle);
                msgHandler.sendMessage(msg2);
              }
            };
            t1.start();
          } else if(currentSelConditionTypeId == CONDITION_ID_COND4 || 
                        currentSelConditionTypeId == CONDITION_ID_COND5 ||
                        currentSelConditionTypeId == CONDITION_ID_COND6 ||
                        currentSelConditionTypeId == CONDITION_ID_COND7) {
            Message msg = new Message();
            msg.what = ConditionSearchActivity.DISPPROGRESSDIALOG;
            msgHandler.sendMessage(msg);

            Thread t1 = new Thread () {
              public void run() {
                int totalSize = appContext.getAllHisData().size();
                if(currentSelConditionTypeId == CONDITION_ID_COND5) {
                  totalSize = 50;
                } else if(currentSelConditionTypeId == CONDITION_ID_COND6) {
                  totalSize = 100;
                } else if(currentSelConditionTypeId == CONDITION_ID_COND7) {
                  totalSize = 300;
                }
                redOccurs = appContext.countRedNumberOccuresCount(totalSize);
                //ͳ�ƺ�����Ŵ���
                String reStr = formatRedOccursDispStr(redOccurs, true);

                Message msgTitle = new Message();
                msgTitle.what = ConditionSearchActivity.SETTITLETEXT;
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("TitleText", "������ʷ������" + totalSize +"���У�ͳ��1-33�������������£�");
                msgTitle.setData(bundleTitle);
                msgHandler.sendMessage(msgTitle);

                Message msgBtn = new Message();
                msgBtn.what = ConditionSearchActivity.SETBTNTEXT;
                msgBtn.arg1 = ConditionSearchActivity.DISPORDERBYOCCCNT;
                Bundle bundleBtn = new Bundle();
                bundleBtn.putString("ButtonText", "�����ִ�����������");
                msgBtn.setData(bundleBtn);
                msgHandler.sendMessage(msgBtn);
                
                Message msg2 = new Message();
                msg2.what = ConditionSearchActivity.DISPRESULTSTR;
                Bundle bundle = new Bundle();
                bundle.putString("ResultStr", reStr);
                msg2.setData(bundle);
                msgHandler.sendMessage(msg2);
              }
            };
            t1.start();
            
          } else if(currentSelConditionTypeId == CONDITION_ID_COND8) {
            Message msg = new Message();
            msg.what = ConditionSearchActivity.DISPPROGRESSDIALOG;
            msgHandler.sendMessage(msg);

            Thread t8 = new Thread() {
              public void run() {
                
                String resultStr = appContext.getAllRecommed6Cover456Codes();

                Message msgTitle = new Message();
                msgTitle.what = ConditionSearchActivity.SETTITLETEXT;
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("TitleText","����6�����1107568���У�δ�г�4�뼰���ϵĺ�������"+appContext.getRecommed6Cover456CodesCnt()+"�飬���£�");
                msgTitle.setData(bundleTitle);
                msgHandler.sendMessage(msgTitle);
                
                Message msg2 = new Message();
                msg2.what = ConditionSearchActivity.DISPRESULTSTR;
                Bundle bundle = new Bundle();
                bundle.putString("ResultStr", resultStr);
                msg2.setData(bundle);
                msgHandler.sendMessage(msg2);
              }
            };
            
            t8.start();

          } else if(currentSelConditionTypeId == CONDITION_ID_COND9) {
            Message msg = new Message();
            msg.what = ConditionSearchActivity.DISPPROGRESSDIALOG;
            msgHandler.sendMessage(msg);

            Thread t9 = new Thread() {
              public void run() {
                
                String resultStr = appContext.getAllRecommed17Cover6Codes();
                
                Message msgTitle = new Message();
                msgTitle.what = ConditionSearchActivity.SETTITLETEXT;
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("TitleText","����17�����1166803110���У�δ�г�6���������" + appContext.getRecommend17Cover6CodesCnt() + "�飬���£�");
                msgTitle.setData(bundleTitle);
                msgHandler.sendMessage(msgTitle);
                
                Message msg2 = new Message();
                msg2.what = ConditionSearchActivity.DISPRESULTSTR;
                Bundle bundle = new Bundle();
                bundle.putString("ResultStr", resultStr);
                msg2.setData(bundle);
                msgHandler.sendMessage(msg2);
              }
            };
            t9.start();
          } else if(currentSelConditionTypeId == CONDITION_ID_COND10) {
            Message msg = new Message();
            msg.what = ConditionSearchActivity.DISPPROGRESSDIALOG;
            msgHandler.sendMessage(msg);

            Thread t10 = new Thread() {
              public void run() {
                
                String resultStr = appContext.getAllRecommed11Cover56Codes();
                
                Message msgTitle = new Message();
                msgTitle.what = ConditionSearchActivity.SETTITLETEXT;
                Bundle bundleTitle = new Bundle();
                bundleTitle.putString("TitleText","����11�����193536720���У�δ�г�5-6���������" + appContext.getRecommed11Cover56CodesCnt() + "�飬���£�");
                msgTitle.setData(bundleTitle);
                msgHandler.sendMessage(msgTitle);
                
                Message msg2 = new Message();
                msg2.what = ConditionSearchActivity.DISPRESULTSTR;
                Bundle bundle = new Bundle();
                bundle.putString("ResultStr", resultStr);
                msg2.setData(bundle);
                msgHandler.sendMessage(msg2);
              }
            };
            t10.start();
          }
          
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
          Log.i(TAG, "spinner_itemid_single:: onNothingSelected()");
      }
      
    });
  }

  private void findAndSetButtonViewAction() {
    redButton[0] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn1);
    redButton[0].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[0].setImageResource(R.drawable.blankr1);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(1));
        } else {
            btnState.ifClicked = true;
            redButton[0].setImageResource(R.drawable.red1);
            selectedRedNumbers.add(1);
        }
      }
    });
    redButton[1] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn2);
    redButton[1].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[1].setImageResource(R.drawable.blankr2);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(2));
        } else {
            btnState.ifClicked = true;
            redButton[1].setImageResource(R.drawable.red2);
            selectedRedNumbers.add(2);
        }
      }
    });
    redButton[2] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn3);
    redButton[2].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[2].setImageResource(R.drawable.blankr3);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(3));
        } else {
            btnState.ifClicked = true;
            redButton[2].setImageResource(R.drawable.red3);
            selectedRedNumbers.add(3);
        }
      }
    });
    redButton[3] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn4);
    redButton[3].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[3].setImageResource(R.drawable.blankr4);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(4));
        } else {
            btnState.ifClicked = true;
            redButton[3].setImageResource(R.drawable.red4);
            selectedRedNumbers.add(4);
        }
      }
    });
    redButton[4] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn5);
    redButton[4].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[4].setImageResource(R.drawable.blankr5);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(5));
        } else {
            btnState.ifClicked = true;
            redButton[4].setImageResource(R.drawable.red5);
            selectedRedNumbers.add(5);
        }
      }
    });
    redButton[5] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn6);
    redButton[5].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[5].setImageResource(R.drawable.blankr6);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(6));
        } else {
            btnState.ifClicked = true;
            redButton[5].setImageResource(R.drawable.red6);
            selectedRedNumbers.add(6);
        }
      }
    });
    redButton[6] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn7);
    redButton[6].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[6].setImageResource(R.drawable.blankr7);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(7));
        } else {
            btnState.ifClicked = true;
            redButton[6].setImageResource(R.drawable.red7);
            selectedRedNumbers.add(7);
        }
      }
    });
    redButton[7] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn8);
    redButton[7].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[7].setImageResource(R.drawable.blankr8);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(8));
        } else {
            btnState.ifClicked = true;
            redButton[7].setImageResource(R.drawable.red8);
            selectedRedNumbers.add(8);
        }
      }
    });
    redButton[8] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn9);
    redButton[8].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[8].setImageResource(R.drawable.blankr9);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(9));
        } else {
            btnState.ifClicked = true;
            redButton[8].setImageResource(R.drawable.red9);
            selectedRedNumbers.add(9);
        }
      }
    });
    redButton[9] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn10);
    redButton[9].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[9].setImageResource(R.drawable.blankr10);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(10));
        } else {
            btnState.ifClicked = true;
            redButton[9].setImageResource(R.drawable.red10);
            selectedRedNumbers.add(10);
        }
      }
    });
    redButton[10] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn11);
    redButton[10].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[10].setImageResource(R.drawable.blankr11);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(11));
        } else {
            btnState.ifClicked = true;
            redButton[10].setImageResource(R.drawable.red11);
            selectedRedNumbers.add(11);
        }
      }
    });
    redButton[11] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn12);
    redButton[11].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[11].setImageResource(R.drawable.blankr12);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(12));
        } else {
            btnState.ifClicked = true;
            redButton[11].setImageResource(R.drawable.red12);
            selectedRedNumbers.add(12);
        }
      }
    });
    redButton[12] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn13);
    redButton[12].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[12].setImageResource(R.drawable.blankr13);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(13));
        } else {
            btnState.ifClicked = true;
            redButton[12].setImageResource(R.drawable.red13);
            selectedRedNumbers.add(13);
        }
      }
    });
    redButton[13] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn14);
    redButton[13].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[13].setImageResource(R.drawable.blankr14);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(14));
        } else {
            btnState.ifClicked = true;
            redButton[13].setImageResource(R.drawable.red14);
            selectedRedNumbers.add(14);
        }
      }
    });
    redButton[14] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn15);
    redButton[14].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[14].setImageResource(R.drawable.blankr15);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(15));
        } else {
            btnState.ifClicked = true;
            redButton[14].setImageResource(R.drawable.red15);
            selectedRedNumbers.add(15);
        }
      }
    });
    redButton[15] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn16);
    redButton[15].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[15].setImageResource(R.drawable.blankr16);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(16));
        } else {
            btnState.ifClicked = true;
            redButton[15].setImageResource(R.drawable.red16);
            selectedRedNumbers.add(16);
        }
      }
    });
    redButton[16] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn17);
    redButton[16].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[16].setImageResource(R.drawable.blankr17);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(17));
        } else {
            btnState.ifClicked = true;
            redButton[16].setImageResource(R.drawable.red17);
            selectedRedNumbers.add(17);
        }
      }
    });
    redButton[17] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn18);
    redButton[17].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[17].setImageResource(R.drawable.blankr18);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(18));
        } else {
            btnState.ifClicked = true;
            redButton[17].setImageResource(R.drawable.red18);
            selectedRedNumbers.add(18);
        }
      }
    });
    redButton[18] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn19);
    redButton[18].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[18].setImageResource(R.drawable.blankr19);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(19));
        } else {
            btnState.ifClicked = true;
            redButton[18].setImageResource(R.drawable.red19);
            selectedRedNumbers.add(19);
        }
      }
    });
    redButton[19] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn20);
    redButton[19].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[19].setImageResource(R.drawable.blankr20);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(20));
        } else {
            btnState.ifClicked = true;
            redButton[19].setImageResource(R.drawable.red20);
            selectedRedNumbers.add(20);
        }
      }
    });
    redButton[20] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn21);
    redButton[20].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[20].setImageResource(R.drawable.blankr21);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(21));
        } else {
            btnState.ifClicked = true;
            redButton[20].setImageResource(R.drawable.red21);
            selectedRedNumbers.add(21);
        }
      }
    });
    redButton[21] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn22);
    redButton[21].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[21].setImageResource(R.drawable.blankr22);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(22));
        } else {
            btnState.ifClicked = true;
            redButton[21].setImageResource(R.drawable.red22);
            selectedRedNumbers.add(22);
        }
      }
    });
    redButton[22] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn23);
    redButton[22].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[22].setImageResource(R.drawable.blankr23);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(23));
        } else {
            btnState.ifClicked = true;
            redButton[22].setImageResource(R.drawable.red23);
            selectedRedNumbers.add(23);
        }
      }
    });
    redButton[23] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn24);
    redButton[23].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[23].setImageResource(R.drawable.blankr24);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(24));
        } else {
            btnState.ifClicked = true;
            redButton[23].setImageResource(R.drawable.red24);
            selectedRedNumbers.add(24);
        }
      }
    });
    redButton[24] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn25);
    redButton[24].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[24].setImageResource(R.drawable.blankr25);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(25));
        } else {
            btnState.ifClicked = true;
            redButton[24].setImageResource(R.drawable.red25);
            selectedRedNumbers.add(25);
        }
      }
    });
    redButton[25] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn26);
    redButton[25].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[25].setImageResource(R.drawable.blankr26);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(26));
        } else {
            btnState.ifClicked = true;
            redButton[25].setImageResource(R.drawable.red26);
            selectedRedNumbers.add(26);
        }
      }
    });
    redButton[26] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn27);
    redButton[26].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[26].setImageResource(R.drawable.blankr27);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(27));
        } else {
            btnState.ifClicked = true;
            redButton[26].setImageResource(R.drawable.red27);
            selectedRedNumbers.add(27);
        }
      }
    });
    redButton[27] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn28);
    redButton[27].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[27].setImageResource(R.drawable.blankr28);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(28));
        } else {
            btnState.ifClicked = true;
            redButton[27].setImageResource(R.drawable.red28);
            selectedRedNumbers.add(28);
        }
      }
    });
    redButton[28] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn29);
    redButton[28].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[28].setImageResource(R.drawable.blankr29);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(29));
        } else {
            btnState.ifClicked = true;
            redButton[28].setImageResource(R.drawable.red29);
            selectedRedNumbers.add(29);
        }
      }
    });
    redButton[29] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn30);
    redButton[29].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[29].setImageResource(R.drawable.blankr30);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(30));
        } else {
            btnState.ifClicked = true;
            redButton[29].setImageResource(R.drawable.red30);
            selectedRedNumbers.add(30);
        }
      }
    });
    redButton[30] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn31);
    redButton[30].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[30].setImageResource(R.drawable.blankr31);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(31));
        } else {
            btnState.ifClicked = true;
            redButton[30].setImageResource(R.drawable.red31);
            selectedRedNumbers.add(31);
        }
      }
    });
    redButton[31] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn32);
    redButton[31].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[31].setImageResource(R.drawable.blankr32);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(32));
        } else {
            btnState.ifClicked = true;
            redButton[31].setImageResource(R.drawable.red32);
            selectedRedNumbers.add(32);
        }
      }
    });
    redButton[32] = (ImageButton) selectRedView.findViewById(R.id.blankrbtn33);
    redButton[32].setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ButtonState btnState = (ButtonState) v.getTag();
        if(btnState == null) {
            btnState = new ButtonState();
            v.setTag(btnState);
         }
        if(btnState.ifClicked) {
            redButton[32].setImageResource(R.drawable.blankr33);
            btnState.ifClicked = false;
            selectedRedNumbers.remove(Integer.valueOf(33));
        } else {
            btnState.ifClicked = true;
            redButton[32].setImageResource(R.drawable.red33);
            selectedRedNumbers.add(33);
        }
      }
    });
  }

  static class ButtonState {
      public ButtonState() {
       ifClicked = false;
      }
      public boolean ifClicked;
  };
  
  private static void initializeAllSelectionConditionTypes(
      List<ItemPair> allSelectionConditions2) {
    allSelectionConditions2.clear();
    allSelectionConditions2.add(new ItemPair(INVALID_CONDITION_ID, "���ѡ���ѯ����������"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND1, "��ѯһ�������ʷ�������"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND2, "ͳ�ƺ������ͬ������"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND3, "ͳ�ƺ�������ͬ������"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND4, "ͳ�ƺ���1-33���Ŵ���(������)"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND5, "ͳ�ƺ���1-33���Ŵ���(��50��)"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND6, "ͳ�ƺ���1-33���Ŵ���(��100��)"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND7, "ͳ�ƺ���1-33���Ŵ���(��300��)"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND8, "��ѯδ��4�뼰���ϵĺ���6��"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND9, "��ѯ��δ��6��ĺ���17��"));
    allSelectionConditions2.add(new ItemPair(CONDITION_ID_COND10, "��ѯδ��5�뼰���ϵĺ���11��"));
  }

  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // land
    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        // port
    }
  }
  
  /** ��ʾ����ͬ��������ͬ���Ľ��
   * 
   * @param valueList
   * @param dispShort
   * @return
   */
  private String dispCompayCodesResult(
      List<Map.Entry<String, Integer>> valueList, boolean dispShort) {
    
    if(valueList == null) return "";
    
    if (!dispShort) {
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, Integer> entry : valueList) {
        sb.append(entry.getKey()).append(": ͬ��")
            .append(entry.getValue().toString()).append("��\n");
      }
      return sb.toString();

    } else {
      // ��ʾ�������ٵĲ���
      StringBuilder sb = new StringBuilder();
      int listSize = valueList.size();
      int dispSectionCount = 0;
      // ����ͷ
      Map.Entry<String, Integer> lastEntry = valueList.get(0);
      for (int listIndex = 0; listIndex < listSize; listIndex++) {
        Map.Entry<String, Integer> entry = valueList.get(listIndex);
        sb.append(entry.getKey()).append(": ͬ��")
            .append(entry.getValue().toString()).append("��\n");
        if (entry.getValue() != lastEntry.getValue()) {
          lastEntry = valueList.get(listIndex);
          dispSectionCount++;
          if(dispSectionCount > ConditionSearchActivity.DISPSECTIONCNT) {
            sb.append("......\n");
            break;
          }
        }
      }
      // ����β��
      dispSectionCount = 0;
      lastEntry = valueList.get(listSize - 1);
      int startIndex = 0;
      for (int listIndex = (listSize - 1); listIndex >= 0; listIndex--) {
        Map.Entry<String, Integer> entry = valueList.get(listIndex);
        if (entry.getValue() != lastEntry.getValue()) {
          dispSectionCount++;
          lastEntry = valueList.get(listIndex);
          if(dispSectionCount > ConditionSearchActivity.DISPSECTIONCNT) {
            startIndex = listIndex;
            break;
          }
        }
      }
      
      for(int beginIndex = startIndex; beginIndex < listSize; beginIndex++) {
        Map.Entry<String, Integer> entry = valueList.get(beginIndex);
        sb.append(entry.getKey()).append(": ͬ��").append(entry.getValue().toString()).append("��\n");
      }

      return sb.toString();
    }
  }
  
  private void InfoMessageBox(String title, String msg) {
    AlertDialog notifyDialog = new AlertDialog.Builder(ConditionSearchActivity.this)
        .setTitle(title).setMessage(msg)
        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
          }
        }).create();
    notifyDialog.show();
  }
  
  public Dialog customInfoMsgBox(String title) {
    AlertDialog.Builder builder = new AlertDialog.Builder(ConditionSearchActivity.this);
    LayoutInflater inflater = ConditionSearchActivity.this.getLayoutInflater();
    
    View dialogView = inflater.inflate(R.layout.custscrollspinnerdialog, null);
    final TextView text = (TextView) dialogView.findViewById(R.id.dispText);
    
    Spinner spinner_selection_his_cnt = (Spinner) dialogView.findViewById(R.id.spinner_selection_his_cnt);
    List<ItemPair> allHisCnt = new ArrayList<ItemPair>();
    int totalCnt = appContext.getAllHisData().size();
    
    int startCnt = totalCnt;
    while(startCnt >1) {
      allHisCnt.add(new ItemPair(startCnt, "��"+startCnt+"��"));
      startCnt -=10;
      if(startCnt <= 1) {
        allHisCnt.add(new ItemPair(1, "��1��"));
        break;
      }
    }
    
    ArrayAdapter<ItemPair> selection_his_adaptor = new ArrayAdapter<ItemPair>(this, android.R.layout.simple_spinner_item, allHisCnt);
    selection_his_adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    spinner_selection_his_cnt.setAdapter(selection_his_adaptor);
    spinner_selection_his_cnt.setOnItemSelectedListener( new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        int selLastHisCnt = ((ItemPair)parent.getItemAtPosition(pos)).getItemVal();
        String textMsg = appContext.countHistoryOutDetailsForRedset(selectedRedNumbers, selLastHisCnt);
        text.setText(textMsg);
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });
    
    builder.setView(dialogView)
        .setTitle(title)
        .setPositiveButton(R.string.OK,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int id) {
                if(dialog != null) dialog.dismiss();
                custDialog = null;
              }
            });
    return builder.create();
  }


  //ʹ��ð�������������У�����һ��Clone�����Ź�����¶���
  private ValueObj[] sort_asc(ValueObj[] valueOccurs) {
      ValueObj[] redOccurs = new ValueObj[valueOccurs.length];
      
      for(int m=0; m < valueOccurs.length; m++) {
        redOccurs[m] = new ValueObj();
        redOccurs[m].val = valueOccurs[m].val;
        redOccurs[m].cnt = valueOccurs[m].cnt;
      }
      
      int tmp_val;
      int tmp_cnt;
      
      for(int i=0;i<(redOccurs.length-1); i++) {
        for(int j=(redOccurs.length-1); j>i; j--) {
            if(redOccurs[j].cnt < redOccurs[j-1].cnt) {
              tmp_val = redOccurs[j].val;
              tmp_cnt = redOccurs[j].cnt;
              
              redOccurs[j].val = redOccurs[j-1].val;
              redOccurs[j].cnt = redOccurs[j-1].cnt;
              redOccurs[j-1].val = tmp_val;
              redOccurs[j-1].cnt = tmp_cnt;
            }
        }
    }
      
    return redOccurs;
  }

  
  private String formatRedOccursDispStr(ValueObj[] valueObj, boolean orderFlag) {
    StringBuilder sb = new StringBuilder();
    if (!orderFlag) { // �����ִ�����������
      ValueObj[] sortedObj = this.sort_asc(valueObj);
      for (int i = 0; i < sortedObj.length; i++) {
        if (sortedObj[i].val <= 9) {
          sb.append("����0").append(sortedObj[i].val).append("��")
              .append(sortedObj[i].cnt).append("��\n");
        } else {
          sb.append("����").append(sortedObj[i].val).append("��")
              .append(sortedObj[i].cnt).append("��\n");
        }
      }
    } else {
      for (int i = 0; i < valueObj.length; i++) {
        if (valueObj[i].val <= 9) {
          sb.append("����0").append(valueObj[i].val).append("��")
              .append(valueObj[i].cnt).append("��\n");
        } else {
          sb.append("����").append(valueObj[i].val).append("��")
              .append(valueObj[i].cnt).append("��\n");
        }
      }
    }
    return sb.toString();
  }
  
}
