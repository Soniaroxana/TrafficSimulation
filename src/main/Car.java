/**
 * Created by neelshah on 7/23/15.
 */
public class Car implements Runnable {

    public Intersection[] intersections;
    public Direction direction;
    public long delay;
    public int id;
    public Position initPosition;
    public Position finalDestination;
    public Position currentPosition;
    public Map map;
    public long speed;


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
        this.initPosition = this.currentPosition = new Position(0,0, LocationType.LANE);
        currentPosition.directions.add(this.direction);
        speed = 50;
    }

    public Car(Intersection[] intersections, Direction direction, long delay, int id, long speed) {
        this.intersections = intersections;
        this.direction = direction;
        this.delay = delay;
        this.id = id;
        this.initPosition = this.currentPosition = new Position(0,0, LocationType.LANE);
        currentPosition.directions.add(this.direction);
        this.speed = speed;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);

            for(Intersection intersection : intersections) {
                Barrier barrier = intersection.accept(this);

                while (barrier.isBlocking) {
                    System.out.println("I ( " + this.id + " ) am waiting at intersection " + intersection.id + " going " + direction.toString() + "!!!");
                    Thread.sleep(100L);
                }

                intersection.locks[direction.value].lock();

                Thread.sleep(speed*10L*intersection.length);

                intersection.remove(this);

                intersection.locks[direction.value].unlock();

                System.out.println("I ( " + this.id + " ) have passed intersection " + intersection.id + " going " + direction.toString() + "!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}