package com.wit.smartcar.bean;

/**
 * Created by wnw on 2016/9/23.
 */
public class Instruction {
    private byte instructions[];

    public Instruction(){

    }

    public Instruction(byte[] instructions){
        this.instructions = instructions;
    }

    public byte[] getInstructions() {
        return instructions;
    }

    public void setInstructions(byte[] instructions) {
        this.instructions = instructions;
    }
}
