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

public class DanTuoSearchFragment extends BaseFragment implements
        SelectRedDialogCallback, SelectBlueDialogCallback {

    private final static String TAG = "DanTuoSearchFragment";
    private List<String> itemIDs;
    private List<ShuangseCodeItem> allHisData;
    private ShuangSeToolsSetApplication appContext;
    private ArrayAdapter<String> allitemIDAdaptor;
    private Activity activity;

    // ����
    private final static int DANCODE = 1;
    private final static int TUOCODE = 2;
    private int currentInWhichPagerFlag = -1;

    private int dantuoSelItemId;
    private ShuangseCodeItem currentSelItemdantuo;
    private TextView resultCodeViewdantuo;
    private Button inputRedBtndantuo;
    private Button inputRed2Btndantuo;
    private Button inputBlueBtndantuo;
    private TextView result_titledantuo;
    // ��ʾѡ��ĺ���
    private TextView red_title_dantuo;
    private TextView red2_title_dantuo;
    private TextView blue_title_dantuo;
    private int[] danTuoTotalRedNumbers = null;
    private int[] redDanNum = null;
    private int[] redTuoNum = null;
    private int[] danTuoSelblueNum = null;

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
        return inflater.inflate(R.layout.dantuosearch, container, false);
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
            allitemIDAdaptor
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        // ��ʼ����ǰ��ʾ�ĵ�3��view
        initlizeFourthView(view);

    }

    private void initlizeFourthView(View view) {
        // �������� ��
        resultCodeViewdantuo = (TextView) view
                .findViewById(R.id.result_code_dantuo);
        // �ں�ѡ��
        Spinner spinner_itemid_dantuo = (Spinner) view
                .findViewById(R.id.spinner_itemid_dantuo);
        spinner_itemid_dantuo.setAdapter(allitemIDAdaptor);
        spinner_itemid_dantuo
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int pos, long id) {
                        Log.i(TAG, "OnItemSelectedListener_dantuo : "
                                + parent.getItemAtPosition(pos).toString());
                        dantuoSelItemId = Integer.parseInt(parent
                                .getItemAtPosition(pos).toString());
                        currentSelItemdantuo = appContext
                                .getCodeItemByID(dantuoSelItemId);
                        resultCodeViewdantuo.setText(currentSelItemdantuo
                                .toCNString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        Log.i(TAG,
                                "spinner_itemid_dantuo:: onNothingSelected()");
                    }
                });

        /* ���� �� �� ���� */
        inputRedBtndantuo = (Button) view.findViewById(R.id.input_dantuo_red);
        inputRedBtndantuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* ��ʼ��һ���Զ����Dialog */
                Dialog dialog = new SelectRedDialog(activity,
                        R.style.SelectRedDialog, DanTuoSearchFragment.this);
                currentInWhichPagerFlag = DANCODE;
                dialog.show();
            }
        });

        inputRed2Btndantuo = (Button) view.findViewById(R.id.input_dantuo_red2);
        inputRed2Btndantuo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* ��ʼ��һ���Զ����Dialog */
                Dialog dialog = new SelectRedDialog(activity,
                        R.style.SelectRedDialog, DanTuoSearchFragment.this);
                currentInWhichPagerFlag = TUOCODE;
                dialog.show();
            }
        });
        /* ���� */
        inputBlueBtndantuo = (Button) view.findViewById(R.id.input_dantuo_blue);
        inputBlueBtndantuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* ��ʼ��һ���Զ����Dialog */
                Dialog dialog = new SelectBlueDialog(activity,
                        R.style.SelectBlueDialog, DanTuoSearchFragment.this);
                dialog.show();
            }
        });

        /* �н���� */
        result_titledantuo = (TextView) view
                .findViewById(R.id.result_title_dantuo);

        red_title_dantuo = (TextView) view.findViewById(R.id.red_title_dantuo);
        red2_title_dantuo = (TextView) view
                .findViewById(R.id.red2_title_dantuo);
        blue_title_dantuo = (TextView) view
                .findViewById(R.id.blue_title_dantuo);

        /* ��ѯ��ť */
        Button single_search_btn = (Button) view
                .findViewById(R.id.search_dantuo_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redDanNum == null) {
                    InfoMessageBox("����", "��ѡ������룬��������1-5����");
                    return;
                }
                if (redTuoNum == null) {
                    InfoMessageBox("����", "��ѡ��������룬��������1-20����");
                    return;
                }

                if (redDanNum.length > 5 || redDanNum.length < 1) {
                    InfoMessageBox("����", "������ֻ����1-5����");
                    return;
                }

                if (redTuoNum.length > 20 || redTuoNum.length < 1) {
                    InfoMessageBox("����", "��������ֻ����1-20����");
                    return;
                }

                /* ���Ϻϲ� */
                danTuoTotalRedNumbers = MagicTool.join(redDanNum, redTuoNum);
                if (danTuoTotalRedNumbers.length < 6) {
                    InfoMessageBox("����", "����ĵ�����������һ���������6������������ѡ��");
                    return;
                }

                int[] blueNum = danTuoSelblueNum;
                if (blueNum.length < 1 || blueNum.length > 16) {
                    InfoMessageBox("����", "ѡ�������������ԣ�");
                    return;
                }

                result_titledantuo.setText("");

                /* redNum[] red2Num[] blueNum[] */
                SparseIntArray resultMap = new SparseIntArray();
                long totalItemCnt = MagicTool.combine(danTuoTotalRedNumbers,
                        danTuoTotalRedNumbers.length, redDanNum,
                        redDanNum.length, 6, currentSelItemdantuo, blueNum,
                        blueNum.length, resultMap);

                StringBuffer dispTextBuf = new StringBuffer();
                dispTextBuf.append("�õ���ʽ������").append(totalItemCnt)
                        .append("ע����,");
                for (int metIndex = 0; metIndex < resultMap.size(); metIndex++) {
                    int hitIndex = resultMap.keyAt(metIndex);
                    int hitCount = resultMap.valueAt(metIndex);

                    if (hitCount > 0) {
                        dispTextBuf.append(getDispText(hitIndex, hitCount))
                                .append("\n");
                    }
                }

                result_titledantuo.setText(dispTextBuf.toString());
            }

        });
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
            return "��" + hitCount + "ע6��1��(һ�Ƚ�), ����ÿע500��Ԫ�����;";
        case 2:
            return "��" + hitCount + "ע6��0��(���Ƚ�), ����ÿע����Ԫ�����;";
        case 3:
            return "��" + hitCount + "ע5��1��(���Ƚ�), ����ÿע3000Ԫ;";
        case 4:
            return "��" + hitCount + "ע4��1��(�ĵȽ�), ����ÿע200Ԫ;";
        case 5:
            return "��" + hitCount + "ע5��0��(�ĵȽ�), ����ÿע200Ԫ;";
        case 6:
            return "��" + hitCount + "ע3��1��(��Ƚ�), ����ÿע10Ԫ;";
        case 7:
            return "��" + hitCount + "ע4��0��(��Ƚ�), ����ÿע10Ԫ;";
        case 8:
            return "��" + hitCount + "ע2��1��(���Ƚ�), ����ÿע5Ԫ;";
        case 9:
            return "��" + hitCount + "ע1��1��(���Ƚ�), ����ÿע5Ԫ;";
        case 10:
            return "��" + hitCount + "ע0��1��(���Ƚ�), ����ÿע5Ԫ;";
        case 11:
            return "��" + hitCount + "ע3��0��(�޽�);";
        case 12:
            return "��" + hitCount + "ע2��0��(�޽�);";
        case 13:
            return "��" + hitCount + "ע1��0��(�޽�);";
        case 14:
            return "��" + hitCount + "ע0��0��(�޽�);";
        case -1:
        default:
            return "δ�н�������0Ԫ.";
        }
    }

    @Override
    public void sendSelectedBlueData(ArrayList<Integer> blueList) {
        if (blueList.size() < 1) {
            InfoMessageBox("����", "ѡ�������������ԣ�����1������");
            return;
        }

        danTuoSelblueNum = new int[blueList.size()];
        StringBuffer sb = new StringBuffer();
        sb.append("����");
        for (int i = 0; i < blueList.size(); i++) {
            danTuoSelblueNum[i] = blueList.get(i);
            if (danTuoSelblueNum[i] < 10) {
                sb.append("0");
            }
            sb.append(danTuoSelblueNum[i]).append(" ");
        }

        blue_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("����·�<��ѯ��ť>����");
    }

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {

        if (this.currentInWhichPagerFlag == DANCODE) {// ѡ��
            if (redList.size() < 1 || redList.size() > 5) {
                InfoMessageBox("����", "ѡ��ĺ�����������ԣ�ѡ��1-5�������룡");
                return;
            }
            redDanNum = new int[redList.size()];
            StringBuffer sb = new StringBuffer();
            sb.append("�쵨��");

            for (int i = 0; i < redList.size(); i++) {
                redDanNum[i] = redList.get(i);
                if (redDanNum[i] < 10) {
                    sb.append("0");
                }
                sb.append(redDanNum[i]).append(" ");
            }

            red_title_dantuo.setText(sb.toString());
            result_titledantuo.setText("����·�<��ѯ��ť>����");
        } else if (this.currentInWhichPagerFlag == TUOCODE) {// ѡ����
            if (redList.size() < 1 || redList.size() > 20) {
                InfoMessageBox("����", "ѡ��ĺ�������������ԣ�ѡ��1-20���������룡");
                return;
            }
            redTuoNum = new int[redList.size()];
            StringBuffer sb = new StringBuffer();
            sb.append("���ϣ�");

            for (int i = 0; i < redList.size(); i++) {
                redTuoNum[i] = redList.get(i);
                if (redTuoNum[i] < 10) {
                    sb.append("0");
                }
                sb.append(redTuoNum[i]).append(" ");
            }

            red2_title_dantuo.setText(sb.toString());
            result_titledantuo.setText("����·�<��ѯ��ť>����");
        }

    }

}
