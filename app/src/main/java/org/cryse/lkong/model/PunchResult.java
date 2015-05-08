package org.cryse.lkong.model;

import java.util.Date;

public class PunchResult {
    private Date punchTime;
    private int punchDay;

    public Date getPunchTime() {
        return punchTime;
    }

    public void setPunchTime(Date punchTime) {
        this.punchTime = punchTime;
    }

    public int getPunchDay() {
        return punchDay;
    }

    public void setPunchDay(int punchDay) {
        this.punchDay = punchDay;
    }
}
