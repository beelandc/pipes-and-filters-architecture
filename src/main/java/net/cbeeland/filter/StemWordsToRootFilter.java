package net.cbeeland.filter;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;
import net.cbeeland.stem.Stemmer;

public class StemWordsToRootFilter extends SimpleFilter<String, String> {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(StemWordsToRootFilter.class);

  public StemWordsToRootFilter(Pipe<String> input, Pipe<String> output, AtomicInteger threadCount) {
    super(input, output, threadCount);
  }

  @Override
  protected String transformOne(String in) {
    Stemmer stemmer = new Stemmer();
    stemmer.add(in.toCharArray(), in.length());
    stemmer.stem(); 
    String stemmedWord = stemmer.toString();
    return stemmedWord;
  }

}
