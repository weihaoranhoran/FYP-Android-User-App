package com.wit.smartcar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.wit.smartcar.bean.SenseItemView;
import com.wit.smartcar.bean.SenseViewData;

import java.util.ArrayList;
import java.util.List;

public class TabViewSense {
    private LayoutInflater mInflater;
    private View mView;
    private Context context;
    private GridView gridView;
    private SenseGridAdapter senseGridAdapter;

    private String datas[] = new String[28];

    public TabViewSense(LayoutInflater inflater){
        this.mInflater = inflater;
        context = mInflater.getContext();
        mView = mInflater.inflate(R.layout.tab_second, null);
        initView();
    }

    private void initView(){
        gridView = (GridView)mView.findViewById(R.id.tab_second_grid);
        for(int i = 0; i < datas.length; i++){
            datas[i] = 0+"";
        }
        senseGridAdapter = new SenseGridAdapter(context,datas);
        gridView.setAdapter(senseGridAdapter);
    }

    public void updateData(String data[]){
        senseGridAdapter.setDatas(data);
        senseGridAdapter.notifyDataSetChanged();
    }

    public View getView(){
        return mView;
    }
}
