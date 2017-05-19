package com.nuowei.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.activity.DiaryActivity;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Diary2Adapter extends BaseAdapter {

    private List<DataDevice> dataDevices;

    private Context context;
    private LayoutInflater layoutInflater;

    public Diary2Adapter(Context context, List<DataDevice> dataDevices) {
        this.context = context;
        this.dataDevices = dataDevices;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataDevices.size();
    }

    @Override
    public DataDevice getItem(int position) {
        return dataDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(DataDevice dataDevice, ViewHolder holder) {
        //TODO implement
        String sAgeFormatString = MyApplication.getMyApplication().getResources().getString(MyUtil.getBodyString(dataDevice.getBodyLocKey()));
        holder.imageIcon.setImageResource(MyUtil.getImageDiary(context, dataDevice.getSubType(), sAgeFormatString));
        String Content = String.format(sAgeFormatString, "");
        MyApplication.getLogger().i("Content:" + Content + "原来:" + dataDevice.getBodyLocKey() + dataDevice.getActionName());
        holder.tvTimer.setText(dataDevice.getHH() + ":" + dataDevice.getMm());
        if (DiaryActivity.diaryActivity.isgw == 2) {
            holder.tvTxt.setText(dataDevice.getActionName());
            holder.tvState.setText(Content);
        } else {
            holder.tvTxt.setText(Content);
            holder.tvState.setText("Here is the message content");
        }

    }

    static class ViewHolder {
        @BindView(R.id.image_icon)
        ImageView imageIcon;
        @BindView(R.id.tv_txt)
        DinProTextView tvTxt;
        @BindView(R.id.tv_state)
        DinProTextView tvState;
        @BindView(R.id.tv_timer)
        DinProTextView tvTimer;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
