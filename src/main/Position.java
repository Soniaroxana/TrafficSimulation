import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by soniamarginean on 7/30/15.
 */
// Class that models a position on the map
public class Position {
    // A position is characterized by is x,y coordinates on the map, its location type and the direction(s) that it can lead to
    // A position can be occupied by one car and one car only, condition that is achieved by using a semaphore
    public int x;
    public int y;
    public LocationType locationtype;
    public ArrayList<Direction> directions;
    public Semaphore isOccupied;

    public Position(int x,int y, LocationType locationType){
        this.x = x;
        this.y = y;
        this.locationtype = locationType;
        directions = new ArrayList<Direction>();
        this.isOccupied = new Semaphore(1);
    }

    public boolean equals(Position pos){
        return ((this.x == pos.x) && (this.y == pos.y));
    }

    //Method to enter (acquire) a position
    public void enter(){
        try {
            isOccupied.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Method to exit (release) a position
    public void exit(){
        isOccupied.release();
    }
}
