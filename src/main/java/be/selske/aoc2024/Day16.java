package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;
import be.selske.aoc2024.util.maze.DijkstraPathfinder;
import be.selske.aoc2024.util.maze.Maze;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static be.selske.aoc2024.util.map.CardinalDirections.*;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.util.function.Predicate.not;

public class Day16 extends Day {
    public Day16() {
        super(16);
    }

    public static void main(String[] args) {
        new Day16()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("94444")
                .verifyPart2(null); // 492 too low
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Input i = parse(input);

        List<Point> path = new DijkstraPathfinder().findPath(i.maze);

        results.setPart1(part1(path, i.maze));
        results.setPart2(part2(i.maze, path, i.map));
    }

    private static int part1(List<Point> path, Maze maze) {
        return cost(path, maze);
    }

    private static int cost(List<Point> path, Maze maze) {
        Point previous = path.getFirst();
        int cost = 0;
        for (int i = 1; i < path.size(); i++) {
            Point current = path.get(i);
            cost += maze.distances().get(previous).get(current);
            previous = current;
        }
        return cost;
    }

    private int part2(Maze maze, List<Point> path, Set<Point> map) {
        int cost = cost(path, maze);
        Set<Point> allPoints = new HashSet<>(expand(path, map));
        for (int i = 1; i < path.size() - 1; i++) {
            Point pointToFilter = path.get(i);
            System.out.println("skipping " + pointToFilter);
            List<Point> p = new DijkstraPathfinder().findPath(maze, pointToFilter);
            if (p.isEmpty()) {
                System.out.println("No solution");
                continue;
            }
            int newCost = cost(p, maze);
            if (newCost > cost) {
                System.out.println("More expensive solution");
                continue;
            }
            p = expand(p, map);
            allPoints.addAll(p);
            System.out.println(newCost);
            print(maze, p, map);
        }

        print(maze, allPoints, map);
        return allPoints.size();
    }

    private List<Point> expand(List<Point> path, Set<Point> map) {
        List<Point> newPath = new ArrayList<>();
        Point previous = path.getFirst();
        newPath.add(previous);
        for (int i = 1; i < path.size(); i++) {
            Point current = path.get(i);
            if (previous.x() == current.x()) {
                int maxY = max(previous.y(), current.y());
                int minY = min(previous.y(), current.y());
                for (int y = minY + 1; y < maxY; y++) {
                    newPath.add(new Point(current.x(), y));
                }
            } else if (previous.y() == current.y()) {
                int maxX = max(previous.x(), current.x());
                int minX = min(previous.x(), current.x());
                for (int x = minX + 1; x < maxX; x++) {
                    newPath.add(new Point(x, current.y()));
                }
            } else {
                Point point = Stream.of(
                                new Point(current.x(), previous.y()),
                                new Point(previous.x(), current.y())
                        )
                        .filter(map::contains)
                        .findFirst().orElseThrow();
                newPath.add(point);
            }
            previous = current;
            newPath.add(previous);
        }
        return newPath;
    }

    private void print(Maze maze, Collection<Point> path, Set<Point> map) {
        Set<Point> nodes = new HashSet<>(path);
        int width = maze.distances().keySet().stream()
                .mapToInt(Point::x)
                .max().orElseThrow();
        int height = maze.distances().keySet().stream()
                .mapToInt(Point::y)
                .max().orElseThrow();

        for (int y = 0; y < height + 5; y++) {
            for (int x = 0; x < width + 5; x++) {
                if (maze.start().equals(new Point(x, y))) {
                    System.out.print('S');
                } else if (maze.end().equals(new Point(x, y))) {
                    System.out.print("F");
                } else if (!map.contains(new Point(x, y))) {
                    System.out.print(' ');
                } else if (nodes.contains(new Point(x, y))) {
                    System.out.print('•');
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
    }


    private static Input parse(String input) {
        Set<Point> originalMap = new HashSet<>();
        AtomicReference<Point> start = new AtomicReference<>();
        AtomicReference<Point> end = new AtomicReference<>();

        MapParser.parse(input, (point, c) -> {
            if (c == '#') {
                return;
            }
            originalMap.add(point);
            if (c == 'S') {
                start.set(point);
            } else if (c == 'E') {
                end.set(point);
            }
        });

        Map<Point, Map<Point, Integer>> distances = new HashMap<>();
        Set<Point> map = new HashSet<>(originalMap);
        List<Point> turns = map.stream()
                .filter(not(start.get()::equals))
                .filter(not(end.get()::equals))
                .filter(point -> {
                    List<Point> verticalNeighbours = Stream.of(UP, DOWN).map(point::getNeighbour).filter(map::contains).toList();
                    List<Point> horizontalNeighbours = Stream.of(LEFT, RIGHT).map(point::getNeighbour).filter(map::contains).toList();

                    if (verticalNeighbours.size() == 2 && !horizontalNeighbours.isEmpty()) {
                        Point first = verticalNeighbours.getFirst();
                        Point last = verticalNeighbours.getLast();
                        distances.computeIfAbsent(first, _ -> new HashMap<>()).put(last, 2);
                        distances.computeIfAbsent(last, _ -> new HashMap<>()).put(first, 2);
                    }
                    if (horizontalNeighbours.size() == 2 && !verticalNeighbours.isEmpty()) {
                        Point first = horizontalNeighbours.getFirst();
                        Point last = horizontalNeighbours.getLast();
                        distances.computeIfAbsent(first, _ -> new HashMap<>()).put(last, 2);
                        distances.computeIfAbsent(last, _ -> new HashMap<>()).put(first, 2);
                    }
                    verticalNeighbours.forEach(v -> {
                        horizontalNeighbours.forEach(h -> {
                            distances.computeIfAbsent(h, _ -> new HashMap<>()).put(v, 1002);
                            distances.computeIfAbsent(v, _ -> new HashMap<>()).put(h, 1002);
                        });
                    });

                    return !verticalNeighbours.isEmpty() && !horizontalNeighbours.isEmpty();
                })
                .toList();
        map.removeAll(turns);
        Arrays.stream(CardinalDirections.values())
                .filter(not(RIGHT::equals))
                .map(start.get()::getNeighbour)
                .filter(map::contains)
                .forEach(point -> {
                    distances.computeIfAbsent(start.get(), _ -> new HashMap<>()).put(point, 1001);
                    distances.computeIfAbsent(point, _ -> new HashMap<>()).put(start.get(), 1001);
                });
        Point rightNeighbour = start.get().getNeighbour(RIGHT);
        if (map.contains(rightNeighbour)) {
            distances.computeIfAbsent(start.get(), _ -> new HashMap<>()).put(rightNeighbour, 1);
            distances.computeIfAbsent(rightNeighbour, _ -> new HashMap<>()).put(start.get(), 1);
        }
        map.remove(start.get());

        map.forEach(point -> Arrays.stream(CardinalDirections.values())
                .map(point::getNeighbour)
                .filter(map::contains)
                .forEach(neighbour -> {
                    distances.computeIfAbsent(point, _ -> new HashMap<>()).put(neighbour, 1);
                }));

        System.out.println(distances.containsKey(new Point(1, 11)));
        System.out.println(distances.values().stream().flatMap(e -> e.keySet().stream()).anyMatch(p -> p.equals(new Point(1, 11))));

        return new Input(
                new Maze(start.get(), end.get(), distances),
                originalMap
        );
    }

    record Input(Maze maze, Set<Point> map) {

    }

}