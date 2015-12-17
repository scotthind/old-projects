package org.rowan.pathfinder.networking.server;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.*;

/**
 *
 * @author nitro
 * @version 1.0
 *
 * @desc This class will be the TCP connection threads that listen in for client to server communication,
 *          and handle it accordingly. This class will be able to interact with a synchronized DbHandler.
 *
 * @DatabaseDesign
 *      For the proof of concept, we are using one table in the database, named "event_messages", that
 *      represents the events for a single regional area on the map. With how the querying functionality is
 *      designed, this can easily be expanded to multiple tables and databases with ease through JDBC.
 *
 *     create database events; //creates our events database
 *     select events; //selects events to be our main DB
 *
 *  **The following is our creation command for the event_messages table.
 *
 *     create table event_messages(
 *          id INT AUTO_INCREMENT,
 *          event_message BLOB,
 *          primary key (id));
 *
 */
public class TCPConnectionThread extends Thread {

    private static final String DEFAULT_DBNAME = "events";
    private static final String DEFAULT_UNAME = "root";
    private static final String DEFAULT_PWORD = "rowan1";
    private static final String DEFAULT_CONFIGFILENAME = "EventDatabaseConfig.txt";
    private Socket socket;
    private DbHandler dbHandler;
    private ObjectInputStream inObjStream;
    private ObjectOutputStream outObjStream;
    public volatile EventMessage eMessage;
    //Previously prepared statement is like a statement with variables that you can change on the fly
    //assignments to change it on statement.XXX
    //actual structure
    //SELECT ? from ? VALUES (ourStr, ourObj)
    //static final String GET_ALL_EVENTS = "SELECT ? FROM ? VALUES (?, ?)";
    static final String GET_ALL_EVENTS = "SELECT ? FROM ?;";

    public static void main(String[] args) {
        TcpRequestHandler server = new TcpRequestHandler();
        server.run();

    }

    public TCPConnectionThread(Socket socket, DbHandler dbHandler, boolean firstTime) {
        super("ServerThread");
        if (firstTime == true) {
            try {
                //attempt to connect at start up.
                Connection conn = getConnection();
            } catch (Exception ex) {
                System.err.println("Fatal Error: Could not connect to database.");
                System.exit(-1);
            }
        } else {
            
            try {
                
                this.socket = socket;
                socket.setSoTimeout(1000);
                this.dbHandler = dbHandler;
                
                inObjStream = new ObjectInputStream(socket.getInputStream());
                outObjStream = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                //Logger.getLogger(TCPConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //This method is designed to get a connection to the Database
    public Connection getConnection() throws Exception {
      
        String dbname = DEFAULT_DBNAME;
        String uname = DEFAULT_UNAME;
        String pword = DEFAULT_PWORD;
        
        File configFile = new File(DEFAULT_CONFIGFILENAME);
        if (!(configFile.exists())) {
            File defaultFile = new File(DEFAULT_CONFIGFILENAME);
            try {
                defaultFile.createNewFile();
            } catch (IOException ex1) {
                System.err.println("error creating event database config file");
            }
            try {
                PrintWriter writer = new PrintWriter(defaultFile);
                writer.println("This file is used to enter the appropriate database"
                        + " configuration properties for PathFinder Event Database Handler."
                        + " Enter the appropriate properties as shown below:");
                writer.println("");

                writer.println("databasename=events");
                writer.println("username=root");
                writer.println("password=rowan1");
                writer.close();

            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
        } else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(DEFAULT_CONFIGFILENAME));
                String str;
                try {
                    while ((str = in.readLine()) != null) {
                        if (str.contains("databasename= ")) {
                            dbname = str.replaceAll("databasename= ", "");
                        } else if (str.contains("databasename=")) {
                            dbname = str.replaceAll("databasename=", "");
                        }
                        if (str.contains("username= ")) {
                            uname = str.replaceAll("username= ", "");
                        } else if (str.contains("username=")) {
                            uname = str.replaceAll("username=", "");
                        }              
                        if (str.contains("password= ")) {
                            pword = str.replaceAll("password= ", "");
                        } else if (str.contains("password=")) {
                            pword = str.replaceAll("password=", "");
                        }
                    }
                    in.close();
                } catch (IOException ex) {
                    //
                }
            } catch (FileNotFoundException ex) {
                // System.err.println("ConfigStartStop file not found");
            }
        }

        //String driver = "org.gjt.mm.mysql.Driver";
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost/" + dbname;
        String username = uname;
        String password = pword;
        Class.forName(driver);
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            return conn;
        } catch (SQLException ex) {
            System.err.println("Fatal Error: can not connect to the MYSQL server.\n" +
                    "Message returned was: " + ex.getMessage() + "\nThe error state was: " + ex.getSQLState());
            System.exit(-1);
        }
        
        return null;
    }

    //This method will write an event to the Database, this should be called after the DBHandler is made
    //aware of the event and what table it is going into. We may need to store the id
    //"INSERT INTO ?(?) VALUES (?, ?)"
    public void addEventMessageToDatabase(EventMessage message) {
        PreparedStatement statement = null;
        Connection conn = null;
        try {

            dbHandler.sendNewEventMessageToObservers(message, this);//report new event to dbHandler

            conn = getConnection(); //get connection

            //static final String ADD_EVENT_TO_DB = "INSERT INTO ?(event_message) VALUES (?);";

            String add_event_to_db = "INSERT INTO " + message.getTableName()
                    + "(event_message) VALUES (?);";

            statement = conn.prepareStatement(add_event_to_db); //begin statement

            // set input parameters
            statement.setObject(1, message);//set tableName to be the table
            //System.out.println(statement);
            //System.out.println(message);
            statement.executeUpdate(); //post to database

            statement.close();//close statement
            conn.close(); //close connection to db

            System.out.println("Added eMessage to database : " + message);
        } catch (MySQLSyntaxErrorException mysqlEX) {
           
                System.out.println("Creating this table");
                //This means we hit a syntax error from the table not existing.. so we create it
                //very primitive functionality****
                createTableInDatabase(message.getTableName());
                addEventMessageToDatabase(message);
            
        }catch (Exception ex) {
            //
        }
    }

    //This method will grab all events from the database, close the database connection, and
    //then push all of the events down to the connected client
    //SELECT ? from ? VALUES (objColumn, tableName)
    //Notice this will add us as an observer to a table that doesn't exist- this is allowed ONLY
    //because the program will create a new table with that name if it doesnt already exist
    public void getAllEventMessages(String tableName) {
        try {

            System.out.println("getAllEvents in TcpConnThread tableName:" + tableName + ", threadRef: " + this);
            dbHandler.addToDatabaseObservers(tableName, this);

            Connection conn = getConnection();

            //PreparedStatement statement = conn.prepareStatement(GET_ALL_EVENTS);

            //statement.setString(1, "event_message");
            //statement.setString(2, tableName);

            PreparedStatement statement = conn.prepareStatement("SELECT event_message FROM " + tableName);
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();

            InputStream is;
            ObjectInputStream oip;
            EventMessage eMessage;
            
            if(rs.next()){//make sure it isnt an empty table- D'OH!
                
                do {
                    is = rs.getBlob(1).getBinaryStream();
                    oip = new ObjectInputStream(is);
                    eMessage = (EventMessage)oip.readObject();
                    System.out.println("Sending EventMessage: "+eMessage + " to client.");
                    sendEventMessageToClient(eMessage);
                } while (rs.next());

            }
            else
                System.out.println("Looks like there's nothing here..");
            rs.close();
            statement.close();
            conn.close();//close connection as soon as the query is finished
            //System.out.println("Reached end of method 'getAllEventMessages()'");
        } catch (MySQLSyntaxErrorException mysqlEX) {
            System.out.println("Creating this table");
            //This means we hit a syntax error from the table not existing.. so we create it
            //very primitive functionality****
            createTableInDatabase(tableName);
            //mysqlEX.printStackTrace();
            
        }catch (Exception ex) {
            System.out.println("Error in getAllEvents method:");
            ex.printStackTrace();
        }
    }

    //This method is invoked to send an EventMessage from the dbHandler, straight down to the client, used
    //for when the table this thread is watching gets updated.
    private void sendEventMessageToClient(EventMessage message) {
        try {
            outObjStream.writeObject(message);
            outObjStream.flush();
        } catch (IOException ex) {
            //
        }
    }

    @Override
    public void run() {

        EventMessage message = null;


        boolean stop = false;

        //BEGIN CONNECTION HANDLING MAIN FUNCTIONALITY
        while (!stop) {

            try {
                //System.out.println("SERVER: waiting for incoming message");

                message = (EventMessage) inObjStream.readObject();

            } catch (SocketTimeoutException e) {
                //System.out.println("SocketTimeoutException Thrown");
                if (message != null) {//we have received an EventMessage
                    if (message.getEvent() == null) {//GET Request
                        System.out.println("ABOUT TO GET ALL MESSAGES FROM TABLE: " + message.getTableName());

                        getAllEventMessages(message.getTableName());
                    } else { //POST request, we have received an event
                        //System.out.println("POSTING RECEIVED MESSAGE TO DB");
                        addEventMessageToDatabase(message);
                    }
                    message = null;
                }

                if (this.eMessage != null) {
                    sendEventMessageToClient(eMessage);
                    //System.out.println("QQQQQQQ SENT TO CLIENT");
                    this.eMessage = null;
                    this.run();
                }

            } catch (Exception e) { //TODO- Make specific exception
                try {
                    inObjStream.close();
                    outObjStream.close();
                    socket.close();
                } catch (IOException ex) {
                    //Logger.getLogger(TCPConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
                }


            }
        }

    }//End run method
    
    private void createTableInDatabase(String tableName){
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String sqlStatement = "CREATE TABLE " + tableName + "(event_message BLOB)";
            
            stmt.executeUpdate(sqlStatement);
            System.out.println("Table " + tableName + " created successfully");
        } catch (Exception ex) {
            //
        } 
    }
}
