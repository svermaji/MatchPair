package com.sv.matchpair;

import com.sv.core.Utils;

public final class GameScore {

    private final String score, date, accuracy, level;

    public GameScore(int score, String date, int accuracy, int level) {
        this(score + "", date, accuracy + "", level + "");
    }

    public GameScore(String score, String date, String accuracy, String level) {
        this.score = score;
        this.date = date;
        this.accuracy = accuracy;
        this.level = level;
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

    public String getAccuracy() {
        return accuracy;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "GameScore{" +
                "score='" + score + '\'' +
                ", date='" + date + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
