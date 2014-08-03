package com.shuangse.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shuangse.base.ShuangSeToolsSetApplication;

public class RulesContent4Fragment extends BaseFragment {
    protected Activity activity;
            
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        activity = this.getActivity();
        mContext = (ShuangSeToolsSetApplication)this.getActivity().getApplication();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        isInit = true;
        return inflater.inflate(R.layout.rules_content4, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
    
}
