package com.nuowei.smarthome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.p2p.core.network.LoginResult;
import com.p2p.core.network.NetManager;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/1/9 13:33
 * @Description :主程序
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginResult loginResult = NetManager.getInstance(this).createLoginResult(NetManager.getInstance(this).login("554674787@qq.com", "8888"));
        MyApplication.getLogger().i(loginResult.contactId + "\n" + loginResult.error_code);
    }
}
