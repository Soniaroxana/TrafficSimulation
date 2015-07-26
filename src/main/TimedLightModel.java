/**
 * Created by neelshah on 7/23/15.
 */
public class TimedLightModel extends LightModel {
    public long duration;

    public TimedLightModel(Barrier[] barriers, long duration) {
        super(barriers);
        this.duration = duration;
    }

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