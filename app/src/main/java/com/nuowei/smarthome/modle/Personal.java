package com.nuowei.smarthome.modle;

/**
 * @Author : 肖力
 * @Time :  2017/4/20 15:52
 * @Description :
 * @Modify record :
 */

public class Personal {

    private int image;
    private String name;
    private String state;

    public Personal(int image, String name, String state) {
        this.image = image;
        this.name = name;
        this.state = state;
    }



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
