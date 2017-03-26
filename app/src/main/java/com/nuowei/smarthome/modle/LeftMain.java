package com.nuowei.smarthome.modle;

public class LeftMain{

	private int imageView;
	private String text;
	
	public LeftMain(int imageView, String text) {
		super();
		this.imageView = imageView;
		this.text = text;
	}

	public int getImageView() {
		return imageView;
	}

	public void setImageView(int imageView) {
		this.imageView = imageView;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
