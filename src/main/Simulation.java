import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by soniamarginean on 8/3/15.
 */
public class Simulation {

    private static class SimulationInstance implements Runnable {

        public long duration;
        public double loadFactor;
        public boolean isTimed;
        public long lightModelTime;
        public int lightModelThreshold;

        public int throughputPerIntersection = 0;
        public long averageWaitTime = 0L;
        public long maxWaitTime = Long.MIN_VALUE;
        public long minWaitTime = Long.MAX_VALUE;


        private int mapLength = 20;
        private int mapWidth = 20;
        private int verticalRoads = 2;
        private int horizontalRoads = 2;

        public SimulationInstance(long duration, double loadFactor, boolean isTimed, long lightModelTime, int lightModelThreshold) {
            this.duration = duration;
            this.loadFactor = loadFactor;
            this.isTimed = isTimed;
            this.lightModelTime = lightModelTime;
            this.lightModelThreshold = lightModelThreshold;
        }

        @Override
        public void run() {

            ArrayList<LightModel> lightModels = new ArrayList<>();

            for (int i = 0; i < verticalRoads * horizontalRoads; i++) {
                Barrier[] barriers = new Barrier[]{
                        new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                        new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
                };
                // Decide whether we want to use a timed or nice traffic light model
                if (isTimed) {
                    lightModels.add(new TimedLightModel(barriers, lightModelTime));
                } else {
                    lightModels.add(new NiceLightModel(barriers, lightModelThreshold));
                }
            }

            Map map = new Map(mapLength, mapWidth, horizontalRoads, verticalRoads, lightModels);

            //generate random cars on the map using the load factor
            ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map, loadFactor);

            //create and start the light model threads
            ArrayList<Thread> lightModelThreads = new ArrayList<>();

            for (LightModel lightModel : lightModels) {
                lightModelThreads.add(new Thread(lightModel));
            }

            for (Thread t : lightModelThreads) {
                t.start();
            }

            //create and start the car model threads
            Thread[] tcar = new Thread[cars.size()];

            for (int i = 0; i < cars.size(); i++) {
                tcar[i] = new Thread(cars.get(i));
            }

            for (int i = 0; i < cars.size(); i++) {
                tcar[i].start();
            }

            //sleep for duration
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //stop all car threads
            for (int i = 0; i < cars.size(); i++) {
                tcar[i].stop();
            }

            for (Thread t : lightModelThreads) {
                t.stop();
            }

            for (Intersection intersection : map.getAllIntersections()) {
                for (AtomicInteger directionalThroughput : intersection.directionalThroughput) {
                    throughputPerIntersection += directionalThroughput.get();
                }

                long temp = 0L;

                for (AtomicLong directionalAverageWait : intersection.directionalAverageWait) {
                    temp += directionalAverageWait.get();
                }

                averageWaitTime += temp / 4;

                for (AtomicLong directionalMaxWait : intersection.directionalMaxWait) {
                    maxWaitTime = (maxWaitTime > directionalMaxWait.get() ? maxWaitTime : directionalMaxWait.get());
                }

                for (AtomicLong directionalMinWait : intersection.directionalMinWait) {
                    minWaitTime = (minWaitTime < directionalMinWait.get() ? minWaitTime : directionalMinWait.get());
                }
            }

            throughputPerIntersection /= map.getAllIntersections().size();
            averageWaitTime /= map.getAllIntersections().size();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        String COMMA_DELIMITER = ",";
        String NEW_LINE ="\n";
        String FILE_HEADER = ",Throughput/Intersection,Average Wait Time,Max Wait Time,Min Wait Time";
        FileWriter fileWriter = null;

        double[] loadFactors = new double[] {0.3, 0.5, 0.8, 1.0};
        long[] durations = new long[] {1000L, 2000L, 4000L, 8000L};
        int[] thresholds = new int[] {1, 2, 3, 4};

        List<SimulationInstance> simulationInstances = new ArrayList<>();


        for (double loadFactor: loadFactors) {
            SimulationInstance simulationInstance = new SimulationInstance(40000L, loadFactor, true, 1000L, 0);
            simulationInstances.add(simulationInstance);

            Thread thread = new Thread(simulationInstance);
            thread.start();
            thread.join();
        }

        try {
            fileWriter = new FileWriter("load_factors.csv");
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE);

            for (SimulationInstance simulationInstance : simulationInstances) {
                fileWriter.append(String.valueOf(simulationInstance.loadFactor));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.throughputPerIntersection));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.averageWaitTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.maxWaitTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.minWaitTime));
                fileWriter.append(NEW_LINE);
            }

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }

        simulationInstances.clear();
        fileWriter = null;

        for (long duration : durations) {
            SimulationInstance simulationInstance = new SimulationInstance(40000L, 0.8, true, duration, 0);
            simulationInstances.add(simulationInstance);

            Thread thread = new Thread(simulationInstance);
            thread.start();
            thread.join();
        }

        try {
            fileWriter = new FileWriter("durations.csv");
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE);

            for (SimulationInstance simulationInstance : simulationInstances) {
                fileWriter.append(String.valueOf(simulationInstance.lightModelTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.throughputPerIntersection));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.averageWaitTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.maxWaitTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.minWaitTime));
                fileWriter.append(NEW_LINE);
            }

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }

        simulationInstances.clear();
        fileWriter = null;

        for (int threshold : thresholds) {
            SimulationInstance simulationInstance = new SimulationInstance(40000L, 0.8, false, 0L, threshold);
            simulationInstances.add(simulationInstance);

            Thread thread = new Thread(simulationInstance);
            thread.start();
            thread.join();
        }

        try {
            fileWriter = new FileWriter("thresholds.csv");
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE);

            for (SimulationInstance simulationInstance : simulationInstances) {
                fileWriter.append(String.valueOf(simulationInstance.lightModelThreshold));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.throughputPerIntersection));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.averageWaitTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.maxWaitTime));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(simulationInstance.minWaitTime));
                fileWriter.append(NEW_LINE);
            }

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

/*
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
            System.out.println("Simulation test <simulation_duration> (print - optional map print) --- will launch a small test application \n" +
                    "Simulation <map_length> <map_width> <vertical_roads> <horizontal_roads> <load_factor> timed <barrier_time> <simulation_duration> (print - optional map print)\n" +
                    "Simulation <map_length> <map_width> <vertical_roads> <horizontal_roads> <load_factor> nice <barrier_threshold> <simulation_duration> (print - optional map print)\n");
            return;
        }

        if (test){
            ArrayList<LightModel> lightModels = new ArrayList<LightModel>();

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

            Map map = new Map(10,10,1,1,lightModels);

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
            // NEEL LOOK HERE!
            // This is the code that does the actual simulation
            // Create a new map
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

            //write to file
            String COMMA_DELIMITER = ",";
            String NEW_LINE ="\n";
            String FILE_HEADER = "intersection,NORTH,EAST,SOUTH,WEST,metric";
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter("load"+(int)(load_factor*10)+args[5]+args[6]+".csv");
                //Get all map intersections and print metrics
                ArrayList<Intersection> intersections = map.getAllIntersections();

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
*/
}
