package com.shuangse.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.ExperienceItem;
import com.shuangse.util.MagicTool;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;

public class ExperienceListActivity extends ListActivity {
   private final static String TAG = "ExperienceListActivity";
   private ProgressDialog progressDialog;
   private ShuangSeToolsSetApplication appContext;
   
   //how many to load on reaching the bottom
   //record numbers on each page
   private  final static int itemsPerPage = 10;
   private final static int titleLength = 16;
   //page number start from 1
   private int currentPageNumber = 1;
   private boolean loadingMore = false;
   private boolean noMoreData = false;
   
   private  ArrayList<ExperienceItem> expListItems;
   private ArrayAdapter<ExperienceItem> adapter;
   private Thread mThread = null;
   private View footerView;
   private ProgressBar footerProgressBar;
   private TextView footerTextView;
   
   private final static int NEWITEMSLOADEDMSG = 1;
   private final static int NODATAMSG = 2;
   private final static int QUERYERRMSG = 3;
   Handler msgHandler = new MHandler(this);
   static class MHandler extends Handler {
     WeakReference<ExperienceListActivity> mActivity;

     MHandler(ExperienceListActivity mAct) {
       mActivity = new WeakReference<ExperienceListActivity>(mAct);
     }

     @Override
     public void handleMessage(Message msg) {
       ExperienceListActivity theActivity = mActivity.get();
       
       theActivity.hideProgressBox();
       
       switch (msg.what) {
       case QUERYERRMSG:
         theActivity.InfoMessageBox("信息", "下载数据出错，请检查您的网络并稍候重试.");
         theActivity.updateFooterViewToDownloadErr();
         break;
         
       case NODATAMSG:
         theActivity.updateFooterViewToNoMoredata();
         break;
         
       case NEWITEMSLOADEDMSG:
         theActivity.refleshAdaptorData();
         break;
         
       default:
         break;
       }
       super.handleMessage(msg);
     }
   };
      
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //更新标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.listplaceholder);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        final TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setText(R.string.custom_title_experience);
        Button returnBtn = (Button)findViewById(R.id.returnbtn);
        returnBtn.setVisibility(View.VISIBLE);
        Button helpBtn = (Button)findViewById(R.id.helpbtn);
        helpBtn.setVisibility(View.VISIBLE);
        helpBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String htmlMsg = "本页列出了经典，靠谱的购买彩票的一些方法、经验和总结，点击对应的行标题，即可阅读详细内容；本页会定期更新，请持续关注。";
            MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, ExperienceListActivity.this).show();
          }
        }); 
        returnBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            onBackPressed();
          }
        });
        
        // 获取全局数据
        appContext = (ShuangSeToolsSetApplication) getApplication();
        
        //This will hold the new items
        expListItems = new ArrayList<ExperienceItem>();
        adapter = new ListViewArrayAdapter(this, R.layout.listviewitem, expListItems);
        
        TextView emptyView = (TextView) findViewById(android.R.id.empty);
        this.getListView().setEmptyView(emptyView);
        
        //add the footer before adding the adapter, else the footer will not load!
        footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listfooter, null, false);
        this.getListView().addFooterView(footerView);
        footerProgressBar = (ProgressBar) footerView.findViewById(R.id.progressBar1);
        footerTextView = (TextView) footerView.findViewById(R.id.inprogressText);
        
        //Set the adapter
        this.setListAdapter(adapter);
        this.getListView().setOnItemClickListener(new OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("onItemClick", "position:" + position + " id:" + id);
            ExperienceItem itm = adapter.getItem(position);
            Log.i("onItemClick", "item.id:" + itm.getId() + " item.title:" + itm.getTitle());
            showProgressDialog("提示", "请稍等...");
            
            Intent intent = new Intent(ExperienceListActivity.this, ExperienceActivity.class);
            intent.putExtra("ItemId", itm.getId());
            startActivity(intent);
          }
        });
        
        //Here is where the magic happens
        this.getListView().setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
                        
            /**
             * onScroll在每次ListView被刷新时都会被调用
             * */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                //what is the bottom item that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;
                Log.i("onScroll", "lastInScreen:" + lastInScreen 
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
        });
       
        this.showProgressDialog("提示", "请稍等，正在努力下载中...");

        //Load the first n items
        mThread =  new LoadListItemsThread();
        mThread.start();
    }
    
    protected void onPause() {
      super.onPause();
      hideProgressBox();
    }
    
    private void showProgressDialog(String title, String msg) {
      progressDialog = new ProgressDialog(ExperienceListActivity.this);
      progressDialog.setTitle(title);
      progressDialog.setMessage(msg);
      progressDialog.setCancelable(true);
      progressDialog.show();
    }

    private void hideProgressBox() {
      if (progressDialog != null) {
        progressDialog.dismiss();
      }
    }
    
    static class ViewHolder {
      View itemView;
      ExperienceItem item;
    }
    
    class ListViewArrayAdapter extends ArrayAdapter<ExperienceItem> {
      private int itemViewResourceId;
      private List<ExperienceItem> listItems;
      
      public ListViewArrayAdapter(Context context, int itemViewResourceId, List<ExperienceItem> listItems) {
        super(context, itemViewResourceId, listItems);
        this.itemViewResourceId = itemViewResourceId;
        this.listItems = listItems;
      }
      
      public int getCount() {  
        if (!listItems.isEmpty()) {  
            return listItems.size();  
        } else {  
            return 0;
        }  
      }
      
      public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ExperienceItem item = getItem(position);
        if(item == null) return null;
               
        if (convertView == null) {
          holder = new ViewHolder();
          
          RelativeLayout itemLayout = new RelativeLayout(getContext());
          convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.itemViewResourceId, itemLayout, true);
          holder.itemView = convertView;
          convertView.setTag(holder);
        } else {
          holder = (ViewHolder) convertView.getTag();
        }
        
        holder.item = item;
        TextView titleTextView = (TextView) holder.itemView.findViewById(R.id.ItemTitleText);
        String titleTxt = item.getTitle();
        if(titleTxt.length() <= titleLength) {
          titleTextView.setText(titleTxt);
        } else {
          titleTextView.setText(titleTxt.substring(0, titleLength) + "...");
        }
        
        return convertView;
      }
    }
    
    //Runnable to load the items 
    class LoadListItemsThread extends Thread {
        @Override
        public void run() {
          try{
            //Set flag so we can not load new items 2 at the same time
            loadingMore = true;
            //Reset the array that holds the new items
            expListItems = new ArrayList<ExperienceItem>();
            HttpGet httpGet = new HttpGet(appContext.getServerAddr() + "GetSharedExperienceAction.do?pageSize="
                                + itemsPerPage + "&pageNum=" + currentPageNumber);
            
            HttpResponse response = appContext.getHttpClient().execute(httpGet);
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
              msg.what = ExperienceListActivity.NEWITEMSLOADEDMSG;
              msgHandler.sendMessage(msg);
              
            } else if(response != null && response.getStatusLine().getStatusCode() == 204) {
              //No Content
              if(response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
              }
              Message msg = new Message();
              msg.what = ExperienceListActivity.NODATAMSG;
              msgHandler.sendMessage(msg);
              
            } else {//Various Error
              if(response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
              }
              Message msg = new Message();
              msg.what = ExperienceListActivity.QUERYERRMSG;
              msgHandler.sendMessage(msg);
            }
          }catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = ExperienceListActivity.QUERYERRMSG;
            msgHandler.sendMessage(msg);
          }
        }
    };
    
    
    private void updateFooterViewToDownloadErr() {
    footerProgressBar.setVisibility(View.GONE);
    footerTextView.setText("下载数据出错，请稍后重试.");
      this.loadingMore = false;
    }
    
    private void updateFooterViewToNoMoredata() {
//      footerProgressBar.setVisibility(View.GONE);
//      footerTextView.setText("没有更多数据...");
      this.getListView().removeFooterView(this.footerView);
      this.loadingMore = false;
      this.noMoreData = true;
    }
     
    private void refleshAdaptorData() {
        Log.v("refleshAdaptorData", "entered");
        // Loop thru the new items and add them to the adapter
        if (expListItems != null && expListItems.size() > 0) {
          for (int i = 0; i < expListItems.size(); i++) {
            adapter.add(expListItems.get(i));
          }
        }
        // Tell to the adapter that changes have been made, this will cause the list
        // to refresh
        adapter.notifyDataSetChanged();
    
        // Done loading more.
        loadingMore = false;
   }
    private void InfoMessageBox(String title, String msg) {
      AlertDialog notifyDialog = new AlertDialog.Builder(ExperienceListActivity.this)
          .setTitle(title).setMessage(msg)
          .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          }).create();
      notifyDialog.show();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
          // land
      } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
          // port
      }
    }
}