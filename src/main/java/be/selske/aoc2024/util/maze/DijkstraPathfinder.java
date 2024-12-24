package be.selske.aoc2024.util.maze;


import be.selske.aoc2024.util.map.Point;
import be.selske.aoc2024.util.maze.Maze.PointDistance;

import java.util.*;

import static java.util.Comparator.comparingInt;


public class DijkstraPathfinder {

    public List<Point> findPath(Maze maze) {
        Map<Point, Integer> distances = initDistances(maze);

        Map<Point, Point> previous = new HashMap<>();
        Queue<PointDistance> queue = new PriorityQueue<>(comparingInt(PointDistance::distance));
        queue.offer(new PointDistance(maze.start(), 0));


        while (!queue.isEmpty()) {
            PointDistance u = queue.poll();
            if (u.distance() != distances.get(u.point())) continue;
            for (PointDistance nd : maze.reachableNeighbours(u.point())) {
                Point v = nd.point();
                int newDistance = distances.getOrDefault(u.point(), Integer.MAX_VALUE) + nd.distance();
                if (newDistance < distances.getOrDefault(v, Integer.MAX_VALUE)) {
                    distances.put(v, newDistance);
                    previous.put(v, u.point());
                    queue.offer(new PointDistance(v, newDistance));
                }
            }
        }

        return getShortestPath(maze, previous);
    }


    private static Map<Point, Integer> initDistances(Maze maze) {
        Map<Point, Integer> distances = new HashMap<>();
        distances.put(maze.start(), 0);
        return distances;
    }


    private static List<Point> getShortestPath(Maze maze, Map<Point, Point> previous) {
        List<Point> path = new ArrayList<>();
        Point current = maze.end();
        while (!current.equals(maze.start())) {
            path.add(current);
            current = previous.get(current);
            if (current == null) {
                return Collections.emptyList();
            }
        }
        path.add(maze.start());

        return path.reversed();
    }


}






