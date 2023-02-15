package ee.twentyten.request;

public enum EMethod {
  GET("GET"), POST("POST");

  private final String method;

  EMethod(String method) {
    this.method = method;
  }

  public static EMethod getMethod(String method) {
    for (EMethod m : EMethod.values()) {
      if (m.method.equals(method)) {
        return m;
      }
    }
    return null;
  }
}
