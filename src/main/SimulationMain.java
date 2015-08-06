import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by soniamarginean on 8/3/15.
 */
// main executable used for simulation
public class SimulationMain {
    //main method called
    public static void main(String[] args) throws InterruptedException {
        boolean test=false;
        boolean print = false;
        boolean timed = true;
        long duration = 10000L;
        long barrier_time = 3000L;
        int barrier_threshold = 1;
        int map_length = 10;
        int map_width = 10;
        int vertical_roads = 1;
        int horizontal_roads = 1;
        double load_factor = 0.5;

        //Command line arguments can be seen in the print below or by calling SimulationMain with no parameters
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
                if (args[5].startsWith("timed")){
                    barrier_time = Long.parseLong(args[6]);
                    timed = true;
                } else {
                    barrier_threshold = Integer.parseInt(args[6]);
                    timed = false;
                }
                duration = Long.parseLong(args[7]);
                if(args.length==9){
                    print = true;
                }
            }

        } else {
            System.out.println("SimulationMain test <simulation_duration> (print - optional map print) --- will launch a small test application \n" +
                    "SimulationMain <map_length> <map_width> <vertical_roads> <horizontal_roads> <load_factor> timed <barrier_time> <simulation_duration> (print - optional map print)\n" +
                    "SimulationMain <map_length> <map_width> <vertical_roads> <horizontal_roads> <load_factor> nice <barrier_threshold> <simulation_duration> (print - optional map print)\n");
            return;
        }

        //This is the demo mode of the simulation with a simple 10 by 10 map, one intersection, 2 perpendicular roads
        // and 5 random cars. It uses a fixed time light model
        if (test){
            ArrayList<LightModel> lightModels = new ArrayList<LightModel>();

            Barrier[] barriers = new Barrier[] {
                    new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                    new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
            };

            lightModels.add(new TimedLightModel(barriers, barrier_time));

            Map map = new Map(10,10,1,1,lightModels);

            // pretty print to show the map (optional)
            if(print) {
                map.print();
            }

            ArrayList<Position> pos = new ArrayList<Position>();
            pos.add(map.mapLocations[5][5]);
            pos.add(map.mapLocations[5][6]);
            pos.add(map.mapLocations[6][5]);
            pos.add(map.mapLocations[6][6]);

            Intersection[] intersections = new Intersection[] {
                    new Intersection(lightModels.get(0), 0, pos)
            };

            Car[] cars = new Car[] {
                    new Car(map, intersections, 100L, 0, 60, 6, 0, 6, 9),
                    new Car(map, intersections, 500L, 4, 60, 6, 3, 6, 9),
                    new Car(map, intersections, 100L, 1, 60, 5, 9, 5, 0),
                    new Car(map, intersections, 200L, 2, 30, 0, 5, 9, 5),
                    new Car(map, intersections, 500L, 3, 30, 9, 6, 0, 6),
            };

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

            Thread.sleep(duration);

            // Metrics are printed at the end of execution
            for (Intersection intersection : intersections) {
                System.out.println("Metrics for intersection " + intersection.id + ":");
                System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
                System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
                System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
                System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
            }
            for(Thread t : lightModelThreads) {
                t.stop();
            }
        }
        else {
            //This is the code that does the actual simulation

            // Create all the light models for all the intersections
            ArrayList<LightModel> lightModels = new ArrayList<>();

            for (int i=0; i<vertical_roads*horizontal_roads; i++){
                Barrier[] barriers = new Barrier[] {
                        new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                        new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
                };
                // Decide whether we want to use a timed or nice traffic light model
                if(timed) {
                    lightModels.add(new TimedLightModel(barriers, barrier_time));
                } else {
                    lightModels.add(new NiceLightModel(barriers, barrier_threshold));
                }
            }

            //Create a map based on the commmand line parameters passed in
            Map map = new Map(map_length, map_width, horizontal_roads, vertical_roads, lightModels);

            //set this to true if you want the map printed
            if (print) {
                map.print();
            }

            //generate random cars on the map using the load factor
            ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map, load_factor);

            //create and start the light model threads
            ArrayList<Thread> lightModelThreads = new ArrayList<>();

            for(LightModel lightModel : lightModels) {
                lightModelThreads.add(new Thread(lightModel));
            }

            for (Thread t:lightModelThreads){
                t.start();
            }

            //create and start the car model threads
            Thread[] tcar = new Thread[cars.size()];

            for (int i=0; i<cars.size(); i++) {
                tcar[i] = new Thread(cars.get(i));
            }

            for (int i=0; i<cars.size(); i++) {
                tcar[i].start();
            }

            //sleep for duration
            Thread.sleep(duration);

            //stop all car threads
            for (int i=0; i<cars.size(); i++) {
                tcar[i].stop();
            }

            for(Thread t : lightModelThreads) {
                t.stop();
            }

            //write metrics to file for post processing
            String COMMA_DELIMITER = ",";
            String NEW_LINE ="\n";
            String FILE_HEADER = "intersection,NORTH,EAST,SOUTH,WEST,metric";
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter("load"+(int)(load_factor*10)+args[5]+args[6]+".csv");
                //Get all map intersections and print metrics
                ArrayList<Intersection> intersections = map.getAllIntersections();

                // Metrics are printed at the end of execution
                for (Intersection intersection : intersections) {
                    System.out.println("Metrics for intersection " + intersection.id + ":");
                    System.out.println("\t" + "Throughput : " + Arrays.toString(intersection.directionalThroughput));
                    System.out.println("\t" + "Average Wait : " + Arrays.toString(intersection.directionalAverageWait));
                    System.out.println("\t" + "Max Wait : " + Arrays.toString(intersection.directionalMaxWait));
                    System.out.println("\t" + "Min Wait : " + Arrays.toString(intersection.directionalMinWait));
                }

                fileWriter.append(FILE_HEADER.toString());
                fileWriter.append(NEW_LINE);
                for (Intersection intersection : intersections) {
                    fileWriter.append(String.valueOf(intersection.id));
                    fileWriter.append(COMMA_DELIMITER);
                    for (int j = 0; j < 4; j++) {
                        fileWriter.append(String.valueOf(intersection.directionalThroughput[j]));
                        fileWriter.append(COMMA_DELIMITER);
                    }
                    fileWriter.append("throughput");
                    fileWriter.append(NEW_LINE);
                }

                fileWriter.append(NEW_LINE);

                fileWriter.append(FILE_HEADER.toString());
                fileWriter.append(NEW_LINE);
                for (Intersection intersection : intersections) {
                    fileWriter.append(String.valueOf(intersection.id));
                    fileWriter.append(COMMA_DELIMITER);
                    for (int j=0; j<4; j++){
                        fileWriter.append(String.valueOf(intersection.directionalAverageWait[j]));
                        fileWriter.append(COMMA_DELIMITER);
                    }
                    fileWriter.append("avg_wait");
                    fileWriter.append(NEW_LINE);
                }

                fileWriter.append(NEW_LINE);

                fileWriter.append(FILE_HEADER.toString());
                fileWriter.append(NEW_LINE);
                for (Intersection intersection : intersections) {
                    fileWriter.append(String.valueOf(intersection.id));
                    fileWriter.append(COMMA_DELIMITER);
                    for (int j=0; j<4; j++){
                        fileWriter.append(String.valueOf(intersection.directionalMaxWait[j]));
                        fileWriter.append(COMMA_DELIMITER);
                    }
                    fileWriter.append("max_wait");
                    fileWriter.append(NEW_LINE);
                }

                fileWriter.append(NEW_LINE);

                fileWriter.append(FILE_HEADER.toString());
                fileWriter.append(NEW_LINE);
                for (Intersection intersection : intersections) {
                    fileWriter.append(String.valueOf(intersection.id));
                    fileWriter.append(COMMA_DELIMITER);
                    for (int j=0; j<4; j++){
                        fileWriter.append(String.valueOf(intersection.directionalMinWait[j]));
                        fileWriter.append(COMMA_DELIMITER);
                    }
                    fileWriter.append("min_wait");
                    fileWriter.append(NEW_LINE);
                }
            } catch (Exception e) {
                System.out.println("Error in CsvFileWriter !!!");
                e.printStackTrace();
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
                }
            }
        }

    }
}
