package edu.kit.informatik.graphProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.kit.informatik.Terminal;
import edu.kit.informatik.userInterface.IllegalInputException;

/**
 * This class executes the Dijkstra Algorithm which is used to find the shortest
 * or fastest route between two towns in a graph depending on the chosen
 * criterion in calculate method.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 */
public class DijkstraAlgorithm {
    /**
     * graph in which is being searched
     */
    private Graph graph;
    /**
     * start town of route to be found
     */
    private Town start;
    /**
     * destination town of route to be found
     */
    private Town destination;
    /**
     * a collection of all towns that have not been "visited" yet
     */
    private HashMap<Town, Town> unvisitedTowns;
    /**
     * the found shortest/fastest route
     */
    private ArrayList<Town> route;
    /**
     * weight of the found route Criterion time: weight in minutes Criterion
     * rout: weight in kilometers
     */
    private Integer weight;
    /**
     * detects whether a route has already been found
     */
    private boolean calculated;
    /**
     * to avoid endless loop in case graph is not connected
     */
    private int counter;

    /**
     * This method creates a new Dijkstra Algorithm object.
     * 
     * @param pGraph
     *            the graph that is being searched in
     * @param pStart
     *            start town of the route to be found
     * @param pDestination
     *            destination town of the route to be found
     * @throws ObjectNotFoundException
     *             if start town (pStart) or destination town (pDestination) do
     *             not exist in given graph or are null
     */
    public DijkstraAlgorithm(Graph pGraph, Town pStart, Town pDestination) throws ObjectNotFoundException {
        graph = pGraph;
        // check: valid input towns?
        if (graph.getTowns().contains(pStart) && graph.getTowns().contains(pDestination) && pStart != null
                && pDestination != null) {
            start = pStart;
            destination = pDestination;
            unvisitedTowns = new HashMap<Town, Town>();
            // putting all towns to "unvisited"
            for (int i = 0; i < graph.getTowns().size(); i++) {
                unvisitedTowns.put(graph.getTowns().get(i), graph.getTowns().get(i));
            }
            route = new ArrayList<Town>();
            weight = new Integer(0);
            calculated = false;
            counter = 0;
        } else {
            // towns are not valid
            throw new ObjectNotFoundException("Error, this graph does not contain one or both of the towns.");
        }
    }

    /**
     * This method sets the starting values for executing the algorithm.
     */
    private void initialization() {
        for (Town town : unvisitedTowns.values()) {
            town.setPredecessor(null);
            town.setMark(Integer.MAX_VALUE);
        }
        start.setMark(0);
    }

    /**
     * This method executes the actual algorithm and saves the found route. In
     * case of the criterion being time, it finds the fastest route, if the<br>
     * criterion is route it finds the shortest route.
     * 
     * @param pCriterion
     *            criterion that is going to be applied in the calculation
     * @throws IllegalInputException
     *             if the criterion is invalid
     */
    public void calculate(Criterion pCriterion) throws IllegalInputException {
        // setting start values
        initialization();
        Town currentTown;
        /*
         * repeat the following while at least one town has not been looked
         * at/'visited' yet
         */
        while (unvisitedTowns.size() > 1) {
            if (unvisitedTowns.size() > 1
                    || (unvisitedTowns.size() == 1 && !unvisitedTowns.containsValue(destination))) {
                try {
                    // find town with minimal distance (mark)
                    currentTown = getMinDistanceTown();
                    // current town now has been visited --> remove it
                    unvisitedTowns.remove(currentTown);
                    // find neighbor of the current town with min distance
                    findBestNeighbour(currentTown, pCriterion);
                } catch (IncorrectInputFileException e) {
                    break; /*
                            * to avoid infinite loop in case of not connected
                            * graph
                            */
                } catch (IllegalInputException e) {
                    // invalid criterion
                    throw new IllegalInputException(e.getMessage());
                }
            }
        }
        /*
         * now create route by tracing back the predecessors, starting with
         * destination
         */
        route.add(destination);
        while (route.get(route.size() - 1).getPredecessor() != null) {
            route.add(route.get(route.size() - 1).getPredecessor());
        }
        // route is reversed
        Collections.reverse(route);
        weight = destination.getMark();
        calculated = true;
    }

    /**
     * This method finds the town with the minimal mark in the collection of
     * unvisited towns.
     * 
     * @return the town with minimal mark in the collection of unvisited towns
     * @throws IncorrectInputFileException
     *             if the town with minimal mark cannot be found, this means the
     *             graph is not connected. To avoid an endless loop, the
     *             exception is thrown.
     */
    private Town getMinDistanceTown() throws IncorrectInputFileException {
        counter++;
        if (counter > graph.getTowns().size()) {
            throw new IncorrectInputFileException("Error, graph is not connected, infinite loop could appear.");
        } else {
            Town minDistance = new Town(
                    "minDistance"); /*
                                     * create default town to avoid null pointer
                                     * exception
                                     */
            minDistance.setMark(Integer.MAX_VALUE);
            for (Town town : unvisitedTowns.values()) {
                if (town.getMark() < minDistance.getMark()) {
                    // new town with minimal mark found
                    minDistance = town;
                }
            }
            return minDistance;
        }
    }

    /**
     * This method finds the neighbor of a town that has the shortest/fastest
     * connection to the town. The current town is set as the predecessor of the
     * found neighbor.
     * 
     * @param pCurrentTown
     *            town of which the neighbor is to be found
     * @param pCriterion
     *            criterion that is going to be applied; either time or route
     * @throws IllegalInputException
     *             in case of invalid criterion
     */
    private void findBestNeighbour(Town pCurrentTown, Criterion pCriterion) throws IllegalInputException {
        // searching in collection of neighbors of this town
        for (Town town : pCurrentTown.getNeighbors().values()) {
            // only look at town if it has not been visited
            if (unvisitedTowns.containsKey(town)) {
                try {
                    if ((pCurrentTown.getMark() + graph.findPath(pCurrentTown, town).getWeight(pCriterion)) < town
                            .getMark()) {
                        // found a neighbor with a path with mark smaller than
                        // current mark of this town
                        town.setMark(pCurrentTown.getMark() + graph.findPath(town, pCurrentTown).getWeight(pCriterion));
                        /*
                         * update weight and predecessor of this town (since
                         * better route has been found)
                         */
                        town.setPredecessor(pCurrentTown);
                    }
                } catch (IllegalInputException e) {
                    // invalid criterion
                    throw new IllegalInputException(e.getMessage());
                }
            }
        }
    }

    /**
     * This method prints the found route in one line, name of each town is
     * separated by a whitespace.
     * 
     * @throws IllegalInputException
     *             if calculate method has not been invoked before
     */
    public void printRoute() throws IllegalInputException {
        if (!calculated) {
            throw new IllegalInputException(
                    "Error, the route has not been calculated yet. Invoke method calculate(Criterion) first.");
        }
        String output = "";
        for (int i = 0; i < route.size(); i++) {
            output = output + route.get(i).getName();
            if (i < route.size() - 1) {
                // summing up all the towns' names
                output = output + " ";
            }
        }
        Terminal.printLine(output);
    }

    /**
     * This method returns the weight of the found route.
     * 
     * @return weight of this calculated route
     */
    public Integer getWeight() {
        if (route.size() == 1) {
            /*
             * route only contains destination --> route could not be found -->
             * weight is set to zero
             */
            weight = 0;
        }
        return weight;
    }

    /**
     * This method returns the found route.
     * 
     * @return the calculated route
     */
    public ArrayList<Town> getRoute() {
        return route;
    }

}
