package com.jwkj.activity;



import com.jwkj.global.Constants;
import com.nuowei.ipclibrary.R;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RecommendProductActivity extends BaseActivity {
	WebView webview;
	ImageView back_btn;
	RelativeLayout loading;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_recommend_product);
		String remmend_url=getIntent().getStringExtra("remmend_url");
		loading=(RelativeLayout)findViewById(R.id.loading);
		webview=(WebView)findViewById(R.id.webview);
    	webview.getSettings().setJavaScriptEnabled(true); 
    	webview.loadUrl(remmend_url);
    	webview.setWebViewClient(new WebViewClient(){
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			// TODO Auto-generated method stub
    			loading.setVisibility(RelativeLayout.GONE);
    			super.onPageFinished(view, url);
    		}   
    	});
    	back_btn=(ImageView)findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}

	@Override
	public int getActivityInfo() {
		// TODO Auto-generated method stub
		return Constants.ActivityInfo.ACTIVITY_RECOMMENDPRODUCTACTIVITY;
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
				if(webview.canGoBack()){
			        webview.goBack();
					return true;
				}
			
	    }
		return super.onKeyDown(keyCode, event);
	}
}
