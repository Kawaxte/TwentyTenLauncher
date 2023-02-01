package ee.twentyten.custom;

public interface CustomThreadFactory {

  Thread newThread(String name, Runnable r);
}
