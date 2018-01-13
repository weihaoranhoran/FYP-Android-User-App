package com.wit.smartcar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wit.smartcar.bean.ModeItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wnw on 2016/9/20.
 */
public class ModeGridAdapter extends BaseAdapter {

    private List<ModeItemView> mItems = new ArrayList<ModeItemView>();
    private Context mContext;

    public ModeGridAdapter(Context context, List<ModeItemView> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ModeViewHolder modeViewHolder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mode_gridview_item, null);
            modeViewHolder = new ModeViewHolder();
            modeViewHolder.imageView = (ImageView)convertView.findViewById(R.id.icon);
            modeViewHolder.textView = (TextView)convertView.findViewById(R.id.text);
            convertView.setTag(modeViewHolder);
        }else{
            modeViewHolder = (ModeViewHolder)convertView.getTag();
        }

        ModeItemView item = mItems.get(position);
        modeViewHolder.imageView.setImageResource(item.getResId());
        modeViewHolder.textView.setText(item.getText());
        return convertView;
    }

    class ModeViewHolder{
        ImageView imageView;
        TextView textView;
    }
}