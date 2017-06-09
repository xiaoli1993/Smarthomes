package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.jwkj.adapter.SystemMessageAdapter;
import com.jwkj.data.DataManager;
import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.data.SystemMsg;
import com.jwkj.entity.Account;
import com.jwkj.fragment.SettingFrag;
import com.jwkj.global.AccountPersist;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.utils.Utils;
import com.lib.pullToRefresh.PullToRefreshBase;
import com.lib.pullToRefresh.PullToRefreshBase.OnRefreshListener;
import com.lib.pullToRefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.p2p.core.P2PHandler;
import com.p2p.core.global.Config;
import com.p2p.core.network.NetManager;
import com.p2p.core.network.SystemMessageResult;
import com.p2p.core.network.SystemMessageResult.SystemMessage;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sauronsoftware.base64.Base64;

public class RecommendInformationActivity extends BaseActivity {
	private Context mcontext;
	ListView mlistview;
	PullToRefreshListView mpull_refresh_list;
	SystemMessageAdapter adapter;
	List<SystemMsg> list;
	ImageView back_btn;
	boolean isRegFilter=false;
    @Override
    protected void onCreate(Bundle arg0) {
    	// TODO Auto-generated method stub
    	super.onCreate(arg0);
    	setContentView(R.layout.activity_recommend_information);
    	mcontext=this;
    	initComponent();
    	regFilter();
    }
	public void initComponent(){
//		progressbar=(ProgressBar)view.findViewById(R.id.progressBar);
//		list_system_mesg=(ListView)view.findViewById(R.id.list_system_mesg);
//		list_system_mesg.setDividerHeight(0);
		
		mpull_refresh_list=(PullToRefreshListView)findViewById(R.id.pull_refresh_list);
		mpull_refresh_list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(
					PullToRefreshBase<ListView> refreshView) {
				       String label = DateUtils.formatDateTime(mcontext,
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				String sellerId=Config.AppConfig.store_id;
				if(sellerId!=null&&!sellerId.equals("")){
				     String msgId=DataManager.findLastMsgIdSystemMessage(mcontext, NpcCommon.mThreeNum);
				     if(msgId==null||msgId.equals("")){
				    	 //数据库中没有的话，取最新的
				    	 new SystemTask(sellerId,2).execute();	
				     }else{
				    	//数据库中有，Option 取值：0向后（下[比MsgIndex新的数据]）
				    	 new GetNewSystemTaskByMsgID(sellerId, msgId,0).execute();				    	 
				     }
				}else{
					mpull_refresh_list.onRefreshComplete();		
				}
			}
		});
		list=DataManager.findSystemMessgeByActiveUser(mcontext,NpcCommon.mThreeNum);
		mpull_refresh_list.setShowIndicator(false);
		mlistview = mpull_refresh_list.getRefreshableView();
	    adapter=new SystemMessageAdapter(mcontext, list);
		mlistview.setAdapter(adapter);
		mlistview.setDividerHeight(0);
		
		back_btn=(ImageView)findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
//		list=DataManager.findSystemMessgeByActiveUser(mcontext,NpcCommon.mThreeNum);
//		adapter=new SystemMessageAdapter(mcontext, list);
//		list_system_mesg.setAdapter(adapter);
	}
	public void regFilter(){
		IntentFilter filter=new IntentFilter();
		filter.addAction(Constants.Action.REFRESH_SYSTEM_MESSAGE);
		registerReceiver(mbr, filter);
		isRegFilter=true;
	}
	BroadcastReceiver mbr=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Constants.Action.REFRESH_SYSTEM_MESSAGE)){
				adapter.updateData();
			}
		}
	};
	 class SystemTask extends AsyncTask{
	    	String sellerId;
	    	int Option;
	        public SystemTask(String sellerId,int Option){
	        	this.sellerId=sellerId;
	        	this.Option=Option;
	        }
			@Override
			protected Object doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				Account account;
				account=AccountPersist.getInstance().getActiveAccountInfo(mcontext);
				Log.e("account","account.three_number="+account.three_number+"account.sessionId="+account.sessionId);
				return NetManager.getInstance(mcontext).getSystemMessage(account.three_number, account.sessionId,sellerId,100,Option);
			}
			@Override
			protected void onPostExecute(Object object) {
				// TODO Auto-generated method stub
				SystemMessageResult result=NetManager.getInstance(mcontext).GetSystemMessageResult((JSONObject) object);
			    String error_code=result.error_code;
			    String RecordCount=result.RecordCount;
			    String Surplus=result.Surplus;
			    String RecommendFlag=result.RecommendFlag;
			    Log.e("system_mesg", "error_code="+error_code+"-------------");
			    Log.e("system_mesg", "RecordCount="+RecordCount+"-------------");
			    Log.e("system_mesg", "Surplus="+Surplus+"-------------");
			    Log.e("system_mesg", "RecommendFlag="+RecommendFlag+"-------------");
				switch (Integer.parseInt(error_code)) {
				case NetManager.SESSION_ID_ERROR:
					Intent i = new Intent();
					i.setAction(Constants.Action.SESSION_ID_ERROR);
					MyApplication.app.sendBroadcast(i);
					break;
				case NetManager.CONNECT_CHANGE:
					new SystemTask(sellerId,Option).execute();
					break;
				case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
					 final List<SystemMsg> msgs=new ArrayList<SystemMsg>();
					 List<SystemMessage> lists = result.systemMessages;
					 String app_name=getResources().getString(R.string.app_name);
					 final String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
						File file=new File(path);
						if(!file.exists()){
							file.mkdirs();
						}
					 for(int j=lists.size()-1;j>=0;j--){
					     try {
					    	 SystemMsg msg=new SystemMsg();
					    	 msg.msgId=lists.get(j).msgId;
					    	 msg.title=Base64.decode(lists.get(j).title,"UTF-8");
					    	 msg.content=Base64.decode(lists.get(j).content,"UTF-8");
					    	 msg.time=Utils.ConvertTimeByLong(lists.get(j).time);
						     msg.pictrue_url=lists.get(j).picture_url;
						     msg.url=Base64.decode(lists.get(j).picture_in_url,"UTF-8");
						     msg.active_user=NpcCommon.mThreeNum;
						     msg.isRead=0;
						     Log.e("system_message", "msg.msgId="+ msg.msgId+" "+
						    		 "msg.title="+ msg.title+" "+
						    		 "msg.content="+ msg.content+" "+
						    		 "msg.time="+msg.time+" "+
						    		 "msg.pictrue="+ msg.pictrue+" "+
						    		 "msg.url="+  msg.url+" "+
						    		 "msg.active_user="+msg.active_user+" ");
						     msgs.add(msg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					     
					 }
//					 new Thread(new Runnable() {
//							@Override
//							public void run() {
								// TODO Auto-generated method stub
								for(final SystemMsg m:msgs){
									boolean contains=false;
									List<SystemMsg> list=DataManager.findSystemMessgeByActiveUser(mcontext,NpcCommon.mThreeNum);
									for(SystemMsg s:list){
										 if(m.title.equals(s.title)&&m.content.equals(s.content)&&m.time.equals(s.time)){
											 contains=true;
											 break;
										 }
									 }
									 if(contains==false){
										 DataManager.insertSystemMessage(mcontext,m);
										 ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
											 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
												 saveSystemMessagePictrue(loadedImage, path,m.msgId);
											 };
										 });	 	 
									 }
								}
//							}
//						}).start();
					 adapter.updateData();
					 P2PHandler.getInstance().setSystemMessageIndex(Constants.SystemMessgeType.MALL_NEW,Integer.parseInt(RecommendFlag));
					 String saveFrag=SharedPreferencesManager.getInstance().getSystemMessageIndex(mcontext);
					 if(saveFrag==null||Integer.parseInt(saveFrag)<Integer.parseInt(RecommendFlag)){
						 SharedPreferencesManager.getInstance().putSystemMessageIndex(mcontext, RecommendFlag);
					 }
					 if(Integer.parseInt(Surplus)>0){
						String messageId=msgs.get(0).msgId;
					    new GetOldSystemTaskByMsgID(sellerId,messageId,1);
					 }
					 mpull_refresh_list.onRefreshComplete();
					break;
				default:
					break;
				}
			}
	    	
	    }
	 class GetOldSystemTaskByMsgID extends AsyncTask{
	    	String sellerId;
	    	String msgId;
	    	int Option;
	    	
	        public GetOldSystemTaskByMsgID(String sellerId,String msgId,int Option){
	        	this.sellerId=sellerId;
	        	this.msgId=msgId;
	        	this.Option=Option;
	        }
			@Override
			protected Object doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				Account account;
				account=AccountPersist.getInstance().getActiveAccountInfo(mcontext);
				Log.e("account","account.three_number="+account.three_number+"account.sessionId="+account.sessionId);
				return NetManager.getInstance(mcontext).getSystemMessageByMsgId(account.three_number, account.sessionId,sellerId,msgId,100,Option);
			}
			@Override
			protected void onPostExecute(Object object) {
				// TODO Auto-generated method stub
				SystemMessageResult result=NetManager.getInstance(mcontext).GetSystemMessageResult((JSONObject) object);
			    String error_code=result.error_code;
			    String RecordCount=result.RecordCount;
			    String Surplus=result.Surplus;
			    String RecommendFlag=result.RecommendFlag;
			    Log.e("system_mesg", "error_code="+error_code+"-------------");
			    Log.e("system_mesg", "RecordCount="+RecordCount+"-------------");
			    Log.e("system_mesg", "Surplus="+Surplus+"-------------");
			    Log.e("system_mesg", "RecommendFlag="+RecommendFlag+"-------------");
				switch (Integer.parseInt(error_code)) {
				case NetManager.SESSION_ID_ERROR:
					Intent i = new Intent();
					i.setAction(Constants.Action.SESSION_ID_ERROR);
					MyApplication.app.sendBroadcast(i);
					break;
				case NetManager.CONNECT_CHANGE:
					new GetOldSystemTaskByMsgID(sellerId, msgId, Option);
					break;
				case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
					 final List<SystemMsg> msgs=new ArrayList<SystemMsg>();
					 List<SystemMessage> lists = result.systemMessages;
					 String app_name=getResources().getString(R.string.app_name);
					 final String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
						File file=new File(path);
						if(!file.exists()){
							file.mkdirs();
						}
					 for(int j=lists.size()-1;j>=0;j--){
					     try {
					    	 SystemMsg msg=new SystemMsg();
					    	 msg.msgId=lists.get(j).msgId;
					    	 msg.title=Base64.decode(lists.get(j).title,"UTF-8");
					    	 msg.content=Base64.decode(lists.get(j).content,"UTF-8");
					    	 msg.time=Utils.ConvertTimeByLong(lists.get(j).time);
						     msg.pictrue_url=lists.get(j).picture_url;
						     msg.url=Base64.decode(lists.get(j).picture_in_url,"UTF-8");
						     msg.active_user=NpcCommon.mThreeNum;
						     msg.isRead=0;
						     msgs.add(msg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					     
					 }
//					 new Thread(new Runnable() {
//							@Override
//							public void run() {
								// TODO Auto-generated method stub
								for(final SystemMsg m:msgs){
									boolean contains=false;
									List<SystemMsg> list=DataManager.findSystemMessgeByActiveUser(mcontext,NpcCommon.mThreeNum);
									for(SystemMsg s:list){
										 if(m.title.equals(s.title)&&m.content.equals(s.content)&&m.time.equals(s.time)){
											 contains=true;
											 break;
										 }
									 }
									 if(contains==false){
										 DataManager.insertSystemMessage(mcontext,m);
										 ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
											 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
												 saveSystemMessagePictrue(loadedImage, path,m.msgId);
											 };
										 });	 	 
									 }
								}
//							}
//						}).start();
					 adapter.updateData();
					 if(Integer.parseInt(Surplus)>0){
						String messageId=msgs.get(0).msgId;
						new GetOldSystemTaskByMsgID(sellerId,messageId,1);
					 }
					break;
				default:
					break;
				}
			}
	    	
	    }
	 class GetNewSystemTaskByMsgID extends AsyncTask{
	    	String sellerId;
	    	String msgId;
	    	int Option;
	    	
	        public GetNewSystemTaskByMsgID(String sellerId,String msgId,int Option){
	        	this.sellerId=sellerId;
	        	this.msgId=msgId;
	        	this.Option=Option;
	        }
			@Override
			protected Object doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				Account account;
				account=AccountPersist.getInstance().getActiveAccountInfo(mcontext);
				Log.e("account","account.three_number="+account.three_number+"account.sessionId="+account.sessionId);
				return NetManager.getInstance(mcontext).getSystemMessageByMsgId(account.three_number, account.sessionId,sellerId,msgId,100,Option);
			}
			@Override
			protected void onPostExecute(Object object) {
				// TODO Auto-generated method stub
				SystemMessageResult result=NetManager.getInstance(mcontext).GetSystemMessageResult((JSONObject) object);
			    String error_code=result.error_code;
			    String RecordCount=result.RecordCount;
			    String Surplus=result.Surplus;
			    String RecommendFlag=result.RecommendFlag;
			    Log.e("system_mesg", "error_code="+error_code+"-------------");
			    Log.e("system_mesg", "RecordCount="+RecordCount+"-------------");
			    Log.e("system_mesg", "Surplus="+Surplus+"-------------");
			    Log.e("system_mesg", "RecommendFlag="+RecommendFlag+"-------------");
				switch (Integer.parseInt(error_code)) {
				case NetManager.SESSION_ID_ERROR:
					Intent i = new Intent();
					i.setAction(Constants.Action.SESSION_ID_ERROR);
					MyApplication.app.sendBroadcast(i);
					break;
				case NetManager.CONNECT_CHANGE:
					new GetNewSystemTaskByMsgID(sellerId, msgId, Option);
					break;
				case NetManager.GET_SYSTEM_MESSAGE_SUCCESS:
					 mpull_refresh_list.onRefreshComplete();
					 List<SystemMsg> msgs=new ArrayList<SystemMsg>();
					 List<SystemMessage> lists = result.systemMessages;
					 String app_name=getResources().getString(R.string.app_name);
					 final String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME;
						File file=new File(path);
						if(!file.exists()){
							file.mkdirs();
						}
					 for(int j=lists.size()-1;j>=0;j--){
					     try {
					    	 SystemMsg msg=new SystemMsg();
					    	 msg.msgId=lists.get(j).msgId;
					    	 msg.title=Base64.decode(lists.get(j).title,"UTF-8");
					    	 msg.content=Base64.decode(lists.get(j).content,"UTF-8");
					    	 msg.time=Utils.ConvertTimeByLong(lists.get(j).time);
						     msg.pictrue_url=lists.get(j).picture_url;
						     msg.url=Base64.decode(lists.get(j).picture_in_url, "UTF-8");
						     msg.active_user=NpcCommon.mThreeNum;
						     msg.isRead=0;
						     msgs.add(msg);
						     Log.e("newsystemdata",msg.msgId);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					     
					 }
					 for(final SystemMsg m:msgs){
						 List<SystemMsg> list=DataManager.findSystemMessgeByActiveUser(mcontext,NpcCommon.mThreeNum);
				            boolean contains=false;
							 for(SystemMsg s:list){
								 if(m.title.equals(s.title)&&m.content.equals(s.content)&&m.time.equals(s.time)){
									 contains=true;
									 break;
								 }
							 }
							 if(contains==false){
								 DataManager.insertSystemMessage(mcontext,m);
								 ImageLoader.getInstance().loadImage(m.pictrue_url, new SimpleImageLoadingListener(){
									 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
										 saveSystemMessagePictrue(loadedImage, path,m.msgId);
									 };
								 });	  
							 }
					 }
					 adapter.updateData();
					 P2PHandler.getInstance().setSystemMessageIndex(Constants.SystemMessgeType.MALL_NEW,Integer.parseInt(RecommendFlag));
					 String saveFrag=SharedPreferencesManager.getInstance().getSystemMessageIndex(mcontext);
					 if(saveFrag==null||Integer.parseInt(saveFrag)<Integer.parseInt(RecommendFlag)){
						 SharedPreferencesManager.getInstance().putSystemMessageIndex(mcontext, RecommendFlag);
					 }
					 if(Integer.parseInt(Surplus)>0){
						 String messagId=msgs.get(0).msgId;
						 new GetNewSystemTaskByMsgID(sellerId, messagId,0);
					 }
					break;
				default:
					break;
				}
			}
	    	
	    }
	 public void saveSystemMessagePictrue(Bitmap bitamap,String path,String fileName){
         Bitmap bitmap = bitamap;
         String picture_path =path+"/"+fileName+".jpg";  // 这个就是你存放的路径了。
         File bitmapFile = new File(picture_path);
         FileOutputStream fos = null;
         if (!bitmapFile.exists()) {
          try{
           bitmapFile.createNewFile();
           fos = new FileOutputStream(bitmapFile);
           bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
          }catch (IOException e) {
           e.printStackTrace();
          }finally {
           try {
            if (fos != null) {
             fos.close();
            }
           } catch (IOException e) {
            e.printStackTrace();
           }
          }
         }else{
         	 try {
 				fos = new FileOutputStream(bitmapFile);
 				 bitmap.compress(Bitmap.CompressFormat.JPEG,100, fos);
 			} catch (FileNotFoundException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}finally {
 		           try {
 		              if (fos != null) {
 		               fos.close();
 		              }
 		             } catch (IOException e) {
 		              e.printStackTrace();
 		             }
 		            }
         }
  }
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_RECOMMENDINFORMATIONACTIVITY;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SettingFrag.isCheckMsg=false;
		if(isRegFilter){
			unregisterReceiver(mbr);
			isRegFilter=false;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SettingFrag.isCheckMsg=true;
	}

}
