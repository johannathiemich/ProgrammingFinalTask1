package edu.kit.informatik.userInterface;

import edu.kit.informatik.Terminal;
import edu.kit.informatik.graphProcessing.Criterion;
import edu.kit.informatik.graphProcessing.DepthFirstSearch;
import edu.kit.informatik.graphProcessing.DijkstraAlgorithm;
import edu.kit.informatik.graphProcessing.Graph;
import edu.kit.informatik.graphProcessing.ObjectNotFoundException;
import edu.kit.informatik.graphProcessing.Path;
import edu.kit.informatik.graphProcessing.Town;

/**
 * This class handles the interaction with the user. Important: Large parts of
 * this class have been taken from the class CommandLine of the given solution
 * of assignment no. 06.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 *
 */
public class UserInteraction {

    /**
     * The graph that all the user's commands are being executed on.
     */
    private Graph graph;

    /**
     * This creates a new interaction with the user (commands are being executed
     * on the given graph).
     * 
     * @param pGraph
     *            the graph that the user's commands are being executed on
     */
    public UserInteraction(Graph pGraph) {
        graph = pGraph;
    }

    /**
     * This method creates a new interactive sequence with the user.
     */
    public void interactiveDialog() {
        String input = "";
        String[] commands = new String[0];

        /*the method expects new input until the command "quit" is being
        entered*/
        while (!input.equals("quit")) {
            // get the new command
            input = Terminal.readLine();
            // separate input at the first whitespace occurrence
            commands = input.split("\\s", 2);
            try {
                switch (commands[0]) {
                /* before each command is being executed, the number of given
                parameters is being checked*/
                case "quit":
                    checkParameterNumber(commands.length, 1);
                    quit();
                    break;
                case "info":
                    checkParameterNumber(commands.length, 1);
                    info();
                    break;
                case "vertices":
                    checkParameterNumber(commands.length, 1);
                    vertices();
                    break;
                case "search":
                    checkParameterNumber(commands.length, 2);
                    search(commands[1]);
                    break;
                case "route":
                    checkParameterNumber(commands.length, 2);
                    route(commands[1]);
                    break;
                case "remove":
                    checkParameterNumber(commands.length, 2);
                    remove(commands[1]);
                    break;
                case "insert":
                    checkParameterNumber(commands.length, 2);
                    insert(commands[1]);
                    break;
                case "nodes":
                    checkParameterNumber(commands.length, 2);
                    nodes(commands[1]);
                    break;
                default:
                    // check: empty input?
                    if (input.trim().length() == 0) {
                        throw new IllegalInputException("Error, please enter a command");
                    } else {
                        // unknown command
                        throw new IllegalInputException("Error, only the following commands are allowed: "
                                + "quit, info, vertices, search, route, remove, insert, nodes.");
                    }
                }
                // in case of illegal input
            } catch (IllegalInputException e) {
                Terminal.printLine(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
            }
        }
    }

    /**
     * This method exits the program.
     */
    private void quit() {
        System.exit(0);
    }

    /**
     * This method prints the serialized graph.
     */
    private void info() {
        graph.print();
    }

    /**
     * This method prints the names of all the towns. (Name of each town in a
     * separate line)
     */
    private void vertices() {
        graph.printTowns();
    }

    /**
     * This method searches for the best route between two cities according to a
     * specific criterion. The weight of the best route is being printed.
     * Criterion route, time or optimal allowed.
     * 
     * @param pCommand
     *            a String containing all the parameters (expected syntax:
     *            nameOfStartTown;nameOfDestinationTown;criterion)
     * @throws IllegalInputException
     *             in case of illegal input (e.g. town does not exist, invalid
     *             criterion, invalid syntax)
     */
    private void search(String pCommand) throws IllegalInputException {
        // separating the given parameters in the string
        String[] parameters = extractArguments(pCommand, 3);
        // check: valid criterion?
        if (getCriterion(parameters[2]) != null) {
            if (getCriterion(parameters[2]).equals(Criterion.ROUTE)
                    || getCriterion(parameters[2]).equals(Criterion.TIME)) {
                try {
                    /*
                     * Criterion route or time is being applied --> use Dijkstra
                     * algorithm to find fastest/shortest route
                     */
                    DijkstraAlgorithm critRouteOrTime = new DijkstraAlgorithm(graph, graph.findTown(parameters[0]),
                            graph.findTown(parameters[1]));
                    // calculate route
                    critRouteOrTime.calculate(getCriterion(parameters[2]));
                    // print weight of route
                    Terminal.printLine(critRouteOrTime.getWeight().toString());
                    return;
                } catch (ObjectNotFoundException e) {
                    // one or two towns could not be found
                    Terminal.printLine(e.getMessage());
                    return;
                } catch (IllegalInputException e) {
                    throw new IllegalInputException(e.getMessage());
                }
            } else if ((getCriterion(parameters[2]) != null) && getCriterion(parameters[2]).equals(Criterion.OPTIMAL)) {
                try {
                    /*criterion: optimal --> use Depth First Search Algorithm
                     to find optimal route among all routes*/
                    DepthFirstSearch critOptimal = new DepthFirstSearch(graph, graph.findTown(parameters[0]),
                            graph.findTown(parameters[1]));
                    // calculate optimal route
                    critOptimal.findOptimalRoute();
                    // print weight
                    Terminal.printLine(critOptimal.getWeightOptimalRoute().toString());
                    return;
                } catch (ObjectNotFoundException e) {
                    // town not found
                    Terminal.printLine(e.getMessage());
                    return;
                }
            } else {
                throw new IllegalInputException(
                        "Error, invalid criterion. Please choose 'time', 'route' or 'optimal'.");
            }
        } else {
            throw new IllegalInputException("Error, invalid criterion. Please choose 'time', 'route' or 'optimal'.");
        }
    }

    /**
     * This method searches for the best route between two cities according to a
     * specific criterion. The best route is being printed. All possible
     * criteria allowed. (In case of Criterion.ALL: all routes between two towns
     * are printed.)
     * 
     * @param pCommand
     *            a String containing all the parameters (expected syntax:
     *            nameOfStartTown;nameOfDestinationTown;criterion)
     * @throws IllegalInputException
     *             in case of illegal input (e.g. town does not exist, invalid
     *             criterion, invalid syntax)
     */
    private void route(String pCommand) throws IllegalInputException {
        // separating the given parameters in the string
        String[] parameters = extractArguments(pCommand, 3);

        // check: valid criterion?
        if (getCriterion(parameters[2]) != null) {
            if (getCriterion(parameters[2]).equals(Criterion.ROUTE)
                    || getCriterion(parameters[2]).equals(Criterion.TIME)) {
                try {
                    /*
                     * Criterion route or time is being applied --> use Dijkstra
                     * algorithm to find fastest/shortest route
                     */
                    DijkstraAlgorithm critTimeOrRoute = new DijkstraAlgorithm(graph, graph.findTown(parameters[0]),
                            graph.findTown(parameters[1]));
                    critTimeOrRoute.calculate(getCriterion(parameters[2]));
                    critTimeOrRoute.printRoute();
                    return;
                } catch (ObjectNotFoundException e) {
                    Terminal.printLine(e.getMessage());
                    return;
                }
            } else {
                try {
                    /*
                     * Criterion optimal or all is being applied --> use Depth
                     * First Search to find all routes between two towns.
                     */
                    DepthFirstSearch critOptimalOrAll = new DepthFirstSearch(graph, graph.findTown(parameters[0]),
                            graph.findTown(parameters[1]));
                    if (getCriterion(parameters[2]).equals(Criterion.ALL)) {
                        // criterion all --> all routes are printed
                        critOptimalOrAll.printAllRoutes();
                    } else if (getCriterion(parameters[2]).equals(Criterion.OPTIMAL)) {
                        // criterion optimal --> only optimal route is printed
                        critOptimalOrAll.printOptimalRoute();
                    } else {
                        throw new IllegalInputException(
                                "Error, invalid criterion. Please choose 'time', 'route', 'optimal' or 'all'");
                    }
                } catch (ObjectNotFoundException e) {
                    // town not found
                    Terminal.printLine(e.getMessage());
                    return;
                }
            }
        } else {
            throw new IllegalInputException(
                    "Error, invalid criterion. Please choose 'time', 'route', 'optimal' or 'all'");
        }
    }

    /**
     * This method removes a path from the graph and prints "OK" in case of
     * successful execution. If graph would not be related after removing the
     * path, the path is not being removed.
     * 
     * @param pCommand
     *            a String containing the parameters needed (expected syntax:
     *            nameOfStart;nameOfDestination)
     * @throws IllegalInputException
     *             if given town or path cannot be found in graph
     */
    private void remove(String pCommand) throws IllegalInputException {
        String[] parameters = extractArguments(pCommand, 2);

        // check: towns exist?
        if (townExists(parameters[0]) && townExists(parameters[1])) {
            try {
                /*
                 * removes path only if graph is still related afterwards if
                 * graph is not related afterwards, a IllegalInputException is
                 * being thrown
                 */
                graph.removePath(graph.findPath(graph.findTown(parameters[0]), graph.findTown(parameters[1])));
                Terminal.printLine("OK");
            } catch (ObjectNotFoundException e) {
                // path or town not found
                Terminal.printLine(e.getMessage());
                return;
            }
        } else {
            throw new IllegalInputException("Error, one of the towns does not exist.");
        }
    }

    /**
     * This method adds a path to the graph and prints "OK" in case of
     * successful execution. The command is only legal if at least one of the
     * given towns already exist.
     * 
     * @param pCommand
     *            String containing the needed parameters (expected syntax:
     *            nameOfStart;nameOfDestination;(int)lengthOfPath;(int)
     *            timeOfPath)
     * @throws IllegalInputException
     *             in case of illegal input (e.g. both towns do not exist,
     *             illegal length or time)
     */
    private void insert(String pCommand) throws IllegalInputException {
        // split String of parameters
        String[] parameters = extractArguments(pCommand, 4);

        // making sure both towns have valid names
        if (parameters[0].matches("[A-Za-z-]+") && parameters[1].matches("[A-Za-z-]+")) {
            // graph must not contain path from one town to itself
            if (!parameters[0].equalsIgnoreCase(parameters[1])) {
                if (townExists(parameters[0]) || townExists(parameters[1])) {
                    if (!townExists(parameters[0])) {
                        // start town does not exist --> add start town to graph
                        graph.addTown(new Town(parameters[0]));
                    } else if (!townExists(parameters[1])) {
                        // destination town does not exist --> add destination
                        // town to graph
                        graph.addTown(new Town(parameters[1]));
                    }

                    Integer distance;
                    Integer time;
                    try {
                        // calculating the weight of the path
                        distance = Integer.parseInt(parameters[2]);
                        time = Integer.parseInt(parameters[3]);
                        if (distance > 0 && time > 0) {
                            try {
                                graph.addPath(new Path(graph.findTown(parameters[0]), graph.findTown(parameters[1]),
                                        distance, time));
                                Terminal.printLine("OK");
                            } catch (IllegalInputException e) {
                                // if path already exists
                                throw new IllegalInputException(e.getMessage());
                            }
                        } else {
                            throw new IllegalInputException("Error, distance and time both must be greater than zero.");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalInputException("Error, " + parameters[2] + " and " + parameters[3]
                                + " have to be Integers greater than 0 and smaller than 2147483648.");
                    }

                } else {
                    throw new IllegalInputException(
                            "Error, at least one of the towns has to exist so that a path can be added.");
                }
            } else {
                throw new IllegalInputException("Error, start town must not equal destination town.");
            }
        } else {
            throw new IllegalInputException(
                    "Error, the name of both of the towns must only contain letters A-Z, a-z or a hyphen.");

        }

    }

    /**
     * This method prints the names of all neighbors of a certain town. Each
     * town is printed in a separate line.
     * 
     * @param pCommand
     *            String containing the town of which the neighbors are supposed
     *            to be printed
     * @throws IllegalInputException
     *             in case of illegal input (e.g. town does not exist, wrong
     *             number of parameters)
     */
    private void nodes(String pCommand) throws IllegalInputException {
        String[] parameters = extractArguments(pCommand, 1);
        try {
            graph.printNeighbors(graph.findTown(parameters[0]));
        } catch (ObjectNotFoundException e) {
            // town not found
            Terminal.printLine(e.getMessage());
        }
    }

    /**
     * This method detects the criterion by analyzing a String. This method
     * ignores large and lower case.
     * 
     * @param pInput
     *            String to detect the criterion of
     * @return criterion that equals the given String (Criterion.TIME in case of
     *         "time", Criterion.ROUTE in case of "route", and so on)<br>
     *         invalid String/criterion not found: null
     */
    private Criterion getCriterion(String pInput) {
        // criterion time
        if (pInput.equalsIgnoreCase("time")) {
            return Criterion.TIME;
            // criterion route
        } else if (pInput.equalsIgnoreCase("route")) {
            return Criterion.ROUTE;
            // criterion optimal
        } else if (pInput.equalsIgnoreCase("optimal")) {
            return Criterion.OPTIMAL;
            // criterion all
        } else if (pInput.equalsIgnoreCase("all")) {
            return Criterion.ALL;
        }
        // criterion not found
        return null;
    }

    /**
     * This method checks whether the given number of parameters equals the
     * required number
     * 
     * @param pGiven
     *            number of found parameters in given String
     * @param pRequired
     *            number of parameters required for invoking a specific method
     * @throws IllegalInputException
     *             in case the number of found given parameters does not match
     *             the number of expected parameters
     */
    private void checkParameterNumber(int pGiven, int pRequired) throws IllegalInputException {
        if (pGiven != pRequired && pRequired == 2) {
            throw new IllegalInputException(
                    "Error, this command requires exactly 1 parameter to work, but you provided no one.");
        } else if (pGiven != pRequired && pRequired == 1) {
            throw new IllegalInputException(
                    "Error, this command does not accept any additional parameter, but you provided some.");
        }
    }

    /**
     * This method returns true if a town with a specific name (pName) exists in
     * given graph.
     * 
     * @param pName
     *            the name of the town that is being searched for
     * @return true if graph contains a town with the given name (pName)<br>
     *         false if graph does not contain a town with the given name
     *         (pName)
     */
    private boolean townExists(String pName) {
        if (graph.findTown(pName) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method splits a given String by all semicolon occurrences and checks
     * whether the number of arguments meets the expected number.
     * @param pCommand
     *            String that is going to be split
     * @param pExpected
     *            number of parameters
     * @return found parameters, in an Array of String (each parameter is one
     *         element in the array)
     * @throws IllegalInputException
     *             if number of found parameters does not meet the expected
     *             number or if there are too many semicolon occurrences
     */
    private String[] extractArguments(String pCommand, int pExpected) throws IllegalInputException {
        // split by all semicolon occurrences
        String[] parameters = pCommand.trim().split(";");

        if (parameters.length != pExpected && pExpected >= 0 || !checkChar(pCommand, ';', pExpected - 1)) {
            throw new IllegalInputException("Error, this command requires exactly " + pExpected
                    + " semicolon-separated parameters" + " to work, but you provided " + parameters.length + ".");
        }
        return parameters;
    }

    /**
     * This method checks whether a character exists a certain number of times
     * in a given String.
     * 
     * @param pString
     *            String to be checked for character occurrences
     * @param pChar
     *            character to be searched for
     * @param pExpected
     *            expected number of character occurrences
     * @return true if number of character occurrences matches the expected
     *         number, <br>
     *         false if not
     */
    private boolean checkChar(String pString, char pChar, int pExpected) {
        int counter = 0;
        for (int i = 0; i < pString.length(); i++) {
            if (pString.charAt(i) == pChar) {
                // char found --> increase counter
                counter++;
            }
        }
        if (counter == pExpected) {
            return true;
        } else {
            return false;
        }
    }
}