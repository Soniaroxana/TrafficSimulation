import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neelshah on 7/21/15.
 */
public class Barrier {
    public List<Direction> directions;
    public ArrayList<Car> cars;
    public boolean isBlocking = true;

//    public Lock mutex = new ReentrantLock();
//    public Condition isBlocking = mutex.newCondition();

    public Barrier(List<Direction> directions) {
        this.directions = directions;
        this.cars = new ArrayList<>();
    }

    public void acquire() {
        isBlocking = false;
        System.out.println("Direction " + this.directions.get(0).toString() + " and direction " + this.directions.get(1).toString() + " enabled");
    }

    public void release() {
        isBlocking = true;
        System.out.println("Direction " + this.directions.get(0).toString() + " and direction " + this.directions.get(1).toString() + " disabled");
    }

    public void add(Car car) {
        cars.add(car);
    }

    public void remove(Car car) {
         cars.remove(car);
    }

    public boolean protects(Direction direction) {
        return directions.contains(direction);
    }
}