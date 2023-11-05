package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

import java.util.Scanner;

public abstract class Player {
    private String name;
    private char color;
    private int resourceId;

    public Player(String name) {
        this.name = name;
    }

    public abstract boolean makeMove(int row, int col, Round r, Board b, ComputerStrategy c, Tournament t, Activity a);



    public String getName() {
        return name;
    }

    public char getColor() {
        return color;
    }

    public void setColor(char c) {
        color = c;
    }

    public void setBackground(int resourceId){
        this.resourceId = resourceId;
    }

    public int getBackground(){
        return resourceId;
    }
}
