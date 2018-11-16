/*
 * Source modified from: https://gist.github.com/roryokane/9606238
 */

package net.cbeeland.pipe;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

public class PipeImpl<T> implements Pipe<T> {

  private static final Logger log = Logger.getLogger(PipeImpl.class);

  private Queue<T> buffer = new LinkedList<T>();
  private boolean isOpenForWriting = true;
  private boolean hasReadLastObject = false;

  public synchronized boolean put(T obj) {
    if (!isOpenForWriting) {
      log.error("Pipe is closed");
      throw new RuntimeException(new IOException("Pipe is closed; cannot write to it"));
    } else if (obj == null) {
      log.error("Cannot put null in pipe - null is reserved for pipe-empty sentinel value");
      throw new IllegalArgumentException("Cannot put null in pipe; null is reserved for pipe-empty sentinel value");
    }

    boolean wasAdded = buffer.add(obj);
    notifyAll();
    return wasAdded;
  }

  public synchronized T nextOrNullIfEmptied() throws InterruptedException {

    if (hasReadLastObject) {
      return null;
    }

    while (buffer.isEmpty()) {
      wait(); // pipe empty - wait
    }

    T obj = buffer.remove();
    if (obj == null) { // will be null if it's the last element
      hasReadLastObject = true;
    }
    return obj;
  }

  public synchronized void closeForWriting() {
    log.debug("Pipe Closing");
    isOpenForWriting = false;
    buffer.add(null);
    notifyAll();
  }
}

