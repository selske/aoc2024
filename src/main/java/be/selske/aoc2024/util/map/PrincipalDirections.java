package be.selske.aoc2024.util.map;

public enum PrincipalDirections implements Direction {
    UP,
    UP_RIGHT,
    RIGHT,
    DOWN_RIGHT,
    DOWN,
    DOWN_LEFT,
    LEFT,
    UP_LEFT,
    ;

    @Override
    public Point getNeighbour(Point point) {
        return switch (this) {
            case UP -> new Point(point.x(), point.y() - 1);
            case UP_LEFT -> new Point(point.x() - 1, point.y() - 1);
            case UP_RIGHT -> new Point(point.x() + 1, point.y() - 1);
            case DOWN -> new Point(point.x(), point.y() + 1);
            case DOWN_LEFT -> new Point(point.x() - 1, point.y() + 1);
            case DOWN_RIGHT -> new Point(point.x() + 1, point.y() + 1);
            case LEFT -> new Point(point.x() - 1, point.y());
            case RIGHT -> new Point(point.x() + 1, point.y());
        };
    }

    public PrincipalDirections turnRight() {
        return PrincipalDirections.values()[(this.ordinal() + 1) % PrincipalDirections.values().length];
    }

}
