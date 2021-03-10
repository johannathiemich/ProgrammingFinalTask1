package edu.kit.informatik.graphProcessing;

import edu.kit.informatik.userInterface.IllegalInputException;

/**
 * This class represents a direct connection between two towns.
 * In a graph, this represents an edge in an undirected graph.<br>
 * The paths bear marks with distance and time (positive integer numbers).
 * 
 * @author Johanna Thiemich
 * @version 1.0
 */
public class Path {

    /* important: since this belongs to an undirected graph, start and destination
    can be exchanged*/
    /**
     * town where the path starts or ends
     */
    private Town start;
    /**
     * the other town where the path starts or ends
     */
    private Town destination;
    /**
     * length of the path in kilometers
     */
    private int length; // in kilometers
    /**
     * time it takes to "walk" the path
     */
    private int time; // in minutes

    /**
     * This creates a new path object. Since this path belongs to an undirected
     * graph,<br>
     * start and destination town can be exchanged.
     * 
     * @param pStart
     *            one side of the connection, start of this path
     * @param pDestination
     *            the other side of the connection, end of this path
     * @param pLength
     *            the length of this path (in kilometers)
     * @param pTime
     *            the time it needs to walk this path (in minutes)
     */
    public Path(Town pStart, Town pDestination, int pLength, int pTime) {
        start = pStart;
        destination = pDestination;
        length = pLength;
        time = pTime;
    }

    /**
     * This method returns the weight of a path.
     * If criterion is time: returns time it takes to walk the path<br>
     * If criterion is route: returns length of this path
     * 
     * @param pCriterion
     *            the criterion according to which the weight is needed
     * @return the weight of a path (time or length)
     * @throws IllegalInputException
     *             if criterion is illegal (e.g. all or optimal) or if criterion
     *             is null.
     */
    public int getWeight(Criterion pCriterion) throws IllegalInputException {
        // check criterion
        if (pCriterion.equals(Criterion.ROUTE)) {
            return this.getLength();
        } else if (pCriterion.equals(Criterion.TIME)) {
            return this.getTime();
        } else { // invalid criterion
            throw new IllegalInputException("Error, invalid criterion. Please choose 'route' or 'time'.");
        }
    }

    /**
     * This method returns the town where the path starts / ends.
     * @return start / destination town
     */
    public Town getStart() {
        return start;
    }

    /**
     * This method returns the town where the path starts / ends.
     * @return start / destination town 
     */
    public Town getDestination() {
        return destination;
    }

    /**
     * This method returns the length of the path.
     * @return length of this path
     */
    public int getLength() {
        return length;
    }

    /**
     * This method returns the time it takes to walk a path.
     * @return time of this path
     */
    public int getTime() {
        return time;
    }

}
