import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class GenerateCars {
    public static ArrayList<Car> GenerateRandomCars(int n, Intersection[] intersections){
        ArrayList<Car> cars = new ArrayList<Car>();
        Random rand = new Random();

        for (int i = 0; i < n; i++){
            cars.add(new Car(intersections, Direction.valueOf(rand.nextInt(4)), 1000L * (rand.nextInt(10)), i, (rand.nextInt(10)) * 10));
        }

        return cars;
    }

    public static ArrayList<Car> GenerateCarsOnMap(int n, Map map) {
        ArrayList<Car> cars = new ArrayList<Car>();
        Random rand = new Random();

        Intersection[] intersections = map.getIntersections();

        for (int i = 0; i < n; i++) {
            cars.add(new Car(intersections, Direction.valueOf(rand.nextInt() % 4), 1000L * (rand.nextInt() % 10), i, (rand.nextInt() % 10) * 10));
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