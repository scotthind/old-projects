package util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jodd.util.ClassLoaderUtil;


public class FrameworkHandler {
	
	private static FrameworkHandler singleton;
	
	private File directory;
	private static final String DIRECTORY = "\\logging";
	public static final String LOG_INFO  = "INFO ";
	public static final String LOG_ERROR = "ERROR";
	public static final String LOG_WARN  = "WARN ";

	private static final boolean LOGGING_ENABLED = true;
	public static final boolean DEBUG_MODE = true;
	
	// mini-cache
	private static File logFile = null;
	
	
	public static void log(String type, Object caller, String message) {
		FrameworkHandler handler = FrameworkHandler.getInstance();
		handler.writeLog(type, caller, message);
	}

	/**
	 * Special log for exceptions, will print stack trace if in debug mode
	 * 
	 * @param type Log type
	 * @param caller Object calling the logger
	 * @param e Exception to log
	 */
	public static void exceptionLog(String type, Object caller, Exception e){
		if (FrameworkHandler.DEBUG_MODE) {
			e.printStackTrace();
		}

		FrameworkHandler.log(type, caller, e.getMessage());
	}
	
	public static File getClassDirectoryFile() {
		File classpath = new File(FrameworkHandler.getClassDirectory());
		return classpath;
	}
	
	// create only a single instance
	public synchronized static FrameworkHandler getInstance() 
	{
		if (singleton == null) {
			singleton = new FrameworkHandler();
		}
		return singleton;
	}
	
	public FrameworkHandler() {
		// place framework directory in the programs running location
		directory = new File("");
	}
	
	/**
	 * Get the directory of the framework
	 * @return file representing framework directory
	 */
	public File getDirectory() {
		return directory;
	}
	
	/**
	 * Retrieves the file that logging information is written to
	 * @return Logging file
	 * @throws IOException
	 */
	private File getLoggingFile() throws IOException{
		if (logFile != null) // cached logFile
			return logFile;
	
		File logDirectory = new File(getDirectory().getAbsoluteFile() + DIRECTORY);
		if (logDirectory.exists() == false){
			logDirectory.mkdir();
		}
		
		File tempLogFile = new File(logDirectory, "logs.txt");
		if (tempLogFile.exists() == false)
			tempLogFile.createNewFile();
		
		logFile = tempLogFile;
		return tempLogFile;
	}
	
	public static void loadClassFile(File file) {
		try {
			// use Jodd ClassLoaderUtil to load class into the current ClassLoader
			ClassLoaderUtil.defineClass(getBytesFromFile(file));
			if (DEBUG_MODE) {
				System.out.println("Loaded Class : " + file.getName());
			}
		} catch (IOException e) {
			exceptionLog(LOG_ERROR, getInstance(), e);
		}
	}
	
	/**
	 * Write to a log file on the current running machine
	 * 
	 * @param type The log status, e.g. LOG_ERROR, LOG_INFO, LOG_WARN
	 * @param caller The object requesting the log. Use 'this' keyword
	 * @param message The reported string to be written
	 */
	@SuppressWarnings("unused")
	public synchronized void writeLog(String type, Object caller, String message){
		
		if (LOGGING_ENABLED == false) // don't do anything if logging is turned off
			return;
		
		String classOrigin = caller.getClass().getSimpleName();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String time = dateFormat.format(cal.getTime());
		
		try {
			File log = getLoggingFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(log, true));
			writer.append(type + " - " +  time + " - " + classOrigin  +" - " + message);
			writer.newLine();
			writer.close();
			
		} catch (IOException e) { // failed to write to file
			e.printStackTrace();
		}

	}
	

/**
 * Load the stored class files that may already exist on this system
 */
	public static void loadStoredClasses() {
		if (DEBUG_MODE){
			System.out.println("------- Loading local class files --------");
		}
		
		for (File classFile : new File(FrameworkHandler.getDataTypesDirectory()).listFiles()){
			// skip known_classes file
			if (classFile.getName().contains("known_classes") || classFile.getName().contains(".DS"))
				continue;
						
			if (DEBUG_MODE)
				System.out.println("Loading class file: " + classFile.getName());
			
			try {
				ClassLoaderUtil.defineClass(getBytesFromFile(classFile));
			} catch (IOException e) {
				exceptionLog(LOG_ERROR, getInstance(), e);
			}
		}
		if (DEBUG_MODE){
			System.out.println("------------------------------------------");
		}
	}
	
	private synchronized static String createDirectoryIfNessecary(File directory){
		if (directory.exists())
			return directory.getAbsolutePath();
		else {
			directory.mkdir();
			return directory.getAbsolutePath();
		}
	}
	
	public synchronized static String getDataTypesDirectory() {
		File dataTypeDir = new File(getClassDirectory(), "datatypes");
		return createDirectoryIfNessecary(dataTypeDir);
	}
	
	// return the directory to store received classes
	public synchronized static String  getClassDirectory(){
		File classpath = new File(FrameworkHandler.getInstance().getDirectory().getAbsolutePath(), "classes");
		return createDirectoryIfNessecary(classpath);
	}
	
	// return the directory to store received rule files
	public synchronized static String  getRuleDirectory(){
		File ruleDirectory = new File(FrameworkHandler.getInstance().getDirectory().getAbsolutePath(), "rules");
		return createDirectoryIfNessecary(ruleDirectory);
	}

	/**
	 * Returns the entire byte array for a given File
	 * @param file
	 * @return byte[] array of the entire file
	 * @throws IOException
	 */
    public static byte[] getBytesFromFile(File file) throws IOException {        
        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }
	    
}
