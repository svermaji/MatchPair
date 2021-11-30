package com.sv.matchpair;

public final class GameScore {

    private final String score, date;

    public GameScore(String score, String date) {
        this.score = score;
        this.date = date;
    }

    public String getScore() {
        return score;
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