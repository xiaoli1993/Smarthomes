package com.jwkj.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.data.SharedPreferencesManager;
import com.jwkj.global.Constants;
import com.nuowei.smarthome.R;

public class MallActivity extends BaseActivity {
	public WebView webview;
	RelativeLayout loading;
	TextView title;
	Context mContext;
	String mall_url;
	View view;
	ProgressBar progressBar;
	ImageView back_btn;
	boolean isHelpUrl=false;
    @Override
    protected void onCreate(Bundle arg0) {
    	// TODO Auto-generated method stub
    	super.onCreate(arg0);
    	setContentView(R.layout.activity_mall);
    	isHelpUrl=getIntent().getBooleanExtra("isHelpUrl", false);
    	mContext=this;
    	initComponent();
    }
    public void initComponent(){
		loading=(RelativeLayout)findViewById(R.id.loading);
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		title=(TextView)findViewById(R.id.title);
		if(isHelpUrl){
			mall_url=getIntent().getStringExtra("url");
			title.setText(R.string.help_center);
		}else{
			mall_url=SharedPreferencesManager.getInstance().getMallUrl(mContext);
			title.setText(R.string.mall);
		}
		back_btn=(ImageView)findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		if(mall_url==null||mall_url.equals("")){
			return;
		}
		webview=(WebView)findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true); 
		webview.getSettings().supportZoom();
		 //设置可以访问文件  
		webview.getSettings().setAllowFileAccess(true);  
		webview.loadUrl(mall_url);
		webview.setWebViewClient(new WebViewClient());
		webview.setDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloadStart(String arg0, String arg1, String arg2,
					String arg3, long arg4) {
				// TODO Auto-generated method stub
				Uri help_url=Uri.parse(arg0);
				Intent intent = new Intent(Intent.ACTION_VIEW, help_url);  
	            startActivity(intent);  
			}
		});
//		webview.setWebViewClient(new WebViewClient(){
//    		@Override
//    		public void onPageFinished(WebView view, String url) {
//    			// TODO Auto-generated method stub
//    		} 
//    		@Override
//    		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//    			// TODO Auto-generated method stub
//    			view.loadUrl(url);
//    			return true;
//    		}
//    		
//    	});
//		webview.setWebChromeClient(new WebChromeClient(){
//			 @Override 
//
//		        public void onProgressChanged(WebView view, int newProgress) { 
//
//		 
//
//		            if (newProgress == 100) { 
//		            	progressBar.setVisibility(RelativeLayout.GONE);
//
//		            } 
//
//		            super.onProgressChanged(view, newProgress); 
//
//		        } 
//		
//		});
	}
	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_MALLACTIVITY;
	}
	   // 设置回退
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}
