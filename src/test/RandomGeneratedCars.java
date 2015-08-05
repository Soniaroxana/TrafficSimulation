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

        ArrayList<LightModel> lightModel = new ArrayList<>();
        lightModel.add(new TimedLightModel(barriers, 5000L));

        Map map = new Map(16,16,3,3,lightModel);

        map.print();

        ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map,0.8);

        Thread lightModelThread = new Thread(lightModel.get(0));
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(10000L);

        System.out.println("Cars on map = "+cars.size());
        for (Intersection intersection : map.getAllIntersections()) {
            System.out.println("Metrics for intersection " + intersection.id + ":");
            System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
            System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
            System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
            System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
        }

    }
}
