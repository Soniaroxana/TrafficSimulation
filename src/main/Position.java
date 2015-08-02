import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class Position {
    public int x;
    public int y;
    public LocationType locationtype;
    public ArrayList<Direction> directions;
    public Semaphore isOccupied;
    public ReentrantLock lock;

    public Position(int x,int y, LocationType locationType){
        this.x = x;
        this.y = y;
        this.locationtype = locationType;
        this.lock = new ReentrantLock();
        directions = new ArrayList<Direction>();
        this.isOccupied = new Semaphore(1);
    }

    public boolean equals(Position pos){
        return ((this.x == pos.x) && (this.y == pos.y));
    }

    public void enter(){
        try {
            isOccupied.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exit(){
        isOccupied.release();
    }
}
