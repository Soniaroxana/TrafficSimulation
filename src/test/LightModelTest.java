import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by neelshah on 7/24/15.
 */
public class LightModelTest {

    @Test
    public void testLightModel() throws InterruptedException {

        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

/*
        LightModel lightModel = new LightModel(barriers) {
            @Override
            public void run() {
                while (true) {
                    enable(barriers);
                    disable(barriers);
                }
            }
        };
*/

        LightModel lightModel = new TimedLightModel(barriers, 5000L);

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModel, 0)
        };

        Car[] cars = new Car[] {
                new Car(intersections, Direction.EAST, 1000L, 0),
                new Car(intersections, Direction.WEST, 1000L, 1)
        };

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(10000L);
    }

    @Test
    public void testNiceLightModel() throws InterruptedException {

        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel = new TimedLightModel(barriers, 5000L);

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModel, 0)
        };

        Car[] cars = new Car[] {
                new Car(intersections, Direction.NORTH, 1000L, 0),
                new Car(intersections, Direction.NORTH, 2000L, 1),
                new Car(intersections, Direction.NORTH, 2000L, 2),
                new Car(intersections, Direction.NORTH, 2000L, 3),
                new Car(intersections, Direction.EAST, 2000L, 4),
                new Car(intersections, Direction.EAST, 3000L, 5),
                new Car(intersections, Direction.EAST, 3000L, 6),
                new Car(intersections, Direction.EAST, 3000L, 7),
                new Car(intersections, Direction.SOUTH, 2000L, 8),
                new Car(intersections, Direction.SOUTH, 3000L, 9),
                new Car(intersections, Direction.SOUTH, 3000L, 10),
                new Car(intersections, Direction.SOUTH, 3000L, 11),
                new Car(intersections, Direction.WEST, 4000L, 12),
                new Car(intersections, Direction.WEST, 5000L, 13),
                new Car(intersections, Direction.WEST, 5000L, 14),
                new Car(intersections, Direction.WEST, 5000L, 15)
        };

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(10000L);
    }
}