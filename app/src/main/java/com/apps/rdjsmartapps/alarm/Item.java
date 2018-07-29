package com.apps.rdjsmartapps.alarm;

/**
 * Created by Harshal on 4/7/2018.
 */

public class Item {

    String time, ampm, repeatType, note, switchState, timeToAlarm;
    int id;

    // Constructor


    public Item(int id, String time, String ampm, String repeatType, String note, String switchState, String timeToAlarm) {
        this.id = id;
        this.time = time;
        this.ampm = ampm;
        this.repeatType = repeatType;
        this.note = note;
        this.switchState = switchState;
        this.timeToAlarm = timeToAlarm;
    }


    // setters and getters


    public String getTimeToAlarm() {
        return timeToAlarm;
    }

    public void setTimeToAlarm(String timeToAlarm) {
        this.timeToAlarm = timeToAlarm;
    }

    public int getId() {
        return id;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSwitchState() {
        return switchState;
    }

    public void setSwitchState(String switchState) {
        this.switchState = switchState;
    }


} // End of class
