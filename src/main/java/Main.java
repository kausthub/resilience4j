import io.vavr.control.Try;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    System.out.println("starting");

    // all these can be understood from these site:
    // 1. https://www.baeldung.com/resilience4j
    // 2. https://github.com/resilience4j/resilience4j
    // 3. https://www.baeldung.com/resilience4j-backoff-jitter

    Resilience r = new Resilience(new FiliServiceImpl());
    r.initialize();
    Try<FiliResponse> res = r.execute();
    System.out.println("Result is: " + res.get().getRes());

    System.out.println("completed");
  }
}
