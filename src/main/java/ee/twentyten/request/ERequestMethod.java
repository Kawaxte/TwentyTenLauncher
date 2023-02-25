package ee.twentyten.request;

public enum ERequestMethod {
  GET("GET"), POST("POST"), HEAD("HEAD");

  private final String method;

  ERequestMethod(String method) {
    this.method = method;
  }

  public static ERequestMethod getMethod(String method) {
    for (ERequestMethod m : ERequestMethod.values()) {
      if (m.method.equals(method)) {
        return m;
      }
    }
    return null;
  }
}
