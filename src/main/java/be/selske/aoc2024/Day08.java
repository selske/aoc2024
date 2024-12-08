package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.MapSize;
import be.selske.aoc2024.util.map.Point;

import java.util.*;

public class Day08 extends Day {


    public Day08() {
        super(8);
    }

    public static void main(String[] args) {
        new Day08()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("289")
                .verifyPart2("1030");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        List<String> lines = input.lines().toList();

        Map<Character, List<Point>> antennas = new HashMap<>();
        MapSize mapSize = MapParser.parse(input, (point, c) -> {
            if (c != '.') {
                antennas.computeIfAbsent(c, _ -> new ArrayList<>()).add(point);
            }
        });

        results.setPart1(getPart1(antennas, mapSize));
        results.setPart2(getPart2(antennas, mapSize));
    }

    private static long getPart1(Map<Character, List<Point>> antennas, MapSize mapSize) {
        Set<Point> antiNodes = new HashSet<>();
        antennas.values().forEach(nodes -> {
            for (Point a : nodes) {
                for (Point b : nodes) {
                    if (a.equals(b)) continue;
                    int deltaX = b.x() - a.x();
                    int deltaY = b.y() - a.y();
                    Point antiNode = new Point(a.x() - deltaX, a.y() - deltaY);

                    if (antiNode.x() >= 0 && antiNode.y() >= 0 && antiNode.x() < mapSize.width() && antiNode.y() < mapSize.height()) {
                        antiNodes.add(antiNode);
                    }
                }
            }
        });
        return antiNodes.size();
    }

    private static long getPart2(Map<Character, List<Point>> antennas, MapSize mapSize) {
        Set<Point> antiNodes = new HashSet<>();
        antennas.values().forEach(nodes -> {
            for (Point a : nodes) {
                for (Point b : nodes) {
                    if (a.equals(b)) continue;
                    int deltaX = b.x() - a.x();
                    int deltaY = b.y() - a.y();
                    int offset = 0;
                    while (true) {
                        Point antiNode = new Point(a.x() - deltaX * offset, a.y() - deltaY * offset);
                        if (antiNode.x() >= 0 && antiNode.y() >= 0 && antiNode.x() < mapSize.width() && antiNode.y() < mapSize.height()) {
                            antiNodes.add(antiNode);
                        } else {
                            break;
                        }
                        offset++;
                    }
                }
            }
        });
        return antiNodes.size();
    }

}