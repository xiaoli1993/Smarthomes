package com.nuowei.smarthome.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuowei.smarthome.Constants;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.PersonalAdapter;
import com.nuowei.smarthome.common.util.ToastUtils;
import com.nuowei.smarthome.manage.DeviceManage;
import com.nuowei.smarthome.modle.Personal;
import com.nuowei.smarthome.modle.XlinkDevice;
import com.nuowei.smarthome.smarthomesdk.http.HttpManage;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.util.PhotoUtil;
import com.nuowei.smarthome.util.SDPathUtils;
import com.nuowei.smarthome.view.cbdialog.CBDialogBuilder;
import com.nuowei.smarthome.view.scrollview.PullScrollView;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;
import com.orhanobut.hawk.Hawk;
import com.umeng.analytics.MobclickAgent;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.MDEditDialog;
import com.wevey.selector.dialog.NormalAlertDialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.xlink.wifi.sdk.XlinkAgent;

/**
 * @Author : 肖力
 * @Time :  2017/4/19 17:20
 * @Description :
 * @Modify record :
 */

public class PersonalActivity extends BaseActivity implements PullScrollView.OnTurnListener {

    @BindView(R.id.background_img)
    ImageView backgroundImg;
    //    @BindView(R.id.scroll_view)
//    PullScrollView scrollView;
    @BindView(R.id.scroll_view_head)
    RelativeLayout scrollViewHead;
    @BindView(R.id.tv_device_n)
    DinProTextView tvDeviceN;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.tv_user_name)
    DinProTextView tvUserName;
    @BindView(R.id.user_divider_layout)
    FrameLayout userDividerLayout;
    @BindView(R.id.tv_address)
    DinProTextView tvAddress;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.image_btn_backs)
    ImageButton btnBack;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;

    private List<Personal> personalList = new ArrayList<Personal>();
    private PersonalAdapter mAdapter;

    //    List<String> gwString = new ArrayList<String>();
    private List<XlinkDevice> gwList = new ArrayList<XlinkDevice>();
    private boolean isChoice = false;
    private XlinkDevice xlinkDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initData();
        initEven();
    }

    private void initEven() {
        btnBack.setBackgroundResource(R.drawable.avoscloud_feedback_thread_actionbar_back);
        tbToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvRight.setVisibility(View.GONE);
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setBackgroundResource(R.drawable.pen);
        tvTitle.setText(R.string.Personal);
        tvTitle.setTextColor(getResources().getColor(R.color.white));
//        btnBack.setImageResource();
//        scrollView.setHeader(backgroundImg);
//        scrollView.setOnTurnListener(this);
        mAdapter = new PersonalAdapter(PersonalActivity.this, personalList);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        try {
                            new CBDialogBuilder(PersonalActivity.this)
                                    .setTouchOutSideCancelable(false)
                                    .showConfirmButton(false)
                                    .setTitle(getString(R.string.Choice_Gateway))
//                                .setConfirmButtonText("ok")
//                                .setCancelButtonText("cancel")
                                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                                    .setItems(gwList, new CBDialogBuilder.onDialogItemXlinkClickListener() {
                                        @Override
                                        public void onDialogItemClick(CBDialogBuilder.DialogItemXlinkAdapter adapter, Context context, CBDialogBuilder cbDialogBuilder, Dialog dialog, int position) {
                                            MyApplication.getLogger().i("点击：" + gwList.get(position).getDeviceName());
                                            xlinkDevice = gwList.get(position);
                                            Hawk.put(Constants.DEVICE_GW, gwList.get(position));
                                            MainActivity.setChoiceGwDevice(gwList.get(position));
                                            personalList.remove(1);
                                            personalList.add(1, new Personal(R.drawable.personal_gw, getString(R.string.Choice_Gateway), gwList.get(position).getDeviceName()));
                                            mAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    }, xlinkDevice.getDeviceMac()).create().show();
                        } catch (Exception e) {
                            new CBDialogBuilder(PersonalActivity.this)
                                    .setTouchOutSideCancelable(true)
                                    .showCancelButton(true)
                                    .setTitle(getString(R.string.not_gateway))
                                    .setMessage("")
                                    .setCustomIcon(R.drawable.alerter_ic_notifications)
                                    .setConfirmButtonText(getString(R.string.Adddevice))
                                    .setCancelButtonText(getResources().getString(R.string.dialog_cancel))
                                    .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
                                    .setButtonClickListener(true, new CBDialogBuilder.onDialogbtnClickListener() {
                                        @Override
                                        public void onDialogbtnClick(Context context, Dialog dialog, int whichBtn) {
                                            switch (whichBtn) {
                                                case BUTTON_CONFIRM:
//                                                    Toast.makeText(context, "点击了确认按钮", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case BUTTON_CANCEL:
//                                                    Toast.makeText(context, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }).create().show();
                        }
                        break;
                    case 2:
                        startActivity(new Intent(PersonalActivity.this, ModifyPasswordActivity.class));
                        break;
                }
            }
        });
    }


    private void initData() {

        isChoice = Hawk.contains(Constants.DEVICE_GW);
        List<XlinkDevice> xlinkDeviceList = DeviceManage.getInstance().getDevices();
        for (int i = 0; i < xlinkDeviceList.size(); i++) {
            if (xlinkDeviceList.get(i).getDeviceType() == Constants.DEVICE_TYPE.DEVICE_WIFI_GATEWAY) {
//                gwString.add(xlinkDeviceList.get(i).getDeviceName());
                gwList.add(xlinkDeviceList.get(i));
            }
        }
        if (MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getEmail())) {
            personalList.add(new Personal(R.drawable.personal_phone, getString(R.string.Phone), MyApplication.getMyApplication().getUserInfo().getPhone()));
        } else {
            personalList.add(new Personal(R.drawable.personal_email, getString(R.string.Email), MyApplication.getMyApplication().getUserInfo().getEmail()));
        }
        if (isChoice) {
            xlinkDevice = Hawk.get(Constants.DEVICE_GW);
            personalList.add(new Personal(R.drawable.personal_gw, getString(R.string.Choice_Gateway), xlinkDevice.getDeviceName()));
        } else {
            try {
                personalList.add(new Personal(R.drawable.personal_gw, getString(R.string.Choice_Gateway), gwList.get(0).getDeviceName()));
                xlinkDevice = gwList.get(0);
            } catch (Exception e) {
                personalList.add(new Personal(R.drawable.personal_gw, getString(R.string.Choice_Gateway), ""));
            }
        }

        personalList.add(new Personal(R.drawable.personal_pawd, getString(R.string.Modify_password), ""));

        if (!MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getAvatar())) {
            Glide.with(this).load(MyApplication.getMyApplication().getUserInfo().getAvatar())
                    .centerCrop()
                    .dontAnimate()
                    .priority(Priority.NORMAL)
                    .placeholder(R.drawable.log_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transform(new GlideCircleTransform(this))
                    .into(userAvatar);
        }

        if (!MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getNickname())) {
            tvUserName.setText(MyApplication.getMyApplication().getUserInfo().getNickname());
        }
        tvAge.setText(MyApplication.getMyApplication().getUserInfo().getAge() + "  " + getString(R.string.Age));
        if (!MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getAddress())) {
            tvAddress.setText(MyApplication.getMyApplication().getUserInfo().getAddress());
        }

    }

    @Override
    public void onTurn() {

    }

    @OnClick(R.id.btn_right)
    void onBtnRight() {
        showChangeName(PersonalActivity.this);
    }

    private void showChangeName(Context context) {
        new MDEditDialog.Builder(context).setTitleVisible(true)
                .setTitleText(getString(R.string.Change_Name))
                .setTitleTextSize(20)
                .setTitleTextColor(R.color.black_light)
                .setContentText(MyApplication.getMyApplication().getUserInfo().getNickname())
                .setContentTextSize(18)
                .setMaxLength(20)
                .setHintText(getString(R.string.no_user_name))
                .setMaxLines(1)
                .setContentTextColor(R.color.colorPrimary)
                .setButtonTextSize(14)
                .setLeftButtonTextColor(R.color.colorPrimary)
                .setLeftButtonText(getString(R.string.cancel))
                .setRightButtonTextColor(R.color.colorPrimary)
                .setRightButtonText(getString(R.string.dialog_ok))
                .setLineColor(R.color.colorPrimary)
                .setInputTpye(InputType.TYPE_CLASS_TEXT)
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<MDEditDialog>() {

                    @Override
                    public void clickLeftButton(MDEditDialog dialog, View view) {

                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(MDEditDialog dialog, View view) {
                        final String rename = dialog.getEditTextContent();
                        HttpManage.getInstance().modifyUser(PersonalActivity.this,
                                rename, new HttpManage.ResultCallback<String>() {
                                    @Override
                                    public void onError(Header[] headers, HttpManage.Error error) {

                                    }

                                    @Override
                                    public void onSuccess(int code, String response) {
                                        MyApplication.getMyApplication().getUserInfo().setNickname(rename);
                                        MyApplication.getLogger().json(response);
                                        tvUserName.setText(rename);
                                    }
                                });
                        dialog.dismiss();
                    }
                }).setMinHeight(0.3f)
                .setWidth(0.8f)
                .build()
                .show();
    }

    @OnClick(R.id.btn_logout)
    void onLogout() {
        new NormalAlertDialog.Builder(PersonalActivity.this).setTitleVisible(false)
                .setTitleText(getResources().getString(R.string.Tips))
                .setTitleTextColor(R.color.black_light)
                .setContentText(getString(R.string.is_logout))
                .setContentTextColor(R.color.black_light)
                .setLeftButtonText(getResources().getString(R.string.cancel))
                .setLeftButtonTextColor(R.color.error_stroke_color)
                .setRightButtonText(getResources().getString(R.string.dialog_ok))
                .setRightButtonTextColor(R.color.color_schedule_start)
                .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickLeftButton(NormalAlertDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(NormalAlertDialog dialog, View view) {
                        XlinkAgent.getInstance().stop();
                        MobclickAgent.onProfileSignOff();
                        Hawk.delete("MY_PASSWORD");
                        Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        MainActivity.instance.finish();
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }


    @OnClick(R.id.user_avatar)
    void onAvatar() {
        new AlertView("上传头像", null, "取消", null,
                new String[]{"拍照", "从相册中选择"},
                this, AlertView.Style.ActionSheet, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                switch (position) {
                    case 0:
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 222);
                            return;
                        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
                            return;
                        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 222);
                            return;
                        } else {
                            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(SDPathUtils.getCachePath(), "temp.jpg")));
                                startActivityForResult(openCameraIntent, 2);
                            } else {
                                Uri imageUri = FileProvider.getUriForFile(PersonalActivity.this, "com.nuowei.smarthome.fileprovider", new File(SDPathUtils.getCachePath(), "nuoweia.jpg"));
                                openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(openCameraIntent, 2);
                            }
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, 1);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    private PersonalActivity getActivity() {
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            startPhotoZoom(data.getData());
        } else if (requestCode == 2) {
            File temp = new File(SDPathUtils.getCachePath(), "nuoweia.jpg");
            startPhotoZoom(Uri.fromFile(temp));
        } else if (requestCode == 3) {
            if (data != null) {
                setPicToView(data);
            }
        }
    }

    private String localImg;

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bitmap bitmap = null;
        byte[] bis = picdata.getByteArrayExtra("bitmap");
        bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
        localImg = System.currentTimeMillis() + ".JPEG";

        if (bitmap != null) {

            SDPathUtils.saveBitmap(bitmap, localImg);
            Log.e("本地图片绑定", SDPathUtils.getCachePath() + localImg);
//            setImageUrl(ivHeadLogo, "file:/" + SDPathUtils.getCachePath() + localImg, R.mipmap.head_logo);
            bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
            HttpManage.getInstance().uploadHeadportrait(MyApplication.getMyApplication(), bitmap, new HttpManage.ResultCallback<String>() {

                @Override
                public void onError(Header[] headers, HttpManage.Error error) {
                    ToastUtils.showShortToast(MyApplication.getMyApplication(), error.getMsg() + error.getCode());
                }

                @Override
                public void onSuccess(int code, String response) {
                    try {
                        MyApplication.getLogger().json(response);
                        JSONObject jsonObject = new JSONObject(response);
                        String Url = jsonObject.getString("url");
                        MyApplication.getLogger().i(Url);
                        MyApplication.getMyApplication().getUserInfo().setAvatar(Url);
                        Glide.with(PersonalActivity.this).load(MyApplication.getMyApplication().getUserInfo().getAvatar())
                                .centerCrop()
                                .dontAnimate()
                                .priority(Priority.NORMAL)
                                .placeholder(R.drawable.log_icon)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(userAvatar);
                        MyApplication.getMyApplication().sendBroad(Constants.CHANGE_URL, Url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent(getActivity(), PreviewActivity.class);
        intent.setDataAndType(uri, "image/*");
        startActivityForResult(intent, 3);
    }


    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implemen
        MyApplication.getLogger().i("点击返回");
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }
}
