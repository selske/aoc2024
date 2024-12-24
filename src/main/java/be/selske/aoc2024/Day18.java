package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.Point;
import be.selske.aoc2024.util.maze.DijkstraPathfinder;
import be.selske.aoc2024.util.maze.Maze;

import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toMap;

public class Day18 extends Day {
    public Day18() {
        super(18, "7", "71");
    }

    public static void main(String[] args) {
        new Day18()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1(null)
                .verifyPart2(null);
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Set<Point> map = new HashSet<>();
        int size = parseInt(parameter);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                map.add(new Point(x, y));
            }
        }
        List<String> lines = input.lines().toList();
        lines.stream()
                .limit(1024)
                .forEach(line -> {
                    String[] coords = line.split(",");
                    int x = parseInt(coords[0]);
                    int y = parseInt(coords[1]);
                    map.remove(new Point(x, y));
                });

        Map<Point, Map<Point, Integer>> distances = map.stream().collect(toMap(Function.identity(), point -> Arrays.stream(CardinalDirections.values())
                .map(point::getNeighbour)
                .filter(map::contains)
                .collect(toMap(Function.identity(), _ -> 1))
        ));

        Maze maze = new Maze(new Point(0, 0), new Point(size - 1, size - 1), distances);

        List<Point> path = new DijkstraPathfinder().findPath(maze);

        results.setPart1(path.size() - 1);

        for (int i = 1024; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] coords = line.split(",");
            int x = parseInt(coords[0]);
            int y = parseInt(coords[1]);

            Point p = new Point(x, y);
            distances.remove(p);
            Arrays.stream(CardinalDirections.values())
                    .map(p::getNeighbour)
                    .filter(distances::containsKey)
                    .forEach(neighbour -> distances.get(neighbour).remove(p));

            path = new DijkstraPathfinder().findPath(maze);
            if (path.isEmpty()) {
                results.setPart2(x + "," + y);
                return;
            }
        }
    }


    private void print(Set<Point> map, int size) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (map.contains(new Point(x, y))) {
                    System.out.print('.');
                } else {
                    System.out.print('#');
                }
            }
            System.out.println();
        }
    }

}