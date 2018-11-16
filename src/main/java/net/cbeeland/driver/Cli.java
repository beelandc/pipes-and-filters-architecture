package net.cbeeland.driver;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import net.cbeeland.filter.Filter;
import net.cbeeland.filter.LowercaseWordsFilter;
import net.cbeeland.filter.RemoveNonAlphabeticalTextFilter;
import net.cbeeland.filter.RemoveStopWordsFilter;
import net.cbeeland.filter.SplitWordsFilter;
import net.cbeeland.filter.StemWordsToRootFilter;
import net.cbeeland.pipe.Pipe;
import net.cbeeland.pipe.PipeImpl;
import net.cbeeland.sink.Sink;
import net.cbeeland.sink.WordSink;

import org.apache.commons.io.FileUtils;

public class Cli {
  private static final Logger log = Logger.getLogger(Cli.class);
  private String[] args = null;
  private Options options = new Options();

  private String filePath;
  private String fileEncoding;

  private AtomicInteger RemoveNonAlphabeticalTextThreadCount = new AtomicInteger(5);
  private AtomicInteger stemWordsToRootFilterThreadCount = new AtomicInteger(5);

  public Cli(String[] args) {

    this.args = args;

    options.addOption("h", "help", false, "show help.");

    // Define CLI options
    options.addOption("f", "fileToProcess", true, "The complete path to the file to process");
    options.addOption("e", "fileEncoding", false, "The encoding of the file. Defaults to UTF-8");

  }

  public void parse() {
    CommandLineParser parser = new BasicParser();

    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("h")) {
        help();
      }

      if (cmd.hasOption("f")) {
        filePath = cmd.getOptionValue("f");
      } else {
        log.error("Missing f option");
        help();
      }

      if (cmd.hasOption("e")) {
        fileEncoding = cmd.getOptionValue("e");
      } else {
        fileEncoding = "UTF-8";
      }

      // Pipe & Filter components
      // Pipes
      final Pipe<String> in_splitWordsFilter = new PipeImpl<String>();
      final Pipe<String> splitWordsFilter_removeNonAlphabeticalTextFilter = new PipeImpl<String>();
      final Pipe<String> removeNonAlphabeticalTextFilter_lowercaseWordsFilter = new PipeImpl<String>();
      final Pipe<String> lowercaseWordsFilter_removeStopWordsFilter = new PipeImpl<String>();
      final Pipe<String> removeStopWordsFilter_stemWordsToRootFilter = new PipeImpl<String>();
      final Pipe<String> stemWordsToRootFilter_WordSink = new PipeImpl<String>();

      // Filters & Data Sink
      Filter<String, String> splitWordsFilter = new SplitWordsFilter(in_splitWordsFilter, splitWordsFilter_removeNonAlphabeticalTextFilter);
      Filter<String, String> removeStopWordsFilter =
          new RemoveStopWordsFilter(lowercaseWordsFilter_removeStopWordsFilter, removeStopWordsFilter_stemWordsToRootFilter, null);
      Filter<String, String> lowercaseWordsFilter =
          new LowercaseWordsFilter(removeNonAlphabeticalTextFilter_lowercaseWordsFilter, lowercaseWordsFilter_removeStopWordsFilter, null);
      final Sink<String> wordSink = new WordSink(stemWordsToRootFilter_WordSink);
      
      // Create multiple threads for these two filters
      for (int x = 0; x < 5; x++) {
        final Filter<String, String> removeNonAlphabeticalTextFilter = new RemoveNonAlphabeticalTextFilter(splitWordsFilter_removeNonAlphabeticalTextFilter,
            removeNonAlphabeticalTextFilter_lowercaseWordsFilter, RemoveNonAlphabeticalTextThreadCount);
        Filter<String, String> stemWordsToRootFilter =
            new StemWordsToRootFilter(removeStopWordsFilter_stemWordsToRootFilter, stemWordsToRootFilter_WordSink, stemWordsToRootFilterThreadCount);

        Thread t1 = new Thread(removeNonAlphabeticalTextFilter);
        Thread t3 = new Thread(stemWordsToRootFilter);
        t1.start();
        t3.start();
      }
      
      // Start Pipe & Filter Components
      splitWordsFilter.start();
      lowercaseWordsFilter.start();
      removeStopWordsFilter.start();
      wordSink.start();


      // Capture Start of Processing
      Date overallStart = new Date();

      // Load File Content
      File file = new File(filePath);
      String fileContent = FileUtils.readFileToString(file, fileEncoding);

      // Push file content into initial pipe
      in_splitWordsFilter.put(fileContent);
      in_splitWordsFilter.closeForWriting();

      // Print Overall Processing Start time to log
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
      log.info("Overall Processing Start: " + sdf.format(overallStart));

    } catch (ParseException e) {
      log.error("Failed to parse comand line properties", e);
      help();
    } catch (IOException e) {
      log.error("Unexpected IOException when reading input file: " + filePath, e);
    }

  }

  private void help() {
    // This prints out some help
    HelpFormatter formater = new HelpFormatter();

    formater.printHelp("Main", options);
    System.exit(0);
  }
}
