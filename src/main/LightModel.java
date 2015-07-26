/**
 * Created by neelshah on 7/21/15.
 */
public abstract class LightModel implements Runnable {
    public Barrier[] barriers;

    public LightModel(Barrier[] barriers) {
        this.barriers = barriers;
    }

    public void enable(Direction... directions) {
        for (Barrier barrier : barriers) {
            for (Direction direction : directions) {
                if (barrier.directions.contains(direction)) {
                    barrier.acquire();
                }
            }
        }
    }

    public void disable(Direction... directions) {
        for (Barrier barrier : barriers) {
            for (Direction direction : directions) {
                if (barrier.directions.contains(direction)) {
                    barrier.release();
                }
            }
        }
    }

    public void enable(Barrier... barriers) {
        for (Barrier barrier : barriers) {
            barrier.acquire();
        }
    }

    public void disable(Barrier... barriers) {
        for (Barrier barrier : barriers) {
            barrier.release();
        }
    }

    @Override
    public abstract void run();
}