package edu.kit.informatik.main;

import edu.kit.informatik.Terminal;
import edu.kit.informatik.graphProcessing.Graph;
import edu.kit.informatik.graphProcessing.GraphBuilder;
import edu.kit.informatik.graphProcessing.IncorrectInputFileException;
import edu.kit.informatik.userInterface.UserInteraction;

/**
 * This class contains the main method, invoking the main methods for running
 * the program.
 * 
 * @author Johanna Thiemich
 * @version 1.0
 */
public final class Main {

    /**
     * This class has a private constructor to avoid instantiation
     */
    private Main() {
        // intentionally left blank 
    }

    /**
     * The main method runs the program.
     * 
     * @param args
     *            contains the path to the file that contains the serialized
     *            graph
     */
    public static void main(String[] args) {
        // create a new graph out of the serialized text version
        if (args.length != 0) {
            GraphBuilder graphBuilder = new GraphBuilder(args[0]);
            try {
                Graph graph = graphBuilder.finishedGraph();
                UserInteraction act = new UserInteraction(graph);
                // starts interactive sequence
                act.interactiveDialog();
            } catch (IncorrectInputFileException e) {
                // in case creating the graph fails (incorrect input file)
                Terminal.printLine(e.getMessage());
            }

        } else {
            Terminal.printLine("Error, a path to a *.txt file has to be provided. Path has not been found.");
        }
    }
}
