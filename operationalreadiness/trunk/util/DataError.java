package util;

import java.io.Serializable;

/**
 * An error possible to be returned by the DataStorageAdapter when entering data
 * 
 */

public class DataError implements Serializable {
	private static final long serialVersionUID = 7765988429268364822L;
	private String host;
	private String port;
	private Object error;
	
	public DataError(String host, String port, Exception e){
		this.host = host;
		this.port = port;
		error = e; 
	}
	
    String getHost() {
       return host;
    }

    String getPort() {
        return port;
    }

    Object getError() {
        return error.toString();
    }
}

