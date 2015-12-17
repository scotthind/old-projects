package userinterface.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

import util.DataType;
import util.FrameworkHandler;


/**
 * The DataRequester class sends requests for Data, DataIDs, and class files to the DataStorage Module
 * and retrieves them
 * @author Kevin Desmond
 */
public class DataRequester extends Thread {

	private final int END_OF_FILE = 0;
	private final int FETCH_DATA_IDS = 0; 
	private final int FETCH_DATA   = 1;
	private final int FETCH_CLASS = 2;
	
	private Queue<LinkedList<Object>> queue;	
	Socket socket;
		
	DataIDReceivedListener idListener;
	DataOutputStream dataOutput;
	ObjectOutputStream objectOutput;
	ObjectInputStream objectInput;
	DataInputStream dataInput;
	private boolean running;
	
	/**
	 * Constructs a DataRequester with the given host and port
	 * @param host
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public DataRequester(String host, int port, DataIDReceivedListener idListener) throws UnknownHostException, IOException{
		socket = new Socket(host, port); //assign this to the host and port
		queue = new LinkedList<LinkedList<Object>>();
		
		OutputStream os = socket.getOutputStream();
	    dataOutput = new DataOutputStream(os);
	    objectOutput = new ObjectOutputStream(os);
	    
	    InputStream is = socket.getInputStream();
	    objectInput = new ObjectInputStream(is);
	    dataInput = new DataInputStream(is);
		
	    this.idListener = idListener;
	    
	    running = true;
	    
		start();
	}
	
	/**
	 * add a list to the queue containing the next function to fire
	 * @param items
	 */
	public void addToQueue(LinkedList<Object> items){
		queue.add(items);
	}
	
	/**
	 * run the thread
	 */
	public void run(){
		//always run
		while (running){
			//wait for a list/command to be placed in the queue
			if(queue.isEmpty()){
				synchronized(this){
					System.out.println("Data requester waiting.....");
					try {
						this.wait();
						System.out.println("Data requester stopped waiting");
					} catch (InterruptedException e) {
						FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
					}	
				}
			}	
			else {
				//get the list
				LinkedList<Object> nextItem = (LinkedList<Object>) queue.poll();
				
				//first item in the list will be an integer corresponding to the function being called
				int i = (Integer) nextItem.poll();
	
				if (i == FETCH_DATA_IDS){ 
					 fetchAvailableDataIDs(); 
				}
				else if (i == FETCH_DATA){
					String dataID = (String) nextItem.poll();
					Serializable constraint = (Serializable) nextItem.poll();
					fetchData(dataID, constraint);
				}
				else if(i == FETCH_CLASS){
					fetchClassFiles();
				}
			
			}
		}

	}

	/**
	 * fetch class files from the Data Storage Manager
	 */
	private void fetchClassFiles() {
		
		try{	
		 
		 dataOutput.writeInt(FETCH_CLASS);
		 
		 boolean done = false;
		 
		 while(!done){
		 	 String filename = (String) objectInput.readObject();
		 	 
		 	 if(filename == null){
		 		 done = true;
		 	 }
		 	 else{
			 long numOfBytesInFile = dataInput.readLong();
			
			 if (FrameworkHandler.DEBUG_MODE){
				System.out.printf("DataRequester: Receiving file : %s - %d bytes\n", filename, numOfBytesInFile);
				System.out.println();
			 }
			
			 // create new file and output stream
			 File newFile = new File(FrameworkHandler.getClassDirectory() +"//" + filename);
			 FileOutputStream wr = new FileOutputStream(newFile);
			 byte[] buffer = new byte[socket.getReceiveBufferSize()];
			 
			 int bytesReceived = 0;
			 
			 int totalBytesReceived = 0;
			 // write to file until we've received all bytes
			 while(totalBytesReceived < numOfBytesInFile && (bytesReceived = socket.getInputStream().read(buffer)) > 0)
			 {
			     if(bytesReceived > 0) {
			    	 totalBytesReceived += bytesReceived;
			    	 wr.write(buffer,0,bytesReceived);
			    	 System.out.println("bytes received: " + bytesReceived + " bytes total: " + totalBytesReceived);
			     }
		     }
			
			 dataOutput.writeInt(END_OF_FILE); //notify data storage to start sending the next file
			 wr.close();
			 
			 idListener.classReceived(newFile); //let the UIManager know a class has been received
			 
			 FrameworkHandler.loadClassFile(newFile);
			 
		 	 }
		 	 	
		}
			
			
		 }catch (Exception e){
				FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		 }
		 
	}

	/**
	 * fetch a DataType 
	 * @param dataID
	 * @param constraint
	 */
	@SuppressWarnings("unchecked")
	private void fetchData(String dataID, Serializable constraint) { 
		List<DataType> dataTypes = null;
	        
	        try{
		    
	        	dataOutput.writeInt(FETCH_DATA);
	        	objectOutput.writeObject(dataID);
	        	objectOutput.writeObject(constraint);	
	        	
	        	dataTypes = (List<DataType>) objectInput.readObject();
	        	
	        
	        }catch(Exception e){
	        	FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
	        }
	        
	        for(DataType dt: dataTypes){
	        	idListener.dataReceived(dt);
	        }
	}

	/**
	 * Fetch the IDs of the available Data
	 * @return availableDataIDs, the dataIDs of all the DataTypes in Data Storage
	 */
	@SuppressWarnings("unchecked")
	private void fetchAvailableDataIDs() {
		List<String> availableDataIDs = null;	
        try{
 
		    dataOutput.writeInt(FETCH_DATA_IDS);
		    
		    availableDataIDs = (List<String>) objectInput.readObject();  
    		
    	} catch(Exception e){
    		FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
    	}
         
    	idListener.receiveDataIDs(availableDataIDs);
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
