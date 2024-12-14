package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;
import be.selske.aoc2024.util.map.PrincipalDirections;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.selske.aoc2024.util.map.PrincipalDirections.*;
import static java.lang.Integer.parseInt;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class Day14 extends Day {

    public Day14() {
        super(14, "11,7", "101,103");
    }

    public static void main(String[] args) {
        new Day14()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("231782040")
                .verifyPart2("6475");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Pattern pattern = Pattern.compile("p=(?<px>-?\\d+),(?<py>-?\\d+) v=(?<vx>-?\\d+),(?<vy>-?\\d+)");
        int sizeX = parseInt(parameter.split(",")[0]);
        int sizeY = parseInt(parameter.split(",")[1]);

        List<Robot> robots = input.lines()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> new Robot(
                        parseInt(matcher.group("px")),
                        parseInt(matcher.group("py")),
                        parseInt(matcher.group("vx")),
                        parseInt(matcher.group("vy"))
                ))
                .toList();
        results.setPart1(part1(robots, sizeX, sizeY));
        results.setPart2(part2(robots, sizeX, sizeY));
    }

    private int part1(List<Robot> robots, int sizeX, int sizeY) {
        int seconds = 100;
        int xMiddle = sizeX / 2;
        int yMiddle = sizeY / 2;
        Map<PrincipalDirections, List<Point>> quadrants = robots.stream()
                .map(robot -> positionAfterSeconds(robot, seconds, sizeX, sizeY))
                .collect(groupingBy(point -> quadrant(point, xMiddle, yMiddle)));

        return quadrants.entrySet().stream()
                .filter(not(entry -> entry.getKey().equals(UP)))
                .mapToInt(e -> e.getValue().size())
                .reduce(1, (a, b) -> a * b);
    }

    private int part2(List<Robot> robots, int sizeX, int sizeY) {
        if (sizeX < 31) {
            return -1;
        }
        Set<Point> tree = getTreePoints();
        int i = 1;
        while (true) {
            int seconds = i;
            Set<Point> positions = robots.stream()
                    .map(robot -> positionAfterSeconds(robot, seconds, sizeX, sizeY))
                    .collect(toSet());

            if (containsImage(positions, tree)) {
                return seconds;
            }
            i++;
        }
    }

    private boolean containsImage(Set<Point> positions, Set<Point> image) {
        return positions.stream()
                .anyMatch(position -> image.stream()
                        .allMatch(point -> positions.contains(new Point(position.x() + point.x(), position.y() + point.y())))
                );
    }

    private static Set<Point> getTreePoints() {
        String tree = """
                XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                X                             X
                X                             X
                X                             X
                X                             X
                X              X              X
                X             XXX             X
                X            XXXXX            X
                X           XXXXXXX           X
                X          XXXXXXXXX          X
                X            XXXXX            X
                X           XXXXXXX           X
                X          XXXXXXXXX          X
                X         XXXXXXXXXXX         X
                X        XXXXXXXXXXXXX        X
                X          XXXXXXXXX          X
                X         XXXXXXXXXXX         X
                X        XXXXXXXXXXXXX        X
                X       XXXXXXXXXXXXXXX       X
                X      XXXXXXXXXXXXXXXXX      X
                X        XXXXXXXXXXXXX        X
                X       XXXXXXXXXXXXXXX       X
                X      XXXXXXXXXXXXXXXXX      X
                X     XXXXXXXXXXXXXXXXXXX     X
                X    XXXXXXXXXXXXXXXXXXXXX    X
                X             XXX             X
                X             XXX             X
                X             XXX             X
                X                             X
                X                             X
                X                             X
                X                             X
                XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                """;

        Set<Point> treePoints = new HashSet<>();
        MapParser.parse(tree, (point, c) -> {
            if (c == 'X') {
                treePoints.add(point);
            }
        });
        return treePoints;
    }

    private static Point positionAfterSeconds(Robot robot, int seconds, int sizeX, int sizeY) {
        int x = (robot.vx() * seconds + robot.px()) % sizeX;
        if (x < 0) {
            x += sizeX;
        }

        int y = (robot.vy() * seconds + robot.py()) % sizeY;
        if (y < 0) {
            y += sizeY;
        }
        return new Point(x, y);
    }

    private record Robot(int px, int py, int vx, int vy) {}

    private PrincipalDirections quadrant(Point point, int xMiddle, int yMiddle) {
        if (point.x() < xMiddle) {
            if (point.y() < yMiddle) {
                return UP_LEFT;
            } else if (point.y() > yMiddle) {
                return DOWN_LEFT;
            }
        } else if (point.x() > xMiddle) {
            if (point.y() < yMiddle) {
                return UP_RIGHT;
            } else if (point.y() > yMiddle) {
                return DOWN_RIGHT;
            }
        }
        return UP;
    }

}