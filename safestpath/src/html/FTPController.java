/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package html;

import java.io.File;
import java.io.IOException;
import org.jibble.simpleftp.*;

/**
 * The FTPController class takes care of uploading files to an ftp server.
 * To upload to a server, the server url, login name, and password are needed.
 * The SimpleFTP class provided by the jibble library actually takes care of uploading
 * files to the server.
 */
public class FTPController {

    private String server, login, password;

    /**
     * Default constructor for the FTPController class
     * @param server The url of the server
     * @param login The login name for the server
     * @param password The password for the given login name
     */
    public FTPController(String server, String login, String password)
    {
        this.server = server;
        this.login = login;
        this.password = password;
    }

    /**
     * Upload the given file to an ftp server at the given directory
     * @param file The file to upload
     * @param directory The directory to add it to
     * @throws IOException If could not connect to the server
     */
    public void uploadToServer(File file, String directory) throws IOException
    {
        SimpleFTP ftp = new SimpleFTP();

        ftp.connect(server, 21, login, password);

        ftp.bin();

        ftp.cwd(directory);

        ftp.stor(file);

        ftp.disconnect();
    }
}
