package com.wit.smartcar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wit.smartcar.bean.BlueDeviceBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by wnw on 2016/9/20.
 */
public class SearchBluetoothActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener{

    /***
     *
     * Return Intent extra
     */

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME = "device_name";

    private static final long SCAN_PERIOD = 10000;
    /**
     * 已经匹配的设备和可以匹配的设备
     * */
    private List<BlueDeviceBean> matchDevices;
    private List<BlueDeviceBean> otherDevices;

    /**
     * 已经匹配设备的列表和待匹配设备的列表
     * */
    private ListView matchView;
    private ListView otherView;

    /**
     * 搜索按钮和progressbar,返回键
     * */
    private Button discoverBlue;
    private ProgressBar searchBar;
    private ImageView backArrow;

    private Handler mHandler;
    /**
     * 两个ListView的适配器
     * */

    private ArrayAdapter<String> matchAdapter;
    private ArrayAdapter<String> otherAdapter;

    /**
     * 蓝牙适配器
     * */

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_device);

        initBlue();
        initView();
        getMatchDevice();
        searchBar.setVisibility(View.VISIBLE);
        scanLeDevice(true);
    }
    /**
     * 初始化蓝牙
     * */
    private void initBlue(){
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "蓝牙不支持", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /**
     * 初始化View
     * */

    private void initView(){
        matchDevices = new ArrayList<BlueDeviceBean>();
        otherDevices = new ArrayList<BlueDeviceBean>();

        matchView = (ListView)findViewById(R.id.lv_matched);
        otherView = (ListView)findViewById(R.id.lv_other_match);

        matchView.setOnItemClickListener(this);
        otherView.setOnItemClickListener(this);

        searchBar = (ProgressBar)findViewById(R.id.search_prg_bar);
        discoverBlue = (Button)findViewById(R.id.btn_search_bluetooth);
        backArrow = (ImageView)findViewById(R.id.back_arrow);
        discoverBlue.setOnClickListener(this);
        backArrow.setOnClickListener(this);

        matchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        otherAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        matchView.setAdapter(matchAdapter);
        otherView.setAdapter(otherAdapter);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    reLoadProgress();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.d("wnw", "you come here?");
        }
        //重新加载进度条
        reLoadProgress();
    }
    private void reLoadProgress(){
        if(mScanning){
            searchBar.setVisibility(View.VISIBLE);
        }else {
            searchBar.setVisibility(View.GONE);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isExistDevice(device.getAddress())){//还不存在
                                otherAdapter.add(device.getName()+"(" + device.getAddress() +")");
                                otherAdapter.notifyDataSetChanged();

                                //存起来，供返回的时候调用
                                BlueDeviceBean myBluetoothDevice = new BlueDeviceBean(device.getName(), device.getAddress());
                                otherDevices.add(myBluetoothDevice);
                            }
                        }
                    });
                }
            };

    /***
     * 得到已经绑定过的设备
     */
    private void getMatchDevice(){
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Iterator iterator = pairedDevices.iterator();

        //get the count
        int count = pairedDevices.size();
        if(count == 0){
            Toast.makeText(SearchBluetoothActivity.this, "没有发现任何可以匹配的设备", Toast.LENGTH_SHORT).show();
        }else{
            while (iterator.hasNext()){
                BluetoothDevice device =(BluetoothDevice) iterator.next();
                //Log.d("SearchBluetoothActivity", device.getName()+":"+device.getAddress());
                matchAdapter.add(device.getName()+"(" + device.getAddress() +")");

                //存起来，供单击返回时调用
                BlueDeviceBean myBluetoothDevice = new BlueDeviceBean(device.getName(), device.getAddress());
                matchDevices.add(myBluetoothDevice);
            }
        }
    }

    /**
     * 判断搜索到的设备，在List中是否已经存在,判断的依据是：Address
     * */
    private boolean isExistDevice(String address){
        for(int i = 0; i < otherDevices.size(); i++){
            if (otherDevices.get(i).getAddress().equals(address)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        switch (adapterView.getId()){
            case R.id.lv_matched:
                if (mScanning) {
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                BlueDeviceBean device = matchDevices.get(i);
                String address = device.getAddress();
                String name = device.getName();
                // Create the result Intent and include the MAC address
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                intent.putExtra(EXTRA_DEVICE_NAME,name);
                setResult(Activity.RESULT_OK, intent);
                finish();	//程序自动返回之前的activity

                break;
            case R.id.lv_other_match:
                if (mScanning) {
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                BlueDeviceBean device1 = otherDevices.get(i);
                String address1 = device1.getAddress();
                String name1 = device1.getName();

                // Create the result Intent and include the MAC address
                Intent intent1 = new Intent();
                intent1.putExtra(EXTRA_DEVICE_ADDRESS, address1);
                intent1.putExtra(EXTRA_DEVICE_NAME,name1);
                setResult(Activity.RESULT_OK, intent1);
                finish();	//程序自动返回之前的activity
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search_bluetooth:
                searchBar.setVisibility(View.VISIBLE);
                //otherAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.back_arrow:
                finish();
                break;
            default:
                break;
        }
    }

    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mScanning) {
            scanLeDevice(false);
        }
    }
}
