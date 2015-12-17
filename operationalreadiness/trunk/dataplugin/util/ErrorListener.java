package dataplugin.util;

import util.DataError;

/**
 * Interface for receiving DataErrors from DPNetworkManager
 * 
 */

public interface ErrorListener {
    void didReceiveError(DataError error);
}
