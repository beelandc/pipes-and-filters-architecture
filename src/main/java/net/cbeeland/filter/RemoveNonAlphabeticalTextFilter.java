package net.cbeeland.filter;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;

public class RemoveNonAlphabeticalTextFilter extends SimpleFilter<String, String> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(RemoveNonAlphabeticalTextFilter.class);

  public RemoveNonAlphabeticalTextFilter(Pipe<String> input, Pipe<String> output, AtomicInteger threadCount) {
    super(input, output, threadCount);
  }

  @Override
  protected String transformOne(String in) {

    // Use Regex to remove all non-alphabetical characters
    String alphabeticalString = in.replaceAll("[^A-Za-z]", "");

    // SimpleFilter will filter out null values from pipeline
    if (alphabeticalString.isEmpty()) {
      return null;
    } else {
      return alphabeticalString;
    }
  }

}
