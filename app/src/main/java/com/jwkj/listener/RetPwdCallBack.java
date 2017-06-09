package com.jwkj.listener;

import android.os.Bundle;

public interface RetPwdCallBack {
	public void toModPwdByPhoneFrag(Bundle bundle);
	public void toModPwdByEmailFrag();
	public void toRetPwdFrag();
	public void toLoginActivity(String type,String account);

}
