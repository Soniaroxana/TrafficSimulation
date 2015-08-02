import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neelshah on 7/21/15.
 */
// Class that simulates an intersection
public class Intersection {
    // light model that it uses : timed or adaptive (based on traffic)
    public LightModel lightModel;
    // barriers protecting the intersection
    public Barrier[] barriers;
    // locks cars need to hold to cross the intersection
    public Lock[] locks;
    // id of intersection
    public int id;
    // how many positions the intersection takes on any direction
    public long length;
    // positions that this intersection holds on the map
    public ArrayList<Position> locations;


    public Intersection(LightModel lightModel, int id) {
        this.lightModel = lightModel;
        this.barriers = lightModel.barriers;
        // Each intersection has a lock for each of the 4 directions
        this.locks = new Lock[] {
                new ReentrantLock(),//replace this with MyLock and alternate there
                new ReentrantLock(),
                new ReentrantLock(),
                new ReentrantLock()
        };

        this.id = id;
        this.length = 2; //positions
    }

    public Intersection(LightModel lightModel, int id, ArrayList<Position> positions) {
        this.lightModel = lightModel;
        this.barriers = lightModel.barriers;
        // Each intersection has a lock for each of the 4 directions
        this.locks = new Lock[] {
                new ReentrantLock(),//replace this with MyLock and alternate there
                new ReentrantLock(),
                new ReentrantLock(),
                new ReentrantLock()
        };

        this.id = id;
        this.length = 2; //positions
        this.locations = positions;
    }

    public void begin() {}

    public void end() {}

    public Barrier accept(Car car) {
        for(Barrier barrier : barriers) {
            if (barrier.directions.contains(car.direction)) {
                barrier.add(car);
                return barrier;
            }
        }

        return null;
    }

    public Barrier remove(Car car) {
        for(Barrier barrier : barriers) {
            if (barrier.directions.contains(car.direction)) {
                barrier.remove(car);
                return barrier;
            }
        }

        return null;
    }
}