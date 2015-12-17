package util;

/**
 * Global data interface. Must have a unique data ID.
 *
 */
public interface DataType extends java.io.Serializable {
    String getDataID();
}

