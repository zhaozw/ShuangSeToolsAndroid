package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ExtShuangseCodeItem;
import com.shuangse.util.MagicTool;

public class HisDataListFragment extends ListFragment {
    private static final String TAG = "HisDataListFragment";
    protected boolean isInit; // 是否可以开始加载数据
    protected ShuangSeToolsSetApplication mContext;
    protected Activity activity;
    
    private final static int itemsPerPage = 20;
    private ProgressDialog progDialog;
    
    private ArrayList<ExtShuangseCodeItem> hisDataListItems = new ArrayList<ExtShuangseCodeItem>();
    private ListViewAdapter adapter = new ListViewAdapter();
    private ListView hisDataListView;
    private View footerView;
    private ProgressBar footerProgressBar;
    private TextView footerTextView;
    private boolean loadingMore = false;
    private boolean noMoreData = false;
    private Thread mThread = null;
    //page number start from 1
    private int currentPageNumber = 1;
    private final static int NEWITEMSLOADEDMSG = 1;
    private final static int STARTDOWNLOADEDMSG = 2;
    private final static int NODATAMSG = 3;
    private final static int QUERYERRMSG = 4;
    
    private LayoutInflater mInflater;

    private Handler msgHandler = new MHandler(this);
    static class MHandler extends Handler {
      WeakReference<HisDataListFragment> mActivity;

      MHandler(HisDataListFragment mAct) {
        this.mActivity = new WeakReference<HisDataListFragment>(mAct);
      }

      @Override
      public void handleMessage(Message msg) {
        HisDataListFragment theActivity = mActivity.get();
        theActivity.hideProgressBox();
        switch (msg.what) {
        //列表下载数据返回的消息
        case STARTDOWNLOADEDMSG:
            theActivity.showProgressDialog("信息", "请稍等，正在下载开奖历史数据...");
            break;
        case QUERYERRMSG:
            theActivity.mThread = null;
            //theActivity.InfoMessageBox("信息", "查询开奖数据出错，请检查您的网络并稍候重试.");
            theActivity.updateFooterViewToDownloadErr();
            break;
        case NODATAMSG:
            theActivity.mThread = null;
            theActivity.updateFooterViewToNoMoredata();
            break;
        case NEWITEMSLOADEDMSG:
            theActivity.mThread = null;
            theActivity.refleshAdaptorData();
            break;

        default:
          break;
        }
        super.handleMessage(msg);
      }
    };
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
        mContext = (ShuangSeToolsSetApplication)this.getActivity().getApplication();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        isInit = true;
        mInflater = inflater;
        return inflater.inflate(R.layout.hisdatalistview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        hisDataListView = getListView();
        
        TextView emptyView = (TextView) view.findViewById(android.R.id.empty);
        hisDataListView.setEmptyView(emptyView);
        //add the footer before adding the adapter, otherwise the footer will not load!
        footerView = mInflater.inflate(R.layout.listfooter, null, false);
        hisDataListView.addFooterView(footerView);
        footerProgressBar = (ProgressBar) footerView.findViewById(R.id.progressBar1);
        footerTextView = (TextView) footerView.findViewById(R.id.inprogressText);

        //要在footer 和 Empty 之后设置adapter
        setListAdapter(adapter);

        hisDataListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                        Log.d(TAG, "ItemClicked");
                        ExtShuangseCodeItem itm = hisDataListItems.get(arg2);
                        Intent intent = new Intent(HisDataListFragment.this.getActivity(), DispItemOpenDataDetailsActivity.class);
                        intent.putExtra("ItemId", itm.id);
                        intent.putExtra("RED1", itm.red[0]);
                        intent.putExtra("RED2", itm.red[1]);
                        intent.putExtra("RED3", itm.red[2]);
                        intent.putExtra("RED4", itm.red[3]);
                        intent.putExtra("RED5", itm.red[4]);
                        intent.putExtra("RED6", itm.red[5]);
                        intent.putExtra("BLUE", itm.blue);
                        intent.putExtra("OPENDATE", itm.openDate);
                        
                        startActivity(intent);
            }
            
        });
        
        hisDataListView.setOnScrollListener(new OnScrollListener() {
            /**
             * onScroll在每次ListView被刷新时都会被调用
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              //what is the bottom item that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;
//                Log.i(TAG, "lastInScreen:" + lastInScreen 
//                      + " firstVisibleItem:" + firstVisibleItem 
//                      + " visibleItemCount:" + visibleItemCount
//                      + " totalItemCount:" + totalItemCount
//                      + " loadingMore:" + loadingMore
//                      + " noMoreData:" + noMoreData);
                
                //is the bottom item visible & not loading more already ? Load more !
                if((lastInScreen == totalItemCount) && !(loadingMore) && !(noMoreData)) {
                    if(mThread == null || (!mThread.isAlive())) {
                       mThread =  new LoadHisDataListItemsThread();
                       Log.i("onScroll", "LoadListItemsThread started");
                       mThread.start();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
            }
            
        });
        
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
    
    protected void showData() {
        if(mThread == null || (!mThread.isAlive())) {
            mThread =  new LoadHisDataListItemsThread();
            Log.i("onScroll", "LoadListItemsThread started");
            mThread.start();
        }
    }

    @SuppressWarnings("unused")
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
    
    static class HisDataListViewHolder {
        View itemView;
        ExtShuangseCodeItem item;
    }
    
    class ListViewAdapter extends BaseAdapter {
        public ListViewAdapter() {
        }
        
        @Override
        public ExtShuangseCodeItem getItem(int position) {
          if (!hisDataListItems.isEmpty()) {
                Log.v(TAG, "getItem");
                return hisDataListItems.get(position);
          }
          return null;
        }

        @Override
        public long getItemId(int position) {
          if (!hisDataListItems.isEmpty()) {
              Log.v(TAG, "getItemId");
              return hisDataListItems.get(position).id;
          }
          return position;
        }
        
        public int getCount() {
          if(hisDataListItems == null) return 0;
          return hisDataListItems.size();
        }
        
        public View getView(final int position, View convertView, ViewGroup parent) {
          HisDataListViewHolder holder;
          ExtShuangseCodeItem item = getItem(position);
          if(item == null) return null;
          Log.v(TAG, "getView");
                 
          if (convertView == null) {
            holder = new HisDataListViewHolder();
            
            convertView = mInflater.inflate(R.layout.hisdatalistviewitem, null);
            holder.itemView = convertView;
            convertView.setTag(holder);
          } else {
            holder = (HisDataListViewHolder) convertView.getTag();
          }
          
          holder.item = item;
          TextView titleTextView = (TextView) holder.itemView.findViewById(R.id.ItemID);
          titleTextView.setText("第"+String.valueOf(item.id)+"期");
          TextView dateTextView = (TextView) holder.itemView.findViewById(R.id.ItemDate);
          dateTextView.setText("开奖日期:" + item.openDate);
          ImageButton red1 = (ImageButton) holder.itemView.findViewById(R.id.blankrbtn1);
          ImageButton red2 = (ImageButton) holder.itemView.findViewById(R.id.blankrbtn2);
          ImageButton red3 = (ImageButton) holder.itemView.findViewById(R.id.blankrbtn3);
          ImageButton red4 = (ImageButton) holder.itemView.findViewById(R.id.blankrbtn4);
          ImageButton red5 = (ImageButton) holder.itemView.findViewById(R.id.blankrbtn5);
          ImageButton red6 = (ImageButton) holder.itemView.findViewById(R.id.blankrbtn6);
          ImageButton blue = (ImageButton) holder.itemView.findViewById(R.id.blankbluebtn);
          red1.setImageResource(MagicTool.getResIDbyRednum(item.red[0]));
          red2.setImageResource(MagicTool.getResIDbyRednum(item.red[1]));
          red3.setImageResource(MagicTool.getResIDbyRednum(item.red[2]));
          red4.setImageResource(MagicTool.getResIDbyRednum(item.red[3]));
          red5.setImageResource(MagicTool.getResIDbyRednum(item.red[4]));
          red6.setImageResource(MagicTool.getResIDbyRednum(item.red[5]));
          blue.setImageResource(MagicTool.getResIDbyBluenum(item.blue));
          return convertView;
        }
    };
    
    
    //Runnable to load the items 
    class LoadHisDataListItemsThread extends Thread {
        @Override
        public void run() {
          try{
            //Set flag so we can not load new items 2 at the same time
            loadingMore = true;
            
            Message msg0 = new Message();
            msg0.what = STARTDOWNLOADEDMSG;
            msgHandler.sendMessage(msg0);
            
            //Reset the array that holds the new items
            //expListItems = new ArrayList<ExperienceItem>();
            ArrayList<ExtShuangseCodeItem> pageHisData = mContext.queryExtHisDataFromLocalDB(itemsPerPage, currentPageNumber);
            
            if(pageHisData != null) {
              for(int i=0;i<pageHisData.size(); i++){
                ExtShuangseCodeItem record = pageHisData.get(i);
                hisDataListItems.add(record);
              }
              currentPageNumber++;
              
              Message msg = new Message();
              msg.what = NEWITEMSLOADEDMSG;
              msgHandler.sendMessage(msg);
            } else {
              //No Content
              Message msg = new Message();
              msg.what = NODATAMSG;
              msgHandler.sendMessage(msg);
            }
          }catch (Exception e) {
            Log.e(TAG, "Query Server gets exception.");
            e.printStackTrace();
            Message msg = new Message();
            msg.what = QUERYERRMSG;
            msgHandler.sendMessage(msg);
          }
        }
    };
    private void updateFooterViewToDownloadErr() {
        Log.v(TAG, "updateFooterViewToDownloadErr entered");
        footerProgressBar.setVisibility(View.GONE);
        footerTextView.setText("下载数据出错，请稍后重试.");
        this.loadingMore = false;
    }
      
    private void updateFooterViewToNoMoredata() {
        //footerProgressBar.setVisibility(View.GONE);
        //footerTextView.setText("没有更多数据...");
        hisDataListView.removeFooterView(this.footerView);
        this.loadingMore = false;
        this.noMoreData = true;
        Log.v(TAG, "updateFooterViewToNoMoredata entered");
    }
    
    private void refleshAdaptorData() {
        Log.v(TAG, "refleshAdaptorData entered, expListItems.size:" + hisDataListItems.size());
        // Tell to the adapter that changes have been made, this will cause the list
        // to refresh
        adapter.notifyDataSetChanged();
        // Done loading more.
        loadingMore = false;
        Log.v(TAG, "refleshAdaptorData exit");
   }
    
    private void hideProgressBox() {
        if (progDialog != null) {
            progDialog.dismiss();
            progDialog = null;
        }
    }
    
    private void showProgressDialog(String title, String msg) {
        if(activity != null) {
            progDialog = new ProgressDialog(activity);
            progDialog.setTitle(title);
            progDialog.setMessage(msg);
            progDialog.setCancelable(true);
            progDialog.show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port
        }
    }
    
    
    
}
