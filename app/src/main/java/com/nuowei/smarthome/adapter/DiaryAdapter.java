package com.nuowei.smarthome.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;
import com.nuowei.smarthome.modle.DataDevice;
import com.nuowei.smarthome.util.MyUtil;
import com.nuowei.smarthome.view.textview.DinProTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author : 肖力
 * @Time :  2017/4/14 15:49
 * @Description :
 * @Modify record :
 */

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.MyViewHolder> {
    private Context context;
    private int src;
    private List<DataDevice> results;

    public DiaryAdapter(int src, List<DataDevice> results) {
        this.results = results;
        this.src = src;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(src, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        results.get(position).getBodyLocKey();
        String sAgeFormatString = MyApplication.getMyApplication().getResources().getString(MyUtil.getBodyString(results.get(position).getBodyLocKey()));
        String Content = String.format(sAgeFormatString, "");
        MyApplication.getLogger().i("Content:"+Content);
        holder.tvTxt.setText(Content);
        holder.tvTimer.setText(results.get(position).getHH() + ":" + results.get(position).getMm());
        holder.tvState.setText("Here is the message content");
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_icon)
        ImageView imageIcon;
        @BindView(R.id.tv_txt)
        DinProTextView tvTxt;
        @BindView(R.id.tv_state)
        DinProTextView tvState;
        @BindView(R.id.tv_timer)
        DinProTextView tvTimer;

        public MyViewHolder(View itemView) {
            super(itemView);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = width / 4;
            itemView.setLayoutParams(layoutParams);
            ButterKnife.bind(this, itemView);
        }
    }
}
