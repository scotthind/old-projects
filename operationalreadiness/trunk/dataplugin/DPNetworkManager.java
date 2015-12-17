package dataplugin;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import dataplugin.util.DataTransmitter;
import dataplugin.util.ErrorListener;

import util.DataError;
import util.DataType;
import util.FrameworkHandler;


/**
 *
 * Handles sending new data to the DataStorage module. Will send the class of 
 * any new data types introduced. 
 * 
 * @author Remo Cocco
 * @author Dan Urbano
 * @author Kevin Desmond 
 * @author Tom Renn 
 * @version 0.1
 */

public class DPNetworkManager implements ErrorListener{
	private List<String> knownClassTypes;
	private List<ErrorListener> errorListeners;
	private HashMap<String, DataTransmitter> transmitters;
	private File knownClassFile;
	
	// constructor
	public DPNetworkManager() throws IOException {
		knownClassTypes = new ArrayList<String>();
		errorListeners  = new LinkedList<ErrorListener>();
		transmitters = new HashMap<String, DataTransmitter>();
		File classDirectory = FrameworkHandler.getClassDirectoryFile();
		knownClassFile = new File(classDirectory, "known_classes.txt");
		
		if (knownClassFile.exists()) {
			readKnownClasses();
		}
		else {
			knownClassFile.createNewFile();
		}
		// read classes we've already sent
	}
	
	// read known classes from file into knownClassTypes list
	private void readKnownClasses() {
		try {
			Scanner lineReader = new Scanner(knownClassFile);
			while (lineReader.hasNextLine()) {
				String line = lineReader.nextLine();
				if (FrameworkHandler.DEBUG_MODE)
					System.out.println("DataPlugin already has sent class : " + line);
				knownClassTypes.add(line);
			}
			lineReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	// write class to knownClassFile
	private void writeClassToFile(String className) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(knownClassFile, true));
			writer.write(className);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends data to the Data Storage Module. If there is no current transmitter for the given
	 * host and port one will be created. 
	 * 
	 * @param host, address of location of the Data Storage Module
	 * @param port, the port the Data Storage Module is listening on
	 * @param data, the DataType being sent 
	 * @return True if the host/port combination connected to something, false otherwise
	 */
    public boolean sendData(String host, int port, DataType data) {
    	try{

    		DataTransmitter transmitter = getDataTransmitter(host, port);

    		if ( needToSendClassFileFor(data) ) // new class - needs to be sent
    		{
    			File classFile = retrieveClassFileFor(data);
    			transmitter.addClass(classFile);
    			knownClassTypes.add(data.getClass().getSimpleName());
    			writeClassToFile(data.getClass().getSimpleName());
    		}
    		
    		transmitter.addData(data);
    		
    		// if waiting, awaken thread to send info
    		if (transmitter.getState() == Thread.State.WAITING){
    			synchronized (transmitter) {
    			transmitter.notify();
    			}
    		}
    		
    		return true;
         }
         catch(Exception e){
        	FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
        	if (FrameworkHandler.DEBUG_MODE) {
        		System.err.println("Unable to connect to " + host+":"+port);
        	}
        	return false;
         }
    }
    
    /**
     * Get the data transmitter to use for a particular host:port. If one already exists, simply return it. Otherwise a new one is created
     * @param host
     * @param port
     * @return a DataTransmitter to send data over
     * @throws UnknownHostException
     * @throws IOException
     */
    private DataTransmitter getDataTransmitter(String host, int port) throws UnknownHostException, IOException {
		String key = createKey(host, port);
		DataTransmitter transmitter = null;
		
		if (transmitters.containsKey(key)) {
			transmitter = transmitters.get(key);
			
			// if the thread has somehow died, create and store a new one
			if (transmitter.getState() == Thread.State.TERMINATED) {
				transmitter = new DataTransmitter(new Socket(host, port), this);
				transmitter.start();
				transmitters.put(key, transmitter);
			}
		}
		else {
			Socket s = new Socket(host, port);
			transmitter = new DataTransmitter(s, this);
			transmitter.start();
			transmitters.put(key, transmitter);
		}
		return transmitter;
    }
    
    /**
     * Returns true if class file has not yet been sent to the receiving connection
     * @param data
     * @return
     */
    private boolean needToSendClassFileFor(Object data){
    	return !knownClassTypes.contains(data.getClass().getSimpleName());
    }
    
    /**
     * Obtain the class file for a given object
     * @param data
     * @return datas class file
     */
    private File retrieveClassFileFor(Object data) {
    	Class<?> classType = data.getClass();
		String className = classType.getSimpleName();
		URL location = classType.getResource(className + ".class");
		
		if (!"file".equalsIgnoreCase(location.getProtocol()))
			throw new IllegalStateException("Unable to locate class file for given data");
		
		return new File(location.getPath());
    }
    
    
    void registerErrorListener(ErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    void unregisterErrorListener(ErrorListener errorListener) {
    	errorListeners.remove(errorListener);
    }

    List<ErrorListener> getErrorListeners() {
        return errorListeners;
    }
    
    /**
     * Notify all the data listeners of the received error
     */
	@Override
	public void didReceiveError(DataError error) {
		for (ErrorListener errorListener : errorListeners) {
			errorListener.didReceiveError(error);
		}
	}
	
	// creates a string representing of host:port
	private String createKey(String host, int port){ return host + ":" + port; }
}
