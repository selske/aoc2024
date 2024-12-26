package be.selske.aoc2024;

import be.selske.aoc2024.benchmark.Day;
import be.selske.aoc2024.util.map.CardinalDirections;
import be.selske.aoc2024.util.map.MapParser;
import be.selske.aoc2024.util.map.Point;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Integer.parseInt;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

public class Day20 extends Day {
    public Day20() {
        super(20, "50", "100");
    }

    public static void main(String[] args) {
        new Day20()
                .example()
                .puzzle()
                .benchmark()
                .verifyPart1("1289")
                .verifyPart2("982425");
    }

    @Override
    protected void solve(ResultContainer results, String input, String parameter) {
        Set<Point> raceTrack = new HashSet<>();
        Set<Point> walls = new HashSet<>();
        AtomicReference<Point> start = new AtomicReference<>();
        AtomicReference<Point> end = new AtomicReference<>();
        MapParser.parse(input, (point, c) -> {
            if (c == '#') {
                walls.add(point);
                return;
            } else if (c == 'S') {
                start.set(point);
            } else if (c == 'E') {
                end.set(point);
            }
            raceTrack.add(point);
        });

        Route route = race(end.get(), raceTrack, new Route(start.get(), List.of(start.get()), 0));

        Map<Point, List<Point>> remainingRoutes = new HashMap<>();
        for (int i = 0; i < route.route().size(); i++) {
            remainingRoutes.put(route.route().get(i), route.route.subList(i + 1, route.route().size()));
        }

        results.setPart1(part2(route, raceTrack, walls, remainingRoutes, 2, parseInt(parameter)));
        results.setPart2(part2(route, raceTrack, walls, remainingRoutes, 20, parseInt(parameter)));
    }

    private static long part1(Route route, Set<Point> raceTrack, Map<Point, List<Point>> remainingRoutes) {
        List<Route> cuts = route.route().parallelStream()
                .flatMap(point -> Arrays.stream(CardinalDirections.values())
                        .map(direction -> {
                            Point neighbour = point.getNeighbour(direction);
                            if (!raceTrack.contains(neighbour)) {
                                Point nextNeighbour = neighbour.getNeighbour(direction);
                                if (raceTrack.contains(nextNeighbour)) {
                                    List<Point> cutRoute = route.route().subList(0, route.route().indexOf(point));
                                    if (!cutRoute.contains(nextNeighbour)) {
                                        List<Point> newRoute = new ArrayList<>(cutRoute);
                                        newRoute.add(neighbour);
                                        newRoute.add(nextNeighbour);
                                        return new Route(nextNeighbour, newRoute, newRoute.size());
                                    }
                                }
                            }
                            return null;
                        }))
                .filter(Objects::nonNull)
                .toList();

        return cuts.stream()
                .mapToInt(cut -> {
                    List<Point> remainingRoute = remainingRoutes.get(cut.current());
                    return route.time() - (cut.time() + remainingRoute.size());
                })
                .filter(gain -> gain >= 100)
                .count();
    }


    private static long part2(Route route, Set<Point> raceTrack, Set<Point> walls, Map<Point, List<Point>> remainingRoutes, int cutSize, int minGain) {
        return route.route().parallelStream()
                .flatMap(point -> {
                    int routeTime = route.route().indexOf(point);

                    List<RouteTime> newCuts = new ArrayList<>();
                    Set<Point> flooded = new HashSet<>(Set.of(point));
                    Set<Point> currentLevel = Set.of(point);
                    int shortCutTime = 0;
                    while (shortCutTime < cutSize) {
                        Set<Point> nextLevel = currentLevel.stream()
                                .flatMap(current -> Arrays.stream(CardinalDirections.values()).map(current::getNeighbour))
                                .distinct()
                                .filter(not(flooded::contains))
                                .filter(p -> walls.contains(p) || raceTrack.contains(p))
                                .collect(toSet());
                        flooded.addAll(nextLevel);

                        int currentTime = routeTime + (++shortCutTime);
                        nextLevel.stream()
                                .filter(raceTrack::contains)
                                .map(end -> new RouteTime(end, currentTime))
                                .forEach(newCuts::add);
                        currentLevel = nextLevel;
                    }
                    return newCuts.stream();
                })
                .mapToInt(cut -> {
                    List<Point> remainingRoute = remainingRoutes.get(cut.current());
                    return route.time() - (cut.time() + remainingRoute.size());
                })
                .filter(gain -> gain >= minGain)
                .count();
    }

    private static Route race(Point end, Set<Point> raceTrack, Route initialRoute) {
        List<Route> routes = List.of(initialRoute);

        while (routes.stream().noneMatch(route -> route.route().contains(end))) {
            routes = routes.stream()
                    .flatMap(route -> Arrays.stream(CardinalDirections.values())
                            .map(route.current()::getNeighbour)
                            .filter(raceTrack::contains)
                            .filter(not(route.route()::contains))
                            .map(neighbour -> {
                                List<Point> newRoute = new ArrayList<>(route.route());
                                newRoute.add(neighbour);
                                return new Route(neighbour, newRoute, route.time() + 1);
                            })
                    )
                    .toList();
        }
        return routes.stream().filter(route -> route.route.contains(end)).findFirst().orElseThrow();
    }


    private record Route(Point current, List<Point> route, int time) {

    }

    private record RouteTime(Point current, int time) {

    }

}