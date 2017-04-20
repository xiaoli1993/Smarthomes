package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.adapter.PersonalAdapter;
import com.nuowei.smarthome.modle.Personal;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.scrollview.PullScrollView;
import com.nuowei.smarthome.view.textview.AvenirTextView;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initData();
        initEven();
    }

    private void initEven() {
        tbToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        tvRight.setVisibility(View.GONE);
        btnRight.setVisibility(View.GONE);
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

                        break;
                    case 2:

                        break;
                }
            }
        });
    }

    private void initData() {
        if (MyUtil.isEmptyString(MyApplication.getMyApplication().getUserInfo().getEmail())) {
            personalList.add(new Personal(R.drawable.personal_phone, getString(R.string.Phone), MyApplication.getMyApplication().getUserInfo().getPhone()));
        } else {
            personalList.add(new Personal(R.drawable.personal_email, getString(R.string.Email), MyApplication.getMyApplication().getUserInfo().getEmail()));
        }
        personalList.add(new Personal(R.drawable.personal_gw, getString(R.string.Choice_Gateway), ""));
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

    @OnClick(R.id.image_btn_backs)
    void onImageBtnBacksClick() {
        //TODO implemen
        MyApplication.getLogger().i("点击返回");
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }
}
