package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ShuangseCodeItem;
import com.shuangse.util.MagicTool;

public class FuShiSearchFragment extends BaseFragment implements
        SelectRedDialogCallback, SelectBlueDialogCallback {
    private final static String TAG = "FuShiSearchFragment";
    private List<String> itemIDs;
    private List<ShuangseCodeItem> allHisData;
    private ShuangSeToolsSetApplication appContext;
    private ArrayAdapter<String> allitemIDAdaptor;
    private Activity activity;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (ShuangSeToolsSetApplication) this.getActivity()
                .getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        isInit = true;
        return inflater.inflate(R.layout.fushisearch, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = this.getActivity();
        itemIDs = new ArrayList<String>();
        allHisData = appContext.getAllHisData();
        if (allHisData != null && allHisData.size() > 1) {
            int maxSize = (allHisData.size() - 1);
            for (int i = maxSize; i >= 0; i--) {
                itemIDs.add(Integer.toString(allHisData.get(i).id));
            }
            allitemIDAdaptor = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item, itemIDs);
            allitemIDAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        // 初始化当前显示的第3个view
        initlizeThirdView(view);

    }

    @Override
    protected void showData() {

    }

    private void InfoMessageBox(String title, String msg) {
        AlertDialog notifyDialog = new AlertDialog.Builder(this.getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
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


  //设置第3个View中的内容
  private void initlizeThirdView(View view) {
      //开奖号码 域
      resultCodeViewFuShi = (TextView) view.findViewById(R.id.result_code_fushi);
      //期号选择
      Spinner spinner_itemid_fushi = (Spinner) view.findViewById(R.id.spinner_itemid_fushi);            
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
      
      red_title_fushi = (TextView)view.findViewById(R.id.red_title_fushi);
      blue_title_fushi = (TextView)view.findViewById(R.id.blue_title_fushi);
      
      //红球
      inputRedBtnFuShi = (Button)view.findViewById(R.id.input_fushi_red);
      inputRedBtnFuShi.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //初始化一个自定义的Dialog
          Dialog dialog = new SelectRedDialog(activity, R.style.SelectRedDialog, FuShiSearchFragment.this);
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      });
      //篮球
      inputBlueBtnFuShi = (Button)view.findViewById(R.id.input_fushi_blue);
      inputBlueBtnFuShi.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        //初始化一个自定义的Dialog
          Dialog dialog = new SelectBlueDialog(activity, R.style.SelectBlueDialog, FuShiSearchFragment.this);
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      });
      
      //中奖结果
      result_titleFuShi = (TextView) view.findViewById(R.id.result_title_fushi);
      
      //查询按钮
      Button single_search_btn = (Button)view.findViewById(R.id.search_fushi_btn);
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
    
    
    @Override
    public void sendSelectedBlueData(ArrayList<Integer> blueList) {
        if (blueList.size() < 1) {
            InfoMessageBox("错误", "选择的蓝球个数不对，至少1个篮球！");
            return;
        }

        fuShiSelblueNum = new int[blueList.size()];
        StringBuffer sb = new StringBuffer();
        sb.append("蓝：");
        for (int i = 0; i < blueList.size(); i++) {
            fuShiSelblueNum[i] = blueList.get(i);
            if (fuShiSelblueNum[i] < 10) {
                sb.append("0");
            }
            sb.append(fuShiSelblueNum[i]).append(" ");
        }

         blue_title_fushi.setText(sb.toString());
         result_titleFuShi.setText("点击下方<查询按钮>查结果");
         
//         }else if(this.currentInWhichPagerFlag == 3 ||
//         this.currentInWhichPagerFlag == 4) {//选红胆
//         blue_title_dantuo.setText(sb.toString());
//         result_titledantuo.setText("点击下方<查询按钮>查结果");
//         }
    }

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {
        if (redList.size() < 6) {
            InfoMessageBox("错误", "选择的红球个数不对，至少6个红球！");
            return;
        }
        fuShiSelRedNum = new int[redList.size()];
        StringBuffer sb = new StringBuffer();
        sb.append("红：");

        for (int i = 0; i < redList.size(); i++) {
            fuShiSelRedNum[i] = redList.get(i);
            if (fuShiSelRedNum[i] < 10) {
                sb.append("0");
            }
            sb.append(fuShiSelRedNum[i]).append(" ");
        }

        red_title_fushi.setText(sb.toString());
        result_titleFuShi.setText("点击下方<查询按钮>查结果");
//        } else if (this.currentInWhichPagerFlag == 3) {// 选胆
//            if (redList.size() < 1 || redList.size() > 5) {
//                InfoMessageBox("错误", "选择的红球胆码个数不对，选择1-5个红球胆码！");
//                return;
//            }
//            redDanNum = new int[redList.size()];
//            StringBuffer sb = new StringBuffer();
//            sb.append("红胆：");
//
//            for (int i = 0; i < redList.size(); i++) {
//                redDanNum[i] = redList.get(i);
//                if (redDanNum[i] < 10) {
//                    sb.append("0");
//                }
//                sb.append(redDanNum[i]).append(" ");
//            }
//
//            red_title_dantuo.setText(sb.toString());
//            result_titledantuo.setText("点击下方<查询按钮>查结果");
//        } else if (this.currentInWhichPagerFlag == 4) {// 选红拖
//            if (redList.size() < 1 || redList.size() > 20) {
//                InfoMessageBox("错误", "选择的红球拖码个数不对，选择1-20个红球拖码！");
//                return;
//            }
//            redTuoNum = new int[redList.size()];
//            StringBuffer sb = new StringBuffer();
//            sb.append("红拖：");
//
//            for (int i = 0; i < redList.size(); i++) {
//                redTuoNum[i] = redList.get(i);
//                if (redTuoNum[i] < 10) {
//                    sb.append("0");
//                }
//                sb.append(redTuoNum[i]).append(" ");
//            }
//
//            red2_title_dantuo.setText(sb.toString());
//            result_titledantuo.setText("点击下方<查询按钮>查结果");
//        }

    }

}
