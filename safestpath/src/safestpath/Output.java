/*
 * Shahid Akhter, Kevin Friesen,
 * Stacey Montresor, Matthew Mullan,
 * Jonathan Summerton
 * Data_Driven Decisions Aid Tool
 * MSE Project
 * Software Engineering I
 * Dr. Rusu Fall 2010
 */
package safestpath;

import html.FTPController;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import safestpath.mapElements.Route;
import java.util.*;
import safestpath.mapElements.Location;

/**
 * The Output class controls making a list of routes into kml. It holds all of
 * the styling information and kml structure necessary to output a kml file that
 * will be readable in google maps.
 */
public class Output {

    //the routes created that will be displayed on a map
    private List<Route> routes;

    //the final strings are used for styling and creating the kml layout
    private static final String KML_FNAME = "route.kml";
    private static final String RED_COLOR = "\n<color>7f0000ff</color>";
    private static final String BLUE_COLOR = "\n<color>7fff0000</color>";
    private static final String GREEN_COLOR = "\n<color>7f00ff00</color>";
    private static final String YELLOW_COLOR = "\n<color>7f00ffff</color>";
    private final String KML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "\n<kml xmlns=\"http://www.opengis.net/kml/2.2\">"
            + "\n<Document>"
            + styling()
            + "\n<name>Primary</name>"
            + "\n<visibility>1</visibility>"
            + "\n<Folder id=\"kml_Route\">"
            + "\n<name>Route</name>";
    private static final String KML_HEADER_CLOSE = "\n</Folder>"
            + "\n</Document>"
            + "\n</kml>";
    private static final double LINE_WIDTH = 5;

    //used to upload to an ftp server
    private FTPController ftp;

    //we use this directory to upload to the Rowan elvis server, because
    //anything in this server shows up in the web server
    //change this to whatever directory the web server uses
    private static final String SERVER_DIRECTORY = "public_html";

    /**
     * Default constructor for the Output class
     * @param routes The list of routes to create the kml file for
     * @param ftp The object used to upload to an ftp server
     */
    public Output(List<Route> routes, FTPController ftp)
    {
        this.routes = routes;
        this.ftp = ftp;
    }

    /**
     * @return The string representation of the kml file created from
     * the list of routes
     */
    public String toKMLString()
    {
        String result = KML_HEADER;

        for (int i = 0; i < routes.size(); i++)
        {
            result += currentColor(i);
        }

        result += KML_HEADER_CLOSE;

        return result;
    }

    /**
     * Used to create the correct styling for the kml file. Right now if
     * the index is 0, you get a green styling, if 1 you get a yellow styling, else
     * you get a red styling
     * @param index The current route index
     * @return The styling info
     */
    private String currentColor(int index)
    {
        if (index == 0)
        {
            return routes.get(0).toKMLString("greenLine");
        }
        else
        {
            if (index == 1)
            {
                return routes.get(1).toKMLString("yellowLine");
            }
            else
            {
                return routes.get(2).toKMLString("redLine");
            }
        }
    }

    /**
     * Writes the this route as a kml file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeToFile() throws FileNotFoundException, IOException
    {
        File kml = new File(KML_FNAME);

        BufferedWriter out = new BufferedWriter(new FileWriter(KML_FNAME));

        String kmlString = toKMLString();

        out.write(kmlString);

        out.close();

        ftp.uploadToServer(kml, SERVER_DIRECTORY);
    }

    /**
     * @return The styling declaration used for the kml file.
     */
    private static final String styling()
    {
        String style = "";

        //red color style
        style += "\n<Style id=\"redLine\">";

        style += "\n<LineStyle>";
        style += RED_COLOR;
        style += "\n<width>" + LINE_WIDTH + "</width>";
        style += "\n</LineStyle>";
        style += "\n<PolyStyle>";
        style += RED_COLOR;
        style += "\n</PolyStyle>";
        style += "\n</Style>";


        //blue color style
        style += "\n<Style id=\"yellowLine\">";

        style += "\n<LineStyle>";
        style += YELLOW_COLOR;
        style += "\n<width>" + LINE_WIDTH + "</width>";
        style += "\n</LineStyle>";
        style += "\n</Style>";

        //green color style
        style += "\n<Style id=\"greenLine\">";

        style += "\n<LineStyle>";
        style += GREEN_COLOR;
        style += "\n<width>" + LINE_WIDTH + "</width>";
        style += "\n</LineStyle>";
        style += "\n</Style>";

        return style;
    }
    
}
