package com.wit.smartcar.bean;

/**
 * Created by wnw on 2016/9/20.
 */
public class ModeItemView {
    private String text;
    private int resId;

    public ModeItemView(int resId, String text){
        this.resId = resId;
        this.text = text;
    }
    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
