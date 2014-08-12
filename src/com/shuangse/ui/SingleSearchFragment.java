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

    // 单式
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
        // 设置第1个View中的内容
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

        // 篮球选项数据初始化
        blueValAdapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.blue_value_list, android.R.layout.simple_spinner_item);
        blueValAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 初始化当前显示的第2个view
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
        // 单注红号码
        inputRedBtn = (Button) view.findViewById(R.id.input_single_red);
        inputRedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 初始化一个自定义的Dialog
                Dialog dialog = new SelectRedDialog(activity,
                        R.style.SelectRedDialog, SingleSearchFragment.this);
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
            }
        });

        red_title = (TextView) view.findViewById(R.id.red_title);
        // 开奖号码 域
        resultCodeView = (TextView) view.findViewById(R.id.result_code);
        // 选择哪一期
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
        // 蓝球号码下拉框
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

        // 中奖结果 - 默认初始值
        result_title = (TextView) view.findViewById(R.id.result_title);

        // 查询按钮
        Button single_search_btn = (Button) view
                .findViewById(R.id.search_single_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (inputItem == null) {
                    InfoMessageBox("错误", "请点击选择你购买的单式红球号码！");
                    return;
                }
                Log.i(TAG, "itemID:" + singleSelItemId + " blue:"
                        + singleSelBlue);
                result_title.setText("");

                int hitResult = MagicTool.getHitResult(currentSelItem,
                        inputItem);
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

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {
        
            if (redList.size() != 6) {
                InfoMessageBox("错误", "选择的红球个数不对，单式是6个红球！");
                return;
            }

            int[] redNum = new int[6];
            for (int i = 0; i < redList.size(); i++) {
                redNum[i] = redList.get(i);
                if (redNum[i] < 1 || redNum[i] > 33) {
                    InfoMessageBox("错误", "选择的红球不对，红球范围在1-33！");
                    return;
                }
            }

            inputItem = new ShuangseCodeItem(singleSelItemId, redNum,
                    singleSelBlue);
            StringBuffer sb = new StringBuffer();
            sb.append("红：").append(inputItem.toRedString());
            red_title.setText(sb.toString());
            result_title.setText("点击下方<查询按钮>查结果");
    }

}
