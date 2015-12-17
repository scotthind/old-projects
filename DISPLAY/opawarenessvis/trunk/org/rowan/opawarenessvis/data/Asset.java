package org.rowan.opawarenessvis.data;

import org.rowan.opawarenessvis.display.DetailWindow;

/**
 * Asset is a subcomponent of an overall OpSystem or Entity
 * 
 * @author Shahid Akhter
 */
public class Asset extends Component {

    private double score;

    public Asset(OpSystem system, String type, String id, double score) {
        super(system, type, id);
        this.score = score;
    }

    /**
     * Returns the score of the Asset
     * @return A double representing the readiness score of the Asset.
     */
    public double getScore() {
        return score;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDetailWindow() {
        DetailWindow.getInstance().setFields(system.getName(), type, id, isReady(currentObjective));
    }
    
    @Override
    public String getName() {
        return system.getName();
    }
    
    @Override
    public String toString() {
        return "Asset (" + getID() + ")";
    }
}
