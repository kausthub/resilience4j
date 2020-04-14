import org.omg.SendingContext.RunTime;

public class FiliServiceImpl implements IFiliService {
  @Override
  public FiliResponse callBackend() {
    return new FiliResponse("success",200);
  }
}
