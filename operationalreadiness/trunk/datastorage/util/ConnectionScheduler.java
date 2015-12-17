package datastorage.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import util.FrameworkHandler;
import util.connections.DataForwarder;
import util.connections.DataReceiver;
import util.connections.DataRequester;

/**
 * Listens on a specified port and spawns new connections
 * 
 *  listenType 		|		connections
 *  ------------------------------------- 
 *  DATA_PLUGIN		|		DataReceiver
 *  DATA_FETCH		|		DataRequester
 *  DATA_FORWARD	|		DataForwarder		
 *  
 *  
 * @author Tom Renn
 */
public class ConnectionScheduler implements Runnable{

	public static final int DATA_PLUGIN  = 0;
	public static final int DATA_FETCH   = 1;
	public static final int DATA_FORWARD = 2;
	
	private int type;
	private ServerSocket listeningSocket;
	private DataStorageManager dataManager;
	
	private List<Thread> connections;
	
	/**
	 * Create a new connection scheduler
	 * @param listenType This determines how the scheduler will handle new connections
	 * @param dataManager Associated DataStorageManager connections may utilize
	 * @param port Which port the scheduler should listen for new connections
	 * @throws IOException If a ServerSocket cannot be created on the specified port
	 */
	public ConnectionScheduler(int listenType, DataStorageManager dataManager, int port) throws IOException{
		type = listenType;
		listeningSocket = new ServerSocket(port);
		this.dataManager = dataManager;
		connections = new LinkedList<Thread>();
	}

	/**
	 * Creates and starts a thread to hold the new connection based on the listening type
	 * 
	 * @param socket the new connection
	 * @throws Exception
	 */
	public void createNewConnection(Socket socket) throws Exception{
		Thread connection = null;
		
		switch (type){
	
			case DATA_PLUGIN:
				connection = new Thread(new DataReceiver(socket, dataManager, true));
				break;
			case DATA_FETCH:
				connection = new Thread(new DataRequester(socket, dataManager));
				break;
			case DATA_FORWARD:
				DataForwarder dataForwarder = new DataForwarder(socket);
				connection = dataForwarder;
				dataManager.addDataForwarder(dataForwarder);
				break;
		}
		
		connections.add(connection);
		connection.start();
	}
	
	/**
	 * Executes the scheduler
	 * 
	 * This will listen indefinitely until a new connection arrives and then put that connection into a 
	 * different thread to handle its requests
	 */
	@Override
	public void run() {
		if (FrameworkHandler.DEBUG_MODE){
			String strType = "";
			if (type == DATA_FETCH) 	strType = "FETCH SERVER   ";
			if (type == DATA_FORWARD)	strType = "FORWARD SERVER ";
			if (type == DATA_PLUGIN) 	strType = "DATA SERVER    ";

			System.out.println(strType +  " listening on port: " + listeningSocket.getLocalPort());
		}
		try {
			while (true) {
				Socket newSocket = listeningSocket.accept();
				
				if (FrameworkHandler.DEBUG_MODE){
					System.out.println();
					System.out.println("New request obtained on " + newSocket.getPort() + "|" + newSocket.getLocalPort());
				}
				createNewConnection(newSocket);
			}
		} catch (Exception e) {
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
	}
}
