import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ResiliencyTest {

  @Mock
  private IFiliService filiService;

  private Resilience resilience;

  @Before
  public void before() {
    resilience = new Resilience(filiService);
    resilience.initialize();
  }

  @Test
  public void retryTest() {
    when(filiService.callBackend()).thenThrow(new RuntimeException());

    resilience.execute();
    verify(filiService, times(2)).callBackend();
  }

  @Test
  public void fallbackTest() {
    when(filiService.callBackend()).thenThrow(new RuntimeException());

    Try<FiliResponse> filiResponse = resilience.execute();

    assertTrue(filiResponse.get().getRes().equalsIgnoreCase("fallback"));

  }
}
