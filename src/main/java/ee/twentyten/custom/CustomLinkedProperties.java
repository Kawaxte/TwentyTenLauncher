package ee.twentyten.custom;

import ee.twentyten.util.LoggerHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CustomLinkedProperties extends Properties {

  private static final long serialVersionUID = 1L;
  private final LinkedHashMap<Object, Object> linkedMap;

  {
    linkedMap = new LinkedHashMap<>();
  }

  /**
   * Puts the specified key and value into the map, extending the behavior of
   * the base class' implementation of the put method.
   *
   * @param key   The key to be added to the map.
   * @param value The value to be associated with the specified key.
   * @return The previous value associated with the specified key, or null if
   * there was no mapping for the key.
   */
  @Override
  public synchronized Object put(
      Object key,
      Object value
  ) {

    /* Put the key and value into the map. */
    linkedMap.put(key, value);
    return super.put(key, value);
  }

  /**
   * Returns a {@link Set} view of the keys contained in this map. The set is
   * backed by the map, so changes to the map are reflected in the set, and
   * vice-versa. If the map is modified while an iteration over the set is in
   * progress, the results of the iteration are undefined.
   *
   * @return a set view of the keys contained in this map.
   */
  @Override
  public Set<Object> keySet() {
    return linkedMap.keySet();
  }

  /**
   * Returns a set view of the mappings contained in this map. The set's
   * iterator returns the entries in ascending key order. The set is backed by
   * the map, so changes to the map are reflected in the set, and vice-versa. If
   * the map is modified while an iteration over the set is in progress, the
   * results of the iteration are undefined.
   *
   * @return a set view of the mappings contained in this map, sorted in
   * ascending key order.
   */
  @Override
  public Set<Map.Entry<Object, Object>> entrySet() {
    return linkedMap.entrySet();
  }

  /**
   * Writes the property list (key and element pairs) in this Properties table
   * to the output stream. This method is called by the store method.
   * <p>
   * The stream is written using the ISO 8859-1 character encoding.
   *
   * @param os       the output stream to write the properties to.
   * @param comments a description of the property list, or {@code null} if no
   *                 description is desired.
   */
  @Override
  public void store(
      OutputStream os, String comments
  ) {

    /* Try to store the properties. */
    try {

      /* If there are comments, write them to the output stream. */
      if (comments != null) {

        /* Add a new line before the comment. */
        String comment = System.lineSeparator()
            + "# "
            + comments
            + System.lineSeparator();

        /* Write the comment to the output stream. */
        os.write(comment.getBytes(StandardCharsets.UTF_8));
      }

      /* Create a set of entries from the map. */
      Set<Map.Entry<Object, Object>> entries = entrySet();

      /* Iterate over the entries. */
      for (Map.Entry<Object, Object> entry : entries) {

        /* Get the key from the entry. */
        String key = (String) entry.getKey();

        /* Write the key and value to the output stream. */
        os.write(key.getBytes(StandardCharsets.UTF_8));
        os.write("=".getBytes(StandardCharsets.UTF_8));

        /* Get the value from the entry. */
        String value = (String) entry.getValue();

        /* Write the value to the output stream. */
        os.write(value.getBytes(StandardCharsets.UTF_8));
        os.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException ioe) {
      /* Create a string for the error message. */
      String errorString = "Failed to store properties";

      /* Log the error. */
      LoggerHelper.logError(errorString, ioe, true);
    }
  }
}
