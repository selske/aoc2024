package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.Point;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class Day13 extends Day {

    public Day13() {
        super(13);
    }

    public static void main(String[] args) {
        new Day13()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("28059")
                .verifyPart2("102255878088512");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Pattern buttonPattern = Pattern.compile("Button .: X\\+(?<x>\\d+), Y\\+(?<y>\\d+)");
        Pattern prizePattern = Pattern.compile("Prize: X=(?<x>\\d+), Y=(?<y>\\d+)");

        List<String> lines = input.lines().toList();
        List<Game> games = IntStream.iterate(0, i -> i < lines.size(), i -> i + 4)
                .mapToObj(i -> {
                    Point a = parsePoint(buttonPattern, lines.get(i));
                    Point b = parsePoint(buttonPattern, lines.get(i + 1));
                    Point prize = parsePoint(prizePattern, lines.get(i + 2));
                    return new Game(a, b, prize);
                })
                .toList();

        results.setPart1(part1(games));
        results.setPart2(part2(games));
    }

    private static long part1(List<Game> games) {
        return games.stream()
                .mapToLong(game -> solve(game, 0))
                .sum();
    }

    private static long part2(List<Game> games) {
        return games.stream()
                .mapToLong(game -> solve(game, 10000000000000L))
                .sum();
    }

    private static long solve(Game game, long modifier) {
        Point aButton = game.a();
        Point bButton = game.b();

        long ax = aButton.x();
        long ay = aButton.y();
        long bx = bButton.x();
        long by = bButton.y();
        long x = game.prize().x() + modifier;
        long y = game.prize().y() + modifier;

        long a = (x * by - y * bx) / (by * ax - ay * bx);
        long b = (x * ay - ax * y) / (ay * bx - by * ax);

        long xCheck = a * ax + b * bx;
        long yCheck = a * ay + b * by;
        if (xCheck == x && yCheck == y) {
            return a * 3 + b;
        } else {
            return 0;
        }
    }

    private static Point parsePoint(Pattern pattern, String line) {
        Matcher aMatcher = pattern.matcher(line);
        if (!aMatcher.find()) throw new IllegalArgumentException();
        return new Point(parseInt(aMatcher.group("x")), parseInt(aMatcher.group("y")));
    }

    private record Game(Point a, Point b, Point prize) {}

}