package com.sv.matchpair;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.sv.matchpair.AppConstants.GAME_COLOR_SEQUENCES;

public final class AppUtils {

    private static final Map<Integer, List<Integer[]>> gameSequences = new ConcurrentHashMap<>();
    public static GameButton lastButton;

    private AppUtils() {
    }

    private static void prepareGameSequences() {
        Arrays.stream(GAME_COLOR_SEQUENCES).forEach(arr -> {
            int sum = Arrays.stream(arr).reduce(0, Integer::sum);
            if (gameSequences.containsKey(sum)) {
                List<Integer[]> list = gameSequences.get(sum);
                list.add(arr);
            } else {
                List<Integer[]> list = new ArrayList<>();
                list.add(arr);
                gameSequences.put(sum, list);
            }
        });
    }

    public static Integer[] getRandomGameSeq(GameInfo gi) {
        if (gameSequences.isEmpty()) {
            prepareGameSequences();
        }
        List<Integer[]> list = gameSequences.get(gi.getRows() * gi.getCols());
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public static Color getColor(String s) {
        switch (s) {
            case "red":
                return Color.red;
            case "green":
                return Color.green;
            case "blue":
                return Color.blue;
            case "purple":
                return new Color(102, 0, 153);
            case "orange":
                return Color.orange;
        }
        return Color.black;
    }
}
