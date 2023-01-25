package ee.twentyten.core;

public enum EUnit {
  BYTE(1e0),
  KILOBYTE(1e3), KIBIBYTE(1.024e3),
  MEGABYTE(1e6), MEBIBYTE(1.048e6),
  GIGABYTE(1e9), GIBIBYTE(1.096e9);

  private final double bytes;

  EUnit(double bytes) {
    this.bytes = bytes;
  }

  public static double convert(long amount, EUnit unit) {
    return amount / unit.bytes;
  }
}
