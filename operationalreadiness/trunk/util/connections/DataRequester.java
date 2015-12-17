package util.connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.FrameworkHandler;


import datastorage.util.DataStorageManager;

/**
 * A single connection to handle requests of fetching information.
 * 
 * DataRequester can ask for all available data IDs, a specific data by ID, and all the available classes.
 * 
 * @author Tom Renn
 */
public class DataRequester implements Runnable{
	public static final int GET_DATA_IDS = 0;
	public static final int GET_DATA = 1;
	public static final int GET_CLASSES = 2;
	
	private Socket socket;
	private DataStorageManager dataManager;
	
	private DataInputStream dataInput;
	private ObjectInputStream objectInput;
	private DataOutputStream dataOutput;  
	private ObjectOutputStream objectOutput;
	
	/**
	 * Basic Constructor
	 * @param socket the connection
	 * @param dsm DataStorageManager to request information from
	 * @throws IOException
	 */
	public DataRequester(Socket socket, DataStorageManager dsm) throws IOException {
		this.socket = socket;
		this.dataManager = dsm;
		
		objectInput = new ObjectInputStream(socket.getInputStream());
		objectOutput = new ObjectOutputStream(socket.getOutputStream());

		dataInput = new DataInputStream(socket.getInputStream());
		dataOutput = new DataOutputStream(socket.getOutputStream());
	}
	
	/**
	 * Reads the request/action, perform it, and write back the response. 
	 */
	@Override
	public void run() {
		while (true) {
			
			try {
				int action = dataInput.readInt();   // read request
				
				Object response = perform(action);  // perform action
				
				objectOutput.writeObject(response); // send response [null in case of GET_CLASSES]
			} catch (Exception e) {
				FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
				break; // thread needs to die
			}
		}
	}

	/**
	 * Executes a defined action and returns any possible response
	 * 
	 * @param action
	 * @return Object representing a possible response
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private Object perform(int action) throws ClassNotFoundException, IOException {
		Object response = null;
		
		switch (action){
		
			// get all the available data IDs or an empty list
			case GET_DATA_IDS:
				response = dataManager.getAvailableDataIDs();
				break;
				
			// get specific data from ID and constraint object
			case GET_DATA:
				String dataID = (String) objectInput.readObject();
				Object constraint = objectInput.readObject();
				
				response = dataManager.getDataForID(dataID, constraint);
				break;
				
			// send out all the classes
			case GET_CLASSES:
				File classDirectory = FrameworkHandler.getClassDirectoryFile();

				for (File classFile : classDirectory.listFiles()) {
					// skip known_classes file
					if (classFile.getName().contains("known_classes"))
						continue;
					
					objectOutput.writeObject(classFile.getName());  // name file
					dataOutput.writeLong(classFile.length());		// length file
					System.out.println("sending " + classFile.getName() + " numBytes = " + classFile.length());
					
					FileInputStream in = new FileInputStream(classFile);
					byte[] buffer = new byte[socket.getSendBufferSize()];
					int bytesRead = 0;
					
					while ((bytesRead = in.read(buffer)) > 0 )
					{
						socket.getOutputStream().write(buffer, 0, bytesRead);
					}
					in.close();
					
					// wait until file was received. Receive an integer (arbitrary) as the received response
					dataInput.readInt(); 
				}
				break;
		}
		// GET_DATA_IDS , GET_DATA are the only two actions that have a viewable response
		if (action < GET_CLASSES && FrameworkHandler.DEBUG_MODE)
			System.out.println("Response:" + response);
		
		return response;
	}
}
