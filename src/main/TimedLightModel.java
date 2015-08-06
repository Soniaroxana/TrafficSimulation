/**
 * Created by neelshah on 7/23/15.
 */
//Class that models a time based traffic light that changes its state based onthe duration field
public class TimedLightModel extends LightModel {
    public long duration;

    public TimedLightModel(Barrier[] barriers, long duration) {
        super(barriers);
        this.duration = duration;
    }

    // This light model enables two opposing directions at the same time, while disabling their perpendicular directions
    // This model runs indefinitely switching at the configured intervals
    @Override
    public void run() {
        try {
            while (true) {
                enable(Direction.NORTH, Direction.SOUTH);
                Thread.sleep(duration);
                disable(Direction.NORTH, Direction.SOUTH);

                enable(Direction.EAST, Direction.WEST);
                Thread.sleep(duration);
                disable(Direction.EAST, Direction.WEST);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}