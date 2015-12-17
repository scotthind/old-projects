package org.rowan.pathfinder.networking.server;

import java.sql.SQLException;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.rowan.pathfinder.pathfinder.Event;

/**
 *
 * @author Jon Schuff
 * @version 1.0
 * 
 * @desc This class will be responsible for creating all TCP connections from incoming requests.
 */
public class TcpRequestHandler {

    private DbHandler dbHandler;

    public TcpRequestHandler(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public TcpRequestHandler() {
        this.dbHandler = DbHandler.getInstance();
    }

    public void run() {

        //seedDatabase();
        ServerSocket serverSocket = null;
        try {
            boolean listening = true;

            try {
                serverSocket = new ServerSocket(1338);
                
                //System.out.println("SERVERSOCKET IS BOUND =" + serverSocket.isBound());
            } catch (IOException e) {
                System.err.println("Fatal Error: Could not listen on port: 1338." + e);
                System.exit(-1);
            }
            
            TCPConnectionThread t = new TCPConnectionThread(null, dbHandler, true);
            try {
                t.getConnection();
            } catch (Exception ex) {
                //
            }

            while (listening) {
                //System.out.println("Listening on port" + serverSocket.getLocalSocketAddress());
                new TCPConnectionThread(serverSocket.accept(), dbHandler, false).start();
                //System.out.println("CONNECTION CREATED!");

            }

        } catch (IOException ex) {
            System.out.println("IOEXCEPTION WHEN CREATING TcpConnectionThread- You've done broke it.");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.err.println("ERROR: " + ex);
            }
        }
    }

    //This is a temporary, hardcoded
    public void seedDatabase() {

        System.out.println("SEEDING DATABASE");
        Connection conn = null;
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost/events_db";
            String username = "root";
            String password = "r3dm1n3!";
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);

            EventMessage message = new EventMessage(null, new Event(null, null, 0.0, null, null, false));

            //dbHandler.sendNewEventMessageToObservers(message, null);//report new event to dbHandler

            PreparedStatement statement = conn.prepareStatement("INSERT INTO events(event_message) VALUES (?);"); //begin statement

            // set input parameters
            //statement.setString(1, "events");//set tableName to be the table
            statement.setObject(1, message); //set EventMessage to be the message
            System.out.println(statement);
            statement.executeUpdate(); //post to database
            statement.close();//close statement
            conn.close(); //close connection to db

            System.out.println("Done serializing: " + message);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("DONE SEEDING DATABASE");


    }

}
