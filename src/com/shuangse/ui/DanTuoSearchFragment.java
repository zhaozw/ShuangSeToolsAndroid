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

    // 胆拖
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
    // 显示选择的号码
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

        // 初始化当前显示的第3个view
        initlizeFourthView(view);

    }

    private void initlizeFourthView(View view) {
        // 开奖号码 域
        resultCodeViewdantuo = (TextView) view
                .findViewById(R.id.result_code_dantuo);
        // 期号选择
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

        /* 红球 胆 和 托码 */
        inputRedBtndantuo = (Button) view.findViewById(R.id.input_dantuo_red);
        inputRedBtndantuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 初始化一个自定义的Dialog */
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
                /* 初始化一个自定义的Dialog */
                Dialog dialog = new SelectRedDialog(activity,
                        R.style.SelectRedDialog, DanTuoSearchFragment.this);
                currentInWhichPagerFlag = TUOCODE;
                dialog.show();
            }
        });
        /* 篮球 */
        inputBlueBtndantuo = (Button) view.findViewById(R.id.input_dantuo_blue);
        inputBlueBtndantuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 初始化一个自定义的Dialog */
                Dialog dialog = new SelectBlueDialog(activity,
                        R.style.SelectBlueDialog, DanTuoSearchFragment.this);
                dialog.show();
            }
        });

        /* 中奖结果 */
        result_titledantuo = (TextView) view
                .findViewById(R.id.result_title_dantuo);

        red_title_dantuo = (TextView) view.findViewById(R.id.red_title_dantuo);
        red2_title_dantuo = (TextView) view
                .findViewById(R.id.red2_title_dantuo);
        blue_title_dantuo = (TextView) view
                .findViewById(R.id.blue_title_dantuo);

        /* 查询按钮 */
        Button single_search_btn = (Button) view
                .findViewById(R.id.search_dantuo_btn);
        single_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redDanNum == null) {
                    InfoMessageBox("错误", "请选择红球胆码，胆码允许1-5个！");
                    return;
                }
                if (redTuoNum == null) {
                    InfoMessageBox("错误", "请选择红球拖码，拖码允许1-20个！");
                    return;
                }

                if (redDanNum.length > 5 || redDanNum.length < 1) {
                    InfoMessageBox("错误", "红球胆码只允许1-5个！");
                    return;
                }

                if (redTuoNum.length > 20 || redTuoNum.length < 1) {
                    InfoMessageBox("错误", "红球拖码只允许1-20个！");
                    return;
                }

                /* 胆拖合并 */
                danTuoTotalRedNumbers = MagicTool.join(redDanNum, redTuoNum);
                if (danTuoTotalRedNumbers.length < 6) {
                    InfoMessageBox("错误", "输入的胆码和拖码组合一起必须最少6个红球，请重新选择！");
                    return;
                }

                int[] blueNum = danTuoSelblueNum;
                if (blueNum.length < 1 || blueNum.length > 16) {
                    InfoMessageBox("错误", "选择的蓝球个数不对！");
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
                dispTextBuf.append("该胆拖式共包含").append(totalItemCnt)
                        .append("注号码,");
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
            return "中" + hitCount + "注6红1蓝(一等奖), 奖金每注500万元或更多;";
        case 2:
            return "中" + hitCount + "注6红0蓝(二等奖), 奖金每注数万元或更多;";
        case 3:
            return "中" + hitCount + "注5红1蓝(三等奖), 奖金每注3000元;";
        case 4:
            return "中" + hitCount + "注4红1蓝(四等奖), 奖金每注200元;";
        case 5:
            return "中" + hitCount + "注5红0蓝(四等奖), 奖金每注200元;";
        case 6:
            return "中" + hitCount + "注3红1蓝(五等奖), 奖金每注10元;";
        case 7:
            return "中" + hitCount + "注4红0蓝(五等奖), 奖金每注10元;";
        case 8:
            return "中" + hitCount + "注2红1蓝(六等奖), 奖金每注5元;";
        case 9:
            return "中" + hitCount + "注1红1蓝(六等奖), 奖金每注5元;";
        case 10:
            return "中" + hitCount + "注0红1蓝(六等奖), 奖金每注5元;";
        case 11:
            return "中" + hitCount + "注3红0蓝(无奖);";
        case 12:
            return "中" + hitCount + "注2红0蓝(无奖);";
        case 13:
            return "中" + hitCount + "注1红0蓝(无奖);";
        case 14:
            return "中" + hitCount + "注0红0蓝(无奖);";
        case -1:
        default:
            return "未中奖；奖金0元.";
        }
    }

    @Override
    public void sendSelectedBlueData(ArrayList<Integer> blueList) {
        if (blueList.size() < 1) {
            InfoMessageBox("错误", "选择的蓝球个数不对，至少1个篮球！");
            return;
        }

        danTuoSelblueNum = new int[blueList.size()];
        StringBuffer sb = new StringBuffer();
        sb.append("蓝：");
        for (int i = 0; i < blueList.size(); i++) {
            danTuoSelblueNum[i] = blueList.get(i);
            if (danTuoSelblueNum[i] < 10) {
                sb.append("0");
            }
            sb.append(danTuoSelblueNum[i]).append(" ");
        }

        blue_title_dantuo.setText(sb.toString());
        result_titledantuo.setText("点击下方<查询按钮>查结果");
    }

    @Override
    public void sendSelectedRedData(ArrayList<Integer> redList) {

        if (this.currentInWhichPagerFlag == DANCODE) {// 选胆
            if (redList.size() < 1 || redList.size() > 5) {
                InfoMessageBox("错误", "选择的红球胆码个数不对，选择1-5个红球胆码！");
                return;
            }
            redDanNum = new int[redList.size()];
            StringBuffer sb = new StringBuffer();
            sb.append("红胆：");

            for (int i = 0; i < redList.size(); i++) {
                redDanNum[i] = redList.get(i);
                if (redDanNum[i] < 10) {
                    sb.append("0");
                }
                sb.append(redDanNum[i]).append(" ");
            }

            red_title_dantuo.setText(sb.toString());
            result_titledantuo.setText("点击下方<查询按钮>查结果");
        } else if (this.currentInWhichPagerFlag == TUOCODE) {// 选红拖
            if (redList.size() < 1 || redList.size() > 20) {
                InfoMessageBox("错误", "选择的红球拖码个数不对，选择1-20个红球拖码！");
                return;
            }
            redTuoNum = new int[redList.size()];
            StringBuffer sb = new StringBuffer();
            sb.append("红拖：");

            for (int i = 0; i < redList.size(); i++) {
                redTuoNum[i] = redList.get(i);
                if (redTuoNum[i] < 10) {
                    sb.append("0");
                }
                sb.append(redTuoNum[i]).append(" ");
            }

            red2_title_dantuo.setText(sb.toString());
            result_titledantuo.setText("点击下方<查询按钮>查结果");
        }

    }

}
