/**
 * Created by soniamarginean on 8/2/15.
 */
import javafx.geometry.Pos;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class MapTest {

    @Test
    public void testSimpleMapModel() throws InterruptedException {

        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel = new TimedLightModel(barriers, 5000L);
        //LightModel lightModel = new NiceLightModel(barriers, 1);

        Map map = new Map(10,10,1,1,lightModel);

        map.print();

        ArrayList<Position> pos = new ArrayList<Position>();
        pos.add(map.mapLocations[1][1]);
        pos.add(map.mapLocations[1][2]);
        pos.add(map.mapLocations[2][1]);
        pos.add(map.mapLocations[2][2]);

        Intersection[] intersections = new Intersection[] {
                new Intersection(lightModel, 0, pos)
        };

        Car[] cars = new Car[] {
                new Car(map, intersections, 100L, 0, 60, 2, 0, 2, 9),
                new Car(map, intersections, 500L, 4, 60, 1, 5, 1, 0),
                new Car(map, intersections, 100L, 1, 60, 1, 9, 1, 0),
                new Car(map, intersections, 200L, 2, 30, 0, 1, 9, 1),
                new Car(map, intersections, 500L, 3, 30, 9, 2, 0, 2),
        };

        Thread lightModelThread = new Thread(lightModel);
        lightModelThread.start();

        for (Car car : cars) {
            new Thread(car).start();
        }

        Thread.sleep(20000L);

        for (Intersection intersection : intersections) {
            System.out.println("Metrics for intersection " + intersection.id + ":");
            System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
            System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
            System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
            System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
        }
    }
}
