import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by soniamarginean on 8/3/15.
 */
public class Simulation {

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



        Barrier[] barriers = new Barrier[] {
                new Barrier(Arrays.asList(Direction.NORTH, Direction.SOUTH)),
                new Barrier(Arrays.asList(Direction.EAST, Direction.WEST))
        };

        LightModel lightModel;

        if(timed) {
            lightModel = new TimedLightModel(barriers, barrier_time);
        } else {
            lightModel = new NiceLightModel(barriers, barrier_threshold);
        }

        if (test){

            Map map = new Map(10,10,1,1,lightModel);

            if(print) {
                map.print();
            }
            ArrayList<Position> pos = new ArrayList<Position>();
            pos.add(map.mapLocations[5][5]);
            pos.add(map.mapLocations[5][6]);
            pos.add(map.mapLocations[6][5]);
            pos.add(map.mapLocations[6][6]);

            Intersection[] intersections = new Intersection[] {
                    new Intersection(lightModel, 0, pos)
            };

            Car[] cars = new Car[] {
                    new Car(map, intersections, 100L, 0, 60, 6, 0, 6, 9),
                    new Car(map, intersections, 500L, 4, 60, 6, 3, 6, 9),
                    new Car(map, intersections, 100L, 1, 60, 5, 9, 5, 0),
                    new Car(map, intersections, 200L, 2, 30, 0, 5, 9, 5),
                    new Car(map, intersections, 500L, 3, 30, 9, 6, 0, 6),
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
            lightModelThread.stop();
        }
        else {

            Map map = new Map(map_length, map_width, horizontal_roads, vertical_roads, lightModel);

            if (print) {
                map.print();
            }

            ArrayList<Car> cars = GenerateCars.GenerateCarsOnMap(map, load_factor, lightModel);

            Thread lightModelThread = new Thread(lightModel);
            lightModelThread.start();

            Thread[] tcar = new Thread[cars.size()];

            for (int i=0; i<cars.size(); i++) {
                tcar[i] = new Thread(cars.get(i));
            }

            for (int i=0; i<cars.size(); i++) {
                tcar[i].start();
            }

            Thread.sleep(duration);

            for (int i=0; i<cars.size(); i++) {
                tcar[i].stop();
            }

            lightModelThread.stop();

            String COMMA_DELIMITER = ",";
            String NEW_LINE ="\n";
            String FILE_HEADER = "intersection,NORTH,EAST,SOUTH,WEST,metric";
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter("load"+(int)(load_factor*10)+args[5]+args[6]+".csv");
                //Write a new student object list to the CSV file
                ArrayList<Intersection> intersections = map.getAllIntersections(lightModel);

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
