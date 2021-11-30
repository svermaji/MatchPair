package com.sv.matchpair;

import java.util.List;

public final class GameScores {

    private final String username;
    private final List<GameScore> topScore, recentScore;

    public GameScores(String username, List<GameScore> topScore, List<GameScore> recentScore) {
        this.username = username;
        this.topScore = topScore;
        this.recentScore = recentScore;
    }

    public String getUsername() {
        return username;
    }

    public List<GameScore> getTopScore() {
        return topScore;
    }

    public List<GameScore> getRecentScore() {
        return recentScore;
    }

    @Override
    public String toString() {
        return "GameScores{" +
                "username='" + username + '\'' +
                ", topScore=" + topScore +
                ", recentScore=" + recentScore +
                '}';
    }
}
