package com.jwkj.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuowei.smarthome.R;
import com.jwkj.data.Contact;
import com.jwkj.global.Constants;
import com.jwkj.global.FList;
import com.jwkj.utils.T;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;

public class DeviceUpdateActivity extends BaseActivity implements OnClickListener {
    Context mContext;
    TextView content_text, button1_text, button2_text, button3_text;
    LinearLayout layout_main;
    Contact mContact;
    ProgressBar content_progress;
    boolean isRegFilter;
    boolean isDownloading = false;
    boolean isUpdate = false;
    boolean isKnowUpdate = false;
    private ImageView lineVertical;
    private boolean isUpdateSuccess=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setBackgroundDrawableResource(R.drawable.bg_dialog_window);
        setContentView(R.layout.activity_device_update);
        mContext = this;
        mContact = (Contact) this.getIntent().getSerializableExtra("contact");
        isUpdate = getIntent().getBooleanExtra("isUpdate", false);
        isKnowUpdate = isUpdate;
        initCompoment();
        regFilter();
    }

    public void initCompoment() {
        layout_main = (LinearLayout) findViewById(R.id.layout_main);
        content_text = (TextView) findViewById(R.id.content_text);
        button1_text = (TextView) findViewById(R.id.button1_text);
        button2_text = (TextView) findViewById(R.id.button2_text);
        button3_text = (TextView) findViewById(R.id.button3_text);
        content_progress = (ProgressBar) findViewById(R.id.content_progress);
        lineVertical = (ImageView) findViewById(R.id.img_line_vertical);

        showButton1();

        button1_text.setText(R.string.cancel);
        button1_text.setOnClickListener(this);
        button2_text.setOnClickListener(this);
        button3_text.setOnClickListener(this);
        if (isUpdate == true) {
            if (mContact.Update == Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_VERSION) {
                isKnowUpdate = true;
                content_progress.setVisibility(RelativeLayout.GONE);
                content_text.setText(mContext.getResources().getString(
                        R.string.current_version_is)
                        + mContact.cur_version
                        + ","
                        + mContext.getResources().getString(
                        R.string.can_update_to) + mContact.up_version);
                content_text.setVisibility(TextView.VISIBLE);
                showButton12();
                button1_text.setText(R.string.update_now);
                button2_text.setText(R.string.next_time);
            } else if (mContact.Update == Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_IN_SD) {
                isKnowUpdate = true;
                content_progress.setVisibility(RelativeLayout.GONE);
                content_text.setText(mContext.getResources().getString(
                        R.string.current_version_is)
                        + mContact.cur_version
                        + ","
                        + mContext.getResources().getString(
                        R.string.can_update_in_sd));
                content_text.setVisibility(TextView.VISIBLE);
                showButton12();
                button1_text.setText(R.string.update_now);
                button2_text.setText(R.string.next_time);
//		        }
//		        else if(mContact.Update == Constants.P2P_SET.DEVICE_UPDATE.IS_LATEST_VERSION){
//		        	T.showShort(
//							mContext,
//							mContext.getResources().getString(
//									R.string.device_is_latest_version)
//									);
//					finish();
            }
        } else {
            content_progress.setVisibility(RelativeLayout.VISIBLE);
            showButton1();
            content_text.setVisibility(TextView.GONE);
            button1_text.setText(R.string.cancel);
            P2PHandler.getInstance().checkDeviceUpdate(mContact.contactId,
                    mContact.contactPassword);
        }
        Animation anim = AnimationUtils
                .loadAnimation(mContext, R.anim.scale_in);
        layout_main.startAnimation(anim);
    }

    public void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.P2P.ACK_RET_CHECK_DEVICE_UPDATE);
        filter.addAction(Constants.P2P.RET_CHECK_DEVICE_UPDATE);

        filter.addAction(Constants.P2P.ACK_RET_DO_DEVICE_UPDATE);
        filter.addAction(Constants.P2P.RET_DO_DEVICE_UPDATE);

        filter.addAction(Constants.P2P.ACK_RET_CANCEL_DEVICE_UPDATE);
        filter.addAction(Constants.P2P.RET_CANCEL_DEVICE_UPDATE);

        filter.addAction(Constants.P2P.RET_DEVICE_NOT_SUPPORT);
        mContext.registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_CHECK_DEVICE_UPDATE)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    T.showShort(mContext, R.string.password_error);
                    finish();
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    P2PHandler.getInstance().checkDeviceUpdate(
                            mContact.contactId, mContact.contactPassword);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_CHECK_DEVICE_UPDATE)) {
                int result = intent.getIntExtra("result", -1);
                String contactId = intent.getStringExtra("contactId");
                isUpdate = true;
                if (isKnowUpdate == true) {
                    return;
                }
                String cur_version = intent.getStringExtra("cur_version");
                String upg_version = intent.getStringExtra("upg_version");
                content_progress.setVisibility(RelativeLayout.GONE);
                content_text.setVisibility(RelativeLayout.VISIBLE);
                if (result == Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_VERSION) {
                    // content_text.setText(mContext.getResources().getString(R.string.update_device_confirm)+" "+version);
                    content_text.setText(mContext.getResources().getString(
                            R.string.current_version_is)
                            + cur_version
                            + ","
                            + mContext.getResources().getString(
                            R.string.can_update_to) + upg_version);
                    showButton12();
                    button1_text.setText(R.string.update_now);
                    button2_text.setText(R.string.next_time);
//					button2_text.setOnComfirmClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//							// TODO Auto-generated method stub
//							finish();
//						}
//					});
//
//					button1_text.setOnComfirmClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							layout_button1
//									.setVisibility(RelativeLayout.VISIBLE);
//							layout_button2.setVisibility(RelativeLayout.GONE);
//							content_progress
//									.setVisibility(RelativeLayout.VISIBLE);
//							content_text.setVisibility(RelativeLayout.GONE);
//							button1_text.setText(R.string.cancel);
//							content_text.setText(mContext.getResources()
//									.getString(R.string.down_device_update)
//									+ "0%");
//							button1_text
//									.setOnComfirmClickListener(new OnClickListener() {
//
//										@Override
//										public void onClick(View v) {
//											// TODO Auto-generated method stub
//											finish();
//										}
//
//									});
//							P2PHandler.getInstance().doDeviceUpdate(
//									mContact.contactId,
//									mContact.contactPassword);
//						}
//
//					});

                } else if (result == Constants.P2P_SET.DEVICE_UPDATE.HAVE_NEW_IN_SD) {
                    content_progress.setVisibility(RelativeLayout.GONE);
                    content_text.setText(mContext.getResources().getString(
                            R.string.current_version_is)
                            + cur_version
                            + ","
                            + mContext.getResources().getString(
                            R.string.can_update_in_sd));
                    showButton12();
                    button1_text.setText(R.string.update_now);
                    button2_text.setText(R.string.next_time);
//					button2_text.setOnComfirmClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//							// TODO Auto-generated method stub
//							finish();
//						}
//					});
//
//					button1_text.setOnComfirmClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							layout_button1
//									.setVisibility(RelativeLayout.VISIBLE);
//							layout_button2.setVisibility(RelativeLayout.GONE);
//							content_progress
//									.setVisibility(RelativeLayout.VISIBLE);
//							content_text.setVisibility(RelativeLayout.GONE);
//							button1_text.setText(R.string.cancel);
//							button1_text
//									.setOnComfirmClickListener(new OnClickListener() {
//
//										@Override
//										public void onClick(View v) {
//											// TODO Auto-generated method stub
//											finish();
//										}
//
//									});
//							P2PHandler.getInstance().doDeviceUpdate(
//									mContact.contactId,
//									mContact.contactPassword);
//						}
//
//					});
                } else if (result == Constants.P2P_SET.DEVICE_UPDATE.IS_LATEST_VERSION) {
                    content_progress.setVisibility(RelativeLayout.GONE);
                    content_text.setText(mContext.getResources().getString(
                            R.string.device_is_latest_version)
                            + ":" + cur_version);
                    isUpdate=false;
                    showButton1();
                    button1_text.setText(R.string.confirm);
                    button2_text.setText(R.string.update_now);
                } else if (result == Constants.P2P_SET.DEVICE_UPDATE.OTHER_WAS_CHECKING) {
                    T.showShort(mContext, R.string.other_was_checking);
                    finish();
                } else {
                    T.showShort(mContext, R.string.operator_error);
                    finish();

                }
            } else if (intent.getAction().equals(
                    Constants.P2P.ACK_RET_DO_DEVICE_UPDATE)) {
                int result = intent.getIntExtra("result", -1);
                if (result == Constants.P2P_SET.ACK_RESULT.ACK_PWD_ERROR) {
                    T.showShort(mContext, R.string.password_error);
                    finish();
                } else if (result == Constants.P2P_SET.ACK_RESULT.ACK_NET_ERROR) {
                    Log.e("my", "net error resend:do device update");
                    P2PHandler.getInstance().doDeviceUpdate(mContact.contactId,
                            mContact.contactPassword);
                }
            } else if (intent.getAction().equals(
                    Constants.P2P.RET_DO_DEVICE_UPDATE)) {
                int result = intent.getIntExtra("result", -1);
                int value = intent.getIntExtra("value", -1);
                String contactId = intent.getStringExtra("contactId");
                Log.e("my", result + ":" + value);
                if (result == P2PValue.UpdateState.STATE_UPDATING) {
                    content_progress.setVisibility(RelativeLayout.GONE);
                    content_text.setVisibility(RelativeLayout.VISIBLE);
                    content_text.setText(mContext.getResources().getString(
                            R.string.down_device_update_firmware)+"\n"
                            + value + "%");
                } else if (result == P2PValue.UpdateState.STATE_UPDATE_SUCCESS) {
                    Contact c = FList.getInstance().isContact(contactId);
                    if (c != null) {
                        c.Update = Constants.P2P_SET.DEVICE_UPDATE.UNKNOWN;
                    }
                    cancelTextViewAttributes();
                    content_text.setText(R.string.device_update_success);
                    content_progress.setVisibility(RelativeLayout.GONE);
                    content_text.setVisibility(RelativeLayout.VISIBLE);
                    showButton3();
                    isUpdateSuccess=true;
                } else {
                    T.showShort(mContext, R.string.update_failed);
                    finish();
                }
            }
        }
    };

    @Override
    public void finish() {
        Animation anim = AnimationUtils.loadAnimation(mContext,
                R.anim.scale_out);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
                DeviceUpdateActivity.super.finish();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });
        layout_main.startAnimation(anim);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (isRegFilter) {
            isRegFilter = false;
            this.unregisterReceiver(mReceiver);
        }
        P2PHandler.getInstance().cancelDeviceUpdate(mContact.contactId,
                mContact.contactPassword);
        if (isUpdateSuccess){
            Intent back=new Intent();
            back.setAction(Constants.Action.REPLACE_BACK_MAINACTIVITY);
            mContext.sendBroadcast(back);
        }
    }

    @Override
    public int getActivityInfo() {
        // TODO Auto-generated method stub
        return Constants.ActivityInfo.ACTIVITY_DEVICE_UPDATE;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button1_text:
                if (isUpdate == true) {
                    showButton1();
                    content_progress.setVisibility(RelativeLayout.VISIBLE);
                    content_text.setVisibility(RelativeLayout.GONE);
                    button1_text.setText(R.string.cancel);
                    content_text.setText(mContext.getResources()
                            .getString(R.string.down_device_update_firmware)+"\n"
                            + "0%");
                    setTextViewAttributes();
                    P2PHandler.getInstance().doDeviceUpdate(
                            mContact.contactId,
                            mContact.contactPassword);
                    isUpdate = false;
                } else {
                    finish();
                }
                break;
            case R.id.button2_text:
                finish();
                break;
            case R.id.button3_text:
                finish();
                break;
            default:
                break;
        }
    }

    private void showButton1(){
        button1_text.setVisibility(View.VISIBLE);
        button2_text.setVisibility(View.GONE);
        button3_text.setVisibility(View.GONE);
        lineVertical.setVisibility(View.GONE);
    }

    private void showButton2(){
        button1_text.setVisibility(View.GONE);
        button2_text.setVisibility(View.VISIBLE);
        button3_text.setVisibility(View.GONE);
        lineVertical.setVisibility(View.GONE);
    }

    private void showButton3(){
        button1_text.setVisibility(View.GONE);
        button2_text.setVisibility(View.GONE);
        button3_text.setVisibility(View.VISIBLE);
        lineVertical.setVisibility(View.GONE);
    }

    private void showButton12(){
        button1_text.setVisibility(View.VISIBLE);
        button2_text.setVisibility(View.VISIBLE);
        button3_text.setVisibility(View.GONE);
        lineVertical.setVisibility(View.VISIBLE);
    }

    //设置TextView 字体居中 加行距
    private void setTextViewAttributes(){
        content_text.setGravity(Gravity.CENTER_HORIZONTAL);
        content_text.setLineSpacing(8.5f,1.5f);
    }
    //设置TextView 字体靠左 取消行距
    private void cancelTextViewAttributes(){
        content_text.setGravity(Gravity.LEFT);
        content_text.setLineSpacing(8.5f,1);
    }

}
