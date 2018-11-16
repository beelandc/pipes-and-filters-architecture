/*
 * Interface reused from:
 * https://gist.github.com/roryokane/9606238
 */

package net.cbeeland.pipe;

public interface Pipe<T> {
    public boolean put(T obj);
    public T nextOrNullIfEmptied() throws InterruptedException;
    public void closeForWriting();
}