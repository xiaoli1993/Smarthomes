package com.jwkj.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jwkj.activity.ImageSeeActivity;
import com.jwkj.fragment.UtilsFrag;
import com.jwkj.utils.T;
import com.jwkj.widget.ConfirmOrCancelDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nuowei.ipclibrary.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;


/**
 * create by wzy 2016/9/22
 */
public class ImageBrowserAdapter extends ImageBaseAdapter {
    Context context;
    String[] s = new String[8];
    UtilsFrag ufFrag;
    File[] files;
    boolean isScrolling;
    //List<ChatMsg> imgMsgs = new ArrayList<ChatMsg>();
    private ImageLoader imageLoader;
    List<ScreenShotImage> shots = new ArrayList<ScreenShotImage>();
    List<ScreenShotImage> selShots = new ArrayList<ScreenShotImage>();
    boolean isSelecteMode = false;// true为多选删除模式，false为单击查看模式
    ConfirmOrCancelDialog deleteDialog;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(R.drawable.default_system_msg_img)
//            .showImageForEmptyUri(R.drawable.default_system_msg_img) // image连接地址为空时
//            .showImageOnFail(R.drawable.default_system_msg_img) // image加载失败
            .cacheInMemory(false) // 加载图片时会在内存中加载缓存
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new SimpleBitmapDisplayer())
            .build();

    public ImageBrowserAdapter(Context context, UtilsFrag ufFrag) {
        this.context = context;
        this.ufFrag = ufFrag;
        //imgMsgs = DbUtil.getImgMsg();
        imageLoader = ImageLoader.getInstance();
        initShots();
        if (shots.size() > 0) {
            ufFrag.hideNoPicture();
        } else if (shots.size() == 0) {
            ufFrag.hideSelBtn();
            ufFrag.showNoPicture();
        }
    }


    public boolean isScrolling() {
        return isScrolling;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    @Override
    public int getCount() {
        return shots.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int arg0, View countview, ViewGroup arg2) {
        ViewHolder viewholder;
        if (null == countview) {
            countview = LayoutInflater.from(context).inflate(
                    R.layout.list_imgbrowser_item, null);
            viewholder = new ViewHolder();
            viewholder.cutImage = (ImageView) countview
                    .findViewById(R.id.img);
            viewholder.Checked = (ImageView) countview
                    .findViewById(R.id.iv_checked);
            countview.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) countview.getTag();
        }
        final ScreenShotImage shot = shots.get(arg0);

        // imageLoader.displayImage("file://" + shot.info, viewholder.cutImage);
        imageLoader.displayImage("file://" + shot.file.getPath(), viewholder.cutImage, options);
//        getInfo(shot.info);
//        viewholder.txID.setText(String.format("ID: %s  %s.%s.%s  %s:%s", s[0],
//                s[1], s[2], s[3], s[4], s[5], s[6]));
        if (isSelecteMode) {
            viewholder.Checked.setVisibility(View.VISIBLE);
        } else {
            viewholder.Checked.setVisibility(View.GONE);
        }
        if (shot.isSelecte) {
            viewholder.Checked.setImageResource(R.drawable.chatimgchecked);
            viewholder.Checked.setVisibility(View.VISIBLE);
        } else {
            //viewholder.cutImage.clearColorFilter();
            //viewholder.Checked.setVisibility(View.GONE);
            viewholder.Checked.setImageResource(R.drawable.chatimgnotchecked);
        }

        viewholder.cutImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!isSelecteMode) {
                    Intent intent = new Intent();
                    intent.setClass(context, ImageSeeActivity.class);
                    intent.putExtra("currentImage", arg0);
                    context.startActivity(intent);
                } else {
                    // listener.onImageCheckedorCancel();
                    shot.isSelecte = !shot.isSelecte;
                    if (shot.isSelecte) {
                        selShots.add(shot);
                    } else {
                        selShots.remove(shot);
                    }
                    notifyDataSetChanged();
                }
            }
        });

        viewholder.cutImage.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                if (!isSelecteMode) {
                    ufFrag.showSelModeView();
                    isSelecteMode = true;
                    shot.isSelecte = true;
                    notifyDataSetChanged();
                    selShots.add(shot);
                }
                return true;
            }
        });
        Log.e("my", Runtime.getRuntime().totalMemory() + "");
        return countview;
    }

    static class ViewHolder {
        public ImageView cutImage;
        public ImageView Checked;
    }

    void getInfo(String str) {
        List<String> ss = new ArrayList<String>();
        int i = 0, j = 0;
        String split1 = "/";
        String split2 = "_- .";
        StringTokenizer token = new StringTokenizer(str, split1);
        while (token.hasMoreTokens()) {
            if (i < s.length) {
                ss.add(token.nextToken());
            }
        }

        String strinfo = ss.get(ss.size() - 1);
        Log.i("di", strinfo);
        StringTokenizer token2 = new StringTokenizer(strinfo, split2);
        while (token2.hasMoreTokens()) {
            if (j < s.length) {
                s[j++] = token2.nextToken();
            }
        }
    }

    private void initShots() {
        ScreenShotImage shot;
        shots.clear();
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/screenshot";
        File file = new File(path);
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                // TODO Auto-generated method stub
                if (pathname.getName().endsWith(".jpg")) {
                    return true;
                } else {
                    return false;
                }

            }
        };
        try {
            files = file.listFiles(filter);
        } catch (Exception e) {

        }
        if (null == files) {
            files = new File[0];
        }
        fileReverse();
        for (int i = 0; i < files.length; i++) {
            shot = new ScreenShotImage();
            shot.file = files[i];
            shot.isSelecte = false;
            shots.add(shot);
        }

    }


    /*
     * 取消选择
     */
    public void deleteCancel() {
        for (ScreenShotImage shot : shots) {
            shot.isSelecte = false;
        }
        isSelecteMode = false;
        notifyDataSetChanged();
    }

    public void selecteAll() {
        isSelecteMode = true;
        selShots.addAll(shots);
        for (ScreenShotImage shot : shots) {
            shot.isSelecte = true;
        }
        // notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    /*
     * 反全选
     */
    public void cancelSelectAll() {
        selShots.clear();
        for (ScreenShotImage shot : shots) {
            shot.isSelecte = false;
        }
        notifyDataSetChanged();
    }

    /*
     * 退出选择模式
     */
    public void existSelectMode() {
        selShots.clear();
        isSelecteMode = false;
        ufFrag.hideSelModeView();
        for (ScreenShotImage shot : shots) {
            shot.isSelecte = false;
        }
        notifyDataSetChanged();
    }

    /**
     * 进入选择模式
     */
    public void setSelMode() {
        isSelecteMode = true;
        for (ScreenShotImage shot : shots) {
            shot.isSelecte = false;
        }
        notifyDataSetChanged();

    }

    public class ScreenShotImage {
        //public ChatMsg msg;
        File file;
        public boolean isSelecte = false;
    }


    public void showDelDialog() {
        if (selShots.size() == 0) {
            T.showShort(context, R.string.choose_one_at_least);
            return;
        }
        deleteDialog = new ConfirmOrCancelDialog(context);
        deleteDialog.setTitle(R.string.confirm_delete);
        deleteDialog.setOnNoClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog.setOnYesClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                shots.removeAll(selShots);
                for (ScreenShotImage shot : selShots) {
                    shot.file.delete();
                }
                selShots.clear();
                isSelecteMode = false;
                notifyDataSetChanged();
                if (shots.size() == 0) {
                    ufFrag.showNoPicture();
                    ufFrag.hideSelBtn();
                    ufFrag.hideCancelBtn();
                    ufFrag.hideDelRel();
                    existSelectMode();
                }
            }
        });
        deleteDialog.show();
    }


    public void fileReverse() {

        if (files == null && files.length <= 0) {
            return;
        }
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File arg0, File arg1) {
                // TODO Auto-generated method stub
                long diff = arg0.lastModified() - arg1.lastModified();
                if (diff > 0)
                    return -1;
                else if (diff == 0)
                    return 0;
                else
                    return 1;
            }

            @Override
            public boolean equals(Object o) {
                // TODO Auto-generated method stub
                return true;
            }

        });
    }


}
