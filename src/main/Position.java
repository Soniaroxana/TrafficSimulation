import java.util.ArrayList;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class Position {
    public int x;
    public int y;
    public LocationType locationtype;
    public ArrayList<Direction> directions;

    public Position(int x,int y, LocationType locationType){
        this.x = x;
        this.y = y;
        this.locationtype = locationType;
        directions = new ArrayList<Direction>();
    }

}
