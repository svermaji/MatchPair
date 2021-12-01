package com.sv.matchpair;

import com.sv.core.Utils;

public final class GameScore {

    private final String score, date;

    public GameScore(String score, String date) {
        this.score = score;
        this.date = date;
    }

    public String getScore() {
        return score;
    }

    // used for sorting
    public int getScoreAsInt() {
        return Utils.convertToInt(score);
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "GameScore{" +
                "score='" + score + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
