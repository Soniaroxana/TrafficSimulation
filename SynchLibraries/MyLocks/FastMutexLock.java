package MyLocks;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class FastMutexLock implements MyLock {
  private AtomicIntegerArray flag;
  private volatile int X;
  private volatile int Y;
  private int numThread;

  public FastMutexLock(int numThread) {
    this.numThread = numThread;
    X = -1;
    Y = -1;
    flag = new AtomicIntegerArray(numThread);
    for (int i = 0; i < numThread; ++i) {
      flag.set(i, 0);
    }
  }

  public void lock(int myId) {
    while (true) {
      flag.set(myId, 1);
      X = myId;
      if (Y != -1) {
        flag.set(myId, 0);
        while (Y != -1);
        continue;
      } else {
        Y = myId;
        if (X == myId)
          return;
        else {
          flag.set(myId, 0);
          while(!areAllFlagDone()) { ; }
          if (Y == myId) {
            return;
          } else {
            while (Y != -1);
            continue;
          }
        }
      }
    }
  }

  public boolean areAllFlagDone() {
    for (int i = 0; i < numThread; ++i) {
      if (flag.get(i) == 1) {
        return false;
      }
    }
    return true;
  }

  public void unlock(int myId) {
    Y = -1;
    flag.set(myId, 0);
  }
}
