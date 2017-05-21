package com.p2p.core.network;

import android.util.Log;

import com.p2p.core.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by WXY on 2016/4/2.
 */
public class CheckPhoneVKeyResult {

    /**
     * error_code : 0
     * ID : -2145124704
     * VKey : d6a0d7a8b38f1371333c88c0377959606541459596276804345
     */

    private String error_code;
    private String ID;
    private String VKey;

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setVKey(String VKey) {
        this.VKey = VKey;
    }

    public String getError_code() {
        return error_code;
    }

    public String getID() {
        return ID;
    }

    public String getVKey() {
        return VKey;
    }
    public CheckPhoneVKeyResult(JSONObject json) {
        init(json);
    }
    private void init(JSONObject json){
        try {
            error_code=json.getString("error_code");
            ID=json.getString("error_code");
            VKey=json.getString("error_code");
        } catch (JSONException e) {
            ID="";
            VKey="";
            if(!MyUtils.isNumeric(error_code)){
                Log.e("my", "LoginResult json解析错误");
                error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
            }
        }
    }
}
