
package net.cbeeland.filter;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;

public class LowercaseWordsFilter extends SimpleFilter<String, String> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(RemoveStopWordsFilter.class);

  public LowercaseWordsFilter(Pipe<String> input, Pipe<String> output, AtomicInteger threadCount) {
    super(input, output, threadCount);
  }

  @Override
  protected String transformOne(String in) {
    return in.toLowerCase();
  }

}
