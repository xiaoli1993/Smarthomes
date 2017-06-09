package com.jwkj.listener;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jwkj.utils.Utils;

import java.io.File;

/**
 * Created by dxs on 2015/9/11.
 */
public class CopyImgaShootTask extends AsyncTask<String,Void,Integer> {
    private Handler handler;
    Message msg=new Message();
    private int prepoint=0;
    public CopyImgaShootTask(Handler handler,int prepoint) {
        this.handler = handler;
        this.prepoint=prepoint;
    }

    @Override
    protected Integer doInBackground(String... params) {
        return Utils.copyfile(new File(params[0]),new File(params[1]),true,false);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        msg.what=integer;
        msg.arg1=prepoint;
        handler.sendMessage(msg);
    }

    @Override
    protected void onCancelled() {
        msg.what=-1;
        msg.arg1=prepoint;
        handler.sendMessage(msg);
    }
}
