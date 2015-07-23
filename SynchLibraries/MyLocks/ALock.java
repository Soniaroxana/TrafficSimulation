// TODO
// Implement Anderson’s array-based lock
package MyLocks;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neelshah on 6/27/15.
 */
public class ALock implements MyLock {

    int n;
    // TODO How can I make sure these are padded to avoid false sharing?
    boolean[] available;
    AtomicInteger tailSlot;
    ThreadLocal<Integer> mySlot;


    public ALock(int n) {
        this.n = n;

        available = new boolean[n];
        available[0] = true;
        for (int i = 1; i < available.length; i++) {
            available[i] = false;
        }

        tailSlot = new AtomicInteger(0);

        mySlot = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };
    }

    @Override
    public void lock(int myId) {
        mySlot.set(tailSlot.getAndIncrement() % n);
        while (!available[mySlot.get()]) ;
    }

    @Override
    public void unlock(int myId) {
        available[mySlot.get()] = false;
        available[(mySlot.get() + 1) % n] = true;
    }
}