import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
    // atomic counters for metrics
    public AtomicInteger[] directionalThroughput;
    public AtomicLong[] directionalAverageWait;
    public AtomicLong[] directionalMaxWait;
    public AtomicLong[] directionalMinWait;


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

        this.directionalThroughput = new AtomicInteger[] {
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0)
        };

        this.directionalAverageWait = new AtomicLong[] {
                new AtomicLong(0L),
                new AtomicLong(0L),
                new AtomicLong(0L),
                new AtomicLong(0L)
        };

        this.directionalMinWait = new AtomicLong[] {
                new AtomicLong(Long.MAX_VALUE),
                new AtomicLong(Long.MAX_VALUE),
                new AtomicLong(Long.MAX_VALUE),
                new AtomicLong(Long.MAX_VALUE)
        };

        this.directionalMaxWait = new AtomicLong[] {
                new AtomicLong(Long.MIN_VALUE),
                new AtomicLong(Long.MIN_VALUE),
                new AtomicLong(Long.MIN_VALUE),
                new AtomicLong(Long.MIN_VALUE)
        };
    }

    public Intersection(LightModel lightModel, int id, ArrayList<Position> positions) {
        this(lightModel, id);
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

                int totalCars = directionalThroughput[car.direction.value].incrementAndGet();
                directionalAverageWait[car.direction.value].set(((totalCars - 1) * directionalAverageWait[car.direction.value].get() + car.lastWait) / totalCars );
                if (car.lastWait > directionalMaxWait[car.direction.value].get()) directionalMaxWait[car.direction.value].set(car.lastWait);
                if (car.lastWait < directionalMinWait[car.direction.value].get()) directionalMinWait[car.direction.value].set(car.lastWait);

                return barrier;
            }
        }

        return null;
    }
}