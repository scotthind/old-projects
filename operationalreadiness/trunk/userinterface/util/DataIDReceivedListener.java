package userinterface.util;

import java.util.List;

import util.DataReceivedListener;

/**
 * Listens for DataIDs from the DataStorage Module
 * @author Kevin Desmond
 */
public interface DataIDReceivedListener extends DataReceivedListener {
	public void receiveDataIDs(List<String> dataIDs);
}
