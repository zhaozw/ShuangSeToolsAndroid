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

        // ��ʼ����ǰ��ʾ�ĵ�3��view
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


  //���õ�3��View�е�����
  private void initlizeThirdView(View view) {
      //�������� ��
      resultCodeViewFuShi = (TextView) view.findViewById(R.id.result_code_fushi);
      //�ں�ѡ��
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
      
      //����
      inputRedBtnFuShi = (Button)view.findViewById(R.id.input_fushi_red);
      inputRedBtnFuShi.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //��ʼ��һ���Զ����Dialog
          Dialog dialog = new SelectRedDialog(activity, R.style.SelectRedDialog, FuShiSearchFragment.this);
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      });
      //����
      inputBlueBtnFuShi = (Button)view.findViewById(R.id.input_fushi_blue);
      inputBlueBtnFuShi.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        //��ʼ��һ���Զ����Dialog
          Dialog dialog = new SelectBlueDialog(activity, R.style.SelectBlueDialog, FuShiSearchFragment.this);
          dialog.show();
          dialog.setCanceledOnTouchOutside(false);
        }
      });
      
      //�н����
      result_titleFuShi = (TextView) view.findViewById(R.id.result_title_fushi);
      
      //��ѯ��ť
      Button single_search_btn = (Button)view.findViewById(R.id.search_fushi_btn);
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
    
    
    @Override
    public void sendSelectedBlueData(ArrayList<Integer> blueList) {
        if (blueList.size() < 1) {
            InfoMessageBox("����", "ѡ�������������ԣ�����1������");
            return;
        }

        fuShiSelblueNum = new int[blueList.size()];
        StringBuffer sb = new StringBuffer();
        sb.append("����");
        for (int i = 0; i < blueList.size(); i++) {
            fuShiSelblueNum[i] = blueList.get(i);
            if (fuShiSelblueNum[i] < 10) {
                sb.append("0");
            }
            sb.append(fuShiSelblueNum[i]).append(" ");
        }

         blue_title_fushi.setText(sb.toString());
         result_titleFuShi.setText("����·�<��ѯ��ť>����");
         
//         }else if(this.currentInWhichPagerFlag == 3 ||
//         this.currentInWhichPagerFlag == 4) {//ѡ�쵨
//         blue_title_dantuo.setText(sb.toString());
//         result_titledantuo.setText("����·�<��ѯ��ť>����");
//         }
    }

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {
        if (redList.size() < 6) {
            InfoMessageBox("����", "ѡ��ĺ���������ԣ�����6������");
            return;
        }
        fuShiSelRedNum = new int[redList.size()];
        StringBuffer sb = new StringBuffer();
        sb.append("�죺");

        for (int i = 0; i < redList.size(); i++) {
            fuShiSelRedNum[i] = redList.get(i);
            if (fuShiSelRedNum[i] < 10) {
                sb.append("0");
            }
            sb.append(fuShiSelRedNum[i]).append(" ");
        }

        red_title_fushi.setText(sb.toString());
        result_titleFuShi.setText("����·�<��ѯ��ť>����");
//        } else if (this.currentInWhichPagerFlag == 3) {// ѡ��
//            if (redList.size() < 1 || redList.size() > 5) {
//                InfoMessageBox("����", "ѡ��ĺ�����������ԣ�ѡ��1-5�������룡");
//                return;
//            }
//            redDanNum = new int[redList.size()];
//            StringBuffer sb = new StringBuffer();
//            sb.append("�쵨��");
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
//            result_titledantuo.setText("����·�<��ѯ��ť>����");
//        } else if (this.currentInWhichPagerFlag == 4) {// ѡ����
//            if (redList.size() < 1 || redList.size() > 20) {
//                InfoMessageBox("����", "ѡ��ĺ�������������ԣ�ѡ��1-20���������룡");
//                return;
//            }
//            redTuoNum = new int[redList.size()];
//            StringBuffer sb = new StringBuffer();
//            sb.append("���ϣ�");
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
//            result_titledantuo.setText("����·�<��ѯ��ť>����");
//        }

    }

}
