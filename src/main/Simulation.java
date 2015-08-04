import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by soniamarginean on 8/3/15.
 */
public class Simulation {

    public static void main(String[] args) throws InterruptedException {
        boolean test=false;
        boolean print = false;
        long duration = 10000L;
        long barrier_time = 3000L;
        int map_length = 10;
        int map_width = 10;
        int vertical_roads = 1;
        int horizontal_roads = 1;
        double load_factor = 0.5;


        if (args.length>0){
            if(args[0].startsWith("test")){
                test = true;
                duration = Long.parseLong(args[1]);
                if(args.length==3){
                    print = true;
                }
            } else {
                map_length = Integer.parseInt(args[0]);
                map_width = Integer.parseInt(args[1]);
                vertical_roads = Integer.parseInt(args[2]);
                horizontal_roads = Integer.parseInt(args[3]);
                load_factor = Double.parseDouble(args[4]);
                barrier_time = Long.parseLong(args[5]);
                duration = Long.parseLong(args[6]);
                if(args.length==8){
                    print = true;
                }
            }

        } else {
            System.out.println("Simulation test <simulation_duration> (print - optional map print) --- will launch a small test application \n" +
                    "Simulation <map_length> <map_width> <vertical_roads> <horizontal_roads> <load_factor> <barrier_time> <simulation_duration> (print - optional map print)\n");
            return;
        }



        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel = new TimedLightModel(barriers, barrier_time);

        if (test){

            Map map = new Map(10,10,1,1,lightModel);

            if(print) {
                map.print();
            }
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

            Thread.sleep(duration);

            for (Intersection intersection : intersections) {
                System.out.println("Metrics for intersection " + intersection.id + ":");
                System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
                System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
                System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
                System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
            }
            return;
        }
        else {

            Map map = new Map(map_length, map_width, horizontal_roads, vertical_roads, lightModel);

            if (print) {
                map.print();
            }

            ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map, load_factor, lightModel);

            Thread lightModelThread = new Thread(lightModel);
            lightModelThread.start();

            for (Car car : cars) {
                new Thread(car).start();
            }

            Thread.sleep(duration);

            for (Intersection intersection : map.getAllIntersections(lightModel)) {
                System.out.println("Metrics for intersection " + intersection.id + ":");
                System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
                System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
                System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
                System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
            }

            return;
        }

    }
}
