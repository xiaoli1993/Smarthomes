package com.p2p.core.network;

import com.p2p.core.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class GetAccountByPhoneNOResult {
	private String error_code;
	private String ID;
	private String VKey;
	private String CountryCode;
	private String PhoneNO;

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getVKey() {
		return VKey;
	}

	public void setVKey(String vKey) {
		VKey = vKey;
	}

	public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String countryCode) {
		CountryCode = countryCode;
	}

	public String getPhoneNO() {
		return PhoneNO;
	}

	public void setPhoneNO(String phoneNO) {
		PhoneNO = phoneNO;
	}

	public GetAccountByPhoneNOResult(JSONObject json) {
		init(json);
	}

	private void init(JSONObject json) {
		try {
			error_code = json.getString("error_code");
			ID = json.getString("ID");
			VKey = json.getString("VKey");
			CountryCode = json.getString("CountryCode");
			PhoneNO = json.getString("PhoneNO");
		} catch (JSONException e) {
			ID = "";
			VKey = "";
			CountryCode = "";
			PhoneNO = "";
			if (!MyUtils.isNumeric(error_code)) {
//				Log.e("my", "LoginResult json解析错误");
				error_code = String.valueOf(NetManager.JSON_PARSE_ERROR);
			}
		}
	}
}
