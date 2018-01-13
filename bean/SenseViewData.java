package com.wit.smartcar.bean;

import com.wit.smartcar.R;

/**
 * Created by wnw on 2016/9/21.
 */
public class SenseViewData {

    public static int bgColor[] = new int[]{
            R.color.colorGroupFirst,
            R.color.colorGroupFive,
            R.color.colorGroupSecond,
            R.color.colorGroupThird,
            R.color.colorGroupFour,
            R.color.colorGroupSix,
            R.color.colorGroupSeven
    };


    public static String dataName[] = new String[]{
            "左红外","左寻迹","右寻迹","右红外",
            "左轮PWM","舵机",  "距离","右轮PWM",
            "左轮速度","温度","湿度","右轮速度",
            "轮速比例","工作模式","航向","GPS速度",
            "角度X","角度Y","角度Z","经度",
            "角速度X","角速度Y","角速度Z","纬度",
            "加速度X","加速度Y","加速度Z", "高度"
    };
}
