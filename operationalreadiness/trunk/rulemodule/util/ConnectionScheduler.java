package rulemodule.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


import util.FrameworkHandler;

/**
 * Listens on a specified port and spawns new connections
 * 
 * This scheduler is a modified version of the one from the datastorage package	
 * While the rule module should only need a single listening type, functionality has been kept
 * if different connection types are desired.
 *  
 * @author Tom Renn
 */
public class ConnectionScheduler implements Runnable{

	public static final int RULE_CONNECTION  = 0;
	
	private int type;
	private ServerSocket listeningSocket;
	private List<Thread> connections;
	private boolean running;
	
	/**
	 * Create a new connection scheduler
	 * @param listenType This determines how the scheduler will handle new connections
	 * @param port Which port the scheduler should listen for new connections
	 * @throws IOException If a ServerSocket cannot be created on the specified port
	 */
	public ConnectionScheduler(int listenType, int port) throws IOException{
		type = listenType;
		listeningSocket = new ServerSocket(port);
		connections = new LinkedList<Thread>();
		running = true;
	}

	/**
	 * Creates and starts a thread to hold the new connection based on the listening type
	 * 
	 * @param socket the new connection
	 * @throws IOException 
	 */
	public void createNewConnection(Socket socket) throws IOException{
		Thread connection = null;
		
		switch (type){
	
			case RULE_CONNECTION:
				connection = new Thread(new RemoteRuleHandler(socket));
				break;
		}
		
		connections.add(connection);
		connection.start();
	}
	
	/**
	 * Executes the scheduler
	 * 
	 * This will listen indefinitely until a new connection arrives and then put that connection into a 
	 * different thread to handle it's requests
	 */
	@Override
	public void run() {
		if (FrameworkHandler.DEBUG_MODE){
			String strType = "";
			if (type == RULE_CONNECTION) strType = "New RemoteRule Listener";
			System.out.println();
			System.out.println(strType +  " LISTENING on port: " + listeningSocket.getLocalPort());
		}
		try {
			while (running) {
				Socket newSocket = listeningSocket.accept();
				
				if (FrameworkHandler.DEBUG_MODE)
					System.out.println("New request obtained on " + newSocket.getPort() + "|" + newSocket.getLocalPort());
				
				createNewConnection(newSocket);
			}
		} catch (IOException e) {
			FrameworkHandler.exceptionLog(FrameworkHandler.LOG_ERROR, this, e);
		}
	}

	public void closeConnection() {
		running = false;
		try {
			listeningSocket.close();
		} catch (IOException e) {
			if (FrameworkHandler.DEBUG_MODE){
				System.out.println("ERROR: Failed to close Rule Connection Scheduler");
				e.printStackTrace();
			}
		}
	}
	
	
}
