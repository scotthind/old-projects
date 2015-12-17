package org.rowan.opawarenessvis.display;

/**
 * An Interface to be implemented by Components which are to be displayed
 * @author Shahid Akhter
 */
public interface Displayable {

    /**
     * Updates the DetailWindow with the information of the Component currently
     * selected
     */
    public void updateDetailWindow();

    /**
     * Retrieves the type of the displayable
     * @return A string representing the type of the component
     */
    public String getType();

    /**
     * Retrieves the ID of the displayable
     * @return A string representing the ID of the component
     */
    public String getID();
    
    
    /**
     * Retrieves the name of the displayable
     * @return A string representing the name of the displayable
     */
    public String getName();
}
