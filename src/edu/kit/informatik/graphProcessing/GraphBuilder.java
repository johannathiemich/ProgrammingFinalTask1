package edu.kit.informatik.graphProcessing;

import java.util.HashSet;

import edu.kit.informatik.Terminal;
import edu.kit.informatik.userInterface.IllegalInputException;

/**
 * This class creates an undirected, connected graph out of a serialized *.txt
 * file. The file should be in this form: <br>
 * First part: Name of each town in one line (each town represents a vertex),
 * then two hyphens in one line to mark end of first part. Second part: All the
 * paths (representing edges of the graph) are listed, each path in one separate
 * line. Expected syntax: nameOfTown1;nameOfTown2;lengthOfPath;timeNeededOnPath
 * Length and time have to be Integer numbers greater than zero.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 *
 */
public class GraphBuilder {
    /**
     * saving already added towns to avoid duplicates
     */
    private HashSet<String> addedTowns;
    /**
     * lines extracted out of the file
     */
    private String[] lines;
    /**
     * path to file that is being parsed
     */
    private String file;
    /**
     * graph that is being built
     */
    private Graph graph;

    /**
     * This creates a new graph out of a *.txt file.
     * 
     * @param pFile
     *            path of the *.txt file
     */
    public GraphBuilder(String pFile) {
        addedTowns = new HashSet<String>();
        file = pFile;
        graph = new Graph();
        lines = new String[0];
    }

    /**
     * This method returns the finished graph.
     * 
     * @return finished graph
     * @throws IncorrectInputFileException
     *             if finished graph is not connected
     */
    public Graph finishedGraph() throws IncorrectInputFileException {
        handleFile();
        if (graph.checkConnected()) {
            return graph;
        } else {
            throw new IncorrectInputFileException("Error, this graph is not connected.");
        }
    }

    /**
     * This method does the main tasks of creating the graph.
     */
    private void handleFile() {
        this.lines = FileInputHelper.read(this.file);
        int endOfFirstPart = -1; // first part contains the towns
        int startOfSecondPart = -1; // second part contains the paths
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals("--")) {
                // finding end of first part and start of second part
                endOfFirstPart = i - 1;
                startOfSecondPart = i + 1;
            }
        }
        // at least two towns have to be provided
        if (endOfFirstPart >= 1) {
            try {
                createTowns(endOfFirstPart);
            } catch (IncorrectInputFileException e) {
                Terminal.printLine(e.getMessage());
                System.exit(1);
            }
        } else {
            Terminal.printLine("Error, at least two towns (first part of file) have to be provided, "
                    + "but you provided less than two.");
            System.exit(1);
        }
        // at least one path has to be provided
        if ((lines.length - startOfSecondPart) >= 1) {
            try {
                createPaths(startOfSecondPart);
            } catch (IncorrectInputFileException e) {
                Terminal.printLine(e.getMessage());
                System.exit(1);
            } catch (NumberFormatException e) {
                Terminal.printLine(e.getMessage());
                System.exit(1);
            }
        } else {
            Terminal.printLine(
                    "Error, at least one path (second part of file) has to be provided, but you provided none.");
            System.exit(1);
        }
    }

    /**
     * This method creates the towns out of the first part of the file.
     * 
     * @param endOfFirstPart
     *            the amount of lines containing names of towns
     * @throws IncorrectInputFileException
     *             if first part of file contains mistakes (e.g. duplicate
     *             towns, name of town does not match [A-Za-z-]+)
     */
    private void createTowns(int endOfFirstPart) throws IncorrectInputFileException {
        addedTowns = new HashSet<String>();
        for (int i = 0; i <= endOfFirstPart; i++) {
            if (lines[i].matches("[A-Za-z-]+")) {
                // only add town if it does not already exist
                if (!addedTowns.contains(lines[i].toLowerCase())) {
                    graph.addTown(new Town(lines[i].toLowerCase()));
                    addedTowns.add(lines[i].toLowerCase());
                } else {
                    throw new IncorrectInputFileException("Error, duplicate towns are not allowed.");
                }
            } else {
                throw new IncorrectInputFileException(
                        "Error, name of town must only contain letters A-Z, a-z or a hyphen.");
            }
        }
    }

    /**
     * This method creates the paths out of the second part of the file.
     * 
     * @param startOfSecondPart
     *            the number of the line, marking the start of the second part
     *            of the file
     * @throws IncorrectInputFileException
     *             if second part of file contains mistakes (e.g. duplicate
     *             paths, towns do not exist, path from one town to itself)
     * @throws NumberFormatException
     *             if given Strings containing information about length and time
     *             of the path cannot be parsed to Integers
     */
    private void createPaths(int startOfSecondPart) throws IncorrectInputFileException, NumberFormatException {

        for (int i = startOfSecondPart; i < lines.length; i++) {
            if (lines[i].split(";").length == 4 && lines[i].charAt(lines[i].length() - 1) != ';') {
                String[] commands = lines[i].split(";");
                /*
                 * not using graph method findTown() to check whether town
                 * already exists since HashMap is working more efficiently
                 */
                if (addedTowns.contains(commands[0].toLowerCase()) && addedTowns.contains(commands[1].toLowerCase())) {
                    // path from one town to itself not allowed
                    if (!commands[0].equalsIgnoreCase(commands[1])) {
                        Integer distance = -1;
                        Integer time = -1;
                        try {
                            distance = Integer.parseInt(commands[2]);
                            time = Integer.parseInt(commands[3]);
                        } catch (NumberFormatException e) {
                            // parsing of strings not possible
                            throw new NumberFormatException(
                                    "Error, " + commands[2] + " and " + commands[3] + " are not valid integers.");
                        }
                        if (distance > 0 && time > 0) {
                            if (graph.findPath(graph.findTown(commands[0]), graph.findTown(commands[1])) == null) {
                                // all parameters have been entered
                                // correctly --> add path
                                try {
                                    graph.addPath(new Path(graph.findTown(commands[0]), graph.findTown(commands[1]),
                                            distance, time));
                                } catch (IllegalInputException e) {
                                    throw new IncorrectInputFileException(e.getMessage());
                                }
                            } else {
                                throw new IncorrectInputFileException("Error, duplicate paths are not allowed.");
                            }
                        } else
                            throw new IncorrectInputFileException(
                                    "Error, " + commands[2] + " and " + commands[3] + " are not valid integers.");
                    } else {
                        throw new IncorrectInputFileException("Error, path from one town to itself is not allowed.");
                    }
                } else {
                    throw new IncorrectInputFileException("Error, both towns have to be listed in first part of "
                            + "file in order to create a path between the two of them.");
                }
            } else {
                throw new IncorrectInputFileException("Error, in part two of the file you need to provide exactly"
                        + " 4 semicolon-separated parameters,\nbut the number of parameters did not match this.");
            }
        }
    }
}
