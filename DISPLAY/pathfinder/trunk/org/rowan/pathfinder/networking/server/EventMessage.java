package org.rowan.pathfinder.networking.server;

import java.io.Serializable;
import org.rowan.pathfinder.pathfinder.Event;

/**
 *
 * @author Jon Schuff
 * @version 1.0
 * 
 * This EventMessage class will be the blueprint for ALL objects that go over the network.
 * 
 * We have two scenarios for EventMessage objects being sent to the server
 * 
 * Case 0: event == null, this means that we are requesting all events from the DB, tableName,
 *                           and all events will be returned in their own EventMessages.
 * 
 * Case 1: event != null, this means that we are POSTing an event to the database- since we are operating over a
 *                           TCP connection, we will assume that all information is received, for simplicity.
 * 
 * We have one scenario for EventMessage objects being received by a client
 * 
 * Case 0: event != null, this is an event that we have not yet received, so add it to the list of events.
 * 
 */
public class EventMessage implements Serializable{
    
    private String tableName;
    private Event event;
    
    public EventMessage(String name, Event evt){
        tableName = name;
        event = evt;
    }
    
    public EventMessage(String name){
        tableName = name;
        event = null;
    }
    
    public String getTableName(){
        return tableName;
    }
    
    public Event getEvent(){
        return event;
    }
}
