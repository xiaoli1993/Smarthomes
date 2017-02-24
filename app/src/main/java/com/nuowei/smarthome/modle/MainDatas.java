package com.nuowei.smarthome.modle;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import org.litepal.crud.DataSupport;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/2/23 09:27
 * @Description :
 */
public class MainDatas extends DataSupport {
    private String MainString;
    private int MainType;
    private int Mainsort;


    public MainDatas(String MainString, int MainType, int Mainsort) {
        this.MainString = MainString;
        this.MainType = MainType;
        this.Mainsort = Mainsort;
    }

    public int getMainsort() {
        return Mainsort;
    }

    public void setMainsort(int mainsort) {
        Mainsort = mainsort;
    }

    public String getMainString() {
        return MainString;
    }

    public void setMainString(String mainString) {
        MainString = mainString;
    }

    public int getMainType() {
        return MainType;
    }

    public void setMainType(int mainType) {
        MainType = mainType;
    }
}
