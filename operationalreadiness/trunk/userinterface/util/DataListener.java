package userinterface.util;

import java.io.File;
import java.util.List;

import util.DataType;


/**
 * Interface DataListener listens for incoming Data, DataIDs, and class files from the DataStorage Module 
 * @author Kevin Desmond
 */
public interface DataListener {
	void didReceiveAvailableDataIDs(List<String> availableDataIDs);
	void didReceiveDataUpdate(DataType dataType);
	void didReceiveClassFile(File filename);
}

