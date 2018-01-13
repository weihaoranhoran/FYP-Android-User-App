package com.wit.smartcar.bean;

/**
 * Created by wnw on 2016/9/20.
 */
public class SenseData {

    private String leftInfrared;  //左红外
    private String leftTracing;   //左寻迹
    private String rightTracing;  //右寻迹
    private String rightInfrared; //右红外

    private String leftSpeed;     //左轮速度
    private String servoPWM;     //舵机PWM
    private String distance;      //距离
    private String rightSpeed;    //右轮速度

    private String leftPWM;       //左PWM
    private String temperature;   //温度
    private String humidity;      //湿度
    private String rightPWM;      //右PWM

    private String motion;        //运动状态
    private String wordMode;      //工作模式
    private String course;         //航向
    private String gpsSpeed;       //GPS速度

    private String xAngle;        //角度X
    private String yAngle;        //角度Y
    private String zAngle;        //角度Z
    private String longitude;      //经度

    private String xAngular;       //角速度X
    private String yAngular;       //角速度Y
    private String zAngular;       //角速度Z
    private String latitude;       //纬度

    private String xAcceleration;  //加速度X
    private String yAcceleration;  //加速度Y
    private String zAcceleration;  //加速度Z
    private String height;         //高度

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(String gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getLeftInfrared() {
        return leftInfrared;
    }

    public void setLeftInfrared(String leftInfrared) {
        this.leftInfrared = leftInfrared;
    }

    public String getRightInfrared() {
        return rightInfrared;
    }

    public void setRightInfrared(String rightInfrared) {
        this.rightInfrared = rightInfrared;
    }

    public String getLeftTracing() {
        return leftTracing;
    }

    public void setLeftTracing(String leftTracing) {
        this.leftTracing = leftTracing;
    }

    public String getRightTracing() {
        return rightTracing;
    }

    public void setRightTracing(String rightTracing) {
        this.rightTracing = rightTracing;
    }

    public String getLeftPWM() {
        return leftPWM;
    }

    public void setLeftPWM(String leftPWM) {
        this.leftPWM = leftPWM;
    }

    public String getRightPWM() {
        return rightPWM;
    }

    public void setRightPWM(String rightPWM) {
        this.rightPWM = rightPWM;
    }

    public String getMotion() {
        return motion;
    }

    public void setMotion(String motion) {
        this.motion = motion;
    }

    public String getServoPWM() {
        return servoPWM;
    }

    public void setServoPWM(String servoPWM) {
        this.servoPWM = servoPWM;
    }

    public String getLeftSpeed() {
        return leftSpeed;
    }

    public void setLeftSpeed(String leftSpeed) {
        this.leftSpeed = leftSpeed;
    }

    public String getRightSpeed() {
        return rightSpeed;
    }

    public void setRightSpeed(String rightSpeed) {
        this.rightSpeed = rightSpeed;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getxAngle() {
        return xAngle;
    }

    public void setxAngle(String xAngle) {
        this.xAngle = xAngle;
    }

    public String getyAngle() {
        return yAngle;
    }

    public void setyAngle(String yAngle) {
        this.yAngle = yAngle;
    }

    public String getzAngle() {
        return zAngle;
    }

    public void setzAngle(String zAngle) {
        this.zAngle = zAngle;
    }

    public String getxAcceleration() {
        return xAcceleration;
    }

    public void setxAcceleration(String xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public String getyAcceleration() {
        return yAcceleration;
    }

    public void setyAcceleration(String yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public String getzAcceleration() {
        return zAcceleration;
    }

    public void setzAcceleration(String zAcceleration) {
        this.zAcceleration = zAcceleration;
    }

    public String getxAngular() {
        return xAngular;
    }

    public void setxAngular(String xAngular) {
        this.xAngular = xAngular;
    }

    public String getyAngular() {
        return yAngular;
    }

    public void setyAngular(String yAngular) {
        this.yAngular = yAngular;
    }

    public String getzAngular() {
        return zAngular;
    }

    public void setzAngular(String zAngular) {
        this.zAngular = zAngular;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWordMode() {
        return wordMode;
    }

    public void setWordMode(String wordMode) {
        this.wordMode = wordMode;
    }
}
