package edu.kit.informatik.graphProcessing;

import java.util.ArrayList;
import java.util.HashMap;

import edu.kit.informatik.Terminal;
import edu.kit.informatik.userInterface.IllegalInputException;

/**
 * This class executes the depth first search algorithm which is used to find
 * all routes between two certain towns in a graph.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 *
 */
public class DepthFirstSearch {

    /**
     * graph in which is being searched
     */
    private Graph graph;
    /**
     * starting town
     */
    private Town start;
    /**
     * destination town
     */
    private Town destination;
    /**
     * memorizes which towns have already been visited (and do not have to be
     * visited again)
     */
    private HashMap<Town, Boolean> visited;
    /**
     * memorizes which neighbors of a town have already been visited (for each
     * town in the graph)
     */
    private HashMap<Town, Town> unvisitedNeighbors;
    /**
     * for each town in the graph: remembers the belonging neighbors
     */
    private HashMap<Town, HashMap<Town, Town>> adjList;
    /**
     * safes all the found routes between start and destination
     */
    private ArrayList<ArrayList<Town>> routes;
    /**
     * safes optimal route (means: (neededTime² + length² is minimal)
     */
    private ArrayList<Town> optimalRoute;
    /**
     * remembers whether the calculate method has already been invoked
     */
    private boolean calculated;
    /**
     * remembers whether the findOptimalRoute method has already been invoked
     */
    private boolean foundOptimalRoute;
    /**
     * the weight of the optimal route (in km² + min²)
     */
    private Integer weightOptimalRoute;

    /**
     * This creates a new DepthFirstSearch, which finds all routes between two
     * towns in a graph and the optimal one.<br>
     * Optimal means: length² + time² is minimal.
     * 
     * @param pGraph
     *            the graph in which is being searched for the routes
     * @param pStart
     *            starting town; start of every found route
     * @param pDestination
     *            destination town; end of every found route
     * @throws ObjectNotFoundException
     *             if starting and/or destination town do not exist in the given
     *             graph or if one or both of the given towns is null
     */
    public DepthFirstSearch(Graph pGraph, Town pStart, Town pDestination) throws ObjectNotFoundException {
        routes = new ArrayList<ArrayList<Town>>();
        graph = pGraph;
        // check if given graph contains start and destination
        if (graph.getTowns().contains(pStart) && graph.getTowns().contains(pDestination) && pStart != null
                && pDestination != null) {
            start = pStart;
            destination = pDestination;
            visited = new HashMap<Town, Boolean>();
            adjList = new HashMap<Town, HashMap<Town, Town>>();
            unvisitedNeighbors = new HashMap<Town, Town>();
            optimalRoute = new ArrayList<Town>();
            // calculate and findOptimalRoute both have not been invoked yet
            calculated = false;
            foundOptimalRoute = false;
            initialization();
        } else { // towns not found in graph or towns are null
            throw new ObjectNotFoundException("Error, this graph does not contain one or both of the towns.");
        }
    }

    /**
     * This method runs the algorithm to find all possible routes. After
     * invoking this method, the routes are available.
     */
    public void calculate() {
        calculated = true;
        // invoking recursive method
        recursion(adjList, visited, start, new ArrayList<Town>());
        // adding the destination town to every found route
        for (ArrayList<Town> oneRoute : routes) {
            oneRoute.add(destination);
        }
    }

    /**
     * This is a recursive method to find all possible routes.
     * 
     * @param adjLists
     *            for each town in the graph: remembers the belonging neighbors,
     *            acts like an adjacency list
     * @param visited
     *            a HashMap memorizing which towns have already been visited,
     *            this is needed so that the algorithm stops when all towns have
     *            been looked at.
     * @param pCurrentTown
     *            the current town that is being looked at
     * @param route
     *            saves the predecessors of the current town, so that the route
     *            can be recreated in case it turns out that this is a possible
     *            <br>
     *            route from start to destination
     */
    private void recursion(HashMap<Town, HashMap<Town, Town>> adjLists, HashMap<Town, Boolean> visited,
            Town pCurrentTown, ArrayList<Town> route) {
        // current town has will have been looked at --> visited true
        visited.put(pCurrentTown, true);
        // save current town to the current route
        route.add(pCurrentTown);

        /*
         * look at all the neighbors (and their neighbors and their neighbors
         * and so on) until all towns have been visited
         */
        for (Town neighbor : adjLists.get(pCurrentTown).values()) {
            // only look at town if it has not been visited
            if (visited.get(neighbor) == false) {
                /*
                 * one route from start to destination has been found --> add to
                 * all routes
                 */
                if (neighbor.equals(destination)) {
                    routes.add(route);
                } else {
                    // still towns left to look at --> invoke method again
                    recursion(adjLists, new HashMap<Town, Boolean>(visited), neighbor, new ArrayList<Town>(route));
                }
            }
        }
    }

    /**
     * initializes the depth first search object for calculation
     */
    private void initialization() {

        for (Town town : graph.getTowns()) {
            /*
             * all towns: not visited, add all neighbors of every town to
             * HashMap
             */
            visited.put(town, false);
            adjList.put(town, town.getNeighbors());
        }
        for (Town town : start.getNeighbors().values()) {
            // put the neighbors of the start to the HashMap
            unvisitedNeighbors.put(town, town);
        }

    }

    /**
     * This method chooses the optimal route out of all found routes. Optimal
     * means: (the length of the route)² + (time needed to drive route)² is
     * minimal
     */
    public void findOptimalRoute() {
        foundOptimalRoute = true;
        // start value for weight of optimal route is negative
        int tmpWeightOptimalRoute = -1;
        int currentWeightTimeSquare = 0;
        int currentWeightLengthSquare = 0;
        // make sure all possible routes have already been found
        if (!calculated) {
            calculate();
        }
        // compare the weights of the routes
        for (ArrayList<Town> oneRoute : routes) {
            currentWeightTimeSquare = oneRoutesWeight(oneRoute, Criterion.TIME)
                    * oneRoutesWeight(oneRoute, Criterion.TIME);
            currentWeightLengthSquare = oneRoutesWeight(oneRoute, Criterion.ROUTE)
                    * oneRoutesWeight(oneRoute, Criterion.ROUTE);
            if (currentWeightTimeSquare + currentWeightLengthSquare < tmpWeightOptimalRoute
                    || tmpWeightOptimalRoute < 0) {
                /*
                 * found a route with a smaller weight --> update optimal route
                 * and weight
                 */
                optimalRoute = oneRoute;
                tmpWeightOptimalRoute = currentWeightTimeSquare + currentWeightLengthSquare;
            }

        }
        weightOptimalRoute = tmpWeightOptimalRoute;

    }

    /**
     * This method calculates the weight of one route by a specific criterion
     * (time or route)
     * 
     * @param pRoute
     *            route of which the weight is being calculated
     * @param pCriterion
     *            criterion which is going to be applied (time or route)
     * @return the weight of the complete route (according to a the criterion)
     */
    private int oneRoutesWeight(ArrayList<Town> pRoute, Criterion pCriterion) {
        int weight = 0;
        for (int i = 1; i < pRoute.size(); i++) {
            try {
                weight = weight + graph.findPath(pRoute.get(i), pRoute.get(i - 1)).getWeight(pCriterion);
            } catch (IllegalInputException e) {
                /*
                 * this should not happen, the method is only invoked by
                 * Criterion.TIME and Criterion.ROUTE
                 */
                return -1;
            }
        }
        return weight;
    }

    /**
     * This method prints all routes that have been found. Each route is printed
     * in one line; the name of each town is separated by a whitespace.
     */
    public void printAllRoutes() {
        if (!calculated) {
            calculate();
        }
        String oneLine = "";
        for (ArrayList<Town> oneRoute : routes) {
            oneLine = "";
            for (int i = 0; i < oneRoute.size(); i++) {
                /*
                 * creating the output string by summing up all names of the
                 * towns
                 */
                oneLine = oneLine + oneRoute.get(i).getName();
                if (i < oneRoute.size() - 1) {
                    oneLine = oneLine + " ";
                }
            }
            // output string
            Terminal.printLine(oneLine);
        }
    }

    /**
     * This method prints the optimal route that has been found. The route is
     * printed in one line, the name of each town is separated by a whitespace.
     */
    public void printOptimalRoute() {
        // making sure the optimal route has already been found
        if (!foundOptimalRoute) {
            // check: all routes already found?
            if (!calculated) {
                // find all routes first...
                calculate();
            }
            // ...then find optimal one
            findOptimalRoute();
        }
        String output = "";
        for (int i = 0; i < optimalRoute.size(); i++) {
            // summing up output string
            output = output + optimalRoute.get(i).getName();
            if (i < optimalRoute.size() - 1) {
                output = output + " ";
            }
        }
        Terminal.printLine(output);
    }

    /**
     * This method returns all the found routes between start and destination.
     * 
     * @return an Array List of all found routes between start and destination
     */
    public ArrayList<ArrayList<Town>> getRoutes() {
        return routes;
    }

    /**
     * This method returns the weight of the optimal route.
     * 
     * @return the weight of the optimal route
     */
    public Integer getWeightOptimalRoute() {
        return weightOptimalRoute;
    }

}
