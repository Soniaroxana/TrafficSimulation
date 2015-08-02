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
    public Map map; //make map a singleton
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

    public Position NextPosition(int steps){
        switch (this.direction){
            case NORTH:
                return map.mapLocations[currentPosition.x-steps][currentPosition.y];
            case SOUTH:
                return map.mapLocations[currentPosition.x+steps][currentPosition.y];
            case EAST:
                return map.mapLocations[currentPosition.x][currentPosition.y+steps];
            case WEST:
                return map.mapLocations[currentPosition.x][currentPosition.y-steps];
        }
        return null;
    }

    @Override
    public void run() {
        try {
            while(!(currentPosition.equals(finalDestination))) {
                System.out.println("My ( " + this.id + " ) current position is ( " + currentPosition.x + " , " + currentPosition.y + " ) going "+this.direction);
                Thread.sleep(delay);
                Position next = NextPosition(1);
                if (next.locationtype == LocationType.INTERSECTION){
                    for (Intersection intersection : intersections) {
                        if (intersection.locations.contains(next)){
                        Barrier barrier = intersection.accept(this);

                        while (barrier.isBlocking) {
                            System.out.println("I ( " + this.id + " ) am waiting at intersection " + intersection.id + " going " + direction.toString() + "!!!");
                            Thread.sleep(100L);
                        }
                        intersection.locks[direction.value].lock();

                        Thread.sleep(speed * 10L * intersection.length);

                        currentPosition = NextPosition(1);

                        intersection.remove(this);

                        intersection.locks[direction.value].unlock();

                        System.out.println("I ( " + this.id + " ) have passed intersection " + intersection.id + " going " + direction.toString() + "!!!");
                    }}
                } else {
                    Thread.sleep(speed * 10L * 1);
                    currentPosition.exit();
                    next.enter();
                    currentPosition = next;
                }
                /*
                for (Intersection intersection : intersections) {
                    Barrier barrier = intersection.accept(this);

                    while (barrier.isBlocking) {
                        System.out.println("I ( " + this.id + " ) am waiting at intersection " + intersection.id + " going " + direction.toString() + "!!!");
                        Thread.sleep(100L);
                    }

                    intersection.locks[direction.value].lock();

                    Thread.sleep(speed * 10L * intersection.length);

                    intersection.remove(this);

                    intersection.locks[direction.value].unlock();

                    System.out.println("I ( " + this.id + " ) have passed intersection " + intersection.id + " going " + direction.toString() + "!!!");
                }*/
            }
            System.out.println("I ( " + this.id + " ) have reached my final destination");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}