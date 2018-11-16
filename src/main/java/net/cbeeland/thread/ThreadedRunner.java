/*
 * Source modified from: https://gist.github.com/roryokane/9606238
 */

package net.cbeeland.thread;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ThreadedRunner implements Runnable {
  private boolean isStarted = false;
  public AtomicInteger threadCount;

  public ThreadedRunner(AtomicInteger threads) {
    if (threads == null) {
      threadCount = new AtomicInteger(1);
    } else {
      threadCount = threads;
    }
  }

  @Override
  abstract public void run();

  public void start() {
    if (!isStarted) {
      isStarted = true;

      Thread thread = new Thread(this);
      thread.start();
    }

  }


  public int getThreadCount() {
    return threadCount.get();
  }

  public void decrementThreadCount() {
    threadCount.decrementAndGet();
  }


  public void stop() {
    isStarted = false;
  }

}
