package be.selske.aoc2024.util.maze;


import be.selske.aoc2024.util.map.Point;

import java.util.*;


public record Maze(
       Point start,
       Point end,
       Map<Point, Map<Point, Integer>> distances
) {

   public Set<Point> points() {
       return distances.keySet();
   }

   public Collection<PointDistance> reachableNeighbours(Point node) {
       return distances.get(node).entrySet().stream()
               .map(connection -> new PointDistance(connection.getKey(), connection.getValue()))
               .toList();
   }

//   public static ReducedMaze reduce(Maze maze) {
//       Map<Point, Map<Point, Integer>> distances = new HashMap<>();
//       for (Point node : maze.nodes()) {
//           HashMap<Point, Integer> connections = new HashMap<>();
//           distances.put(node, connections);
//
//
//           node.getPossibleNeighbors().stream()
//                   .filter((maze.nodes())::contains)
//                   .forEach(n -> connections.put(n, 1));
//       }
//
//
//       Set<Point> distancePoints = new HashSet<>(distances.keySet());
//       for (Point node : distancePoints) {
//           if (node.equals(maze.start())) {
//               continue;
//           }
//           Map<Point, Integer> connections = distances.get(node);
//           if (connections.size() == 2) {
//               Iterator<Point> connectionIterator = connections.keySet().iterator();
//               Point n1 = connectionIterator.next();
//               Point n2 = connectionIterator.next();
//               // add n2 to n1 as weight to node +1
//               Integer distanceToPoint1 = distances.get(n1).get(node);
//               Integer distanceToPoint2 = distances.get(n2).get(node);
//
//
//               distances.get(n1).put(n2, distanceToPoint1 + distanceToPoint2);
//               distances.get(n2).put(n1, distanceToPoint2 + distanceToPoint1);
//
//
//               distances.remove(node);
//               distances.get(n1).remove(node);
//               distances.get(n2).remove(node);
//           }
//       }
//
//
//       return new ReducedMaze(maze.start(), maze.end(), distances);
//   }

    public record PointDistance(Point point, int distance) {}

}



