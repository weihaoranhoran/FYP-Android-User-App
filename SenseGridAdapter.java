package com.wit.smartcar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wit.smartcar.bean.SenseData;
import com.wit.smartcar.bean.SenseItemView;
import com.wit.smartcar.bean.SenseViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wnw on 2016/9/20.
 */
public class SenseGridAdapter extends BaseAdapter {

    private String datas[] = new String[28];
    private String titles[] ;
    private Context mContext;

    public void setDatas(String datas[]){
        this.datas = datas;
    }

    public SenseGridAdapter(Context context, String datas[]) {
        this.datas = datas;
        this.mContext = context;
        titles = SenseViewData.dataName;
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SenseViewHolder senseViewHolder = null;
        if(convertView == null) {
            senseViewHolder = new SenseViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sense_gridview_item, null);
            senseViewHolder.squareLayout = (SquareLayout)convertView.findViewById(R.id.tab_second_gv_item);
            senseViewHolder.titleTextView = (TextView)convertView.findViewById(R.id.gv_item_title);
            senseViewHolder.numTextView = (TextView)convertView.findViewById(R.id.gv_item_num);
            convertView.setTag(senseViewHolder);
            senseViewHolder.squareLayout.setBackgroundResource(SenseViewData.bgColor[0]);
            senseViewHolder.titleTextView.setText(titles[position]);
        }
        else{
            senseViewHolder = (SenseViewHolder)convertView.getTag();
        }
        senseViewHolder.numTextView.setText(datas[position]);
        return convertView;
    }

    class SenseViewHolder{
        TextView titleTextView;
        TextView numTextView;
        SquareLayout squareLayout;
    }
}
