/**
 * Created by soniamarginean on 7/30/15.
 */
//Map location types enum. A location on the map can either be on a LANE, in an INTERSECTION or in teh SCENERY
public enum LocationType {
    LANE(0),
    INTERSECTION(1),
    SCENERY(2);

    public int value;

    LocationType(int value) {
        this.value = value;
    }
}
