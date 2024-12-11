package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingLong;

public class Day11 extends Day {

    public Day11() {
        super(11);
    }

    public static void main(String[] args) {
        new Day11()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("203228")
                .verifyPart2("240884656550923");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        String[] inputStones = input.split(" ");

        List<Stone> stones = new ArrayList<>();
        Stone start = null;
        for (String inputStone : inputStones) {
            Stone current = new Stone(parseInt(inputStone), 1);
            stones.add(current);
            if (start == null) {
                start = current;
            }
        }

        results.setPart1(solve(stones, 25));
        results.setPart2(solve(stones, 75));
    }

    private long solve(List<Stone> stones, int iterations) {
        List<Stone> result = stones;
        for (int i = 0; i < iterations; i++) {
            result = blink(result);
        }
        return result.stream().mapToLong(Stone::count).sum();
    }


    private List<Stone> blink(List<Stone> stones) {
        return stones.parallelStream()
                .flatMap(current -> {
                    if (current.value == 0) {
                        return Stream.of(new Stone(1, current.count()));
                    } else {
                        int numberOfDigits = (int) floor(log10(current.value)) + 1;
                        if (numberOfDigits % 2 == 0) {
                            int newLength = numberOfDigits / 2;
                            int order = (int) pow(10, newLength);
                            Stone left = new Stone(current.value / order, current.count());
                            Stone right = new Stone(current.value % order, current.count());
                            return Stream.of(left, right);
                        } else {
                            return Stream.of(new Stone(current.value() * 2024, current.count()));
                        }
                    }
                })
                .sorted(comparingLong(Stone::count))
                .reduce(emptyList(), this::merge, this::merge);
    }

    private List<Stone> merge(List<Stone> a, Stone b) {
        return merge(a, List.of(b));
    }

    private List<Stone> merge(List<Stone> a, List<Stone> b) {
        int aIndex = 0;
        int bIndex = 0;
        List<Stone> result = new ArrayList<>();
        while (aIndex < a.size() && bIndex < b.size()) {
            Stone aStone = a.get(aIndex);
            Stone bStone = b.get(bIndex);
            if (aStone.value() == bStone.value()) {
                result.add(new Stone(aStone.value(), aStone.count() + bStone.count()));
                aIndex++;
                bIndex++;
            } else if (aStone.value() < bStone.value()) {
                result.add(aStone);
                aIndex++;
            } else {
                result.add(bStone);
                bIndex++;
            }
        }
        result.addAll(a.subList(aIndex, a.size()));
        result.addAll(b.subList(bIndex, b.size()));
        return result;
    }

    private record Stone(long value, long count) {
    }

}