/**
 * Created by neelshah on 7/21/15.
 */
// The 4 directions that cars can travel to
public enum Direction {
    NORTH(0),
    EAST(1),
    SOUTH(2),
    WEST(3);

    public int value;

    Direction(int value) {
        this.value = value;
    }

    public static Direction valueOf(int value) {
        switch (value) {
            case 0 : return NORTH;
            case 1 : return EAST;
            case 2 : return SOUTH;
            case 3 : return WEST;
            default: return null;
        }
    }
}