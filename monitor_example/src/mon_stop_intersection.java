/**
 * Created by davidali on 7/27/2015.
 */
public class mon_stop_intersection {
    public static void main(String[] args) {
        System.out.println("Starting program");
        int total_passing_car = 10;
        Intersection_Monitor intersectionMonitor = new Intersection_Monitor(total_passing_car);
        new carsAtIntersection(intersectionMonitor);
        new carsPassingIntersection(intersectionMonitor);
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (intersectionMonitor.total_allowed == 0) {
                System.out.println("Resetting intersection");
                intersectionMonitor.total_allowed = 10;
            }

        }
    }
}

class Intersection_Monitor {

    boolean vehicle_in_inter = false;
    int total_allowed;
    int n;
    public Intersection_Monitor(int cars_allowed) {
        total_allowed = cars_allowed;
    }

    synchronized int getCar() {
        if (total_allowed > 0) {
            if (!vehicle_in_inter) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException caught");
                }
            }
            System.out.println("Taking: " + n);
            vehicle_in_inter = false;
            total_allowed--;
            notify();
            return n;
        } else {
            return -1;
        }
    }

    synchronized void putCar(int car_id) {
        if (vehicle_in_inter) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        this.n = car_id;
        vehicle_in_inter = true;
        System.out.println("Put car: " + n);
        notify();
    }
}

class carsAtIntersection implements Runnable{
    Intersection_Monitor inter;

    public carsAtIntersection(Intersection_Monitor i) {
        inter = i;
        new Thread(this, "car_start").start();
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {

            inter.putCar(i++);
        }
    }
}

class carsPassingIntersection implements Runnable{
    Intersection_Monitor inter;

    public carsPassingIntersection(Intersection_Monitor i ) {
        inter = i;
        new Thread(this, "car_passing_start").start();
    }
    @Override
    public void run() {
        while (true) {
            if(inter.getCar() == -1) {
                try {
                    System.out.println("Full, slowing down allowed cars");
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}