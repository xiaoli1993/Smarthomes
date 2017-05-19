package com.jwkj.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;


/**
 * Created by wzy on 2016/10/11.
 */

public class DismissTextWatcher<T extends View> implements TextWatcher {
    private T view;

    T getView() {
        return view;
    }


    void setView(T view) {
        this.view = view;
    }

    public DismissTextWatcher(T view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(view!=null&&view.getVisibility()==View.VISIBLE){
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
