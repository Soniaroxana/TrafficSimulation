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

        LightModel lightModel = new TimedLightModel(barriers, 5000L);

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModel, 0)
        };

        Car[] cars = new Car[] {
                new Car(intersections, Direction.EAST, 1000L, 0),
        };

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(10000L);
    }

    @Test
    public void testMultipleCars() throws InterruptedException {

        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel = new TimedLightModel(barriers, 5000L);

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModel, 0)
        };

        /*Car[] cars = new Car[] {
                new Car(intersections, Direction.NORTH, 1000L, 0, 30),
                new Car(intersections, Direction.NORTH, 2000L, 1, 50),
                new Car(intersections, Direction.NORTH, 2000L, 2, 30),
                new Car(intersections, Direction.NORTH, 2000L, 3, 40),
                new Car(intersections, Direction.EAST, 2000L, 4, 60),
                new Car(intersections, Direction.EAST, 3000L, 5, 20),
                new Car(intersections, Direction.EAST, 3000L, 6, 50),
                new Car(intersections, Direction.EAST, 3000L, 7, 20),
                new Car(intersections, Direction.SOUTH, 2000L, 8, 30),
                new Car(intersections, Direction.SOUTH, 3000L, 9, 20),
                new Car(intersections, Direction.SOUTH, 3000L, 10, 30),
                new Car(intersections, Direction.SOUTH, 3000L, 11, 20),
                new Car(intersections, Direction.WEST, 4000L, 12, 30),
                new Car(intersections, Direction.WEST, 5000L, 13, 20),
                new Car(intersections, Direction.WEST, 5000L, 14, 30),
                new Car(intersections, Direction.WEST, 5000L, 15, 20)
        };*/

        ArrayList<Car> cars = GenerateCars.GenerateRandomCars(100, intersections);

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(10000L);
    }

    @Test
    public void testMultipleIntersections() throws InterruptedException {

        Barrier[][] barriers = {
                new Barrier[] {
                        new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                        new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
                },
                new Barrier[] {
                        new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                        new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
                },
                new Barrier[] {
                        new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                        new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
                },
                new Barrier[] {
                        new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                        new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
                }
        };

        LightModel[] lightModels= new LightModel[] {
                new TimedLightModel(barriers[0], 2000L),
                new TimedLightModel(barriers[1], 3000L),
                new TimedLightModel(barriers[2], 2000L),
                new TimedLightModel(barriers[3], 3000L)
        };

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModels[0], 0),
                new Intersection(lightModels[1], 1),
                new Intersection(lightModels[2], 2),
                new Intersection(lightModels[3], 3)
        };

        /*Car[] cars = new Car[] {
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
        };*/

        ArrayList<Car> cars = GenerateCars.GenerateRandomCars(100, intersections);

        for (LightModel lightModel : lightModels) {
            new Thread(lightModel).start();
        }

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(15000L);
    }
}