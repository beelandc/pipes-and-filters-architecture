/*
 * Source modified from:
 * https://gist.github.com/roryokane/9606238
 */

package net.cbeeland.filter;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;
import net.cbeeland.thread.ThreadedRunner;

public abstract class Filter<I, O> extends ThreadedRunner {
  
  private static final Logger log = Logger.getLogger(Filter.class);

  protected Pipe<I> input;
  protected Pipe<O> output;

  public Filter(Pipe<I> input, Pipe<O> output, AtomicInteger threadCount) {
    super(threadCount);
    this.input = input;
    this.output = output;
  }

  @Override
  public void run() {
    log.debug("Start " + this.getClass().getSimpleName());
    transformBetween(input, output);
    log.debug("End " + this.getClass().getSimpleName());
  }

  protected abstract void transformBetween(Pipe<I> input, Pipe<O> output);

}
