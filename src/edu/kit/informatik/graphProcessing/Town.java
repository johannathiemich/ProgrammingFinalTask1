package edu.kit.informatik.graphProcessing;

import java.util.HashMap;

/**
 * This class represents a town. In a graph, these towns are the vertices.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 *
 */
public class Town {

    /**
     * Name of this town; ignoring large and lower case
     */
    private String lowerCaseName;

    /**
     * Neighbors of this town (a neighbor is a town which is connected directly
     * to this town by one path)
     */
    private HashMap<Town, Town> neighbors;

    /**
     * Each town bears a mark (set to 0 by default, needed for Dijkstra
     * algorithm)
     */
    private Integer mark;
    /**
     * predecessor of this town in a certain route (set to null by default,
     * needed for Dijkstra algorithm)
     */
    private Town predecessor;

    /**
     * This creates a new town.
     * 
     * @param pName
     *            name of this town
     */
    public Town(String pName) {
        lowerCaseName = pName.toLowerCase();
        neighbors = new HashMap<Town, Town>();

        // predecessor, mark only needed for Dijkstra algorithm, set values to
        // default
        predecessor = null;
        mark = new Integer(0);
    }

    /**
     * This method adds a town to the list of neighbors of this town.
     * 
     * @param pNeighbor
     *            town to be added to the collection of neighbors
     */
    public void addNeighbor(Town pNeighbor) {
        neighbors.put(pNeighbor, pNeighbor);
    }

    /**
     * This method checks whether one town equals another one. Two towns are
     * equal if their name is equal.
     * 
     * @param object this town is going to be compared to
     * @return true if this town equals the object, false if not
     */
    @Override
    public boolean equals(Object pObject) {
        if (pObject == null || !(pObject instanceof Town)) {
            return false;
        }
        Town town = (Town) pObject;
        // towns are equal if their names are equal (ignoring large and lower
        // case)
        if (town.getName().equalsIgnoreCase(this.getName())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method returns the hashCode. The method had to be overwritten since
     * the equals method has been overwritten and towns are sometimes used in
     * HashMaps.
     */
    @Override
    public int hashCode() {
        return lowerCaseName.hashCode();
    }

    /**
     * This method returns the name of this town.
     * 
     * @return the name of this town
     */
    public String getName() {
        return lowerCaseName;
    }

    /**
     * This method returns the collection of neighbors of this town.
     * 
     * @return the collection of neighbors of this town
     */
    public HashMap<Town, Town> getNeighbors() {
        return neighbors;
    }

    /**
     * This method returns the mark of this town.
     * 
     * @return the mark of this town
     */
    public int getMark() {
        return mark;
    }

    /**
     * This method returns a town which is the predecessor of this town in a
     * certain route.
     * 
     * @return the predecessor of this town
     */
    public Town getPredecessor() {
        return predecessor;
    }

    /**
     * This method sets the mark of this town to a certain value.
     * 
     * @param pMark
     *            value the mark is to be set to
     * @throws IllegalArgumentException
     *             if pMark is below 0
     */
    public void setMark(int pMark) throws IllegalArgumentException {
        if (pMark < 0) {
            throw new IllegalArgumentException();
        } else {
            mark = pMark;
        }
    }

    /**
     * This method sets the predecessor of this town.
     * 
     * @param pPredecessor
     *            town that is going to be the new predecessor
     */
    public void setPredecessor(Town pPredecessor) {
        predecessor = pPredecessor;
    }

}
