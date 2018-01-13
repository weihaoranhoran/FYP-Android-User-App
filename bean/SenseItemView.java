package com.wit.smartcar.bean;

import android.widget.TextView;

/**
 * Created by wnw on 2016/9/20.
 */
public class SenseItemView {
    private String titleText;
    private String numberText;

    public SenseItemView(){}

    public SenseItemView(String titleText, String numberText){
        this.titleText = titleText;
        this.numberText = numberText;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getNumberText() {
        return numberText;
    }

    public void setNumberText(String numberText) {
        this.numberText = numberText;
    }
}
