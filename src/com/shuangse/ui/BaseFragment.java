package com.shuangse.ui;

import com.shuangse.base.ShuangSeToolsSetApplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {
    protected boolean isInit; // 是否可以开始加载数据
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
        /* 初始化控件 */
    }

    @Override
    public void onResume() {
        super.onResume();
        // 判断当前fragment是否显示
        if (getUserVisibleHint()) {
            showData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次切换fragment时调用的方法
        if (isVisibleToUser) {
            showData();
        }
    }

    /**
     * 初始化数据
     */
    protected void showData() {
        if (isInit) {
            isInit = false;
            // 加载各种数据
        }
    }
}
