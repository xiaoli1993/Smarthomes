package com.jwkj.entity;

/**
 * Created by dxs on 2015/9/9.
 */
public class OnePrepoint implements Comparable<OnePrepoint>{
    public String imagePath;
    public String name;
    public int prepoint;
    public boolean isSelected=false;

    public OnePrepoint() {
    }

    public OnePrepoint(String imagePath, String name, int prepoint, boolean isSelected) {
        this.imagePath = imagePath;
        this.name = name;
        this.prepoint = prepoint;
        this.isSelected = isSelected;
    }
    
    public Integer getPrepoint(){
    	return prepoint;
    }

	@Override
	public int compareTo(OnePrepoint another) {
		return getPrepoint().compareTo(another.getPrepoint());
	}
}
