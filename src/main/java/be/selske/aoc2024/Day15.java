package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.Point;

import java.util.*;
import java.util.stream.Stream;

import static be.selske.aoc2024.util.map.CardinalDirections.*;
import static java.util.Collections.unmodifiableMap;
import static java.util.Map.entry;
import static java.util.stream.Collectors.toMap;

public class Day15 extends Day {
    public Day15() {
        super(15);
    }

    public static void main(String[] args) {
        new Day15()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("1527563")
                .verifyPart2("1521635");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<String> lines = input.lines().toList();
        Map<Point, Character> map = new HashMap<>();
        Point robot = null;
        int y = 0;
        while (y < lines.size()) {
            String line = lines.get(y);
            if (line.isBlank()) {
                break;
            }
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '.') continue;
                Point point = new Point(x, y);
                map.put(point, c);
                if (c == '@') {
                    robot = point;
                }
            }
            y++;
        }
        map = unmodifiableMap(map);
        if (robot == null) {
            throw new IllegalArgumentException("No robot found");
        }
        List<CardinalDirections> instructions = lines.stream()
                .skip(y + 1)
                .flatMapToInt(String::chars)
                .mapToObj(c -> switch (c) {
                    case '<' -> LEFT;
                    case '>' -> RIGHT;
                    case '^' -> UP;
                    case 'v' -> DOWN;
                    default -> throw new IllegalArgumentException("Unexpected value: " + c);
                })
                .toList();

        results.setPart1(part1(robot, instructions, map));
        results.setPart2(part2(robot, instructions, map));
    }

    private int part1(Point robot, List<CardinalDirections> instructions, Map<Point, Character> initialMap) {
        Map<Point, Character> map = new HashMap<>(initialMap);
        for (CardinalDirections direction : instructions) {
            robot = move(direction, robot, map);
        }
        return map.entrySet().stream()
                .filter(e -> e.getValue() == 'O')
                .map(Map.Entry::getKey)
                .mapToInt(point -> point.x() + 100 * point.y())
                .sum();
    }

    private int part2(Point robot, List<CardinalDirections> instructions, Map<Point, Character> initialMap) {
        Map<Point, Character> map = initialMap.entrySet().stream()
                .flatMap(e -> {
                    char c = e.getValue();
                    int x = e.getKey().x();
                    int y = e.getKey().y();
                    Point point = new Point(2 * x, y);
                    if (c == '@') {
                        return Stream.of(entry(point, c));
                    } else if (c == 'O') {
                        return Stream.of(entry(point, '['), entry(new Point(2 * x + 1, y), ']'));
                    } else if (c == '#') {
                        return Stream.of(entry(point, '#'), entry(new Point(2 * x + 1, y), '#'));
                    } else {
                        throw new IllegalStateException();
                    }
                })
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        robot = new Point(robot.x() * 2, robot.y());

        for (CardinalDirections direction : instructions) {
            robot = move(direction, robot, map);
        }

        return map.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() == '[')
                .map(Map.Entry::getKey)
                .mapToInt(point -> point.x() + 100 * point.y())
                .sum();
    }

    private static Point move(CardinalDirections direction, Point robot, Map<Point, Character> map) {
        if (direction == LEFT || direction == RIGHT) {
            robot = moveHorizontal(direction, robot, map).orElse(robot);
        } else {
            robot = moveVertical(direction, robot, map).orElse(robot);
        }
        return robot;
    }

    private static Optional<Point> moveHorizontal(CardinalDirections direction, Point point, Map<Point, Character> map) {
        Point newPosition = point.getNeighbour(direction);
        Character c = map.get(newPosition);
        if (c != null) {
            if (c == '#') {
                return Optional.empty();
            } else if (c == '[' || c == ']' || c == 'O') {
                boolean moved = moveHorizontal(direction, newPosition, map).isPresent();
                if (!moved) {
                    return Optional.empty();
                }
            }
        }
        map.put(newPosition, map.remove(point));
        return Optional.of(newPosition);
    }

    private static Optional<Point> moveVertical(CardinalDirections direction, Point robot, Map<Point, Character> map) {
        List<Point> level = List.of(robot);
        Stack<List<Point>> levels = new Stack<>();
        levels.push(level);

        while (!level.isEmpty()) {
            level = level.stream()
                    .flatMap(point -> {
                        Point newPosition = point.getNeighbour(direction);
                        return switch (map.get(newPosition)) {
                            case '#', 'O' -> Stream.of(newPosition);
                            case '[' -> Stream.of(newPosition, newPosition.getNeighbour(RIGHT));
                            case ']' -> Stream.of(newPosition.getNeighbour(LEFT), newPosition);
                            case null, default -> Stream.empty();
                        };
                    })
                    .distinct()
                    .toList();
            boolean canMove = level.stream().noneMatch(point -> map.get(point) == '#');
            if (!canMove) {
                return Optional.empty();
            }
            levels.push(level);
        }

        while (!levels.isEmpty()) {
            for (Point point : levels.pop()) {
                map.put(point.getNeighbour(direction), map.remove(point));
            }
        }
        return Optional.of(robot.getNeighbour(direction));
    }

}