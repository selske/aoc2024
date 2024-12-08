package be.selske.aoc2024.util.map;

import java.util.ArrayList;
import java.util.List;

public record Point(int x, int y) {

    public Point getNeighbour(Direction direction) {
        return direction.getNeighbour(this);
    }

    public List<Point> getNeighbours(Direction direction, int number) {
        List<Point> neighbours = new ArrayList<>();
        Point previous = this;
        for (int i = 0; i < number; i++) {
            Point current = direction.getNeighbour(previous);
            neighbours.add(current);
            previous = current;
        }
        return neighbours;
    }

}
