package com.example.checkapp;

import java.util.Date;

public class Record {
    private Date recordTime;
    private String cameraID;
    private int recordID;

    public Record(Date recordTime, String cameraID, int recordID) {
        this.recordTime = recordTime;
        this.cameraID = cameraID;
        this.recordID = recordID;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public String getCameraID() {
        return cameraID;
    }

    public int getRecordID() {
        return recordID;
    }
}
