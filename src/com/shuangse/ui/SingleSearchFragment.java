package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

public class SingleSearchFragment extends BaseFragment implements SelectRedDialogCallback {
    private final static String TAG = "SingleSearchFragment";
    private List<String> itemIDs;
    private List<ShuangseCodeItem> allHisData;
    private ShuangSeToolsSetApplication appContext;
    private ArrayAdapter<String> allitemIDAdaptor;
    private ArrayAdapter<CharSequence> blueValAdapter;
    private Activity activity;

    // ��ʽ
    private int singleSelItemId;
    private ShuangseCodeItem currentSelItem;
    private int singleSelBlue;
    private ShuangseCodeItem inputItem;
    private Button inputRedBtn;
    private TextView resultCodeView;
    private TextView result_title;
    private TextView red_title;

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
        return inflater.inflate(R.layout.singlesearch, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // ���õ�1��View�е�����
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

        // ����ѡ�����ݳ�ʼ��
        blueValAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.blue_value_list, android.R.layout.simple_spinner_item);
        blueValAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // ��ʼ����ǰ��ʾ�ĵ�2��view
        initlizeFirstView(view);

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

    private void initlizeFirstView(View view) {
        // ��ע�����
        inputRedBtn = (Button) view.findViewById(R.id.input_single_red);
        inputRedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ��ʼ��һ���Զ����Dialog
                Dialog dialog = new SelectRedDialog(activity,
                        R.style.SelectRedDialog, SingleSearchFragment.this);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
            }
        });

        red_title = (TextView) view.findViewById(R.id.red_title);
        // �������� ��
        resultCodeView = (TextView) view.findViewById(R.id.result_code);
        // ѡ����һ��
        Spinner spinner_itemid_single = (Spinner) view
                .findViewById(R.id.spinner_itemid);
        spinner_itemid_single.setAdapter(allitemIDAdaptor);
        spinner_itemid_single
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int pos, long id) {
                        Log.i(TAG, "OnItemSelectedListener : "
                                + parent.getItemAtPosition(pos).toString());
                        singleSelItemId = Integer.parseInt(parent
                                .getItemAtPosition(pos).toString());
                        currentSelItem = appContext
                                .getCodeItemByID(singleSelItemId);
                        resultCodeView.setText(currentSelItem.toCNString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        Log.i(TAG,
                                "spinner_itemid_single:: onNothingSelected()");
                    }

                });
        // �������������
        Spinner spinner_blue_val_single = (Spinner) view
                .findViewById(R.id.spinner_blue);
        spinner_blue_val_single.setAdapter(blueValAdapter);
        spinner_blue_val_single
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int pos, long id) {
                        Log.i(TAG, "OnItemSelectedListener : "
                                + parent.getItemAtPosition(pos).toString());
                        singleSelBlue = Integer.parseInt(parent
                                .getItemAtPosition(pos).toString());
                        if (inputItem != null) {
                            inputItem.blue = singleSelBlue;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        Log.i(TAG,
                                "spinner_itemid_single:: onNothingSelected()");
                    }

                });

        // �н���� - Ĭ�ϳ�ʼֵ
        result_title = (TextView) view.findViewById(R.id.result_title);

        // ��ѯ��ť
        Button single_search_btn = (Button) view
                .findViewById(R.id.search_single_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (inputItem == null) {
                    InfoMessageBox("����", "����ѡ���㹺��ĵ�ʽ������룡");
                    return;
                }
                Log.i(TAG, "itemID:" + singleSelItemId + " blue:"
                        + singleSelBlue);
                result_title.setText("");

                int hitResult = MagicTool.getHitResult(currentSelItem,
                        inputItem);
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

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {
        
            if (redList.size() != 6) {
                InfoMessageBox("����", "ѡ��ĺ���������ԣ���ʽ��6������");
                return;
            }

            int[] redNum = new int[6];
            for (int i = 0; i < redList.size(); i++) {
                redNum[i] = redList.get(i);
                if (redNum[i] < 1 || redNum[i] > 33) {
                    InfoMessageBox("����", "ѡ��ĺ��򲻶ԣ�����Χ��1-33��");
                    return;
                }
            }

            inputItem = new ShuangseCodeItem(singleSelItemId, redNum,
                    singleSelBlue);
            StringBuffer sb = new StringBuffer();
            sb.append("�죺").append(inputItem.toRedString());
            red_title.setText(sb.toString());
            result_title.setText("����·�<��ѯ��ť>����");
    }

}
