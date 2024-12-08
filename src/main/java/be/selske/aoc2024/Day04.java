package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;
import be.selske.aoc2024.util.map.PrincipalDirections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day04 extends Day {


    public Day04() {
        super(4);
    }

    public static void main(String[] args) {
        new Day04()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("2507")
                .verifyPart2("1969");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Map<Point, Character> letters = new HashMap<>();
        MapParser.parse(input, letters::put);

        results.setPart1(part1(letters));
        results.setPart2(part2(letters));
    }

    private static long part1(Map<Point, Character> letters) {
        return letters.entrySet().parallelStream()
                .filter(e -> e.getValue() == 'X')
                .mapToLong(e -> Arrays.stream(PrincipalDirections.values())
                        .map(direction -> e.getKey().getNeighbours(direction, 3).stream()
                                .mapToInt(neighbour -> letters.getOrDefault(neighbour, ' '))
                                .toArray())
                        .filter(word -> Arrays.equals(word, MAS))
                        .count())
                .sum();
    }

    private static final int[] MAS = {'M', 'A', 'S'};

    private static long part2(Map<Point, Character> letters) {
        return letters.entrySet().parallelStream()
                .filter(e -> e.getValue() == 'A')
                .filter(e -> Stream.of(
                                Stream.of(PrincipalDirections.UP_LEFT, PrincipalDirections.DOWN_RIGHT),
                                Stream.of(PrincipalDirections.DOWN_LEFT, PrincipalDirections.UP_RIGHT)
                        )
                                     .map(directions -> directions
                                             .map(direction -> letters.get(e.getKey().getNeighbour(direction)))
                                             .collect(Collectors.toSet())
                                     )
                                     .filter(sm -> sm.equals(Set.of('S', 'M')))
                                     .count() == 2)
                .count();
    }

}