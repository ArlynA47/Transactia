package com.egls.transactia;

public class DataHolder {
    private static final DataHolder instance = new DataHolder();
    private int displayed;

    private DataHolder() {}

    public static DataHolder getInstance() {
        return instance;
    }

    public int getDisplayed() {
        return displayed;
    }

    public void setDisplayed(int displayed) {
        this.displayed = displayed;
    }
}

