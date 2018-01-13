package com.wit.smartcar;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wit.smartcar.bean.Instruction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wnw on 2016/9/19.
 */
public class TabViewProgrammable implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, NumberPicker.Formatter{
    private LayoutInflater mInflater;
    private View mView;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private Button moveBtn;
    private Button servoBtn;
    private Button delayBtn;
    private Button sendBtn;
    private Switch avoidanceSw;
    private Switch uwsAvoidanceSw;
    private Switch ledSw;
    private Button stopBtn;

    private boolean isStop = false;    //是否点击了停止的按钮

    private List<Instruction> instructionList = new ArrayList<>();  //存放指令
    private List<String> instrctionText = new ArrayList<>();        //说明是什么指令

    public TabViewProgrammable(LayoutInflater inflater){
        this.mInflater = inflater;
        mView = mInflater.inflate(R.layout.tab_third, null);
        initView();
    }

    private void initView(){

        moveBtn = (Button)mView.findViewById(R.id.btn_move);
        servoBtn = (Button)mView.findViewById(R.id.btn_servo);
        delayBtn = (Button)mView.findViewById(R.id.btn_delay);
        sendBtn = (Button)mView.findViewById(R.id.btn_send);

        avoidanceSw = (Switch) mView.findViewById(R.id.sw_avoidance);
        uwsAvoidanceSw = (Switch)mView.findViewById(R.id.sw_avoidance_uw);
        ledSw = (Switch) mView.findViewById(R.id.sw_led);
        stopBtn = (Button)mView.findViewById(R.id.btn_stop);

        moveBtn.setOnClickListener(this);
        servoBtn.setOnClickListener(this);
        delayBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        avoidanceSw.setOnCheckedChangeListener(this);
        uwsAvoidanceSw.setOnCheckedChangeListener(this);
        ledSw.setOnCheckedChangeListener(this);

        listView = (ListView)mView.findViewById(R.id.lv_cheng_kong);

        adapter = new ArrayAdapter<String>(mInflater.getContext(), android.R.layout.simple_list_item_1,instrctionText);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

    }

    public View getView(){
        return mView;
    }

    private int currentSelectedId = 0;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //选中其中一个，弹出Dialog,然后如果点击删除就将这个删除出去，更新ListView
        showDeleteDialog();
        currentSelectedId = i;
        return true;
    }

    /**
     * 点击删除的时候，就弹出这个提示框
     * */
    private AlertDialog deleteDialog;
    private void showDeleteDialog(){
        LayoutInflater inflater = LayoutInflater.from(mInflater.getContext());
        final LinearLayout linearLayout =(LinearLayout) inflater.inflate(R.layout.dialog_delete,null);
        Button deleteBtn = (Button)linearLayout.findViewById(R.id.dialog_btn_delete);
        Button cancelBtn = (Button)linearLayout.findViewById(R.id.dialog_btn_cancel);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除指令
                deleteInstruction();
                deleteDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
        deleteDialog = new AlertDialog.Builder(mInflater.getContext()).create();
        deleteDialog.setView(linearLayout);
        deleteDialog.show();
    }

    /**
     * 插入一条指令，更新adapter
     * */
    private void insertInstruction(byte data[], String text){
        instructionList.add(new Instruction(data));
        instrctionText.add(text);
        adapter.notifyDataSetChanged();
    }

    /**
     * 删除指令，更新adapter
     * */

    private void deleteInstruction(){
        instrctionText.remove(currentSelectedId);
        instructionList.remove(currentSelectedId);
        adapter.notifyDataSetChanged();
    }

    private int lastSelectId = 0;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        View lastView = adapterView.getChildAt(lastSelectId);
        if(lastView != null){   //如果上一个view还存在，就设置背景颜色
            lastView.setBackgroundResource(R.color.color_lv_item_nor);
        }
        view.setBackgroundResource(R.color.color_btn_selected);
        lastSelectId = i;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_move:
                showMoveDialog();
                break;
            case R.id.btn_servo:
                showServoDialog();
                break;
            case R.id.btn_delay:
                showDelayDialog();
                break;
            case R.id.btn_send:
                isStop = false;
                break;
            case R.id.btn_stop:
                if(isStop){
                    isStop = false;
                    stopBtn.setBackgroundResource(R.color.color_lv_item_nor);
                    stopBtn.setText("停止");
                }else {
                    isStop = true;
                    stopBtn.setBackgroundColor(Color.RED);
                    stopBtn.setText("恢复");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.sw_avoidance:
                if(b){
                    byte[] data = new byte[]{(byte)0xff,(byte)0xaa,(byte)0x09,0x01,0x00};
                    instructionList.add(new Instruction(data));
                    insertInstruction(data, "避障开");
                }else {
                    byte[] data = new byte[]{(byte)0xff,(byte)0xaa,(byte)0x09,0x00,0x00};
                    insertInstruction(data, "避障关");

                }
                break;
            case R.id.sw_avoidance_uw:
                if(b){
                    byte[] data = new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0a,0x01,0x00};
                    insertInstruction(data, "超声波避障开");
                }else {
                    byte[] data = new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0a,0x00,0x00};
                    insertInstruction(data, "超声波避障关");
                }
                break;
            case R.id.sw_led:
                if(b){
                    byte[] data = new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0b,0x01,0x00};
                    insertInstruction(data, "LED开");
                }else {
                    byte[] data = new byte[]{(byte)0xff,(byte)0xaa,(byte)0x0b,0x00,0x00};
                    insertInstruction(data, "LED关");
                }
                break;
        }
    }

    /**
     * 点击运动的时候，就显示这个Dialog
     * */

    private AlertDialog moveDialog;
    private int currentMoveLeftNum = 0;
    private int currentMoveRightNum = 0;
    int nowLeftNum = currentMoveLeftNum;
    int nowRightNum = currentMoveRightNum;

    private void showMoveDialog(){
        nowLeftNum = 0;
        nowRightNum = 0;
        final LayoutInflater inflater = LayoutInflater.from(mInflater.getContext());
        final LinearLayout linearLayout =(LinearLayout) inflater.inflate(R.layout.dialog_move,null);

        NumberPicker leftPicker = (NumberPicker)linearLayout.findViewById(R.id.left_speed_num);
        NumberPicker rightPicker = (NumberPicker)linearLayout.findViewById(R.id.right_speed_num);
        leftPicker.setFormatter(this);
        leftPicker.setMinValue(0);
        leftPicker.setMaxValue(360);
        leftPicker.setValue(180);

        rightPicker.setMinValue(0);
        rightPicker.setMaxValue(360);
        rightPicker.setValue(180);
        rightPicker.setFormatter(this);

        leftPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                nowLeftNum = i1 - 180;
            }
        });

        rightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                nowRightNum = i1 - 180;
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveDialog.dismiss();
                currentMoveLeftNum = nowLeftNum;
                currentMoveRightNum = nowRightNum;
                Log.d("wnw", currentMoveLeftNum + ":" + currentMoveRightNum);
                //在这里去插入指令
                short leftNum = (short) currentMoveLeftNum;     //int转换成short,在转换成byte
                short rightNum = (short)currentMoveRightNum;

                byte left = (byte)leftNum;
                byte right = (byte)rightNum;

                byte[] data = new byte[]{(byte)0xff,(byte)0xaa,0x07,left,right};
                insertInstruction(data, "左右轮速为："+ currentMoveLeftNum + "  " + currentMoveRightNum);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveDialog.dismiss();
            }
        });

        moveDialog = builder.create();
        moveDialog.setTitle("请选择左右轮的速度");
        moveDialog.setView(linearLayout);
        moveDialog.show();
    }

    @Override
    public String format(int i) {
        int j = i - 180;
        return j + "";
    }

    /**
     * 点击舵机的时候，就显示这个Dialog
     * */
    private AlertDialog servoDialog;
    private int currentServoProgress = 0;
    private int nowServoProgress = currentServoProgress;
    private void showServoDialog(){
        LayoutInflater inflater = LayoutInflater.from(mInflater.getContext());
        final LinearLayout linearLayout =(LinearLayout) inflater.inflate(R.layout.dialog_servo,null);
        SeekBar seekBar = (SeekBar)linearLayout.findViewById(R.id.seek_bar);
        final TextView barNum = (TextView)linearLayout.findViewById(R.id.servo_num);
        seekBar.setMax(180);
        seekBar.setProgress(currentServoProgress);
        barNum.setText(currentServoProgress+"");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nowServoProgress  =  i;
                Log.d("wnw", i+"");
                barNum.setText(i+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                servoDialog.dismiss();
                currentServoProgress = nowServoProgress;
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

    /**
     * 点击延时的时候，就弹出这个框
     * */

    private AlertDialog delayDialog;
    private int currentDelayProgress = 0;
    private int nowDelayProgress = currentDelayProgress;

    private void showDelayDialog(){
        LayoutInflater inflater = LayoutInflater.from(mInflater.getContext());
        final LinearLayout linearLayout =(LinearLayout) inflater.inflate(R.layout.dialog_delay,null);
        SeekBar seekBar = (SeekBar)linearLayout.findViewById(R.id.seek_bar_delay);
        final TextView barNum = (TextView)linearLayout.findViewById(R.id.delay_num);
        seekBar.setMax(10000);
        seekBar.setProgress(currentDelayProgress);
        barNum.setText(currentDelayProgress+"");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nowDelayProgress  =  i;
                Log.d("wnw", i+"");
                barNum.setText(i+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delayDialog.dismiss();
                currentDelayProgress = nowDelayProgress;
                //在这里将要执行的指令添加到ListView中
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delayDialog.dismiss();
            }
        });

        delayDialog = builder.create();
        delayDialog.setTitle("请选择延时的时间：");
        delayDialog.setView(linearLayout);
        delayDialog.show();
    }

    /**
     * 开始延时
     * */

    private void startDelay(final int time){
        new Thread(new Runnable(){
            public void run(){
                try{
                    Thread.sleep(time);
                    Message msg = new Message();
                    handler.sendMessage(msg); //告诉主线程执行任务
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 延时完成后，就执行的操作
     * */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //to do something
        }
    };

}
