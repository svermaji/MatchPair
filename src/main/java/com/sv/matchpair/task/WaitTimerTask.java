package com.sv.matchpair.task;

import com.sv.matchpair.MatchPair;

import java.util.TimerTask;

public class WaitTimerTask extends TimerTask {

    private final MatchPair mp;

    public WaitTimerTask(MatchPair mp) {
        this.mp = mp;
    }

    @Override
    public void run() {
        mp.updateWaitTime();
    }
}
