package com.wit.smartcar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.wit.smartcar.bean.ModeItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wnw on 2016/9/19.
 */
public class TabViewMode implements AdapterView.OnItemClickListener{

    private LayoutInflater mInflater;
    private View mView;

    private boolean isConnected = false;    //是否已经连接了蓝牙

    public List<ModeItemView> itemList = new ArrayList<>();
    private String text[] = new String[]{"超声波避障", "红外避障","寻迹模式","跟踪模式","手势模式","体感模式","手动模式","停机模式"};
    private int resId[] = new int[]{R.drawable.uitrasonic_wave,R.drawable.infrared, R.drawable.tracing, R.drawable.tracing, R.drawable.gesture, R.drawable.hand_control,R.drawable.hand_control,R.drawable.hand_control
    };

    public TabViewMode(LayoutInflater inflater){
        this.mInflater = inflater;
        mView = mInflater.inflate(R.layout.tab_first, null);
        initView();
    }

    private void initView(){
        GridView view = (GridView)mView.findViewById(R.id.grid);
        for(int i = 0 ; i < text.length; i++){
            itemList.add(new ModeItemView(resId[i], text[i]));
        }
        view.setAdapter(new ModeGridAdapter(mInflater.getContext(),itemList));
        view.setOnItemClickListener(this);
    }
    public View getView(){
        return mView;
    }

    public void updateBluetoothState(boolean isConnected){
        this.isConnected = isConnected;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(isConnected){
            writeToBlue(i);
        }else {
            Toast.makeText(mInflater.getContext(), "蓝牙没有连接到小车",Toast.LENGTH_SHORT).show();
        }
    }

    private void writeToBlue(int i){
        byte RunMode=(byte)i ;
        MainActivity.writeModeToBlue(new byte[]{(byte)0xff,(byte)0xaa,(byte)0x05, RunMode ,0});
        MainActivity.setCurrentMode(i);
    }
}

