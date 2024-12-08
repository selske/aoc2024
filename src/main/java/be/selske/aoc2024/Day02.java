package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.signum;
import static java.lang.Math.abs;

public class Day02 extends Day {

    public Day02() {
        super(2);
    }

    public static void main(String[] args) {
        new Day02()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("242")
                .verifyPart2("311");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<int[]> reports = input.lines()
                .map(line -> Arrays.stream(line.split(" +")).mapToInt(Integer::parseInt).toArray())
                .toList();

        results.setPart1(reports.stream()
                .filter(this::isSafe)
                .count());

        results.setPart2(reports.stream()
                .filter(report -> {
                    if (isSafe(report)) {
                        return true;
                    } else {
                        return permuteRemoveOne(report).anyMatch(this::isSafe);
                    }
                })
                .count());

    }

    private boolean isSafe(int[] levels) {
        int signum = 0;
        for (int i = 1; i < levels.length; i++) {
            int a = levels[i - 1];
            int b = levels[i];
            int delta = a - b;
            if (abs(delta) < 1 || abs(delta) > 3) {
                return false;
            }
            if (signum != 0 && signum != Math.signum(delta)) {
                return false;
            }
            signum = signum(delta);
        }
        return true;
    }

    private Stream<int[]> permuteRemoveOne(int[] levels) {
        return IntStream.range(0, levels.length)
                .mapToObj(levelToSkip -> {
                    int[] newLevels = new int[levels.length - 1];
                    int j = 0;
                    for (int i = 0; i < levels.length; i++) {
                        if (i == levelToSkip) continue;
                        newLevels[j++] = levels[i];
                    }
                    return newLevels;
                });
    }

}

