package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwkj.activity.RecommendProductActivity;
import com.jwkj.data.DataManager;
import com.jwkj.data.SystemMsg;
import com.jwkj.global.Constants;
import com.jwkj.global.NpcCommon;
import com.jwkj.widget.NormalDialog;
import com.nuowei.ipclibrary.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class SystemMessageAdapter extends BaseAdapter {
    List<SystemMsg> list;
    Context mcontext;
    public SystemMessageAdapter(Context mcontext,List<SystemMsg> list) {
	// TODO Auto-generated constructor stub
    	this.list=list;
    	this.mcontext=mcontext;
    }
    class ViewHolder {
		private TextView sys_mesg_title;
		private TextView sys_mesg_content;
		private TextView sys_mesg_time;
		private ImageView sys_mesg_pictrue;
		public TextView getSys_mesg_title() {
			return sys_mesg_title;
		}
		public void setSys_mesg_title(TextView sys_mesg_title) {
			this.sys_mesg_title = sys_mesg_title;
		}
		public TextView getSys_mesg_content() {
			return sys_mesg_content;
		}
		public void setSys_mesg_content(TextView sys_mesg_content) {
			this.sys_mesg_content = sys_mesg_content;
		}
		public TextView getSys_mesg_time() {
			return sys_mesg_time;
		}
		public void setSys_mesg_time(TextView sys_mesg_time) {
			this.sys_mesg_time = sys_mesg_time;
		}
		public ImageView getSys_mesg_pictrue() {
			return sys_mesg_pictrue;
		}
		public void setSys_mesg_pictrue(ImageView sys_mesg_pictrue) {
			this.sys_mesg_pictrue = sys_mesg_pictrue;
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View view=arg1;
		final ViewHolder holder;
		if(view==null){
		  view=LayoutInflater.from(mcontext).inflate(R.layout.list_system_mesg_item, null);
		  holder=new ViewHolder();
		  holder.setSys_mesg_title((TextView)view.findViewById(R.id.sys_mesg_title));
		  holder.setSys_mesg_content((TextView)view.findViewById(R.id.sys_mesg_content));
		  holder.setSys_mesg_pictrue((ImageView)view.findViewById(R.id.sys_mesg_pictrue));
		  holder.setSys_mesg_time((TextView)view.findViewById(R.id.sys_mesg_time));
		  view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		final SystemMsg message=list.get(arg0);
		holder.getSys_mesg_title().setText(message.title);
		holder.getSys_mesg_content().setText(message.content);
		holder.getSys_mesg_time().setText(message.time);
//	    Bitmap bitmap = BitmapFactory.decodeByteArray(message.pictrue, 0,message.pictrue.length);
		String app_name=mcontext.getResources().getString(R.string.app_name);
	  	String path=Environment.getExternalStorageDirectory().getPath()+"/"+Constants.CACHE_FOLDER_NAME+"/"+message.msgId+".jpg";
	  	Log.e("imagesDir", path);
//		ImageLoader.getInstance().displayImage(path,holder.getSys_mesg_pictrue());
		Log.e("meessog", "message.msgId="+message.msgId+"--"+"message.title="+message.title+"path="+path);
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize=2;
		Bitmap bitmap=BitmapFactory.decodeFile(path,options);
		if(bitmap!=null){
			holder.getSys_mesg_pictrue().setImageBitmap(bitmap);
		}else{
			holder.getSys_mesg_pictrue().setImageResource(R.drawable.default_system_msg_img);
		}
		
	    if(message.isRead==1){
	    	holder.getSys_mesg_title().setTextColor(mcontext.getResources().getColor(R.color.gray));
	    	holder.getSys_mesg_content().setTextColor(mcontext.getResources().getColor(R.color.gray));
	    	holder.getSys_mesg_time().setTextColor(mcontext.getResources().getColor(R.color.gray));
	    }else{
	    	holder.getSys_mesg_title().setTextColor(mcontext.getResources().getColor(R.color.black));
	    	holder.getSys_mesg_content().setTextColor(mcontext.getResources().getColor(R.color.black));
	    	holder.getSys_mesg_time().setTextColor(mcontext.getResources().getColor(R.color.black));
	    }
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DataManager.UpdateSystemMessageIsRead(mcontext, message.id);
				String url=message.url;
				if(url!=null&&(!url.equals(""))){
					Intent open_web = new Intent(mcontext,RecommendProductActivity.class);  
					open_web.putExtra("remmend_url", url);
					mcontext.startActivity(open_web);
				}
				updateData();
			}
		});
		view.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				NormalDialog dialog = new NormalDialog(
						mcontext,
						mcontext.getResources().getString(R.string.delete_system_message),
						mcontext.getResources().getString(R.string.are_you_sure_delete)+" "+message.title+"?",
						mcontext.getResources().getString(R.string.delete),
						mcontext.getResources().getString(R.string.cancel)
						);
				dialog.setOnButtonOkListener(new NormalDialog.OnButtonOkListener() {
					
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						
						DataManager.deleteSystemMessageById(mcontext, message.id);
						Intent refresh=new Intent();
						refresh.setAction(Constants.Action.REFRESH_SYSTEM_MESSAGE);
						mcontext.sendBroadcast(refresh);
					}
				});
				dialog.showDialog();
				return true;
			}
		});
		return view;
	}
	public  Bitmap loadImageFromUrl(String url) throws Exception  {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(url);

        HttpResponse response = client.execute(getRequest);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK)  {
            Log.e("PicShow", "Request URL failed, error code =" + statusCode);
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            Log.e("PicShow", "HttpEntity is null");
        }
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            is = entity.getContent();
            byte[] buf = new byte[1024];
            int readBytes = -1;
            while ((readBytes = is.read(buf)) != -1) {
                baos.write(buf, 0, readBytes);
            }
        } finally {
            if (baos != null) {
                baos.close();
            }
            if (is != null) {
                is.close();
            }
        }
        byte[] imageArray = baos.toByteArray();
        return BitmapFactory.decodeByteArray(
                imageArray, 0, imageArray.length);
   }
   public void updateData(){
	   list=DataManager.findSystemMessgeByActiveUser(mcontext,String.valueOf(NpcCommon.mThreeNum)); 
	   this.notifyDataSetChanged();
   }
}
