import java.util.ArrayList;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class Map {
    //create a 100 by 100 positions map
    //each intersection has 4 positions
    //each road is 2 positions wide, one for each lane
    //initially one intersection and two roads
    private ArrayList<ArrayList<Position>> mapLocations;

    public Map(int m, int n){
    }

    public Direction getPositionDirection(int x, int y){
        return Direction.NORTH;
    }

}
