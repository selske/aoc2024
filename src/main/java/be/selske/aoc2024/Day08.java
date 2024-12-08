package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;

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
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Point point = new Point(x, y);
                if (c != '.') {
                    antennas.computeIfAbsent(c, _ -> new ArrayList<>()).add(point);
                }
            }
        }

        int width = lines.getFirst().length();
        int height = lines.size();
        results.setPart1(getPart1(antennas, width, height));
        results.setPart2(getPart2(antennas, width, height));
    }

    private static long getPart1(Map<Character, List<Point>> antennas, int width, int height) {
        Set<Point> antiNodes = new HashSet<>();
        antennas.values().forEach(nodes -> {
            for (Point a : nodes) {
                for (Point b : nodes) {
                    if (a.equals(b)) continue;
                    int deltaX = b.x() - a.x();
                    int deltaY = b.y() - a.y();
                    Point antiNode = new Point(a.x() - deltaX, a.y() - deltaY);

                    if (antiNode.x() >= 0 && antiNode.y() >= 0 && antiNode.x() < width && antiNode.y() < height) {
                        antiNodes.add(antiNode);
                    }
                }
            }
        });
        return antiNodes.size();
    }

    private static long getPart2(Map<Character, List<Point>> antennas, int width, int height) {
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
                        if (antiNode.x() >= 0 && antiNode.y() >= 0 && antiNode.x() < width && antiNode.y() < height) {
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

    private record Point(int x, int y) {}

}