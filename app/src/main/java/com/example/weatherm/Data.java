package com.example.weatherm;

public class Data {

    private String name;
    private int index;
    private int resId;
    private int set;

    public Data() {
    }

    public Data(String name, int index, int resId, int set) {
        this.name = name;
        this.index = index;
        this.resId = resId;
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public int getSet() {
        return set;
    }

    public void setSet(Integer set) {
        this.set = set;
    }

}