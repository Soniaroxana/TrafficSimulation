import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class GenerateCars {
    public static ArrayList<Car> GenerateRandomCars(int n, Intersection[] intersections){
        ArrayList<Car> cars;
        Random rand = new Random();
        for (int i = 0; i < n; i++){
            cars.add(new Car(intersections, rand.nextInt()%4,1000L*(rand.nextInt()%10),i,(rand.nextInt()%10)*10));
        }
        return cars;
    }
}
