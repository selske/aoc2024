package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Day01 extends Day {

    public Day01() {
        super(1);
    }

    public static void main(String[] args) {
        new Day01()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("2176849")
                .verifyPart2("23384288");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Inputs inputs = parse(input);

        results.setPart1(part1(inputs));
        results.setPart2(part2(inputs));
    }

    private static int part1(Inputs inputs) {
        int sum = 0;
        for (int i = 0; i < inputs.left().length; i++) {
            sum += Math.abs(inputs.left()[i] - inputs.right()[i]);
        }
        return sum;
    }

    private static long part2(Inputs inputs) {
        Map<Integer, Long> rightCounts = IntStream.of(inputs.right())
                .boxed()
                .collect(groupingBy(identity(), counting()));

        long sum = 0;
        for (int i : inputs.left()) {
            sum += i * rightCounts.getOrDefault(i, 0L);
        }
        return sum;
    }

    private static Inputs parse(String input) {
        List<String> lines = input.lines().toList();
        int[] left = new int[lines.size()];
        int[] right = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(" +");
            left[i] = parseInt(parts[0]);
            right[i] = parseInt(parts[1]);
        }
        Arrays.sort(left);
        Arrays.sort(right);
        return new Inputs(left, right);
    }

    private record Inputs(int[] left, int[] right) {

    }

}