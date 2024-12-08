package be.selske.aoc2024.util.map;

public interface Direction {

    Point getNeighbour(Point point);

    Direction turnRight();

}
