package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ExperienceItem;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShareExpFragment extends ListFragment {
    private static final String TAG = "ShareExpFragment";
    protected boolean isInit; // 是否可以开始加载数据
    protected ShuangSeToolsSetApplication mContext;
    protected Activity activity;
    
    private final static int itemsPerPage = 10;
    private final static int titleLength = 16;
    private ProgressDialog progDialog;
    
    private ArrayList<ExperienceItem> expListItems = new ArrayList<ExperienceItem>();
    private ListViewAdapter adapter = new ListViewAdapter();
    private ListView expListView;
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
      WeakReference<ShareExpFragment> mActivity;

      MHandler(ShareExpFragment mAct) {
        this.mActivity = new WeakReference<ShareExpFragment>(mAct);
      }

      @Override
      public void handleMessage(Message msg) {
        ShareExpFragment theActivity = mActivity.get();
        theActivity.hideProgressBox();
        switch (msg.what) {
        //列表下载数据返回的消息
        case STARTDOWNLOADEDMSG:
            theActivity.showProgressDialog("信息", "请稍等，正在查询中奖经验列表...");
            break;
        case QUERYERRMSG:
            theActivity.mThread = null;
            //theActivity.InfoMessageBox("信息", "查询中奖经验出错，请检查您的网络并稍候重试.");
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
        return inflater.inflate(R.layout.shareexplistview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        expListView = getListView();
        
        TextView emptyView = (TextView) view.findViewById(android.R.id.empty);
        expListView.setEmptyView(emptyView);
        //add the footer before adding the adapter, otherwise the footer will not load!
        footerView = mInflater.inflate(R.layout.listfooter, null, false);
        expListView.addFooterView(footerView);
        footerProgressBar = (ProgressBar) footerView.findViewById(R.id.progressBar1);
        footerTextView = (TextView) footerView.findViewById(R.id.inprogressText);

        //要在footer 和 Empty 之后设置adapter
        setListAdapter(adapter);

        expListView.setOnScrollListener(new OnScrollListener() {
            /**
             * onScroll在每次ListView被刷新时都会被调用
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              //what is the bottom item that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;
                Log.i(TAG, "lastInScreen:" + lastInScreen 
                      + " firstVisibleItem:" + firstVisibleItem 
                      + " visibleItemCount:" + visibleItemCount
                      + " totalItemCount:" + totalItemCount
                      + " loadingMore:" + loadingMore
                      + " noMoreData:" + noMoreData);
                
                //is the bottom item visible & not loading more already ? Load more !
                if((lastInScreen == totalItemCount) && !(loadingMore) && !(noMoreData)) {
                    if(mThread == null || (!mThread.isAlive())) {
                       mThread =  new LoadListItemsThread();
                       Log.i("onScroll", "LoadListItemsThread started");
                       mThread.start();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
            }
            
        });
        
        Button shareMyExpBtn = (Button) view.findViewById(R.id.shareMyExpBtn);
        shareMyExpBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareExpFragment.this.getActivity(), ShareMyExperienceActivity.class);
                startActivity(intent);
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
            mThread =  new LoadListItemsThread();
            Log.i("onScroll", "LoadListItemsThread started");
            mThread.start();
        }
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
    
    static class ExprShareViewHolder {
        View itemView;
        ExperienceItem item;
    }
    
    class ListViewAdapter extends BaseAdapter {
        public ListViewAdapter() {
        }
        
        @Override
        public ExperienceItem getItem(int position) {
          if (!expListItems.isEmpty()) {
                Log.v(TAG, "getItem");
                return expListItems.get(position);
          }
          return null;
        }

        @Override
        public long getItemId(int position) {
          if (!expListItems.isEmpty()) {
              Log.v(TAG, "getItemId");
              return expListItems.get(position).getId();
          }
          return position;
        }
        
        public int getCount() {
          if(expListItems == null) return 0;
          return expListItems.size();
        }
        
        public View getView(final int position, View convertView, ViewGroup parent) {
          ExprShareViewHolder holder;
          ExperienceItem item = getItem(position);
          if(item == null) return null;
          Log.v(TAG, "getView");
                 
          if (convertView == null) {
            holder = new ExprShareViewHolder();
            
            convertView = mInflater.inflate(R.layout.listviewitem, null);
            holder.itemView = convertView;
            convertView.setTag(holder);
          } else {
            holder = (ExprShareViewHolder) convertView.getTag();
          }
          
          holder.item = item;
          TextView titleTextView = (TextView) holder.itemView.findViewById(R.id.ItemTitleText);
          String titleTxt = item.getTitle();
          if(titleTxt.length() <= titleLength) {
            titleTextView.setText(titleTxt);
          } else {
            titleTextView.setText(titleTxt.substring(0, titleLength) + "...");
          }
          
          convertView.setOnClickListener(new OnClickListener() {  
              @Override  
              public void onClick(View v) { // 加载详细  
                  ExperienceItem itm = expListItems.get(position);
                  Intent intent = new Intent(ShareExpFragment.this.getActivity(), ExperienceActivity.class);
                  intent.putExtra("ItemId", itm.getId());
                  startActivity(intent);
              }  
          });
          
          return convertView;
        }
    };
    
    
    //Runnable to load the items 
    class LoadListItemsThread extends Thread {
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
            
            String reqURL = mContext.getServerAddr() + "GetSharedExperienceAction.do?pageSize=" + itemsPerPage + "&pageNum=" + currentPageNumber;
            Log.v(TAG, reqURL);
            HttpGet httpGet = new HttpGet(reqURL);
            
            HttpResponse response = mContext.getHttpClient().execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
              String responseContent = EntityUtils.toString(response.getEntity(),  HTTP.UTF_8).trim();
              Log.v(TAG, "responseContent: " + responseContent);
              JSONArray jsonArray = new JSONArray(responseContent); 
              
              for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i); //每条记录又由几个Object对象组成     
                int id = item.getInt("id"); // 获取对象对应的值
                String title = item.getString("title");
                                
                ExperienceItem record = new ExperienceItem();
                record.setId(id);
                record.setTitle(title);
                expListItems.add(record);
              }
              currentPageNumber++;
              
              Message msg = new Message();
              msg.what = NEWITEMSLOADEDMSG;
              msgHandler.sendMessage(msg);
              
            } else if(response != null && response.getStatusLine().getStatusCode() == 204) {
              //No Content
              if(response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
              }
              Message msg = new Message();
              msg.what = NODATAMSG;
              msgHandler.sendMessage(msg);
              
            } else {//Various Error
              Log.e(TAG, "Server returns error.");
              if(response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
              }
              Message msg = new Message();
              msg.what = QUERYERRMSG;
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
        expListView.removeFooterView(this.footerView);
        this.loadingMore = false;
        this.noMoreData = true;
        Log.v(TAG, "updateFooterViewToNoMoredata entered");
    }
    
    private void refleshAdaptorData() {
        Log.v(TAG, "refleshAdaptorData entered, expListItems.size:" + expListItems.size());
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
