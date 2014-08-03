package com.shuangse.ui;

import com.shuangse.base.ShuangSeToolsSetApplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {
    protected boolean isInit; // �Ƿ���Կ�ʼ��������
    protected ShuangSeToolsSetApplication mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        isInit = true;
        return inflater.inflate(R.layout.shareexplistview, container, false);
    }
    
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = (ShuangSeToolsSetApplication)activity.getApplication();
        //mContext = activity.getApplicationContext();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /* ��ʼ���ؼ� */
    }

    @Override
    public void onResume() {
        super.onResume();
        // �жϵ�ǰfragment�Ƿ���ʾ
        if (getUserVisibleHint()) {
            showData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // ÿ���л�fragmentʱ���õķ���
        if (isVisibleToUser) {
            showData();
        }
    }

    /**
     * ��ʼ������
     */
    protected void showData() {
        if (isInit) {
            isInit = false;
            // ���ظ�������
        }
    }
}
