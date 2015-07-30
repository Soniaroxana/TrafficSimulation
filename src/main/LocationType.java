/**
 * Created by soniamarginean on 7/30/15.
 */
public enum LocationType {
    LANE(0),
    INTERSECTION(1),
    SCENERY(2);

    public int value;

    LocationType(int value) {
        this.value = value;
    }
}
