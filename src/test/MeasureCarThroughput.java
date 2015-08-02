import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by davidliu on 8/2/15.
 */
public class MeasureCarThroughput {
    @Test
    public void BasicMeasurement() throws InterruptedException {
        //Standard boilerplate to get test ready (barriers plus intersection)
        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel = new TimedLightModel(barriers, 5000L);

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModel, 0)
        };
        //End of boilerplate code


        GenerateCars mycars = new GenerateCars();

        ArrayList<Car> cars = mycars.GenerateCarsFromData("car_set_small.txt", intersections);

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(10000L);
    }
}
