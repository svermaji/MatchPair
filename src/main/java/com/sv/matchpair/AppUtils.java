package com.sv.matchpair;

import com.sv.core.Constants;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sv.matchpair.AppConstants.*;

public final class AppUtils {

    private static Map<Integer, List<Integer[]>> gameSequences = new ConcurrentHashMap<>();

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

    public static List<GameButton> prepareGameButtons(GameInfo gi) {
        Integer[] seq = getRandomGameSeq(gi);
        int total = gi.getRows() * gi.getCols();

        List<GameButton> list = new ArrayList<>(total);
        List<Character> chList = new ArrayList<>(total);
        int elem = total - PAIRS_COUNT;
        Random rand = new Random();
        while (chList.size() < elem) {
            Character ch = AppConstants.GAME_CHARS[rand.nextInt(GAME_CHARS.length)];
            if (!chList.contains(ch)) {
                chList.add(ch);
            }
        }
        AtomicInteger k = new AtomicInteger();
        AtomicInteger t = new AtomicInteger();
        AtomicInteger p = new AtomicInteger();
        // todo check it
        Arrays.stream(seq).forEach(i -> {
            for (int j = 0; j < i; j++) {
                list.add(new GameButton(chList.get(t.intValue()) + Constants.EMPTY,
                        gi.getColors()[k.intValue()]));
            }
            k.getAndIncrement();
            t.getAndIncrement();
            if (p.intValue() < PAIRS_COUNT) {
                list.add(new GameButton(chList.get(t.intValue()) + Constants.EMPTY,
                        gi.getColors()[k.intValue()]));
            }
            p.getAndIncrement();
            k.getAndIncrement();
            t.getAndIncrement();
        });

        return list;
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
