package org.rowan.pathfinder.networking.server;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Jon Schuff
 *
 * This DbHandler will act as a singleton object, used to implement the observer
 * pattern on our database. It will store a HashMap of database tables as keys,
 * that hold the TcpConnectionThread that is currently "observing" that table.
 * Once a post is received by the DbHandler, it will look into the observerMap
 * and forward the newly posted Event to all threads that are observing that
 * table. This allows for a simple implementation that will keep all clients
 * up-to-date with their list of events.
 */
public class DbHandler {

    //This map stores a mapping of which threads are observing which database,
    //that way they will be updated when new Events are posted.
    private HashMap<String, ArrayList<TCPConnectionThread>> observerMap;
    private static DbHandler handler;

    private DbHandler() {

        observerMap = new HashMap<String, ArrayList<TCPConnectionThread>>();
        this.handler = this;

    }

    //We only want one instance of this bad boy operating so that we can guarantee
    //consistency with the observerMap and threads.
    public static synchronized DbHandler getInstance() {

        if (DbHandler.handler == null) {
            return (DbHandler.handler = new DbHandler());
        }
        return DbHandler.handler;

    }

    //This method gets called when a new thread hits the database for the first time, and stores
    //that thread as an observer of that database to monitor for EventMessages coming in.
    public synchronized void addToDatabaseObservers(String dbName, TCPConnectionThread observingThread) {

        if (observerMap.containsKey(dbName)) {
            observerMap.get(dbName).add(observingThread);
        } else {
            ArrayList<TCPConnectionThread> obsList = new ArrayList<TCPConnectionThread>();
            obsList.add(observingThread);
            observerMap.put(dbName, obsList);
        }
    }

    //This method is passed an EventMessage to be broadcasted to all observing threads, as well as the posting
    //thread so that we don't rebroadcast the EventMessage back to the client that reported it in the first place.
    public synchronized void sendNewEventMessageToObservers(EventMessage message, TCPConnectionThread postingThread) {

        //System.out.println(message);
        //System.out.println(message.getTableName());
        //System.out.println(observerMap.get(message.getTableName()));


        if (observerMap.get(message.getTableName()) != null) {
            for (TCPConnectionThread t : observerMap.get(message.getTableName())) {
                if (!t.equals(postingThread)) {
                    //System.out.println("Server: sending message down to observer!!!");
                    t.eMessage = message; //set the main memory variable to the message
                    t.interrupt();//interrupt the thread, causing it to see if there is a new message
                }
            }
        }
    }

    //Good programming practice, no cloning a singleton!!!!
    public Object clone()
            throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
        // this is a singleton, give it up already
    }
}
