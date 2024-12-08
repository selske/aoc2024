package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

import java.util.*;
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
        List<String> lines = input.lines().toList();
        Map<Point, Character> letters = new HashMap<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                letters.put(new Point(x, y), line.charAt(x));
            }
        }
        results.setPart1(part1(letters));
        results.setPart2(part2(letters));
    }

    private static long part1(Map<Point, Character> letters) {
        return letters.entrySet().parallelStream()
                .filter(e -> e.getValue() == 'X')
                .mapToLong(e -> Arrays.stream(Direction.values())
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
                                Stream.of(Direction.UP_LEFT, Direction.DOWN_RIGHT),
                                Stream.of(Direction.DOWN_LEFT, Direction.UP_RIGHT)
                        )
                                     .map(directions -> directions
                                             .map(direction -> letters.get(e.getKey().getNeighbour(direction)))
                                             .collect(Collectors.toSet())
                                     )
                                     .filter(sm -> sm.equals(Set.of('S', 'M')))
                                     .count() == 2)
                .count();
    }

    private record Point(int x, int y) {

        public List<Point> getNeighbours(Direction direction, int number) {
            List<Point> neighbours = new ArrayList<>();
            Point previous = this;
            for (int i = 0; i < number; i++) {
                Point current = previous.getNeighbour(direction);
                neighbours.add(current);
                previous = current;
            }
            return neighbours;
        }

        private Point getNeighbour(Direction direction) {
            return switch (direction) {
                case UP -> new Point(x, y - 1);
                case UP_LEFT -> new Point(x - 1, y - 1);
                case UP_RIGHT -> new Point(x + 1, y - 1);
                case DOWN -> new Point(x, y + 1);
                case DOWN_LEFT -> new Point(x - 1, y + 1);
                case DOWN_RIGHT -> new Point(x + 1, y + 1);
                case LEFT -> new Point(x - 1, y);
                case RIGHT -> new Point(x + 1, y);
            };
        }

    }

    private enum Direction {
        UP,
        UP_LEFT,
        UP_RIGHT,
        DOWN,
        DOWN_LEFT,
        DOWN_RIGHT,
        LEFT,
        RIGHT,
    }

}