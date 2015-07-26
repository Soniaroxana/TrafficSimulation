/**
 * Created by neelshah on 7/23/15.
 */
public class NiceLightModel extends LightModel {
    public int threshold;

    public NiceLightModel(Barrier[] barriers, int threshold) {
        super(barriers);
        this.threshold = threshold;
    }

    @Override
    public void run() {
        while (true) {
            for(Barrier barrier : barriers) {
                if (barrier.cars.size() >= threshold) {
                    enable(barrier);
                } else {
                    disable(barrier);
                }
            }
        }
    }
}