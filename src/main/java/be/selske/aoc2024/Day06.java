package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.*;

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
        List<String> lines = input.lines().toList();

        Map<Point, Character> map = new HashMap<>();
        GuardState guard = null;
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '^') {
                    guard = new GuardState(new Point(x, y), Direction.UP);
                }
                map.put(new Point(x, y), c);
            }
        }
        if (guard == null) {
            throw new IllegalArgumentException("No guard found");
        }

        List<Point> path = part1(guard, map);
        results.setPart1(path.size());
        results.setPart2(part2(guard, map, path));
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

    private record Point(int x, int y) {

        private Point getNeighbour(Direction direction) {
            return switch (direction) {
                case UP -> new Point(x, y - 1);
                case DOWN -> new Point(x, y + 1);
                case LEFT -> new Point(x - 1, y);
                case RIGHT -> new Point(x + 1, y);
            };
        }

    }

    private enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT,
        ;

        public Direction turnRight() {
            return Direction.values()[(this.ordinal() + 1) % Direction.values().length];
        }
    }

}