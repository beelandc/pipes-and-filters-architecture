/*
 * Source retrieved from: https://gist.github.com/roryokane/9606238
 */

package net.cbeeland.sink;

import net.cbeeland.pipe.Pipe;
import net.cbeeland.thread.ThreadedRunner;

public abstract class Sink<T> extends ThreadedRunner {
  protected Pipe<T> input;

  public Sink(Pipe<T> input) {
    super(null);
    this.input = input;
  }

  @Override
  public void run() {
    takeFrom(input);
  }

  public abstract void takeFrom(Pipe<T> pipe);
}
