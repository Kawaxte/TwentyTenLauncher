package ee.twentyten.custom;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class UTF8PropertyResourceBundle extends UTF8ResourceBundle {

  public UTF8PropertyResourceBundle(InputStream is) throws IOException {
    super(is);
  }

  public UTF8PropertyResourceBundle(Reader reader) throws IOException {
    super(reader);
  }
}
