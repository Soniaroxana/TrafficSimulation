import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by soniamarginean on 8/2/15.
 */
public class RandomGeneratedCars {
    @Test
    public void testRandomlyGeneratedCars() throws InterruptedException {
        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel = new TimedLightModel(barriers, 5000L);

        Map map = new Map(20,20,4,4,lightModel);

        map.print();

        ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map,0.5,lightModel);

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(60000L);

        for (Intersection intersection : map.getAllIntersections(lightModel)) {
            System.out.println("Metrics for intersection " + intersection.id + ":");
            System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
            System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
            System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
            System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
        }

    }
}
