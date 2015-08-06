/**
 * Created by neelshah on 7/23/15.
 */
// Light model based on # of cars thresholds instead of time
public class NiceLightModel extends LightModel {
    public int threshold;

    public NiceLightModel(Barrier[] barriers, int threshold) {
        super(barriers);
        this.threshold = threshold;
    }

    // This model will enable/disable a barrier based on how many cars are waiting on it
    // It will let cars cross if the number of cars > threshold and will not let cars pass if they are less than the threshold
    @Override
    public void run() {
        while (true) {
            for(Barrier barrier : barriers) {
                if (barrier.cars.size() >= threshold && barrier.isBlocking) {
                    enable(barrier);
                } else if (!barrier.cars.isEmpty()) {
                    disable(barrier);
                }
            }
        }
    }
}