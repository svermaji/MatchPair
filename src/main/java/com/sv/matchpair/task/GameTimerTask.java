package com.sv.matchpair.task;

import com.sv.core.Utils;
import com.sv.matchpair.MatchPair;

import java.util.TimerTask;

public class GameTimerTask extends TimerTask {

    private final MatchPair mp;

    public GameTimerTask(MatchPair mp) {
        this.mp = mp;
    }

    @Override
    public void run() {
        do {
            mp.updateGameTime();
            Utils.sleep1Sec();
        } while (mp.isGameRunning());
    }
}
