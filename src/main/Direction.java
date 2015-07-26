/**
 * Created by neelshah on 7/21/15.
 */
public enum Direction {
    NORTH(0),
    EAST(1),
    SOUTH(2),
    WEST(3);

    public int value;

    Direction(int value) {
        this.value = value;
    }
}