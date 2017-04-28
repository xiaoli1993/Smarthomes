package com.nuowei.smarthome.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badoualy.stepperindicator.StepperIndicator;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.view.textview.AvenirTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author : 肖力
 * @Time :  2017/4/26 15:34
 * @Description :
 * @Modify record :
 */

public class GuideWifiDeviceActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    AvenirTextView tvTitle;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    @BindView(R.id.tv_right)
    AvenirTextView tvRight;
    @BindView(R.id.tb_toolbar)
    RelativeLayout tbToolbar;
    @BindView(R.id.stepper_indicator)
    StepperIndicator stepperIndicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_device);
        assert viewPager != null;
        viewPager.setAdapter(new EmptyPagerAdapter(getSupportFragmentManager()));

        assert stepperIndicator != null;
        // We keep last page for a "finishing" page
        stepperIndicator.setViewPager(viewPager, 1);
        stepperIndicator.addOnStepClickListener(new StepperIndicator.OnStepClickListener() {
            @Override
            public void onStepClicked(int step) {
                viewPager.setCurrentItem(step, true);
            }
        });
    }

    public static class PageFragment extends Fragment {

        private TextView lblPage;

        public static PageFragment newInstance(int page, boolean isLast) {
            Bundle args = new Bundle();
            args.putInt("page", page);
            if (isLast)
                args.putBoolean("isLast", true);
            final PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_guide_wifi_device, container, false);
            lblPage = (TextView) view.findViewById(R.id.tv_si);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            final int page = getArguments().getInt("page", 0);
            if (getArguments().containsKey("isLast"))
                lblPage.setText("You're done!");
            else
                lblPage.setText(Integer.toString(page));
        }
    }

    private static class EmptyPagerAdapter extends FragmentPagerAdapter {

        public EmptyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position + 1, position == getCount() - 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    @OnClick(R.id.image_btn_backs)
    public void onViewClicked() {
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
        this.finish();
    }
}
