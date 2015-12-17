package datastorage.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import util.DataError;
import util.DataType;

import dataplugin.util.DataTypeStub;

// stub for testing
public class DataStorageStub implements DataStorageAdapter {

	@Override
	public List<DataType> getData(String dataID, Object constraint) {
		return new LinkedList<DataType>(Arrays.asList(new DataTypeStub("data1"), new DataTypeStub("data2")));       // STUB RETURN
	}

	@Override
	public List<String> getAvailableDataIDs() {
		return new LinkedList<String>(Arrays.asList("DataID1", "DataID2", "DataID3", "DataID4"));
	}

	@Override
	public Serializable putData(DataType data) {
		// a pseudo error
		Serializable error = null;//(Serializable) new DataError(null, null, null);
		return error;
	}

}
