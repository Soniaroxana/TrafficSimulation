// TODO
// Implement the MCS Lock
package MyLocks;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by neelshah on 6/27/15.
 */
public class MCSLock implements MyLock {

    AtomicReference<Node> tailNode;
    ThreadLocal<Node> myNode;

    public MCSLock() {
        tailNode = new AtomicReference<Node>(null);

        myNode = new ThreadLocal<Node>() {
            @Override
            protected Node initialValue() {
                return new Node();
            }
        };
    }

    @Override
    public void lock(int myId) {
        Node pred = tailNode.getAndSet(myNode.get());

        if (pred != null) {
            myNode.get().locked = true;
            pred.next = myNode.get();
            while (myNode.get().locked) ;
        }
    }

    @Override
    public void unlock(int myId) {
        if (myNode.get().next == null) {
            if (tailNode.compareAndSet(myNode.get(), null)) return;
            while (myNode.get().next == null) ;
        }

        myNode.get().next.locked = false;
        myNode.get().next = null;
    }

    private class Node {
        boolean locked;
        Node next = null;
    }
}