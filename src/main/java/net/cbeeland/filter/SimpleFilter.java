/*
 * Source modified from: https://gist.github.com/roryokane/9606238
 */

package net.cbeeland.filter;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;

public abstract class SimpleFilter<I, O> extends Filter<I, O> {

  private static final Logger log = Logger.getLogger(SimpleFilter.class);
  private final int SAMPLE_COUNT = 10000;

  public SimpleFilter(Pipe<I> input, Pipe<O> output, AtomicInteger threadCount) {
    super(input, output, threadCount);
  }

  @Override
  protected void transformBetween(Pipe<I> input, Pipe<O> output) {
    try {
      I in;
      int processCount = 0;
      Date simpleFilterStart = null;
      Date simpleFilterEnd = null;
      Double simpleFilterCumulativeDuration = new Double(0.0);
      String simpleFilterName = this.getClass().getSimpleName();

      while ((in = input.nextOrNullIfEmptied()) != null) {
        log.debug(this.getClass().getSimpleName() + " Start while loop. In = " + in);
        if (processCount < SAMPLE_COUNT) {
          // Capture Start of Processing
          simpleFilterStart = new Date();
        }

        // Process input
        O out = transformOne(in);
        if (out != null) {
          output.put(out);
        }

        if (processCount < SAMPLE_COUNT) {
          // Capture End of Processing
          simpleFilterEnd = new Date();
          simpleFilterCumulativeDuration += (simpleFilterEnd.getTime() - simpleFilterStart.getTime());
        }

        if (processCount == SAMPLE_COUNT) {

          // Print Split Words Filter Processing Avg Time to log
          double avgProcessingDuration = (Double.valueOf(simpleFilterCumulativeDuration) / Double.valueOf(SAMPLE_COUNT));
          log.info(simpleFilterName + " Avg Processing Duration: " + avgProcessingDuration + " ms");
        }

        // Increment processCount
        processCount++;
      }
    } catch (InterruptedException e) {
      log.error(e);
      return;
    }

    if (getThreadCount() < 2) {
      output.closeForWriting();
      log.debug(this.getClass().getSimpleName() + " done and output pipe closed for writing");
    } else {
      synchronized (threadCount) {
        decrementThreadCount();
      }
    }

  }

  protected abstract O transformOne(I in);
}
