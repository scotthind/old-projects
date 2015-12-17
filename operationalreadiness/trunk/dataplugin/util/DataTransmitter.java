package dataplugin.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import util.DataError;
import util.FrameworkHandler;
import util.connections.DataReceiver;



/**
 * Data Transmitter
 * -
 * Used to send out new data to a connection. 
 * 
 * @author Tom Renn
 */
public class DataTransmitter extends Thread{
	private Socket socket;
	private ObjectOutputStream objectOutput;
	private ObjectInputStream objectInput;
	private DataOutputStream dataOutput;
	private DataInputStream dataInput;  
	
	private Queue<Object> dataQueue;
	private Queue<File> classQueue;
	
	private ErrorListener errorBroadcaster;
	
	
	/**
	 * Creates a new data forwarder.
	 * 
	 * @param socket Connection data will be forwarded on
	 * @throws IOException Thrown if there are problems with the connection
	 */
	public DataTransmitter(Socket socket, ErrorListener errorBroadcaster) throws IOException{
		this.socket = socket;
		this.errorBroadcaster = errorBroadcaster;
		dataQueue = new LinkedList<Object>();
		classQueue = new LinkedList<File>();
		
		objectOutput = new ObjectOutputStream(socket.getOutputStream());
		objectInput = new ObjectInputStream(socket.getInputStream());
		dataOutput = new DataOutputStream(socket.getOutputStream());
		dataInput = new DataInputStream(socket.getInputStream());
	}
	
	/**
	 * Queue data that needs to be forwarded
	 * 
	 * @param data
	 */
	public void addData(Serializable data){
		dataQueue.add(data);
	}
	
	/**
	 * Add a class that needs to be forwarded
	 * 
	 * @param file File instance of the .class file
	 */
	public void addClass(File file){
		classQueue.add(file);
	}
	
	
	/**
	 * Send data over the connection or begin waiting. Once data is added, notify() must be called on this thread
	 */
	@Override
	public void run() {
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("DataTransmitter spawned");
		FrameworkHandler.log(FrameworkHandler.LOG_INFO, this, "New DataTransmitter Connection started");
		
		while (true) {
			if ( dataQueue.isEmpty() && classQueue.isEmpty()) {
				synchronized (this) {
					try {
						if (FrameworkHandler.DEBUG_MODE) {
							System.out.println("Data Transmitter thread is now waiting...");
						}
						this.wait();
					} catch (InterruptedException e) {
						
						FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
					}
				}
			}
			
			try {
				// send classes first
				if (classQueue.isEmpty() == false) {
					File file = classQueue.poll();
					sendClass(file);
				}
				else {
					// send data only after class queue is empty
					if (dataQueue.isEmpty() == false) {
						Object data = dataQueue.poll();
						sendData(data);
					}
				}
				readResponse();
			}
			catch (Exception e){
				FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
				// connect reset or something happened, exit thread
				break;
			}
		}
	}
	
	
	/**
	 * Send data to the connection
	 * @param object the data to forward
	 * @throws IOException 
	 */
	public void sendData(Object object) throws IOException{
			
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("Sending new data : " + object);
		
		dataOutput.writeInt(DataReceiver.RECEIVE_DATA); // tell connection to receive data
		objectOutput.writeObject(object);
		
	}
	
	/**
	 * Send new class file
	 * @param File the class to forward
	 * @throws IOException 
	 */
	public void sendClass(File file) throws IOException{
		
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("Sending new class file : " + file.getName());
		
		// tell connection to receive a new class, the filename, and length of file
		dataOutput.writeInt(DataReceiver.RECEIVE_CLASS);
		objectOutput.writeObject(file.getName());
		dataOutput.writeLong(file.length());
		
		// write file over socket
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[socket.getSendBufferSize()];
		int bytesRead = 0;
		
		while ((bytesRead = in.read(buffer)) > 0 )
		{
			socket.getOutputStream().write(buffer, 0, bytesRead);
		}
		in.close();
	}
	
	/**
	 * Reads the response from DataReceiver and tells DPNetworkManager(errorBroadcaster) to alert listeners
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readResponse() throws IOException, ClassNotFoundException {
		int response = dataInput.readInt();
		
		if (response == DataReceiver.RECEIVED_ERROR){
			// must read addition error object
			DataError error = (DataError)objectInput.readObject();
			errorBroadcaster.didReceiveError(error);
		}
	}

}
