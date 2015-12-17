package util;

import java.io.File;
import java.io.Serializable;

/**
 * Interface for objects that will be receiving data and class files.
 *
 */
public interface DataReceivedListener {

	/**
	 * Tell object new data it is to receive
	 * @param data
	 * @return Possible DataError 
	 */
	public Serializable dataReceived(Serializable data);
	
	/**
	 * Gives the object the File of a newly received class 
	 * @param file
	 */
	public void classReceived(File file);
}
