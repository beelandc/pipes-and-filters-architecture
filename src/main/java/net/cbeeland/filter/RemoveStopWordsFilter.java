
package net.cbeeland.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;

public class RemoveStopWordsFilter extends SimpleFilter<String, String> {

  private static final Logger log = Logger.getLogger(RemoveStopWordsFilter.class);
  private final String STOPWORDS_FILE_PATH = "stopwords.txt";
  private final String STOPWORDS_FILE_ENCODING = "UTF-8";
  private Set<String> stopWordsSet;

  public RemoveStopWordsFilter(Pipe<String> input, Pipe<String> output, AtomicInteger threadCount) {
    super(input, output, threadCount);

    InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream((STOPWORDS_FILE_PATH));
    StringWriter writer = new StringWriter();

    try {
      IOUtils.copy(in, writer, STOPWORDS_FILE_ENCODING);
      String fileContent = writer.toString();
      stopWordsSet = new HashSet<String>();

      String[] stopwordsArray = fileContent.split("\\r?\\n");
      for (String s : stopwordsArray) {
        stopWordsSet.add(s);
      }
    } catch (IOException e) {
      log.fatal("Unexpected IOException processing Stopwords file at: " + STOPWORDS_FILE_PATH, e);
    }

  }

  @Override
  protected String transformOne(String in) {
    // SimpleFilter will filter out null values from pipeline
    if (stopWordsSet.contains(in.toLowerCase())) {
      return null;
    } else {
      return in;
    }
  }

}
