package net.cbeeland.filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;

public class SplitWordsFilter extends SplitFilter<String, String> {

  private static final Logger log = Logger.getLogger(SplitWordsFilter.class);

  public SplitWordsFilter(Pipe<String> input, Pipe<String> output) {
    super(input, output);
  }

  @Override
  protected List<String> splitOneToMany(String in) {
    // Capture Start of Processing
    Date splitWordsStart = new Date();

    // Split on non-alphanumeric characters (excluding apostrophes)
    String[] stringWordArr = in.split("[^a-zA-Z0-9']+");
    List<String> out = new ArrayList<String>(Arrays.asList(stringWordArr));

    // Capture End of Processing
    Date splitWordsEnd = new Date();

    // Print Split Words Filter Processing Start Time to log
    long executionDuration = splitWordsEnd.getTime() - splitWordsStart.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    log.info("Split Words Processing Start: " + sdf.format(splitWordsStart));
    log.info("Split Words Processing End: " + sdf.format(splitWordsEnd));
    log.info("Split Words Processing Duration: " + executionDuration + " ms");

    return out;
  }

}
