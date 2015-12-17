package util.connections;

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

import util.FrameworkHandler;


/**
 *
 * Used to push new information, always makes sure to forward class files first.
 * This thread will wait() if no information is to be forwarded and notify() must be called after information is added.
 *
 * @author Tom Renn
 */
public class DataForwarder extends Thread{
	private Socket socket;
	private ObjectOutputStream objectOutput;
	private ObjectInputStream objectInput;
	private DataOutputStream dataOutput;
	private DataInputStream dataInput;
	
	private Queue<Object> forwardData;
	private Queue<File> forwardClass;
	
	/**
	 * Create a new data forwarder
	 * 
	 * @param socket Connection the data will be forwarded to
	 * @throws IOException Thrown if there are problems with the connection
	 */
	public DataForwarder(Socket socket) throws IOException{
		this.socket = socket;
		forwardData = new LinkedList<Object>();
		forwardClass = new LinkedList<File>();
		
		objectOutput = new ObjectOutputStream(socket.getOutputStream());
		// while objectInput is not used, it offsets an ObjectOutputStream from a DataReceiver
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
		forwardData.add(data);
	}
	
	/**
	 * Add a class that needs to be forwarded
	 * 
	 * @param file File instance of the .class file
	 */
	public void addClass(File file){
		forwardClass.add(file);
	}
	
	
	/**
	 * Main execution of the DataForwarder thread
	 * TODO:
	 * The forwarder will continue to run forever TODO: add way to stop the thread if wanted  
	 * If there is nothing to forward, the thread will wait until it is notified 
	 * 
	 * Once notified it will send any queued classes first. Only once the class queue is empty
	 * will it will begin to forward the queued data
	 */
	@Override
	public void run() {
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("Data forwarder spawned");
		FrameworkHandler.log(FrameworkHandler.LOG_INFO, this, "New DataForwarder Connection started");
		
		while (true) {
			
			// cause the thread to wait() if queues are empty
			if ( forwardData.isEmpty() && forwardClass.isEmpty()) {
				synchronized (this) {
					try {
						if (FrameworkHandler.DEBUG_MODE) {
							System.out.println("Data forwarder waiting...");
						}
						this.wait();
					} catch (InterruptedException e) {
						FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
					}
				}
			}
			
			
			try {
				// forward class first
				if (forwardClass.isEmpty() == false) {
					File file = forwardClass.poll();
					forwardClass(file);
				}
				else {
					// forward data after class queue is empty
					if (forwardData.isEmpty() == false) {
						Object data = forwardData.poll();
						forwardData(data);
					}
				}
				
				// read basic response (fix bug where all classes are spit to output before they can be read)
				int read = dataInput.readInt();
				System.out.println("RECEIVED INTEGER BACK FROM FORWRADER - " + read);
			}
			catch (Exception e){
				FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
				// exit loop, error has occurred
				break;
			}
		}
	}
	
	
	/**
	 * Forward data to the connection
	 * @param object the data to forward
	 * @throws IOException 
	 */
	private void forwardData(Object object) throws IOException{
			
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("forwarding new data - " + object);
		
		dataOutput.writeInt(DataReceiver.RECEIVE_DATA);
		objectOutput.writeObject(object);
	}
	
	/**
	 * Forward new class file
	 * @param File the class to forward
	 * @throws IOException 
	 */
	private void forwardClass(File file) throws IOException{
		
		if (FrameworkHandler.DEBUG_MODE)
			System.out.println("forwarding new class file - " + file.getName());
		
		// tell connection to receive a new class, the filename, and length of file
		dataOutput.writeInt(DataReceiver.RECEIVE_CLASS);
		objectOutput.writeObject(file.getName());
		dataOutput.writeLong(file.length());
		
		// write file over socket
		FileInputStream fileStream = new FileInputStream(file);
		byte[] buffer = new byte[socket.getSendBufferSize()];
		int bytesRead = 0;
		
		while ((bytesRead = fileStream.read(buffer)) > 0 )
		{
			socket.getOutputStream().write(buffer, 0, bytesRead);
		}
		fileStream.close();
	}

}
