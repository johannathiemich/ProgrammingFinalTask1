package edu.kit.informatik.graphProcessing;

/**
 * This class represents the different criteria the user can choose from 
 * when using this navigation system
 * @author Johanna Thiemich
 * @version 1.0
 */
public enum Criterion {

    /**
     * Criterion TIME: when searching for a route, find the one that needs the least amount of time
     */
    TIME, 
    
    /**
     * Criterion ROUTE: when searching for a route, find the shortest one.
     */
    ROUTE, 
    
    /**
     * Criterion OPTIMAL: when searching for a route, find the optimal one.<br>
     * This means: length² + time² is minimal.
     */
    OPTIMAL, 
    
    /**
     * Criterion ALL: when searching for a route, find all routes between
     * start and end town.
     */
    ALL

}
