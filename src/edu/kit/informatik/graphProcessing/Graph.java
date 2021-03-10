package edu.kit.informatik.graphProcessing;

import java.util.ArrayList;

import edu.kit.informatik.Terminal;
import edu.kit.informatik.userInterface.IllegalInputException;

/**
 * This class represents an undirected Graph. The towns represent the vertices
 * and the paths represent the edges.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 */
public class Graph {
    /**
     * Here, the towns represent the vertices
     */
    private ArrayList<Town> towns;
    /**
     * Here, the paths represent the edges
     */
    private ArrayList<Path> paths;

    /**
     * This creates a new graph object with empty lists of towns and paths.
     */
    public Graph() {
        towns = new ArrayList<Town>();
        paths = new ArrayList<Path>();
    }

    /**
     * This method adds a town to the list of towns.
     * 
     * @param pTown
     *            town that is going to be added
     */
    public void addTown(Town pTown) {
        towns.add(pTown);
    }

    /**
     * This method adds a path to the list of paths.
     * 
     * @param pPath
     *            path that is going to be added.
     * @throws IllegalInputException
     *             if path already exists in this graph
     */
    public void addPath(Path pPath) throws IllegalInputException {
        // only add path if it does not already exist
        if (findPath(pPath.getStart(), pPath.getDestination()) == null) {
            paths.add(pPath);
            // in case a path is added, for both the start and destination a new
            // neighbor has to be added
            for (int i = 0; i < towns.size(); i++) {
                // adding destination to the start's neighbors
                if (towns.get(i).equals(pPath.getStart())) {
                    towns.get(i).addNeighbor(pPath.getDestination());
                }
                // adding start to the destination's neighbors
                if (towns.get(i).equals(pPath.getDestination())) {
                    towns.get(i).addNeighbor(pPath.getStart());
                }
            }
        } else {
            throw new IllegalInputException("Error, a path between " + pPath.getStart().getName() + " and "
                    + pPath.getDestination().getName() + " already exists.");
        }
    }

    /**
     * This method removes a town from this graph's list of towns
     * 
     * @param pTown
     *            town that is going to be removed
     */
    private void removeTown(Town pTown) {
        towns.remove(pTown);
    }

    /**
     * This method removes a path from this graph, but only in case the graph is
     * still related afterwards. If the graph would not be related anymore
     * afterwards, the edge is not being removed.
     * 
     * @param pPath
     *            path that is supposed to be removed
     * @throws IllegalInputException
     *             in case the graph would not be related anymore after removing
     *             the path. in this case, the path is not removed
     * @throws ObjectNotFoundException
     *             if graph does not contain the path (pPath) or if path is null
     */
    public void removePath(Path pPath) throws IllegalInputException, ObjectNotFoundException {
        // check: valid parameter?
        if (pPath == null || !this.getPaths().contains(pPath)) {
            throw new ObjectNotFoundException("Error, this path does not exist.");
        } else {
            //
            boolean deleteStart = false;
            boolean deleteDestination = false;
            // removing start town from hashmap of neighbors of destination town
            findPath(pPath.getStart(), pPath.getDestination()).getStart().getNeighbors().remove(pPath.getDestination());
            // removing destination town from hashmap of neighbors of start town
            findPath(pPath.getStart(), pPath.getDestination()).getDestination().getNeighbors().remove(pPath.getStart());
            paths.remove(pPath);

            // save
            String nameOfDeletedTownStart = "";
            String nameOfDeletedTownDestination = "";
            if (pPath.getStart().getNeighbors().size() == 0) {
                // if a town does not have any neighbors after removing the
                // path, the town itself is being deleted
                deleteStart = true;
                // saving the name of the deleted town so it can be added to
                // graph again later if needed
                nameOfDeletedTownStart = pPath.getStart().getName();
                removeTown(pPath.getStart());
            }
            if (pPath.getDestination().getNeighbors().size() == 0) {
                // town has no neighbors after removing path; remove town
                deleteDestination = true;
                // saving name
                nameOfDeletedTownDestination = pPath.getDestination().getName();
                removeTown(pPath.getDestination());
            }
            // check whether graph is still related
            if (!checkConnected()) {
                /*
                 * graph is not related anymore after removing a path --> adding
                 * removed towns and path again
                 */
                if (deleteStart) {
                    this.addTown(new Town(nameOfDeletedTownStart));
                }
                if (deleteDestination) {
                    this.addTown(new Town(nameOfDeletedTownDestination));
                }
                this.addPath(pPath);
                throw new IllegalInputException(
                        "Error, graph would not be connected anymore. Edge between " + pPath.getStart().getName()
                                + " and " + pPath.getDestination().getName() + " has not been removed.");
            }
        }

    }

    /**
     * This method checks whether this graph is connected.
     * 
     * @return true if graph is related, false if graph is not connected
     */
    public boolean checkConnected() {
        // empty graph --> graph is connected
        if (towns.size() == 0) {
            return true;
        } else {

            Town start = towns.get(0);
            /*
             * idea: if you can find a route from one town to all the other
             * towns, the graph is connected so try to find the shortest path
             * from one town (first town in list) to all the other towns
             */
            for (Town town : towns) {
                // route from one town to itself is not allowed
                if (!town.equals(start)) {
                    try {
                        DijkstraAlgorithm calculator = new DijkstraAlgorithm(this, start, town);
                        calculator.calculate(Criterion.TIME);
                        /*
                         * found route has only a size of 1, so the route only
                         * contains the destination -->a route from start to
                         * destination has not been found, graph is not
                         * connected
                         */
                        if (calculator.getRoute().size() == 1) {
                            return false;
                        }
                    } catch (IllegalInputException e) {
                        // this should not happen since only criterion time is
                        // being applied
                        Terminal.printLine(e.getMessage());
                        return false;
                    } catch (ObjectNotFoundException e) {
                        /*
                         * this should not happen as well since all the towns do
                         * exist (iterating through list of existing towns)
                         */
                        Terminal.printLine(e.getMessage());
                        return false;
                    }
                }
            }
            // all routes had a size of at least 2 --> graph is connected
            return true;
        }
    }

    /**
     * This method finds a path in this graph between two certain towns.
     * 
     * @param pStart
     *            the town where the paths starts or ends
     * @param pDestination
     *            the other town where the path starts or ends
     * @return the path between pStart (town of start) and pDestination (town of
     *         destination)
     */
    public Path findPath(Town pStart, Town pDestination) {
        for (int i = 0; i < paths.size(); i++) {
            // undirected graph: destination and start might be switched, so
            // both possibilities have to be checked
            if (paths.get(i).getDestination().equals(pStart) && paths.get(i).getStart().equals(pDestination)
                    || paths.get(i).getDestination().equals(pDestination) && paths.get(i).getStart().equals(pStart)) {
                return paths.get(i);
            }
        }
        // path not found
        return null;
    }

    /**
     * This method finds a town in this graph by its name.
     * 
     * @param pName
     *            name of the town that is being searched for
     * @return the town with the given name (pName)
     */
    public Town findTown(String pName) {
        for (int i = 0; i < towns.size(); i++) {
            // ignore capital letters
            if (towns.get(i).getName().equalsIgnoreCase(pName)) {
                return towns.get(i);
            }
        }
        // town not found
        return null;
    }

    /**
     * This prints the graph in serialized form. This means: In the first part,
     * the towns are listed (one town per line).<br>
     * In the second part, all paths are listed (in following syntax:
     * nameOfStart;nameOfDestination;length;neededTime).<br>
     * Both parts are divided by '--'.
     */
    public void print() {
        // in case of an empty graph, only print an empty line
        if (towns.size() == 0) {
            Terminal.printLine("");
        } else {
            // vertices
            printTowns();
            // separating line
            Terminal.printLine("--");
            // edges
            for (Path path : paths) {
                Terminal.printLine(path.getStart().getName() + ";" + path.getDestination().getName() + ";"
                        + path.getLength() + ";" + path.getTime());
            }
        }
    }

    /**
     * This method prints all towns (name of each town in its own line).
     */
    public void printTowns() {
        if (towns.size() > 0)
            for (Town town : towns) {
                Terminal.printLine(town.getName());
            }
        else {
            Terminal.printLine("");
        }
    }

    /**
     * This method prints all the neighbors of a town (name of each neighbor in
     * its own line).
     * 
     * @param pTown
     *            town whose neighbors are being printed
     * @throws ObjectNotFoundException
     *             in case this graph does not contain pTown or if pTown is null
     */
    public void printNeighbors(Town pTown) throws ObjectNotFoundException {
        // check: valid parameter?
        if (towns.contains(pTown) && pTown != null) {
            for (Town town : pTown.getNeighbors().values()) {
                Terminal.printLine(town.getName());
            }
        } else {
            // town not found or town is null
            throw new ObjectNotFoundException("Error, this town does not exist.");
        }
    }

    /**
     * This method returns the list of towns of this graph.
     * 
     * @return the vertices (= list of towns) of this graph
     */
    public ArrayList<Town> getTowns() {
        return towns;
    }

    /**
     * This method returns the list of paths of this graph.
     * 
     * @return the edges (= list of paths) of this graph
     */
    public ArrayList<Path> getPaths() {
        return paths;
    }

}
