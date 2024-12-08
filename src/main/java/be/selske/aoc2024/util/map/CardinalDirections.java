package be.selske.aoc2024.util.map;

public enum CardinalDirections implements Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT,
    ;

    @Override
    public Point getNeighbour(Point point) {
            return switch (this) {
                case UP -> new Point(point.x(), point.y() - 1);
                case DOWN -> new Point(point.x(), point.y() + 1);
                case LEFT -> new Point(point.x() - 1, point.y());
                case RIGHT -> new Point(point.x() + 1, point.y());
            };
    }

    public CardinalDirections turnRight() {
        return CardinalDirections.values()[(this.ordinal() + 1) % CardinalDirections.values().length];
    }

}
