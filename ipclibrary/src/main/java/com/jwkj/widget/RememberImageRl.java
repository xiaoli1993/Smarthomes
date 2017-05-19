package com.jwkj.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jwkj.entity.OnePrepoint;
import com.nuowei.ipclibrary.R;

/**
 * Created by dxs on 2015/12/4.
 */
public class RememberImageRl extends RelativeLayout {

    private RememberPointImagView PointImageView;
    private RememberPointImagView SelectedIv;
    private OnePrepoint point;
    private int position;
    private float ImageAlfa=1f;

    private OnRememberImageRlClickListner clickListner;
    private OnRememberImageRlLongClickListner longClickListner;
    private TextView txRememberName;
    public RememberImageRl(Context context) {
        super(context);
        initUI(context);
    }

    public RememberImageRl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public RememberImageRl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context);
    }
    

    private void initUI(Context mCoutext){
        LayoutInflater inflater = (LayoutInflater) mCoutext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_recycle_remember, this);
        PointImageView= (RememberPointImagView) findViewById(R.id.iv_sreenshot_monitor);
        SelectedIv= (RememberPointImagView) findViewById(R.id.iv_selected);
        txRememberName= (TextView) findViewById(R.id.tx_remember_name);
       // txRememberName.setShadowLayer(10F, 11F,5F, Color.BLACK);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setPrePoint(OnePrepoint points,int positions){
        this.point=points;
        this.position=positions;
        //PointImageView.setOnPrepoints(points);
        txRememberName.setText(points.name);
        //TypefaceHelper.typeface(txRememberName);
        PointImageView.setBitmap(point.imagePath);
        //PointImageView.setAlpha(ImageAlfa);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListner.onClick(point,position);
            }
        });
        this.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                longClickListner.onLongClick(point, position);
                return true;
            }
        });
        updateIsSelected(point.isSelected);
    }

    public void updateIsSelected(boolean isSelecteds){
        if(isSelecteds){
            SelectedIv.setVisibility(View.VISIBLE);
        }else{
            SelectedIv.setVisibility(View.GONE);
        }
    }

    public void updateIsSelected(){
        updateIsSelected(point.isSelected);
        PointImageView.setBitmap(point.imagePath);
        txRememberName.setText(point.name);
        //PointImageView.setAlpha(ImageAlfa);
    }
    
    public OnePrepoint getPrepoint(){
        return point;
    }

    public interface OnRememberImageRlClickListner{
        void onClick(OnePrepoint point, int position);
    }
    public interface OnRememberImageRlLongClickListner{
        void onLongClick(OnePrepoint point, int position);
    }

    public void setOnRememberImageRlClickListner(OnRememberImageRlClickListner Listner){
        this.clickListner=Listner;
    }

    public void setOnRememberImageRlLongClickListner(OnRememberImageRlLongClickListner longClickListner){
        this.longClickListner=longClickListner;
    }

}
