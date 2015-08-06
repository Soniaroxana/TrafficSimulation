import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by soniamarginean on 8/2/15.
 */
public class RandomGeneratedCars {
    @Test
    public void testRandomlyGeneratedCars() throws InterruptedException {

        ArrayList<LightModel> lightModels = new ArrayList<>();
        for (int i=0; i<3*3; i++){
            Barrier[] barriers = new Barrier[] {
                    new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                    new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
            };
            lightModels.add(new TimedLightModel(barriers, 5000));
        }

        Map map = new Map(16,16,3,3,lightModels);

        map.print();

        ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map,0.8);

        //create and start the light model threads
        ArrayList<Thread> lightModelThreads = new ArrayList<>();

        for(LightModel lightModel : lightModels) {
            lightModelThreads.add(new Thread(lightModel));
        }

        for (Thread t:lightModelThreads){
            t.start();
        }

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
