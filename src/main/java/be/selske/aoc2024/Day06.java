package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.Direction;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Day06 extends Day {

    public Day06() {
        super(6);
    }

    public static void main(String[] args) {
        new Day06()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("4656")
                .verifyPart2("1575");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        AtomicReference<GuardState> guard = new AtomicReference<>();
        Map<Point, Character> map = new HashMap<>();
        MapParser.parse(input, (point, c) -> {
            if (c == '^') {
                guard.set(new GuardState(point, CardinalDirections.UP));
            }
            map.put(point, c);
        });
        if (guard.get() == null) {
            throw new IllegalArgumentException("No guard found");
        }

        List<Point> path = part1(guard.get(), map);
        results.setPart1(path.size());
        results.setPart2(part2(guard.get(), map, path));
    }

    private static List<Point> part1(GuardState guard, Map<Point, Character> map) {
        Set<GuardState> history = new HashSet<>();

        while (true) {
            history.add(guard);
            Point ahead = guard.pointAhead();
            if (map.get(ahead) == null) {
                break;
            } else if (map.get(ahead) == '#') {
                guard = guard.turnRight();
            } else {
                guard = guard.advance();
            }
        }
        return history.stream().map(GuardState::position).distinct().toList();
    }

    private static long part2(GuardState startPosition, Map<Point, Character> initialMap, List<Point> path) {
        return path.parallelStream()
                .mapToInt(point -> {
                    Set<GuardState> history = new HashSet<>();
                    Map<Point, Character> map = new HashMap<>(initialMap);
                    map.put(point, '#');
                    GuardState guard = startPosition;
                    while (!history.contains(guard)) {
                        history.add(guard);
                        Point ahead = guard.pointAhead();
                        if (map.get(ahead) == null) {
                            return 0;
                        } else if (map.get(ahead) == '#') {
                            guard = guard.turnRight();
                        } else {
                            guard = guard.advance();
                        }
                    }
                    return 1;
                })
                .sum();
    }

    private record GuardState(Point position, Direction direction) {
        public Point pointAhead() {
            return position.getNeighbour(direction);
        }

        public GuardState turnRight() {
            return new GuardState(position(), direction.turnRight());
        }

        public GuardState advance() {
            return new GuardState(pointAhead(), direction);
        }
    }

}