package net.cbeeland.filter;

import java.util.List;

import org.apache.log4j.Logger;

import net.cbeeland.pipe.Pipe;

public abstract class SplitFilter<I, O> extends Filter<I, O> {

  private static final Logger log = Logger.getLogger(SplitFilter.class);

  public SplitFilter(Pipe<I> input, Pipe<O> output) {
    super(input, output, null);
  }

  @Override
  protected void transformBetween(Pipe<I> input, Pipe<O> output) {
    try {
      I in;
      while ((in = input.nextOrNullIfEmptied()) != null) {
        List<O> out = splitOneToMany(in);

        if ((out != null) && (!out.isEmpty())) {
          for (O outElem : out) {
            if(outElem != null){
              output.put(outElem);
            }
          }
        } else {
          log.error("Unexpected null or empty list");
        }

      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    output.closeForWriting();
    log.debug("SplitFilter done and output pipe closed for writing");
  }

  protected abstract List<O> splitOneToMany(I in);

}
