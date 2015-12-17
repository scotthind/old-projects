/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rowan.pathfinder.networking.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.rowan.pathfinder.display.Director;
import org.rowan.pathfinder.networking.server.EventMessage;

/**
 *
 * @author Jon Schuff
 * @version 1.0
 * 
 * Client-side implementation of the ClientTcpConnectionHandler. When we first open the line, we will request
 * all events in the table we are interested in. Then, we will listen patiently on this dedicated thread for any
 * new events to be pushed down the line to us (the server-side will automatically update us with new events when
 * they are posted to the database).
 */
public class ClientTcpConnectionHandler extends Thread {

    public static ClientTcpConnectionHandler instance;
    private Socket socket;
    private ObjectInputStream inObjStream;
    private ObjectOutputStream outObjStream;
    private String tableName;
    private boolean stop;

    private Director director;
    private JFrame frame;
    public volatile EventMessage eMessage;

    public synchronized static ClientTcpConnectionHandler getInstance(Director director, String ipAddress, int port, String table) {
        if (instance == null) {
            try {
                instance = new ClientTcpConnectionHandler(director, ipAddress, port, table);
            } catch (Exception e) {
                instance = null;
                JOptionPane.showMessageDialog(director.getFrame(),
                        "Connecting to server failed!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return instance;

    }

    protected ClientTcpConnectionHandler(Director director, String ipAddress, int port, String table) throws SocketException, IOException {
            this.director = director;
            this.frame = director.getFrame();
            this.socket = new Socket();
            this.socket.setSoTimeout(1000);
            this.tableName = table;
            this.stop = false;
            this.socket.connect(new InetSocketAddress(ipAddress, port), 3000);
          //  System.out.println("Socket is " + socket.isConnected());

            outObjStream = new ObjectOutputStream(socket.getOutputStream());
        //    System.out.println("outObjStream is connected");
            inObjStream = new ObjectInputStream(socket.getInputStream());
         //   System.out.println("inObjStream is connected");

            //initConnectionToDatabase(tableName);
    }

   
    /* This is called once when we create the initial connection to the database. The first time we connect, we
     * will request all of the events in the table of the database that we are interested in. From that point on,
     * any other events that enter the database will also be automagically sent down to us from the client-side,
     * removing the need to constantly query for changes on the database. It is a pseudo-observation setup.
     */
    public void initConnectionToDatabase(String tableName) throws IOException {       
        try {
        //    System.out.println("ATTEMPTING TO SEND EVENTMESSAGE");
            outObjStream.writeObject(new EventMessage(tableName, null)); //send our request
         //   System.out.println("ATTEMPING TO FLUSH EVENTMESSAGE DOWN PIPE");
            outObjStream.flush();
         //   System.out.println("Initial message sent");
        } catch (SocketException ex) {
            JOptionPane.showMessageDialog(frame, "Connection to sever has been lost. Please reconnecto to server.", "Server Connection Lost", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * This run function is the meat of our thread, allowing the client to have a thread that immediately grabs
     * the events from the table of events it is working with, and then waits to listen for any updates sent down the
     * line after the initial database request. This guarantees that we will have an up-to-date list of events for our
     * pathfinder algorithms to work with.
     */
    @Override
    public void run() {

        EventMessage message = null;

        while (!stop) {
            try {
                //listen for events
                //System.out.println("Client " + this + " waiting for a message.");

                message = (EventMessage) inObjStream.readObject();
              //  System.out.println("Message received!!!! " + message);
                message.getEvent().convertListToPoly();
                director.addNewEventToQueue(message.getEvent());
                if (director.shouldShowAlert() == true) {
                    director.getGui().showAlert();
                }



            } catch (SocketTimeoutException e) {
                //System.out.println("SocketTimeoutException Thrown");
//                if (message != null) {//we have received an EventMessage
//                    //Send event to where it needs to go
//                    System.out.println("Message received!!!! " + message);
//                    message.getEvent().convertListToPoly();
//                    director.drawNewEvent(message.getEvent());
//                    director.getGui().showAlert();
//                    message = null;
//                }

                if (this.eMessage != null) {
                    reportEventToServer();
                    this.eMessage = null;
                    this.run();
                } else {
                    //System.err.println("Unexpected error: " + e);
                }

            } catch (Exception e) {//TODO-Catch only the single exception
                try {
                    if (inObjStream != null) {
                        inObjStream.close();
                    }
                    if (outObjStream != null) {
                        outObjStream.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ex) {
                    //Logger.getLogger(ClientTcpConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void reportEventToServer() {
        try {
       //     System.out.println("ATTEMPTING TO SEND EVENTMESSAGE");
            outObjStream.writeObject(eMessage); //send our request
      //      System.out.println("ATTEMPING TO FLUSH EVENTMESSAGE DOWN PIPE");
            outObjStream.flush();
         //   System.out.println("Report message sent");
        } catch (IOException ex) {
            
        }

    }

    public boolean isConnected() {
        return socket.isConnected();
    }
    
    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
