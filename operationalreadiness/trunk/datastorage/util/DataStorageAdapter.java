package datastorage.util;

import java.io.Serializable;
import java.util.List;

import util.DataType;


/**
 * The adapter to be implemented for storing DataTypes
 *
 */
public interface DataStorageAdapter {
    List<DataType> getData(String dataID, Object constraint);
    List<String> getAvailableDataIDs();
    Serializable putData(DataType data);
}

