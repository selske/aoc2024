package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.Character.getNumericValue;

public class Day10 extends Day {

    public Day10() {
        super(10);
    }

    public static void main(String[] args) {
        new Day10()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("501")
                .verifyPart2("1017");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Map<Point, Integer> map = new HashMap<>();
        List<Point> trailHeads = new ArrayList<>();
        MapParser.parse(input, (point, c) -> {
            int value = getNumericValue(c);
            if (value == 0) {
                trailHeads.add(point);
            }
            map.put(point, value);
        });

        long sum = trailHeads.stream()
                .mapToLong(trailHead -> {
                    List<Point> current = List.of(trailHead);
                    for (int i = 1; i < 10; i++) {
                        int nextHeight = i;
                        current = current.stream()
                                .flatMap(point -> getPossibleNeighbours(point, map, nextHeight))
                                .distinct()
                                .toList();
                    }
                    return current.size();
                })
                .sum();

        results.setPart1(sum);

        sum = trailHeads.stream()
                .mapToLong(trailHead -> {
                    List<Point> current = List.of(trailHead);
                    for (int i = 1; i < 10; i++) {
                        int nextHeight = i;
                        current = current.stream()
                                .flatMap(point -> getPossibleNeighbours(point, map, nextHeight))
                                .toList();
                    }
                    return current.size();
                })
                .sum();

        results.setPart2(sum);

    }

    private static Stream<Point> getPossibleNeighbours(Point point, Map<Point, Integer> map, int nextHeight) {
        return Arrays.stream(CardinalDirections.values())
                .map(point::getNeighbour)
                .filter(p -> map.getOrDefault(p, -1) == nextHeight);
    }

}