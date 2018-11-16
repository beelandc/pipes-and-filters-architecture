package net.cbeeland.sink;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;
import net.cbeeland.sink.Sink;

public class WordSink extends Sink<String> {

  private static final Logger log = Logger.getLogger(WordSink.class);
  private Map<String, Integer> wordCountMap;

  public WordSink(Pipe<String> input) {
    super(input);
    wordCountMap = new HashMap<String, Integer>();
  }

  @Override
  public void takeFrom(Pipe<String> pipe) {
    try {
      String in;
      while ((in = pipe.nextOrNullIfEmptied()) != null) {
        addToWordCountMap(in);
      }

      sortAndPrintTop10FrequencyWords();

    } catch (InterruptedException e) {
      log.error("Unexpected InterruptedException", e);
    }

    // Print overall processing end time to log
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
    Date overallEnd = new Date();
    log.info("Overall Processing End: " + sdf.format(overallEnd));
  }

  private void sortAndPrintTop10FrequencyWords() {
    // Generate list of Entry sorted by frequency of word
    List<Entry<String, Integer>> wordCountSortedList =
        wordCountMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).collect(Collectors.toList());

    // Trim sorted list to 10 elements
    wordCountSortedList = wordCountSortedList.subList(0, 10);

    // Prepare objects for sorting tied words
    Entry<String, Integer> wordCount = null;
    Entry<String, Integer> nextWordCount = null;
    List<Entry<String, Integer>> tiedEntriesList = new ArrayList<Entry<String, Integer>>();

    log.debug("Top Words by Frequency:");
    log.debug("-----------------------");
    System.out.println("Top Words by Frequency:");
    System.out.println("-----------------------");

    for (int x = 0; x < wordCountSortedList.size(); x++) {
      wordCount = wordCountSortedList.get(x);

      if (x < wordCountSortedList.size() - 1) {
        nextWordCount = wordCountSortedList.get(x + 1);
      } else {
        nextWordCount = null;
      }

      if ((nextWordCount != null) && (wordCount.getValue() == nextWordCount.getValue())) {
        // Add to "tied" list for alphabetical sorting
        if (!tiedEntriesList.contains(wordCount)) {
          tiedEntriesList.add(wordCount);
        }
        tiedEntriesList.add(nextWordCount);

      } else {
        if (!tiedEntriesList.isEmpty()) {
          sortAndPrintTiedWords(tiedEntriesList);
        } else {
          // Print current word
          log.debug(wordCount.getKey() + " - " + wordCount.getValue());
          System.out.println(wordCount.getKey() + " - " + wordCount.getValue());
        }
      }
    }

    // Ensure all words have printed after iterating
    if (!tiedEntriesList.isEmpty()) {
      sortAndPrintTiedWords(tiedEntriesList);
    }
  }

  private void addToWordCountMap(String in) {
    // If word already logged, increment count, otherwise add with count of 1
    if (wordCountMap.containsKey(in)) {
      wordCountMap.put(in, (wordCountMap.get(in) + 1));
    } else {
      wordCountMap.put(in, 1);
    }
  }

  private void sortAndPrintTiedWords(List<Entry<String, Integer>> tiedEntriesList) {
    // Sort "tied" list
    tiedEntriesList.sort(new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
        return e1.getKey().compareTo(e2.getKey());
      }
    });

    // Print any sorted words
    for (Entry<String, Integer> sortedEntry : tiedEntriesList) {
      log.debug(sortedEntry.getKey() + " - " + sortedEntry.getValue());
      System.out.println((sortedEntry.getKey() + " - " + sortedEntry.getValue()));
    }

    // Clear list for next set of tied words
    tiedEntriesList.clear();
  }
}
