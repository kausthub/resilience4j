public class FiliResponse {
  private String res;
  private int code;

  public FiliResponse(String res, int code){
    this.res = res;
    this.code = code;
  }

  public String getRes() {
    return res;
  }

  public void setRes(String res) {
    this.res = res;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
