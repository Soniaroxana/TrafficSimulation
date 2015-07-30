import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neelshah on 7/21/15.
 */
public class Intersection {
    public LightModel lightModel;
    public Barrier[] barriers;
    public Lock[] locks;
    public int id;
    public long length;

    public Intersection(LightModel lightModel, int id) {
        this.lightModel = lightModel;
        this.barriers = lightModel.barriers;

        this.locks = new Lock[] {
                new ReentrantLock(),
                new ReentrantLock(),
                new ReentrantLock(),
                new ReentrantLock()
        };

        this.id = id;
        this.length = 2; //positions
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