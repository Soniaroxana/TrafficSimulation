import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by neelshah on 7/21/15.
 */
// Simulates the traffic light behavior
public class Barrier {
    //directions protected by this barrier (usually 2)
    public List<Direction> directions;
    //cars waiting at this barrier
    public ArrayList<Car> cars;
    //condition that checks whether the barrier is blocking one or more directions
    public boolean isBlocking = true;

//    public Lock mutex = new ReentrantLock();
//    public Condition isBlocking = mutex.newCondition();

    //constructor that creates a barrier for 2 dirctions
    public Barrier(List<Direction> directions) {
        this.directions = directions;
        this.cars = new ArrayList<Car>();
    }

    //method to allow cars to pass
    public void acquire() {
        isBlocking = false;
        System.out.println("Direction " + this.directions.get(0).toString() + " and direction " + this.directions.get(1).toString() + " enabled");
    }

    //method to hold cars at the barrier
    public void release() {
        isBlocking = true;
        System.out.println("Direction " + this.directions.get(0).toString() + " and direction " + this.directions.get(1).toString() + " disabled");
    }

    //add car to barrier
    public void add(Car car) {
        cars.add(car);
    }

    //remove car from barrier
    public void remove(Car car) {
         cars.remove(car);
    }

    //Function which checks whether a direction is protected (N,E,S,W)
    public boolean protects(Direction direction) {
        return directions.contains(direction);
    }
}