/**
 * Created by neelshah on 7/23/15.
 */
public class Car implements Runnable {

    public Intersection[] intersections;
    public Direction direction;
    public long delay;
    public int id;

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

                intersection.remove(this);

                intersection.locks[direction.value].unlock();

                System.out.println("I ( " + this.id + " ) have passed intersection " + intersection.id + " going " + direction.toString() + "!!!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}