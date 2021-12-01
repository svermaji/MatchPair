package com.sv.matchpair;

import java.util.*;

public final class GameScores {

    private final String username;
    private final List<GameScore> scores;

    public GameScores(String username, List<GameScore> scores) {
        this.username = username;
        this.scores = scores == null ? new ArrayList<>() : scores;
    }

    public String getUsername() {
        return username;
    }

    public List<GameScore> getTopScores() {
        List<GameScore> topScore = new ArrayList<>();
        topScore.addAll(scores);
        topScore.sort(Comparator.comparing(GameScore::getScore).reversed());
        return topScore;
    }

    public String getTopScore() {
        return getTopScores().size() > 0 ? getTopScores().get(0).getScore() : "0";
    }

    public List<GameScore> getRecentScores() {
        return scores;
    }

    public void addScore(GameScore gs) {
        scores.add(0, gs);
    }

    @Override
    public String toString() {
        return "GameScores{" +
                "username='" + username + '\'' +
                ", scores=" + scores +
                '}';
    }
}
