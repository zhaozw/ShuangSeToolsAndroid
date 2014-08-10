package com.shuangse.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuangse.base.ShuangSeToolsSetApplication;
import com.shuangse.meta.MyHisRecord;

public class MyHisActivity extends ListActivity {
  private final static String TAG = "MyHisActivity";
  private ProgressDialog progressDialog;
  private ShuangSeToolsSetApplication appContext;
  
  private DataLoadCompletedListener loadCompletedLisneter;
  public void setLoadDataComplete(DataLoadCompletedListener dataComplete) {
      this.loadCompletedLisneter = dataComplete;
  }
  
  private  ArrayList<MyHisRecord> myHisListItems;
  private ArrayAdapter<MyHisRecord> adapter;
  
  //当前点击选中的项
  private MyHisRecord currentSelItem;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //更新标题
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);        
    setContentView(R.layout.listplaceholder);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
    
    final TextView titleTextView = (TextView) findViewById(R.id.title_text);
    titleTextView.setText(R.string.custom_title_myhis);
  
//    Button returnBtn = (Button)findViewById(R.id.returnbtn);
//    returnBtn.setVisibility(View.VISIBLE);
//    Button helpBtn = (Button)findViewById(R.id.helpbtn);
//    helpBtn.setVisibility(View.VISIBLE);
//    helpBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        String htmlMsg = "本页为您提供您个人购买号码的记录保存，供您回顾与查看，每行一个记录，点击对应的行可以 查看详细中奖情况 或 删除该记录。";
//        MagicTool.customInfoMsgBox("本页帮助信息", htmlMsg, MyHisActivity.this).show();
//      }
//    });
//    returnBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        onBackPressed();
//      }
//    });
    
    appContext = (ShuangSeToolsSetApplication) getApplication();
    
    //This will hold the new items
    myHisListItems = new ArrayList<MyHisRecord>();
    Log.i("MyHisActivity", myHisListItems.size() + "");
    adapter = new MyHisListViewArrayAdapter(this, R.layout.myhislistviewitem, myHisListItems);
    //Set the adapter
    this.setListAdapter(adapter);
    
    TextView emptyView = (TextView) findViewById(android.R.id.empty);
    this.getListView().setEmptyView(emptyView);
    emptyView.setText("在<旋转组号 或 胆拖组号>后，点击<保存>即可将组号记录保存于此，方便开奖后查看和历史回顾。");
    
    // 为所有列表项注册上下文菜单
    this.registerForContextMenu(getListView());
    
    progressDialog = new ProgressDialog(MyHisActivity.this);
    progressDialog.setTitle("提示");
    progressDialog.setMessage("请稍等，正在加载...");
    progressDialog.setCancelable(false);
    progressDialog.show();
    
    new LoadDataAsyncTask().execute("");
    
    setLoadDataComplete(new DataLoadCompletedListener() {
        @Override
        public void loadComplete() {
          if (myHisListItems != null && myHisListItems.size() > 0) {
            for (int i = 0; i < myHisListItems.size(); i++) {
              adapter.add(myHisListItems.get(i));
            }
          }
          adapter.notifyDataSetChanged();
          //这里执行你要的操作，当UI更新完成后会自动调用这里面的代码 
          progressDialog.dismiss();
        }});

    //点击List项
    this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position,
          long arg3) {
        currentSelItem = adapter.getItem(position);
        Log.i("onItemLongClick", "item.id:" + currentSelItem.getItemId() +
            " item.recordIndex:" + currentSelItem.getRecordIndex() + 
            " item.red:" + currentSelItem.getRedNumbers() + 
            "item.blue" + currentSelItem.getBlueNumbers() + 
            "model:" + currentSelItem.getSelectModel() + 
            "hisFlag:" + currentSelItem.getGetoutHisFlag());
        parent.showContextMenu();
        return true;
      }
      
    });
    
    this.getListView().setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("onItemClick", "position:" + position + " id:" + id);
        
        currentSelItem = adapter.getItem(position);
        Log.i("onItemClick", "item.id:" + currentSelItem.getItemId() +
            " item.recordIndex:" + currentSelItem.getRecordIndex() + 
            " item.red:" + currentSelItem.getRedNumbers() + 
            "item.blue" + currentSelItem.getBlueNumbers() + 
            "model:" + currentSelItem.getSelectModel() + 
            "hisFlag:" + currentSelItem.getGetoutHisFlag());

        parent.showContextMenu();

      }
    });

    }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      Log.v(TAG, "populate context menu");
      // set context menu title
      menu.setHeaderTitle("选择操作");
      // add context menu item
      menu.add(0, 1, Menu.NONE, "查看详情");
      menu.add(0, 2, Menu.NONE, "删除该项");
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
      // 得到当前被选中的item信息
      AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
      if(menuInfo != null) {//长按弹出时
          Log.v(TAG, "context item seleted ID="+ menuInfo.id);
          currentSelItem = adapter.getItem(menuInfo.position);
      }
      
        Log.i("onContextItemSelected", "item.id:" + currentSelItem.getItemId() +
        " item.recordIndex:" + currentSelItem.getRecordIndex() + 
        " item.red:" + currentSelItem.getRedNumbers() + 
        "item.blue" + currentSelItem.getBlueNumbers() + 
        "model:" + currentSelItem.getSelectModel() + 
        "hisFlag:" + currentSelItem.getGetoutHisFlag());
        
      switch(item.getItemId()) {
      case 1:
        //do something
        Intent launcher = new Intent().setClass(MyHisActivity.this, MyHisDetailActivity.class);
        Bundle bundle = new Bundle();
        
        bundle.putInt("ITEMRECORDINDEX", currentSelItem.getRecordIndex());
        bundle.putInt("ITEMID", currentSelItem.getItemId());
        bundle.putInt("MODEL", currentSelItem.getSelectModel());
        if(currentSelItem.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
           bundle.putIntegerArrayList("RedDanArrayList", currentSelItem.getRedDanNumbers());
           bundle.putIntegerArrayList("RedTuoArrayList", currentSelItem.getRedTuoNumbers());
        } else {
            bundle.putIntegerArrayList("RedArrayList", currentSelItem.getRedNumbers());
        }
        bundle.putIntegerArrayList("BlueArrayList", currentSelItem.getBlueNumbers());
        bundle.putInt("GetOutHisFlag", currentSelItem.getGetoutHisFlag());
        
        launcher.putExtras(bundle);
        startActivity(launcher);
         break;
      case 2:
          //Delete the selected item.
          AlertDialog notifyDialog = new AlertDialog.Builder(
              MyHisActivity.this)
                  .setTitle("请确认")
                  .setMessage("点<确定>按钮删除此记录，点<取消>返回.")
                  .setPositiveButton(R.string.OK,
                          new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog,    int which) {
                                //删除号码
                                appContext.deleteOneRecord(currentSelItem);
                                adapter.remove(currentSelItem);
                                adapter.notifyDataSetChanged();
                              }
                          })
                  .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog,    int which) {
                                  return;
                              }
                          }).create();
          notifyDialog.show();
          break;
        
      default:
          return super.onContextItemSelected(item);
      }
      return true;
  }
   
  private interface DataLoadCompletedListener {
    public void loadComplete();
  }
  
  class LoadDataAsyncTask extends AsyncTask<String, String, String> {
    
    @Override
    protected void onPreExecute() {            
        super.onPreExecute();    
    }

    @Override
    protected void onPostExecute(String result) {
        if (loadCompletedLisneter != null) {
            loadCompletedLisneter.loadComplete();
        }
        super.onPostExecute(result);
    }
    
    protected void onProgressUpdate(String... text) {            
    }
    
    protected String doInBackground(String... params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
              myHisListItems = appContext.selectAllMyCombineRecord();
            }
        });

        return "Success";
    }
  }

  
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        // land
    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        // port
    }
  }
  
  static class ViewHolder {
    View itemView;
    MyHisRecord item;
  }
  
  class MyHisListViewArrayAdapter extends ArrayAdapter<MyHisRecord> {
    private int itemViewResourceId;
    private List<MyHisRecord> listItems;
    
    public MyHisListViewArrayAdapter(Context context, int itemViewResourceId, List<MyHisRecord> listItems) {
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
      MyHisRecord item = getItem(position);
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
      String titleTxt = String.valueOf(item.getItemId());
      titleTextView.setText(titleTxt + "期");
      
      TextView redStrTextView = (TextView) holder.itemView.findViewById(R.id.ItemRedStr);
      TextView blueStrTextView = (TextView) holder.itemView.findViewById(R.id.ItemBlueStr);
      TextView modelStrTextView = (TextView) holder.itemView.findViewById(R.id.ItemModelStr);
      
      if(item.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
          redStrTextView.setText("红胆:" + item.getRedDanNumberStr() + "\n红拖:" + item.getRedTuoNumberStr());
      } else {
          Log.i(TAG, "red" + item.getRedNumberStr() + "blue" + item.getBlueNumberStr() + "model" + item.getSelectModel());
          redStrTextView.setText("红球:" + item.getRedNumberStr() + "共" + item.getRedNumbers().size() + "码");
      }
      
      blueStrTextView.setText("篮球:" + item.getBlueNumberStr() + "共" + item.getBlueNumbers().size() + "码");
      modelStrTextView.setText("模式:" + SmartCombineActivity.getModelStrByModelID(item.getSelectModel()));
      
      return convertView;
    }
    
  }

}
