import javafx.geometry.Pos;

/**
 * Created by neelshah on 7/23/15.
 */
// The car is modeled as a Thread
public class Car implements Runnable {

    //Each car knows the intersections it has to pass through, the direction it's going, has a delay that corresponds to acceleration
    //a unique id, its initial, final and current positions
    //The car also has knowledge of the full map and its speed
    public Intersection[] intersections;
    public Direction direction;
    public long delay;
    public int id;
    public Position initPosition;
    public Position finalDestination;
    public Position currentPosition;
    public Map map; //make map a singleton
    public long speed;
    public long lastWait = 0L;

    //Constructors - we need several depending on what we're testing
    public Car(Intersection[] intersections, Direction direction, int id) {
        this.intersections = intersections;
        this.direction = direction;
        this.delay = 0L;
        this.id = id;
    }

    public Car(Intersection[] intersections, Direction direction, long delay, int id) {
        this.intersections = intersections;
        this.direction = direction;
        this.delay = delay;
        this.id = id;
        this.initPosition = this.currentPosition = new Position(0, 0, LocationType.LANE);
        currentPosition.directions.add(this.direction);
        speed = 50;
    }

    public Car(Intersection[] intersections, Direction direction, long delay, int id, long speed) {
        this.intersections = intersections;
        this.direction = direction;
        this.delay = delay;
        this.id = id;
        this.initPosition = this.currentPosition = new Position(0, 0, LocationType.LANE);
        currentPosition.directions.add(this.direction);
        this.speed = speed;
    }

    public Car(Map map, Intersection[] intersections, long delay, int id, long speed, int initx, int inity, int finalx, int finaly) {
        this.intersections = intersections;
        this.delay = delay;
        this.id = id;
        this.initPosition = this.currentPosition = map.mapLocations[initx][inity];
        map.mapLocations[initx][inity].enter();
        this.direction = initPosition.directions.get(0);
        this.finalDestination = map.mapLocations[finalx][finaly];
        this.speed = speed;
        this.map = map;
    }

    //method to compute next position
    public Position NextPosition(int steps) {
        switch (this.direction) {
            case NORTH:
                return map.mapLocations[Math.max(0,currentPosition.x-steps)][currentPosition.y];
            case SOUTH:
                return map.mapLocations[Math.min(map.length-1,currentPosition.x+steps)][currentPosition.y];
            case EAST:
                return map.mapLocations[currentPosition.x][Math.min(currentPosition.y+steps,map.width-1)];
            case WEST:
                return map.mapLocations[currentPosition.x][Math.max(0,currentPosition.y-steps)];
        }
        return null;
    }

    // thread lifecycle:
    // The car thread finishes when the final destination is reached
    // to move to the next position the car computes it first
    // if the next position is an intersection, then the car checks whether the traffic light is on or off
    // while the traffic light is blocking the car waits
    // once it's free to go, it grabs the semaphore for the next position, crosses and release the semaphore
    // for the previous position
    // If the next position is not an intersection, then the car tries to grab the semaphore for the next position
    // and only releases its current position after it successfully entered the next position
    @Override
    public void run() {
        try {
            boolean waiting = false;
            while(!(currentPosition.equals(finalDestination))) {
                System.out.println("My ( " + this.id + " ) current position is ( " + currentPosition.x + " , " + currentPosition.y + " ) going "+this.direction + " to ( "+ finalDestination.x + " , " + finalDestination.y + " )");
                Thread.sleep(delay);
                Position next = NextPosition(1);
                if (next.locationtype == LocationType.INTERSECTION) {
                    for (Intersection intersection : intersections) {
                        if (intersection.locations.contains(next)) {
                            //Add the car to the intersection and get its corresponding barrier based on the car direction
                            Barrier barrier = intersection.accept(this);
                            lastWait = System.currentTimeMillis();

                            while (barrier.isBlocking) {
                                if (!waiting) {
                                    System.out.println("I ( " + this.id + " ) am waiting at intersection " + intersection.id + " going " + direction.toString() + "!!!");
                                }
                                waiting = true;
                                Thread.sleep(100L);
                            }
                            waiting = false;
                            intersection.locks[direction.value].lock();
                            //This sleep below simulates the car crossing at speed "speed" and intersection of length = length
                            Thread.sleep(speed * 10L * intersection.length);
                            next  = NextPosition(2);
                            next.enter();
                            currentPosition.exit();
                            currentPosition = next;
                            lastWait = System.currentTimeMillis() - lastWait;
                            //finally remove the car from the intersection and unlock the direction the barrier was protecting
                            intersection.remove(this);
                            intersection.locks[direction.value].unlock();

                            System.out.println("I ( " + this.id + " ) have passed intersection " + intersection.id + " going " + direction.toString() + "!!!");
                        }
                    }
                } else {
                    next.enter();
                    Thread.sleep(speed * 10L * 1);
                    currentPosition.exit();
                    currentPosition = next;
                }
            }
            System.out.println("I ( " + this.id + " ) have reached my final destination");
            currentPosition.exit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}