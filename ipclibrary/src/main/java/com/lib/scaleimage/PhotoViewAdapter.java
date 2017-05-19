package com.lib.scaleimage;



import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lib.scaleimage.PhotoView;
import com.lib.scaleimage.PhotoViewListener;

import java.util.List;

/**
 * Created by USER on 2016/5/19.
 */
public class PhotoViewAdapter extends PagerAdapter {
    private List<String> imgPath ;
    private Context mContext ;
    PhotoViewListener photoViewListener ;
    public PhotoViewAdapter(Context context, List<String> resources){
        imgPath = resources;
        mContext = context;
    }
    @Override
    public int getCount() {
        return imgPath.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    public void setPhotoClicked(PhotoViewListener photoViewListener){
        this.photoViewListener = photoViewListener;
    };
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoView iv = new PhotoView(mContext);
        iv.setPhotoClicked(photoViewListener);
        iv.setUrlFromFile(imgPath.get(position));
        Log.e("touch", imgPath.get(position).toString());
        iv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(iv, 0);
        return iv;
    }

                    @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                        ((ViewGroup) container).removeView((View) object);

                        object=null;
                    }

}
