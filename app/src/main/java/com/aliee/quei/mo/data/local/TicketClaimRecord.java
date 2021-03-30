package com.aliee.quei.mo.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TicketClaimRecord extends RealmObject {
    @PrimaryKey
    private int tid;

    private long time;

    private int status;  //0    1已领取金币

    public TicketClaimRecord() {
    }

    public TicketClaimRecord(int tid, long time) {
        this.tid = tid;
        this.time = time;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
