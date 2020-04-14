import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Try;

import java.time.Duration;
import java.util.function.Supplier;

public class Resilience {

  private IFiliService service;
  private Supplier<FiliResponse> decoratedSupplier;

  public Resilience(IFiliService service) {
    this.service = service;
  }

  public void initialize() {
  // default circuit breaker
  //CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("Fili-Backend");

    // can configure stuff like failure rate and wait until 5 seconds before you try to close the circuit etc
    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                                                                  .failureRateThreshold(20)
                                                                  .ringBufferSizeInClosedState(5)
                                                                  .waitDurationInOpenState(Duration.ofSeconds(5))
                                                                  .build();

    CircuitBreaker circuitBreaker = CircuitBreaker.of("Fili-backend",circuitBreakerConfig);

  // Create a Retry with exponential backoff
    //In simple terms, the clients wait progressively longer intervals between consecutive retries:
    // formula: wait_interval = base * 5^n
    // base is the initial interval, ie, wait for the first retry
    // n is the number of failures that have occurred

    // default retry
  // Retry retry = Retry.ofDefaults("Fili-Backend");

    // retry with 4s between every retry and max 2 attempts
  //    RetryConfig retryConfig = RetryConfig.custom()
  //                                        .maxAttempts(2)
  //                                        .waitDuration(Duration.ofSeconds(4))
  //                                        .build();

    // exponential backoff alogrithm, initialInterval is in milliseconds, here 1000 = 1 second
    IntervalFunction intervalFn =
      IntervalFunction.ofExponentialBackoff(1000, 5);

    RetryConfig retryConfig = RetryConfig.custom()
                                        .maxAttempts(2)
                                        .intervalFunction(intervalFn)
                                        .build();

   Retry retry = Retry.of("Fili-backend",retryConfig);


   // custom ratelimter implemented
    RateLimiterConfig config = RateLimiterConfig.custom().limitForPeriod(2).build();
    RateLimiter rateLimiter = RateLimiter.of("Fili-backend",config);

  // You can add multiple such resilience properties before calling you actual service
  // Decorate your call with a CircuitBreaker
  decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, () -> service.callBackend());

  // Decorate your call with rate limiter
  decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter,decoratedSupplier);

  // Decorate your call with automatic retry
    decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
  }

  public Try<FiliResponse> execute() {
    // Execute the decorated supplier and recover from any exception and return a fallback in such a case if you want
    Try<FiliResponse> result = Try.ofSupplier(decoratedSupplier)
      .recover(throwable -> new FiliResponse("fallback",200));

    return result;
  }

}
