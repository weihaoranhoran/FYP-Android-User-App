package com.wit.smartcar.bean;

/**
 * Created by wnw on 2016/9/3.
 */
public class BlueDeviceBean {
    private String name;
    private String address;

    public BlueDeviceBean(){

    }

    public BlueDeviceBean(String name, String add){
        this.name = name;
        this.address  = add;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
