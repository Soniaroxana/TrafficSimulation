package Barriers;

import java.util.concurrent.Semaphore;

public class CyclicBarrier {
  // TODO: Declare variables and the constructor for CyclicBarrier
  // Note that you can use only semaphores but not synchronized blocks and
  // locks
  private Semaphore waitParties;
  private Semaphore mutex;
  private int waitCount;
  private int parties;

  public CyclicBarrier(int parties) {
    // TODO: The constructor for this CyclicBarrier
    this.waitParties = new Semaphore(1);
    this.waitCount = parties;
    this.parties = parties;
    this.mutex = new Semaphore(1);
  }

  public int await() throws InterruptedException {
    // Waits until all parties have invoked await on this barrier.
    // If the current thread is not the last to arrive then it is
    // disabled for thread scheduling purposes and lies dormant until
    // the last thread arrives.
    // Returns: the arrival index of the current thread, where index
    // (parties - 1) indicates the first to arrive and zero indicates
    // the last to arrive.
    mutex.acquire();
    waitCount--;
    int index = waitCount;
    System.out.println("Entering"+index);
    if (waitCount ==0){
      waitParties.release();
      mutex.release();
    } else if (waitCount == parties - 1) {
      waitParties.acquire();
      mutex.release();
      while(waitParties.availablePermits()==0);
    } else {
      mutex.release();
      while(waitParties.availablePermits()==0);
    }
    return index;
  }
}
