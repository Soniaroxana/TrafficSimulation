/**
 * Created by neelshah on 7/21/15.
 */
//Abstract class that defines the interface of a light model
public abstract class LightModel implements Runnable {
    public Barrier[] barriers;

    public LightModel(Barrier[] barriers) {
        this.barriers = barriers;
    }

    //Method used to enable all of the barriers in on or more directions
    public void enable(Direction... directions) {
        for (Barrier barrier : barriers) {
            for (Direction direction : directions) {
                if (barrier.directions.contains(direction)) {
                    barrier.acquire();
                }
            }
        }
    }

    //Method used to disable all of the barriers in on or more directions
    public void disable(Direction... directions) {
        for (Barrier barrier : barriers) {
            for (Direction direction : directions) {
                if (barrier.directions.contains(direction)) {
                    barrier.release();
                }
            }
        }
    }

    //Method used to enable all of the barriers passed in
    public void enable(Barrier... barriers) {
        for (Barrier barrier : barriers) {
            barrier.acquire();
        }
    }

    //Method used to disable all of the barriers passed in
    public void disable(Barrier... barriers) {
        for (Barrier barrier : barriers) {
            barrier.release();
        }
    }

    // Each light model will implement its own lifecycle method run()
    @Override
    public abstract void run();
}