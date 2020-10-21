package com.example.icarus;

public class Data {

    private long startCount;
    private long endCount;
    public Data() {
    }

    public Data(long startCount, long endCount) {
        this.setStartCount(startCount);
        this.setEndCount(endCount);
    }

    public void setStartCount(long startCount) {
        this.startCount = startCount;
    }

    public void setEndCount(long endCount) {
        this.endCount = endCount;
    }

    public long getStartCount(long startCount) {
        return this.startCount;
    }

    public long getEndCount(long endCount) {
        return this.endCount;
    }
}
