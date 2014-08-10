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
  
  //��ǰ���ѡ�е���
  private MyHisRecord currentSelItem;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    //���±���
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
//        String htmlMsg = "��ҳΪ���ṩ�����˹������ļ�¼���棬�����ع���鿴��ÿ��һ����¼�������Ӧ���п��� �鿴��ϸ�н���� �� ɾ���ü�¼��";
//        MagicTool.customInfoMsgBox("��ҳ������Ϣ", htmlMsg, MyHisActivity.this).show();
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
    emptyView.setText("��<��ת��� �� �������>�󣬵��<����>���ɽ���ż�¼�����ڴˣ����㿪����鿴����ʷ�عˡ�");
    
    // Ϊ�����б���ע�������Ĳ˵�
    this.registerForContextMenu(getListView());
    
    progressDialog = new ProgressDialog(MyHisActivity.this);
    progressDialog.setTitle("��ʾ");
    progressDialog.setMessage("���Եȣ����ڼ���...");
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
          //����ִ����Ҫ�Ĳ�������UI������ɺ���Զ�����������Ĵ��� 
          progressDialog.dismiss();
        }});

    //���List��
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
      menu.setHeaderTitle("ѡ�����");
      // add context menu item
      menu.add(0, 1, Menu.NONE, "�鿴����");
      menu.add(0, 2, Menu.NONE, "ɾ������");
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
      // �õ���ǰ��ѡ�е�item��Ϣ
      AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
      if(menuInfo != null) {//��������ʱ
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
                  .setTitle("��ȷ��")
                  .setMessage("��<ȷ��>��ťɾ���˼�¼����<ȡ��>����.")
                  .setPositiveButton(R.string.OK,
                          new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog,    int which) {
                                //ɾ������
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
      titleTextView.setText(titleTxt + "��");
      
      TextView redStrTextView = (TextView) holder.itemView.findViewById(R.id.ItemRedStr);
      TextView blueStrTextView = (TextView) holder.itemView.findViewById(R.id.ItemBlueStr);
      TextView modelStrTextView = (TextView) holder.itemView.findViewById(R.id.ItemModelStr);
      
      if(item.getSelectModel() == SmartCombineActivity.M_DAN_TUO_COMBINE) {
          redStrTextView.setText("�쵨:" + item.getRedDanNumberStr() + "\n����:" + item.getRedTuoNumberStr());
      } else {
          Log.i(TAG, "red" + item.getRedNumberStr() + "blue" + item.getBlueNumberStr() + "model" + item.getSelectModel());
          redStrTextView.setText("����:" + item.getRedNumberStr() + "��" + item.getRedNumbers().size() + "��");
      }
      
      blueStrTextView.setText("����:" + item.getBlueNumberStr() + "��" + item.getBlueNumbers().size() + "��");
      modelStrTextView.setText("ģʽ:" + SmartCombineActivity.getModelStrByModelID(item.getSelectModel()));
      
      return convertView;
    }
    
  }

}
