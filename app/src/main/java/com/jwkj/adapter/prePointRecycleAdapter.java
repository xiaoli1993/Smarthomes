package com.jwkj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jwkj.activity.ApMonitorActivity;
import com.jwkj.data.DataManager;
import com.jwkj.data.Prepoint;
import com.jwkj.entity.OnePrepoint;
import com.jwkj.fragment.MonitorThreeFrag;
import com.jwkj.utils.T;
import com.jwkj.utils.Utils;
import com.jwkj.widget.RememberPointImagView;
import com.jwkj.widget.prepointPopwindow;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nuowei.smarthome.MyApplication;
import com.nuowei.smarthome.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dxs on 2015/9/8.
 */
public class prePointRecycleAdapter extends RecyclerView.Adapter<prePointRecycleAdapter.ViewHolder> {
    private List<OnePrepoint> prePoints=new ArrayList<OnePrepoint>();
    private List<String> deletePaths = new ArrayList<String>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    public boolean isSelectedMode=false;
    private String deviceId;
    private Context context;
    private int selectePoints=0;
    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.remember_defalt) // 设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.drawable.remember_defalt)// 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.remember_defalt) // 设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                    // .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                     .bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型//
                    // .delayBeforeLoading(int delayInMillis)//int
                    // delayInMillis为你设置的下载前的延迟时间
                    // 设置图片加入缓存前，对bitmap进行设置
                    // .preProcessor(BitmapProcessor preProcessor)
            .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
            //.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                    // .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
            .build();// 构建完成

    private static final int imageW= MyApplication.SCREENWIGHT/ prepointPopwindow.GridCouluns-80;
    private static final int imageH=imageW*9/16;

    public prePointRecycleAdapter(Context context,String deviceId){
        super();
        this.context=context;
        this.deviceId=deviceId;
        PrepointsRefrush();
    }
    @Override
    public prePointRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_recycle_remember, null);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final prePointRecycleAdapter.ViewHolder holder, final int position) {
        final OnePrepoint point=prePoints.get(position);
        if(holder.getPosition()>MonitorThreeFrag.PREPOINTCOUNTS){
            holder.ivImage.setVisibility(View.GONE);
        }else if(point.prepoint==MonitorThreeFrag.PREPOINTCOUNTS){
            holder.ivImage.setImageResource(R.drawable.prepoint_add);
        }else{
            imageLoader.displayImage("file://" + point.imagePath, holder.ivImage, options);
        }
        if(point.isSelected){
//            holder.ivImage.setColorFilter(R.color.half_alpha,
//                    PorterDuff.Mode.DARKEN);
//            holder.ivImage.setAlpha((float) 0.3);
            holder.ivSelected.setVisibility(View.VISIBLE);
        }else{
//            holder.ivImage.clearColorFilter();
//            holder.ivImage.setAlpha((float) 1.0);
            holder.ivSelected.setVisibility(View.GONE);
        }

        
        holder.ivImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                itemClickLisener.onClick(v,holder.getPosition());
                if(isSelectedMode){
                    //选择模式
                    if(point.prepoint!=MonitorThreeFrag.PREPOINTCOUNTS){
                        //选择
                        if(point.isSelected){
                            //取消选择
                            point.isSelected=false;
                        }else{
                            //选择
                            point.isSelected=true;
                        }
                        prepointLisener.selectedPrepoints(getSeletedPrepoint(),deletePaths, selectePoints);
                    }else{
                        T.showLong(context,R.string.prepoint_cannottoadd);
                    }
                    notifyItemChanged(position);
                }else{
                    //单击模式
                    if(point.prepoint==MonitorThreeFrag.PREPOINTCOUNTS){
                        //添加预置位
                        prepointLisener.addPrepoint(getNextPrepoint());
                    } else {
                        //查看预置位
                        prepointLisener.toSeePrepoint(point);
                    }
                }
            }
        });

        holder.ivImage.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (point.prepoint != MonitorThreeFrag.PREPOINTCOUNTS) {
                    if (!isSelectedMode) {
                        isSelectedMode = true;
                        point.isSelected = true;
                        Utils.PhoneVibrator(context);
                        notifyItemChanged(position);
                        prepointLisener.selectedPrepoints(getSeletedPrepoint(), deletePaths, selectePoints);
                    }
                    itemLongClickLisener.onLongClick(v, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return prePoints.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RememberPointImagView ivImage;
        public RememberPointImagView ivSelected;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = (RememberPointImagView) itemView.findViewById(R.id.iv_sreenshot_monitor);
            ivImage.setLayoutParams(new RelativeLayout.LayoutParams(imageW, imageW));
            ivSelected= (RememberPointImagView) itemView.findViewById(R.id.iv_selected);
            ivSelected.setLayoutParams(new RelativeLayout.LayoutParams(imageW, imageW));
        }
    }

    /**
     * 清除预置位所有选中状态
     */
    public void ClearPoints(){
        isSelectedMode=false;
        for (OnePrepoint points:prePoints
             ) {
            points.isSelected=false;
        }
        notifyDataSetChanged();
    }

    public void reFrushName(int position,String newName){
        prePoints.get(position).name=newName;
        prePoints.get(position).isSelected=false;
        isSelectedMode=false;
        notifyItemChanged(position);
    }

    /**
     * 获取下一个可设置的预置位
     * @return 下一个可设置预置位编号
     */
    private int getNextPrepoint(){
        int[] prepointTemp = new int[]{0, 1, 2, 3, 4};
        for (OnePrepoint prepoint : prePoints) {
            if(prepoint.prepoint!=MonitorThreeFrag.PREPOINTCOUNTS){
                prepointTemp[prepoint.prepoint]=-1;
            }
        }
        for (int i=0;i<prepointTemp.length;i++){
            if(prepointTemp[i]!=-1){
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取已经存在的预置位
     */
    public void PrepointsRefrush() {
        this.prePoints = getPrePoint(deviceId, ParsePrepointInfo(ApMonitorActivity.PrePointInfo));
    }

    private List<OnePrepoint> getPrePoint(String deviceId, int[] devicePoints) {
        List<OnePrepoint> OnePrePoints = new ArrayList<OnePrepoint>();
        Prepoint points = DataManager.findPrepointByDevice(context, deviceId);
        OnePrepoint point;
        for (int i = 0; i < MonitorThreeFrag.PREPOINTCOUNTS; i++) {
            if (devicePoints[i] == 1) {
                point = new OnePrepoint();
                point.prepoint = i;
                point.imagePath = Utils.getPrepointPath(deviceId, i);
                point.name = points.getName(i);
                OnePrePoints.add(point);
            }
        }
        if(!(OnePrePoints.size()>=MonitorThreeFrag.PREPOINTCOUNTS)){
            OnePrePoints.add(new OnePrepoint("", "", MonitorThreeFrag.PREPOINTCOUNTS, false));
        }
        return OnePrePoints;
    }

    /**
     * 是否已设置的预置位
     * @param prepoint 被检测的预置位
     * @return
     */
    private boolean isLocalPrepoint(int prepoint){
        for (OnePrepoint poin:prePoints
             ) {
            if(poin.prepoint==prepoint){
                return true;
            }
        }
        return false;
    }

    /**
     * 添加一个预置位,如果已经存在则更新数据
     * @param position 待添加的预置位
     */
    public void addPrepoint(int position){
        imageLoader.clearMemoryCache();
        if(isLocalPrepoint(position)){
            notifyItemChanged(getPrepointBuypoint(position));
            return;
        }
        Prepoint points = DataManager.findPrepointByDevice(context, deviceId);
        OnePrepoint point = new OnePrepoint();
        point.prepoint = position;
        point.imagePath = Utils.getPrepointPath(deviceId, position);
        point.name = points.getName(position);
        this.prePoints.add(position, point);
        notifyItemInserted(position);
        if(prePoints.size()>MonitorThreeFrag.PREPOINTCOUNTS){
            prePoints.remove(prePoints.size() - 1);
            notifyItemRemoved(prePoints.size());
        }
        if(position!=prePoints.size()-1){
            notifyItemRangeChanged(position,prePoints.size()-position);
        }
        Log.i("dxsprepoint","add之后列表大小-->"+prePoints.size());
        T.showShort(context, R.string.set_wifi_success);
    }

    /**
     * 删除几个预置位
     * @param info
     */
    public void deletePrepoint(byte info){
        int[] temp=ParsePrepointInfo(info);
        for (int i=0;i<MonitorThreeFrag.PREPOINTCOUNTS;i++){
            if(temp[i]==1){
                DataManager.upDataPrepointByCount(context, deviceId, i);
                int s=getPrepointBuypoint(i);
                if(s!=-1){
                    this.prePoints.remove(s);
                    notifyItemRemoved(s);
                    Log.e("dxsprepoint", "delete--------->" + s);
                }
            }
        }
        isSelectedMode=false;
        if(prePoints.size()==0||prePoints.get(prePoints.size()-1).prepoint!=MonitorThreeFrag.PREPOINTCOUNTS){
            prePoints.add(prePoints.size(),new OnePrepoint("", "", MonitorThreeFrag.PREPOINTCOUNTS, false));
            notifyItemInserted(prePoints.size());
        }
        notifyDataSetChanged();
    }

    public List<OnePrepoint> getSeletedPrepoint(){
        List<OnePrepoint> SeletedPrePoint=new ArrayList<OnePrepoint>();
        deletePaths.clear();
        selectePoints=0;
        for (OnePrepoint po: prePoints
             ) {
            if(po.isSelected){
                SeletedPrePoint.add(po);
                deletePaths.add(po.imagePath);
                selectePoints=select(selectePoints,po.prepoint);
            }
        }
        return SeletedPrePoint;
    }

    /**
     * 根据预置位标记获得在布局中的位置
     * @param i
     * @return
     */
    private int getPrepointBuypoint(int i){
        int temp=0;
        for (OnePrepoint p:prePoints
             ) {
            if(p.prepoint==i){
                return temp;
            }
            temp++;
        }
        return -1;
    }

    /**
     * 解析预置位信息
     * @param prepointInfo
     * @return
     */
    public int[] ParsePrepointInfo(byte prepointInfo) {
        return Utils.getByteBinnery(prepointInfo, true);
    }

    /**指定某位二进制为1，其他位为0，即设置为预置位
     * @param src
     * @param position
     * @return
     */
    private int select(int src,int position){
        return src|(1<<position);
    }


    private onItemClickLisener itemClickLisener;
    private onItemLongClickLisener itemLongClickLisener;
    private onPrepointListner prepointLisener;
    public void setOnItemClickLisener(onItemClickLisener listener){
        itemClickLisener=listener;
    }
    public void setOnItemLongClickLisener(onItemLongClickLisener listener){
        itemLongClickLisener=listener;
    }

    public void setOnPrepointListner(onPrepointListner prepointLisener){
        this.prepointLisener=prepointLisener;
    }

    public interface onItemClickLisener{
        void onClick(View v,int position);
    }
    public interface onItemLongClickLisener{
        void onLongClick(View v,int position);
    }

    public interface onPrepointListner{
        void addPrepoint(int prepoint);
        void selectedPrepoint(OnePrepoint point,int position);
        void cancelPrepoint(OnePrepoint point,int position);
        void toSeePrepoint(OnePrepoint point);
        void selectedPrepoints(List<OnePrepoint> Selected,List<String> prepointPath,int SelectedPoins);
    }
}
