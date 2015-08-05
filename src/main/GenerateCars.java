import javafx.scene.effect.Light;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.lang.Math;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class GenerateCars {
    public static ArrayList<Car> GenerateRandomCars(int n, Intersection[] intersections){
        ArrayList<Car> cars = new ArrayList<Car>();
        Random rand = new Random();

        for (int i = 0; i < n; i++){
            cars.add(new Car(intersections, Direction.valueOf(rand.nextInt(4)), 10L * (rand.nextInt(10)), i, (rand.nextInt(10)) * 10));
        }

        return cars;
    }


    public static ArrayList<Car> GenerateCarsOnMap(Map map, double loadFactor) {
        ArrayList<Car> cars = new ArrayList<Car>();
        Random rand = new Random();
        // first get the allowed positions for placing cars for each lane in each direction
        // also get the intersections per lane
        // last put positions/loadFactor cars on the road for each lane
        int carIndex = 0;
        for (int i=0; i< map.horizontalRoads; i++){
            ArrayList<Position> eastPositions = map.getLanePositions(i, Direction.EAST);
            Intersection[] eastIntersections = map.getMyIntersections(i, Direction.EAST);
            int x = eastPositions.get(i).x;
            for (int j=0; j< eastPositions.size()*loadFactor; j++){
                cars.add(new Car(map,eastIntersections,Math.abs(100L * (rand.nextInt() % 10)), carIndex,Math.abs((rand.nextInt() % 10) * 10), x,eastPositions.get(j).y, x, map.width - 1));//Math.min(map.width - 1, eastPositions.get(j).y + Math.abs(rand.nextInt(map.width - 1)))));
                carIndex++;
            }
            ArrayList<Position> westPositions = map.getLanePositions(i, Direction.WEST);
            Intersection[] westIntersections = map.getMyIntersections(i, Direction.WEST);
            x = westPositions.get(i).x;
            for (int j=westPositions.size()-1; j>westPositions.size() - westPositions.size()*loadFactor; j--){
                cars.add(new Car(map,westIntersections,Math.abs(100L * (rand.nextInt() % 10)), carIndex,Math.abs((rand.nextInt() % 10) * 10), x, westPositions.get(j).y, x, 0));//Math.max(0, westPositions.get(j).y - Math.abs(rand.nextInt(map.width - 1)))));
                carIndex++;
            }
        }

        for (int i=0; i<map.verticalRoads; i++){
            ArrayList<Position> northPositions = map.getLanePositions(i, Direction.NORTH);
            Intersection[] northIntersections = map.getMyIntersections(i, Direction.NORTH);
            int y = northPositions.get(i).y;
            for (int j=northPositions.size()-1; j> northPositions.size()-northPositions.size()*loadFactor; j--){
                //cars.add(new Car(map,northIntersections,Math.abs(1000L * (rand.nextInt() % 10)), carIndex,Math.abs((rand.nextInt() % 10) * 10), northPositions.get(j).x,y,Math.max(0, northPositions.get(i).x - Math.abs(rand.nextInt(map.length - 1))),y));
                cars.add(new Car(map,northIntersections,Math.abs(100L * (rand.nextInt() % 10)), carIndex,Math.abs((rand.nextInt() % 10) * 10), northPositions.get(j).x,y,0,y));
                carIndex++;
            }
            ArrayList<Position> southPositions = map.getLanePositions(i, Direction.SOUTH);
            Intersection[] southIntersections = map.getMyIntersections(i, Direction.SOUTH);
            y = southPositions.get(i).y;
            for (int j=0; j< southPositions.size()*loadFactor; j++){
                //cars.add(new Car(map,southIntersections,Math.abs(1000L * (rand.nextInt() % 10)), carIndex,Math.abs((rand.nextInt() % 10) * 10), southPositions.get(j).x, y, Math.min(map.length - 1, southPositions.get(i).x + Math.abs(rand.nextInt(map.length - 1))),y));
                cars.add(new Car(map,southIntersections,Math.abs(100L * (rand.nextInt() % 10)), carIndex,Math.abs((rand.nextInt() % 10) * 10), southPositions.get(j).x, y, map.length - 1,y));
                carIndex++;
            }
        }

        return cars;
    }



    public ArrayList<Car> GenerateCarsFromData(String filename, Intersection[] intersections) {
        ArrayList<Car> cars = new ArrayList<Car>();
        int id = 0;
        File file = new File(filename);
        try {
            Scanner input = new Scanner(file);
            while(input.hasNext()) {
                //Construction is: Car(Intersection[] intersections, Direction direction, long delay, int id, long speed)
                String nextLine = input.nextLine();
                System.out.println(nextLine);
                String[] fields = nextLine.split(",");

                if (fields.length == 3) {
                    //TODO: fix direction or default values on non-sensical data
                    String dir_value = fields[0];
                    dir_value = dir_value.replace(" ", "");
                    Direction direction = Direction.valueOf(Integer.parseInt(dir_value));

                    String delay_value = fields[1];
                    delay_value = delay_value.replace(" ", "");
                    long delay = Long.parseLong(delay_value);

                    String speed_value = fields[2];
                    speed_value = speed_value.replace(" ", "");
                    long speed = Long.parseLong(speed_value);

                    cars.add(new Car(intersections,
                            direction,
                            delay,
                            id,
                            speed));

                    id++;
                } else {
                    System.out.println("Not counting, too many parameters");
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File cannot be found, check path");
        }

        System.out.println("Created List of cars " + cars.size() + " in size");
        return cars;
    }
}