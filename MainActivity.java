package com.wit.smartcar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wit.smartcar.blue.TVIntentFilter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Date;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SensorEventListener {
    //the request code of search bluetooth
    public static int SEARCH_BLUETOOTH_CODE = 1;
    /**
     * 创建一个BluetoothLeService对象
     * */

    public static int MODE_GUSTURE = 0;             //手势模式
    public static int MODE_HAND_CONTROL = 1;        //手控模式
    public static int MODE_TRACING = 2;             //寻迹模式
    public static int MODE_INFRARED = 3;            //红外模式
    public static int MODE_UITRASONIC_WAVE = 4;     //超声波模式
    public static int MODE_MOTION = 5;     //超声波模式

    public static int CURRENT_MODE = MODE_UITRASONIC_WAVE;    //默认是超声波模式

    private static boolean isConnected = false;   //是否已经连接蓝牙

    private static BluetoothLeService mBluetoothLeService;
    private boolean isBindService = false;

    private String mDeviceAddress;


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();      //页卡标题集合
    private View view1, view2, view3;                   //页卡视图
    private List<View> mViewList = new ArrayList<>();         //页卡视图集合

    private LinearLayout bottomBar;    //底部的bottomBar;

    private TabViewMode tabView1;
    private TabViewSense tabView2;
    private TabViewProgrammable tabView3;

    private Button turnLeft;
    private Button turnRight;
    private Button turnUp;
    private Button turnDown;
    private Button brake;
    private Button controlServo;
    private static TextView currentSpeed;
    private static TextView tvSpeedRate;


    private SensorManager sm;
    //需要两个Sensor
    private Sensor aSensor;
    private Sensor mSensor;

    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        registerReceiver(mGattUpdateReceiver, TVIntentFilter.makeGattUpdateIntentFilter());
        startSearchBluetooth();
        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(myListener, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        //更新显示数据的方法
        calculateOrientation();
    }
    final SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues = sensorEvent.values;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = sensorEvent.values;
            calculateOrientation();
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private Date lastDate = new Date(System.currentTimeMillis());
    private  void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        if (iCurrentMode!=MODE_MOTION) return;
        // 要经过一次数据格式的转换，转换为度
        Date curDate = new Date(System.currentTimeMillis());
        if (curDate.getTime() - lastDate.getTime()>200)
        {
            SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(R, values);
            values[0] = (float) Math.toDegrees(values[0]);// azimuth, rotation around the Z axis.
            values[1] = (float) Math.toDegrees(values[1]);//pitch, rotation around the X axis.
            values[2] = (float) Math.toDegrees(values[2]);//roll, rotation around the Y axis.
           // tvSpeedRate.setText(String.format("%.2f,%.2f,%.2f",values[0],values[1],values[2]));

            fSpeed[0] = (values[1]+values[2]/2);
            fSpeed[1] = (values[1]-values[2]/2);
            for (int i=0;i<2;i++) {
                if (fSpeed[i] > 100) fSpeed[i] = 100;
                else if (fSpeed[i] < -100) fSpeed[i] = -100;
            }
            writeModeToBlue(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x07,(byte)fSpeed[0] ,(byte)fSpeed[1]});
            lastDate=curDate;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) return;
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
        }
    }
    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomBar = (LinearLayout)findViewById(R.id.bottom_bar);
        turnLeft = (Button)findViewById(R.id.turn_left);
        turnRight = (Button)findViewById(R.id.turn_right);
        turnUp = (Button)findViewById(R.id.turn_up);
        turnDown = (Button)findViewById(R.id.turn_down);
        brake = (Button)findViewById(R.id.brake);
        controlServo = (Button)findViewById(R.id.servo_control);
        tvSpeedRate = (TextView)findViewById(R.id.speedRate);
        currentSpeed = (TextView)findViewById(R.id.current_speed);

        turnLeft.setOnClickListener(this);
        turnRight.setOnClickListener(this);
        turnUp.setOnClickListener(this);
        turnDown.setOnClickListener(this);
        brake.setOnClickListener(this);
        tvSpeedRate.setOnClickListener(this);
        controlServo.setOnClickListener(this);
        tabLayout = (TabLayout)findViewById(R.id.layout_tab);
        viewPager = (ViewPager)findViewById(R.id.vp_view);

        mInflater = LayoutInflater.from(this);
        tabView1 = new TabViewMode(mInflater);
        tabView2 = new TabViewSense(mInflater);
        //tabView3 = new TabViewProgrammable(mInflater);

        view1 = tabView1.getView();
        view2 = tabView2.getView();
       // view3 = tabView3.getView();

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
       // mViewList.add(view3);

        //添加页卡标题
        mTitleList.add("模式");
        mTitleList.add("传感器");
       // mTitleList.add("程控");

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(1)));
       // tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(2)));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2){
                    bottomBar.setVisibility(View.GONE);
                }else {
                    bottomBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        viewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_device) {
            startSearchBluetooth();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * start search bluetooth;
     * */
    private void startSearchBluetooth(){
        Intent intent = new Intent(this, SearchBluetoothActivity.class);
        startActivityForResult(intent, SEARCH_BLUETOOTH_CODE);
    }

    //get the data from the
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SEARCH_BLUETOOTH_CODE){
            if(resultCode == Activity.RESULT_OK){
                mDeviceAddress = data.getStringExtra(SearchBluetoothActivity.EXTRA_DEVICE_ADDRESS);
                String name = data.getStringExtra(SearchBluetoothActivity.EXTRA_DEVICE_NAME);
               openBluetooth();
            }
        }
    }

    /**
     *  由SearchActivity返回当前MainActivity的时候，就开启蓝牙服务区连接BLE
     *  1. 注册广播和定义广播接收器
     *  2. 声明服务ServiceConnection
     *  3. 绑定服务
     *  4. 调用BluetoothLeService的contect()函数
     *  5. 在onDestroy方法中取消绑定服务，取消注册广播
     * */
    private void openBluetooth(){
        Toast.makeText(MainActivity.this, "正在连接中...",Toast.LENGTH_SHORT).show();

        //这两句就是开启蓝牙连接服务,用绑定的方式开启服务
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        isBindService = true;
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }else{
            Log.d("wnwBTL", "mBluetoothLeService is null");
        }
    }

    /**
     * 连接蓝牙服务
     * */

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * 1. 在这里通过广播的方式去监听返回的连接蓝牙服务的状态
     * 2. 一旦连接成功后，也是从这里去监听数据返回，返回不同的数据做不同的处理
     * 3. 返回的数据可能为：步数和时间间隔，电量，角度，判断的标准是根据：intent的flags标志
     * */
    public static enum Reg{
        MOTERPWM,
        DMPWM,
        VSCALE	,
        IRL	,
        IRR	,
        BWIRL,
        BWIRR,
        VOTAGE,
        ENCODEV,

        ULTRONIC,
        TEMP,
        HUMIDITY,
        AX		,
        AZ		,
        AY		,
        WX		,
        WY		,
        WZ		,

        ANGLE_X	,
        ANGLE_Y		,
        ANGLE_Z	,
        LatL		,
        LatH		,
        LonL	,
        LonH	,
        GPSHeight,
        GPSYAW	,

        SVNUM	,
        GPSVL 	,
        GPSVH 	,
        PDOP 	,
        HDOP 	,
        VDOP 	,
        GPSBAUD ,
        YYMM	,
        DDHH	,

        MMSS	,
        MS		,
        IRLMAX	,
        IRLMIN	,
        IRRMAX ,
        IRRMIN ,
        BWLTHR	,
        BWRTHR	,
        RSV
    }
    String dataList[] = new String[28];

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
               Toast.makeText(MainActivity.this,"连接成功", Toast.LENGTH_SHORT).show();
                isConnected = true;
                tellTab1BlueToothState();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(MainActivity.this,"连接已断开", Toast.LENGTH_SHORT).show();
                isConnected = false;
                tellTab1BlueToothState();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                byte[] packBuffer = intent.getByteArrayExtra("data");
                if (packBuffer[0]!= 0x55) return;

                short data[] = new short[9];
                for (byte i = 0;i<9;i++) data[i] = (short)(((packBuffer[(i<<1)+3] << 8) | (packBuffer[(i<<1)+2] & 0xff)));


     /*           public static String dataName[] = new String[]{
                        0"左红外","左寻迹","右寻迹","右红外",
                        4"左轮速度","舵机",  "距离","右轮速度",
                        8"左轮PWM","温度","湿度","右轮PWM",
                        12"轮速平衡","电压","航向","GPS速度",
                        16"角度X","角度Y","角度Z","经度",
                        20"角速度X","角速度Y","角速度Z","纬度",
                       24 "加速度X","加速度Y","加速度Z", "高度"
                };*/
                try {
                    switch (packBuffer[1]) {
                        case 0x51:
                            dataList[4] = (data[0] >> 8) + "";                            //MOTERPWM
                            dataList[7] = (data[0] & 0xff) + "";                          //
                            dataList[5] = data[1] + "";                                 //DMPWM
                            dataList[0] = String.format("%.2fV", (double)data[3]/1000) ;//IRL
                            dataList[3] = String.format("%.2fV", (double)data[4]/1000) ;//IRR
                            dataList[1] = String.format("%.2fV", (double)data[5]/1000) ;//BWIRL
                            dataList[2] = String.format("%.2fV", (double)data[6]/1000) ;//BWIRR
                            dataList[13] = String.format("%.2fV", (double)data[7]/1000) ;//VOTAGE
                            dataList[8] = (data[8] >> 8) + "";//ENCODEV
                            dataList[11] = (data[8] & 0xff) + "";//ENCODEV
                            dataList[12] = String.format("1:%.2f", (double)data[2] / 1000);//VSCALE
                            tabView2.updateData(dataList);
                            break;
                        case 0x52:
                            dataList[6] = data[0] + "cm";//ULTRPMOC
                            dataList[9] = data[1] + "℃";//Temp
                            dataList[10] = data[2] + "%";//HUMIDITY
                            dataList[24] = String.format("%.1f", (double)data[3]/1000.0) + "g";//AX
                            dataList[25] = String.format("%.1f", (double)data[4]/1000.0) + "g";//AY
                            dataList[26] = String.format("%.1f", (double)data[5]/1000.0) + "g";//AZ
                            dataList[20] = String.format("%.1f", (double)data[6]/10.0) + "°/s";//WX
                            dataList[21] = String.format("%.1f", (double)data[7]/10.0) + "°/s";//WY
                            dataList[22] = String.format("%.1f", (double)data[8]/10.0) + "°/s";//WZ
                            break;
                        case 0x53:
                            dataList[16] = String.format("%.1f", (double)data[0]/10.0) + "°";//ANGLEX
                            dataList[17] = String.format("%.1f", (double)data[1]/10.0) + "°";//ANGLEY
                            dataList[18] = String.format("%.1f", (double)data[2]/10.0) + "°";//ANGELEZ
                            dataList[19] = String.format("%.5f", (double)(((long) data[4] << 16) | data[3]) / 1e6) + "°";//Lon
                            dataList[23] = String.format("%.5f", (double)(((long) data[6] << 16) | data[5]) / 1e6) + "°";//lat
                            dataList[27] = String.format("%.1f", (double)data[7]) + "m";//GPSHeight
                            dataList[14] = String.format("%.1f", (double)data[8]) + "°";//GPSYAW
                            break;
                        case 0x54:
                            dataList[15] = String.format("%.2f", (double)(((long) data[2] << 16) | data[1]) / 1e3) + "km/h";//GPSV
                            break;
                    }
                }
                catch (Exception err){}
            }
        }
    };

    private void tellTab1BlueToothState(){
        tabView1.updateBluetoothState(isConnected);
    }

    public static void writeModeToBlue(byte[] bytes){
        if(isConnected){
            mBluetoothLeService.writeByes(bytes);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        if(isBindService){
            unbindService(mServiceConnection);
        }
        sm.unregisterListener(this);
    }

    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }
    }

    private float []fSpeed = new float[2];
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.turn_right:
                if(fSpeed[0]==0){fSpeed[0] = 20;fSpeed[1] = -20;}
                else if ((fSpeed[0]<0)|(fSpeed[1]>0)) {fSpeed[0] = 0;fSpeed[1] = 0;}
                else {fSpeed[0] *= 1.2;fSpeed[1] *= 1.2;}
                break;
            case R.id.turn_left:
                if(fSpeed[0]==0){fSpeed[0] = -20;fSpeed[1] = 20;}
                else if ((fSpeed[0]>0)|(fSpeed[1]<0)) {fSpeed[0] = 0;fSpeed[1] = 0;}
                else {fSpeed[0] *= 1.2;fSpeed[1] *= 1.2;}
                break;
            case R.id.turn_up:
                if(fSpeed[0]==0){fSpeed[0] = 20;fSpeed[1] = 20;}
                else if ((fSpeed[0]<0)|(fSpeed[1]<0)) {fSpeed[0] = 0;fSpeed[1] = 0;}
                else {fSpeed[0] *= 1.2;fSpeed[1] *= 1.2;}
                break;
            case R.id.turn_down:

                if(fSpeed[0]==0){fSpeed[0] = -20;fSpeed[1] = -20;}
                else if ((fSpeed[0]>0)|(fSpeed[1]>0)) {fSpeed[0] = 0;fSpeed[1] = 0;}
                else {fSpeed[0] *= 1.2;fSpeed[1] *= 1.2;}
                break;
            case R.id.brake:
                fSpeed[0] = 0;
                fSpeed[1] = 0;
                writeModeToBlue(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x07,(byte)fSpeed[0] ,(byte)fSpeed[1]});
                break;
            case R.id.servo_control:
                showServoDialog();
                return;
            case R.id.speedRate:
                showSpeedRateDialog();
                return;
            default:
                break;
        }

        for (int i=0;i<2;i++) {
            if (fSpeed[i] > 100) fSpeed[i] = 100;
            else if (fSpeed[i] < -100) fSpeed[i] = -100;
        }
        writeModeToBlue(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x07, (byte)fSpeed[0] ,(byte)fSpeed[1]});
        currentSpeed.setText(String.format("%.0f : %.0f",fSpeed[0],fSpeed[1]));
    }

    //设置当前模式
    public static int iCurrentMode = 0;
    public static void setCurrentMode(int i){
        iCurrentMode = i;
       /* if(i == MODE_GUSTURE){
            currentMode.setText("当前模式\n\r手势模式");
            CURRENT_MODE = MODE_GUSTURE;
        }else if(i == MODE_HAND_CONTROL){
            currentMode.setText("当前模式\n\r手控模式");
            CURRENT_MODE = MODE_HAND_CONTROL;
        }else if (i == MODE_TRACING){
            currentMode.setText("当前模式\n\r寻迹模式");
            CURRENT_MODE = MODE_TRACING;
        }else if(i == MODE_INFRARED){
            currentMode.setText("当前模式\n\r红外模式");
            CURRENT_MODE = MODE_INFRARED;
        }else if(i == MODE_UITRASONIC_WAVE){
            currentMode.setText("当前模式\n\r超声波模式");
            CURRENT_MODE = MODE_UITRASONIC_WAVE;
        }*/
    }

    //设置当前速度
    public static void setCurrentSpeed(float speed){
        currentSpeed.setText("当前速度\n\r"+ speed +"km/h");
    }

    /**
     * 点击控制舵机的时候，就弹出这个提示框
     * */
    private AlertDialog speedRateDialog;
    private void showSpeedRateDialog(){
        LayoutInflater inflater = LayoutInflater.from(mInflater.getContext());
        final LinearLayout linearLayout =(LinearLayout) inflater.inflate(R.layout.dialog_servo,null);
        SeekBar seekBar = (SeekBar)linearLayout.findViewById(R.id.seek_bar);
        final TextView barNum = (TextView)linearLayout.findViewById(R.id.servo_num);
        seekBar.setMax(2000);
        seekBar.setProgress(1000);
        barNum.setText("1:1");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                barNum.setText(String.format("1:%.2f",(float)i/1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                writeModeToBlue(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x09,(byte)(seekBar.getProgress()&0xff) ,(byte)(seekBar.getProgress()>>8)});
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                speedRateDialog.dismiss();
            }
        });
        speedRateDialog = builder.create();
        speedRateDialog.setTitle("请选择左右轮速比例");
        speedRateDialog.setView(linearLayout);
        speedRateDialog.show();
    }
    /**
     * 点击控制舵机的时候，就弹出这个提示框
     * */
    private AlertDialog servoDialog;
    private int currentProgress = 90;
    private int nowProgress = currentProgress;
    private void showServoDialog(){
        LayoutInflater inflater = LayoutInflater.from(mInflater.getContext());
        final LinearLayout linearLayout =(LinearLayout) inflater.inflate(R.layout.dialog_servo,null);
        SeekBar seekBar = (SeekBar)linearLayout.findViewById(R.id.seek_bar);
        final TextView barNum = (TextView)linearLayout.findViewById(R.id.servo_num);
        seekBar.setMax(180);
        seekBar.setProgress(currentProgress);
        barNum.setText(currentProgress+"");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nowProgress  =  i;
                Log.d("wnw", i+"");
                barNum.setText(i+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                writeModeToBlue(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x08,(byte)seekBar.getProgress() ,0});
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                servoDialog.dismiss();
                currentProgress = nowProgress;
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                servoDialog.dismiss();
            }
        });
        servoDialog = builder.create();
        servoDialog.setTitle("请选择舵机的方向");
        servoDialog.setView(linearLayout);
        servoDialog.show();
    }
}
