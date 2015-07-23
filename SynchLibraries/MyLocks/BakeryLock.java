package MyLocks;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class BakeryLock implements MyLock {
    private AtomicIntegerArray flag;
    private AtomicIntegerArray label;
    private int num;

    public BakeryLock(int numThread) {
        num = numThread;
        flag = new AtomicIntegerArray(num);
        label = new AtomicIntegerArray(num);
        for (int i = 0; i < num ; ++i){
            flag.set(i, 0);
            label.set(i,0);
        }
    }

    @Override
    public void lock(int myId) {
        flag.set(myId, 1);
        int max = 0;
        for (int i = 0; i < num; ++i){
            max = (label.get(i) > max) ? label.get(i) : max;
        }
        label.set(myId, max + 1);

        boolean existSmall = true;
        while (checkLabel(myId)) { ; }


    }

    @Override
    public void unlock(int myId) {
        flag.set(myId, 0);
    }

    boolean checkLabel(int myId) {
        for (int k = 0; k < label.length(); k++) {
            if ((k != myId) && (flag.get(k) == 1) && (
                        (label.get(k) < label.get(myId)) ||
                        ((label.get(k) == label.get(myId)) && (k < myId)))) {
                return true;
            }
        }
        return false;
    }
}
