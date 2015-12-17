package util.connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import util.DataReceivedListener;
import util.FrameworkHandler;

/**
 * DataReceiver is used to handle a single connection for receiving information.
 *
 * 	An integer is to be read and used to determine which request/action needs to be taken.
 * 	The requests that can be performed are as given:
 * 		RECEIVE_CLASS: read a class file from the connection stream
 * 		RECEIVE_DATA : read a data object from the connection stream
 * 	Once received, a DataRecievedListener (e.g. the DataStorageManager) is notified of the new information
 * 
 * @author Tom Renn
 */
public class DataReceiver implements Runnable{
	// incoming requests
	public static final int RECEIVE_CLASS = 0;
	public static final int RECEIVE_DATA  = 1;
	
	// response codes
	public static final int RECEIVED_OK = 0;
	public static final int RECEIVED_ERROR = 1;
	private boolean reportFeedback;
	
	private Socket socket;
	private ObjectInputStream objectInput;
	private DataInputStream dataInput;
	private ObjectOutputStream objectOutput;
	private DataOutputStream dataOutput;
	
	private DataReceivedListener dataReceviedListener;
	private boolean running;
	
	/**
	 * Basic Constructor. Takes a socket connection, DataReceivedListener, and a boolean reportFeedback which determines if the receiver should send response codes.
	 * @param socket
	 * @param dataManager
	 * @param reportFeedback
	 * @throws IOException
	 */
	public DataReceiver(Socket socket, DataReceivedListener dataManager, boolean reportFeedback) throws IOException{
		this.socket = socket;
		this.dataReceviedListener = dataManager;
		this.reportFeedback = reportFeedback;
		this.running = true;
		
		objectOutput = new ObjectOutputStream(socket.getOutputStream());
		objectInput = new ObjectInputStream(socket.getInputStream());
		dataInput = new DataInputStream(socket.getInputStream());
		dataOutput = new DataOutputStream(socket.getOutputStream());
	}
	
	/**
	 * Executes the main function of the DataReceiver.
	 * 
	 * Reads an incoming request/action and then performs it.
	 * If reportFeedback is true:
	 * 		Respond with RECEIVED_OK or RECEIVED_ERROR and the error if there is one 
	 */
	@Override
	public void run() {
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("Data reciever spawned");
		
		while (running) {
			
			try {
				int action = dataInput.readInt();
				Serializable error = perform(action);
				
				if (reportFeedback) {
	 				if ( error == null ) { 
						dataOutput.writeInt(RECEIVED_OK);
					}
					else {
						dataOutput.writeInt(RECEIVED_ERROR);
						objectOutput.writeObject(error);
					}
				}
			} catch (Exception e) {
				FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
				break; // thread needs to stop running
			}
		}
	}
	
	/**
	 * Performs the given action.
	 * 
	 * @param action What needs to be done
	 * @return Error if there is one
	 * @throws ClassNotFoundException if a received object Class/Type doesn't exist on the system
	 * @throws IOException if there is a connection issues
	 */
	public Serializable perform(int action) throws ClassNotFoundException, IOException{
		
		Serializable dataError = null;

		switch (action) {
			case RECEIVE_CLASS:

				// read name and number of bytes of the file
				String filename = (String)objectInput.readObject();
				long numOfBytesInFile = dataInput.readLong();
				
				if (FrameworkHandler.DEBUG_MODE){
					System.out.println();
					System.out.printf("-- Receiving file : %s - %d bytes\n", filename, numOfBytesInFile);
				}

				// create new file and output stream
				File newFile = new File(FrameworkHandler.getDataTypesDirectory(), filename);
				FileOutputStream wr = new FileOutputStream(newFile);
				
				
				byte[] buffer = new byte[socket.getReceiveBufferSize()];
				int bytesReceived = 0;
				int totalBytesReceived = 0;
				// write to file until we've received all bytes
				while(totalBytesReceived < numOfBytesInFile && (bytesReceived = socket.getInputStream().read(buffer))>0)
				{
					wr.write(buffer,0,bytesReceived);
					totalBytesReceived += bytesReceived;
					System.out.println("bytes received: " + bytesReceived);
				}
				wr.close();
				
				// Tell the ClassLoader about the new java class
				FrameworkHandler.loadClassFile(newFile);
				dataReceviedListener.classReceived(newFile);
				break;
			case RECEIVE_DATA:
				
				Object obj = objectInput.readObject();
				dataError = dataReceviedListener.dataReceived((Serializable) obj);
				break;
		}
		
		return dataError;
	}
	
	public void endRun(){
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			if (FrameworkHandler.DEBUG_MODE){
				System.out.println("ERROR: Failed to close Data Storage connection");
				e.printStackTrace();
			}
		}
	}
	
}
